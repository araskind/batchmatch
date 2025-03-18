////////////////////////////////////////////////////
// BinnerSpreadSheetWriter.java
// Created February 2017
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

import edu.umich.mrc2.batchmatch.utils.BinnerNumUtils;
import edu.umich.mrc2.batchmatch.utils.StringUtils;

public class BinnerSpreadSheetWriter extends SpreadSheetWriter implements Serializable {
	private static final long serialVersionUID = -6273318608527606812L;

	protected static final Integer STYLE_INTEGER = 0;
	protected static final Integer STYLE_NUMERIC = 1;

	protected static final Integer[] CUSTOM_WIDTHS = { 256, // I
			30 * 256, // F
			18 * 256, // M
			15 * 256, // RT
			15 * 256, // MI
			15 * 256, // KMD
			40 * 256, // Iso
			256, // IsoInGrp
			40 * 256, // Annot
			256, // AnnotInGrp
			40 * 256, // Deriv
			15 * 256, // Mass Error
			256, // Molecular Ion Number
			256, // Charge Carrier
			256, // Neutral Annot
			256, // Ref Mass
			10 * 256, // Bin
			10 * 256, // Old
			10 * 256, // New
			10 * 256, // NewNew
			2 * 256 };

	public BinnerSpreadSheetWriter() {
		super();
	}

	/*
	 * protected void sizeColumns(Sheet sheet, BinnerOutput output, AnalysisData
	 * analysisData, int dataWidth, int initialDataWidth) { int nMaxColsNeeded = 0;
	 * int nAddedCols = -1; for (BinnerGroup group : output.getGroups()) { if
	 * (nAddedCols == -1 && group.getFeatureIndexList().size() > 0) nAddedCols =
	 * analysisData.getNonMissingFeaturesInOriginalOrder().get(group.
	 * getFeatureIndexList().get(0)).getAddedColValues().size();
	 * 
	 * nMaxColsNeeded = Math.max(group.getFeatureIndexList().size(),
	 * nMaxColsNeeded); } nMaxColsNeeded += (nAddedCols +
	 * BinnerConstants.OUTPUT_FIXED_COLUMN_LABELS.length + 10);
	 * 
	 * for (int i = BinnerConstants.OUTPUT_FIXED_COLUMN_LABELS.length; i <
	 * nMaxColsNeeded; i++) sheet.setColumnWidth(i, i >= output.getDataStartCol() ?
	 * dataWidth * 256 : initialDataWidth * 256);
	 * 
	 * sheet.createFreezePane(4, 1); }
	 */

	public List<XSSFCellStyle> grabStyleListPalette(Workbook workBook, String hexColor, Boolean wideFormat)
			throws Exception {
		List<XSSFCellStyle> styleList = new ArrayList<XSSFCellStyle>();
		XSSFCellStyle styleInteger = (XSSFCellStyle) workBook.createCellStyle();
		styleInteger.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFColor color = ColorUtils.grabFromHtml(hexColor);
		styleInteger.setFillForegroundColor(color);
		XSSFDataFormat format = (XSSFDataFormat) workBook.createDataFormat();
		styleInteger.setDataFormat(format.getFormat("0"));
		XSSFCellStyle styleNumeric = (XSSFCellStyle) workBook.createCellStyle();
		styleNumeric.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleNumeric.setFillForegroundColor(color);
		if (wideFormat)
			styleNumeric.setDataFormat(format.getFormat("0.000000"));
		else
			styleNumeric.setDataFormat(format.getFormat("0.00"));
		styleList.add(styleInteger);
		styleList.add(styleNumeric);

		return styleList;
	}

	public Map<Integer, List<XSSFCellStyle>> initializeStyleMap(Workbook workBook, Palette palette, Boolean wideDoubles)
			throws Exception {
		Map<Integer, List<XSSFCellStyle>> styleMap = new HashMap<Integer, List<XSSFCellStyle>>();

		int i = 0;
		for (PaletteRow row : palette.getRows())
			for (String colorVal : row.getValues())
				if (!StringUtils.isEmptyOrNull(colorVal))
					styleMap.put(i++, grabStyleListPalette(workBook, colorVal, wideDoubles));
		return styleMap;
	}

