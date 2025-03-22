package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import edu.umich.med.mrc2.batchmatch.data.orig.Feature;

public class BatchMatchFeatureWriter extends BinnerSpreadSheetWriter implements Serializable {
	protected static final long serialVersionUID = -118116585712037767L;
	protected SXSSFWorkbook workBook;
	protected int rowCt;
	// protected Boolean writeIntensitiesFromMemory = true;

	public BatchMatchFeatureWriter(SXSSFWorkbook workBook) {
		super();
		this.workBook = workBook;
	}

	/*
	 * public void createFeatureTab(AnalysisData analysisData, BinnerOutput output,
	 * List<XSSFCellStyle> styleList, Boolean onlyRebin ) throws Exception {
	 * 
	 * Sheet sheet = createEmptySheet(output.getTabName(), workBook);
	 * sizeColumns(sheet, output, analysisData, 18, 18); //reeze
	 * sheet.setColumnWidth(0, CUSTOM_WIDTHS[0]); sheet.setColumnWidth(5, 0);
	 * //CUSTOM_WIDTHS[5]); sheet.setColumnWidth(6, 0); /// CUSTOM_WIDTHS[6]);
	 * sheet.setColumnWidth(7, 0); //CUSTOM_WIDTHS[7]);
	 * 
	 * XSSFCellStyle styleBoring = grabStyleBoring(workBook); XSSFCellStyle
	 * styleInteger = grabStyleInteger(workBook); XSSFCellStyle styleNumeric =
	 * grabStyleNumeric(workBook); XSSFCellStyle styleBoringLeft =
	 * grabStyleBoringLeft(workBook);
	 * 
	 * Font font = workBook.createFont(); font.setFontName("Courier");
	 * 
	 * styleNumeric.setFont(font); styleBoringLeft.setFont(font);
	 * styleInteger.setFont(font);
	 * 
	 * createHeader(output, sheet); for (BinnerGroup group : output.getGroups())
	 * createFeatureList(sheet, analysisData, group, styleBoring, styleInteger,
	 * styleNumeric, styleBoringLeft, styleList, onlyRebin); }
	 */

	/*
	 * public void createFeatureList(Sheet sheet, AnalysisData analysisData,
	 * BinnerGroup group, XSSFCellStyle styleBoring, XSSFCellStyle styleInteger,
	 * XSSFCellStyle styleNumeric, XSSFCellStyle styleBoringLeft,
	 * List<XSSFCellStyle> styleList, Boolean onlyRebin) { List<Integer>
	 * featureIndices = getFeatureIndexListSortedByFactor(analysisData, group,
	 * BinnerConstants.SORT_TYPE_INTENSITY);
	 * 
	 * 
	 * for (int j = featureIndices.size() - 1; j >= 0; j--) { Feature feature =
	 * analysisData.getNonMissingFeaturesInOriginalOrder().get(featureIndices.get(j)
	 * );
	 * 
	 * if (!StringUtils.isEmptyOrNull(feature.getAdductOrNL())) continue;
	 * 
	 * if (!StringUtils.isEmptyOrNull(feature.getIsotope())) continue;
	 * 
	 * writeFeatureEntry(sheet, rowCt, feature, styleBoring, styleInteger,
	 * styleNumeric, styleBoringLeft, styleList, onlyRebin); } }
	 */

