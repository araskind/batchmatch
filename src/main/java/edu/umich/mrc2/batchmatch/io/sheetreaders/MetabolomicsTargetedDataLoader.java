////////////////////////////////////////////////////
// MetabolomicsTargetedDataLoader.java
// Written by Jan Wigginton and Bill Duren, November 2019
//////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetreaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.TextFile;
import edu.umich.mrc2.batchmatch.main.PostProccessConstants;
import edu.umich.mrc2.batchmatch.utils.StringUtils;

public class MetabolomicsTargetedDataLoader {

	public MetabolomicsTargetedDataLoader() {
		
	}

	private int featureNameCol;
	private int dataStart;
	private List<Integer> rtHeaderdIndices = new ArrayList<Integer>();
	private List<String> rtHeaders = new ArrayList<String>();
	private List<Integer> intensityHeaderIndices = new ArrayList<Integer>();
	private List<String> intensityHeaders = new ArrayList<String>();
	private List<Integer> massHeaderIndices = new ArrayList<Integer>();
	private List<String> massHeaders = new ArrayList<String>();

	private TextFile loadData(String fileName) {

		File inputFile = new File(fileName);
		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return rawTextData;
	}

	private Map<String, List<Double>> locateAndLoadValues(String fileName, boolean justPools, boolean loadRts) {

		TextFile txtData = loadData(fileName);
		if (txtData == null)
			return null;

		Map<String, List<Double>> valueMap = new HashMap<String, List<Double>>();

		locateRTandIntensityColumns(justPools, txtData);

		valueMap = null;
		if (loadRts)
			valueMap = loadValueMap(txtData, justPools, loadRts);
		else
			valueMap = loadMassValueMap(txtData, justPools);

		return valueMap;
	}
	
	public Map<String, Double> locateMassesAndLoadAverageMap(String fileName, boolean justPools) {

		return locateValuesAndLoadAverageMap(fileName, justPools, false);
	}

	public Map<String, Double> locateRTsAndLoadAverageMap(String fileName, boolean justPools) {

		return locateValuesAndLoadAverageMap(fileName, justPools, true);
	}

	private Map<String, Double> locateValuesAndLoadAverageMap(String fileName, boolean justPools, boolean useRts) {

		Map<String, List<Double>> valueMap = locateAndLoadValues(fileName, justPools, useRts);

		Map<String, Double> featureByAvgRTMap = new HashMap<String, Double>();
		Double avgValue = null;
		for (String key : valueMap.keySet()) {

			List<Double> valuesForFeature = valueMap.get(key);

			if (valuesForFeature != null && valuesForFeature.size() > 0) {
				avgValue = 0.0;

				for (int i = 0; i < valuesForFeature.size(); i++)
					avgValue += valuesForFeature.get(i);

				avgValue /= (1.0 * valuesForFeature.size());

				featureByAvgRTMap.put(key, avgValue);
			}
		}
		return featureByAvgRTMap;
	}

	public Map<String, Integer> locateRTsAndLoadSampleCtMap(String fileName, boolean justPools) {

		Map<String, List<Double>> valueMap = locateAndLoadValues(fileName, justPools, true);

		Map<String, Integer> featureBySampleCtMap = new HashMap<String, Integer>();
		Double avgValue = null;
		for (String key : valueMap.keySet()) {

			List<Double> valuesForFeature = valueMap.get(key);

			if (valuesForFeature == null)
				featureBySampleCtMap.put(key, 0);
			else
				featureBySampleCtMap.put(key, valuesForFeature.size());
		}
		return featureBySampleCtMap;
	}
	
	private Map<String, List<Double>> loadMassValueMap(TextFile txtData, boolean justPools) {
		return loadValueMap(txtData, justPools, false, true);
	}

	private Map<String, List<Double>> loadValueMap(TextFile txtData, boolean justPools, boolean loadRts) {
		return loadValueMap(txtData, justPools, true, false);
	}

