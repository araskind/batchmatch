
////////////////////////////////////////////////////////////
// BatchMatchSummarySheetWriter.java
// Written by Jan Wigginton and Bill Duren, November 2020
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import edu.umich.mrc2.batchmatch.process.BatchMatchSummaryInfo;
import edu.umich.mrc2.batchmatch.utils.DateUtils;
import edu.umich.mrc2.batchmatch.utils.ListUtils;

public class BatchMatchSummarySheetWriter extends BinnerInfoSheetWriter implements Serializable {

	private static final long serialVersionUID = 6417491361048719026L;

	public BatchMatchSummarySheetWriter(Workbook workBook) {
		super(workBook, 2);
	}

	public void fillSummaryTab(BatchMatchSummaryInfo summaryInfo) {
		Sheet sheet = createEmptySheet("Summary", workBook);
		sheet.setColumnWidth(1, 170 * 256);
		int rowNum = 0;

		PoiUtils.createBlankCleanRow(rowNum++, sheet, totalWidth, styleBoring);
		PoiUtils.createBlankCleanRow(rowNum++, sheet, totalWidth, styleBoring);
		String runDate = DateUtils.dateStrFromCalendar("MM/dd/yy", Calendar.getInstance());
		String runTime = DateUtils.dateStrFromCalendar("HH:mm", Calendar.getInstance());

		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum++, firstCol, sheet, "This report was created using "
				+ summaryInfo.getTitleWithVersion() + " on " + runDate + " at " + runTime + ".", styleBoring);

		PoiUtils.createBlankCleanRow(rowNum++, sheet, totalWidth, styleBoring);

		createSectionHeader(rowNum++, sheet, "Input");
		createTableEntry(rowNum++, sheet, "");
		createTableEntry(rowNum++, sheet, "Merge File: " + summaryInfo.getMergeFilePath());
		createTableEntry(rowNum++, sheet, "Raw Data File List: " + summaryInfo.getRawDataFileListPath());

		List<Integer> orderKeysForBatchFiles = (List<Integer>) ListUtils
				.makeListFromCollection(summaryInfo.getBatchFileMap().keySet());
		Collections.sort(orderKeysForBatchFiles);

		for (int i = 0; i < orderKeysForBatchFiles.size(); i++)
			createTableEntry(rowNum++, sheet, "     " + orderKeysForBatchFiles.get(i) + "   "
					+ summaryInfo.getBatchFileMap().get(orderKeysForBatchFiles.get(i)));

		createTableEntry(rowNum++, sheet, "");
		createTableEntry(rowNum++, sheet, "");

		createSectionHeader(rowNum++, sheet, "Data Characteristics");
		createTableEntry(rowNum++, sheet, "");
		createTableEntry(rowNum++, sheet, ("Number of Batches: "
				+ (summaryInfo.getnBatches() == null ? "" : summaryInfo.getnBatches().toString())));
		createTableEntry(rowNum++, sheet, "");

		createTableEntry(rowNum++, sheet, ("Original Number of Features: "
				+ (summaryInfo.getNTotalRawFeatures() == null ? "" : summaryInfo.getNTotalRawFeatures().toString())));
		createTableEntry(rowNum++, sheet, "");

		String tableFormatStr = "%1$-10s%2$-15s";

		String tableEntry, entry = String.format(tableFormatStr, "Batch", "# Features");
		createTableEntry(rowNum++, sheet, entry);
		createTableEntry(rowNum++, sheet, "_____________________________________________");

		for (Integer i = 1; i < summaryInfo.getnRawFeaturesByBatch().size(); i++) {
			entry = String.format(tableFormatStr, i, summaryInfo.getnRawFeaturesByBatch().get(i));
			createTableEntry(rowNum++, sheet, entry);
		}

		createTableEntry(rowNum++, sheet, "");

		createTableEntry(rowNum++, sheet,
				("Number of Batch Features Meeting Filter Criteria: "
						+ (summaryInfo.getnTotalFilteredFeatures() == null ? ""
								: summaryInfo.getnTotalFilteredFeatures().toString())));

