////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//PoiUtils.java
//Written by Jan Wigginton, November 2015
//////////////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class PoiUtils {
	public static String readDropDownCol(Row row, short col) {
		String strCel = row.getCell(col).toString().trim();
		if (strCel != null && strCel.toUpperCase().contains("SELECT OR ENTER TEXT"))
			strCel = "";

		if (strCel != null && strCel.toUpperCase().contains("CHOOSE ONE"))
			strCel = "";
		return strCel;
	}

	public static Row createBlankRow(int row, Sheet sheet) {
		Row rowBlank = sheet.createRow(row);

		Cell cellBlank = rowBlank.createCell(0);
		cellBlank.setCellValue("");
		return rowBlank;
	}

	public static Row createBlankCleanRow(int rowNum, Sheet sheet, int width, CellStyle style) {
		Row row = sheet.createRow(rowNum);
		for (int col = 0; col < width; col++)
			createRowEntry(rowNum, col, sheet, "", style);
		return row;
	}

	public static Cell createRowStart(int row, Sheet sheet, String cellValue, CellStyle cellStyle) {
		return createRowEntry(row, 0, sheet, cellValue, cellStyle);
	}

	public static Cell updateRowEntry(int row, int col, Sheet sheet, String cellValue, CellStyle cellStyle) {
		return createRowEntry(row, col, sheet, cellValue, cellStyle);
	}

	// MERGE 05/08
	public static Cell createRowEntry(int row, int col, Sheet sheet, String cellValue, CellStyle cellStyle) {
		return createRowEntry(row, col, sheet, cellValue, cellStyle, 16);
	}

	// MERGE 05/08
	public static Cell createRowEntry(int row, int col, Sheet sheet, String cellValue, CellStyle cellStyle,
			Integer rowHeight) {
		Row rowBlank = sheet.getRow(row);
		if (rowBlank == null)
			rowBlank = sheet.createRow(row);

		if (rowHeight != null)
			rowBlank.setHeightInPoints(rowHeight);

		Cell cellBlank = rowBlank.createCell(col, CellType.STRING);
		cellBlank.setCellValue("" + cellValue);
		if (cellStyle != null)
			cellBlank.setCellStyle(cellStyle);

		return cellBlank;
	}

	public static Cell createNumericRowEntry(int row, int col, Sheet sheet, String cellValue, CellStyle cellStyle) {
		Row rowBlank = sheet.getRow(row);
		if (rowBlank == null)
			rowBlank = sheet.createRow(row);

		Cell cellBlank = rowBlank.createCell(col, CellType.NUMERIC);
		cellBlank.setCellStyle(cellStyle);
		cellBlank.setCellValue(Double.valueOf(cellValue));

		return cellBlank;
	}

	public static Cell createRowEntryWithFormula(int row, int col, Sheet sheet, String formula, CellStyle cellStyle) {
		
		Cell cell = createRowEntry(row, col, sheet, "", cellStyle);
		//cell.setCellType(CellType.FORMULA);
		cell.setCellFormula(formula);
		return cell;
	}

	public static String getString(Cell cell) {
		return getString(cell, false);
	}

	public static String getString(Cell cell, Boolean forceInteger) {
		String value = "";
		CellType type = cell.getCellType();

		if (type.equals(CellType.FORMULA))
			type = cell.getCachedFormulaResultType();

		switch (type) {
			case BLANK:
				value = "";
				break;
	
			case BOOLEAN:
				value = cell.getBooleanCellValue() ? "true" : "false";
				break;
				
			case STRING:
				value = cell.getStringCellValue();
				break;
				
			case NUMERIC:
				value = "" + cell.getNumericCellValue();
				cell.setCellValue("" + value);
				value = cell.getStringCellValue();
				break;
				
			case ERROR:
			default:
				break;
		}
		return value;
	}
}
