////////////////////////////////////////////////////////
//BatchMatchDataSetSummaryWriter.java
//Written by Jan Wigginton and Bill Duren
//October 2020
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.process.PostProcessDataSet;

public class BatchMatchDataSetSummaryWriter extends BatchMatchFeatureWriter {

	private String sheetName = null;

	public BatchMatchDataSetSummaryWriter(SXSSFWorkbook workBook) {
		super(workBook);
	}

	// .006
	public void writeSummaryToFile(PostProcessDataSet data, List<XSSFCellStyle> styleList) throws Exception {

		if (data == null)
			return;

		Sheet sheet = createEmptySheet(getSheetName() == null ? "All Features" : getSheetName(), workBook);

		XSSFCellStyle styleBoring = grabStyleBoring(workBook);
		XSSFCellStyle styleInteger = grabStyleInteger(workBook);
		XSSFCellStyle styleIntegerGrey = grabStyleInteger(workBook, true);

		XSSFCellStyle styleNumeric = grabStyleNumeric(workBook);
		XSSFCellStyle styleNumericGrey = grabStyleNumeric(workBook, true);
		XSSFCellStyle styleBoringLeft = grabStyleBoringLeft(workBook);
		XSSFCellStyle styleBoringLeftGrey = grabStyleBoringLeftGrey(workBook);

		XSSFCellStyle styleBoringBlueGrey = (XSSFCellStyle)workBook.createCellStyle();
		styleBoringBlueGrey.cloneStyleFrom(styleList.get(BinnerConstants.STYLE_BORING_GREY));
		
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

		sheet.createFreezePane(1, 1);

		rowCt = 0;
		writeHeader(sheet, styleHeader, styleBlankBoring, data.getLastBatchIdx(), false);
		writeData(data, sheet, styleList);
		writeHeader(sheet, styleHeader, styleBlankBoring, data.getLastBatchIdx(), true);

	}

	private void writeData(PostProcessDataSet data, Sheet sheet, List<XSSFCellStyle> styleList) throws Exception {

		XSSFCellStyle styleBoring = grabStyleBoring(workBook);
		XSSFCellStyle styleBlankBoring = this.grabStyleBlankBoring(workBook);

		Integer lastBatchIdx = data.getLastBatchIdx();
		rowCt++;
		Map<String, List<Integer>> matchGroupToBatchIdsMap = data.buildMatchGroupToBatchIdsMap(false);
		Map<String, List<String>> redundancyGroupToFeatureNamesMap = data.buildMatchGroupToFeatureNamesMap(false);
		Map<Integer, Double> matchGroupToRtRangeMap = data.buildMatchGroupToRtRangeMap();

		data.buildAvgRtMassByMatchGrpMap(false);
		Map<Integer, Double> avgRts = data.getAvgRtsByMatchGrp();
		Map<Integer, Double> avgMasses = data.getAvgMassesByMatchGrp();

		int idx = 0, col = 0;
		for (String matchGroupName : matchGroupToBatchIdsMap.keySet()) {

			List<Integer> batchIdsForGroup = matchGroupToBatchIdsMap.get(matchGroupName);

			if (batchIdsForGroup.size() < 1)
				continue;

			Collections.sort(batchIdsForGroup);

			idx = 0;
			col = 0;

			PoiUtils.createRowEntry(rowCt, col++, sheet, matchGroupName, styleBoring, 30);

			Integer ct = 0, overallCt = 0;
			Integer nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;

			for (int i = 1; i <= lastBatchIdx; i++) {

				ct = nextId.equals(i) ? 1 : 0;
				if (ct > 0)
					nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;

				while (nextId.equals(i)) {
					ct++;
					nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;
				}

				createAppropriateRowEntry(rowCt, col++, sheet, ct > 0 ? ct.toString() : "-",
						(ct > 1 ? styleList.get(BinnerConstants.STYLE_ORANGE)
								: styleList.get(BinnerConstants.STYLE_YELLOW)),
						(ct > 1 ? styleList.get(BinnerConstants.STYLE_ORANGE)
								: styleList.get(BinnerConstants.STYLE_YELLOW)),
						styleBoring);

				if (ct > 0)
					overallCt++;
			}
			createAppropriateRowEntry(rowCt, col++, sheet, overallCt.toString(), styleBoring, styleBoring, styleBoring);

			Integer featureCt = redundancyGroupToFeatureNamesMap.get(matchGroupName).size();
			createAppropriateRowEntry(rowCt, col++, sheet, featureCt.toString(), styleBoring, styleBoring, styleBoring);

			Integer redInt = null;
			Double range = null, rt = null, mass = null;
			try {
				redInt = Integer.parseInt(matchGroupName);
				rt = avgRts.get(redInt);
				mass = avgMasses.get(redInt);
				range = matchGroupToRtRangeMap.get(redInt);
			} catch (Exception e) {
				redInt = null;
				rt = null;
				mass = null;
			}
			createAppropriateRowEntry(rowCt, col++, sheet, rt == null ? "" : rt.toString(),
					styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
					styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
					styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER));
			createAppropriateRowEntry(rowCt, col++, sheet, mass == null ? "" : mass.toString(),
					styleList.get(BinnerConstants.STYLE_NUMERIC),
					styleList.get(BinnerConstants.STYLE_NUMERIC),
					styleList.get(BinnerConstants.STYLE_NUMERIC));

			createAppropriateRowEntry(rowCt, col++, sheet, range == null ? "" : range.toString(),
					styleList.get(BinnerConstants.STYLE_NUMERIC),
					styleList.get(BinnerConstants.STYLE_NUMERIC),
					styleList.get(BinnerConstants.STYLE_NUMERIC));

			StringBuilder sb = new StringBuilder();
			sb.append("   ");
			for (int i = 0; i < redundancyGroupToFeatureNamesMap.get(matchGroupName).size(); i++)
				sb.append(redundancyGroupToFeatureNamesMap.get(matchGroupName).get(i)
						+ (i < redundancyGroupToFeatureNamesMap.get(matchGroupName).size() - 1 ? "," : ""));

			PoiUtils.createRowEntry(rowCt, col++, sheet, sb.toString(),
					styleList.get(BinnerConstants.STYLE_BORING_LEFT), 30);

			rowCt++;
		}
	}

	private void writeHeader(Sheet sheet, XSSFCellStyle styleBoring, XSSFCellStyle styleBlankBoring, Integer nBatches,
			Boolean second) {

		styleBoring.setAlignment(HorizontalAlignment.CENTER);

		int col = 0;

		PoiUtils.createRowEntry(rowCt, col++, sheet, "MATCH GRP", styleBoring, 30);
		for (int i = 1; i <= nBatches; i++)
			PoiUtils.createRowEntry(rowCt, col++, sheet, "BATCH " + i, styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "# BATCHES MATCHED", styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "# FEATURES", styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "AVG RT", styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "AVG MASS", styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "RT RANGE", styleBoring, 30);
		PoiUtils.createRowEntry(rowCt, col++, sheet, "FEATURE NAMES", styleBoring, 30);

		if (second) {

			// trackColumnForAutoSizing
			for (int i = 1; i < col; i++) {
				int defaultCol = sheet.getColumnWidth(i);
				sheet.setColumnWidth(i, i < 10 ? (int) (1.9 * defaultCol) : 30000);
			}

			Row row = sheet.getRow(rowCt);
			row.setZeroHeight(true);
		}
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
