////////////////////////////////////////////////////////
//RecursiveLatticeFileWriter.java
//Written by Jan Wigginton and Bill Duren
//August 2020
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.comparators.orig.RtPairComparator;
import edu.umich.mrc2.batchmatch.data.orig.AnchorMap;
import edu.umich.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.orig.RtPair;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.process.orig.PostProcessDataSet;

public class RecursiveLatticeFileWriter {

	private int completeSetSize = 7;

	public RecursiveLatticeFileWriter(int cSize) {
		this.completeSetSize = cSize;
	}

	public Map<String, List<Double>> getRTsByBatchForCompleteMatchGroups(PostProcessDataSet data) {
		return getRTsByBatchForCompleteMatchGroups(data, false);
	}

	public Map<String, List<Double>> getRTsByBatchForCompleteMatchGroups(PostProcessDataSet data, Boolean useMasses) {

		if (data == null)
			return null;

		Map<String, List<Integer>> matchGroupToBatchIdsMap = data.buildMatchGroupToBatchIdsMap(true);
		Map<String, List<String>> matchGroupToFeatureNamesMap = data.buildMatchGroupToFeatureNamesMap(false);
		Map<String, List<FeatureFromFile>> featureByFeatureNamesMap = data.getFeaturesByNameMap();

		List<String> completeMatchGroups = new ArrayList<String>();

		int ct = 0;
		for (String key : matchGroupToBatchIdsMap.keySet()) {
			if (matchGroupToBatchIdsMap.get(key) != null) {
				ct = 1;
				List<Integer> batchIdsForGroup = matchGroupToBatchIdsMap.get(key);
				Collections.sort(batchIdsForGroup);
				Integer prev = batchIdsForGroup.get(0), curr = null;
				for (int i = 1; i < batchIdsForGroup.size(); i++) {
					curr = batchIdsForGroup.get(i);
					if (curr != null && !curr.equals(prev))
						ct++;
					prev = curr;
				}
				if (ct == completeSetSize)
					completeMatchGroups.add(key);
			}
		}

		int maxBatchIdx = data.getMaxBatch();
		Map<String, List<Double>> rtsForBatchesByMatchGroup = new HashMap<String, List<Double>>();

		Double currRT = null;
		FeatureFromFile f;
		for (String key : completeMatchGroups) {
			List<String> featureNamesForGroup = matchGroupToFeatureNamesMap.get(key);

			List<Double> rtsByBatchForMatchGroup = new ArrayList<Double>();
			for (int i = 0; i < maxBatchIdx; i++)
				rtsByBatchForMatchGroup.add(Double.MAX_VALUE);

			for (String featureName : featureNamesForGroup) {

				f = featureByFeatureNamesMap.get(featureName) != null ? featureByFeatureNamesMap.get(featureName).get(0)
						: null;
				if (f == null)
					continue;
				if (useMasses)
					currRT = f.getMass();
				else
					currRT = f.getOldRt() != null ? f.getOldRt() : f.getRT();

				if (currRT == null)
					continue;
				if (f.getBatchIdx() != null && f.getBatchIdx() <= maxBatchIdx) {
					rtsByBatchForMatchGroup.set(f.getBatchIdx() - 1, currRT);
				}
				rtsForBatchesByMatchGroup.put(key, rtsByBatchForMatchGroup);
			}
		}
		return rtsForBatchesByMatchGroup;
	}

	public void writeLatticeSetRelativeTo(int baseIdx, String outputDirectory, PostProcessDataSet data) {
		writeLatticeSetRelativeTo(baseIdx, outputDirectory, data, false);
	}

