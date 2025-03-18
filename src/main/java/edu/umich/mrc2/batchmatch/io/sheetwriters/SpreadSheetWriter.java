//////////////////////////////////////////////
//SpreadSheetWriter.java			
//Written by Jan Wigginton, November 2015	
//////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

public class SpreadSheetWriter implements Serializable {
	private static final long serialVersionUID = -6435767927472882356L;

	public enum MyCellStyle {
		RED_INSTRUCTIONS, BLUE_TITLE, BLUE_TITLE_SMALL, BLUE_TABLEHEADER_SMALL, BLUE_TABLEHEADER_LARGE, YELLOW,
		WHITE_BORDERED, WHITE, LIGHTBLUE_SUBTITLE
	}

	public SpreadSheetWriter() {
	}

	protected Workbook createWorkBook(File newFile, boolean isExcel) {
		try {
			if (isExcel) {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(newFile));
				return new HSSFWorkbook(fs);
			}

			OPCPackage pkg = OPCPackage.open(newFile);

			return new XSSFWorkbook(pkg);
		} catch (Exception e) {
			String msg = "Error while opening spreadsheet";
			System.out.println(msg);
			// throw new METWorksException(msg); //e.getMessage());
		}

		return null;
	}

	public Sheet createEmptySheet(String title, Workbook workBook) {
		Sheet sheet = workBook.createSheet(title);
		sheet.setPrintGridlines(false);
		sheet.setDisplayGridlines(true);
		sheet.getPrintSetup().setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		return sheet;
	}
	/*
	 * public SXSSFSheet createEmptyStreamingSheet(String title, SXSSFWorkbook
	 * workBook) { SXSSFSheet sheet = workBook.createSheet(title);
	 * sheet.setPrintGridlines(false); sheet.setDisplayGridlines(true);
	 * sheet.getPrintSetup().setLandscape(true); sheet.setFitToPage(true);
	 * sheet.setHorizontallyCenter(true);
	 * 
	 * return sheet; }
	 */

	public void createCellWithValue(int col, Row row, String val, MyCellStyle style, Workbook workBook) {
		Cell cell = row.createCell(col);
		if (style != null)
			cell.setCellStyle(grabStyle(style, workBook));
		cell.setCellValue(val);
	}

	public XSSFDataValidation createDateTypeValidation(XSSFSheet sheet, CellRangeAddressList addressList,
			String startDate, String endDate, String format) {
		DVConstraint dvConstraint = DVConstraint.createDateConstraint(
				org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType.BETWEEN, startDate, endDate, format);

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);

		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setEmptyCellAllowed(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.INFO);
		validation.createErrorBox("Error", "Invalid Date.");
		validation.setShowErrorBox(true);

		sheet.addValidationData(validation);
		return validation;
	}

	public XSSFDataValidation createIntBeyondOrEqualToValidation(XSSFSheet sheet, Integer rangeLimit,
			Boolean greaterThan, CellRangeAddressList addressList, String valueLabel) {
		DVConstraint dvConstraint = null;
		if (greaterThan)
			dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.GREATER_OR_EQUAL, rangeLimit.toString(),
					new Integer(Integer.MAX_VALUE).toString());
		else
			dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.LESS_OR_EQUAL, rangeLimit.toString(),
					new Integer(Integer.MIN_VALUE).toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(), valueLabel + " must be "
				+ (greaterThan ? "greater than or equal to " : " less than or equal to ") + rangeLimit);

		sheet.addValidationData(validation);
		// return so error message can be further customized
		return validation;
	}

	public XSSFDataValidation createDecimalBeyondOrEqualToValidation(XSSFSheet sheet, Integer rangeLimit,
			Boolean greaterThan, CellRangeAddressList addressList, String valueLabel) {
		DVConstraint dvConstraint = null;
		if (greaterThan)
			dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
					DVConstraint.OperatorType.GREATER_OR_EQUAL, rangeLimit.toString(),
					new Double(Integer.MAX_VALUE).toString());
		else
			dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
					DVConstraint.OperatorType.LESS_OR_EQUAL, rangeLimit.toString(),
					new Double(Double.MIN_VALUE).toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(), valueLabel + " must be "
				+ (greaterThan ? "greater than or equal to " : " less than or equal to ") + rangeLimit);

		sheet.addValidationData(validation);
		// return so error message can be further customized
		return validation;
	}

	public XSSFDataValidation createIntBeyondValidation(XSSFSheet sheet, Integer rangeLimit, Boolean greaterThan,
			CellRangeAddressList addressList, String valueLabel) {
		DVConstraint dvConstraint = null;
		if (greaterThan)
			dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.GREATER_THAN, rangeLimit.toString(),
					new Integer(Integer.MAX_VALUE).toString());
		else
			dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.LESS_THAN, new Integer(Integer.MIN_VALUE).toString(),
					rangeLimit.toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.WARNING);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(), valueLabel + " must be "
				+ (greaterThan ? "greater than or equal to " : " less than or equal to ") + rangeLimit);

		sheet.addValidationData(validation);
		return validation;
	}

	public XSSFDataValidation createIntRangeValidation(XSSFSheet sheet, Integer rangeBottom, Integer rangeTop,
			CellRangeAddressList addressList, String valueLabel) {
		DVConstraint dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,
				DVConstraint.OperatorType.BETWEEN, rangeBottom.toString(), rangeTop.toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.WARNING);
		validation.createErrorBox(valueLabel + " error",
				valueLabel + " must be between " + rangeBottom + " and " + rangeTop);

		sheet.addValidationData(validation);
		return validation;
	}

	public XSSFDataValidation createDecimalRangeValidation(XSSFSheet sheet, Double rangeBottom, Double rangeTop,
			CellRangeAddressList addressList, String valueLabel) {
		DVConstraint dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
				DVConstraint.OperatorType.BETWEEN, rangeBottom.toString(), rangeTop.toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(),
				valueLabel + " must be between " + rangeBottom + " and " + rangeTop);

		sheet.addValidationData(validation);
		return validation;
	}

	public void createDataValidation(XSSFSheet sheet, String[] values, int firstRow, int lastRow, int firstCol,
			int lastCol) {
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
				.createExplicitListConstraint(values);

		CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
		validation.setShowErrorBox(true);
		validation.setEmptyCellAllowed(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.INFO);
		validation.setShowPromptBox(true);
		sheet.addValidationData(validation);
	}

	private char buildHiddenList(Workbook workbook, XSSFSheet sheet, List<String> list, int colNum) {
		Sheet hidden = workbook.getSheet("hidden");

		for (int i = 0; i < list.size(); i++) {
			Row row = hidden.getRow(i);
			if (row == null)
				row = hidden.createRow(i);
			Cell cell = row.createCell(colNum);
			cell.setCellValue(list.get(i));
		}

		return (char) (colNum + (int) 'A');
	}

	public void buildColorRegion(Workbook workbook, Sheet sheet, int startRow, int endRow, int startCol, int endCol,
			int rgb1, int rgb2, int rgb3) {
		XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
		cellStyle.setFillBackgroundColor(ColorUtils.grabRGBColor(rgb1, rgb2, rgb3));
		CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);

		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row row = sheet.getRow(i);

			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				XSSFCell cell = (XSSFCell) row.getCell(j);
				cellStyle = cell.getCellStyle();
				cellStyle.setFillForegroundColor(ColorUtils.grabRGBColor(rgb1, rgb2, rgb3));
				cell.setCellStyle(cellStyle);
			}
		}
	}

	public void createDataValidationFromRange(Workbook workbook, XSSFSheet sheet, CellRangeAddressList addressList,
			List<String> list, int colNum) {
		char colName = buildHiddenList(workbook, sheet, list, colNum);

		Name namedCell = workbook.createName();
		namedCell.setNameName("hidden" + colNum);
		namedCell.setRefersToFormula("'hidden'!$" + colName + "$1:$" + colName + "$" + list.size());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
				.createFormulaListConstraint("hidden" + colNum);

		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);

		validation.setEmptyCellAllowed(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.INFO);
		validation.setShowErrorBox(false);

		sheet.addValidationData(validation);
	}

	public void writeWorkbook(OutputStream output, Workbook workBook) {
		try {
			workBook.write(output);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected XSSFCellStyle grabStyle(MyCellStyle style, Workbook workBook) {
		switch (style) {
		case RED_INSTRUCTIONS:
			return this.grabStyleInstructions(workBook, false);

		case BLUE_TITLE:
			return this.grabStylePageTitle(workBook, false, true);

		case BLUE_TITLE_SMALL:
			return this.grabStylePageTitle(workBook, false, false);

		case LIGHTBLUE_SUBTITLE:
			return this.grabStyleSubTitle(workBook, false);

		case BLUE_TABLEHEADER_SMALL:
			return this.grabStyleBlue(workBook, false);

		case BLUE_TABLEHEADER_LARGE:
			return this.grabStyleBlue(workBook, true);

		case YELLOW:
			return this.grabStyleYellow(workBook);

		case WHITE:
			return this.grabStyleWhite(workBook, false);

		case WHITE_BORDERED:
			return this.grabStyleWhite(workBook, true);

		default:
			return this.grabStyleWhite(workBook, false);
		}
	}

	public XSSFCellStyle grabStyleSubTitle(Workbook workBook, boolean larger) {
		Font fontHeader = workBook.createFont();
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) (larger ? 18 : 16));
		fontHeader.setItalic(true);
		fontHeader.setColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());

		XSSFCellStyle styleTitle = (XSSFCellStyle) workBook.createCellStyle();
		styleTitle.setAlignment(HorizontalAlignment.LEFT);
		styleTitle.setFont(fontHeader);
		// if (bottomAlign)
		// styleTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		styleTitle.setLocked(true);
		return styleTitle;
	}

	public XSSFCellStyle grabStyleYellow(Workbook workBook) {
		return grabStyleYellow(workBook, false);
	}

	public XSSFCellStyle grabStyleYellow(Workbook workBook, boolean light) {
		return grabStyleYellow(workBook, light, false);
	}

	public XSSFCellStyle grabStyleYellow(Workbook workBook, boolean light, boolean align_left) {
		return grabStyleYellow(workBook, light, align_left, false);
	}

	public XSSFCellStyle grabStyleYellow(Workbook workBook, boolean light, boolean align_left, boolean lightYellow) {
		return grabStyleYellow(workBook, light, align_left, lightYellow, true);
	}

	public XSSFCellStyle grabStyleYellowLeft(Workbook workBook) {
		XSSFCellStyle yellowStyleLeft = grabStyleYellow(workBook, true, false, false, false);
		yellowStyleLeft.setAlignment(HorizontalAlignment.LEFT);

		return yellowStyleLeft;
	}

	public XSSFCellStyle grabStyleYellow(Workbook workBook, boolean light, boolean align_left, boolean lightYellow,
			Boolean wrapText) {
		XSSFCellStyle styleYellow = (XSSFCellStyle) workBook.createCellStyle();
		styleYellow.setLocked(false);
		styleYellow.setAlignment(HorizontalAlignment.CENTER);

		styleYellow.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());

		XSSFColor colorBlack = ColorUtils.grabRGBColor(0, 0, 0); // new XSSFColor(Color.BLACK);
		styleYellow.setBorderColor(BorderSide.BOTTOM, colorBlack);
		styleYellow.setBorderColor(BorderSide.TOP, colorBlack);
		styleYellow.setBorderColor(BorderSide.LEFT, colorBlack);
		styleYellow.setBorderColor(BorderSide.RIGHT, colorBlack);

		styleYellow.setBorderBottom(BorderStyle.THIN);
		styleYellow.setBorderTop(BorderStyle.THIN);
		styleYellow.setBorderLeft(BorderStyle.THIN);
		styleYellow.setBorderRight(BorderStyle.THIN);

		styleYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		if (lightYellow)
			// styleYellow.setFillForegroundColor(ColorUtils.grabRGBColor(255, 255, 224));
			styleYellow.setFillForegroundColor(IndexedColors.ORANGE.getIndex());

		else if (light)
			styleYellow.setFillForegroundColor(ColorUtils.grabRGBColor(248, 253, 175));
		else
			styleYellow.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());

		styleYellow.setWrapText(wrapText);
		styleYellow.setVerticalAlignment(VerticalAlignment.TOP);

		return styleYellow;
	}

	public XSSFCellStyle grabStyleLightBlue(Workbook workBook) {
		XSSFCellStyle styleLightBlue = (XSSFCellStyle) workBook.createCellStyle();
		styleLightBlue.setLocked(false);
		styleLightBlue.setAlignment(HorizontalAlignment.CENTER);

		XSSFColor colorBlack = ColorUtils.grabRGBColor(0, 0, 0); // new XSSFColor(Color.BLACK);
		styleLightBlue.setBorderColor(BorderSide.BOTTOM, colorBlack);
		styleLightBlue.setBorderColor(BorderSide.TOP, colorBlack);
		styleLightBlue.setBorderColor(BorderSide.LEFT, colorBlack);
		styleLightBlue.setBorderColor(BorderSide.RIGHT, colorBlack);

		styleLightBlue.setBorderBottom(BorderStyle.THIN);
		styleLightBlue.setBorderTop(BorderStyle.THIN);
		styleLightBlue.setBorderLeft(BorderStyle.THIN);
		styleLightBlue.setBorderRight(BorderStyle.THIN);

		styleLightBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		styleLightBlue.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());

		styleLightBlue.setWrapText(true);
		styleLightBlue.setVerticalAlignment(VerticalAlignment.TOP);

		return styleLightBlue;
	}

	public XSSFCellStyle grabStyleLightGreen(Workbook workBook) {
		XSSFCellStyle styleLightGreen = (XSSFCellStyle) workBook.createCellStyle();
		styleLightGreen.setLocked(false);
		styleLightGreen.setAlignment(HorizontalAlignment.CENTER);

		styleLightGreen.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());

		XSSFColor colorBlack = ColorUtils.grabRGBColor(0, 0, 0); // new XSSFColor(Color.BLACK);
		styleLightGreen.setBorderColor(BorderSide.BOTTOM, colorBlack);
		styleLightGreen.setBorderColor(BorderSide.TOP, colorBlack);
		styleLightGreen.setBorderColor(BorderSide.LEFT, colorBlack);
		styleLightGreen.setBorderColor(BorderSide.RIGHT, colorBlack);

		styleLightGreen.setBorderBottom(BorderStyle.THIN);
		styleLightGreen.setBorderTop(BorderStyle.THIN);
		styleLightGreen.setBorderLeft(BorderStyle.THIN);
		styleLightGreen.setBorderRight(BorderStyle.THIN);

		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());

		styleLightGreen.setWrapText(false);
		styleLightGreen.setVerticalAlignment(VerticalAlignment.TOP);

		return styleLightGreen;
	}

	public XSSFCellStyle grabStyleRGB(Workbook workBook, Integer hue, Double saturation, Double vibrance)
			throws Exception {
		XSSFCellStyle styleYellow = (XSSFCellStyle) workBook.createCellStyle();

		styleYellow.setLocked(true);
		styleYellow.setAlignment(HorizontalAlignment.CENTER);
		styleYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleYellow.setWrapText(false);
		styleYellow.setVerticalAlignment(VerticalAlignment.CENTER);

		XSSFColor color = ColorUtils.HSVtoRGBSimple(hue, saturation, vibrance);
		styleYellow.setFillForegroundColor(color);

		return styleYellow;
	}

	public XSSFCellStyle grabStyleRGBFromHTML(Workbook workBook, String hexColor) throws Exception {
		return grabStyleRGBFromHTML(workBook, hexColor, false);
	}

	public XSSFCellStyle grabStyleRGBFromHTML(Workbook workBook, String hexColor, boolean useWhite) throws Exception {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		style.setLocked(true);
		style.setAlignment(HorizontalAlignment.CENTER);

		Font fontHeaderWhite = workBook.createFont();
		fontHeaderWhite.setColor(IndexedColors.GREY_40_PERCENT.getIndex());
		if (useWhite)
			style.setFont(fontHeaderWhite);

		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFColor color = ColorUtils.grabFromHtml(hexColor);
		style.setFillForegroundColor(color);

		style.setWrapText(false);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		return style;
	}

	public XSSFCellStyle grabStyleBoring(Workbook workBook) throws Exception {
		return grabStyleBoring(workBook, false);
	}

	public XSSFCellStyle grabStyleBoring(Workbook workBook, Boolean wrapText) throws Exception {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		style.setLocked(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(false);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		Font font = workBook.createFont();
		font.setFontName("Courier");
		style.setFont(font);
		return style;
	}

	public XSSFCellStyle grabStyleBoringHeader(Workbook workBook, Boolean wrapText) throws Exception {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		style.setLocked(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(wrapText);
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);

		Font font = workBook.createFont();
		font.setFontName("Courier");
		style.setFont(font);
		return style;
	}

	public XSSFCellStyle grabStyleBatchMatchHeader(Workbook workBook) throws Exception {
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

		return styleHeader;
	}

	public XSSFCellStyle grabStyleBoringUnderlined(Workbook workBook) throws Exception {
		XSSFColor colorBlack = ColorUtils.grabRGBColor(255, 255, 255);

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
		return styleBoringUnderlinedEntry;
	}

	public XSSFCellStyle grabStyleBoringLeft(Workbook workBook) throws Exception {
		XSSFCellStyle style = grabStyleBoring(workBook);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);
		return style;
	}

	public XSSFCellStyle grabStyleBoringLeftGrey(Workbook workBook) throws Exception {
		XSSFCellStyle style = grabStyleBoring(workBook);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);
		style.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		return style;
	}

	public XSSFCellStyle grabStyleBoringRight(Workbook workBook) throws Exception {
		XSSFCellStyle style = grabStyleBoring(workBook);
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);
		return style;
	}

	public XSSFCellStyle grabStyleBlankBoring(Workbook workBook) throws Exception {
		XSSFColor colorWhite = ColorUtils.grabRGBColor(255, 255, 255);
		return grabStyleBlankBoring(workBook, colorWhite);
	}

	public XSSFCellStyle grabStyleBlankBoring(Workbook workBook, XSSFColor color) throws Exception {
		XSSFCellStyle styleBlankBoring = grabStyleBoring(workBook);

		styleBlankBoring.setAlignment(HorizontalAlignment.CENTER);
		styleBlankBoring.setBorderBottom(BorderStyle.NONE);
		styleBlankBoring.setBorderTop(BorderStyle.NONE);
		styleBlankBoring.setBorderLeft(BorderStyle.NONE);
		styleBlankBoring.setBorderRight(BorderStyle.NONE);
		styleBlankBoring.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleBlankBoring.setFillForegroundColor(color);

		return styleBlankBoring;
	}

	public XSSFCellStyle grabStyleYellowBorderless(Workbook workBook) {
		XSSFCellStyle styleYellow = (XSSFCellStyle) workBook.createCellStyle();
		styleYellow.setLocked(false);
		styleYellow.setAlignment(HorizontalAlignment.LEFT);

		styleYellow.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());

		styleYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleYellow.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
		styleYellow.setWrapText(true);
		styleYellow.setVerticalAlignment(VerticalAlignment.TOP);

		return styleYellow;
	}

	public XSSFCellStyle grabStyleWhite(Workbook workBook, boolean bordered) {
		return grabStyleWhite(workBook, bordered, false);
	}

	public XSSFCellStyle grabStyleMonotypeBoring(Workbook workBook) throws Exception {
		XSSFCellStyle cellStyle = grabStyleBoring(workBook);
		Font font = workBook.createFont();

		// font.setFontHeightInPoints((short)24);
		font.setFontName("Courier");
		cellStyle.setFont(font);
		return cellStyle;
	}

	public XSSFCellStyle grabStyleWhite(Workbook workBook, boolean bordered, boolean locked) {
		XSSFCellStyle styleWhite = (XSSFCellStyle) workBook.createCellStyle();
		styleWhite.setAlignment(HorizontalAlignment.RIGHT);
		if (!bordered)
			styleWhite.setIndention((short) 5);
		Font fontLabel = workBook.createFont();
		fontLabel.setFontHeightInPoints((short) 16);
		fontLabel.setBold(true);
		styleWhite.setFont(fontLabel);

		if (bordered) {
			fontLabel.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			XSSFColor colorGrey = ColorUtils.grabRGBColor(200, 200, 200); // colorGrey;
			styleWhite.setBorderColor(BorderSide.BOTTOM, colorGrey);
			styleWhite.setBorderColor(BorderSide.TOP, colorGrey);
			styleWhite.setBorderColor(BorderSide.LEFT, colorGrey);
			styleWhite.setBorderColor(BorderSide.RIGHT, colorGrey);

			styleWhite.setBorderBottom(BorderStyle.THIN);
			styleWhite.setBorderTop(BorderStyle.THIN);
			styleWhite.setBorderLeft(BorderStyle.THIN);
			styleWhite.setBorderRight(BorderStyle.THIN);

			styleWhite.setAlignment(HorizontalAlignment.CENTER);
		}

		styleWhite.setLocked(locked);
		return styleWhite;
	}

	public XSSFCellStyle grabStyleLocked(Workbook workBook, boolean bordered) {
		return grabStyleLocked(workBook, bordered, true);
	}

	public XSSFCellStyle grabStyleLocked(Workbook workBook, boolean bordered, boolean locked) {
		XSSFCellStyle styleWhite = (XSSFCellStyle) workBook.createCellStyle();
		styleWhite.setAlignment(HorizontalAlignment.RIGHT);
		if (!bordered)
			styleWhite.setIndention((short) 5);

		Font fontLabel = workBook.createFont();
		fontLabel.setBold(true);
		styleWhite.setFont(fontLabel);

		if (bordered) {
			fontLabel.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			XSSFColor color = ColorUtils.grabRGBColor(200, 200, 200);
			styleWhite.setBorderColor(BorderSide.BOTTOM, color);
			styleWhite.setBorderColor(BorderSide.TOP, color);
			styleWhite.setBorderColor(BorderSide.LEFT, color);
			styleWhite.setBorderColor(BorderSide.RIGHT, color);

			styleWhite.setBorderBottom(BorderStyle.THIN);
			styleWhite.setBorderTop(BorderStyle.THIN);
			styleWhite.setBorderLeft(BorderStyle.THIN);
			styleWhite.setBorderRight(BorderStyle.THIN);

			styleWhite.setAlignment(HorizontalAlignment.CENTER);
		}

		styleWhite.setLocked(locked);
		return styleWhite;
	}

	public XSSFCellStyle grabStyleBlue(Workbook workBook) {
		return grabStyleBlue(workBook, false);
	}

	public XSSFCellStyle grabStyleBlue(Workbook workBook, boolean larger) {
		Font fontHeaderWhite = workBook.createFont();
		fontHeaderWhite.setBold(true);
		fontHeaderWhite.setFontHeightInPoints(larger ? (short) 16 : (short) 12);
		fontHeaderWhite.setColor(IndexedColors.WHITE.getIndex());

		XSSFCellStyle styleBlueHeader = (XSSFCellStyle) workBook.createCellStyle();
		styleBlueHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleBlueHeader.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		XSSFColor color = ColorUtils.grabRGBColor(255, 255, 255); // ; //new XSSFColor(Color.WHITE);

		styleBlueHeader.setBorderColor(BorderSide.BOTTOM, color);
		styleBlueHeader.setBorderColor(BorderSide.TOP, color);
		styleBlueHeader.setBorderColor(BorderSide.LEFT, color);
		styleBlueHeader.setBorderColor(BorderSide.RIGHT, color);
		styleBlueHeader.setAlignment(HorizontalAlignment.CENTER);

		styleBlueHeader.setBorderBottom(BorderStyle.THIN);
		styleBlueHeader.setBorderTop(BorderStyle.THIN);
		styleBlueHeader.setBorderLeft(BorderStyle.THIN);
		styleBlueHeader.setBorderRight(BorderStyle.THIN);

		styleBlueHeader.setFont(fontHeaderWhite);
		styleBlueHeader.setLocked(true);

		return styleBlueHeader;
	}

	public XSSFCellStyle grabStyleInstructions(Workbook workBook, boolean bottomAlign) {
		return grabStyleInstructions(workBook, bottomAlign, false);
	}

	public XSSFCellStyle grabStyleInstructions(Workbook workBook, boolean bottomAlign, boolean emphasize) {
		Font fontHeader = workBook.createFont();
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints(emphasize ? (short) 16 : (short) 12);
		fontHeader.setItalic(true);
		if (emphasize)
			fontHeader.setColor(IndexedColors.LIGHT_BLUE.getIndex());

		XSSFCellStyle styleInstructions = (XSSFCellStyle) workBook.createCellStyle();
		styleInstructions.setFont(fontHeader);
		if (bottomAlign)
			styleInstructions.setVerticalAlignment(VerticalAlignment.BOTTOM);

		styleInstructions.setLocked(true);
		return styleInstructions;
	}

	public XSSFCellStyle grabStylePageTitle(Workbook workBook, boolean bottomAlign, boolean larger) {
		Font fontHeader = workBook.createFont();
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) (larger ? 16 : 12));
		fontHeader.setItalic(true);
		fontHeader.setColor(IndexedColors.DARK_BLUE.getIndex());

		XSSFCellStyle styleTitle = (XSSFCellStyle) workBook.createCellStyle();
		styleTitle.setAlignment(HorizontalAlignment.CENTER);
		styleTitle.setFont(fontHeader);
		if (bottomAlign)
			styleTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);

		styleTitle.setLocked(true);
		return styleTitle;
	}

	public XSSFCellStyle grabStylePaletteInteger(Workbook workBook, String hexColor) throws Exception {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFColor color = ColorUtils.grabFromHtml(hexColor);
		style.setFillForegroundColor(color);
		XSSFDataFormat format = (XSSFDataFormat) workBook.createDataFormat();
		style.setDataFormat(format.getFormat("0"));
		return style;
	}

	public List<XSSFCellStyle> grabStyleListPalette(Workbook workBook, String hexColor) throws Exception {
		return grabStyleListPalette(workBook, hexColor, false);
	}

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
			styleNumeric.setDataFormat(format.getFormat("0.00000"));
		else
			styleNumeric.setDataFormat(format.getFormat("0.00"));
		styleList.add(styleInteger);
		styleList.add(styleNumeric);

		return styleList;
	}

	public XSSFCellStyle grabStyleInteger(Workbook workBook) {
		return grabStyleInteger(workBook, false);
	}

	public XSSFCellStyle grabStyleInteger(Workbook workBook, Boolean greyOption) {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		XSSFDataFormat format = (XSSFDataFormat) workBook.createDataFormat();
		style.setDataFormat(format.getFormat("0"));
		if (greyOption) {
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// style.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
			style.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));
		}

		return style;
	}

	public XSSFCellStyle grabStyleNumeric(Workbook workBook) {
		return grabStyleNumeric(workBook, false);
	}

	public XSSFCellStyle grabStyleNumeric(Workbook workBook, Boolean greyOption) {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		XSSFDataFormat format = (XSSFDataFormat) workBook.createDataFormat();
		style.setDataFormat(format.getFormat("#0.000000"));
		if (greyOption) {
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// style.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
			style.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));
		}

		return style;
	}

	public XSSFCellStyle grabStyleNumericShorter(Workbook workBook) {
		return grabStyleNumericShorter(workBook, false);
	}

	public XSSFCellStyle grabStyleNumericShorter(Workbook workBook, Boolean usingGrey) {
		return grabStyleNumericShorter(workBook, usingGrey, 4);
	}

	public XSSFCellStyle grabStyleNumericShorter(Workbook workBook, Boolean usingGrey, int digits) {
		XSSFCellStyle style = (XSSFCellStyle) workBook.createCellStyle();
		XSSFDataFormat format = (XSSFDataFormat) workBook.createDataFormat();

		if (digits == 2)
			style.setDataFormat(format.getFormat("#0.00"));
		else if (digits == 3)
			style.setDataFormat(format.getFormat("#0.000"));
		else
			style.setDataFormat(format.getFormat("#0.0000"));
		if (usingGrey) {
			style.setFillForegroundColor(ColorUtils.grabFromHtml("#D6DCE4"));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		return style;
	}

	public XSSFCellStyle grabStyleNumericGrey(Workbook workBook) {
		XSSFCellStyle styleGreyNumeric = (XSSFCellStyle) grabStyleNumeric(workBook);
		styleGreyNumeric.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
		styleGreyNumeric.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyNumeric.setAlignment(HorizontalAlignment.RIGHT);

		return styleGreyNumeric;
	}

	public XSSFCellStyle grabStyleIntegerGrey(Workbook workBook) {
		XSSFCellStyle styleGreyInteger = (XSSFCellStyle) grabStyleInteger(workBook);
		styleGreyInteger.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
		styleGreyInteger.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyInteger.setAlignment(HorizontalAlignment.RIGHT);

		return styleGreyInteger;
	}

	public XSSFCellStyle grabStyleNumericGreyCentered(Workbook workBook) {
		XSSFCellStyle styleGreyNumericCenter = grabStyleNumeric(workBook);
		styleGreyNumericCenter.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
		styleGreyNumericCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyNumericCenter.setAlignment(HorizontalAlignment.CENTER);
		return styleGreyNumericCenter;
	}

	public XSSFCellStyle grabStyleLightGreyCentered(Workbook workBook) throws Exception {
		XSSFCellStyle styleGreyNumericCenter = grabStyleBoring(workBook);
		styleGreyNumericCenter.setFillForegroundColor(ColorUtils.grabFromHtml("#F3F3F3"));
		styleGreyNumericCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyNumericCenter.setAlignment(HorizontalAlignment.CENTER);
		return styleGreyNumericCenter;
	}

	public XSSFCellStyle grabStyleGreyCentered(Workbook workBook) throws Exception {
		XSSFCellStyle styleGreyNumericCenter = grabStyleBoring(workBook);
		styleGreyNumericCenter.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
		styleGreyNumericCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyNumericCenter.setAlignment(HorizontalAlignment.CENTER);
		return styleGreyNumericCenter;
	}

	public XSSFCellStyle grabStyleGreyLeft(Workbook workBook) throws Exception {
		XSSFCellStyle styleGreyNumericCenter = grabStyleBoring(workBook);
		styleGreyNumericCenter.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
		styleGreyNumericCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyNumericCenter.setAlignment(HorizontalAlignment.LEFT);
		return styleGreyNumericCenter;
	}

	public XSSFCellStyle grabStyleIntegerGreyCentered(Workbook workBook) {
		XSSFCellStyle styleGreyIntegerCenter = grabStyleInteger(workBook);
		styleGreyIntegerCenter.setFillForegroundColor(ColorUtils.grabFromHtml("#e1e1e1"));
		styleGreyIntegerCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleGreyIntegerCenter.setAlignment(HorizontalAlignment.CENTER);
		return styleGreyIntegerCenter;
	}

	public XSSFCellStyle grabStyleLavender(Workbook workBook) {
		return grabStyleLavender(workBook, false);
	}

	public XSSFCellStyle grabStyleLavender(Workbook workBook, Boolean asInteger) {
		XSSFCellStyle styleLavender = asInteger ? grabStyleInteger(workBook) : grabStyleNumeric(workBook);
		styleLavender.setLocked(false);

		XSSFColor colorBlack = ColorUtils.grabRGBColor(0, 0, 0); // new XSSFColor(Color.BLACK);
		styleLavender.setBorderColor(BorderSide.BOTTOM, colorBlack);
		styleLavender.setBorderColor(BorderSide.TOP, colorBlack);
		styleLavender.setBorderColor(BorderSide.LEFT, colorBlack);
		styleLavender.setBorderColor(BorderSide.RIGHT, colorBlack);

		styleLavender.setBorderBottom(BorderStyle.THIN);
		styleLavender.setBorderTop(BorderStyle.THIN);
		styleLavender.setBorderLeft(BorderStyle.THIN);
		styleLavender.setBorderRight(BorderStyle.THIN);
		styleLavender.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleLavender.setFillForegroundColor(ColorUtils.grabFromHtml("#ccccff"));
		styleLavender.setWrapText(true);
		styleLavender.setVerticalAlignment(VerticalAlignment.TOP);

		return styleLavender;
	}

	protected XSSFCellStyle grabStyleChart(Workbook workBook, Boolean isNumeric) throws Exception {
		Font fontChart = workBook.createFont();
		fontChart.setBold(true);
		fontChart.setFontHeightInPoints((short) 14);
		fontChart.setColor(IndexedColors.BLACK.getIndex());

		XSSFCellStyle styleChart = grabStyleBlankBoring(workBook);
		styleChart.setAlignment(HorizontalAlignment.RIGHT);
		styleChart.setIndention((short) 1);
		styleChart.setFont(fontChart);
		XSSFColor colorGrey = ColorUtils.grabRGBColor(240, 240, 240);
		styleChart.setBorderColor(BorderSide.RIGHT, colorGrey);
		styleChart.setBorderColor(BorderSide.LEFT, colorGrey);

		if (isNumeric) {
			String chartNumberFormatString = "0.000";
			XSSFDataFormat chartDataFormat = (XSSFDataFormat) workBook.createDataFormat();
			short chartNumberFormatCode = chartDataFormat.getFormat(chartNumberFormatString);
			styleChart.setDataFormat(chartNumberFormatCode);
		}

		return styleChart;
	}

	protected XSSFCellStyle grabStyleChartBothBar(Workbook workBook, Boolean isNumeric, Boolean isRight)
			throws Exception {
		Font fontChart = workBook.createFont();
		fontChart.setBold(true);
		fontChart.setFontHeightInPoints((short) 14);
		fontChart.setColor(IndexedColors.BLACK.getIndex());

		XSSFCellStyle styleChartBothBar = grabStyleBoring(workBook);
		styleChartBothBar.setLocked(true);
		styleChartBothBar.setAlignment(HorizontalAlignment.RIGHT);
		styleChartBothBar.setIndention((short) 1);
		styleChartBothBar.setFont(fontChart);

		styleChartBothBar.setBorderLeft(BorderStyle.THIN);
		styleChartBothBar.setBorderRight(BorderStyle.THIN);
		styleChartBothBar.setBorderColor(BorderSide.RIGHT, ColorUtils.grabRGBColor(240, 240, 240));
		styleChartBothBar.setBorderColor(BorderSide.LEFT, ColorUtils.grabRGBColor(240, 240, 240));

		if (isRight) {
			styleChartBothBar.setBorderRight(BorderStyle.THIN);
			styleChartBothBar.setBorderLeft(BorderStyle.NONE);
		}

		if (isNumeric) {
			String chartNumberFormatString = "0.000";
			XSSFDataFormat chartDataFormat = (XSSFDataFormat) workBook.createDataFormat();
			short chartNumberFormatCode = chartDataFormat.getFormat(chartNumberFormatString);
			styleChartBothBar.setDataFormat(chartNumberFormatCode);
		}
		return styleChartBothBar;
	}

	public List<XSSFColor> grabYellowColorsForShading() {
		List<XSSFColor> colors = new ArrayList<XSSFColor>();

		colors.add(ColorUtils.grabFromHtml("#feefc9"));
		colors.add(ColorUtils.grabFromHtml("#ffe099"));
		colors.add(ColorUtils.grabFromHtml("#ffd168"));
		colors.add(ColorUtils.grabFromHtml("#ffc237"));

		return colors;
	}
}