	protected int writeFeatureEntry(Sheet sheet, int rowCt, Feature feature, XSSFCellStyle styleBoring,
			XSSFCellStyle styleInteger, XSSFCellStyle styleNumeric, XSSFCellStyle styleBoringLeft,
			List<XSSFCellStyle> styleList, Boolean onlyRebin) {
		{
			sheet.createRow(rowCt);

			int i = 0;
			return i;
		}
		/*
		 * PoiUtils.createRowEntry(rowCt, i++, sheet, feature.getName(),
		 * styleBoringLeft); createAppropriateRowEntry(rowCt, i++, sheet,
		 * String.valueOf(feature.getMass()), styleInteger, styleNumeric, styleBoring);
		 * createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(feature.getRT()),
		 * styleList.get(BinnerConstants.STYLE_INTEGER),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_SHORTER),
		 * styleList.get(BinnerConstants.STYLE_BORING));
		 * 
		 * createAppropriateRowEntry(rowCt, i++, sheet, (feature.getMedianIntensity() ==
		 * null ? "" : String.valueOf(Math.round(feature.getMedianIntensity()))),
		 * styleInteger, styleNumeric, styleBoring); createAppropriateRowEntry(rowCt,
		 * i++, sheet, String.valueOf((feature.getMassDefectKendrick())), styleInteger,
		 * styleNumeric, styleBoring); PoiUtils.createRowEntry(rowCt, i++, sheet,
		 * feature.getIsotope(), styleBoring); createAppropriateRowEntry(rowCt, i++,
		 * sheet, String.valueOf((feature.getAdductOrNL())), styleInteger, styleNumeric,
		 * styleBoring); createAppropriateRowEntry(rowCt, i++, sheet,
		 * String.valueOf((feature.getDerivation())), styleInteger, styleNumeric,
		 * styleBoring);
		 * 
		 * createAppropriateRowEntry(rowCt, i++, sheet,
		 * feature.getPutativeMolecularMass() == null ? "-" :
		 * String.valueOf((feature.getPutativeMolecularMass())), styleInteger,
		 * styleNumeric, styleBoring);
		 * 
		 * createAppropriateRowEntry(rowCt, i++, sheet, (feature.getMassError() == null
		 * ? "-" : String.valueOf(feature.getMassError())), styleInteger,styleNumeric,
		 * styleBoring);
		 * 
		 * //System.out.println("Feature is " + (feature == null ? " null" :
		 * " not null")); //System.out.println("Bin is " + (feature != null ?
		 * feature.getBinIndex() : "1")); PoiUtils.createRowEntry(rowCt, i++, sheet,
		 * (StringUtils.isEmptyOrNull(feature.getMolecularIonNumber()) ? "-"
		 * :feature.getMolecularIonNumber()), styleBoring);
		 * PoiUtils.createRowEntry(rowCt, i++, sheet,
		 * (StringUtils.isEmptyOrNull(feature.getChargeCarrier()) ? "-"
		 * :feature.getChargeCarrier()), styleBoring); PoiUtils.createRowEntry(rowCt,
		 * i++, sheet, (StringUtils.isEmptyOrNull(feature.getNeutralMass()) ? "-"
		 * :feature.getNeutralMass()), styleBoring);
		 * 
		 * feature.setBinIndex(1); createAppropriateRowEntry(rowCt, i++, sheet,
		 * String.valueOf(feature.getBinIndex() + 1), styleInteger, styleNumeric,
		 * styleBoring); createAppropriateRowEntry(rowCt, i++, sheet,
		 * String.valueOf(feature.getOldCluster()), styleInteger, styleNumeric,
		 * styleBoring); createAppropriateRowEntry(rowCt, i++, sheet,
		 * String.valueOf(feature.getNewCluster()), styleInteger, styleNumeric,
		 * styleBoring);
		 * 
		 * createAppropriateRowEntry(rowCt, i++, sheet, (onlyRebin ? "-" :
		 * String.valueOf(feature.getNewNewCluster())),
		 * styleList.get(BinnerConstants.STYLE_INTEGER),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC),
		 * styleList.get(BinnerConstants.STYLE_BORING));
		 * 
		 * // createAppropriateRowEntry(rowCt, i++, sheet, "",
		 * styleList.get(BinnerConstants.STYLE_INTEGER), //
		 * styleList.get(BinnerConstants.STYLE_NUMERIC), //
		 * styleList.get(BinnerConstants.STYLE_BORING));
		 * 
		 * if (writeIntensitiesFromMemory) for (String value :
		 * feature.getAddedColValues()) createAppropriateRowEntry(rowCt, i++, sheet,
		 * value, styleInteger, styleNumeric, styleBoring);
		 * 
		 * if (writeIntensitiesFromMemory) createIntensitySection2(feature, sheet, i,
		 * styleList); rowCt++; return i; } }
		 * 
		 * 
		 * protected void createIntensitySection2(Feature feature, Sheet sheet, int i,
		 * List<XSSFCellStyle> styleList) { i++; Map<Integer, Double> outlierMap =
		 * feature.getOutlierMap();
		 * 
		 * Boolean isAdjusted = false;
		 * //Arrays.asList(BinnerConstants.ADJ_OUTPUTS).contains(originalIdx); double []
		 * valueList = feature.getUnadjustedIntensityListWithOutliers();
		 * //getValueList(originalIdx, feature); Double featureMedIntensity =
		 * feature.getMedianIntensity(); Double logFeatureMedIntensity =
		 * Math.log1p(featureMedIntensity);
		 * 
		 * Integer sampleIdx = 0; for (Double dblValue : valueList) { if
		 * (outlierMap.containsKey(sampleIdx)) createAppropriateRowEntry(rowCt, i++,
		 * sheet, String.valueOf(dblValue), !isAdjusted ?
		 * styleList.get(BinnerConstants.STYLE_LAVENDER) :
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_LAVENDER),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_LAVENDER),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_LAVENDER)); else if (dblValue <
		 * BinnerConstants.BIG_NEGATIVE / 2.0) createAppropriateRowEntry(rowCt, i++,
		 * sheet, ".", styleList.get(BinnerConstants.STYLE_INTEGER_GREY_CENTERED),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_GREY_CENTERED),
		 * styleList.get(BinnerConstants.STYLE_INTEGER_GREY_CENTERED)); else if
		 * (isAdjusted && featureMedIntensity != null && ( Math.abs(dblValue -
		 * logFeatureMedIntensity) < BinnerConstants.EPSILON || Math.abs(dblValue -
		 * featureMedIntensity) < BinnerConstants.EPSILON)) { if
		 * (!sampleIdx.equals(feature.getMedianIntensityIdx()))
		 * createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(dblValue),
		 * styleList.get(BinnerConstants.STYLE_INTEGER_GREY),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_GREY),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC_GREY)); else
		 * createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(dblValue),
		 * styleList.get(BinnerConstants.STYLE_INTEGER),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC)); } else
		 * createAppropriateRowEntry(rowCt, i++, sheet, String.valueOf(dblValue),
		 * styleList.get(BinnerConstants.STYLE_INTEGER),
		 * styleList.get(BinnerConstants.STYLE_NUMERIC),
		 * styleList.get(BinnerConstants.STYLE_BORING)); sampleIdx++; }
		 */
	}

	public void createHeader(BinnerOutput output, Sheet sheet) throws Exception {
		/*
		 * rowCt = 0; int col = 0; sheet.createRow(rowCt);
		 * 
		 * XSSFCellStyle style = grabStyleBoringHeader(workBook, true);
		 * 
		 * for (String header : output.getHeaders()) {
		 * style.setAlignment(HorizontalAlignment.CENTER);
		 * PoiUtils.createRowEntry(rowCt, col++, sheet, header, style, 30); } rowCt +=
		 * 2;
		 */
	}
}
