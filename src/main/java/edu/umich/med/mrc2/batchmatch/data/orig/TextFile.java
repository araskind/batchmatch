package edu.umich.med.mrc2.batchmatch.data.orig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;

public class TextFile {

	private List<List<String>> data;

	public TextFile() {
		data = new ArrayList<List<String>>();
	}

	public TextFile(List<String[]> rawData) {
		
		int dataRowLen = 0;
		int pastDataRowLen = 0;
		data = new ArrayList<List<String>>();
		for (int row = 0; row < rawData.size(); row++) {
			
			pastDataRowLen = dataRowLen;
			dataRowLen = rawData.get(row).length;
			if (pastDataRowLen > 0 && dataRowLen != pastDataRowLen) {
				System.out.println("Data row len " + dataRowLen + " at row " 
						+ row + " differs from " + pastDataRowLen + " for row  " + (row - 1));
				//	TODO throw exception?
			}
		}
		for (int row = 0; row < rawData.size(); row++)			
			data.add(Arrays.asList(rawData.get(row)));
	}

	public TextFile(File file) throws IOException {
		open(file);
	}

	public void open(File file) throws IOException {
		open(file, false);
	}

	private void open(File file, boolean filterTrailing) throws IOException {
		
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line = input.readLine();
		String line2 = line;
		if (filterTrailing && line != null) 
			line = line.trim();
		
		CSVParser commaParser = new CSVParser(
				CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, '|');
		CSVParser tabParser = new CSVParser('\t', CSVParser.DEFAULT_QUOTE_CHARACTER, '|');
		CSVParser parser = (tabParser.parseLine(line).length > 1) ? tabParser : commaParser;

		data = new ArrayList<List<String>>();
		while (line != null) {
			String[] rowArray = parser.parseLine(line);
			List<String> row = new ArrayList<String>(Arrays.asList(rowArray));
			data.add(row);
			line = input.readLine();
			if (filterTrailing && line != null) {
				line2 = line.trim();
				line = line2;
			}
		}
		input.close();
	}

	public String getString(int row, int col) {
		if (data.size() <= row || data.get(row) == null || data.get(row).size() <= col) {
			return null;
		}

		return data.get(row).get(col);
	}

	public Integer getInteger(int row, int col) {
		if (data.size() <= row || data.get(row) == null || data.get(row).size() <= col) {
			return null;
		}
		/// System.out.println("Data " + data.get(row).get(col));

		try {
			return Integer.parseInt(data.get(row).get(col));
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public Double getDouble(int row, int col) {
		if (data.size() <= row || data.get(row) == null || data.get(row).size() <= col) {
			return null;
		}

		try {
			return Double.parseDouble(data.get(row).get(col));
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public int getStartRowIndex() {

		if (data.isEmpty())
			return -1;
		else
			return 0;
	}

	public int getEndRowIndex() {
		return (data.size() - 1);
	}

	public int getStartColIndex(int row) {
		if (data.size() <= row || data.get(row) == null) {
			return -1;
		}

		return 0;
	}

	public int getEndColIndex(int row) {
		if (data.size() <= row || data.get(row) == null) {
			return -1;
		}
		return data.get(row).size();
	}

	public List<String> getRawStringRow(int row) {
		return data.get(row);
	}
}