		entry = String.format(tableFormatStr, "Batch", "# Features");
		createTableEntry(rowNum++, sheet, entry);
		createTableEntry(rowNum++, sheet, "_____________________________________________");

		for (Integer i = 0; i < summaryInfo.getnFilteredFeaturesByBatch().size(); i++) {
			tableEntry = String.format(tableFormatStr, i + 1, summaryInfo.getnFilteredFeaturesByBatch().get(i));
			createTableEntry(rowNum++, sheet, tableEntry);
		}
		createTableEntry(rowNum++, sheet, "");

		createTableEntry(rowNum++, sheet, "");
		createTableEntry(rowNum++, sheet, ("Total Number of Samples: "
				+ (summaryInfo.getnSamples() == null ? "" : "" + summaryInfo.getSampleNames().size())));

		entry = String.format(tableFormatStr, "Batch", "# Samples");
		createTableEntry(rowNum++, sheet, entry);
		createTableEntry(rowNum++, sheet, "_____________________________________________");

		for (Integer i = 0; i < summaryInfo.getnBatchSamples().size(); i++) {
			entry = String.format(tableFormatStr, i + 1, summaryInfo.getnBatchSamples().get(i));
			createTableEntry(rowNum++, sheet, entry);
		}
		createTableEntry(rowNum++, sheet, "");
		createTableEntry(rowNum++, sheet, "");

		// createTableEntry(rowNum++, sheet, "Number of Controls: " +
		// summaryInfo.getnSamples() == null ? "" :
		// summaryInfo.getnSamples().toString());

		createSectionHeader(rowNum++, sheet, "Sample/Control Names");

		createTableEntry(rowNum++, sheet, "");

		for (int i = 0; i < summaryInfo.getSampleNames().size(); i++) {
			createTableEntry(rowNum++, sheet, summaryInfo.getSampleNames().get(i));
		}
		for (int i = 0; i < 10; i++)
			createTableEntry(rowNum++, sheet, "");

		createTableEntry(rowNum++, sheet, "");

