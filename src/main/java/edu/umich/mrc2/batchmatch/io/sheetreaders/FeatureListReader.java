
////////////////////////////////////////////////////
// FeatureListReader.java
// Written by Jan Wigginton July 2023
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetreaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import edu.umich.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.utils.orig.StringUtils;

public class FeatureListReader {

	public FeatureListReader() {
	}

	public static Map<Integer, Map<Integer, List<String>>> readFeatures(String inputFileName) {

		File inputFile = new File(inputFileName);

		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null; // false;
		}

		return readFeatureList(rawTextData);
	}

	public static Map<Integer, Map<Integer, List<String>>> readFeatureList(TextFile textFile) {

		Map<Integer, Map<Integer, List<String>>> listedFeaturesMap = new HashMap<Integer, Map<Integer, List<String>>>();

		try {
			List<String> rowContents = null;

			int rowStart = 0, rowEnd = textFile.getEndRowIndex() + 1;
			// Boolean firstNonBlankHeaderFound = false;

			int maxColUsed = -1;

			int batchCol = -1;
			for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.contains("Batch")) {
					batchCol = colIndex;
					if (batchCol > maxColUsed)
						maxColUsed = batchCol;
					break;
				}
			}

			int matchGroupCol = -1;
			for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.equals("Match Group")) {// || colHeader.equals("RT observed")) {
					matchGroupCol = colIndex;
					if (matchGroupCol > maxColUsed)
						maxColUsed = matchGroupCol;

					break;
				}
			}

			int featureNameCol = -1;
			for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.equals("Feature Name")) {
					featureNameCol = colIndex;
					if (featureNameCol > maxColUsed)
						maxColUsed = featureNameCol;

					break;
				}
			}

			if (featureNameCol == -1 || matchGroupCol == -1 || batchCol == -1) {
				JOptionPane.showMessageDialog(null,
						"Error: Expected cols were not found while reading feature list: "
								+ (batchCol == -1 ? "Batch " : "") + (featureNameCol == -1 ? "Feature Name " : "")
								+ (matchGroupCol == -1 ? "Match Group" : "") + BatchMatchConstants.LINE_SEPARATOR);
				return null;
			}

			int dataStart = 1;
			for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

				rowContents = textFile.getRawStringRow(rowNum);

				if (rowContents == null || rowContents.size() < maxColUsed)
					continue;

				String matchGrp = rowContents.get(matchGroupCol);
				String batchNo = rowContents.get(batchCol);
				String featureName = StringUtils.removeTrailingWhiteSpace(rowContents.get(featureNameCol));

				featureName = StringUtils.removeSpaces(featureName);
				if (featureName.length() < 1) {
					JOptionPane.showMessageDialog(null, "Empty feature name found at row " + rowNum);
					break;
				}

				Integer batchKey = null;
				try {
					batchKey = Integer.parseInt(batchNo);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error: Non-integer value for batch found" + batchNo);
					break;
				}

				if (batchKey != null) {
					if (!listedFeaturesMap.containsKey(batchKey))
						listedFeaturesMap.put(batchKey, new HashMap<Integer, List<String>>()); // new HashMap<Integer,
																								// List<String>>());
				}

				Integer matchGroupKey = null;
				try {
					matchGroupKey = Integer.parseInt(matchGrp);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error: Non-integer value for match group col " + matchGrp);
					break;
				}

				if (!listedFeaturesMap.get(batchKey).containsKey(matchGroupKey))
					listedFeaturesMap.get(batchKey).put(matchGroupKey, new ArrayList<String>());

				listedFeaturesMap.get(batchKey).get(matchGroupKey).add(featureName);
			}
		} catch (Exception e) {
		}

		return listedFeaturesMap;
	}
}