	protected void createAppropriateRowEntry(int row, int col, Sheet sheet, String value, XSSFCellStyle styleInteger,
			XSSFCellStyle styleNumeric, XSSFCellStyle styleBoring) {
		if (BinnerNumUtils.isParsableAsDouble(value)) {
			double x = Double.valueOf(value);
			Boolean isTrueInt = ((x == (int) x) && value.indexOf('.') == -1);
			PoiUtils.createNumericRowEntry(row, col, sheet, value, isTrueInt ? styleInteger : styleNumeric);
		} else {
			if (value.indexOf(',') != -1) {

				String stripValue = StringUtils.removeCommas(value);
				if (BinnerNumUtils.isParsableAsDouble(stripValue)) {
					double x = Double.valueOf(stripValue);
					Boolean isTrueInt = ((x == (int) x) && stripValue.indexOf('.') == -1);
					PoiUtils.createNumericRowEntry(row, col, sheet, stripValue,
							isTrueInt ? styleInteger : styleNumeric);
				}
			} else {
				PoiUtils.createRowEntry(row, col, sheet, value, styleBoring);
			}
		}
	}
}

/*
 * protected List<Integer> getFeatureIndexListSortedByFactor(AnalysisData
 * analysisData, BinnerGroup group, Integer sortType) { Double [] RTList = new
 * Double[group.getFeatureIndexList().size()]; for (int i = 0; i <
 * group.getFeatureIndexList().size(); i++) { Feature feature =
 * analysisData.getNonMissingFeaturesInOriginalOrder().
 * get(group.getFeatureIndexList().get(i));
 * 
 * if (sortType.equals(BinnerConstants.SORT_TYPE_RT)) RTList[i] =
 * feature.getRT(); else if (sortType.equals(BinnerConstants.SORT_TYPE_MASS))
 * RTList[i] = feature.getMass(); else RTList[i] = feature.getMedianIntensity();
 * } List<IndexListItem<Double>> sortedRTList = ListUtils.sortedList(RTList);
 * List<Integer> featureIndexListByRT = new ArrayList<Integer>(); for (int i =
 * 0; i < group.getFeatureIndexList().size(); i++)
 * featureIndexListByRT.add(group.getFeatureIndexList().get(sortedRTList.get(i).
 * getIndex())); return featureIndexListByRT; }
 * 
 * //15 * 256
 * 
 * protected void initializeColWidths(AnalysisData analysisData) { int
 * longestName = -1, longestAnnotation = -1, longestDerivation = -1,
 * longestIsotope = -1; for (int i = 0; i <
 * analysisData.getNonMissingFeaturesInOriginalOrder().size(); i++) { Feature
 * feature = analysisData.getNonMissingFeaturesInOriginalOrder().get(i);
 * longestName = Math.max(longestName, feature.getName().length());
 * longestAnnotation = Math.max(longestAnnotation,
 * feature.getAdductOrNL().length()); longestDerivation =
 * Math.max(longestDerivation, feature.getDerivation().length()); longestIsotope
 * = Math.max(longestIsotope, feature.getIsotope().length()); }
 * 
 * CUSTOM_WIDTHS[BinnerConstants.FEATURE_COL] =
 * Math.max((BinnerConstants.OUTPUT_FIXED_COLUMN_LABELS[BinnerConstants.
 * FEATURE_COL]. length() + 2) * 256, longestName * 256);
 * CUSTOM_WIDTHS[BinnerConstants.ISOTOPE_COL] =
 * Math.max((BinnerConstants.OUTPUT_FIXED_COLUMN_LABELS[BinnerConstants.
 * ISOTOPE_COL]. length() + 1) * 256, longestIsotope * 256);
 * CUSTOM_WIDTHS[BinnerConstants.ANNOTATION_COL] =
 * Math.max((BinnerConstants.OUTPUT_FIXED_COLUMN_LABELS[BinnerConstants.
 * ANNOTATION_COL]. length() + 1) * 256, longestAnnotation * 256);
 * CUSTOM_WIDTHS[BinnerConstants.DERIVATION_COL] =
 * Math.max((BinnerConstants.OUTPUT_FIXED_COLUMN_LABELS[BinnerConstants.
 * DERIVATION_COL]. length() + 1) * 256, longestDerivation * 256); } }
 */