	public void writeLatticeSetRelativeTo(int baseIdx, String outputDirectory, PostProcessDataSet data,
			Boolean useMasses) {

		Map<String, List<Double>> rtsByBatchesForCompleteMatchGroups = this.getRTsByBatchForCompleteMatchGroups(data,
				useMasses);

		AnchorFileWriter pairWriter = new AnchorFileWriter();

		baseIdx--;
		List<Integer> fileIndices = new ArrayList<Integer>();
		List<String> fileNames = new ArrayList<String>();

		Integer maxIdx = data.getMaxBatch();

		for (int i = 0; i < maxIdx; i++) {

			if (baseIdx == i)
				continue;

			List<RtPair> prsForLattice = new ArrayList<RtPair>();

			Double prev = 0.0;

			for (String key : rtsByBatchesForCompleteMatchGroups.keySet()) {

				List<Double> rtSource = rtsByBatchesForCompleteMatchGroups.get(key);
				if (rtSource == null || rtSource.size() < this.completeSetSize)
					continue;

				if (rtSource.get(i) < Double.MAX_VALUE && rtSource.get(baseIdx) < Double.MAX_VALUE) {
					prsForLattice.add(new RtPair(rtSource.get(i), rtSource.get(baseIdx)));
				}
			}

			if (!useMasses)
				prsForLattice.add(new RtPair(100.0, 100.0));
			Collections.sort(prsForLattice, new RtPairComparator());

			List<RtPair> thinPrsForLattice = new ArrayList<RtPair>();

			if (!useMasses)
				thinPrsForLattice.add(new RtPair(0.0, 0.0));

			for (int j = 0; j < prsForLattice.size(); j++) {
				if (Math.abs(prsForLattice.get(j).getRt1() - prev) > .0001) {

					Double diff = prsForLattice.get(j).getRt2() - prsForLattice.get(j).getRt1();
					if (useMasses && Math.abs(diff) > 10.0)
						continue;

					if (!useMasses && thinPrsForLattice.size() == 1) {
						diff = prsForLattice.get(j).getRt2() - prsForLattice.get(j).getRt1();

						if (diff >= 0)
							thinPrsForLattice.get(0).setRt2(diff);
						else
							thinPrsForLattice.get(0).setRt1(-1.0 * diff);
					}
					thinPrsForLattice.add(new RtPair(prsForLattice.get(j).getRt1(), prsForLattice.get(j).getRt2()));
					prev = prsForLattice.get(j).getRt1();
				}
			}

			AnchorMap mapWithoutOutliers = new AnchorMap();
			mapWithoutOutliers.addRtPairs(thinPrsForLattice);
			// mapWithoutOutliers.filterOutliers(500.0);

			String firstFileName = String.format("%02d", i + 1) + "_" + String.format("%02d", baseIdx + 1);
			String fileTypeTag = useMasses ? "Mass_Lattice" : "Recursive_Lattice";
			String completeFileName = pairWriter.outputResults(firstFileName, mapWithoutOutliers.getAsRtPairs(),
					outputDirectory, fileTypeTag, "Batch" + String.format("%02d", i + 1),
					"Batch" + String.format("%02d", baseIdx + 1, useMasses));

			fileNames.add(completeFileName);
			fileIndices.add(i);
		}

		String fileListFile = useMasses ? "mass_lattice_list.csv" : "recursive_lattice_list.csv";
		pairWriter.outputFileList(outputDirectory, fileNames, fileIndices, fileListFile);
	}

