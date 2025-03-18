////////////////////////////////////////////////////
// BinnerInfoSheetWriter.java
// Created May 2018
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

public class BinnerInfoSheetWriter extends BinnerSpreadSheetWriter {

	protected XSSFCellStyle styleBoring = null, styleBoringEntry = null, styleBoringRedEntry = null,
			styleBoringBold = null, styleBoringUnderlinedEntry = null, styleBoringBoldUnderlinedEntry = null;
	protected XSSFCellStyle styleHeader = null, styleBoringBottom;

	protected Workbook workBook;
	protected int totalWidth = 40, headerHeight = 24, firstCol = 1;

	public BinnerInfoSheetWriter(Workbook workBook, int indent) {
		super();
		this.workBook = workBook;
		createStyles(indent);
	}

	protected void createSectionHeader(int rowNum, Sheet sheet, String title) {
		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum, firstCol, sheet, title, styleHeader, headerHeight);
	}

	protected void createTableEntry(int rowNum, Sheet sheet, String entry) {
		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum, firstCol, sheet, entry, styleBoringEntry);
	}

	protected void createTableBottom(int rowNum, Sheet sheet) {
		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum, firstCol, sheet, "", styleBoringBottom);
	}

	protected void createTableTitle(int rowNum, Sheet sheet, String header) {
		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum++, firstCol, sheet, header, styleBoringBold);
	}

	protected void createUnderlineRow(int rowNum, Sheet sheet, int length) {
		StringBuilder sb = new StringBuilder();
		sb.append("|");
		for (int i = 0; i < length; i++)
			sb.append("_");
		sb.append("_|");

		String header = String.format("%1$-" + length + 5 + "s", sb.toString());
		PoiUtils.createBlankCleanRow(rowNum, sheet, totalWidth, styleBoring);
		PoiUtils.createRowEntry(rowNum++, firstCol, sheet, header, styleBoringBold);
	}

	private void createStyles(int indent) {
		XSSFColor colorBlack = ColorUtils.grabRGBColor(0, 0, 0);

		styleHeader = grabStyle(MyCellStyle.BLUE_TABLEHEADER_LARGE, workBook);
		styleHeader.setAlignment(HorizontalAlignment.LEFT);
		styleHeader.setIndention((short) 2);
		styleHeader.setBorderColor(BorderSide.LEFT, colorBlack);
		styleHeader.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleHeader.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleHeader.setBorderRight(XSSFCellStyle.BORDER_THIN);

		try {
			styleBoring = grabStyleBlankBoring(workBook, ColorUtils.grabRGBColor(242, 242, 242));
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoring.setAlignment(HorizontalAlignment.LEFT);
		styleBoring.setIndention((short) indent);

		styleBoringEntry = null;
		try {
			styleBoringEntry = grabStyleBlankBoring(workBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoringEntry.setAlignment(HorizontalAlignment.LEFT);
		styleBoringEntry.setIndention((short) indent);
		styleBoringEntry.setBorderColor(BorderSide.LEFT, colorBlack);
		styleBoringEntry.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleBoringEntry.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleBoringEntry.setBorderRight(XSSFCellStyle.BORDER_THIN);

		styleBoringRedEntry = null;
		try {
			styleBoringRedEntry = grabStyleBlankBoring(workBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoringRedEntry.setAlignment(HorizontalAlignment.LEFT);
		styleBoringRedEntry.setIndention((short) indent);
		styleBoringRedEntry.setBorderColor(BorderSide.LEFT, colorBlack);
		styleBoringRedEntry.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleBoringRedEntry.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleBoringRedEntry.setBorderRight(XSSFCellStyle.BORDER_THIN);
		styleBoringRedEntry.getFont().setColor(ColorUtils.grabRGBColor(179, 0, 0));

		try {
			styleBoringUnderlinedEntry = grabStyleBlankBoring(workBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoringUnderlinedEntry.setAlignment(HorizontalAlignment.LEFT);
		styleBoringUnderlinedEntry.setIndention((short) indent);
		styleBoringUnderlinedEntry.setBorderColor(BorderSide.LEFT, colorBlack);
		styleBoringUnderlinedEntry.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleBoringUnderlinedEntry.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleBoringUnderlinedEntry.setBorderRight(XSSFCellStyle.BORDER_THIN);
		styleBoringUnderlinedEntry.getFont().setUnderline(FontUnderline.SINGLE);

		try {
			styleBoringBoldUnderlinedEntry = grabStyleBlankBoring(workBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoringBoldUnderlinedEntry.setAlignment(HorizontalAlignment.LEFT);
		styleBoringBoldUnderlinedEntry.setIndention((short) indent);
		styleBoringBoldUnderlinedEntry.setBorderColor(BorderSide.LEFT, colorBlack);
		styleBoringBoldUnderlinedEntry.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleBoringBoldUnderlinedEntry.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleBoringBoldUnderlinedEntry.setBorderRight(XSSFCellStyle.BORDER_THIN);
		styleBoringBoldUnderlinedEntry.getFont().setUnderline(FontUnderline.SINGLE);
		styleBoringBoldUnderlinedEntry.getFont().setBold(true);

		try {
			styleBoringBold = grabStyleBlankBoring(workBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		styleBoringBold.setAlignment(HorizontalAlignment.LEFT);
		styleBoringBold.setIndention((short) indent);
		styleBoringBold.setBorderColor(BorderSide.LEFT, colorBlack);
		styleBoringBold.setBorderColor(BorderSide.RIGHT, colorBlack);
		styleBoringBold.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleBoringBold.setBorderRight(XSSFCellStyle.BORDER_THIN);
		styleBoringBold.getFont().setBold(true);
		styleBoringBold.getFont().setUnderline(FontUnderline.NONE);

		styleBoringBottom = (XSSFCellStyle) styleBoring.clone();
		styleBoringBottom.setBorderColor(BorderSide.TOP, colorBlack);
		styleBoringBottom.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleBoringBottom.setBorderLeft(XSSFCellStyle.BORDER_NONE);
		styleBoringBottom.setBorderRight(XSSFCellStyle.BORDER_NONE);
		styleBoringBottom.setBorderBottom(XSSFCellStyle.BORDER_NONE);
	}
}
