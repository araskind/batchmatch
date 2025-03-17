////////////////////////////////////////////////////
// BatchMatchExpandedFeatureWriter.java
// Written by Jan Wigginton, September 2019
////////////////////////////////////////////////////
package edu.umich.batchmatch.io.sheetwriters;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import edu.umich.batchmatch.data.FeatureFromFile;
import edu.umich.batchmatch.main.BatchMatchConstants;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.utils.StringUtils;

public class BatchMatchExpandedFeatureWriter extends BatchMatchFeatureWriter {

	private static final long serialVersionUID = -2361568255632647473L;

	private Boolean usingGrey = true;
	private int nNonIntensityHeaders = 0;
	private Map<String, String> derivedColNameMapping;
	private Map<String, Double> featureNameToOldRtMap;
	private Integer lastRedundancyGrp = -1; // currGrp = null;
	private String sheetName = null;

	public BatchMatchExpandedFeatureWriter(SXSSFWorkbook workBook) {
		this(workBook, null);
	}

	public BatchMatchExpandedFeatureWriter(SXSSFWorkbook workBook, Map<String, String> derivedColNameMapping) {
		super(workBook);
		this.derivedColNameMapping = derivedColNameMapping;
	}

	public void writeExpandedFeatureSheet(List<String> nonStandardHeaders, List<String> intensityHeaders,
			List<FeatureFromFile> features, List<XSSFCellStyle> styleList, Boolean useAmbiguousFormat)
			throws Exception {

		Sheet sheet = createEmptySheet(getSheetName() == null ? "All Features" : getSheetName(), workBook);

		XSSFCellStyle styleBoring = grabStyleBoring(workBook);
		XSSFCellStyle styleInteger = grabStyleInteger(workBook);
		XSSFCellStyle styleIntegerGrey = grabStyleInteger(workBook, true);

		XSSFCellStyle styleNumeric = grabStyleNumeric(workBook);
		XSSFCellStyle styleNumericGrey = grabStyleNumeric(workBook, true);
		XSSFCellStyle styleBoringLeft = grabStyleBoringLeft(workBook);
		XSSFCellStyle styleBoringLeftGrey = grabStyleBoringLeftGrey(workBook);
		XSSFCellStyle styleToClone = styleList.get(PostProccessConstants.STYLE_BORING_GREY);
		XSSFCellStyle styleBoringBlueGrey = (XSSFCellStyle) styleToClone.clone();
		XSSFCellStyle styleBlankBoring = this.grabStyleBlankBoring(workBook);
		XSSFCellStyle styleHeader = this.grabStyleBatchMatchHeader(workBook);
		XSSFCellStyle styleBoringUnderlinedEntry = this.grabStyleBoringUnderlined(workBook);

		styleBoringBlueGrey.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));
		styleBoringBlueGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleBoringLeft.setIndention((short) 1);
		styleBoringLeftGrey.setIndention((short) 1);

		Font font = workBook.createFont();
		font.setFontName("Courier");
		styleNumeric.setFont(font);
		styleBoring.setFont(font);
		styleInteger.setFont(font);
		styleNumericGrey.setFont(font);
		styleBoringLeft.setFont(font);
		styleIntegerGrey.setFont(font);

		sheet.createFreezePane(8, 1);

		createHeader(nonStandardHeaders, intensityHeaders, sheet, styleHeader, styleBlankBoring, false);
		createFeatureList(sheet, features, intensityHeaders, styleBoring, styleInteger, styleIntegerGrey, styleNumeric,
				styleNumericGrey, styleBoringLeft, styleBoringLeftGrey, styleBoringBlueGrey, styleList,
				styleBlankBoring, styleBoringUnderlinedEntry, useAmbiguousFormat);
		// Force column autosize to accommodate header for non-intensity cols
		createHeader(nonStandardHeaders, intensityHeaders, sheet, styleHeader, styleBlankBoring, true);

		for (int i = 0; i < nNonIntensityHeaders; i++) {
			// sheet.autoSizeColumn(i);
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

	private void createFeatureList(Sheet sheet, List<FeatureFromFile> features, List<String> intensityHeaders,
			XSSFCellStyle styleBoring, XSSFCellStyle styleInteger, XSSFCellStyle styleIntegerGrey,
			XSSFCellStyle styleNumeric, XSSFCellStyle styleNumericGrey, XSSFCellStyle styleBoringLeft,
			XSSFCellStyle styleBoringLeftGrey, XSSFCellStyle styleBoringBlueGrey, List<XSSFCellStyle> styleList,
			XSSFCellStyle styleBlankBoring, XSSFCellStyle styleBoringUnderlinedEntry, Boolean useAmbiguous) {

		XSSFCellStyle styleNumericShortestGrey = styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTEST_GREY); // ,
																													// this.grabStyleNumericShorter(workBook,
																													// true,
																													// 2));
		XSSFCellStyle styleNumericShortest = styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTEST); // ,
																											// this.grabStyleNumericShorter(workBook,
																											// true,
																											// 2));

		Font font = workBook.createFont();
		font.setFontName("Courier");
		styleNumericShortest.setFont(font);
		styleNumericShortestGrey.setFont(font);

		XSSFCellStyle styleBoringRed = (XSSFCellStyle) styleBoring.clone();
		XSSFCellStyle styleNumericRed = (XSSFCellStyle) styleNumeric.clone();
		XSSFCellStyle styleNumericGreyRed = (XSSFCellStyle) styleNumericGrey.clone();
		XSSFCellStyle styleBoringLeftRed = (XSSFCellStyle) styleBoringLeft.clone();
		XSSFCellStyle styleBoringLeftGreyRed = (XSSFCellStyle) styleBoringLeftGrey.clone();
		XSSFCellStyle styleBoringBlueGreyRed = (XSSFCellStyle) styleBoringBlueGrey.clone();
		XSSFCellStyle styleIntegerRed = (XSSFCellStyle) styleInteger.clone();
		XSSFCellStyle styleIntegerGreyRed = (XSSFCellStyle) styleIntegerGrey.clone();
		XSSFCellStyle styleNumericShortestGreyRed = (XSSFCellStyle) styleNumericShortestGrey.clone();
		XSSFCellStyle styleNumericShortestRed = (XSSFCellStyle) styleNumericShortest.clone();

		Font redFont = workBook.createFont();
		redFont.setColor(IndexedColors.RED.getIndex());
		redFont.setFontName("Courier");

		Font greenFont = workBook.createFont();
		greenFont.setColor(IndexedColors.YELLOW.getIndex());
		greenFont.setFontName("Courier");

		styleBoringRed.setFont(redFont);
		styleNumericGreyRed.setFont(redFont);
		styleBoringLeftRed.setFont(redFont);
		styleBoringLeftGreyRed.setFont(redFont);
		styleBoringBlueGreyRed.setFont(redFont);
		styleIntegerRed.setFont(redFont);
		styleIntegerGreyRed.setFont(redFont);
		styleNumericRed.setFont(redFont);
		styleNumericShortestGreyRed.setFont(redFont);
		styleNumericShortestRed.setFont(redFont);

		rowCt = 1;
		int startCol = 0;
		for (int j = 0; j < features.size(); j++) {

			FeatureFromFile feature = features.get(j);
			if (feature == null) {

				if (useAmbiguous) {
					for (int row = rowCt; row < rowCt + 3; row++)
						for (int col = 0; col < 100; col++)
							PoiUtils.createRowEntry(row, col, sheet, "", styleBlankBoring, 30);
					rowCt += 3;
				}
				continue;
			}
			// FeatureFromFile nextFeature = j < features.size() - 1 ? features.get(j + 1) :
			// null;

			if (feature.getRedundancyGroup() != null)
				feature.setFlaggedAsDuplicate(true);

			startCol = writeFeatureEntry(sheet, rowCt, feature,
					feature.getFlaggedAsDuplicate() ? styleBoringRed : styleBoring,
					feature.getFlaggedAsDuplicate() ? styleIntegerRed : styleInteger,
					feature.getFlaggedAsDuplicate() ? styleIntegerGreyRed : styleIntegerGrey,
					feature.getFlaggedAsDuplicate() ? styleNumericRed : styleNumeric,
					feature.getFlaggedAsDuplicate() ? styleNumericGreyRed : styleNumericGrey,
					feature.getFlaggedAsDuplicate() ? styleBoringLeftRed : styleBoringLeft,
					feature.getFlaggedAsDuplicate() ? styleBoringLeftGreyRed : styleBoringLeftGrey,
					feature.getFlaggedAsDuplicate() ? styleBoringBlueGreyRed : styleBoringBlueGrey, styleList,
					feature.getFlaggedAsDuplicate() ? styleBlankBoring : styleBlankBoring,
					feature.getFlaggedAsDuplicate() ? styleNumericShortestGreyRed : styleNumericShortestGrey,
					feature.getFlaggedAsDuplicate() ? styleNumericShortestRed : styleNumericShortest);

			startCol++;
			startCol = createIntensitySection(feature, sheet, startCol, styleList, styleBlankBoring,
					feature.getFlaggedAsDuplicate() ? styleBoringRed : styleBoring,
					feature.getFlaggedAsDuplicate() ? styleBoringBlueGreyRed : styleBoringBlueGrey,
					feature.getFlaggedAsDuplicate() ? styleIntegerRed : styleInteger,
					feature.getFlaggedAsDuplicate() ? styleIntegerGreyRed : styleIntegerGrey,
					feature.getFlaggedAsDuplicate() ? styleNumericRed : styleNumeric,
					feature.getFlaggedAsDuplicate() ? styleNumericGreyRed : styleNumericGrey, intensityHeaders);

			rowCt++;
		}
		int start = 0, end = startCol;
		for (int row = rowCt; row < rowCt + 15; row++)
			for (int col = start; col < end; col++)
				PoiUtils.createRowEntry(row, col, sheet, "", styleBlankBoring, 30);
	}

	private int writeFeatureEntry(Sheet sheet, int rowCt, FeatureFromFile feature, XSSFCellStyle styleBoring,
			XSSFCellStyle styleInteger, XSSFCellStyle styleIntegerGrey, XSSFCellStyle styleNumeric,
			XSSFCellStyle styleNumericGrey, XSSFCellStyle styleBoringLeft, XSSFCellStyle styleBoringLeftGrey,
			XSSFCellStyle styleBoringBlueGrey, List<XSSFCellStyle> styleList, XSSFCellStyle styleBlankBoring,
			XSSFCellStyle styleShortestNumericGrey, XSSFCellStyle styleShortestNumeric) {

		int i = 0;

		Integer currGrp = feature.getRedundancyGroup();
		Integer prevGrp = lastRedundancyGrp;
		Boolean sameGrp = (currGrp == null || prevGrp == null) ? false : (currGrp.intValue() == prevGrp.intValue());
		if (!sameGrp) {
			usingGrey = (usingGrey == false);
		}
		lastRedundancyGrp = feature.getRedundancyGroup();
		sheet.createRow(rowCt);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getBatchIdx() == null ? "" : String.valueOf(feature.getBatchIdx()),
				(usingGrey ? styleBoringBlueGrey : styleBoring), (usingGrey ? styleBoringBlueGrey : styleBoring),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getRedundancyGroup() == null ? "" : String.valueOf(feature.getRedundancyGroup()),
				(usingGrey ? styleBoringBlueGrey : styleBoring), (usingGrey ? styleBoringBlueGrey : styleBoring),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet, "   " + feature.getName(),
				usingGrey ? styleBoringLeftGrey : styleBoringLeft);

		createAppropriateRowEntry(rowCt, i++, sheet, feature.getMass() == null ? "" : String.valueOf(feature.getMass()),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleList.get(PostProccessConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet, feature.getRT() == null ? "" : String.valueOf(feature.getRT()),
				usingGrey ? styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(PostProccessConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getOldRt() == null ? "" : String.valueOf(feature.getOldRt()),
				usingGrey ? styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(PostProccessConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(PostProccessConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				(feature.getMedianIntensity() == null ? "" : String.valueOf(Math.round(feature.getMedianIntensity()))),
				(usingGrey ? styleIntegerGrey : styleInteger), usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleList.get(PostProccessConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getMassDefectKendrick() == null ? "-" : String.valueOf((feature.getMassDefectKendrick())),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet, feature.getIsotope(), usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet, feature.getOtherGroupIsotope(),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet, feature.getAnnotation(),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet, feature.getOtherGroupAnnotation(),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet,
				StringUtils.isEmptyOrNull(feature.getFurtherAnnotation()) ? feature.getPossibleRedundancies()
						: feature.getFurtherAnnotation() + ", " + feature.getPossibleRedundancies(),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet, feature.getDerivation() == null ? "" : feature.getDerivation(),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getPutativeMolecularMass() == null ? "-" : String.valueOf((feature.getPutativeMolecularMass())),
				usingGrey ? styleNumericGrey : styleNumeric, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				(feature.getMassError() == null ? "-" : String.valueOf(feature.getMassError())),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet,
				(StringUtils.isEmptyOrNull(feature.getMolecularIonNumber()) ? "-" : feature.getMolecularIonNumber()),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet,
				(StringUtils.isEmptyOrNull(feature.getChargeCarrier()) ? "-" : feature.getChargeCarrier()),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		PoiUtils.createRowEntry(rowCt, i++, sheet,
				(StringUtils.isEmptyOrNull(feature.getNeutralMass()) ? "-" : feature.getNeutralMass()),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getBinIndex() == null ? "-" : String.valueOf(feature.getBinIndex()),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getOldCluster() == null ? "_" : String.valueOf(feature.getOldCluster()),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getNewCluster() == null ? "-" : String.valueOf(feature.getNewCluster()),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				(feature.getNewNewCluster() == null ? "-" : String.valueOf(feature.getNewNewCluster())),
				usingGrey ? styleIntegerGrey : styleList.get(PostProccessConstants.STYLE_INTEGER),
				usingGrey ? styleNumericGrey : styleList.get(PostProccessConstants.STYLE_NUMERIC),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		for (int j = 0; j < feature.getAddedColValues().size(); j++) {
			createAppropriateRowEntry(rowCt, i++, sheet,
					feature.getAddedColValues().get(j) == null ? "-" : feature.getAddedColValues().get(j),
					usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
					usingGrey ? styleBoringBlueGrey : styleBoring);
		}
		rowCt++;
		return i;
	}

	private int createIntensitySection(FeatureFromFile feature, Sheet sheet, int startCol,
			List<XSSFCellStyle> styleList, XSSFCellStyle styleBlankBoring, XSSFCellStyle styleBoring,
			XSSFCellStyle styleBoringBlueGrey, XSSFCellStyle styleInteger, XSSFCellStyle styleIntegerGrey,
			XSSFCellStyle styleNumeric, XSSFCellStyle styleNumericGrey, List<String> intensityHeaders) {

		Integer headerIdx = 0;
		String value = null;

		for (int idx = 0; idx < intensityHeaders.size(); idx++) {
			value = feature.getValueForIntensityHeader(intensityHeaders.get(idx), this.getDerivedColNameMapping());

			if (StringUtils.isEmptyOrNull(intensityHeaders.get(idx)))
				PoiUtils.createRowEntry(rowCt, startCol++, sheet, "", styleBlankBoring);
			else if (StringUtils.isEmptyOrNull(value))
				PoiUtils.createRowEntry(rowCt, startCol++, sheet, "", usingGrey ? styleBoringBlueGrey : styleBoring);
			else if (".".equals(value.trim()))
				PoiUtils.createRowEntry(rowCt, startCol++, sheet, value,
						styleList.get(PostProccessConstants.STYLE_BORING_GREY));
			else if (feature.valueForHeaderIsOutlier(intensityHeaders.get(idx), derivedColNameMapping))
				createAppropriateRowEntry(rowCt, startCol++, sheet, String.valueOf(value),
						styleList.get(PostProccessConstants.STYLE_LAVENDER),
						styleList.get(PostProccessConstants.STYLE_NUMERIC_LAVENDER),
						styleList.get(PostProccessConstants.STYLE_NUMERIC_LAVENDER));
			else
				createAppropriateRowEntry(rowCt, startCol++, sheet, String.valueOf(value),
						usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
						usingGrey ? styleBoringBlueGrey : styleBoring);

			headerIdx++;
		}

		for (int i = 0; i < 10; i++)
			PoiUtils.createRowEntry(rowCt, startCol++, sheet, "", styleBlankBoring);

		return startCol;
	}

	private void createHeader(List<String> nonStandardHeadersRead, List<String> intensityHeaders, Sheet sheet,
			XSSFCellStyle styleBoring, XSSFCellStyle styleBlankBoring, Boolean second) {

		styleBoring.setAlignment(HorizontalAlignment.CENTER);

		XSSFCellStyle styleBoringGreen = (XSSFCellStyle) styleBoring.clone();

		Font greenFont = workBook.createFont();
		greenFont.setColor(IndexedColors.YELLOW.getIndex());
		greenFont.setFontName("Courier");

		styleBoringGreen.setFont(greenFont);

		if (!second)
			rowCt = 0;

		int col = 0;

		PoiUtils.createRowEntry(rowCt, col++, sheet, "Batch", styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "Match Group", styleBoring, 30);

		for (int i = 0; i < BatchMatchConstants.REGULAR_OUTPUT_FIXED_COLUMN_LABELS.length; i++) {
			String header = BatchMatchConstants.REGULAR_OUTPUT_FIXED_COLUMN_LABELS[i];
			PoiUtils.createRowEntry(rowCt, col++, sheet, header,
					StringUtils.isEmptyOrNull(header) ? styleBlankBoring : styleBoring, 30);
		}

		for (int i = 0; i < nonStandardHeadersRead.size(); i++) {
			PoiUtils.createRowEntry(rowCt, col++, sheet,
					(nonStandardHeadersRead.get(i).length() > 0 ? nonStandardHeadersRead.get(i).substring(1) : ""),
					StringUtils.isEmptyOrNull(nonStandardHeadersRead.get(i)) ? styleBlankBoring : styleBoring, 30);
		}

		if (intensityHeaders != null && intensityHeaders.size() > 0
				&& !StringUtils.isEmptyOrNull(intensityHeaders.get(0)))
			PoiUtils.createRowEntry(rowCt, col++, sheet, "", styleBlankBoring, 30);

		for (int i = 0; i < intensityHeaders.size(); i++) {
			// if (mergedRSDColLookup != null &&
			// mergedRSDColLookup.containsKey(intensityHeaders.get(i)))
			// PoiUtils.createRowEntry(rowCt, col++, sheet, second ? "" :
			// intensityHeaders.get(i), styleBoringGreen, 30);
			if (!StringUtils.isEmptyOrNull(intensityHeaders.get(i)))
				PoiUtils.createRowEntry(rowCt, col++, sheet, second ? "" : intensityHeaders.get(i), styleBoring, 30);
			else if (i > 0)
				PoiUtils.createRowEntry(rowCt, col++, sheet, "", styleBlankBoring, 30);
		}
		nNonIntensityHeaders = col;

		for (int i = 0; i < 4; i++)
			PoiUtils.createRowEntry(rowCt, col++, sheet, "", styleBlankBoring, 30);
		if (second) {
			Row row = sheet.getRow(rowCt);
			row.setZeroHeight(true);
		}
		rowCt++;
	}

	public Map<String, String> getDerivedColNameMapping() {
		return derivedColNameMapping;
	}

	public Map<String, Double> getFeatureNameToOldRtMap() {
		return featureNameToOldRtMap;
	}

	public void setFeatureNameToOldRtMap(Map<String, Double> map) {
		this.featureNameToOldRtMap = map;
	}

	public void setDerivedColNameMapping(Map<String, String> derivedColNameMapping) {
		this.derivedColNameMapping = derivedColNameMapping;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
