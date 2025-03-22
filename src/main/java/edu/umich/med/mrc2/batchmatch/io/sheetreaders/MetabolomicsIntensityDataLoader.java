////////////////////////////////////////////////////
// MetabolomicsIntensityDataLoader.java
// Written by Jan Wigginton and Bill Duren, November 2019
//////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.io.sheetreaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.med.mrc2.batchmatch.main.PostProccessConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class MetabolomicsIntensityDataLoader {

	private int featureNameCol, massMeasurementCol;
	private Boolean requireRtValue = false;

	public MetabolomicsIntensityDataLoader() {
	}

	public void completeDataSetFromBatchFilesList(PostProcessDataSet dataSet, Map<Integer, String> batchFileMap,
			List<String> selectedSamplesForRSD) {

		// System.out.println();
		// System.out.println("Main loop making target lists from data read in from
		// report");
		// System.out.println("----------------------");
		// System.out.println();
		for (Integer batchNo : batchFileMap.keySet()) {
			Map<String, List<FeatureFromFile>> targetList = makeFeatureTargetListForBatch(dataSet, batchNo);
			fillIntensitiesForBatch(targetList, batchFileMap.get(batchNo), dataSet, batchNo);
		}

		// read available cols (by batch), then filter map by those selected for RSD
		Map<Integer, List<String>> colsByBatch = grabBatchwiseIntensityHeaders(dataSet, batchFileMap);

		String colName = null;
		Map<Integer, List<String>> filteredColsByBatch = new HashMap<Integer, List<String>>();
		for (Integer batchNo : colsByBatch.keySet()) {

			if (!filteredColsByBatch.containsKey(batchNo))
				filteredColsByBatch.put(batchNo, new ArrayList<String>());

			for (int i = 0; i < colsByBatch.get(batchNo).size(); i++) {
				colName = colsByBatch.get(batchNo).get(i);
				if (selectedSamplesForRSD != null && selectedSamplesForRSD.contains(colName)) // colsByBatch.get(batchNo)))
					filteredColsByBatch.get(batchNo).add(colName);
			}
		}

		dataSet.setColsForRSDByBatch(filteredColsByBatch);
	}

	public Map<Integer, List<String>> grabBatchwiseIntensityHeaders(PostProcessDataSet dataSet,
			Map<Integer, String> batchFileMap) {

		Map<Integer, List<String>> intensityHeadersByBatchMap = new HashMap<Integer, List<String>>();

		// System.out.println();
		// System.out.println("Main loop initializing intensity headers from data read
		// in from report");
		// System.out.println("----------------------");
		// System.out.println();

		for (Integer batchNo : batchFileMap.keySet()) {

			Map<String, List<FeatureFromFile>> targetList = makeFeatureTargetListForBatch(dataSet, batchNo);
			List<String> intensityHeadersForBatch = getIntensityHeadersForBatch(targetList, batchFileMap.get(batchNo),
					dataSet);
			intensityHeadersByBatchMap.put(batchNo, intensityHeadersForBatch);
		}
		return intensityHeadersByBatchMap;
	}

	private Map<String, List<FeatureFromFile>> makeFeatureTargetListForBatch(PostProcessDataSet dataSet,
			Integer targetBatch) {

		Map<String, List<FeatureFromFile>> featureNamesForBatch = new HashMap<String, List<FeatureFromFile>>();

		// System.out.println("\n\n");
		// for (FeatureFromFile feature : dataSet.getFeatures()) {
		// System.out.println(feature.getName());
		// }
		// System.out.println("\n");

		// System.out.println("Making freature target lists by batch from merge report
		// data");
		for (FeatureFromFile feature : dataSet.getFeatures()) {
			if (feature.getBatchIdx().equals(targetBatch)) {
				String fn = feature.getName().trim();
				if (!featureNamesForBatch.containsKey(fn))
					featureNamesForBatch.put(fn, new ArrayList<FeatureFromFile>());
				featureNamesForBatch.get(fn).add(feature);
			}
		}

		// System.out.println();
		// System.out.println();
		// System.out.println("Target features for batch from established data set" +
		// targetBatch);
		// for (String f : featureNamesForBatch.keySet())
		// System.out.println(f);

		// System.out.println();
		// System.out.println();

		return featureNamesForBatch;
	}

	private void fillIntensitiesForBatch(Map<String, List<FeatureFromFile>> targetFeatures, String fileName,
			PostProcessDataSet dataSet, Integer batchNo) {

		File inputFile = new File(fileName);
		System.out.println("Opening file to fill in intensities " + fileName);
		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return; // false;
		}
		locateFeaturesAndCompleteIntensityData(rawTextData, targetFeatures, dataSet, batchNo);
	}

	private List<String> getIntensityHeadersForBatch(Map<String, List<FeatureFromFile>> targetFeatures, String fileName,
			PostProcessDataSet dataSet) {

		File inputFile = new File(fileName);
		System.out.println("Opening file " + fileName + " t0 preread intensity headers");
		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null; // false;
		}

		List<Integer> intensityHeaderIndices = new ArrayList<Integer>();
		List<String> intensityHeaders = new ArrayList<String>();

		List<Integer> regularHeaderIndices = new ArrayList<Integer>();
		List<String> regularHeaders = new ArrayList<String>();
		// Integer fnInt = -1;
		preReadIntensityHeadersForFile(rawTextData, intensityHeaderIndices, intensityHeaders, regularHeaderIndices,
				regularHeaders);

		return intensityHeaders;
	}

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
					}
					sb2.append(rowContents.get(col) + ",");
				}

				int nConsecutiveBlanks = 0;
				for (int col = 0; col < rowContents.size(); col++) {

					value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

					// System.out.println("Column name," + value + ",");
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

	// Called for data driven lattices. It locates master pool intensities and subs
	// the pool mean intensity
	// for feature intensity. It reads expected rt for rt. column 0 is the assumed
	// name column (although it is irrelevant
	// for lattice building

	public List<FeatureFromFile> grabFeaturesWithMeanMasterPoolForIntensity(TextFile txtData) {
		List<String> rowContents = null;
		String value;

		List<Integer> intensityHeaderIndices = new ArrayList<Integer>();
		List<String> intensityHeaders = new ArrayList<String>();

		List<Integer> regularHeaderIndices = new ArrayList<Integer>();
		List<String> regularHeaders = new ArrayList<String>();
		// Integer fnInt = -1;
		int dataStart = preReadIntensityHeadersForFile(txtData, intensityHeaderIndices, intensityHeaders,
				regularHeaderIndices, regularHeaders);

		featureNameCol = 0;
		for (int col = 0; col < intensityHeaders.size(); col++) {
			if (StringUtils.isEmptyOrNull(intensityHeaders.get(col)))
				continue;
			// System.out.println("Header " + intensityHeaders.get(col));
		}

		Integer rtMeasurementCol = null;
		for (int col = 0; col < regularHeaders.size(); col++) {
			if (StringUtils.isEmptyOrNull(regularHeaders.get(col)))
				continue;
			// System.out.println("Regular Header " + regularHeaders.get(col));
			if (PostProccessConstants.SPECIAL_EXPECTED_RETENTION_TIME_CHOICES_ARRAY
					.contains(StringUtils.removeSpaces(regularHeaders.get(col).toLowerCase()))) {
				rtMeasurementCol = col;
				break;
			}
		}

		// CS00000MP
		List<Integer> poolColumnIndices = new ArrayList<Integer>();
		for (int i = 0; i < intensityHeaders.size(); i++)
			if (intensityHeaders.get(i).contains(PostProccessConstants.MASTER_POOL_ID_FORMAT))
				poolColumnIndices.add(intensityHeaderIndices.get(i));

		List<FeatureFromFile> features = new ArrayList<FeatureFromFile>();

		try {
			// String keyForIntensity;
			int rowEnd = txtData.getEndRowIndex() + 1;
			for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);

				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				String fn = rowContents.get(featureNameCol);

				if (StringUtils.isEmptyOrNull(fn))
					continue;

				fn = fn.trim();

				String rt = null;
				if (rtMeasurementCol != -1)
					rt = rowContents.get(rtMeasurementCol);

				Double rtDbl = null;
				try {
					rtDbl = Double.parseDouble(rt);
				} catch (Exception e) {
					continue;
				}

				FeatureFromFile feature = new FeatureFromFile();
				feature.setName(fn);
				feature.setRT(rtDbl);

				Double valueDbl = null, avgPoolIntensity = 0.0;
				int nNonMissingIntensities = 0;
				for (int i = 0; i < poolColumnIndices.size(); i++) {
					value = rowContents.get(poolColumnIndices.get(i));

					valueDbl = null;
					try {
						valueDbl = Double.parseDouble(value);
					} catch (Exception e) {
						continue;
					}

					nNonMissingIntensities++;
					avgPoolIntensity += valueDbl;
				}

				if (nNonMissingIntensities == 0)
					avgPoolIntensity = Double.MIN_VALUE;

				else
					avgPoolIntensity /= nNonMissingIntensities;

				feature.setMedianIntensity(avgPoolIntensity);
				features.add(feature);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return features;
	}

	// 02/28/22 : Limited to assume Feature name, Monoisotopic M/Z and RT expected,
	// no break for intensities since we search on sample name tags
	private void locateFeaturesAndCompleteIntensityData(TextFile txtData,
			Map<String, List<FeatureFromFile>> targetFeaturesMap, PostProcessDataSet featureData, Integer batchNo) {

		List<String> rowContents = null;
		String value;

		List<Integer> intensityHeaderIndices = new ArrayList<Integer>();
		List<String> intensityHeaders = new ArrayList<String>();

		List<Integer> regularHeaderIndices = new ArrayList<Integer>();
		List<String> regularHeaders = new ArrayList<String>();

		int dataStart = preReadIntensityHeadersForFile(txtData, intensityHeaderIndices, intensityHeaders,
				regularHeaderIndices, regularHeaders);

		for (String header : intensityHeaders)
			System.out.println(header);

		String val = null, lcVal = null;
		for (int col = 0; col < intensityHeaders.size(); col++) {
			if (StringUtils.isEmptyOrNull(intensityHeaders.get(col)))
				continue;
			// System.out.println("Header " + intensityHeaders.get(col));
			lcVal = intensityHeaders.get(col).toLowerCase();
			val = StringUtils.removeNonPrintable(lcVal);
			if (PostProccessConstants.LIMITED_COMPOUND_CHOICES_ARRAY
					.contains(intensityHeaders.get(col).toLowerCase())) {
				featureNameCol = col;
				break;
			}
		}

		massMeasurementCol = -1;
		for (int col = 0; col < regularHeaders.size(); col++) {
			if (StringUtils.isEmptyOrNull(regularHeaders.get(col)))
				continue;
			// System.out.println("Regular Header " + regularHeaders.get(col));

			lcVal = regularHeaders.get(col).toLowerCase();
			val = StringUtils.removeNonPrintable(lcVal);

			if (PostProccessConstants.LIMITED_MASS_MEASUREMENT_CHOICES_ARRAY
					.contains(StringUtils.removeSpaces(regularHeaders.get(col).toLowerCase()))) {
				this.massMeasurementCol = col;
				break;
			}
		}

		int otherMassCol = -1;
		for (int col = 0; col < regularHeaders.size(); col++) {
			if (StringUtils.isEmptyOrNull(regularHeaders.get(col)))
				continue;

			lcVal = regularHeaders.get(col).toLowerCase();
			val = StringUtils.removeNonPrintable(lcVal);

			if (PostProccessConstants.MASS_CHOICES_ARRAY
					.contains(StringUtils.removeSpaces(regularHeaders.get(col).toLowerCase()))) {
				otherMassCol = col;
				break;
			}
		}

		try {
			String keyForIntensity;
			int rowEnd = txtData.getEndRowIndex() + 1;
			for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);

				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				String fn = rowContents.get(featureNameCol);

				if (StringUtils.isEmptyOrNull(fn))
					continue;

				fn = fn.trim();
				String mass = null;
				if (this.massMeasurementCol != -1)
					mass = rowContents.get(massMeasurementCol);
				else if (otherMassCol != -1)
					mass = rowContents.get(otherMassCol);

				Double massDbl = null;
				try {
					massDbl = Double.parseDouble(mass);
				} catch (Exception e) {
					continue;
				}

				try {
					fn = fn + "_" + String.format("%.5f", massDbl) + "-" + batchNo;
				} catch (Exception e) {
					System.out.println("Unable to print " + mass == null ? "" : mass);
				}

				if (!targetFeaturesMap.containsKey(fn))
					continue;

				List<FeatureFromFile> candidateFeatures = targetFeaturesMap.get(fn);

				if (candidateFeatures == null || candidateFeatures.size() < 1)
					continue;

				// String mass = null;
				// if (this.massMeasurementCol != -1)
				// mass = rowContents.get(massMeasurementCol);
				// else if (otherMassCol != -1)
				// mass = rowContents.get(otherMassCol);

				// Double massDbl = null;
				// try { massDbl = Double.parseDouble(mass); }
				// catch (Exception e) { continue; }

				FeatureFromFile targetFeature = null;
				for (int i = 0; i < candidateFeatures.size(); i++) {
					if (Math.abs(massDbl - candidateFeatures.get(i).getMass()) < 1.0) {
						targetFeature = candidateFeatures.get(i);
					}
				}

				if (targetFeature == null)
					continue;

				for (int i = 0; i < intensityHeaderIndices.size(); i++) {
					value = rowContents.get(intensityHeaderIndices.get(i));

					keyForIntensity = intensityHeaders.get(i);
					if (!StringUtils.isEmptyOrNull(keyForIntensity))
						keyForIntensity = StringUtils.removeSpaces(intensityHeaders.get(i)).toLowerCase();
					else
						keyForIntensity = "";

					targetFeature.getIntensityValuesByHeaderMap().put(keyForIntensity, value);
				}
			}
		} // rowNumLoop
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		featureData.updateForNewIntensityColumns(intensityHeaders);
	}

	public int getFeatureNameCol() {
		return featureNameCol;
	}

	public void setFeatureNameCol(int featureNameCol) {
		this.featureNameCol = featureNameCol;
	}

	public Boolean getRequireRtValue() {
		return requireRtValue;
	}

	public void setRequireRtValue(Boolean requireRtValue) {
		this.requireRtValue = requireRtValue;
	}
}
