////////////////////////////////////////////////////
//BatchMatchNamedResultsWriter.java
//Written by Jan Wigginton March 2023
////////////////////////////////////////////////////

package edu.umich.batchmatch.io.sheetwriters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.umich.batchmatch.data.FeatureFromFile;
import edu.umich.batchmatch.data.FeatureMatch;
import edu.umich.batchmatch.data.MassRangeGroup;
import edu.umich.batchmatch.data.MatchedFeatureGroup;
import edu.umich.batchmatch.data.PostProcessDataSet;
import edu.umich.batchmatch.data.comparators.MatchedFeatureGroupByNameTagComparator;
import edu.umich.batchmatch.process.BatchMatchMappingFileInfo;
import edu.umich.batchmatch.process.BatchMatchNamedMassResults;
import edu.umich.batchmatch.process.BatchMatchSummaryInfo;
import edu.umich.batchmatch.utils.ListUtils;
import edu.umich.batchmatch.utils.StringUtils;

public class BatchMatchNamedResultsWriter extends BinnerSpreadSheetWriter {

	private String namedResultFileName;

	public BatchMatchNamedResultsWriter() {
		super();
	}

	public void reportOnTargetFeaturesToExcel(PostProcessDataSet data, Map<String, String> targetFeatures,
			int completeSetSize, BatchMatchMappingFileInfo mapInfo, BatchMatchSummaryInfo summaryInfo)
			throws Exception {

		XSSFWorkbook wb = new XSSFWorkbook(); // or new HSSFWorkbook();

		try {
			// if (summaryInfo != null) {
			// BatchMatchSummarySheetWriter summaryWriter = new
			// BatchMatchSummarySheetWriter(wb);
			// summaryWriter.fillSummaryTab(summaryInfo);
			// }

			BatchMatchNamedMassResults results2 = new BatchMatchNamedMassResults(data);
			XSSFSheet sheet2 = wb.createSheet("Compound Matches");
			writeCompoundReport(sheet2, results2, completeSetSize, wb);

			BatchMatchNamedMassResults results3 = new BatchMatchNamedMassResults(data);
			XSSFSheet sheet3 = wb.createSheet("Remaining Compounds");
			writeMissingCompoundReport(sheet3, results3, completeSetSize, wb, mapInfo);

			XSSFSheet sheet4 = wb.createSheet("Mapping");
			writeMapReport(sheet4, completeSetSize, wb, mapInfo);

			BatchMatchNamedMassResults results = new BatchMatchNamedMassResults(data);
			List<MassRangeGroup> interestingMassRanges = results.grabMassRangesToStudy(targetFeatures);
			XSSFSheet sheet1 = wb.createSheet("Named Mass Groups");
			writeMassRegionReport(sheet1, interestingMassRanges, completeSetSize, wb);

			FileOutputStream fileOut = new FileOutputStream(namedResultFileName);
			wb.write(fileOut);
			fileOut.close();
			System.out.println("Successfully created named mass group workbook");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void writeMapReport(XSSFSheet sheet, Integer completeSetSize, XSSFWorkbook wb,
			BatchMatchMappingFileInfo mapInfo) throws Exception {

		sheet.createFreezePane(1, 1);

		List<XSSFCellStyle> boringStyles = grabBoringStyles(wb);
		List<XSSFCellStyle> integerStyles = grabIntegerStyles(wb);
		List<XSSFCellStyle> numericStyles = grabNumericStyles(wb);

		XSSFCellStyle styleHeader = grabStyleBatchMatchHeader(wb);
		XSSFCellStyle styleWhiteSolid = grabStyleWhiteSolid(wb);

		int rowCt = 0;
		createMapReportHeader(rowCt++, sheet, styleHeader, styleWhiteSolid);
		int shadeIdx = 0;

		List<FeatureMatch> allMappings = mapInfo.getAllMappings(false);
		String lastCompound = allMappings.size() > 1 ? allMappings.get(0).getNameStub() : "";

		for (FeatureMatch f : allMappings) {

			int i = 0;
			if (!f.getNameStub().equals(lastCompound))
				shadeIdx++;

			shadeIdx %= 2;

			createAppropriateRowEntry(rowCt, i++, sheet, f.getNamedFeature(), integerStyles.get(shadeIdx),
					numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

			createAppropriateRowEntry(rowCt, i++, sheet, f.getUnnamedFeature(), integerStyles.get(shadeIdx),
					numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

			/*
			 * String corrString = f.getCorrStr();
			 * 
			 * if (f.getCorr().equals(BatchMatchConstants.NAMED_ALL_MISSING)) corrString =
			 * BatchMatchConstants.NAMED_ALL_MISSING_MSG;
			 * 
			 * else if (f.getCorr().equals(BatchMatchConstants.UNNAMED_ALL_MISSING))
			 * corrString = BatchMatchConstants.UNNAMED_ALL_MISSING_MSG;
			 */

			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getCorrStr(), numericStyles.get(shadeIdx));
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getNamedMassStr(), numericStyles.get(shadeIdx));
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getNamedRtStr(), numericStyles.get(shadeIdx));
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getDeltaCorrStr(1), numericStyles.get(shadeIdx));
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getDeltaCorrStr(2), numericStyles.get(shadeIdx));
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getDeltaCorrStr(3), numericStyles.get(shadeIdx));