	public void writeShiftMapForMerge(String outputFileName, PostProcessDataSet dataSet) {

		Map<String, List<Double>> completeSetShifts = getRTsByBatchForCompleteMatchGroups(dataSet, true);
		Map<String, List<Double>> completeSetRTS = getRTsByBatchForCompleteMatchGroups(dataSet);
		Map<String, Boolean> completeSetsWithNamed = getNamedStatusForCompleteMatchGroups(dataSet, false);
		Map<String, Boolean> completeSetsWithAllNamed = getNamedStatusForCompleteMatchGroups(dataSet, true);

		StringBuilder sb = new StringBuilder();

		for (int batchIdx = 0; batchIdx < this.completeSetSize; batchIdx++) {
			if (batchIdx > 0)
				sb.append(BatchMatchConstants.LINE_SEPARATOR + BatchMatchConstants.LINE_SEPARATOR);

			for (String matchGroup : completeSetRTS.keySet()) {

				if (!completeSetShifts.containsKey(matchGroup))
					continue;
				// Neither value existed
				List<Double> rts = completeSetRTS.get(matchGroup);
				List<Double> shifts = completeSetShifts.get(matchGroup);

				if (shifts == null || rts == null)
					continue;

				if (shifts.size() != rts.size())
					continue;

				if (shifts.size() < completeSetSize)
					continue;

				Boolean hasNamed = completeSetsWithNamed.get(matchGroup);
				Boolean hasAllNamed = completeSetsWithAllNamed.get(matchGroup);
				String matchGroupPrefix = hasAllNamed ? "**" : (hasNamed ? "*" : "");
				sb.append(String.format("%s%s,%d,%.3f,%.4f", matchGroupPrefix, matchGroup, batchIdx + 1,
						rts.get(batchIdx), shifts.get(batchIdx)) + BatchMatchConstants.LINE_SEPARATOR);
			}
		}

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));
			bos.write((sb.toString() + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public Map<String, Boolean> getNamedStatusForCompleteMatchGroups(PostProcessDataSet data, Boolean allOnly) {

		if (data == null)
			return null;

		Map<String, List<Integer>> matchGroupToBatchIdsMap = data.buildMatchGroupToBatchIdsMap(true);
		Map<String, List<String>> matchGroupToFeatureNamesMap = data.buildMatchGroupToFeatureNamesMap(false);
		Map<String, List<FeatureFromFile>> featureByFeatureNamesMap = data.getFeaturesByNameMap();

		List<String> completeMatchGroups = new ArrayList<String>();

		int ct = 0;
		for (String key : matchGroupToBatchIdsMap.keySet()) {
			if (matchGroupToBatchIdsMap.get(key) != null) {
				ct = 1;
				List<Integer> batchIdsForGroup = matchGroupToBatchIdsMap.get(key);
				Collections.sort(batchIdsForGroup);
				Integer prev = batchIdsForGroup.get(0), curr = null;
				for (int i = 1; i < batchIdsForGroup.size(); i++) {
					curr = batchIdsForGroup.get(i);
					if (curr != null && !curr.equals(prev))
						ct++;
					prev = curr;
				}
				if (ct == completeSetSize)
					completeMatchGroups.add(key);
			}
		}

		Map<String, Boolean> namedStatusByCompleteMatchGroup = new HashMap<String, Boolean>();

		for (String matchGroupId : completeMatchGroups) {
			List<String> matchGroupNames = matchGroupToFeatureNamesMap.get(matchGroupId);

			Boolean containsNamed = false;

			if (allOnly) {
				containsNamed = true;
				for (String name : matchGroupNames) {
					containsNamed &= !(name.startsWith("UNK"));
				}
			} else {
				for (String name : matchGroupNames) {
					containsNamed |= !(name.startsWith("UNK"));
				}
			}
			namedStatusByCompleteMatchGroup.put(matchGroupId, containsNamed);
		}
		return namedStatusByCompleteMatchGroup;
	}

	/*
	 * 
	 * private Boolean createNamedMassMapRelativeTo(PostProcessDataSet data, Integer
	 * baseIdx, String outputDirectory) {
	 * 
	 * //Map<String, String> batchFileTagMap =
	 * namedFileListLoaderPanel.getBatchFileMap();
	 * 
	 * Map<String, List<Double>> rtsByBatchesForCompleteMatchGroups =
	 * this.getRTsByBatchForCompleteMatchGroups(data, true);
	 * 
	 * //String outputDirectory = outputDirectoryPanel.getOutputDirectoryPath();
	 * 
	 * MetabolomicsTargetedDataLoader rtLoader = new
	 * MetabolomicsTargetedDataLoader(); Map<String, Map<String, Double>>
	 * latticeMassAverages = new HashMap<String, Map<String, Double>>(); for (String
	 * fileName : batchFileTagMap.values()) latticeMassAverages.put(fileName,
	 * rtLoader.locateMassesAndLoadAverageMap(fileName, false));
	 * //useControlsOnly.isSelected()));
	 * 
	 * Map<String, Map<String, Double>> latticeRtAverages = new HashMap<String,
	 * Map<String, Double>>(); for (String fileName : batchFileTagMap.values())
	 * latticeRtAverages.put(fileName, rtLoader.locateRTsAndLoadAverageMap(fileName,
	 * false));
	 * 
	 * 
	 * String targetKey = baseIdx.toString(); //latticeTypePanel.getTargetSelected()
	 * == null ? "0" : latticeTypePanel.getTargetSelected().toString();;
	 * //batchLabelPanel.getIntSelected().toString(); Integer targetKeyAsInt =
	 * baseIdx; //latticeTypePanel.getTargetSelected(); for (String fileName :
	 * latticeMassAverages.keySet()) { System.out.println("Filename : " + fileName);
	 * }
	 * 
	 * Map<String, List<RtPair>> latticeMapRelativeToKeyed = new HashMap<String,
	 * List<RtPair>>();
	 * 
	 * String targetedFile = batchFileTagMap.get(targetKey); String targetLabel =
	 * String.format("Batch%02d", targetKeyAsInt), currentLabel = ""; String
	 * fileTagTarget = String.format("%02d", targetKeyAsInt);
	 * 
	 * Map<String, Double> keyedMap = latticeMassAverages.get(targetedFile);
	 * 
	 * Map<String, String> completeLatticeFileNames = new HashMap<String, String>();
	 * String fileTagCurrent = null; String currIdx = null;
	 * 
	 * List<String> latticeFileNames = new ArrayList<String>(); List<Integer>
	 * fileIndices = new ArrayList<Integer>();
	 * 
	 * AnchorFileWriter writer = new AnchorFileWriter(true); for (String fileName :
	 * latticeMassAverages.keySet()) {
	 * 
	 * if (fileName.equals(targetedFile)) continue;
	 * 
	 * Map<String, Double> testedMap = latticeMassAverages.get(fileName);
	 * Map<String, Double> avgRtsForTestedMap = latticeRtAverages.get(fileName);
	 * 
	 * List<RtPair> matchedMassPairs = new ArrayList<RtPair>(); List<Double>
	 * avgRtsForMatchedMassPairs = new ArrayList<Double>();
	 * 
	 * for (String featureName : keyedMap.keySet()) { if
	 * (testedMap.containsKey(featureName)) { matchedMassPairs.add(new
	 * RtPair(testedMap.get(featureName), keyedMap.get(featureName)));
	 * avgRtsForMatchedMassPairs.add(avgRtsForTestedMap.get(featureName)); } }
	 * 
	 * //matchedRtPairs.add(new RtPair(100.0, 100.0));
	 * 
	 * //Double diff2 = matchedRtPairs.get(0).getRt1() -
	 * matchedRtPairs.get(0).getRt2(); //if (diff2 > 0) //
	 * matchedRtPairs.get(0).setRt1(diff2); //else //
	 * matchedRtPairs.get(0).setRt2(-1.0 * diff2);
	 * 
	 * //Collections.sort(matchedRtPairs, new RtPairComparator());
	 * 
	 * latticeMapRelativeToKeyed.put(fileName, matchedMassPairs);
	 * 
	 * for (String idx : batchFileTagMap.keySet()) { if
	 * (fileName.equals(batchFileTagMap.get(idx))) { Integer intValue = 0; try {
	 * intValue = Integer.parseInt(idx); } catch (Exception e) { } currentLabel =
	 * String.format("Batch%02d", intValue); fileTagCurrent = String.format("%02d",
	 * intValue); currIdx = idx; break; } }
	 * 
	 * System.out.println("File : " + fileName); System.out.println(currentLabel +
	 * " " + targetLabel);
	 * 
	 * List<RtPair> pairs = latticeMapRelativeToKeyed.get(fileName); writer = new
	 * AnchorFileWriter(true); String fileNameBase = "Mass_Named_Lattice_" +
	 * fileTagCurrent + "_" + fileTagTarget + "_MP"; //(useControlsOnly.isSelected()
	 * ? "_MP" : "");
	 * 
	 * String latticeFileName = fileNameBase + ".csv";
	 * 
	 * writer.outputResultsToFile(latticeFileName, matchedMassPairs,
	 * outputDirectory, currentLabel, targetLabel, true);
	 * //writer.outputResultsWithTagsToFile(latticeFileName,
	 * avgRtsForMatchedMassPairs, matchedMassPairs, outputDirectory, currentLabel,
	 * targetLabel, true); completeLatticeFileNames.put(currIdx, outputDirectory +
	 * BatchMatchConstants.FILE_SEPARATOR + latticeFileName);
	 * latticeFileNames.add(latticeFileName);
	 * 
	 * Integer idx = null; try { idx = Integer.parseInt(currIdx);
	 * 
	 * } catch (Exception e) { idx = 0; } fileIndices.add(idx); }
	 * //writeFileFile(outputDirectory, completeLatticeFileNames); writer = new
	 * AnchorFileWriter(true); writer.outputFileList(outputDirectory,
	 * latticeFileNames, fileIndices, "named_lattice_list.csv");
	 * 
	 * return true; }
	 * 
	 */

}
