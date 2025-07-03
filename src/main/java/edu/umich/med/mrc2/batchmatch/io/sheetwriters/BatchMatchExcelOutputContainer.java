////////////////////////////////////////////////////
// BatchMatchExcelOutputContainer.java
// Written by Jan Wigginton, September 2019
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchMatchSummaryInfo;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;

public class BatchMatchExcelOutputContainer extends BinnerSpreadSheetWriter implements Serializable {

	private static final long serialVersionUID = 488660231889571100L;

	private File baseFile;
	private SXSSFWorkbook workBook = null;
	private List<XSSFCellStyle> styleList = new ArrayList<XSSFCellStyle>();

	public BatchMatchExcelOutputContainer(File baseFile) {
		super();
		this.baseFile = baseFile;
	}

	public void writeBatchMatchDataSet(
			PostProcessDataSet data, 
			BatchMatchSummaryInfo summaryInfo, 
			OutputStream output,
			Boolean writeCollapsed) throws Exception {
		writeBatchMatchDataSet(data, summaryInfo, output, writeCollapsed, true);
	}

	public void writeBatchMatchDataSet(
			PostProcessDataSet data, 
			BatchMatchSummaryInfo summaryInfo, 
			OutputStream output,
			Boolean writeCollapsed, 
			Boolean writeGrid) throws Exception {

		workBook = new SXSSFWorkbook(100);
		workBook.setCompressTempFiles(true);

		styleList.add(BinnerConstants.STYLE_BORING, grabStyleBoring(workBook));
		styleList.add(BinnerConstants.STYLE_INTEGER, grabStyleInteger(workBook));
		styleList.add(BinnerConstants.STYLE_NUMERIC, grabStyleNumeric(workBook));
		styleList.add(BinnerConstants.STYLE_YELLOW, grabStyleYellow(workBook, true, false, false, false));
		styleList.add(BinnerConstants.STYLE_LIGHT_BLUE, grabStyleLightBlue(workBook));
		styleList.add(BinnerConstants.STYLE_LIGHT_GREEN, grabStyleLightGreen(workBook));
		styleList.add(BinnerConstants.STYLE_LAVENDER, grabStyleLavender(workBook, true));
		styleList.add(BinnerConstants.STYLE_INTEGER_GREY, grabStyleIntegerGrey(workBook));
		styleList.add(BinnerConstants.STYLE_NUMERIC_GREY, grabStyleNumericGrey(workBook));
		styleList.add(BinnerConstants.STYLE_INTEGER_GREY_CENTERED, grabStyleIntegerGreyCentered(workBook));
		styleList.add(BinnerConstants.STYLE_NUMERIC_GREY_CENTERED, grabStyleNumericGreyCentered(workBook));
		styleList.add(BinnerConstants.STYLE_BORING_LEFT, grabStyleBoringLeft(workBook));
		styleList.add(BinnerConstants.STYLE_HEADER_WRAPPED, grabStyleBoringHeader(workBook, true));
		styleList.add(BinnerConstants.STYLE_NUMERIC_SHORTER, grabStyleNumericShorter(workBook));
		styleList.add(BinnerConstants.STYLE_BORING_RIGHT, grabStyleBoringRight(workBook));
		styleList.add(BinnerConstants.STYLE_NUMERIC_LAVENDER, grabStyleLavender(workBook, false));
		styleList.add(BinnerConstants.STYLE_BORING_GREY, this.grabStyleLightGreyCentered(workBook));
		styleList.add(BinnerConstants.STYLE_YELLOW_LEFT, this.grabStyleYellowLeft(workBook));
		styleList.add(BinnerConstants.STYLE_GREY_LEFT, this.grabStyleGreyLeft(workBook));
		styleList.add(BinnerConstants.STYLE_NUMERIC_SHORTER_GREY, this.grabStyleNumericShorter(workBook, true));
		styleList.add(BinnerConstants.STYLE_NUMERIC_SHORTEST_GREY,
				this.grabStyleNumericShorter(workBook, true, 2));
		styleList.add(BinnerConstants.STYLE_NUMERIC_SHORTEST, this.grabStyleNumericShorter(workBook, false, 2));
		styleList.add(BinnerConstants.STYLE_ORANGE, grabStyleYellow(workBook, true, false, true, false));

		styleList.get(BinnerConstants.STYLE_LAVENDER).setAlignment(HorizontalAlignment.RIGHT);

		Font font = workBook.createFont();
		font.setFontName("Courier");

		for (XSSFCellStyle style : styleList)
			style.setFont(font);

		if (summaryInfo != null) {
			BatchMatchSummarySheetWriter summaryWriter = new BatchMatchSummarySheetWriter(workBook);
			summaryWriter.fillSummaryTab(summaryInfo);
		}
		// "Ambiguous
		if (!writeCollapsed) {
			BatchMatchExpandedFeatureWriter jointWriter = new BatchMatchExpandedFeatureWriter(workBook, null);
			jointWriter.setDerivedColNameMapping(data.getDerivedNameToHeaderMap());
			jointWriter.setSheetName("All Features");

			try {
				jointWriter.writeExpandedFeatureSheet(data.getOrderedNonStandardHeaders(),
						data.getOrderedIntensityHeaders(), data.getFeatures(), styleList, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (writeCollapsed)
			writeCollapsedSummaries(data, writeGrid);
		
		workBook.write(output);
		workBook.close();
	}

	private void writeCollapsedSummaries(PostProcessDataSet data, Boolean writeGrid) throws Exception {

		if (!writeGrid) {
			BatchMatchDataSetSummaryWriter writer = new BatchMatchDataSetSummaryWriter(workBook);
			writer.setSheetName("Match Group Summary");
			writer.writeSummaryToFile(data, styleList);

			BatchMatchExpandedFeatureWriter apparentMatchWriter = new BatchMatchExpandedFeatureWriter(workBook, null);
			apparentMatchWriter.setDerivedColNameMapping(data.getDerivedNameToHeaderMap());
			apparentMatchWriter.setSheetName("Unambiguous Match Groups");

			Map<Integer, List<FeatureFromFile>> unambigousMatchedFeaturesMap = data.grabFeatureByGroupMap(true, false);
			List<FeatureFromFile> unambiguousMatchedFeatures = data
					.grabFeatureByGroupAsRTSortedList(unambigousMatchedFeaturesMap);

			try {
				apparentMatchWriter.writeExpandedFeatureSheet(data.getOrderedNonStandardHeaders(),
						data.getOrderedIntensityHeaders(), unambiguousMatchedFeatures, styleList, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			BatchMatchExpandedFeatureWriter possibleMatchWriter = new BatchMatchExpandedFeatureWriter(workBook, null);
			possibleMatchWriter.setDerivedColNameMapping(data.getDerivedNameToHeaderMap());
			possibleMatchWriter.setSheetName("Ambiguous Match Groups");

			Map<Integer, List<FeatureFromFile>> ambigousMatchedFeaturesMap = data.grabFeatureByPossibleGroupMap(.005,
					.05);
			List<FeatureFromFile> ambiguousMatchedFeatures = data
					.grabFeatureByGroupAsRTSortedList(ambigousMatchedFeaturesMap);

			try {
				possibleMatchWriter.writeExpandedFeatureSheet(data.getOrderedNonStandardHeaders(),
						data.getOrderedIntensityHeaders(), ambiguousMatchedFeatures, styleList, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (writeGrid) {
			int maxBatch = data.getMaxBatch();
			try {
				data.collapseFeatureGroups();
			} catch (Exception e) {
				e.printStackTrace();
			}

			BatchMatchCollapsedFeatureWriter jointWriter3 = new BatchMatchCollapsedFeatureWriter(workBook, null);
			jointWriter3.setDerivedColNameMapping(data.getDerivedNameToHeaderMap());
			jointWriter3.setSheetName("Collapsed Unambiguous Groups");

			try {
				jointWriter3.writeCollapsedFeatureSheet(data, maxBatch, data.getOrderedNonStandardHeaders(), null,
						data.getOrderedIntensityHeaders(), styleList, true);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public File grabIncrementedOutputFile() {
		
		String parentDirPath = baseFile.getParentFile().getAbsolutePath();
		String baseName = FileNameUtils.getBaseName(baseFile.toPath());
		String altName = "";
		try {
			altName = baseName + ".xlsx";
			File outputWithName = Paths.get(parentDirPath, altName).toFile();
			Integer suffix = 1;
			while (outputWithName.length() > 0L) {
				altName = baseName + "." + suffix + ".xlsx";
				outputWithName = Paths.get(parentDirPath, altName).toFile();
				suffix++;
			}
		} catch (Exception fnfe) {
			fnfe.printStackTrace();
		}
		return Paths.get(parentDirPath, altName).toFile();
	}

	public String getFileName() {
		return baseFile.getAbsolutePath();
	}
	
	public File getBaseFile() {
		return baseFile;
	}

	public SXSSFWorkbook getWorkBook() {
		return workBook;
	}

	public void setWorkBook(SXSSFWorkbook workBook) {
		this.workBook = workBook;
	}

}