	private Map<String, List<Double>> loadValueMap(TextFile txtData, boolean justPools, boolean loadRts,
			boolean loadMasses) {

		Map<String, List<Double>> valueMap = new HashMap<String, List<Double>>();

		int rowEnd = txtData.getEndRowIndex() + 1;
		List<String> rowContents = null;
		String value;

		String currFeatureName = null;

		for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

			rowContents = txtData.getRawStringRow(rowNum);
			if (rowContents == null || rowContents.size() < 2)
				continue;

			if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
				continue;

			for (int col = 0; col < rowContents.size(); col++) {

				value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

				if (col == this.featureNameCol) {

					if (!StringUtils.isEmptyOrNull(value)) {

						currFeatureName = value;
						valueMap.put(currFeatureName, new ArrayList<Double>());
					}
					break;
				}
			}

			Double dblValue = null;

			if (loadRts) {
				for (int i = 0; i < rtHeaderdIndices.size(); i++) {

					value = StringUtils.removeNonPrintable(rowContents.get(rtHeaderdIndices.get(i)).trim());
					try {
						dblValue = Double.parseDouble(value);
					} catch (Exception e) {
						continue;
					}

					valueMap.get(currFeatureName).add(dblValue);
				}
			} else if (loadMasses) {
				for (int i = 0; i < massHeaderIndices.size(); i++) {

					value = StringUtils.removeNonPrintable(rowContents.get(massHeaderIndices.get(i)).trim());
					try {
						dblValue = Double.parseDouble(value);
					} catch (Exception e) {
						continue;
					}

					valueMap.get(currFeatureName).add(dblValue);
				}
			} else {
				for (int i = 0; i < intensityHeaderIndices.size(); i++) {

					value = StringUtils.removeNonPrintable(rowContents.get(rtHeaderdIndices.get(i)).trim());
					try {
						dblValue = Double.parseDouble(value);
					} catch (Exception e) {
						continue;
					}

					valueMap.get(currFeatureName).add(dblValue);
				}

			}
		}
		return valueMap;
	}

	private void locateRTandIntensityColumns(boolean justPools, TextFile txtData) {

		rtHeaderdIndices = new ArrayList<Integer>();
		rtHeaders = new ArrayList<String>();

		massHeaderIndices = new ArrayList<Integer>();
		massHeaders = new ArrayList<String>();

		int rowStart = 0, rowEnd = txtData.getEndRowIndex() + 1;
		boolean firstNonBlankHeaderFound = false;
		boolean readingRTs = false, foundIons = false, readingIntensities = false, readingMasses = false;
		String value;
		List<String> rowContents = null;

		try {
			for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);
				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				int nConsecutiveBlanks = 0;
				for (int col = 0; col < rowContents.size(); col++) {

					value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

					if (!firstNonBlankHeaderFound && StringUtils.checkEmptyOrNull(value))
						continue;

					firstNonBlankHeaderFound = true;

					if (PostProccessConstants.COMPOUND_CHOICES_ARRAY.contains(value.toLowerCase()))
						featureNameCol = col;

					if (value.startsWith("Ion")) {
						foundIons = true;
						continue;
					}

					if (StringUtils.checkEmptyOrNull(value)) {
						nConsecutiveBlanks++;
						continue;
					} else {
						nConsecutiveBlanks = 0;
					}

					if (!foundIons)
						continue;

					readingRTs = value.startsWith("[RT");
					readingIntensities = value.startsWith("[Area");
					readingMasses = value.startsWith("[Mass");

					if (!readingRTs && !readingIntensities && !readingMasses)
						continue;

					if (readingRTs) {
						if (justPools && !value.contains("0MP"))
							continue;
						
						rtHeaderdIndices.add(col);
						rtHeaders.add(value);
					}

					if (readingMasses) {
						if (justPools && !value.contains("0MP")) 
							continue;

						massHeaderIndices.add(col);
						massHeaders.add(value);
					}

					if (readingIntensities) {
						if (justPools && !value.contains("0MP"))
							continue;
						
						intensityHeaderIndices.add(col);
						intensityHeaders.add(value);
					}
					if (nConsecutiveBlanks > 4)
						break;

					dataStart = rowNum + 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public int getFeatureNameCol() {
		return featureNameCol;
	}

	public void setFeatureNameCol(int featureNameCol) {
		this.featureNameCol = featureNameCol;
	}
}
