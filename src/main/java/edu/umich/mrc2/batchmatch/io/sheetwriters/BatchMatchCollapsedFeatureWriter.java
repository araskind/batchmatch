////////////////////////////////////////////////////
// BatchMatchCollapsedFeatureWriter.java
// Written by Jan Wigginton, September 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByRedGrpMassAndRtComparator;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.process.PostProcessDataSet;
import edu.umich.mrc2.batchmatch.utils.ListUtils;
import edu.umich.mrc2.batchmatch.utils.StringUtils;

public class BatchMatchCollapsedFeatureWriter extends BatchMatchFeatureWriter {

	private static final long serialVersionUID = -2361568255632647473L;

	private boolean usingGrey = true;
	private int nNonIntensityHeaders = 0;
	private Map<String, String> derivedColNameMapping;
	private Integer lastMatchGrp = -1;
	private String sheetName = null;

	public BatchMatchCollapsedFeatureWriter(SXSSFWorkbook workBook) {
		this(workBook, null);
	}

	public BatchMatchCollapsedFeatureWriter(SXSSFWorkbook workBook, Map<String, String> derivedColNameMapping) {
		super(workBook);
		this.derivedColNameMapping = derivedColNameMapping;
	}

	public void writeCollapsedFeatureSheet(PostProcessDataSet data, int maxBatch, List<String> nonStandardHeaders,
			List<String> extraLibFileHeaders, List<String> intensityHeaders, List<XSSFCellStyle> styleList,
			boolean onlyNonRedudant) throws Exception {

		System.out.println();
		List<FeatureFromFile> features = data.getFeatures();

		Sheet sheet = createEmptySheet(getSheetName() == null ? "Details" : getSheetName(), workBook);

		XSSFCellStyle styleBoring = grabStyleBoring(workBook);
		XSSFCellStyle styleInteger = grabStyleInteger(workBook);
		XSSFCellStyle styleIntegerGrey = grabStyleInteger(workBook, true);

		XSSFCellStyle styleNumeric = grabStyleNumeric(workBook);
		XSSFCellStyle styleNumericGrey = grabStyleNumeric(workBook, true);
		XSSFCellStyle styleBoringLeft = grabStyleBoringLeft(workBook);
		XSSFCellStyle styleBoringLeftGrey = grabStyleBoringLeftGrey(workBook);
		
		//	XSSFCellStyle styleToClone = styleList.get(BinnerConstants.STYLE_BORING_GREY);
		//	XSSFCellStyle styleBoringBlueGrey = (XSSFCellStyle) styleToClone.clone();
		XSSFCellStyle styleBoringBlueGrey = (XSSFCellStyle) workBook.createCellStyle();
		styleBoringBlueGrey.cloneStyleFrom(styleList.get(BinnerConstants.STYLE_BORING_GREY));
		
		styleBoringBlueGrey.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));
		styleBoringBlueGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFCellStyle styleBlankBoring = this.grabStyleBlankBoring(workBook);

		styleBoringLeft.setIndention((short) 1);
		styleBoringLeftGrey.setIndention((short) 1);

		XSSFColor colorBlack = ColorUtils.grabRGBColor(255, 255, 255);
		Font headerFont = workBook.createFont();
		headerFont.setFontName("Courier");
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		XSSFCellStyle styleHeader = this.grabStyleBlue(workBook, false);

		styleHeader.setAlignment(HorizontalAlignment.CENTER);
		styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
		styleHeader.setWrapText(true);


		styleHeader.setIndention((short) 2);
		styleHeader.setBorderColor(BorderSide.LEFT, colorBlack);
		styleHeader.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleHeader.setBorderLeft(BorderStyle.THIN);
		styleHeader.setBorderRight(BorderStyle.THIN);
		styleHeader.setFillForegroundColor(ColorUtils.grabFromHtml("#203764"));
		styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleHeader.setFont(headerFont);

		XSSFCellStyle styleBoringUnderlinedEntry = null;
		try {
			styleBoringUnderlinedEntry = grabStyleBlankBoring(workBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoringUnderlinedEntry.setAlignment(HorizontalAlignment.LEFT);
		styleBoringUnderlinedEntry.setBorderColor(BorderSide.LEFT, colorBlack);
		styleBoringUnderlinedEntry.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleBoringUnderlinedEntry.setBorderLeft(BorderStyle.THIN);
		styleBoringUnderlinedEntry.setBorderRight(BorderStyle.THIN);
		styleBoringUnderlinedEntry.getFont().setUnderline(FontUnderline.SINGLE);

		Font font = workBook.createFont();
		font.setFontName("Courier");
		styleNumeric.setFont(font);
		styleBoring.setFont(font);
		styleInteger.setFont(font);
		styleNumericGrey.setFont(font);
		styleBoringLeft.setFont(font);
		styleIntegerGrey.setFont(font);

		sheet.createFreezePane(8, 1);

		Collections.sort(features, new FeatureByRedGrpMassAndRtComparator());

		// System.out.println("Max batch is " + maxBatch);

		createHeader(nonStandardHeaders, intensityHeaders, sheet, styleHeader, styleBlankBoring, false, maxBatch);
		createFeatureList(sheet, features, intensityHeaders, styleBoring, styleInteger, styleIntegerGrey, styleNumeric,
				styleNumericGrey, styleBoringLeft, styleBoringLeftGrey, styleBoringBlueGrey, styleList,
				styleBlankBoring, styleBoringUnderlinedEntry, onlyNonRedudant);
		// Force column autosize to accommodate header for non-intensity cols
		createHeader(nonStandardHeaders, intensityHeaders, sheet, styleHeader, styleBlankBoring, true, maxBatch);

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

	private void createHeader(List<String> nonStandardHeadersRead, List<String> intensityHeaders, Sheet sheet,
			XSSFCellStyle styleBoring, XSSFCellStyle styleBlankBoring, boolean second, Integer maxBatch) {

		styleBoring.setAlignment(HorizontalAlignment.CENTER);	
		XSSFCellStyle styleBoringGreen = (XSSFCellStyle) workBook.createCellStyle();
		styleBoringGreen.cloneStyleFrom(styleBoring);
		Font greenFont = workBook.createFont();
		greenFont.setColor(IndexedColors.YELLOW.getIndex());
		greenFont.setFontName("Courier");
		styleBoringGreen.setFont(greenFont);

		if (!second)
			rowCt = 0;

		int col = 0;

		// Skip batch col for collapsed data
		for (int i = 1; i < BatchMatchConstants.COLLAPSE_OUTPUT_FIXED_COLUMN_LABELS.length; i++) {
			String header = BatchMatchConstants.COLLAPSE_OUTPUT_FIXED_COLUMN_LABELS[i];
			PoiUtils.createRowEntry(rowCt, col++, sheet, header,
					StringUtils.isEmptyOrNull(header) ? styleBlankBoring : styleBoring, 30);
		}

		for (int i = 1; i < maxBatch + 1; i++) {
			PoiUtils.createRowEntry(rowCt, col++, sheet, "Batch " + i,
					StringUtils.isEmptyOrNull("B") ? styleBlankBoring : styleBoring, 30);
		}

		if (intensityHeaders != null && intensityHeaders.size() > 0
				&& !StringUtils.isEmptyOrNull(intensityHeaders.get(0)))
			PoiUtils.createRowEntry(rowCt, col++, sheet, "", styleBlankBoring, 30);

		for (int i = 0; i < intensityHeaders.size(); i++) {
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

	private void createFeatureList(Sheet sheet, List<FeatureFromFile> features, List<String> intensityHeaders,
			XSSFCellStyle styleBoring, XSSFCellStyle styleInteger, XSSFCellStyle styleIntegerGrey,
			XSSFCellStyle styleNumeric, XSSFCellStyle styleNumericGrey, XSSFCellStyle styleBoringLeft,
			XSSFCellStyle styleBoringLeftGrey, XSSFCellStyle styleBoringBlueGrey, List<XSSFCellStyle> styleList,
			XSSFCellStyle styleBlankBoring, XSSFCellStyle styleBoringUnderlinedEntry, boolean onlyNonRedundant) {

		XSSFCellStyle styleNumericShortestGrey = styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTEST_GREY);
		XSSFCellStyle styleNumericShortest = styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTEST);

		Font font = workBook.createFont();
		font.setFontName("Courier");
		styleNumericShortest.setFont(font);
		styleNumericShortestGrey.setFont(font);
		
		Font redFont = workBook.createFont();
		redFont.setColor(IndexedColors.RED.getIndex());
		redFont.setFontName("Courier");
		
		Font greenFont = workBook.createFont();
		greenFont.setColor(IndexedColors.YELLOW.getIndex());
		greenFont.setFontName("Courier");
		
		XSSFCellStyle styleBoringRed = (XSSFCellStyle)workBook.createCellStyle();
		styleBoringRed.cloneStyleFrom(styleBoring);
		styleBoringRed.setFont(redFont);
		
		XSSFCellStyle styleNumericRed = (XSSFCellStyle)workBook.createCellStyle();
		styleNumericRed.cloneStyleFrom(styleNumeric);
		styleNumericRed.setFont(redFont);
				
		XSSFCellStyle styleNumericGreyRed = (XSSFCellStyle)workBook.createCellStyle();
		styleNumericGreyRed.cloneStyleFrom(styleNumericGrey);
		styleNumericGreyRed.setFont(redFont);
		
		XSSFCellStyle styleBoringLeftRed = (XSSFCellStyle)workBook.createCellStyle();
		styleBoringLeftRed.cloneStyleFrom(styleBoringLeft);
		styleBoringLeftRed.setFont(redFont);		
		
		XSSFCellStyle styleBoringLeftGreyRed = (XSSFCellStyle)workBook.createCellStyle(); 
		styleBoringLeftGreyRed.cloneStyleFrom(styleBoringLeftGrey);
		styleBoringLeftGreyRed.setFont(redFont);
		
		XSSFCellStyle styleBoringBlueGreyRed = (XSSFCellStyle)workBook.createCellStyle(); 
		styleBoringBlueGreyRed.cloneStyleFrom(styleBoringBlueGrey);
		styleBoringBlueGreyRed.setFont(redFont);
		
		XSSFCellStyle styleIntegerRed = (XSSFCellStyle)workBook.createCellStyle();
		styleIntegerRed.cloneStyleFrom(styleInteger);
		styleIntegerRed.setFont(redFont);
		
		XSSFCellStyle styleIntegerGreyRed = (XSSFCellStyle)workBook.createCellStyle();
		styleIntegerGreyRed.cloneStyleFrom(styleIntegerGrey);
		styleIntegerGreyRed.setFont(redFont);
		
		XSSFCellStyle styleNumericShortestGreyRed = (XSSFCellStyle)workBook.createCellStyle();
		styleNumericShortestGreyRed.cloneStyleFrom(styleNumericShortestGrey);
		styleNumericShortestGreyRed.setFont(redFont);
		
		XSSFCellStyle styleNumericShortestRed = (XSSFCellStyle)workBook.createCellStyle();
		styleNumericShortestRed.cloneStyleFrom(styleNumericShortest);
		styleNumericShortestRed.setFont(redFont);

		rowCt = 1;
		int startCol = 0;
		for (int j = 0; j < features.size(); j++) {

			FeatureFromFile feature = features.get(j);
			FeatureFromFile nextFeature = j < features.size() - 1 ? features.get(j + 1) : null;

			if (feature.getRedundancyGroup() != null)
				feature.setFlaggedAsDuplicate(true);

			if (onlyNonRedundant
					&& (feature.getnMatchFeatureReplicates() == null || feature.getnMatchReplicates() == null
							|| feature.getnMatchFeatureReplicates() > feature.getnMatchReplicates()))
				continue;

			if (!onlyNonRedundant && feature.getnMatchFeatureReplicates() <= feature.getnMatchReplicates())

				continue;

			startCol = writeFeatureEntry(sheet, rowCt, feature, nextFeature,
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

	private int writeFeatureEntry(Sheet sheet, int rowCt, FeatureFromFile feature, FeatureFromFile nextFeature,
			XSSFCellStyle styleBoring, XSSFCellStyle styleInteger, XSSFCellStyle styleIntegerGrey,
			XSSFCellStyle styleNumeric, XSSFCellStyle styleNumericGrey, XSSFCellStyle styleBoringLeft,
			XSSFCellStyle styleBoringLeftGrey, XSSFCellStyle styleBoringBlueGrey, List<XSSFCellStyle> styleList,
			XSSFCellStyle styleBlankBoring, XSSFCellStyle styleShortestNumericGrey,
			XSSFCellStyle styleShortestNumeric) {

		int i = 0;

		Integer currGrp = feature.getRedundancyGroup();
		Integer prevGrp = lastMatchGrp;
		boolean sameGrp = (currGrp == null || prevGrp == null) ? false : (currGrp.intValue() == prevGrp.intValue());
		if (!sameGrp) {
			usingGrey = (usingGrey == false);
		}
		lastMatchGrp = feature.getRedundancyGroup();
		sheet.createRow(rowCt);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getRedundancyGroup() == null ? "" : String.valueOf(feature.getRedundancyGroup()),
				(usingGrey ? styleBoringBlueGrey : styleBoring), (usingGrey ? styleBoringBlueGrey : styleBoring),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getnMatchReplicates() == null ? "" : String.valueOf(feature.getnMatchReplicates()),
				(usingGrey ? styleBoringBlueGrey : styleBoring), (usingGrey ? styleBoringBlueGrey : styleBoring),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getnMatchFeatureReplicates() == null ? ""
						: String.valueOf(feature.getnMatchFeatureReplicates()),
				(usingGrey ? styleBoringBlueGrey : styleBoring), (usingGrey ? styleBoringBlueGrey : styleBoring),
				usingGrey ? styleBoringBlueGrey : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet, feature.getRsd() == null ? "-" : String.valueOf(feature.getRsd()),
				usingGrey ? styleShortestNumericGrey : styleShortestNumeric,
				usingGrey ? styleShortestNumericGrey : styleShortestNumeric,
				usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getPctMissing() == null ? "" : String.valueOf(feature.getPctMissing()),
				usingGrey ? styleShortestNumericGrey : styleShortestNumeric,
				usingGrey ? styleShortestNumericGrey : styleShortestNumeric,
				usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);

		List<Integer> batchesForRSD = ListUtils.makeListFromObjectCollection(feature.getBatchwiseRSDs().keySet());
		Collections.sort(batchesForRSD);

		// System.out.print(String.format("%5.4f ",
		// feature.getBatchwiseRSDs().get(batchesForRSD.get(b))));
		// System.out.println();

		PoiUtils.createRowEntry(rowCt, i++, sheet, "   " + feature.getName(),
				usingGrey ? styleBoringLeftGrey : styleBoringLeft);

		createAppropriateRowEntry(rowCt, i++, sheet, feature.getMass() == null ? "" : String.valueOf(feature.getMass()),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
				usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet, feature.getRT() == null ? "" : String.valueOf(feature.getRT()),
				usingGrey ? styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getOldRt() == null ? "" : String.valueOf(feature.getOldRt()),
				usingGrey ? styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER_GREY)
						: styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
				usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);

		createAppropriateRowEntry(rowCt, i++, sheet,
				feature.getMedianIntensity() == null ? "" : String.valueOf(feature.getMedianIntensity()),
				usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleIntegerGrey : styleInteger,
				usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);

		int lastBatch = 0;
		for (int b = 0; b < batchesForRSD.size(); b++) {
			if (batchesForRSD.get(b) - lastBatch > 1 || feature.getBatchwiseRSDs() == null || feature.getRsd() == null
					|| feature.getRsd() == 0.0)
				PoiUtils.createRowEntry(rowCt, i++, sheet, "", usingGrey ? styleBoringLeftGrey : styleBoringLeft);
			else
				createAppropriateRowEntry(rowCt, i++, sheet,
						feature.getBatchwiseRSDs().get(batchesForRSD.get(b)) == null ? ""
								: String.valueOf(
										feature.getBatchwiseRSDs().get(batchesForRSD.get(b)) / feature.getRsd()),
						usingGrey ? styleIntegerGrey : styleInteger, usingGrey ? styleNumericGrey : styleNumeric,
						usingGrey ? styleList.get(BinnerConstants.STYLE_BORING_GREY) : styleBoring);
			lastBatch = batchesForRSD.get(b);
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
						styleList.get(BinnerConstants.STYLE_BORING_GREY));
			else if (feature.valueForHeaderIsOutlier(intensityHeaders.get(idx), derivedColNameMapping))
				createAppropriateRowEntry(rowCt, startCol++, sheet, String.valueOf(value),
						styleList.get(BinnerConstants.STYLE_LAVENDER),
						styleList.get(BinnerConstants.STYLE_NUMERIC_LAVENDER),
						styleList.get(BinnerConstants.STYLE_NUMERIC_LAVENDER));
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

	public Map<String, String> getDerivedColNameMapping() {
		return derivedColNameMapping;
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
