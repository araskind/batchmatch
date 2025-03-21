////////////////////////////////////////////////////
// BinnerTextToDataSetReader.java
// Written by Jan Wigginton July 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io.sheetreaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.comparators.orig.FeatureByIntensityComparator;
import edu.umich.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.mrc2.batchmatch.main.PostProccessConstants;
import edu.umich.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.mrc2.batchmatch.utils.orig.StringUtils;

public class BinnerTextToDataSetReader extends MetabolomicsDataReader {

	Integer maxBatchRead;

	public BinnerTextToDataSetReader() {
		super(null);
	}

	public PostProcessDataSet readFeatureData(List<String[]> rawData, Boolean readingMolecularIons,
			Boolean readingPosData) {
		TextFile txtData = new TextFile(rawData);
		return readFeatureData(txtData, readingMolecularIons, readingPosData);
	}

	public PostProcessDataSet readFeatureData(TextFile txtData, Boolean readingMolecularIons, Boolean readingPosData) {

		return readFeatureData(txtData, readingMolecularIons, readingPosData, -1);
	}

	public PostProcessDataSet readFeatureData(TextFile txtData, Boolean readingMolecularIons2, Boolean readingPosData,
			int targetMatchLevel) {

		initializeFoundStatus();

		nonStandardHeadersWithSpacing = new ArrayList<String>();
		intensityHeaders = new ArrayList<String>();
		featuresToSearch = new ArrayList<FeatureFromFile>();

		List<String> standardHeaderTags = new ArrayList<String>();

		String value = null;
		int maxBatchRead = PostProccessConstants.BATCHMATCH_MIN_BATCH,
				minBatchRead = PostProccessConstants.BATCHMATCH_MAX_BATCH;

		readingIntensities = false;

		// Needed for duplicate check and to map all the tags/names, regardless of
		// standard or non-standard
		Map<String, String> foundHeaderMap = new HashMap<String, String>();

		// Column indices corresponding to (standard) columns where info will be used to
		// initialize the featureFromFile -- these values will be reported via the
		// standard header set
		List<Integer> standardHeaderIndices = new ArrayList<Integer>();

		// Column indices of columns that are (classwise) redundant or not corresponding
		// to a variable name (these will go after in the user column section)
		List<Integer> nonstandardHeaderIndices = new ArrayList<Integer>();

		// Column indices of columns corresponding to intensity values
		List<Integer> intensityHeaderIndices = new ArrayList<Integer>();

		int matchGrpLevelIdx = -1, matchGrpCol = -1, batchColIdx = -1;
		List<String> rowContents = null;
		List<Integer> blankHeaderIndices = new ArrayList<Integer>();

		try {
			int rowStart = 0, rowEnd = txtData.getEndRowIndex() + 1;
			int dataStart = 0;
			Boolean firstNonBlankHeaderFound = false;
			int headerCt = 0;
			for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);
				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				int nConsecutiveBlanks = 0;

				for (int col = 0; col < rowContents.size(); col++) {

					// Fixes Index issue
					value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

					// skip blank cols at beginning
					if (!firstNonBlankHeaderFound && StringUtils.checkEmptyOrNull(value)) {
						// headerIdx++;
						continue;
					}
					firstNonBlankHeaderFound = true;

					if (!StringUtils.isEmptyOrNull(value)) {

						nConsecutiveBlanks = 0;
						String tag = StringUtils.removeSpaces(value).toLowerCase();

						if (foundHeaderMap.containsKey(tag + "_dup"))
							foundHeaderMap.put(tag + "_dup(2)", value);
						else if (foundHeaderMap.containsKey(tag))
							foundHeaderMap.put(tag + "_dup", value);
						else
							foundHeaderMap.put(tag, value);

						if (readingIntensities) {
							intensityHeaderIndices.add(col);
							intensityHeaders.add(value);
						} else {
							Boolean haveRedundantHeader = haveDuplicateClassName(tag);
							Boolean haveUnrecognizedHeader = !PostProccessConstants.METABOLOMICS_RECOGNIZED_COL_TAGS
									.contains(tag);

							if (matchGrpLevelIdx == -1
									&& PostProccessConstants.BATCH_MATCHGRP_CT_CHOICES_ARRAY.contains(tag))
								matchGrpLevelIdx = col;

							if (matchGrpCol == -1 && PostProccessConstants.REDUNDANCY_GRP_CHOICES_ARRAY.contains(tag))
								matchGrpCol = col;

							if (batchColIdx == -1 && PostProccessConstants.BATCH_IDX_CHOICES_ARRAY.contains(tag))
								batchColIdx = col;

							if ((haveUnrecognizedHeader || haveRedundantHeader)) {
								nonstandardHeaderIndices.add(col);
								nonStandardHeadersWithSpacing.add(value);
							} else {
								standardHeaderIndices.add(col);
								standardHeaderTags.add(StringUtils.removeSpaces(value).toLowerCase());
							}
						}
					}

					if (StringUtils.isEmptyOrNull(value)) {
						if (readingIntensities) {
							intensityHeaderIndices.add(col);
							intensityHeaders.add(value);
						} else {
							readingIntensities = true;
							intensityHeaderIndices.add(col);
							intensityHeaders.add(value);
						}
						if (nConsecutiveBlanks > 4)
							break;
					}
				} // !haveHeader

				if (!screenForEssentialCols(false))
					return null;

				for (int headercol = 0; headercol < rowContents.size(); headercol++) {

					String cv = rowContents.get(headercol);

					if (StringUtils.isEmptyOrNull(cv) || "-".equals(cv) || StringUtils.removeSpaces(cv).length() == 0)
						blankHeaderIndices.add(headercol);
				}
				dataStart = rowNum + 1;
				headerCt = rowContents.size();
				break;
			}

