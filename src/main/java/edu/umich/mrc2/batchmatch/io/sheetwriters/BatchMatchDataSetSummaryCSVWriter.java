////////////////////////////////////////////////////////
//BatchMatchDataSetSummaryCSVWriter.java
//Written by Jan Wigginton and Bill Duren
//September 2019
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.RtPair;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByAbsDeltaRtComparator;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByBatchAndMassComparator;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByMassComparator;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByRtOnlyComparator;
import edu.umich.mrc2.batchmatch.data.comparators.RtPairComparator;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.process.BacktrackingEngine;
import edu.umich.mrc2.batchmatch.process.PostProcessDataSet;
import edu.umich.mrc2.batchmatch.utils.ListUtils;

public class BatchMatchDataSetSummaryCSVWriter {

	private Double cutoffForBacktrack = .007;
	private Integer nFullMatches = 0, nAmbiguousFullMatches = 0;

	public BatchMatchDataSetSummaryCSVWriter() {
	}

	public BatchMatchDataSetSummaryCSVWriter(Double cutoffForBacktrack) {
		this.cutoffForBacktrack = cutoffForBacktrack;
	}

	public Map<String, List<RtPair>> writeSummaryToFile(PostProcessDataSet data, String outputFileName,
			Integer minDesertSize) {
		return writeSummaryToFile(data, outputFileName, minDesertSize, null);
	}

