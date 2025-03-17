package edu.umich.batchmatch.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import edu.umich.batchmatch.main.BinnerConstants2;


public class BinnerTestUtils {

	public static void printRealMatrix(RealMatrix matrix, Integer[][] extraInfo) {
		printRealMatrix(matrix, extraInfo, false);
	}

	public static void printRealMatrix(RealMatrix matrix, Integer[][] extraInfo, Boolean append) {
		if (matrix == null)
			return;

		try {
			String fileLocation = BinnerConstants2.HOME_DIRECTORY + BinnerConstants2.FILE_SEPARATOR
					+ BinnerConstants2.CONFIGURATION_DIRECTORY + BinnerConstants2.FILE_SEPARATOR + "matrix_output."
					+ BinnerConstants2.POSTPROCESS_VERSION.toString() + ".csv";

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation, append));
			writer.write("\n");
			for (int row = 0; row < matrix.getRowDimension(); row++) {
				for (int col = 0; col < row; col++) {
					writer.write("r" + row + ", c" + col + ": " + new Double(matrix.getEntry(row, col)).toString());
					if (extraInfo != null) {
						writer.write("(" + extraInfo[row][col] + ")");
					}
					writer.write("\n");

					// if (col > 0 && (col%7 == 0 && matrix.getColumnDimension() > 7) &&
					// matrix.getColumnDimension() > 1) writer.write("\n");
				}
			}
			writer.write("\n\n\n\n");
			writer.close();
		} catch (Exception e) {
			System.out.println("Error while printing real matrix");
		}
	}

	public static void printArray(Array2DRowRealMatrix matrix, Integer[][] extraInfo) {
		printArray(matrix, extraInfo, false);
	}

	public static void printArray(Array2DRowRealMatrix matrix, Integer[][] extraInfo, Boolean append) {
		if (matrix == null)
			return;

		try {
			String fileLocation = BinnerConstants2.HOME_DIRECTORY + BinnerConstants2.FILE_SEPARATOR
					+ BinnerConstants2.CONFIGURATION_DIRECTORY + BinnerConstants2.FILE_SEPARATOR + "matrix_output."
					+ BinnerConstants2.POSTPROCESS_VERSION.toString() + ".csv";

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation, append));
			writer.write("\n");
			for (int row = 0; row < matrix.getRowDimension(); row++) {
				for (int col = 0; col < row; col++) {
					writer.write("r" + row + ", c" + col + ": " + new Double(matrix.getEntry(row, col)).toString());
					if (extraInfo != null) {
						writer.write("(" + extraInfo[row][col] + ")");
					}
					writer.write("\n");
					// if (col > 0 && (col%7 == 0 && matrix.getColumnDimension() > 7) &&
					// matrix.getColumnDimension() > 1) writer.write("\n");
				}
			}
			writer.write("\n\n\n\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			System.out.println("Error while printing array 2D");
		}
	}

	public static void printArray(int[] array) {
		printArray(array, false);
	}

	public static void printArray(int[] array, Boolean append) {
		try {
			String fileLocation = BinnerConstants2.HOME_DIRECTORY + BinnerConstants2.FILE_SEPARATOR
					+ BinnerConstants2.CONFIGURATION_DIRECTORY + BinnerConstants2.FILE_SEPARATOR + "array_output."
					+ BinnerConstants2.POSTPROCESS_VERSION.toString() + ".csv";

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation, append));
			writer.write("\n");
			for (int i = 0; i < array.length; i++) {
				writer.write("entry " + i + ": " + array[i] + "\n");
			}
			writer.write("\n\n\n\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			System.out.println("Error while printing array");
		}
	}
}