		/*
		 * if (summaryInfo.getLibFileCompCol() == null) {
		 * PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		 * createTableEntry(rowNum++, sheet, "   Mass Column: " +
		 * summaryInfo.getExpFileMassCol()); createTableEntry(rowNum++, sheet,
		 * "   Retention Time Column: " + summaryInfo.getExpFileRTCol()); }
		 * 
		 * createTableEntry(rowNum++, sheet, "   First Sample: " +
		 * summaryInfo.getFirstSamp()); createTableEntry(rowNum++, sheet,
		 * "   Last Sample: " + summaryInfo.getLastSamp()); createTableEntry(rowNum++,
		 * sheet, "   Number of Samples Analyzed: " + summaryInfo.getNSamps());
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * if (summaryInfo.getLibFileCompCol() != null) { createTableEntry(rowNum++,
		 * sheet, "Library File: " + summaryInfo.getLibFilePath());
		 * createTableEntry(rowNum++, sheet, "   Compound Column: " +
		 * summaryInfo.getLibFileCompCol()); createTableEntry(rowNum++, sheet,
		 * "   Mass Column: " + summaryInfo.getLibFileMassCol());
		 * createTableEntry(rowNum++, sheet, "   Retention Time Column: " +
		 * summaryInfo.getLibFileRTCol()); PoiUtils.createBlankCleanRow(rowNum++, sheet,
		 * totalWidth, styleBoring); }
		 * 
		 * createSectionHeader(rowNum++, sheet, "Output"); createTableEntry(rowNum++,
		 * sheet, ""); PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth,
		 * styleBoring); PoiUtils.createRowEntry(rowNum++, firstCol, sheet,
		 * "Output Directory: " + summaryInfo.getOutputDirectory(), styleBoringEntry);
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * createSectionHeader(rowNum++, sheet, "Data Cleaning");
		 * createTableEntry(rowNum++, sheet, ""); String missingnessString =
		 * getMissingnessString(summaryInfo); createTableEntry(rowNum++, sheet,
		 * "Any intensity value recorded as " + missingnessString +
		 * " was marked as missing."); createTableEntry(rowNum++, sheet,
		 * (summaryInfo.getnOutlierPts() == null ? 0 : summaryInfo.getnOutlierPts()) +
		 * " measurements more than " + String.format("%3.1f",
		 * summaryInfo.getOutlierThreshold()) +
		 * " standard deviations from the feature mean were identified and marked as missing."
		 * ); createTableEntry(rowNum++, sheet, "Features missing measurements in > " +
		 * String.format("%3.1f", summaryInfo.getPctMissingCutoff()) +
		 * "% of samples were removed.");
		 * 
		 * boolean singular = (summaryInfo.getRemovedFeatureCount() == 1);
		 * createTableEntry(rowNum++, sheet, "Of the original " +
		 * summaryInfo.getTotalFeatureCount() + " features, " +
		 * summaryInfo.getRemovedFeatureCount() + (singular ? " was" : " were") +
		 * " removed, leaving " + summaryInfo.getAnalyzedFeatureCount() +
		 * " features for analysis.");
		 * 
		 * createTableEntry(rowNum++, sheet, ""); createTableEntry(rowNum++, sheet,
		 * "Missing values were imputed using the median across all samples.");
		 * 
		 * if (summaryInfo.getDoTransform()) createTableEntry(rowNum++, sheet,
		 * "Log transformation was then performed using ln(1+x).");
		 * 
		 * createTableEntry(rowNum++, sheet, ""); if (summaryInfo.getDoDeisotoping()) {
		 * createTableEntry(rowNum++, sheet, "Deisotoping was performed.");
		 * createTableEntry(rowNum++, sheet, "   Mass Tolerance for Isotopes: " +
		 * summaryInfo.getIsotopeMassTol()); createTableEntry(rowNum++, sheet,
		 * "   Correlation Cutoff for Isotopes: " + summaryInfo.getIsotopeCorrCutoff());
		 * createTableEntry(rowNum++, sheet, "   Retention Time Range for Isotopes: " +
		 * summaryInfo.getIsotopeRTRange()); createTableEntry(rowNum++, sheet, "   " +
		 * (summaryInfo.getIsotopeGroupCount() - 1) +
		 * " isotope groups were identified."); createTableEntry(rowNum++, sheet,
		 * "   These groups contained a total of " + summaryInfo.getIsotopesFoundCount()
		 * + " isotopes."); } else { createTableEntry(rowNum++, sheet,
		 * "Deisotoping was not performed."); }
		 * 
		 * createTableEntry(rowNum++, sheet,
		 * "   Mass difference distribution calculated " +
		 * (summaryInfo.getDoHistogramDeisotoping() ? " after" : " before") +
		 * " deisotoping."); createTableEntry(rowNum++, sheet,
		 * "   Mass difference distribution derived from a total of  " +
		 * (summaryInfo.getnHistogramPoints() == null ? "" :
		 * summaryInfo.getnHistogramPoints().toString()) + " points. Of these, " +
		 * (summaryInfo.getnAnnotatedHistogramPoints() == null ? "0" :
		 * summaryInfo.getnAnnotatedHistogramPoints().toString()) +
		 * " points correspond to a frequently occuring value. ");
		 * 
		 * 
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * createSectionHeader(rowNum++, sheet, "Feature Grouping");
		 * createTableEntry(rowNum++, sheet, ""); createTableEntry(rowNum++, sheet,
		 * "Gap Between Bins: " + summaryInfo.getGapBetweenBins());
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * createTableEntry(rowNum++, sheet, "Cluster Bins by Correlation:");
		 * createTableEntry(rowNum++, sheet, "    Correlation Type: " +
		 * summaryInfo.getCorrelationType()); createTableEntry(rowNum++, sheet,
		 * "    Clustering Rule: " + summaryInfo.getClusteringRule());
		 * createTableEntry(rowNum++, sheet, "    Clustering Method: " +
		 * summaryInfo.getClusteringMethod());
		 * 
		 * createTableEntry(rowNum++, sheet, ""); createTableEntry(rowNum++, sheet,
		 * "Subdivide Correlation Clusters:"); createTableEntry(rowNum++, sheet,
		 * "    Division Method : " + summaryInfo.getRtClusteringMethod()); if
		 * (!StringUtils.isEmptyOrNull(summaryInfo.getReclusteredClustersRule())) {
		 * createTableEntry(rowNum++, sheet, "    Sub-clustering Rule: " +
		 * summaryInfo.getReclusteredClustersRule()); createTableEntry(rowNum++, sheet,
		 * "    Gap Constraint: " + summaryInfo.getRtGapRule()); }
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * createTableEntry(rowNum++, sheet, (summaryInfo.getAnnotGroupCount() - 1) +
		 * " adduct/NL groups were identified."); createTableEntry(rowNum++, sheet,
		 * "These groups contained a total of " +
		 * summaryInfo.getNonMolecularIonAnnotCount() +
		 * " non-molecular ion annotations.");
		 * 
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * 
		 * createSectionHeader(rowNum++, sheet, "Annotation");
		 * createTableEntry(rowNum++, sheet, ""); createTableEntry(rowNum++, sheet,
		 * "Annotation Mass Tolerance: " + summaryInfo.getAnnotMassTol() + " Da");
		 * createTableEntry(rowNum++, sheet, "Annotation RT Tolerance: " +
		 * summaryInfo.getAnnotRtTol()); createTableEntry(rowNum++, sheet, "");
		 * createTableEntry(rowNum++, sheet, "Annotation File: " +
		 * summaryInfo.getAnnotFilePath()); createTableEntry(rowNum++, sheet,
		 * "   Annotation Column: " + summaryInfo.getAnnotFileAnnotCol());
		 * createTableEntry(rowNum++, sheet, "   Mass Column: " +
		 * summaryInfo.getAnnotFileMassCol()); createTableEntry(rowNum++, sheet,
		 * "   Mode Column: " + summaryInfo.getAnnotFileModeCol());
		 * createTableEntry(rowNum++, sheet, "   Charge Column: " +
		 * summaryInfo.getAnnotFileChargeCol()); createTableEntry(rowNum++, sheet,
		 * "   Tier Column: " + summaryInfo.getAnnotFileTierCol());
		 * createTableEntry(rowNum++, sheet, "Charge Carriers Excluded : " +
		 * summaryInfo.getSkippedChargeCarriers()); createTableEntry(rowNum++, sheet,
		 * ""); createTableEntry(rowNum++, sheet, "Ionization Mode: " +
		 * summaryInfo.getIonizationMode()); createTableEntry(rowNum++, sheet, "");
		 * createTableEntry(rowNum++, sheet, "Neutral masses " +
		 * (summaryInfo.getUseNMForChargeCarrierCall() ? "helped" : "did not help") +
		 * " determine best charge carrier."); createTableEntry(rowNum++, sheet,
		 * "Charge " + (summaryInfo.getChargeCanVaryWithoutIsotopeInfo() ? "was" :
		 * "was not") + " allowed to vary in the absence of isotope information.");
		 * createTableEntry(rowNum++, sheet, "Warnings: " +
		 * summaryInfo.getIsotopeAnnotationWarnings());
		 * 
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * createSectionHeader(rowNum++, sheet, "Charge Carrier Handling");
		 * createTableEntry(rowNum++, sheet, ""); String header =
		 * String.format("%1$-20s%2$-20s%3$-20s%4$-20s%5$-20s%6$-20s", "Carrier",
		 * "Tier", "Charge", "Base", "Multimer Base",
		 * "Combine Without Precedent                  ");
		 * PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		 * PoiUtils.createRowEntry(rowNum++, firstCol, sheet, header,
		 * styleBoringUnderlinedEntry);
		 * 
		 * if (summaryInfo.getChargeCarrierPrefs() != null) { for
		 * (ChargeCarrierPreferences value :
		 * summaryInfo.getChargeCarrierPrefs().values()) { if (value == null) continue;
		 * String tier = value.wasCustomized() ? "Custom" : value.getTier(); String
		 * entry = String.format("%1$-20s%2$-20s%3$-20s%4$-20s%5$-20s%6$-20s",
		 * value.getGroupName(), tier, value.getCharge(), value.getAllowAsBase() ? "  Y"
		 * : "  N", value.getAllowAsMultimerBase() ? "      Y" : "      N" ,
		 * value.getRequireAloneBeforeCombined() ? "               N" :
		 * "               Y");
		 * 
		 * PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		 * PoiUtils.createRowEntry(rowNum++, firstCol, sheet, entry,
		 * value.wasCustomized() ? styleBoringRedEntry : styleBoringEntry); } }
		 * createTableEntry(rowNum++, sheet, ""); //\n
		 * 
		 * 
		 * for (int i = 0; i < 3; i++) createTableEntry(rowNum++, sheet, "");
		 * 
		 * PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		 * PoiUtils.createRowEntry(rowNum++, firstCol, sheet, "", styleBoringBottom);
		 * 
		 * for (int i = 0; i < 5; i++) PoiUtils.createBlankCleanRow(rowNum++, sheet,
		 * totalWidth, styleBoring);
		 * 
		 * createSectionHeader(rowNum++, sheet, "Annotation File");
		 * createTableEntry(rowNum++, sheet, "");
		 * 
		 * header = String.format("%1$-50s%2$-25s%3$-25s%4$-25s%5$-25s", "Annotation",
		 * "Mass", "Mode", "Charge", "Tier");
		 * 
		 * PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		 * PoiUtils.createRowEntry(rowNum++, firstCol, sheet, header,
		 * styleBoringUnderlinedEntry);
		 * 
		 * List<AnnotationInfo> lst =
		 * ListUtils.makeListFromCollection(summaryInfo.getAnnotationMap().values());
		 * Collections.sort(lst, new AnnotationInfoByMassComparator()); for
		 * (AnnotationInfo info : lst) {
		 * 
		 * String chg = info.getCharge(); Boolean chargeIsNeg =
		 * "-".equals(chg.substring(0, 1));
		 * 
		 * String entry = String.format("%1$-50s%2$-25s%3$-25s%4$-25s%5$-25s",
		 * info.getAnnotation(), info.getMass() < 0 ? info.getMass() : " " +
		 * info.getMass(), info.getMode(), chargeIsNeg ? info.getCharge() : " " +
		 * info.getCharge(), info.getTier());
		 * 
		 * PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		 * PoiUtils.createRowEntry(rowNum++, firstCol, sheet, entry, styleBoringEntry);
		 * }
		 */
		createTableEntry(rowNum++, sheet, "");
		createTableEntry(rowNum++, sheet, "");
		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum++, firstCol, sheet, "", styleBoringBottom);

		for (int i = 0; i < 20; i++)
			PoiUtils.createBlankCleanRow(rowNum++, sheet, totalWidth, styleBoring);
	}
}

/*
 * private String getMissingnessString(SummaryInfo summaryInfo) { StringBuilder
 * sb = new StringBuilder(); List<String> missingChars =
 * Arrays.asList(BinnerConstants.MISSINGNESS_LIST);
 * 
 * for (int i = 1; i < missingChars.size() - 1; i++) if (i !=
 * missingChars.size() - 2) sb.append(missingChars.get(i) + ", "); else
 * sb.append(summaryInfo.getZeroMeansMissing() ? "" : "or " +
 * missingChars.get(i));
 * 
 * sb.append((summaryInfo.getZeroMeansMissing() ? "or 0" : ""));
 * 
 * return sb.toString(); } }
 */
