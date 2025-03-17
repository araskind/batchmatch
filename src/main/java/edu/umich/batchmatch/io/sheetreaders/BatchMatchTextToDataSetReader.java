////////////////////////////////////////////////////
// BatchMatchTextToDataSetReader.java
// Written by Jan Wigginton July 2019
////////////////////////////////////////////////////
package edu.umich.batchmatch.io.sheetreaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.data.FeatureFromFile;
import edu.umich.batchmatch.data.PostProcessDataSet;
import edu.umich.batchmatch.data.TextFile;
import edu.umich.batchmatch.main.BatchMatchConstants;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.utils.StringUtils;

//FeatureInfo
//Salts
// Reads a csv version of a batchmatch merge report
public class BatchMatchTextToDataSetReader extends MetabolomicsDataReader {

	Integer maxPossibleMatchCt;
	Boolean readMiscCols = false;

	public BatchMatchTextToDataSetReader() {
		super(null);
	}

	public PostProcessDataSet readFeatureData(List<String[]> rawData, Boolean readingPosData) {
		TextFile txtData = new TextFile(rawData);
		return readFeatureData(txtData, readingPosData);
	}

	public PostProcessDataSet readFeatureData(TextFile txtData, Boolean readingPosData) {

		return readFeatureData(txtData, readingPosData, -1, -1);
	}