			// if (matchGrpLevelIdx == -1 && matchGrpCol != -1 && batchColIdx != -1)
			// matchGrpToLevelMap = preReadForMatchGrpLevels(txtData, dataStart,
			// matchGrpCol, batchColIdx);

			// if (intensityHeaders.size() < 1)
			// JOptionPane.showMessageDialog(null, "Warning : No intensity values were found
			// in your file. Although any text found in your report will be mirrored,"
			// + BatchMatchConstants.LINE_SEPARATOR + "to the processed output, any actual
			// intensities will not be recognized as numeric and no shading for outliers"
			// + BatchMatchConstants.LINE_SEPARATOR + "or missingness will be carried over.
			// The intensity section of your (input) Binner report "
			// + BatchMatchConstants.LINE_SEPARATOR + "should be preceded by a single blank
			// column.");

			// Now read data
			Boolean isOutlier = false;
			Integer nRead = 0;
			String keyForIntensity, header, matchGrp;

			for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);

				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				if (rowContents.size() < headerCt) {
					for (int i = 0; i < blankHeaderIndices.size(); i++) {
						
						if (!rowContents.get(blankHeaderIndices.get(i)).equals("-")) {
							
							rowContents.set(blankHeaderIndices.get(i), "");
							if (rowContents.size() >= headerCt)
								break;
						}
					}
				}
				while (rowContents.size() < headerCt)
					rowContents.add("");

				FeatureFromFile featureFromFile = new FeatureFromFile();

				for (int i = 0; i < standardHeaderIndices.size(); i++) {
					value = rowContents.get(standardHeaderIndices.get(i));
					featureFromFile.setValueForHeaderTag(standardHeaderTags.get(i), value);
				}

				header = null;
				for (int i = 0; i < nonstandardHeaderIndices.size(); i++) {
					value = rowContents.get(nonstandardHeaderIndices.get(i));
					header = nonStandardHeadersWithSpacing.get(i);
					if (!header.startsWith("*"))
						nonStandardHeadersWithSpacing.set(i, "*" + header);
					featureFromFile.setValueForHeaderTag(header, value);
				}

				Boolean addFeature = (!StringUtils.isEmptyOrNull(featureFromFile.getName())
						&& featureFromFile.getRT() != null
						&& !StringUtils.isEmptyOrNull(featureFromFile.getRT().toString())
						&& featureFromFile.getMass() != null
						&& !StringUtils.isEmptyOrNull(featureFromFile.getMass().toString()));

				if (addFeature) {
					for (int i = 0; i < intensityHeaderIndices.size(); i++) {
						value = rowContents.get(intensityHeaderIndices.get(i));

						keyForIntensity = intensityHeaders.get(i);
						if (!StringUtils.isEmptyOrNull(keyForIntensity))
							keyForIntensity = StringUtils.removeSpaces(intensityHeaders.get(i)).toLowerCase();
						else
							keyForIntensity = "";

						featureFromFile.getIntensityValuesByHeaderMap().put(keyForIntensity, value);
					}

					if (!foundPctMissing)
						initializeMissingValueInfo(featureFromFile);

					if (!foundRSD)
						featureFromFile.setRsd(null);

					featureFromFile.setReadOrder(nRead++);
					if (!foundIndex)
						featureFromFile.setValueForHeaderTag("index", nRead.toString());

					if (PostProccessConstants.BATCHMATCH_FORWARD_BATCHING) {
						if (featureFromFile.getBatchIdx() != null && featureFromFile.getBatchIdx() > maxBatchRead)
							maxBatchRead = featureFromFile.getBatchIdx();
					} else {
						if (featureFromFile.getBatchIdx() != null && featureFromFile.getBatchIdx() < minBatchRead)
							minBatchRead = featureFromFile.getBatchIdx();
					}

					featureFromFile.setFromPosMode(readingPosData);
					featuresToSearch.add(featureFromFile);
				}
			} // rowNumLoop
		} // try
		catch (Exception e) {
			e.printStackTrace();
		}

		PostProcessDataSet processedData = new PostProcessDataSet();
		processedData.initializeHeaders(featuresToSearch, nonStandardHeadersWithSpacing, intensityHeaders, null,
				readingPosData, false, "", "", null);
		processedData.setRsdPctMissPrecalculated(foundPctMissing);

		maxBatchRead++;
		minBatchRead--;
		for (FeatureFromFile feature : processedData.getFeatures())
			if (feature.getBatchIdx() == null)
				feature.setBatchIdx(PostProccessConstants.BATCHMATCH_FORWARD_BATCHING ? maxBatchRead : minBatchRead);

		int prt = 0;
		Integer lastGrp = -1;
		if (targetMatchLevel != -1) {
			for (FeatureFromFile feature : processedData.getFeatures()) {

				if (feature.getRedundancyGroup() == null || feature.getRedundancyGroup().equals(lastGrp))
					continue;

				// System.out.println(feature.getRedundancyGroup() + ", ");
				lastGrp = feature.getRedundancyGroup();
			}
		}
		return processedData;
	}

	public List<FeatureFromFile> loadTopNFeaturesByIntensity(TextFile rawData, Integer nFeaturesToLoad) {

		PostProcessDataSet dataSet = this.readFeatureData(rawData, false, false);
		List<FeatureFromFile> allFeatures = dataSet.getFeatures();

		Collections.sort(allFeatures, new FeatureByIntensityComparator());

		List<FeatureFromFile> topFeatures = new ArrayList<FeatureFromFile>();
		for (int i = 0; i < nFeaturesToLoad; i++)
			topFeatures.add(allFeatures.get(allFeatures.size() - i - 1));

		return topFeatures;
	}

	/*
	 * private Map<String,Integer> preReadForMatchGrpLevels(TextFile txtData, int
	 * dataStart, int matchGrpCol, int batchCol) {
	 * 
	 * Vector<String> rowContents =null; int rowEnd = txtData.getEndRowIndex() + 1;
	 * String matchGrp = "", batch = ""; Map<String, Integer> matchGrpCtMap = new
	 * HashMap<String, Integer>(); Map<String, String> existingGrpByBatchMap = new
	 * HashMap<String, String>();
	 * 
	 * Map<String, String> batchIndicesRead = new HashMap<String, String>();
	 * 
	 * for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {
	 * 
	 * rowContents = txtData.getRawStringRow(rowNum);
	 * 
	 * if (rowContents == null || rowContents.size() < 2) continue;
	 * 
	 * if (StringUtils.isEmptyOrNull(rowContents.get(0)) &&
	 * StringUtils.isEmptyOrNull(rowContents.get(1))) continue;
	 * 
	 * matchGrp = rowContents.get(matchGrpCol); batch = rowContents.get(batchCol);
	 * 
	 * if (!batchIndicesRead.containsKey(batch)) batchIndicesRead.put(batch, null);
	 * 
	 * if (StringUtils.isEmptyOrNull(matchGrp)) continue;
	 * 
	 * if (!matchGrpCtMap.containsKey(matchGrp)) matchGrpCtMap.put(matchGrp, 0);
	 * 
	 * int currCt = matchGrpCtMap.get(matchGrp);
	 * 
	 * String grpByBatchTag = matchGrp + "_" + batch; if
	 * (existingGrpByBatchMap.containsKey(grpByBatchTag)) continue;
	 * 
	 * existingGrpByBatchMap.put(grpByBatchTag, null); matchGrpCtMap.put(matchGrp,
	 * ++currCt); }
	 * 
	 * maxBatchRead = -100;
	 * 
	 * for (String batchIdx : batchIndicesRead.keySet()) try { Integer currIdx =
	 * Integer.parseInt(batchIdx); if (currIdx > this.maxBatchRead) maxBatchRead =
	 * currIdx; } catch (Exception e) { continue; }
	 * 
	 * return matchGrpCtMap; }
	 */
}
