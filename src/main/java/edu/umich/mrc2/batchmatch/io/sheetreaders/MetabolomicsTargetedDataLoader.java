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

	int featureNameCol, dataStart;
	List<Integer> rtHeaderdIndices = new ArrayList<Integer>();
	List<String> rtHeaders = new ArrayList<String>();

	List<Integer> intensityHeaderIndices = new ArrayList<Integer>();
	List<String> intensityHeaders = new ArrayList<String>();

	List<Integer> massHeaderIndices = new ArrayList<Integer>();
	List<String> massHeaders = new ArrayList<String>();

	private TextFile loadData(String fileName) {

		File inputFile = new File(fileName);
		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// Map<String, List<Double>> values = locateRTsAndLoadValues(rawTextData,
		// justPools);
		return rawTextData;
	}

	public Map<String, List<Double>> locateAndLoadValues(String fileName, Boolean justPools, Boolean loadRts) {

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

	/*
	 * public Map<String, List<Double>> locateAndLoadMassValues(String fileName,
	 * Boolean justPools, Boolean loadMasses) {
	 * 
	 * TextFile txtData = loadData(fileName); if (txtData == null) return null;
	 * 
	 * Map<String, List<Double>> valueMap = new HashMap<String, List<Double>>();
	 * 
	 * locateRTandIntensityColumns(justPools, txtData);
	 * 
	 * valueMap = loadValueMap(txtData, justPools, false, true);
	 * 
	 * return valueMap; }
	 */

	public Map<String, Double> locateMassesAndLoadAverageMap(String fileName, Boolean justPools) {

		return locateValuesAndLoadAverageMap(fileName, justPools, false);
	}
	/*
	 * Map<String, List<Double>> valueMap = locateAndLoadMassValues(fileName,
	 * justPools, true);
	 * 
	 * Map<String, Double> featureByAvgMassMap = new HashMap<String, Double>();
	 * Double avgValue = null; for (String key : valueMap.keySet()) {
	 * 
	 * List<Double> valuesForFeature = valueMap.get(key);
	 * 
	 * if (valuesForFeature != null && valuesForFeature.size() > 0) { avgValue =
	 * 0.0;
	 * 
	 * for (int i = 0; i < valuesForFeature.size(); i++) avgValue +=
	 * valuesForFeature.get(i);
	 * 
	 * avgValue /= (1.0 * valuesForFeature.size());
	 * 
	 * featureByAvgMassMap.put(key, avgValue); } } return featureByAvgMassMap; }
	 */

	public Map<String, Double> locateRTsAndLoadAverageMap(String fileName, Boolean justPools) {

		return locateValuesAndLoadAverageMap(fileName, justPools, true);
	}
	/*
	 * Map<String, List<Double>> valueMap = locateAndLoadValues(fileName, justPools,
	 * true);
	 * 
	 * Map<String, Double> featureByAvgRTMap = new HashMap<String, Double>(); Double
	 * avgValue = null; for (String key : valueMap.keySet()) {
	 * 
	 * List<Double> valuesForFeature = valueMap.get(key);
	 * 
	 * if (valuesForFeature != null && valuesForFeature.size() > 0) { avgValue =
	 * 0.0;
	 * 
	 * for (int i = 0; i < valuesForFeature.size(); i++) avgValue +=
	 * valuesForFeature.get(i);
	 * 
	 * avgValue /= (1.0 * valuesForFeature.size());
	 * 
	 * featureByAvgRTMap.put(key, avgValue); } } return featureByAvgRTMap; }
	 */

	public Map<String, Double> locateValuesAndLoadAverageMap(String fileName, Boolean justPools, Boolean useRts) {

		// Map<String, List<Double>> valueMap = null;
		// if (useRts)
		// valueMap = locateAndLoadValues(fileName, justPools, useRts);
		// else
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

	public Map<String, Integer> locateRTsAndLoadSampleCtMap(String fileName, Boolean justPools) {

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

	/*
	 * //for (Double val : allEntries) { if (val == null || val < 0.0) continue;
	 * mean += val; sqTot += (val * val); nNonMissing++; } mean /= nNonMissing;
	 * stDev = Math.sqrt(sqTot/(nNonMissing - 1) - mean * mean);
	 */

	public Map<String, Double> locateMassesAndLoadStdDevMap(String fileName, Boolean justPools) {
		return locateValuesAndLoadStdDevMap(fileName, justPools, false);
	}

	public Map<String, Double> locateRTsAndLoadStdDevMap(String fileName, Boolean justPools) {
		return locateValuesAndLoadStdDevMap(fileName, justPools, true);
	}

	public Map<String, Double> locateValuesAndLoadStdDevMap(String fileName, Boolean justPools, Boolean useRts) {

		Map<String, List<Double>> valueMap = locateAndLoadValues(fileName, justPools, useRts); // null;
		// if (useRts)
		// valueMap = locateAndLoadValues(fileName, justPools, useRts);
		// else
		// valueMap = locateAndLoadMassValues(fileName, justPools, false);

		Map<String, Double> featureByAvgValueMap = new HashMap<String, Double>();
		Double avgValue = null, sqValue = null;
		for (String key : valueMap.keySet()) {

			List<Double> valuesForFeature = valueMap.get(key);

			if (valuesForFeature != null && valuesForFeature.size() > 0) {
				avgValue = 0.0;
				sqValue = 0.0;

				for (int i = 0; i < valuesForFeature.size(); i++) {
					avgValue += valuesForFeature.get(i);
					sqValue += (valuesForFeature.get(i) * valuesForFeature.get(i));
				}

				Double nValues = 1.0 * valuesForFeature.size();

				avgValue /= (1.0 * valuesForFeature.size());

				Double stDev = Math.sqrt(sqValue / (nValues - 1) - avgValue * avgValue);

				featureByAvgValueMap.put(key, stDev);
			}
		}
		return featureByAvgValueMap;
	}
	/*
	 * // public Map<Double, String> locateRTsAndLoadAvgToFeatureNameMap(String
	 * fileName, Boolean justPools) { // return
	 * locateValuesAndLoadAvgToFeatureNameMap(fileName, justPools, true); }
	 */
	/*
	 * // public Map<Double, String> locateMassesAndLoadAvgToFeatureNameMap(String
	 * fileName, Boolean justPools) { // return
	 * locateValuesAndLoadAvgToFeatureNameMap(fileName, justPools, false); }
	 */

	/*
	 * // private Map<Double, String> locateValuesAndLoadAvgToFeatureNameMap(String
	 * fileName, Boolean justPools, Boolean useRts) {
	 * 
	 * Map<String, List<Double>> valueMap = locateAndLoadValues(fileName, justPools,
	 * useRts); //null;
	 * 
	 * // if (useRts) // valueMap = locateAndLoadValues(fileName, justPools,
	 * useRts);/ // else // valueMap = locateAndLoadMassValues(fileName, justPools,
	 * false);
	 * 
	 * // Map<Double, String> avgRTByFeatureNameMap = new HashMap<Double, String>();
	 * Double avgValue = null; for (String key : valueMap.keySet()) {
	 * 
	 * List<Double> valuesForFeature = valueMap.get(key);
	 * 
	 * if (valuesForFeature != null && valuesForFeature.size() > 0) { avgValue =
	 * 0.0;
	 * 
	 * for (int i = 0; i < valuesForFeature.size(); i++) { avgValue +=
	 * valuesForFeature.get(i); }
	 * 
	 * Double nValues = 1.0 * valuesForFeature.size();
	 * 
	 * avgValue /= (1.0 * valuesForFeature.size());
	 * 
	 * avgRTByFeatureNameMap.put(avgValue, key); } } return avgRTByFeatureNameMap; }
	 */
	/*
	 * 
	 * public void printMassDiagnostics(String fileName, Boolean useRts) {
	 * ///{Map<String,Double> avgValues, Map<String, Double> stdDevValues) {
	 * 
	 * Map<String, Double> featureAllAverages = null, featurePoolAverages = null;
	 * Map<String, Double> featureAllStdDevs = null, featurePoolStdDevs = null;
	 * 
	 * 
	 * if (useRts) { featureAllAverages = locateRTsAndLoadAverageMap(fileName,
	 * false); featurePoolAverages = locateRTsAndLoadAverageMap(fileName, true);
	 * featureAllStdDevs= locateRTsAndLoadStdDevMap(fileName, false);
	 * featurePoolStdDevs = locateRTsAndLoadStdDevMap(fileName, true); } else {
	 * featureAllAverages = locateMassesAndLoadAverageMap(fileName, false);
	 * featurePoolAverages = locateMassesAndLoadAverageMap(fileName, true);
	 * featureAllStdDevs= locateMassesAndLoadStdDevMap(fileName, false);
	 * featurePoolStdDevs = locateMassesAndLoadStdDevMap(fileName, true); }
	 * 
	 * Double poolStd = null, allStd= null, poolAvg = null, allAvg = null;
	 * List<String> missingFeatureNames = new ArrayList<String>();
	 * 
	 * //Map<Double, String> avgRTByFeatureNameMap =
	 * locateRTsAndLoadAvgToFeatureNameMap(fileName, false);
	 * 
	 * System.out.println("Targeted Diagnostics for " + fileName);
	 * 
	 * String header = String.format("%-70s %10s %10s %10s %10s,%10s",
	 * "Feature Name", "All Avg", "All Std", "Pool Avg", "Pool Std","Diff");
	 * 
	 * System.out.println(header); System.out.println(String.format("%120s",
	 * "---------------------------------------------------------------------------------------------------------------------------------------------------------"
	 * ));
	 * 
	 * 
	 * List<Double> orderedRTKeys =
	 * ListUtils.makeListFromCollection(avgRTByFeatureNameMap.keySet());
	 * Collections.sort(orderedRTKeys);
	 * 
	 * //for (String featureName : featureAllAverages.keySet()) { for (int i = 0; i
	 * < orderedRTKeys.size(); i++) {
	 * 
	 * String featureName = avgRTByFeatureNameMap.get(orderedRTKeys.get(i));
	 * 
	 * allAvg = featureAllAverages.get(featureName); allStd = poolAvg = poolStd =
	 * null;
	 * 
	 * if (featurePoolAverages.containsKey(featureName)) { poolAvg
	 * =featurePoolAverages.get(featureName); }
	 * 
	 * if (featurePoolStdDevs.containsKey(featureName)) { poolStd =
	 * featurePoolStdDevs.get(featureName); }
	 * 
	 * if (featureAllStdDevs.containsKey(featureName)) { allStd =
	 * featureAllStdDevs.get(featureName); }
	 * 
	 * String allAvgStr = (allAvg == null ? "-" : String.format("%10.4f", allAvg));
	 * String poolAvgStr = (poolAvg == null ? "-" : String.format("%10.4f",
	 * poolAvg)); String allStdStr = (allStd == null ? "-" : String.format("%10.4f",
	 * allStd)); String poolStdStr = (poolStd == null ? "-" :
	 * String.format("%10.4f", poolStd)); String diffAvgStr= "-", flagStr = ""; if
	 * (allAvg != null && poolAvg != null) { if (Math.abs(allAvg - poolAvg) >
	 * .00999999) flagStr = "**";
	 * 
	 * diffAvgStr = String.format("%10.4f",(allAvg - poolAvg)); }
	 * 
	 * String line = String.format("%-70s %10s (%10s) %10s (%10s) %10s%1s",
	 * featureName, allAvgStr, allStdStr, poolAvgStr, poolStdStr, diffAvgStr,
	 * flagStr);
	 * 
	 * System.out.println(line); } }
	 */
	/*
	 * public void printTargetedDiagnostics(String fileName) {
	 * ///{Map<String,Double> avgValues, Map<String, Double> stdDevValues) {
	 * 
	 * Map<String, Double> featureAllAverages = locateRTsAndLoadAverageMap(fileName,
	 * false); Map<String, Double> featurePoolAverages =
	 * locateRTsAndLoadAverageMap(fileName, true);
	 * 
	 * Map<String, Double> featureAllStdDevs= locateRTsAndLoadStdDevMap(fileName,
	 * false); Map<String, Double> featurePoolStdDevs =
	 * locateRTsAndLoadStdDevMap(fileName, true);
	 * 
	 * Double poolStd = null, allStd= null, poolAvg = null, allAvg = null;
	 * List<String> missingFeatureNames = new ArrayList<String>();
	 * 
	 * // Map<Double, String> avgRTByFeatureNameMap =
	 * locateRTsAndLoadAvgToFeatureNameMap(fileName, false);
	 * 
	 * System.out.println("Targeted Diagnostics for " + fileName);
	 * 
	 * String header = String.format("%-70s %10s %10s %10s %10s,%10s",
	 * "Feature Name", "All Avg", "All Std", "Pool Avg", "Pool Std","Diff");
	 * 
	 * System.out.println(header); System.out.println(String.format("%120s",
	 * "---------------------------------------------------------------------------------------------------------------------------------------------------------"
	 * ));
	 * 
	 * 
	 * List<Double> orderedRTKeys =
	 * ListUtils.makeListFromCollection(avgRTByFeatureNameMap.keySet());
	 * Collections.sort(orderedRTKeys);
	 * 
	 * //for (String featureName : featureAllAverages.keySet()) { for (int i = 0; i
	 * < orderedRTKeys.size(); i++) {
	 * 
	 * String featureName = avgRTByFeatureNameMap.get(orderedRTKeys.get(i));
	 * 
	 * allAvg = featureAllAverages.get(featureName); allStd = poolAvg = poolStd =
	 * null;
	 * 
	 * if (featurePoolAverages.containsKey(featureName)) { poolAvg
	 * =featurePoolAverages.get(featureName); }
	 * 
	 * if (featurePoolStdDevs.containsKey(featureName)) { poolStd =
	 * featurePoolStdDevs.get(featureName); }
	 * 
	 * if (featureAllStdDevs.containsKey(featureName)) { allStd =
	 * featureAllStdDevs.get(featureName); }
	 * 
	 * String allAvgStr = (allAvg == null ? "-" : String.format("%10.4f", allAvg));
	 * String poolAvgStr = (poolAvg == null ? "-" : String.format("%10.4f",
	 * poolAvg)); String allStdStr = (allStd == null ? "-" : String.format("%10.4f",
	 * allStd)); String poolStdStr = (poolStd == null ? "-" :
	 * String.format("%10.4f", poolStd)); String diffAvgStr= "-", flagStr = ""; if
	 * (allAvg != null && poolAvg != null) { if (Math.abs(allAvg - poolAvg) >
	 * .00999999) flagStr = "**";
	 * 
	 * diffAvgStr = String.format("%10.4f",(allAvg - poolAvg)); }
	 * 
	 * String line = String.format("%-70s %10s (%10s) %10s (%10s) %10s%1s",
	 * featureName, allAvgStr, allStdStr, poolAvgStr, poolStdStr, diffAvgStr,
	 * flagStr);
	 * 
	 * System.out.println(line); } }
	 */

	private Map<String, List<Double>> loadMassValueMap(TextFile txtData, Boolean justPools) {
		return loadValueMap(txtData, justPools, false, true);
	}

	private Map<String, List<Double>> loadValueMap(TextFile txtData, Boolean justPools, Boolean loadRts) {
		return loadValueMap(txtData, justPools, true, false);
	}

	private Map<String, List<Double>> loadValueMap(TextFile txtData, Boolean justPools, Boolean loadRts,
			Boolean loadMasses) {

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

			int nConsecutiveBlanks = 0;
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

	private void locateRTandIntensityColumns(Boolean justPools, TextFile txtData) {

		rtHeaderdIndices = new ArrayList<Integer>();
		rtHeaders = new ArrayList<String>();

		massHeaderIndices = new ArrayList<Integer>();
		massHeaders = new ArrayList<String>();

		int rowStart = 0, rowEnd = txtData.getEndRowIndex() + 1;
		Boolean firstNonBlankHeaderFound = false;
		Boolean readingRTs = false, foundIons = false, readingIntensities = false, readingMasses = false;
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
						if (justPools) {
							if (!value.contains("0MP"))
								continue;
							// else
							// System.out.println("Reading value" + value);
						}

						rtHeaderdIndices.add(col);
						rtHeaders.add(value);
					}

					if (readingMasses) {
						if (justPools) {
							if (!value.contains("0MP"))
								continue;
							// else
							// System.out.println("Reading value" + value);
						}

						massHeaderIndices.add(col);
						massHeaders.add(value);
					}

					if (readingIntensities) {
						if (justPools) {
							if (!value.contains("0MP"))
								continue;
							// else
							// System.out.println("Reading value" + value);
						}

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

		// for (int i = 0; i < rtHeaders.size(); i++) {
		// System.out.println("RT Header is" + rtHeaders.get(i));
		// }
	}

	/*
	 * private Map<String, List<FeatureFromFile>>
	 * makeTargetListForBatch(PostProcessDataSet dataSet, Integer targetBatch) {
	 * 
	 * Map<String, List<FeatureFromFile>> featureNamesForBatch = new HashMap<String,
	 * List<FeatureFromFile>>();
	 * 
	 * for (FeatureFromFile feature : dataSet.getFeatures()) if
	 * (feature.getBatchIdx().equals(targetBatch)) { String fn =
	 * feature.getName().trim(); if (!featureNamesForBatch.containsKey(fn))
	 * featureNamesForBatch.put(fn, new ArrayList<FeatureFromFile>());
	 * featureNamesForBatch.get(fn).add(feature); }
	 * 
	 * return featureNamesForBatch; }
	 */

	/*
	 * private void fillIntensitiesForBatch(Map<String, List<FeatureFromFile>>
	 * targetFeatures, String fileName, PostProcessDataSet dataSet) {
	 * 
	 * File inputFile = new File(fileName); System.out.println("Opening file " +
	 * fileName); TextFile rawTextData = new TextFile(); try {
	 * rawTextData.open(inputFile); } catch (Exception e) { e.printStackTrace();
	 * return ; //false; } locateFeaturesAndCompleteIntensityData(rawTextData,
	 * targetFeatures, dataSet); }
	 */
	/*
	 * public List<String> preReadCommonHeaders(Map<Integer, String> batchFileMap) {
	 * 
	 * List<Integer> intensityHeaderIndices = new ArrayList<Integer>(); List<String>
	 * intensityHeaders = new ArrayList<String>();
	 * 
	 * for (Integer batchNo : batchFileMap.keySet()) {
	 * 
	 * String fileName = batchFileMap.get(batchNo); File inputFile = new
	 * File(fileName);
	 * 
	 * TextFile rawTextData = new TextFile(); try { rawTextData.open(inputFile); }
	 * catch (Exception e) { e.printStackTrace(); return new ArrayList<String>();
	 * //false; }
	 * 
	 * 
	 * preReadRegularHeadersForFile(rawTextData, intensityHeaderIndices,
	 * intensityHeaders); } return intensityHeaders; }
	 */

	public List<String> preReadIntensityHeaders(Map<Integer, String> batchFileMap) {

		List<Integer> intensityHeaderIndices = new ArrayList<Integer>();
		List<String> intensityHeaders = new ArrayList<String>();

		for (Integer batchNo : batchFileMap.keySet()) {

			String fileName = batchFileMap.get(batchNo);
			File inputFile = new File(fileName);

			TextFile rawTextData = new TextFile();
			try {
				rawTextData.open(inputFile);
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<String>(); // false;
			}
			preReadIntensityHeadersForFile(rawTextData, intensityHeaderIndices, intensityHeaders,
					new ArrayList<Integer>(), new ArrayList<String>());
		}
		return intensityHeaders;
	}
	/*
	 * private int preReadRegularHeadersForFile(TextFile txtData, List<Integer>
	 * intensityHeaderIndices, List<String> intensityHeaders) {
	 * 
	 * StringBuilder sb = new StringBuilder(), sb2 = new StringBuilder();
	 * List<Integer> newIntensityHeaderIndices = new ArrayList<Integer>();
	 * List<String> newIntensityHeaders = new ArrayList<String>();
	 * 
	 * int dataStart = 0, rowStart = 0,rowEnd = txtData.getEndRowIndex() + 1 ;
	 * Boolean firstNonBlankHeaderFound = false; Boolean readingIntensities = false;
	 * String value; Vector<String> rowContents = null;
	 * 
	 * try { for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
	 * 
	 * rowContents = txtData.getRawStringRow(rowNum); if (rowContents == null ||
	 * rowContents.size() < 2) continue;
	 * 
	 * if (newIntensityHeaders.size() > 1) break;
	 * 
	 * if (StringUtils.isEmptyOrNull(rowContents.get(0)) &&
	 * StringUtils.isEmptyOrNull(rowContents.get(1))) continue;
	 * 
	 * sb.setLength(0); //(0, sb.length()- 1); sb2.setLength(0); for (int col = 0;
	 * col < rowContents.size(); col++) {
	 * 
	 * value = rowContents.get(col); sb.append(rowContents.get(col) + ","); if
	 * (value != null && value.trim().startsWith(".") && value.length() < 3) {
	 * rowContents.set(col, ""); } sb2.append(rowContents.get(col) + ","); }
	 * 
	 * int nConsecutiveBlanks = 0 ; for (int col = 0; col < rowContents.size();
	 * col++) {
	 * 
	 * // Fixes Index issue value =
	 * StringUtils.removeNonPrintable(rowContents.get(col).trim());
	 * 
	 * if (!firstNonBlankHeaderFound && StringUtils.checkEmptyOrNull(value))
	 * continue;
	 * 
	 * firstNonBlankHeaderFound = true;
	 * 
	 * //if (StringUtils.isEmptyOrNull(value)) { if ("Charge".equals(value)) {
	 * readingIntensities = true; continue; }
	 * 
	 * if
	 * (PostProccessConstants.COMPOUND_CHOICES_ARRAY.contains(value.toLowerCase()))
	 * featureNameCol = col;
	 * 
	 * if (readingIntensities) { newIntensityHeaderIndices.add(col);
	 * newIntensityHeaders.add(value); }
	 * 
	 * if (nConsecutiveBlanks > 4) break;
	 * 
	 * dataStart = rowNum + 1; } } } catch (Exception e) { e.printStackTrace();
	 * throw e; }
	 * 
	 * if (newIntensityHeaders.size() < 1) JOptionPane.showMessageDialog(null,
	 * "Warning : No intensity values were found in your file.  Although any text found in your report will be mirrored,"
	 * + BatchMatchConstants.LINE_SEPARATOR +
	 * "to the processed output, any actual intensities will not be recognized as numeric and no shading for outliers"
	 * + BatchMatchConstants.LINE_SEPARATOR +
	 * "or missingness will be carried over. The intensity section of your Binner report "
	 * + BatchMatchConstants.LINE_SEPARATOR +
	 * "should be preceded by a single blank column.");
	 * 
	 * int startCol = 0;
	 * 
	 * for (int col = 0; col < newIntensityHeaders.size(); col++) {
	 * intensityHeaders.add(newIntensityHeaders.get(col));
	 * intensityHeaderIndices.add(startCol + newIntensityHeaderIndices.get(col)); }
	 * return dataStart; }
	 */

	private int preReadIntensityHeadersForFile(TextFile txtData, List<Integer> intensityHeaderIndices,
			List<String> intensityHeaders, List<Integer> regularHeaderIndices, List<String> regularHeaders) {

		if (regularHeaderIndices != null)
			regularHeaderIndices.clear();
		else
			regularHeaderIndices = new ArrayList<Integer>();

		if (regularHeaders != null)
			regularHeaders.clear();
		else
			regularHeaders = new ArrayList<String>();

		StringBuilder sb = new StringBuilder(), sb2 = new StringBuilder();
		List<Integer> newIntensityHeaderIndices = new ArrayList<Integer>();
		List<String> newIntensityHeaders = new ArrayList<String>();

		int dataStart = 0, rowStart = 0, rowEnd = txtData.getEndRowIndex() + 1;
		Boolean firstNonBlankHeaderFound = false;
		Boolean readingIntensities = false;
		String value;
		List<String> rowContents = null;

		try {
			for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);
				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (newIntensityHeaders.size() > 1)
					break;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				sb.setLength(0); // (0, sb.length()- 1);
				sb2.setLength(0);
				for (int col = 0; col < rowContents.size(); col++) {

					value = rowContents.get(col);
					sb.append(rowContents.get(col) + ",");
					if (value != null && value.trim().startsWith(".") && value.length() < 3) {
						rowContents.set(col, "");
					} else if (value != null && value.trim().toLowerCase().startsWith("na")) {
						rowContents.set(col, "");
					}
					sb2.append(rowContents.get(col) + ",");
				}

				int nConsecutiveBlanks = 0;
				for (int col = 0; col < rowContents.size(); col++) {

					// Fixes Index issue
					value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

					if (!firstNonBlankHeaderFound && StringUtils.checkEmptyOrNull(value))
						continue;

					firstNonBlankHeaderFound = true;

					// if (StringUtils.isEmptyOrNull(value)) {
					if ("Charge".equals(value)) {
						readingIntensities = true;
						continue;
					}

					if (PostProccessConstants.COMPOUND_CHOICES_ARRAY.contains(value.toLowerCase()))
						featureNameCol = col;

					if (readingIntensities) {
						newIntensityHeaderIndices.add(col);
						newIntensityHeaders.add(value);
					} else {
						regularHeaderIndices.add(col);
						regularHeaders.add(value);
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

		// if (newIntensityHeaders.size() < 1)
		// JOptionPane.showMessageDialog(null, "Warning : No intensity values were found
		// in your file. Although any text found in your report will be mirrored,"
		// + BatchMatchConstants.LINE_SEPARATOR + "to the processed output, any actual
		// intensities will not be recognized as numeric and no shading for outliers"
		// + BatchMatchConstants.LINE_SEPARATOR + "or missingness will be carried over.
		// The intensity section of your Binner report "
		// + BatchMatchConstants.LINE_SEPARATOR + "should be preceded by a single blank
		// column.");

		int startCol = 0;

		for (int col = 0; col < newIntensityHeaders.size(); col++) {
			intensityHeaders.add(newIntensityHeaders.get(col));
			intensityHeaderIndices.add(startCol + newIntensityHeaderIndices.get(col));
		}
		return dataStart;
	}

	/*
	 * private void locateFeaturesAndCompleteIntensityData(TextFile txtData,
	 * Map<String, List<FeatureFromFile>> targetFeaturesMap, PostProcessDataSet
	 * featureData) {
	 * 
	 * Vector<String> rowContents = null; String value;
	 * 
	 * List<Integer> intensityHeaderIndices = new ArrayList<Integer>(); List<String>
	 * intensityHeaders = new ArrayList<String>();
	 * 
	 * List<Integer> regularHeaderIndices = new ArrayList<Integer>(); List<String>
	 * regularHeaders= new ArrayList<String>(); //Integer fnInt = -1; int dataStart
	 * = preReadIntensityHeadersForFile(txtData, intensityHeaderIndices,
	 * intensityHeaders, regularHeaderIndices, regularHeaders);
	 * 
	 * for (int col = 0; col < intensityHeaders.size(); col++) { if
	 * (StringUtils.isEmptyOrNull( intensityHeaders.get(col))) continue;
	 * System.out.println("Headeere " + intensityHeaders.get(col)); if
	 * (PostProccessConstants.COMPOUND_CHOICES_ARRAY2.contains(
	 * intensityHeaders.get(col).toLowerCase())) { featureNameCol = col; break; } }
	 * 
	 * featureMeasurementCol = -1; for (int col = 0; col < regularHeaders.size();
	 * col++) { if (StringUtils.isEmptyOrNull( regularHeaders.get(col))) continue;
	 * System.out.println("Regular Header " + regularHeaders.get(col)); if
	 * (PostProccessConstants.SPECIAL_MASS_MEASUREMENT_CHOICES_ARRAY.contains(
	 * StringUtils.removeSpaces(regularHeaders.get(col).toLowerCase()))) {
	 * this.massMeasurementCol = col; break; } }
	 * 
	 * int otherMassCol = -1; for (int col = 0; col < regularHeaders.size(); col++)
	 * { if (StringUtils.isEmptyOrNull( regularHeaders.get(col))) continue;
	 * System.out.println("Regular Header " + intensityHeaders.get(col)); if
	 * (PostProccessConstants.MASS_CHOICES_ARRAY.contains(StringUtils.removeSpaces(
	 * regularHeaders.get(col).toLowerCase()))) { otherMassCol = col; break; } }
	 * 
	 * try { String keyForIntensity; int rowEnd = txtData.getEndRowIndex() + 1; for
	 * (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {
	 * 
	 * rowContents = txtData.getRawStringRow(rowNum);
	 * 
	 * if (rowContents == null || rowContents.size() < 2) continue;
	 * 
	 * if (StringUtils.isEmptyOrNull(rowContents.get(0)) &&
	 * StringUtils.isEmptyOrNull(rowContents.get(1))) continue;
	 * 
	 * String fn = rowContents.get(featureNameCol);
	 * 
	 * if (StringUtils.isEmptyOrNull(fn)) continue;
	 * 
	 * 
	 * fn = fn.trim();
	 * 
	 * if (!targetFeaturesMap.containsKey(fn)) continue;
	 * 
	 * List<FeatureFromFile> candidateFeatures = targetFeaturesMap.get(fn);
	 * 
	 * if (candidateFeatures == null || candidateFeatures.size() < 1) continue;
	 * 
	 * String mass = null; if (this.massMeasurementCol != -1) mass =
	 * rowContents.get(massMeasurementCol); else if (otherMassCol != -1) mass =
	 * rowContents.get(otherMassCol);
	 * 
	 * Double massDbl = null; try { massDbl = Double.parseDouble(mass); } catch
	 * (Exception e) { continue; }
	 * 
	 * FeatureFromFile targetFeature = null; for (int i = 0; i <
	 * candidateFeatures.size(); i++) { if (Math.abs(massDbl -
	 * candidateFeatures.get(i).getMass()) < 1.0) { targetFeature =
	 * candidateFeatures.get(i); } }
	 * 
	 * if (targetFeature == null) continue;
	 * 
	 * for (int i = 0; i < intensityHeaderIndices.size(); i++) { value =
	 * rowContents.get(intensityHeaderIndices.get(i));
	 * 
	 * keyForIntensity = intensityHeaders.get(i); if
	 * (!StringUtils.isEmptyOrNull(keyForIntensity)) keyForIntensity =
	 * StringUtils.removeSpaces(intensityHeaders.get(i)).toLowerCase(); else
	 * keyForIntensity = "";
	 * 
	 * //if (featureFromFile.getBatchIdx().equals(2) &&
	 * keyForIntensity.startsWith("CS00000MP-01")) //
	 * System.out.println("Adding intensity for " + featureFromFile.getName() + " "
	 * + " key " + keyForIntensity + " value " + value);
	 * 
	 * targetFeature.getIntensityValuesByHeaderMap().put(keyForIntensity, value); }
	 * } }// rowNumLoop catch (Exception e) { e.printStackTrace(); return; }
	 * featureData.updateForNewIntensityColumns(intensityHeaders); }
	 */
	public int getFeatureNameCol() {
		return featureNameCol;
	}

	public void setFeatureNameCol(int featureNameCol) {
		this.featureNameCol = featureNameCol;
	}
}
