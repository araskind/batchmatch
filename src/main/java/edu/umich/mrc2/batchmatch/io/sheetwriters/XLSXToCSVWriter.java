package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XLSXToCSVWriter {
	static private Pattern rxquote = Pattern.compile("\"");

	public XLSXToCSVWriter() {
	}

	public void writeSheetsToCSV(String reportFileName, Workbook workBook) throws IOException {
		for (int i = 0; i < workBook.getNumberOfSheets(); i++)
			writeToFile(reportFileName + "." + i + ".csv", workBook.getSheetAt(i));
	}

	public void writeToFile(String csvFile, Sheet sheet) throws IOException {

		FormulaEvaluator fe = null;

		DataFormatter formatter = new DataFormatter();
		PrintStream out = new PrintStream(new FileOutputStream(csvFile), true, "UTF-8");
		byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		out.write(bom);
		for (int r = 0, rn = sheet.getLastRowNum(); r <= rn; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				out.println(',');
				continue;
			}
			boolean firstCell = true;
			for (int c = 0, cn = row.getLastCellNum(); c < cn; c++) {
				Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (!firstCell)
					out.print(',');
				if (cell != null) {
					if (fe != null)
						cell = fe.evaluateInCell(cell);
					String value = formatter.formatCellValue(cell);
					if (cell.getCellTypeEnum() == CellType.FORMULA) {
						value = "=" + value;
					}
					out.print(encodeValue(value));
				}
				firstCell = false;
			}
			out.println();
		}
	}

	static private String encodeValue(String value) {
		boolean needQuotes = (value.indexOf(',') != -1 || value.indexOf('"') != -1 || value.indexOf('\n') != -1
				|| value.indexOf('\r') != -1);

		Matcher m = rxquote.matcher(value);
		needQuotes |= (m.find());
		value = m.replaceAll("\"\"");

		return (needQuotes ? "\"" + value + "\"" : value);
	}
}

/*
 * /* StringBuilder lines = new StringBuilder(); for (int iMap = 0; iMap <
 * featureMapList.size(); iMap++) { header.append(", Group" +
 * (featureMapList.size() > 1 ? (iMap + 1) : "")); }
 * header.append(DNEAConstants.LINE_SEPARATOR);
 * 
 * try { FileUtils.writeStringToFile(outputFile, header.toString(), null,
 * false); } catch (IOException ioe) { ioe.printStackTrace(); return false; }
 * 
 * Row row = null; for (int i = 0; i < sheet.getLastRowNum(); i++) { row =
 * sheet.getRow(i); for (int j = 0; j < row.getLastCellNum(); j++) {
 * lines.append("\"" + row.getCell(j) + "\";"); } if (i % 100 == 0 || i =
 * sheet.getLastRowNum() - 1) System.out.println(); }
 */
