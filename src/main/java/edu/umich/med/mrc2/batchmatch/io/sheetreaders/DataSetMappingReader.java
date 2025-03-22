
////////////////////////////////////////////////////
// DataSetMappingReader.java
// Written by Jan Wigginton June 2023
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.io.sheetreaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureInfoForMatchGroupMapping;
import edu.umich.med.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class DataSetMappingReader {

	public DataSetMappingReader() {
	}

	public static Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> readDataSetMapping(String inputFileName) {

		File inputFile = new File(inputFileName);

		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null; // false;
		}

		return readDataSetMapping(rawTextData);
	}

	// FeatureInfo

	public static Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> readDataSetMapping(TextFile txtData) {

		Map<Integer, Map<Integer, Integer>> dataSetMapping = new HashMap<Integer, Map<Integer, Integer>>();

		Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> mappedFeatures = new HashMap<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>>();

		try {
			List<String> rowContents = null;

			int rowStart = 0, rowEnd = txtData.getEndRowIndex() + 1;
			Boolean firstNonBlankHeaderFound = false, readingGlobalMapping = false;

			String value = null;
			Integer intValue = null;
			Double dblValue = null;
			List<Integer> globalBatches = null;

			for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);
				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				if (rowContents.size() < 2)
					continue;

				if (!firstNonBlankHeaderFound) {

					intValue = null;

					String rowStr = rowContents.get(0);

					String[] tokens = rowStr.split(" ");

					// if (rowContents.get(0).equals("Global")) {
					if (tokens[0].equals("Global")) {

						globalBatches = new ArrayList<Integer>();

						for (int i = 1; i < tokens.length; i++) {

							value = tokens[i]; // .get(i);
							intValue = null;
							try {
								intValue = Integer.parseInt(value);
							} catch (Exception e) {
								throw e;
							}

							globalBatches.add(intValue);
						}
						firstNonBlankHeaderFound = true;
						continue;
					}
				}

				if (rowContents.size() != 3 && globalBatches != null)
					throw new Exception("Error:  Line size incorrect in data set mapping reader");

				if (rowContents.size() != 4 && globalBatches == null)
					throw new Exception("Error:  Line size incorrect in data set mapping reader");

				readingGlobalMapping = rowContents.size() == 3;

				int nToAddPerLine = readingGlobalMapping ? globalBatches.size() : 1;
				Integer batch = null, destGroup = null, sourceGroup = null;

				if (!readingGlobalMapping) {
					value = rowContents.get(2);
					try {
						batch = Integer.parseInt(value);
					} catch (Exception e) {
						throw e;
					}
				} else
					batch = globalBatches.get(0);

				value = rowContents.get(0);
				intValue = null;
				try {
					sourceGroup = Integer.parseInt(value);
				} catch (Exception e) {
					throw e;
				}

				value = rowContents.get(1);
				try {
					destGroup = Integer.parseInt(value);
				} catch (Exception e) {
					throw e;
				}

				value = rowContents.get(2);
				try {
					dblValue = Double.parseDouble(value);
				} catch (Exception e) {
					throw e;
				}

				for (int nAdded = 0; nAdded < nToAddPerLine; nAdded++) {
					if (readingGlobalMapping) {
						batch = globalBatches.get(nAdded);
					}
					// if (!dataSetMapping.containsKey(batch)) {
					// dataSetMapping.put(batch, new HashMap<Integer, Integer>());
					// }
					// dataSetMapping.get(batch).put(sourceGroup, destGroup);

					if (!mappedFeatures.containsKey(batch)) {
						mappedFeatures.put(batch, new HashMap<Integer, FeatureInfoForMatchGroupMapping>());
					}

					FeatureInfoForMatchGroupMapping newFeatureInfo = new FeatureInfoForMatchGroupMapping();
					newFeatureInfo.setSourceBatch(sourceGroup);
					newFeatureInfo.setDestinationBatch(destGroup);
					newFeatureInfo.setMappedRt(dblValue);

					mappedFeatures.get(batch).put(sourceGroup, newFeatureInfo);

				}
			}
		} catch (Exception e) {
		}

		return mappedFeatures;
	}
}