	public PostProcessDataSet readFeatureData(TextFile txtData, Boolean readingPosData, Integer minTargetMatchLevel,
			Integer maxTargetMatchLevel) {

		initializeFoundStatus();

		Map<Integer, Integer> rawFeatureCtsByBatch = new HashMap<Integer, Integer>();
		nonStandardHeadersWithSpacing = new ArrayList<String>();
		intensityHeaders = new ArrayList<String>();
		featuresToSearch = new ArrayList<FeatureFromFile>();

		List<String> standardHeaderTags = new ArrayList<String>();
		List<String> miscHeaderTags = new ArrayList<String>();

		String value = null;
		// int maxBatchRead = PostProccessConstants.BATCHMATCH_MIN_BATCH, minBatchRead =
		// PostProccessConstants.BATCHMATCH_MAX_BATCH;

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

		// Column indices of colums that are written as nonstandard but need to
		// case-by-case
		// assigned values in the feature according to header tag. Thus both
		// non-standard and standard
		List<Integer> miscHeaderIndices = new ArrayList<Integer>();

		int matchGrpLevelIdx = -1, matchGrpCol = -1, batchColIdx = -1;
		List<Map<String, Integer>> matchGrpToInfoMapList = null;
		Map<String, Integer> matchGrpToBatchCtMap, matchGrpToFeatureCtMap;
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

				for (int col = 0; col < rowContents.size(); col++) {

					value = rowContents.get(col);
					if (value != null && value.trim().startsWith(".") && value.length() < 2) {
						rowContents.set(col, "");
					}
				}

				int nConsecutiveBlanks = 0;

				for (int col = 0; col < rowContents.size(); col++) {

					// Fixes Index issue
					value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

					// skip blank cols at beginning
					if (!firstNonBlankHeaderFound && StringUtils.checkEmptyOrNull(value)) {
						continue;
					}
					firstNonBlankHeaderFound = true;
					// no intensity values
					if (!StringUtils.isEmptyOrNull(value)) {

						nConsecutiveBlanks = 0;
						String tag = StringUtils.removeSpaces(value).toLowerCase();

						if (foundHeaderMap.containsKey(tag + "_dup"))
							foundHeaderMap.put(tag + "_dup(2)", value);
						else if (foundHeaderMap.containsKey(tag))
							foundHeaderMap.put(tag + "_dup", value);
						else
							foundHeaderMap.put(tag, value);

						Boolean haveRedundantHeader = haveDuplicateClassName(tag);
						Boolean haveUnrecognizedHeader = !BatchMatchConstants.BATCHMATCH_POSSIBLE_FIXED_COL_TAGS
								.contains(tag);
						Boolean haveMiscHeader = this.readMiscCols && "furtherannotation".equals(tag);

						if (matchGrpLevelIdx == -1
								&& PostProccessConstants.BATCH_MATCHGRP_CT_CHOICES_ARRAY.contains(tag))
							matchGrpLevelIdx = col;

						if (matchGrpCol == -1 && PostProccessConstants.REDUNDANCY_GRP_CHOICES_ARRAY.contains(tag))
							matchGrpCol = col;

						if (batchColIdx == -1 && PostProccessConstants.BATCH_IDX_CHOICES_ARRAY.contains(tag))
							batchColIdx = col;

						if ((haveUnrecognizedHeader || haveRedundantHeader)) { // will print at end
							nonstandardHeaderIndices.add(col);
							nonStandardHeadersWithSpacing.add(value);
							if (haveMiscHeader) {
								miscHeaderIndices.add(col);
								miscHeaderTags.add(StringUtils.removeSpaces(value).toLowerCase());
							}
						} else {
							standardHeaderIndices.add(col);
							standardHeaderTags.add(StringUtils.removeSpaces(value).toLowerCase());
						}
					}

					if (StringUtils.isEmptyOrNull(value)) {
						nConsecutiveBlanks++;
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

			if (matchGrpLevelIdx == -1 && matchGrpCol != -1 && batchColIdx != -1)
				matchGrpToInfoMapList = preReadForMatchGrpLevels(txtData, dataStart, matchGrpCol, batchColIdx);

			matchGrpToBatchCtMap = matchGrpToInfoMapList.get(0);
			matchGrpToFeatureCtMap = matchGrpToInfoMapList.get(1);

			// if (intensityHeaders.size() < 1)
			// JOptionPane.showMessageDialog(null, "Warning : No intensity values were found
			// in your file. Although any text found in your report will be mirrored,"
			// + BatchMatchConstants.LINE_SEPARATOR + "to the processed output, any actual
			// intensities will not be recognized as numeric and no shading for outliers"
			// + BatchMatchConstants.LINE_SEPARATOR + "or missingness will be carried over.
			// The intensity section of your Binner report "
			// + BatchMatchConstants.LINE_SEPARATOR + "should be preceded by a single blank
			// column.");

			// Now read data
			// Boolean isOutlier = false;
			Integer nRead = 0, batchKey = null, currCt = null;
			String header, matchGrp = null, batchNo = null;

			for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);

				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				matchGrp = rowContents.get(matchGrpCol);
				batchNo = rowContents.get(batchColIdx);

				boolean keepNulls = minTargetMatchLevel.equals(0);
				boolean haveNull = StringUtils.isEmptyOrNull(matchGrp);

				batchKey = null;
				try {
					batchKey = Integer.parseInt(batchNo);
				} catch (Exception e) {
					batchKey = null;
				}

				if (batchKey != null) {

					if (!rawFeatureCtsByBatch.containsKey(batchKey))
						rawFeatureCtsByBatch.put(batchKey, 0);
					currCt = rawFeatureCtsByBatch.get(batchKey);
					currCt++;
					rawFeatureCtsByBatch.put(batchKey, currCt);
				}

				if (!keepNulls && haveNull)
					continue;

				if (!haveNull) {

					if (matchGrpToBatchCtMap != null && (matchGrpToBatchCtMap.get(matchGrp) < minTargetMatchLevel)
							|| matchGrpToBatchCtMap.get(matchGrp) > maxTargetMatchLevel)
						continue;
				}

				if (rowContents.size() < headerCt) {
					for (int i = 0; i < blankHeaderIndices.size(); i++) {
						if (!rowContents.get(blankHeaderIndices.size()).equals("-")) {
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

				for (int i = 0; i < miscHeaderIndices.size(); i++) {
					value = rowContents.get(miscHeaderIndices.get(i));
					featureFromFile.setValueForHeaderTag(miscHeaderTags.get(i), value);
				}

				header = null;
				for (int i = 0; i < nonstandardHeaderIndices.size(); i++) {
					value = rowContents.get(nonstandardHeaderIndices.get(i));
					header = nonStandardHeadersWithSpacing.get(i);
					if (!header.startsWith("*"))
						nonStandardHeadersWithSpacing.set(i, "*" + header);
					featureFromFile.setValueForHeaderTag("@" + header, value);
				}

				boolean addFeature = (!StringUtils.isEmptyOrNull(featureFromFile.getName())
						&& featureFromFile.getRT() != null
						&& !StringUtils.isEmptyOrNull(featureFromFile.getRT().toString())
						&& featureFromFile.getMass() != null
						&& !StringUtils.isEmptyOrNull(featureFromFile.getMass().toString()));

				if (addFeature) {

					featureFromFile.setRsd(null);
					featureFromFile.setPctMissing(null);
					featureFromFile.setReadOrder(nRead++);

					if (matchGrpToBatchCtMap != null)
						featureFromFile.setNMatchReplicates(
								matchGrpToBatchCtMap.get(matchGrp) == null ? null : matchGrpToBatchCtMap.get(matchGrp)); // :
																															// null);
					else
						featureFromFile.setNMatchReplicates(null);

					if (matchGrpToFeatureCtMap != null)
						featureFromFile.setnMatchFeatureReplicates(matchGrpToFeatureCtMap.get(matchGrp) == null ? null
								: matchGrpToFeatureCtMap.get(matchGrp)); // : null);
					else
						featureFromFile.setnMatchFeatureReplicates(null);

					/// System.out.println("Adding feature " + featureFromFile.getBatchIdx() + "
					/// with replicates " + featureFromFile.getnMatchReplicates());

					if (!foundIndex)
						featureFromFile.setValueForHeaderTag("index", nRead.toString());

					if (featureFromFile.getOldRt() == null)
						featureFromFile.setOldRt(featureFromFile.getRT());

					featureFromFile.setFromPosMode(readingPosData);
					featuresToSearch.add(featureFromFile);
				}
			} // rowNumLoop
		} // try
		catch (Exception e) {
			e.printStackTrace();
		}

		PostProcessDataSet processedData = new PostProcessDataSet();
		processedData.setMaxPossibleMatchCt(maxPossibleMatchCt);
		processedData.setRawFeatureCtsByBatch(rawFeatureCtsByBatch);

		processedData.initializeHeaders(featuresToSearch, nonStandardHeadersWithSpacing, intensityHeaders, null,
				readingPosData, false, "", "", null);
		processedData.setRsdPctMissPrecalculated(foundPctMissing);

		return processedData;
	}

	protected List<Map<String, Integer>> preReadForMatchGrpLevels(TextFile txtData, int dataStart, int matchGrpCol,
			int batchCol) {

		List<String> rowContents = null;
		int rowEnd = txtData.getEndRowIndex() + 1;
		String matchGrp = "", batch = "";
		Map<String, Integer> matchGrpCtMap = new HashMap<String, Integer>();
		Map<String, Integer> matchGrpFeatureCtMap = new HashMap<String, Integer>();
		List<Map<String, Integer>> matchGrpInfoList = new ArrayList<Map<String, Integer>>();

		Map<String, String> existingGrpByBatchMap = new HashMap<String, String>();

		Map<String, String> batchIndicesRead = new HashMap<String, String>();
		maxPossibleMatchCt = 0;

		for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

			rowContents = txtData.getRawStringRow(rowNum);

			if (rowContents == null || rowContents.size() < 2)
				continue;

			if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
				continue;

			matchGrp = rowContents.get(matchGrpCol);
			batch = rowContents.get(batchCol);

			if (!batchIndicesRead.containsKey(batch))
				batchIndicesRead.put(batch, null);

			if (StringUtils.isEmptyOrNull(matchGrp))
				continue;

			if (!matchGrpCtMap.containsKey(matchGrp))
				matchGrpCtMap.put(matchGrp, 0);

			if (!matchGrpFeatureCtMap.containsKey(matchGrp))
				matchGrpFeatureCtMap.put(matchGrp, 0);

			int currFeatureCt = matchGrpFeatureCtMap.get(matchGrp);
			matchGrpFeatureCtMap.put(matchGrp, ++currFeatureCt);

			int currCt = matchGrpCtMap.get(matchGrp);

			String grpByBatchTag = matchGrp + "_" + batch;
			// don't count replicate features
			if (existingGrpByBatchMap.containsKey(grpByBatchTag))
				continue;

			existingGrpByBatchMap.put(grpByBatchTag, null);
			matchGrpCtMap.put(matchGrp, ++currCt);
			if (currCt > maxPossibleMatchCt)
				maxPossibleMatchCt = currCt;
		}

		matchGrpInfoList.add(matchGrpCtMap);
		matchGrpInfoList.add(matchGrpFeatureCtMap);

		return matchGrpInfoList;
	}

	public void setReadMiscCols(Boolean ifRead) {
		this.readMiscCols = ifRead;
	}
}