	public Map<String, List<RtPair>> writeSummaryToFile(PostProcessDataSet data, String outputFileName,
			Integer minDesertSize, Map<Integer, String> filesToConvertByBatchNoMap) {

		if (data == null)
			return null;

		File outputFile = new File(outputFileName);

		List<Integer> batchList = data.getSortedUniqueBatchIndices();
		Integer maxBatch = batchList.get(batchList.size() - 1);
		Integer minBatch = batchList.get(0);

		StringBuilder sb = new StringBuilder();
		// System.out.println("Writing " );
		System.out.println();
		sb.append("MATCH GROUP, ");
		for (int i = 1; i <= maxBatch; i++)
			sb.append("BATCH " + i + ",");
		sb.append("# BATCHES MATCHED" + ", ");
		sb.append("# FEATURES" + ", ");

		sb.append("AVG RT" + ", ");
		sb.append("AVG MONOISOTOPIC M/Z" + ", ");

		sb.append(BinnerConstants.LINE_SEPARATOR);
		Map<String, List<Integer>> matchGroupToBatchIdsMap = data.buildMatchGroupToBatchIdsMap(false);
		Map<String, List<String>> redundancyGroupToFeatureNamesMap = data.buildMatchGroupToFeatureNamesMap(false);

		data.buildAvgRtMassByMatchGrpMap(false);
		Map<Integer, Double> avgRts = data.getAvgRtsByMatchGrp();
		Map<Integer, Double> avgMasses = data.getAvgMassesByMatchGrp();

		// Map<Double,List<String>> matchGroupsByAverageRtMap = new HashMap<Double,
		// List<String>>();
		Map<Integer, List<String>> matchGroupsByAverageRtMap = new HashMap<Integer, List<String>>();

		int idx = 0, fullMatches = 0, ambiguousFullMatches = 0;
		Map<String, Integer> offByOneMatchGroups = new HashMap<String, Integer>();

		for (String matchGroupName : matchGroupToBatchIdsMap.keySet()) {

			List<Integer> batchIdsForGroup = matchGroupToBatchIdsMap.get(matchGroupName);

			if (batchIdsForGroup.size() < 1)
				continue;

			Collections.sort(batchIdsForGroup);

			idx = 0;
			sb.append(matchGroupName + ", ");
			int ct = 0, overallCt = 0;
			Integer nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;

			Integer fullSetSize = filesToConvertByBatchNoMap != null ? filesToConvertByBatchNoMap.keySet().size()
					: batchIdsForGroup.size();
			for (int i = minBatch; i <= maxBatch; i++) {

				if (!filesToConvertByBatchNoMap.containsKey(nextId)) {
					nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;
					sb.append("-, ");
					continue;
				}

				ct = nextId.equals(i) ? 1 : 0;
				if (ct > 0)
					nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;

				while (nextId.equals(i)) {
					ct++;
					nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;

				}
				if (ct > 0) {
					sb.append(ct + ", ");
					overallCt++;
				} else
					sb.append("-, ");
			}
			sb.append(overallCt + ", ");

			Integer featureCt = redundancyGroupToFeatureNamesMap.get(matchGroupName).size();
			if (featureCt != null)
				sb.append(featureCt + ", ");
			else {
				System.out.println("Feature Ct " + (featureCt == null ? "null" : featureCt));
				sb.append("0" + ", ");
			}

			if (overallCt == fullSetSize - 1 && featureCt == fullSetSize - 1) {
				offByOneMatchGroups.put(matchGroupName, null);
			}

			if (overallCt == batchList.size()) {
				fullMatches++;
				if (featureCt > overallCt)
					ambiguousFullMatches++;
			}

			/*
			 * Integer redInt = null; Double rt = null, mass = null; try { redInt =
			 * Integer.parseInt(matchGroupName); rt = avgRts.get(redInt); mass =
			 * avgMasses.get(redInt); } catch (Exception e) { redInt = null; rt =null; mass
			 * = null; }
			 */

			Integer redInt = null;
			Double rt = null, mass = null;
			Integer integerRTKey = null;
			try {
				redInt = Integer.parseInt(matchGroupName);
				rt = avgRts.get(redInt);
				integerRTKey = (int) (rt * 1000000);

				mass = avgMasses.get(redInt);
			} catch (Exception e) {
				redInt = null;
				rt = null;
				integerRTKey = null;
				mass = null;
			}

			if (!matchGroupsByAverageRtMap.containsKey(integerRTKey))
				matchGroupsByAverageRtMap.put(integerRTKey, new ArrayList<String>());
			matchGroupsByAverageRtMap.get(integerRTKey).add(matchGroupName);

			sb.append(rt == null ? ", " : rt + ", ");
			sb.append(mass == null ? ", " : mass + ", ");

			for (String featureName : redundancyGroupToFeatureNamesMap.get(matchGroupName))
				sb.append(featureName + "       ");

			sb.append(BinnerConstants.LINE_SEPARATOR);
		}
		try {
			Files.writeString(outputFile.toPath(), 
					sb.toString(), 
					StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, 
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Final match counts (current iteration)");
		System.out.println("--------------------------------------");
		System.out.println("    Unambiguous :" + fullMatches);
		System.out.println("      Ambiguous :" + ambiguousFullMatches + BatchMatchConstants.LINE_SEPARATOR
				+ BatchMatchConstants.LINE_SEPARATOR);

		System.out.println("\n\n" + "===================================================\n"
				+ "Backtracking merge results\n" + "===================================================\n");

		System.out.println("\nIdentifying match deserts .................." + BatchMatchConstants.LINE_SEPARATOR);
		Map<String, List<RtPair>> desertRtMassMap = null;

		Collections.sort(batchList);
		if (maxBatch.equals(2) || batchList.size() == 2)
			desertRtMassMap = grabFakeMapFromUnMatchedTargetBatchFeatures(data.getFeatures(), batchList.get(0),
					batchList.get(1), minDesertSize);
		else
			desertRtMassMap = createByDesertRtMassMap(offByOneMatchGroups, matchGroupToBatchIdsMap,
					matchGroupsByAverageRtMap, avgRts, avgMasses, minDesertSize, maxBatch, minBatch,
					filesToConvertByBatchNoMap);

		Map<String, List<RtPair>> candidateBackTrackPoints = determineCandidateBackTrackPoints(data, desertRtMassMap,
				cutoffForBacktrack, batchList.size() == 2);
		Map<String, List<RtPair>> backTrackedPairsByBatch = new HashMap<String, List<RtPair>>();

		for (String desertKey : candidateBackTrackPoints.keySet()) {
			String batchKey = desertKey.substring(0, desertKey.indexOf("_"));

			if (!backTrackedPairsByBatch.containsKey(batchKey))
				backTrackedPairsByBatch.put(batchKey, new ArrayList<RtPair>());

			List<RtPair> desertPairs = candidateBackTrackPoints.get(desertKey);
			Collections.sort(desertPairs, new RtPairComparator());

			if (desertPairs.size() > 0) {
				System.out.println("\nCandidate lattice points to fill desert " + desertKey + " (based on "
						+ desertPairs.size() + " backtracked features).");
				System.out.println(
						"---------------------------------------------------------------------------------------------------");
			}
			List<RtPair> reportPairs = new ArrayList<RtPair>();
			Double lastProjPt = null, lastDiff = null;
			for (int w = 0; w < desertPairs.size(); w++) {
				if (Math.abs(desertPairs.get(w).getRt2()) < 35 || desertPairs.get(w).getRt1() > 20.0) {

					String secondStr = String.format("%7.5f",
							desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2());
					String firstStr = String.format("%7.5f", desertPairs.get(w).getRt1());
					String deltaStr = String.format("%7.5f", desertPairs.get(w).getRt2());

					Double projPt = desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2();
					Double diff = desertPairs.get(w).getRt2();

					if (lastProjPt != null && Math.abs(projPt - lastProjPt) < .0009)
						if (Math.abs(diff - lastDiff) < .0004)
							continue;

					reportPairs.add(new RtPair(desertPairs.get(w).getRt1(),
							desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2()));

					System.out.println(firstStr + ",  " + secondStr + ", " + deltaStr);
					lastProjPt = projPt;
					lastDiff = diff;
				}
			}
			// AnchorFileWriter writer = new AnchorFileWriter(true);
			// if (maxBatch.equals(2))
			// writer.outputResults(outputFileName + "_Lattice_" + desertKey + ".csv",
			// reportPairs, "Batch02", "Batch01");
			// else
			backTrackedPairsByBatch.get(batchKey).addAll(reportPairs);
		}

		for (int w = 0; w < 3; w++)
			System.out.println();

		nFullMatches = fullMatches - ambiguousFullMatches;
		nAmbiguousFullMatches = ambiguousFullMatches;
		return backTrackedPairsByBatch;
	}

	// FIX
	private Map<String, List<RtPair>> determineCandidateBackTrackPoints(PostProcessDataSet data,
			Map<String, List<RtPair>> desertRtMassMap, Double massTol, Boolean inPairMode) {

		Collections.sort(data.getFeatures(), new FeatureByMassComparator());

		String keyFormat = "%.0f";

		String valStr = String.format("%.3f", massTol);
		System.out.println();
		System.out.println();
		System.out.println("After merging, at mass tolerance " + String.format("%.3f", massTol)
				+ " the following unmatched features could be assigned to a match group. ");

		System.out.println();
		// Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBatchAndMassMap =
		// mapFeaturesByBatchAndMass(data.getFeatures(),
		// inPairMode);//BacktrackingEngine.mapFeaturesByBatchAndMass(data.getFeatures(),
		// inPairMode);
		Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBatchAndMassMap = BacktrackingEngine
				.mapFeaturesByBatchAndMass(data.getFeatures(), inPairMode);

		List<String> orderedDesertKeys = ListUtils.makeListFromCollection(desertRtMassMap.keySet());
		Collections.sort(orderedDesertKeys);

		Map<String, List<RtPair>> suggestedTweakPoints = new HashMap<String, List<RtPair>>();

		for (int i = 0; i < orderedDesertKeys.size(); i++) {

			int dashIdxForKey = orderedDesertKeys.get(i).indexOf("_");
			String currBatchStr = orderedDesertKeys.get(i).substring(0, dashIdxForKey);
			Integer sourceBatch = Integer.parseInt(currBatchStr);

			List<RtPair> desertPts = desertRtMassMap.get(orderedDesertKeys.get(i));
			List<RtPair> suggestedTweakPrs = new ArrayList<RtPair>();

			int nPrinted = 0;
			for (int j = 0; j < desertPts.size(); j++) {

				Double targetMass = desertPts.get(j).getRt2();
				Double targetRt = desertPts.get(j).getRt1();
				String massKey = String.format(keyFormat, Math.floor(targetMass));

				List<FeatureFromFile> featuresToScreen = featuresByBatchAndMassMap.get(sourceBatch).get(massKey);

				if (featuresToScreen == null)
					continue;

				Collections.sort(featuresToScreen, new FeatureByBatchAndMassComparator());
				Double deltaMass = null, deltaRt = null;

				for (int k = 0; k < featuresToScreen.size(); k++) {

					featuresToScreen.get(k).setDeltaMass(Double.POSITIVE_INFINITY);
					featuresToScreen.get(k).setDeltaRt(Double.POSITIVE_INFINITY);

					if (!featuresToScreen.get(k).getBatchIdx().equals(sourceBatch))
						continue;

					if (!(featuresToScreen.get(k).getRedundancyGroup() == null)) {
						continue;
					}

					if (featuresToScreen.get(k).getOldRt() == null)
						featuresToScreen.get(k).setOldRt(featuresToScreen.get(k).getRT());

					deltaMass = targetMass - featuresToScreen.get(k).getMass();
					deltaRt = targetRt - featuresToScreen.get(k).getOldRt();

					if (Math.abs(deltaMass) > massTol) {

						// if (Math.abs(deltaMass)< massTol +.002)
						// System.out.println("For target mass "+ targetMass + " and target rt " +
						// targetRt + " there is a near mass match " + deltaMass+ " rt " + deltaRt);
						continue;
					}
					// BAD TWEAK
					if (Math.abs(deltaRt) > 3.0 && targetRt < 5)
						continue;

					// BAD TWEAK
					if (Math.abs(deltaRt) > 3.0)
						continue;

					featuresToScreen.get(k).setDeltaMass(deltaMass);
					featuresToScreen.get(k).setDeltaRt(deltaRt);
				}

				Collections.sort(featuresToScreen, new FeatureByAbsDeltaRtComparator());

				for (int k = 0; k < Math.min(4, featuresToScreen.size()); k++) {

					// BAD
					if (Math.abs(featuresToScreen.get(k).getDeltaRt()) > 3.0)
						continue;

					if (featuresToScreen.get(k).getRedundancyGroup() == null) {// ||
																				// featuresToScreen.get(k).getBatchIdx().equals(targetBatch))
																				// {

						if (nPrinted == 0) {
							nPrinted++;
							System.out.println("\n");
							System.out.println("To fill desert " + orderedDesertKeys.get(i));
							System.out.println("-------------------------------------------------");
						}

						suggestedTweakPrs.add(
								new RtPair(featuresToScreen.get(k).getOldRt(), featuresToScreen.get(k).getDeltaRt()));
						String targetRtStr = String.format("%7.5f", targetRt);
						String deltaRtStr = String.format("%7.5f", featuresToScreen.get(k).getDeltaRt());
						String rtStr = String.format("%7.5f", featuresToScreen.get(k).getOldRt());
						String massStr = String.format("%.4f", featuresToScreen.get(k).getMass());
						String deltaMassStr = String.format("%5.4f", featuresToScreen.get(k).getDeltaMass());
						if (featuresToScreen.get(k).getDeltaMass() < 2.0) {
							System.out.println("If unmatched feature at " + rtStr + " were shifted by " + deltaRtStr
									+ " it would fill out an incomplete match group at average RT " + targetRtStr
									+ " and average mass " + massStr + ". Mass of unmatched feature falls within "
									+ deltaMassStr + " daltons of the group average mass.");
						}
						break;
					}
				}
			}
			suggestedTweakPoints.put(orderedDesertKeys.get(i), suggestedTweakPrs);
		}
		return suggestedTweakPoints;
	}

	// Pair version of createByDesertRtMass Map. Needs to return map with single key
	// "2-1" (targetBatch (2) - desert id\x for target batch (1))
	// pointing pointing to a list with mass/rt? targets for all unclaimed batch 1
	// features
	private Map<String, List<RtPair>> grabFakeMapFromUnMatchedTargetBatchFeatures(List<FeatureFromFile> featuresToMap,
			Integer targetIdx, Integer sourceIdx, Integer minDesertSize) {

		Collections.sort(featuresToMap, new FeatureByRtOnlyComparator());

		List<RtPair> unclaimedTargetBatchMassRtPairs = new ArrayList<RtPair>();

		int consecutiveCt = 0;
		List<RtPair> candidatePairs = new ArrayList<RtPair>();
		FeatureFromFile f = null;

		for (int j = 0; j < featuresToMap.size(); j++) {
			f = featuresToMap.get(j);
			if (f.getRedundancyGroup() != null) {
				if (f.getBatchIdx().equals(targetIdx)) {
					consecutiveCt = 0;
					candidatePairs = new ArrayList<RtPair>();
				}
				continue;
			}
			// Quality
			if (!f.getBatchIdx().equals(targetIdx))
				continue;

			consecutiveCt++;

			if (consecutiveCt > minDesertSize) {
				for (int i = 0; i < candidatePairs.size(); i++) {
					unclaimedTargetBatchMassRtPairs.add(candidatePairs.get(i));
				}
				consecutiveCt = 0;
				candidatePairs = new ArrayList<RtPair>();
				candidatePairs.add(new RtPair(1111111111.0, 111111111111.0));
			} else
				candidatePairs.add(new RtPair(f.getOldRt() == null ? f.getRT() : f.getOldRt(), f.getMass()));
		}

		Map<String, List<RtPair>> fakeDesertRtMassMapForUnclaimedTarget = new HashMap<String, List<RtPair>>();
		fakeDesertRtMassMapForUnclaimedTarget.put(sourceIdx + "_" + targetIdx, unclaimedTargetBatchMassRtPairs);
		return fakeDesertRtMassMapForUnclaimedTarget;
	}

	private Map<String, List<RtPair>> createByDesertRtMassMap(Map<String, Integer> offByOneMatchGroups,
			Map<String, List<Integer>> matchGroupToBatchIdsMap, Map<Integer, List<String>> matchGroupsByAverageRtMap,
			Map<Integer, Double> averageRtsByMatchGroupMap, Map<Integer, Double> averageMassesByMatchGroupMap,
			Integer minDesertSize, int maxBatchId, int minBatchId, Map<Integer, String> filesToConvertByBatchNoMap) {

		Map<String, List<RtPair>> desertRtMassMap = new HashMap<String, List<RtPair>>();
		// List<Double> sortedRts =
		// ListUtils.makeListFromCollection(matchGroupsByAverageRtMap.keySet());
		List<Integer> sortedRts = ListUtils.makeListFromCollection(matchGroupsByAverageRtMap.keySet());

		Collections.sort(sortedRts);

		List<String> offByOneMatchGroupsSortedByRt = new ArrayList<String>();

		for (int i = 0; i < sortedRts.size(); i++) {

			List<String> matchGroupsForRt = matchGroupsByAverageRtMap.get(sortedRts.get(i));

			for (int j = 0; j < matchGroupsForRt.size(); j++) {
				if (offByOneMatchGroups.containsKey(matchGroupsForRt.get(j))) {
					offByOneMatchGroupsSortedByRt.add(matchGroupsForRt.get(j));
				}
			}
		}
		// Map<Double
		// matchGroupToBatchIdsMap

		Boolean done = false;
		int iter = 0;

		while (!done) {

			if (iter > 0) {
				done = true;
				minDesertSize = 2;
			}

			Integer missingBatch = null, prevMissingBatch = null, consecutiveCt = 0;
			;
			Double rtForRangeStart = null, rtForRangeEnd = null;
			List<RtPair> desertPts = new ArrayList<RtPair>();

			iter++;

			for (int i = 0; i < offByOneMatchGroupsSortedByRt.size(); i++) {

				String matchGroup = offByOneMatchGroupsSortedByRt.get(i);

				if (matchGroup == null)
					continue;

				Integer matchGroupAsInt = Integer.parseInt(matchGroup);
				Double rtForGroup = averageRtsByMatchGroupMap.get(matchGroupAsInt);
				Double massForGroup = averageMassesByMatchGroupMap.get(matchGroupAsInt);

				if (rtForRangeStart == null)
					rtForRangeStart = rtForGroup;

				List<Integer> batchesForOffByOneGroup = matchGroupToBatchIdsMap.get(matchGroup);
				Collections.sort(batchesForOffByOneGroup);

				missingBatch = null;

				if (batchesForOffByOneGroup.get(0).equals(minBatchId + 1))
					missingBatch = minBatchId;
				else {
					for (int j = 1; j < batchesForOffByOneGroup.size(); j++) {
						if (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j - 1) > 1) {
							if (filesToConvertByBatchNoMap != null
									&& !filesToConvertByBatchNoMap.containsKey(batchesForOffByOneGroup.get(j - 1) + 1))
								continue;

							missingBatch = batchesForOffByOneGroup.get(j - 1) + 1;
							break;
						}
					}
				}

				if (missingBatch == null)
					missingBatch = maxBatchId;

				if (prevMissingBatch != null) {
					if (missingBatch.equals(prevMissingBatch)) {
						consecutiveCt++;
						rtForRangeEnd = rtForGroup;
						desertPts.add(new RtPair(rtForGroup, massForGroup));
					} else {
						if (consecutiveCt >= minDesertSize) {
							done = true;
							String rtStartStr = String.format("%.4f", rtForRangeStart);
							String rtEndStr = String.format("%.4f", rtForRangeEnd);
							String nextKey = pullNextKey(desertRtMassMap, prevMissingBatch);

							System.out.println(nextKey + ". Found a desert for Batch " + prevMissingBatch + ". Length: "
									+ consecutiveCt + ". RT range: " + rtStartStr + " to " + rtEndStr);

							desertRtMassMap.put(nextKey, desertPts);

							// rtForLastSavedDesert = rtForRangeEnd;

							// for (int k = 0; k < desertPts.size(); k++)
							// System.out.println(nextKey + " Mass " + desertPts.get(k).getRt2() + " RT: " +
							// desertPts.get(k).getRt1());
						}
						consecutiveCt = 0;
						rtForRangeStart = rtForGroup;
						desertPts = new ArrayList<RtPair>();
					}
				}
				prevMissingBatch = missingBatch;
			}
		}

		// desertPts = new ArrayList<RtPair>();
		/*
		 * //printed++ for (int i = 0; i < offByOneMatchGroupsSortedByRt.size(); i++) {
		 * 
		 * String matchGroup = offByOneMatchGroupsSortedByRt.get(i);
		 * 
		 * if (matchGroup == null) continue;
		 * 
		 * Integer matchGroupAsInt = Integer.parseInt(matchGroup); Double rtForGroup =
		 * averageRtsByMatchGroupMap.get(matchGroupAsInt); Double massForGroup =
		 * averageMassesByMatchGroupMap.get(matchGroupAsInt); if (rtForGroup <=
		 * rtForLastSavedDesert) continue;
		 * 
		 * desertPts.add(new RtPair(rtForGroup, massForGroup));
		 * 
		 * missingBatch = null;
		 * 
		 * List<Integer> batchesForOffByOneGroup =
		 * matchGroupToBatchIdsMap.get(matchGroup);
		 * Collections.sort(batchesForOffByOneGroup);
		 * 
		 * if (batchesForOffByOneGroup.get(0).equals(2)) missingBatch = 1; else { for
		 * (int j = 1; j < batchesForOffByOneGroup.size(); j++) { if
		 * (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
		 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
		 * 
		 * if (missingBatch == null) missingBatch = maxBatchId;
		 * 
		 * String nextKey = pullNextKey(desertRtMassMap, missingBatch);
		 * desertRtMassMap.put(nextKey, desertPts); desertPts = new ArrayList<RtPair>();
		 * 
		 * System.out.println("Adding end point " + rtForGroup + " and mass  " +
		 * massForGroup); }
		 */

		return desertRtMassMap;
	}

	private Integer findMissingBatch(List<Integer> batchesForGroup, Integer minBatchId, Integer maxBatchId,
			Integer skipBatchId) {

		Integer missingBatch = null;

		List<Integer> newBatchesForGroup = new ArrayList<Integer>();
		// int j = 0;
		for (int i = 0; i < batchesForGroup.get(i); i++) {
			if (skipBatchId != null && i == skipBatchId)
				continue;
			newBatchesForGroup.add(batchesForGroup.get(i));
		}

		if (batchesForGroup.get(0).equals(minBatchId + 1))
			missingBatch = minBatchId;
		else {
			for (int j = 1; j < batchesForGroup.size(); j++) {
				if (batchesForGroup.get(j) - batchesForGroup.get(j - 1) > 1) {
					missingBatch = batchesForGroup.get(j - 1) + 1;
					break;
				}
			}
		}

		if (missingBatch == null)
			missingBatch = maxBatchId;

		return missingBatch;
	}
	/*
	 * private Map<Integer, Map<String, List<FeatureFromFile>>>
	 * mapFeaturesByBatchAndMass(List<FeatureFromFile> featuresToMap, Boolean
	 * unClaimedOnly) {
	 * 
	 * Collections.sort(featuresToMap, new FeatureByMassComparator());
	 * 
	 * String keyFormat = "%.0f";
	 * 
	 * Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBatchAndMassMap =
	 * new HashMap<Integer, Map<String, List<FeatureFromFile>>>();
	 * 
	 * for (FeatureFromFile f : featuresToMap) { if (unClaimedOnly &&
	 * f.getRedundancyGroup() != null) continue;
	 * 
	 * Integer batchKeyForFeature = f.getBatchIdx(); if
	 * (!featuresByBatchAndMassMap.containsKey(batchKeyForFeature))
	 * featuresByBatchAndMassMap.put(batchKeyForFeature, new HashMap<String,
	 * List<FeatureFromFile>>());
	 * 
	 * Double massKeyAsFloorDbl = Math.floor(f.getMass()); String massKeyForFeature
	 * = String.format(keyFormat, massKeyAsFloorDbl);
	 * 
	 * Map<String, List<FeatureFromFile>> featuresByMassMap =
	 * featuresByBatchAndMassMap.get(batchKeyForFeature);
	 * 
	 * if (!featuresByMassMap.containsKey(massKeyForFeature))
	 * featuresByMassMap.put(massKeyForFeature, new ArrayList<FeatureFromFile>());
	 * 
	 * featuresByMassMap.get(massKeyForFeature).add(f);
	 * featuresByBatchAndMassMap.put(batchKeyForFeature, featuresByMassMap); }
	 * return featuresByBatchAndMassMap; }
	 */

	private Map<Integer, Map<String, List<FeatureFromFile>>> mapFeaturesByBatchAndMass(
			List<FeatureFromFile> featuresToMap, Boolean unClaimedOnly) {

		Collections.sort(featuresToMap, new FeatureByMassComparator());

		String keyFormat = "%.0f";

		// System.out.println("Mass tolerance " + massTol);
		Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBatchAndMassMap = new HashMap<Integer, Map<String, List<FeatureFromFile>>>();

		for (FeatureFromFile f : featuresToMap) {
			if (unClaimedOnly && f.getRedundancyGroup() != null)
				continue;

			Integer batchKeyForFeature = f.getBatchIdx();
			if (!featuresByBatchAndMassMap.containsKey(batchKeyForFeature))
				featuresByBatchAndMassMap.put(batchKeyForFeature, new HashMap<String, List<FeatureFromFile>>());

			Double massKeyAsFloorDbl = Math.floor(f.getMass());

			String massKeyForFeature = String.format(keyFormat, massKeyAsFloorDbl);

			Map<String, List<FeatureFromFile>> featuresByMassMap = featuresByBatchAndMassMap.get(batchKeyForFeature);

			if (!featuresByMassMap.containsKey(massKeyForFeature))
				featuresByMassMap.put(massKeyForFeature, new ArrayList<FeatureFromFile>());

			featuresByMassMap.get(massKeyForFeature).add(f);

			// if (massKeyForFeature.equals("177") && f.getBatchIdx().equals(1))
			// System.out.println(f);
			featuresByBatchAndMassMap.put(batchKeyForFeature, featuresByMassMap);
		}
		return featuresByBatchAndMassMap;
	}

	String pullNextKey(Map<String, List<RtPair>> desertRtMassMap, Integer missingBatch) {

		Integer nCurrDesertsForMissingBatch = 0;
		for (String key : desertRtMassMap.keySet()) {
			if (key.startsWith(missingBatch.toString()))
				nCurrDesertsForMissingBatch++;
		}

		Integer nextDesert = nCurrDesertsForMissingBatch + 1;
		String nextKey = missingBatch.toString() + "_" + nextDesert.toString();
		return nextKey;
	}

	public Integer getnFullMatches() {
		return nFullMatches;
	}

	public Integer getnAmbiguousFullMatches() {
		return nAmbiguousFullMatches;
	}
}
