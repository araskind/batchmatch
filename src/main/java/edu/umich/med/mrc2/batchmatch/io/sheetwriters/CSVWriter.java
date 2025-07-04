///////////////////////////////////////////////////
// CSVWriter.java
// Written by Jan Wigginton, August 2020
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.compress.utils.FileNameUtils;

import edu.umich.med.mrc2.batchmatch.utils.orig.BinnerNumUtils;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class CSVWriter {

	public CSVWriter() {
	}

	public File grabCSVforXLSXName(File xlsxFile) {
		
//		String altName = "";
//		try {
//			String baseName = xlsxName.substring(0, xlsxName.lastIndexOf('.'));
//			altName = baseName + ".csv";
//			File outputWithName = new File(altName);
//
//			Integer suffix = 1;
//			while (outputWithName.length() > 0L) {
//				baseName = altName.substring(0, altName.lastIndexOf('.'));
//				altName = baseName + "." + suffix + ".csv";
//				outputWithName = new File(altName);
//				suffix++;
//			}
//		} catch (Exception fnfe) {
//			fnfe.printStackTrace();
//			throw fnfe;
//		}
//		return createCSVEntry(altName);
		
		String parentDirPath = xlsxFile.getParentFile().getAbsolutePath();
		String baseName = FileNameUtils.getBaseName(xlsxFile.toPath());
		String altName = "";
		try {
			altName = baseName + ".xlsx";
			File outputWithName = Paths.get(parentDirPath, altName).toFile();
			Integer suffix = 1;
			while (outputWithName.length() > 0L) {
				altName = baseName + "." + suffix + ".csv";
				outputWithName = Paths.get(parentDirPath, altName).toFile();
				suffix++;
			}
		} catch (Exception fnfe) {
			fnfe.printStackTrace();
		}
		return Paths.get(parentDirPath, altName).toFile();
	}

	protected String createCSVEntry(String str) {
		// return "\'" + str + "\'";

		return str == null ? null : str.replace(',', ';');
	}

	protected String createAppropriateRowEntry(String value, String formatStringNumeric, String formatStringInteger) {

		String parseValue = value.indexOf(',') != -1 ? StringUtils.removeCommas(value) : value;

		if (BinnerNumUtils.isParsableAsDouble(parseValue)) {
			double x = Double.valueOf(parseValue);
			Boolean isTrueInt = ((x == (int) x) && parseValue.indexOf('.') == -1);

			if (isTrueInt)
				return String.format(formatStringInteger, Integer.parseInt(value));

			return String.format(formatStringNumeric, x);
		}
		return value == null ? null : value.replace(',', ';');
	}
}