			for (int j = 0; j < 10; j++) {
				createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid, styleWhiteSolid);
			}

			lastCompound = f.getNameStub();
			rowCt++;
		}

		for (int l = 0; l < 50; l++)
			PoiUtils.createBlankCleanRow(rowCt++, sheet, 40, styleWhiteSolid);

		for (int i = 0; i < 40; i++) {
			sheet.autoSizeColumn(i);
			if (sheet.getColumnWidth(i) < 3000) {
				sheet.setColumnWidth(i, Math.max(3000, sheet.getColumnWidth(i) * 2));
				continue;
			}
			if (sheet.getColumnWidth(i) < 20000) {
				Double curr = 1.2 * sheet.getColumnWidth(i);
				sheet.setColumnWidth(i, curr.intValue());
				continue;
			}

			try {
				Double curr = sheet.getColumnWidth(i) * 1.3;
				sheet.setColumnWidth(i, curr.intValue());
			} catch (Exception e) {
				sheet.setColumnWidth(i, 20000);
			}
		}
	}

	public void writeMissingCompoundReport(XSSFSheet sheet, BatchMatchNamedMassResults namedResults,
			Integer completeSetSize, XSSFWorkbook wb, BatchMatchMappingFileInfo mapInfo) throws Exception {

		Integer rowCt = 0;
		int shadeIdx = 0;

		sheet.createFreezePane(1, 1);

		List<XSSFCellStyle> boringStyles = grabBoringStyles(wb);
		List<XSSFCellStyle> integerStyles = grabIntegerStyles(wb);
		List<XSSFCellStyle> numericStyles = grabNumericStyles(wb);

		XSSFCellStyle styleHeader = grabStyleBatchMatchHeader(wb);
		XSSFCellStyle styleWhiteSolid = grabStyleWhiteSolid(wb);

		Map<String, List<FeatureMatch>> unmappedCompoundsByName = mapInfo.getUnmappedCompoundsMap();
		Map<String, Double> lowCorrelationMappings = mapInfo.getCompoundsWithoutSignificantCorrSet(.9);

		createMissingCompoundHeader(completeSetSize, rowCt++, sheet, styleHeader, styleWhiteSolid);

		Map<String, List<String>> mappingCommentsMap = new HashMap<String, List<String>>();

		for (String name : unmappedCompoundsByName.keySet()) {

			List<FeatureMatch> unmappedMatches = unmappedCompoundsByName.get(name);

			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			for (int ll = 0; ll < unmappedMatches.size(); ll++) {
				sb.append("B" + unmappedMatches.get(ll).getBatch());
				if (ll < unmappedMatches.size() - 1)
					sb.append(",");
			}

			// String existingComment = "";
			// if (mappingCommentsMap.containsKey(name))
			/// existingComment = mappingCommentsMap.get(name) + " ";

			if (!mappingCommentsMap.containsKey(name)) {
				mappingCommentsMap.put(name, new ArrayList<String>());
				mappingCommentsMap.get(name).add("");
				mappingCommentsMap.get(name).add("");
			}
			mappingCommentsMap.get(name).set(0, sb.toString());
		}

		for (String name : lowCorrelationMappings.keySet()) {

			Double maxAvgCorr = lowCorrelationMappings.get(name);
			String corrString = "NA";
			if (maxAvgCorr != null && !maxAvgCorr.isNaN())
				corrString = String.format("%.3f", maxAvgCorr);

			String corrComment = corrString;

			if (!mappingCommentsMap.containsKey(name)) {
				mappingCommentsMap.put(name, new ArrayList<String>());
				mappingCommentsMap.get(name).add("");
				mappingCommentsMap.get(name).add("");
			}
			mappingCommentsMap.get(name).set(1, corrComment);
		}

		Map<String, String> matchingCommentsMap = new HashMap<String, String>();
		Map<String, String> maxAltCorrs = new HashMap<String, String>();
		Map<String, Map<Integer, List<MatchedFeatureGroup>>> insignificantGroups = namedResults
				.grabCompoundsWithoutSignificantCompleteMatchGroup(completeSetSize, true);

		/*
		 * Map<String, Map<Integer, List<MatchedFeatureGroup>>>
		 * namedMatchGroupsLessThanSizeByCompoundAndSize =
		 * namedResults.grabNamedMatchGroupsLessThanSizeByCompoundAndSize(
		 * completeSetSize + 1, true);
		 */

		for (String name : insignificantGroups.keySet()) {
			System.out.println(name);

			StringBuilder sb = new StringBuilder();
			Double maxCorr = null;

			for (Integer sz : insignificantGroups.get(name).keySet()) {

				List<MatchedFeatureGroup> grpList = insignificantGroups.get(name).get(sz);
				System.out.print("" + grpList.size() + "groups of size " + sz + ":   ");

				for (MatchedFeatureGroup grp : grpList) {

					// deal with this later "true"
					Double corr = grp.getAvgCorrelationIfPossible(true);
					if (corr != null && !corr.isNaN())
						if (maxCorr == null || corr > maxCorr)
							maxCorr = corr;

					String corrString = String.format("%.3f", grp.getAvgCorrelationIfPossible(true));

					sb.append("(" + sz + ") MG" + grp.getMatchGrpKey() + " corr = " + corrString + "  ");
				}
				System.out.println(sb.toString());
			}
			matchingCommentsMap.put(name, sb.toString());

			String corrString = "";
			if (maxCorr != null && !maxCorr.isNaN())
				corrString = String.format("%.3f", maxCorr);

			maxAltCorrs.put(name, corrString);
		}

		rowCt++;
		for (String name : mappingCommentsMap.keySet()) {
			int i = 0;

			createAppropriateRowEntry(rowCt, i++, sheet, StringUtils.isEmptyOrNull(name) ? "-" : name,
					integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

			if (mappingCommentsMap.containsKey(name)) {
				createAppropriateRowEntry(rowCt, i++, sheet, mappingCommentsMap.get(name).get(0),
						integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

				createAppropriateRowEntry(rowCt, i++, sheet, mappingCommentsMap.get(name).get(1),
						integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));
			} else {
				createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIdx),
						numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

				createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIdx),
						numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));
			}

			if (matchingCommentsMap.containsKey(name)) {
				createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(matchingCommentsMap.get(name)),
						integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

				createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(maxAltCorrs.get(name)),
						integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));
			} else {
				createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIdx),
						numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

				createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIdx),
						numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));
			}

			for (int j = 0; j < 10; j++) {
				createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid, styleWhiteSolid);
			}

			shadeIdx++;
			shadeIdx %= 2;
			rowCt++;
		}

		for (String name : matchingCommentsMap.keySet()) {

			int i = 0;

			if (mappingCommentsMap.containsKey(name))
				continue;

			createAppropriateRowEntry(rowCt, i++, sheet, StringUtils.isEmptyOrNull(name) ? "-" : name,
					integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

			createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIdx), numericStyles.get(shadeIdx),
					boringStyles.get(shadeIdx));

			createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIdx), numericStyles.get(shadeIdx),
					boringStyles.get(shadeIdx));

			createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(matchingCommentsMap.get(name)),
					integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

			createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(maxAltCorrs.get(name)),
					integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

			for (int j = 0; j < 10; j++) {
				createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid, styleWhiteSolid);
			}

			shadeIdx++;
			shadeIdx %= 2;
			rowCt++;
		}

		for (int l = 0; l < 50; l++)
			PoiUtils.createBlankCleanRow(rowCt++, sheet, 40, styleWhiteSolid);

		// int longestMapComment = Integer.MIN_VALUE;
		// for (String comment : mappingCommentsMap.getvalues()) {
		// if (comment.length() > longestMapComment)
		// longestMapComment = comment.length();
		// }

		int longestMatchComment = Integer.MIN_VALUE;
		for (String comment : matchingCommentsMap.values()) {
			if (comment.length() > longestMatchComment)
				longestMatchComment = comment.length();
		}

		sheet.setColumnWidth(0, 50 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 40 * 256);
		sheet.setColumnWidth(3, longestMatchComment * 256);

		for (int i = 4; i < 10; i++) {
			sheet.autoSizeColumn(i);
			if (sheet.getColumnWidth(i) < 3000) {
				sheet.setColumnWidth(i, Math.max(3000, sheet.getColumnWidth(i) * 2));
				continue;
			}

			if (sheet.getColumnWidth(i) < 20000) {
				Double curr = 1.2 * sheet.getColumnWidth(i);
				sheet.setColumnWidth(i, curr.intValue());
				continue;
			}

			try {
				Double curr = sheet.getColumnWidth(i) * 1.3;
				sheet.setColumnWidth(i, curr.intValue());
			} catch (Exception e) {
				sheet.setColumnWidth(i, 20000);
			}
		}
	}

	public List<String> writeCompoundReport(XSSFSheet sheet, BatchMatchNamedMassResults namedResults,
			Integer completeSetSize, XSSFWorkbook wb) throws Exception {

		sheet.createFreezePane(2, 1);

		List<XSSFCellStyle> boringStyles = grabBoringStyles(wb);
		List<XSSFCellStyle> integerStyles = grabIntegerStyles(wb);
		List<XSSFCellStyle> numericStyles = grabNumericStyles(wb);

		XSSFCellStyle styleHeader = grabStyleBatchMatchHeader(wb);
		XSSFCellStyle styleWhiteSolid = grabStyleWhiteSolid(wb);

		Map<Integer, MatchedFeatureGroup> namedMatches = namedResults.grabNamedFeatureMatchGroupsOfSize(completeSetSize,
				true);

		Integer rowCt = 0;
		createCompoundReportHeader(completeSetSize, rowCt++, sheet, styleHeader, styleWhiteSolid);

		rowCt++;

		List<String> matchedCompounds = new ArrayList<String>();
		Map<String, Integer> uniqueCompounds = new HashMap<String, Integer>();
		Map<String, Double> bestUniqueCompounds = new HashMap<String, Double>();

		for (MatchedFeatureGroup grp : namedMatches.values()) {
			try {
				if (grp.getBatches().size() < completeSetSize)
					continue;

				if (grp.getFeatureNames() != null && grp.getFeatureNames().size() > 0) {

					int k = 0;
					String compoundName = grp.getNameTag(); // .getFeatureNames().get(k++);
					Double grpCorr = grp.getAvgCorrelationIfPossible(true);

					if (!uniqueCompounds.containsKey(compoundName)) {
						uniqueCompounds.put(compoundName, null);
						bestUniqueCompounds.put(compoundName, grpCorr);
					}

					if (grpCorr > bestUniqueCompounds.get(compoundName))
						bestUniqueCompounds.put(compoundName, grpCorr);
				}
			} catch (Exception e) {
			}
		}

		List<String> compoundNamesInOrder = ListUtils.makeListFromCollection(uniqueCompounds.keySet());
		Collections.sort(compoundNamesInOrder, String.CASE_INSENSITIVE_ORDER);

		for (Integer i = 0; i < compoundNamesInOrder.size(); i++) {
			uniqueCompounds.put(compoundNamesInOrder.get(i), i + 1);
		}

		List<MatchedFeatureGroup> orderedGroups = ListUtils.makeListFromCollection(namedMatches.values());
		Collections.sort(orderedGroups, new MatchedFeatureGroupByNameTagComparator());

		for (int idx = 0; idx < orderedGroups.size(); idx++) {
			MatchedFeatureGroup grp = orderedGroups.get(idx);

			try {
				if (grp.getBatches().size() < completeSetSize)
					continue;

				if (grp.getFeatureNames() != null && grp.getFeatureNames().size() > 0) {

					XSSFRow row = (XSSFRow) sheet.createRow(rowCt);

					int i = 0, k = 0;

					String compoundNameMatch = grp.getFeatureNames().get(k++);
					while (k < grp.getFeatureNames().size() && compoundNameMatch != null
							&& compoundNameMatch.startsWith("UNK")) {
						compoundNameMatch = grp.getFeatureNames().get(k);
						k++;
					}
					// String compoundName = compoundNameMatch.substring(0,
					// compoundNameMatch.length() - 8);

					String compoundName = compoundNameMatch.substring(0, compoundNameMatch.lastIndexOf("-"));

					Double bestCorr = bestUniqueCompounds.get(compoundName);
					if (bestCorr == null)
						continue;

					Double currCorr = grp.getAvgCorrelationIfPossible(true);

					Boolean bestInGroup = (bestCorr <= currCorr && currCorr >= .9);
					if (!bestInGroup)
						createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid,
								styleWhiteSolid);
					else {
						createAppropriateRowEntry(rowCt, i++, sheet, "1", styleWhiteSolid, styleWhiteSolid,
								styleWhiteSolid);
						matchedCompounds.add(compoundName);
					}
					int shadeIdx = (uniqueCompounds.get(compoundName)) % 2;
					// if (bestInGroup)
					// shadeIdx += 2;

					createAppropriateRowEntry(rowCt, i++, sheet, compoundName, integerStyles.get(shadeIdx),
							numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(grp.getMatchGrpKey()),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					// Double corr = grp.getAvgCorrelationIfPossible(true);
					createAppropriateRowEntry(rowCt, i++, sheet,
							currCorr == null || currCorr.isNaN() ? "" : String.valueOf(currCorr),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					Double avgMass = grp.getAvgMass();
					createAppropriateRowEntry(rowCt, i++, sheet,
							avgMass == null || avgMass.isNaN() ? "" : String.valueOf(avgMass),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					Double avgRT = grp.getAvgRt();
					createAppropriateRowEntry(rowCt, i++, sheet,
							avgRT == null || avgRT.isNaN() ? "" : String.valueOf(avgRT), integerStyles.get(shadeIdx),
							numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					Double adiTotIntensity = grp.getAdjustedTotalIntensity();
					createAppropriateRowEntry(rowCt, i++, sheet,
							adiTotIntensity == null || adiTotIntensity.isNaN() ? ""
									: String.valueOf(Math.round(adiTotIntensity)),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid,
							styleWhiteSolid);

					for (int j = 0; j < grp.getFeaturesInGroup().size(); j++)
						createAppropriateRowEntry(rowCt, i++, sheet, grp.getFeaturesInGroup().get(j).getAnnotation(),
								boringStyles.get(shadeIdx), boringStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid,
							styleWhiteSolid);

					for (int j = 0; j < grp.getFeatureNames().size(); j++)
						createAppropriateRowEntry(rowCt, i++, sheet, grp.getFeatureNames().get(j),
								boringStyles.get(shadeIdx), boringStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid,
							styleWhiteSolid);

					Integer nNamed = grp.countMatchedTargetFeatures(null);
					createAppropriateRowEntry(rowCt, i++, sheet, nNamed == null ? "" : String.valueOf(nNamed),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					Integer nNaPIs = grp.countPIsWith("Na");
					createAppropriateRowEntry(rowCt, i++, sheet, nNaPIs == null ? "" : String.valueOf(nNaPIs),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					Integer nHPIs = grp.countPIsWith("H");
					createAppropriateRowEntry(rowCt, i++, sheet, nHPIs == null ? "" : String.valueOf(nHPIs),
							integerStyles.get(shadeIdx), numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					Integer compoundSetIdx = uniqueCompounds.get(compoundName);
					createAppropriateRowEntry(rowCt, i++, sheet,
							compoundSetIdx == null ? "" : String.valueOf(compoundSetIdx), integerStyles.get(shadeIdx),
							numericStyles.get(shadeIdx), boringStyles.get(shadeIdx));

					for (int j = 0; j < 10; j++) {
						createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid,
								styleWhiteSolid);
					}
					rowCt++;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				continue;
			}
		}

		for (int l = 0; l < 50; l++)
			PoiUtils.createBlankCleanRow(rowCt++, sheet, 40, styleWhiteSolid);

		sheet.setColumnWidth(1, 60 * 256);

		for (int i = 3; i < 40; i++) {
			sheet.autoSizeColumn(i);
			if (sheet.getColumnWidth(i) < 3000) {
				sheet.setColumnWidth(i, Math.max(3000, sheet.getColumnWidth(i) * 2));
				continue;
			}
			if (sheet.getColumnWidth(i) < 20000) {
				Double curr = 1.2 * sheet.getColumnWidth(i);
				sheet.setColumnWidth(i, curr.intValue());
				continue;
			}

			try {
				Double curr = sheet.getColumnWidth(i) * 1.3;
				sheet.setColumnWidth(i, curr.intValue());
			} catch (Exception e) {
				sheet.setColumnWidth(i, 20000);
			}
		}
		return matchedCompounds;
	}

	private void writeMassRegionReport(XSSFSheet sheet, List<MassRangeGroup> interestingMassRanges, int completeSetSize,
			XSSFWorkbook wb) throws Exception {

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 3));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 10, 12));

		XSSFCellStyle styleBoring = grabStyleBoring(wb);
		XSSFCellStyle styleInteger = grabStyleInteger(wb);
		XSSFCellStyle styleNumeric = grabStyleNumeric(wb);
		XSSFCellStyle styleHeader = grabStyleBatchMatchHeader(wb);

		XSSFCellStyle styleWhiteSolid = this.grabStyleWhite(wb, false);
		styleWhiteSolid.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleWhiteSolid.setFillForegroundColor(IndexedColors.WHITE.getIndex());

		List<XSSFCellStyle> boringStyles = new ArrayList<XSSFCellStyle>();
		boringStyles.add(styleBoring);

		List<XSSFCellStyle> integerStyles = new ArrayList<XSSFCellStyle>();
		integerStyles.add(styleInteger);

		List<XSSFCellStyle> numericStyles = new ArrayList<XSSFCellStyle>();
		numericStyles.add(styleNumeric);

		List<XSSFColor> yellowsForShading = grabYellowColorsForShading();
		for (int i = 0; i < yellowsForShading.size(); i++) {

			XSSFCellStyle baseStyle = grabStyleBoring(wb);
			baseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			baseStyle.setFillForegroundColor(yellowsForShading.get(i));
			boringStyles.add(baseStyle);

			XSSFCellStyle baseStyle2 = grabStyleInteger(wb);
			baseStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			baseStyle2.setFillForegroundColor(yellowsForShading.get(i));
			integerStyles.add(baseStyle2);

			XSSFCellStyle baseStyle3 = grabStyleNumeric(wb);
			baseStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			baseStyle3.setFillForegroundColor(yellowsForShading.get(i));
			numericStyles.add(baseStyle3);
		}

		Integer rowCt = 0;
		List<String> headers = this.getMassReportHeaders();

		createMassReportTitleLine(sheet, rowCt, styleWhiteSolid);
		rowCt++;
		createMassReportHeader(sheet, rowCt++, styleHeader, styleWhiteSolid);

		for (MassRangeGroup grp : interestingMassRanges) {

			// Hmmmm....
			if (grp.getMaxBatchCount() < completeSetSize - 2)
				continue;

			rowCt = sortGroupByRTAndWriteExcel(grp, completeSetSize, wb, sheet, rowCt++, numericStyles, integerStyles,
					boringStyles, styleWhiteSolid);

			for (int i = 0; i < 5; i++) {
				for (int col = 0; col < headers.size() + 40; col++) {
					PoiUtils.createRowEntry(rowCt, col, sheet, "   ", styleWhiteSolid);
				}
				rowCt++;
			}
		}

		sheet.setColumnHidden(0, true);
		sheet.setColumnWidth(3, 40 * 256);
		sheet.setColumnWidth(4, 15 * 256);
		sheet.setColumnWidth(5, 15 * 256);
		sheet.setColumnWidth(6, 15 * 256);
		sheet.setColumnWidth(8, 40 * 256);

		for (int i = 2; i < 40; i++) {

			if ((i >= 3 && i <= 6) || i == 8)
				continue;
			sheet.autoSizeColumn(i);

			if (i >= 10 && i <= 16)
				sheet.setColumnWidth(i, 15 * 256);

			if (i >= 18)
				sheet.setColumnWidth(i, 15 * 256);
		}
	}

	public Integer sortGroupByRTAndWriteExcel(MassRangeGroup grp, Integer completeSetSize, XSSFWorkbook wb,
			XSSFSheet sheet, Integer rowCt, List<XSSFCellStyle> numericStyles,

			List<XSSFCellStyle> integerStyles, List<XSSFCellStyle> boringStyles, XSSFCellStyle styleWhiteSolid)
			throws IOException {

		List<FeatureFromFile> allFeatures = grp.grabAsFeatureList();
		Map<String, Integer> grpByNames = grp.getFeatureNamesForGroupsWithNBatchesByFeatureName(completeSetSize, true);
		Map<String, Integer> indicesByName = getFeatureGroupIndicesByFeatureName(grpByNames);

		Integer startRow = rowCt, endRow = startRow + allFeatures.size();
		Double shiftDiff = null;
		FeatureFromFile f = null;

		sheet.createFreezePane(8, 2);
		SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

		ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.1",
				".1");
		PatternFormatting patternFmt = rule1.createPatternFormatting();
		patternFmt.setFillBackgroundColor(IndexedColors.ORANGE.index);

		ConditionalFormattingRule rule2 = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.1",
				".1");
		PatternFormatting patternFmt2 = rule2.createPatternFormatting();
		patternFmt2.setFillBackgroundColor(IndexedColors.ORANGE.index);

		ConditionalFormattingRule massRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.01",
				".01");
		PatternFormatting massPatternFmt = massRule.createPatternFormatting();
		massPatternFmt.setFillBackgroundColor(IndexedColors.RED.index);

		CellRangeAddress[] regions = { CellRangeAddress.valueOf("O" + startRow + ":O" + endRow) };

		CellRangeAddress[] regions2 = { CellRangeAddress.valueOf("M" + startRow + ":M" + endRow) };

		CellRangeAddress[] massRegions = { CellRangeAddress.valueOf("K" + startRow + ":K" + endRow) };

		sheetCF.addConditionalFormatting(regions, rule1);
		sheetCF.addConditionalFormatting(regions2, rule2);
		sheetCF.addConditionalFormatting(massRegions, massRule);

		for (int j = 0; j < allFeatures.size(); j++) {
			f = allFeatures.get(j);

			if (f.getOldRt() == null)
				f.setOldRt(f.getRT());

			if (f.getOldRt() == null || f.getRT() == null)
				shiftDiff = 0.0;
			else
				shiftDiff = f.getRT() - f.getOldRt();

			XSSFRow row = (XSSFRow) sheet.createRow(rowCt);
			int i = 0;

			String compareName = f.getName(); // .isEmptyOrNull(f.getFurtherAnnotation())
			// ? f.getName() : f.getFurtherAnnotation();

			Integer shadeIndex = 0;
			if (indicesByName != null && indicesByName.containsKey(compareName))
				;
			shadeIndex = indicesByName.get(compareName);
			if (shadeIndex == null)
				shadeIndex = 0;
			if (shadeIndex > 3)
				shadeIndex = (shadeIndex - 1) % 3 + 1;
			// if (StringUtils.isEmptyOrNull(f.getFurtherAnnotation()))
			// shadeIndex = 0;

			// createAppropriateRowEntry(rowCt, i++, sheet, rowCt.toString(),
			// integerStyles.get(shadeIndex), numericStyles.get(shadeIndex),
			// boringStyles.get(shadeIndex));
			createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid, styleWhiteSolid);
			// B
			createAppropriateRowEntry(rowCt, i++, sheet, f.getBatchIdx() == null ? "" : String.valueOf(f.getBatchIdx()),
					boringStyles.get(shadeIndex), boringStyles.get(shadeIndex), boringStyles.get(shadeIndex));
			// C
			createAppropriateRowEntry(rowCt, i++, sheet,
					f.getRedundancyGroup() == null ? "" : String.valueOf(f.getRedundancyGroup()),
					boringStyles.get(shadeIndex), boringStyles.get(shadeIndex), boringStyles.get(shadeIndex));
			// D
			PoiUtils.createRowEntry(rowCt, i++, sheet,
					StringUtils.isEmptyOrNull(f.getFurtherAnnotation()) ? "   " + f.getName()
							: "   " + f.getFurtherAnnotation(),
					boringStyles.get(shadeIndex));

			// StringUtils.isEmptyOrNull(f.getFurtherAnnotation()) ? "" : f.getName(),
			// boringStyles.get(shadeIndex));

			// E
			createAppropriateRowEntry(rowCt, i++, sheet, f.getMass() == null ? "" : String.valueOf(f.getMass()),
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			// F Unnamed/Old RT
			createAppropriateRowEntry(rowCt, i++, sheet,
					f.getOldRt() == null ? String.valueOf(f.getRT()) : String.valueOf(f.getOldRt()),
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			// G
			createAppropriateRowEntry(rowCt, i++, sheet,
					(f.getMedianIntensity() == null ? "" : String.valueOf(Math.round(f.getMedianIntensity()))),
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			// H
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getAnnotation(), boringStyles.get(shadeIndex));

			// I
			PoiUtils.createRowEntry(rowCt, i++, sheet, f.getIsotope(), boringStyles.get(shadeIndex));

			/// J: Match Statistics Gap
			PoiUtils.createRowEntry(rowCt, i++, sheet, "   ", styleWhiteSolid);
			Integer prev = rowCt - 1;

			// K : Mass diff
			XSSFCell formulaCellMassDiff = row.createCell(i++);
			if (rowCt > 1) {
				String numStr = "ABS(E" + (rowCt + 1) + "- E" + rowCt + ")";
				formulaCellMassDiff.setCellFormula("IF(ISNUMBER(" + numStr + ")," + numStr + ", \"--NA--\")");
			} else
				formulaCellMassDiff.setCellValue("");

			formulaCellMassDiff.setCellStyle(numericStyles.get(shadeIndex));

			XSSFFormulaEvaluator formulaEvaluatorMass = wb.getCreationHelper().createFormulaEvaluator();
			formulaEvaluatorMass.evaluateFormulaCell(formulaCellMassDiff);

			// L : Unnamed/Old RT again
			createAppropriateRowEntry(rowCt, i++, sheet,
					f.getOldRt() == null ? String.valueOf(f.getRT()) : String.valueOf(f.getOldRt()),
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			// M Unnamed RT Diff
			XSSFCell formulaCell2 = row.createCell(i++);
			if (rowCt > 1) {
				String numStr = "ABS(L" + (rowCt + 1) + "- L" + rowCt + ")";
				formulaCell2.setCellFormula("IF(ISNUMBER(" + numStr + ")," + numStr + ", \"--NA--\")");
				// formulaCell2.setCellFormula("ABS(L" + (rowCt +1) + "-" + "L" + rowCt + ")");
			} else
				formulaCell2.setCellValue("");

			formulaCell2.setCellStyle(numericStyles.get(shadeIndex));

			XSSFFormulaEvaluator formulaEvaluator2 = wb.getCreationHelper().createFormulaEvaluator();
			formulaEvaluator2.evaluateFormulaCell(formulaCell2);

			// N : Projected/New RT
			createAppropriateRowEntry(rowCt, i++, sheet, f.getRT() == null ? "" : String.valueOf(f.getRT()),
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			// O : New RT Diff
			XSSFCell formulaCell = row.createCell(i++);
			if (rowCt > 1) {
				String numStr = "ABS(N" + (rowCt + 1) + "- N" + rowCt + ")";
				formulaCell.setCellFormula("IF(ISNUMBER(" + numStr + ")," + numStr + ", \"--NA--\")");
			} else
				formulaCell.setCellValue("");

			formulaCell.setCellStyle(numericStyles.get(shadeIndex));

			XSSFFormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
			formulaEvaluator.evaluateFormulaCell(formulaCell);

			// P RT Match Shift
			createAppropriateRowEntry(rowCt, i++, sheet, shiftDiff == null ? "" : String.valueOf(shiftDiff),
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			PoiUtils.createRowEntry(rowCt, i++, sheet, "   ", styleWhiteSolid);

			try {
				PoiUtils.createRowEntry(rowCt, i++, sheet,
						StringUtils.isEmptyOrNull(f.getFurtherAnnotation()) ? "" : f.getName(),
						boringStyles.get(shadeIndex));
			} catch (Exception e) {
			}

			String tempStr = null;
			try {
				tempStr = String.valueOf(f.getCompoundMass());
				createAppropriateRowEntry(rowCt, i++, sheet,
						(tempStr == null || tempStr.equals("null")) ? "-" : tempStr, integerStyles.get(shadeIndex),
						numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));
			} catch (Exception e) {
			}

			try {
				tempStr = String.valueOf(f.getCompoundRT());
				createAppropriateRowEntry(rowCt, i++, sheet,
						(tempStr == null || tempStr.equals("null")) ? "-" : tempStr, integerStyles.get(shadeIndex),
						numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));
			} catch (Exception e) {
			}

			try {
				tempStr = String.valueOf(f.getCompoundMass() - f.getMass());
			} catch (Exception e) {
				tempStr = "-";
			}

			createAppropriateRowEntry(rowCt, i++, sheet,
					(tempStr == null || tempStr.equals("null") || tempStr.contains("#DIV/0!")) ? "-" : tempStr,
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			try {
				tempStr = "-";
				if (f.getCompoundRT() != null && f.getOldRt() != null)
					tempStr = String.valueOf(f.getCompoundRT() - f.getOldRt());
			} catch (Exception e) {
				tempStr = "-";
			}

			createAppropriateRowEntry(rowCt, i++, sheet,
					(tempStr == null || tempStr.equals("null") || tempStr.contains("#DIV/0!")) ? "-" : tempStr,
					integerStyles.get(shadeIndex), numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));
			createAppropriateRowEntry(rowCt, i++, sheet, "-", integerStyles.get(shadeIndex),
					numericStyles.get(shadeIndex), boringStyles.get(shadeIndex));

			for (int reps = 0; reps < 30; reps++) {
				createAppropriateRowEntry(rowCt, i++, sheet, " ", styleWhiteSolid, styleWhiteSolid, styleWhiteSolid);
			}
			rowCt++;
		}
		return rowCt;
	}

	private Map<String, Integer> getFeatureGroupIndicesByFeatureName(Map<String, Integer> grpByNames) {

		Map<Integer, Integer> indicesForGroups = new HashMap<Integer, Integer>();

		Integer nGroups = 0;
		for (Integer group : grpByNames.values()) {
			if (indicesForGroups.containsKey(group))
				continue;
			indicesForGroups.put(group, ++nGroups);
		}

		Map<String, Integer> indicesByName = new HashMap<String, Integer>();
		for (String fName : grpByNames.keySet()) {
			Integer group = grpByNames.get(fName);
			Integer groupIdx = indicesForGroups.get(group);
			indicesByName.put(fName, groupIdx);
		}
		return indicesByName;
	}

	private List<String> getMassReportHeaders() {

		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Batch");
		headers.add("Match Group");
		headers.add("Feature Name");
		headers.add("Monoisotopic M/Z");
		headers.add("Unnamed RT");
		headers.add("Median Intensity");
		headers.add("Binner Annotation");
		headers.add("Isotope");

		headers.add("");
		headers.add("Prev Row Diff - M/Z");
		headers.add("Unnamed RT");
		headers.add("Prev Row Diff - Unnamed RT");
		headers.add("Projected Match RT");
		headers.add("Prev Row Diff - Match RT");
		headers.add("RT Match Shift");

		headers.add("");
		headers.add("Mapped Compound");
		headers.add("Named Mass");
		headers.add("Named RT");
		headers.add("Delta Mass Named to Unnamed");
		headers.add("Delta RT Named to Unnamed");
		headers.add("Named-Unnamed Correlation");

		return headers;
	}

	private List<String> getCompoundReportHeaders(int completeSetSize) {

		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Compound");
		headers.add("Match Group");
		headers.add("Avg Corr");
		headers.add("Avg Mass");
		headers.add("Avg RT");
		headers.add("Adj Avg Intensity");
		headers.add(" ");

		for (int i = 0; i < completeSetSize; i++)
			headers.add("Batch " + (i + 1));

		headers.add("  ");

		for (int i = 0; i < completeSetSize; i++)
			headers.add("Batch " + (i + 1));

		headers.add(" ");
		headers.add("Named Features Mapped");
		headers.add("# Na PIs");
		headers.add("# H PIs");
		headers.add("Compound Match Group");
		for (int i = 0; i < 10; i++)
			headers.add("  ");

		return headers;
	}

	private List<String> getMissingCompoundReportHeaders(int completeSetSize) {

		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Compound");
		headers.add("Unmapped Batches");
		headers.add("Max Poss Correlation For Complete Set");
		headers.add("Relevant Match Groups");
		headers.add("Largest MG/MG Fragment Corr");

		for (int i = 0; i < 10; i++)
			headers.add("  ");

		return headers;
	}

	private List<String> getMapReportHeaders() {

		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Named Feature");
		headers.add("Unnamed Feature");
		headers.add("Correlation");
		headers.add("Named Mass");
		headers.add("Named RT");
		headers.add("Delta Corr - 1");
		headers.add("Delta Corr - 2");
		headers.add("Delta Corr - 3");

		for (int i = 0; i < 10; i++)
			headers.add("  ");

		return headers;
	}

	private int createMapReportHeader(Integer rowCt, XSSFSheet sheet, XSSFCellStyle styleHeader,
			XSSFCellStyle styleWhite) {

		List<String> headers = getMapReportHeaders();

		for (int col = 0; col < headers.size(); col++)
			if (!StringUtils.isEmptyOrNull(headers.get(col)))
				PoiUtils.createRowEntry(rowCt, col, sheet, headers.get(col), styleHeader, 30);
			else
				PoiUtils.createRowEntry(rowCt, col, sheet, "", styleWhite);

		for (int i = 0; i < 10; i++)
			headers.add("  ");

		Row currRow = sheet.getRow(rowCt);
		currRow.setHeight((short) (currRow.getHeight() * 2));

		return headers.size();
	}

	private int createMissingCompoundHeader(int completeSetSize, Integer rowCt, XSSFSheet sheet,
			XSSFCellStyle styleHeader, XSSFCellStyle styleWhite) {

		List<String> headers = this.getMissingCompoundReportHeaders(completeSetSize);

		for (int col = 0; col < headers.size(); col++)
			if (!StringUtils.isEmptyOrNull(headers.get(col)))
				PoiUtils.createRowEntry(rowCt, col, sheet, headers.get(col), styleHeader, 30);
			else
				PoiUtils.createRowEntry(rowCt, col, sheet, "", styleWhite);

		Row currRow = sheet.getRow(rowCt);
		currRow.setHeight((short) (currRow.getHeight() * 2));

		return headers.size();
	}

	private int createCompoundReportHeader(int completeSetSize, Integer rowCt, XSSFSheet sheet,
			XSSFCellStyle styleHeader, XSSFCellStyle styleWhite) {

		List<String> headers = this.getCompoundReportHeaders(completeSetSize);

		for (int col = 0; col < headers.size(); col++)
			if (!StringUtils.isEmptyOrNull(headers.get(col)))
				PoiUtils.createRowEntry(rowCt, col + 1, sheet, headers.get(col), styleHeader, 30);
			else
				PoiUtils.createRowEntry(rowCt, col + 1, sheet, "", styleWhite);

		Row currRow = sheet.getRow(rowCt);
		currRow.setHeight((short) (currRow.getHeight() * 2));

		return headers.size();
	}

	private void createMassReportTitleLine(XSSFSheet sheet, Integer rowCt, XSSFCellStyle styleSolidWhite) {

		List<String> headers = getMassReportHeaders();

		styleSolidWhite.setAlignment(HorizontalAlignment.LEFT);
		styleSolidWhite.setIndention((short) 1);

		PoiUtils.createRowEntry(rowCt, 0, sheet, "  ", styleSolidWhite, 30);
		PoiUtils.createRowEntry(rowCt, 1, sheet, "Unnamed Feature Matching", styleSolidWhite, 30);

		for (int col = 2; col < headers.size() + 40; col++) {
			if (col == 10)
				PoiUtils.createRowEntry(rowCt, col, sheet, "Match Diagnostics", styleSolidWhite, 30);
			else if (col == 17)
				PoiUtils.createRowEntry(rowCt, col, sheet, "Named-Unnamed Mapping", styleSolidWhite, 30);
			else
				PoiUtils.createRowEntry(rowCt, col, sheet, " ", styleSolidWhite, 30);
		}
	}

	private int createMassReportHeader(XSSFSheet sheet, Integer rowCt, XSSFCellStyle styleHeader,
			XSSFCellStyle styleWhite) {

		List<String> headers = getMassReportHeaders();

		for (int col = 0; col < headers.size(); col++) {
			if (!StringUtils.isEmptyOrNull(headers.get(col)))
				PoiUtils.createRowEntry(rowCt, col + 1, sheet, headers.get(col), styleHeader, 30);
			else
				PoiUtils.createRowEntry(rowCt, col + 1, sheet, "", styleWhite);
		}
		return headers.size();
	}

	public String getNamedResultFileName() {
		return namedResultFileName;
	}

	public void setNamedResultFileName(String namedResultFileName) {
		this.namedResultFileName = namedResultFileName;
	}

	public List<XSSFCellStyle> grabBoringStyles(XSSFWorkbook wb) throws Exception {

		XSSFCellStyle styleBoring = grabStyleBoring(wb);
		XSSFCellStyle styleBoringGrey = grabStyleBoring(wb);

		styleBoring.setAlignment(HorizontalAlignment.RIGHT);
		styleBoring.setIndention((short) 2);

		styleBoringGrey.setAlignment(HorizontalAlignment.RIGHT);
		styleBoringGrey.setIndention((short) 2);

		styleBoringGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleBoringGrey.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));

		List<XSSFCellStyle> boringStyles = new ArrayList<XSSFCellStyle>();
		boringStyles.add(styleBoring);
		boringStyles.add(styleBoringGrey);

		return boringStyles;
	}

	public List<XSSFCellStyle> grabIntegerStyles(XSSFWorkbook wb) throws Exception {

		XSSFCellStyle styleInteger = grabStyleInteger(wb);
		XSSFCellStyle styleIntegerGrey = grabStyleInteger(wb, true);

		styleInteger.setAlignment(HorizontalAlignment.RIGHT);
		styleInteger.setIndention((short) 2);

		styleIntegerGrey.setAlignment(HorizontalAlignment.RIGHT);
		styleIntegerGrey.setIndention((short) 2);

		List<XSSFCellStyle> integerStyles = new ArrayList<XSSFCellStyle>();
		integerStyles.add(styleInteger);
		integerStyles.add(styleIntegerGrey);

		return integerStyles;
	}

	public List<XSSFCellStyle> grabNumericStyles(XSSFWorkbook wb) throws Exception {

		XSSFCellStyle styleNumeric = grabStyleNumeric(wb);
		XSSFCellStyle styleNumericGrey = grabStyleNumeric(wb, true);

		styleNumeric.setAlignment(HorizontalAlignment.RIGHT);
		styleNumeric.setIndention((short) 2);

		styleNumericGrey.setAlignment(HorizontalAlignment.RIGHT);
		styleNumericGrey.setIndention((short) 2);

		List<XSSFCellStyle> numericStyles = new ArrayList<XSSFCellStyle>();
		numericStyles.add(styleNumeric);
		numericStyles.add(styleNumericGrey);

		return numericStyles;

	}

	public XSSFCellStyle grabStyleWhiteSolid(XSSFWorkbook wb) {

		XSSFCellStyle styleWhiteSolid = this.grabStyleWhite(wb, false);
		styleWhiteSolid.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleWhiteSolid.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		return styleWhiteSolid;
	}
}

/*
 * String namedResultFileName;
 * 
 * public BatchMatchNamedResultsWriter() { super(); }
 * 
 * public String getNamedResultFileName() { return namedResultFileName; }
 * 
 * public void setNamedResultFileName(String namedResultFileName) {
 * this.namedResultFileName = namedResultFileName; }
 * 
 * 
 * public void reportOnTargetFeatures(PostProcessDataSet data, Map<String,
 * String> targetFeatures) {
 * 
 * BatchMatchNamedMassResults results = new BatchMatchNamedMassResults(data); if
 * (results == null) return;
 * 
 * List<MassRangeGroup> interestingMassRanges =
 * results.grabMassRangesToStudy(targetFeatures);
 * 
 * BufferedOutputStream bos = null; try { bos = new BufferedOutputStream(new
 * FileOutputStream(new File(namedResultFileName)));
 * 
 * writeMassRegionReport(bos, interestingMassRanges); bos.close(); } catch
 * (IOException ioe) { ioe.printStackTrace(); } }
 * 
 * 
 * public void reportOnTargetFeaturesToExcel(PostProcessDataSet data,
 * Map<String, String> targetFeatures) throws Exception {
 * 
 * BatchMatchNamedMassResults results = new BatchMatchNamedMassResults(data); if
 * (results == null) return;
 * 
 * List<MassRangeGroup> interestingMassRanges =
 * results.grabMassRangesToStudy(targetFeatures);
 * 
 * XSSFWorkbook wb = new XSSFWorkbook(); //or new HSSFWorkbook(); XSSFSheet
 * sheet = wb.createSheet("Named Mass Groups");
 * 
 * try { writeExcelMassRegionReport(sheet, interestingMassRanges, wb);
 * 
 * FileOutputStream fileOut = new FileOutputStream(namedResultFileName);
 * wb.write(fileOut); fileOut.close();
 * System.out.println("Successfully created named mass group workbook");
 * 
 * } catch (IOException ioe) { ioe.printStackTrace(); } }
 * 
 * 
 * private void writeExcelMassRegionReport(XSSFSheet sheet, List<MassRangeGroup>
 * interestingMassRanges, XSSFWorkbook wb) throws Exception {
 * 
 * XSSFCellStyle styleBoring = grabStyleBoring(wb); XSSFCellStyle styleInteger =
 * grabStyleInteger(wb); XSSFCellStyle styleIntegerGrey = grabStyleInteger(wb,
 * true);
 * 
 * XSSFCellStyle styleNumeric = grabStyleNumeric(wb); XSSFCellStyle styleHeader
 * = grabStyleBatchMatchHeader(wb);
 * 
 * Integer rowCt = 0; ArrayList<String> headers = new ArrayList<String>();
 * createExcelHeader(sheet, rowCt++, styleHeader); for (MassRangeGroup grp:
 * interestingMassRanges) { rowCt = sortGroupByRTAndWriteExcel(grp, wb, sheet,
 * rowCt++, styleNumeric, styleInteger, styleBoring); for (int i = 0; i < 3;
 * i++) { XSSFRow row = (XSSFRow) sheet.createRow(rowCt++); for (int col = 0;
 * col < headers.size(); col++) { row.createCell(col).setCellValue(""); } } } }
 * 
 * 
 * private void writeMassRegionReport(BufferedOutputStream bos,
 * List<MassRangeGroup> interestingMassRanges) throws IOException {
 * 
 * System.out.println(createHeader("\t")); bos.write((createHeader(",") +
 * BatchMatchConstants.LINE_SEPARATOR).getBytes());
 * 
 * for (MassRangeGroup grp : interestingMassRanges) { sortAndPrintByRT(grp,
 * bos);
 * 
 * for (int i = 0; i < 3; i++) { bos.write(("" +
 * BatchMatchConstants.LINE_SEPARATOR).getBytes()); } } }
 * 
 * public Integer sortGroupByRTAndWriteExcel(MassRangeGroup grp, XSSFWorkbook
 * wb, XSSFSheet sheet, Integer rowCt, XSSFCellStyle styleNumeric, XSSFCellStyle
 * styleInteger, XSSFCellStyle styleBoring) throws IOException {
 * 
 * List<FeatureFromFile> allFeatures = grp.renewAllFeatures(); Integer startRow
 * = rowCt, endRow = startRow + allFeatures.size(); Double shiftDiff = null;
 * FeatureFromFile f = null;
 * 
 * sheet.createFreezePane(8, 1); SheetConditionalFormatting sheetCF =
 * sheet.getSheetConditionalFormatting();
 * 
 * ConditionalFormattingRule rule1 =
 * sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.1",
 * ".1"); PatternFormatting patternFmt = rule1.createPatternFormatting();
 * patternFmt.setFillBackgroundColor(IndexedColors.ORANGE.index);
 * 
 * ConditionalFormattingRule rule2 =
 * sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.1",
 * ".1"); PatternFormatting patternFmt2 = rule2.createPatternFormatting();
 * patternFmt2.setFillBackgroundColor(IndexedColors.ORANGE.index);
 * 
 * ConditionalFormattingRule rule3 =
 * sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.01",
 * ".01"); PatternFormatting patternFmt3 = rule3.createPatternFormatting();
 * patternFmt3.setFillBackgroundColor(IndexedColors.RED.index);
 * 
 * CellRangeAddress[] regions = { CellRangeAddress.valueOf("H" + startRow + ":H"
 * + endRow) };
 * 
 * CellRangeAddress[] regions2 = { CellRangeAddress.valueOf("J" + startRow +
 * ":J" + endRow) };
 * 
 * CellRangeAddress[] regions3 = { CellRangeAddress.valueOf("F" + startRow +
 * ":F" + endRow) };
 * 
 * sheetCF.addConditionalFormatting(regions, rule1);
 * sheetCF.addConditionalFormatting(regions2, rule2);
 * sheetCF.addConditionalFormatting(regions3, rule3);
 * 
 * for (int j = 0; j < allFeatures.size(); j++) { f = allFeatures.get(j); if
 * (f.getOldRt() == null) f.setOldRt(f.getRt());
 * 
 * if (f.getOldRt() == null || f.getRt() == null) shiftDiff = 0.0; else
 * shiftDiff = f.getRt() - f.getOldRt();
 * 
 * XSSFRow row = (XSSFRow) sheet.createRow(rowCt); int i = 0; //Freeze
 * createAppropriateRowEntry(rowCt, i++, sheet, rowCt.toString(), styleInteger,
 * styleNumeric, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getBatchIdx() == null ? "" :
 * String.valueOf(f.getBatchIdx()), styleBoring, styleBoring, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getRedundancyGroup() == null ?
 * "" : String.valueOf(f.getRedundancyGroup()), styleBoring, styleBoring,
 * styleBoring);
 * 
 * PoiUtils.createRowEntry(rowCt, i++, sheet, "   " + f.getName(), styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getMass() == null ? "" :
 * String.valueOf(f.getMass()), styleInteger, styleNumeric, styleBoring);
 * 
 * 
 * Integer prev = rowCt -1; XSSFCell formulaCell3 = row.createCell(i++); if
 * (rowCt > 1) formulaCell3.setCellFormula("ABS(E" + (rowCt+1) + "-" + "E" +
 * rowCt + ")"); else formulaCell3.setCellValue("");
 * 
 * formulaCell3.setCellStyle(styleNumeric);
 * 
 * XSSFFormulaEvaluator formulaEvaluator3 =
 * wb.getCreationHelper().createFormulaEvaluator();
 * formulaEvaluator3.evaluateFormulaCell(formulaCell3);
 * 
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getRT() == null ? "" :
 * String.valueOf(f.getRT()), styleInteger, styleNumeric, styleBoring);
 * 
 * XSSFCell formulaCell = row.createCell(i++); if (rowCt > 1)
 * formulaCell.setCellFormula("ABS(G" + (rowCt+1) + "-" + "G" + rowCt + ")");
 * else formulaCell.setCellValue("");
 * 
 * formulaCell.setCellStyle(styleNumeric);
 * 
 * XSSFFormulaEvaluator formulaEvaluator =
 * wb.getCreationHelper().createFormulaEvaluator();
 * formulaEvaluator.evaluateFormulaCell(formulaCell);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getOldRt() == null ?
 * String.valueOf(f.getRt()) : String.valueOf(f.getOldRt()), styleInteger,
 * styleNumeric, styleBoring);
 * 
 * 
 * 
 * XSSFCell formulaCell2 = row.createCell(i++); if (rowCt > 1)
 * formulaCell2.setCellFormula("ABS(I" + (rowCt +1) + "-" + "I" + rowCt + ")");
 * else formulaCell2.setCellValue("");
 * 
 * formulaCell2.setCellStyle(styleNumeric);
 * 
 * XSSFFormulaEvaluator formulaEvaluator2 =
 * wb.getCreationHelper().createFormulaEvaluator();
 * formulaEvaluator2.evaluateFormulaCell(formulaCell2);
 * 
 * //createAppropriateRowEntry(rowCt, i++, sheet, rtOldDiff == null ? "" : //
 * String.valueOf(rtOldDiff), // styleInteger, styleNumeric, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, shiftDiff == null ? "" :
 * String.valueOf(shiftDiff), styleInteger, styleNumeric, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, (f.getMedianIntensity() == null
 * ? "" : String.valueOf(Math.round(f.getMedianIntensity()))), styleInteger,
 * styleNumeric, styleBoring);
 * 
 * PoiUtils.createRowEntry(rowCt, i++, sheet, f.getAnnotation(), styleBoring);
 * PoiUtils.createRowEntry(rowCt, i++, sheet, f.getIsotope(), styleBoring);
 * PoiUtils.createRowEntry(rowCt, i++, sheet,
 * StringUtils.isEmptyOrNull(f.getFurtherAnnotation()) ? "" :
 * f.getFurtherAnnotation() + ", " + f.getPossibleRedundancies(), styleBoring);
 * 
 * rowCt++; } return rowCt; }
 * 
 * private List<String> getHeaderList() { ArrayList<String> headers = new
 * ArrayList<String>();
 * 
 * headers.add("Batch"); headers.add("Match Group");
 * headers.add("Feature Name"); headers.add("Monoisotopic M/Z");
 * headers.add("Diff - Mass"); headers.add("New RT");
 * headers.add("Diff - New RT"); headers.add("Old RT");
 * headers.add("Diff - Old RT"); headers.add("RT Shift");
 * headers.add("Median Intensity"); headers.add("Annotation");
 * headers.add("Isotope"); headers.add("Original Name"); return headers; }
 * 
 * private String createHeader(String separator) {
 * 
 * StringBuilder sb = new StringBuilder();
 * 
 * List<String> headers = getHeaderList(); for (String header: headers) {
 * sb.append(header + separator); } return sb.toString(); }
 * 
 * private int createExcelHeader(XSSFSheet sheet, Integer rowCt, XSSFCellStyle
 * styleHeader) {
 * 
 * List<String> headers = getHeaderList();
 * 
 * PoiUtils.createRowEntry(rowCt, 0, sheet, "Index", styleHeader, 30); for (int
 * col = 0; col < headers.size(); col++) PoiUtils.createRowEntry(rowCt, col+1,
 * sheet, headers.get(col), styleHeader, 30);
 * 
 * return headers.size(); }
 * 
 * public void sortAndPrintByRT(MassRangeGroup grp, BufferedOutputStream bos)
 * throws IOException {
 * 
 * List<FeatureFromFile> allFeatures = grp.renewAllFeatures();
 * 
 * Double massDiff = null, rtNewDiff = null, rtOldDiff = null, shiftDiff = null;
 * FeatureFromFile f = null, prevFeature = null;
 * 
 * for (int i = 0; i < allFeatures.size(); i++) {
 * 
 * StringBuilder sb = new StringBuilder();
 * 
 * f = allFeatures.get(i); if (f.getOldRt() == null) f.setOldRt(f.getRt());
 * 
 * if (i > 0) { prevFeature = allFeatures.get(i-1); massDiff =
 * Math.abs(f.getMass() - prevFeature.getMass()); rtNewDiff = Math.abs(f.getRt()
 * - prevFeature.getRt()); rtOldDiff = Math.abs(f.getOldRt() -
 * prevFeature.getOldRt()); } if (f.getOldRt() == null || f.getRt() == null)
 * shiftDiff = 0.0; else shiftDiff = f.getRt() - f.getOldRt();
 * 
 * String separator = "\t"; int loop = 0;
 * 
 * while (loop < 2) {
 * 
 * sb.append((f.getBatchIdx() == null ? "" : f.getBatchIdx()) + separator);
 * sb.append((f.getRedundancyGroup() == null ? "" : f.getRedundancyGroup()) +
 * separator); sb.append((f.getName() == null ? "" : f.getName()) + separator);
 * sb.append((f.getMass() == null ? "" : String.format("%8.4f", f.getMass())) +
 * separator); sb.append(massDiff == null ? "" : String.format("%5.3f",
 * massDiff) + separator); sb.append((f.getRt() == null ? "" :
 * String.format("%5.3f", f.getRt())) + separator); sb.append((rtNewDiff == null
 * ? "" : String.format("%5.3f", rtNewDiff)) + separator);
 * sb.append((f.getOldRt() == null ? String.format("%5.3f", f.getRt()) :
 * String.format("%5.3f",f.getOldRt()))+ separator); sb.append((rtOldDiff ==
 * null ? "" : String.format("%5.3f", rtOldDiff)) + separator);
 * sb.append((String.format("%5.3f", shiftDiff)) + separator);
 * sb.append((f.getMedianIntensity() == null ? "" : String.format("%f",
 * f.getMedianIntensity())) + separator); sb.append((f.getAnnotation() == null ?
 * "" : f.getAnnotation()) + separator); sb.append((f.getIsotope() == null ? ""
 * : f.getIsotope()) + separator); sb.append((f.getFurtherAnnotation() == null ?
 * "" : f.getFurtherAnnotation()) + separator);
 * 
 * if (loop++ == 0) { System.out.println(sb.toString()); sb = new
 * StringBuilder(); separator = ","; } else { bos.write((sb.toString() +
 * BatchMatchConstants.LINE_SEPARATOR).getBytes()); } } } } }
 * 
 * 
 */
