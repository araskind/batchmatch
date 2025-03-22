////////////////////////////////////////////////////
// BacktrackingEngine.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.FeatureByMassComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;

//import edu.umich.wld.RtPair;

public class BacktrackingEngine {

	/*
	 * private List<Integer> batchList = null; private Integer maxBatch = null,
	 * minBatch = null, minDesertSize = null; private Map<String, List<Integer>>
	 * matchGroupToBatchIdsMap = null; private Map<Integer, Double>
	 * avgRtByMatchGroupMap = null, avgMassByMMatchGroupMap = null; private Double
	 * massTol = null, rtTol = null;
	 * 
	 * 
	 * public BacktrackingEngine(PostProcessDataSet data, Integer minDesertSize,
	 * Double massTol, Double rtTol) {
	 * 
	 * batchList = data.getSortedUniqueBatchIndices(); maxBatch =
	 * batchList.get(batchList.size() - 1); minBatch = batchList.get(0);
	 * 
	 * matchGroupToBatchIdsMap = data.buildMatchGroupToBatchIdsMap(false);
	 * avgRtByMatchGroupMap = data.getAvgRtsByMatchGrp(); avgMassByMMatchGroupMap =
	 * data.getAvgMassesByMatchGrp(); data.buildAvgRtMassByMatchGrpMap(false);
	 * this.minDesertSize = minDesertSize; this.massTol = .003; //massTol;
	 * this.rtTol = rtTol; }
	 * 
	 * 
	 * // public Map<String, List<RtPair>> runBacktracking(PostProcessDataSet data,
	 * Map<String, Integer> offByOneMatchGroups, Map<String, Integer>
	 * offByTwoMatchGroups, Map<Double,List<String>> matchGroupsByAverageRtMap) {
	 * 
	 * Collections.sort(batchList);
	 * 
	 * Map<String, List<RtPair>> desertRtMassMap = null; if (maxBatch.equals(2) ||
	 * batchList.size() == 2) desertRtMassMap =
	 * grabFakeMapFromUnMatchedTargetBatchFeatures(data.getFeatures(),
	 * batchList.get(0), batchList.get(1)); else desertRtMassMap =
	 * createByDesertRtMassMap(offByOneMatchGroups, matchGroupsByAverageRtMap);
	 * 
	 * 
	 * Map<String, List<RtPair>> candidateBackTrackPoints =
	 * determineCandidateBackTrackPoints(data, desertRtMassMap, batchList.size() ==
	 * 2);
	 * 
	 * Map<String, List<RtPair>> backTrackedPairsByBatch = new HashMap<String,
	 * List<RtPair>>();
	 * 
	 * for (String desertKey : candidateBackTrackPoints.keySet()) { String batchKey
	 * = desertKey.substring(0, desertKey.indexOf("_"));
	 * 
	 * if (!backTrackedPairsByBatch.containsKey(batchKey))
	 * backTrackedPairsByBatch.put(batchKey, new ArrayList<RtPair>());
	 * 
	 * List<RtPair> desertPairs = candidateBackTrackPoints.get(desertKey);
	 * Collections.sort(desertPairs, new RtPairComparator());
	 * 
	 * System.out.println("\nCandidate lattice points to fill desert " + desertKey +
	 * " (based on " + desertPairs.size() + " backtracked features).");
	 * System.out.println(
	 * "---------------------------------------------------------------------------------------------------"
	 * );
	 * 
	 * List<RtPair> reportPairs = new ArrayList<RtPair>(); Double lastProjPt = null,
	 * lastDiff = null; for (int w = 0; w < desertPairs.size(); w++) { if
	 * (Math.abs(desertPairs.get(w).getRt2()) < 2.0 || desertPairs.get(w).getRt1() >
	 * 10.0) {
	 * 
	 * String secondStr = String.format("%7.5f", desertPairs.get(w).getRt1() +
	 * desertPairs.get(w).getRt2()); String firstStr = String.format("%7.5f",
	 * desertPairs.get(w).getRt1()); String deltaStr = String.format("%7.5f",
	 * desertPairs.get(w).getRt2());
	 * 
	 * Double projPt = desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2();
	 * Double diff = desertPairs.get(w).getRt2();
	 * 
	 * if (lastProjPt != null && Math.abs(projPt - lastProjPt) < .001) if
	 * (Math.abs(diff - lastDiff) < .002) continue;
	 * 
	 * reportPairs.add(new RtPair(desertPairs.get(w).getRt1(),
	 * desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2()));
	 * 
	 * System.out.println(firstStr + ",  " + secondStr + ", " + deltaStr);
	 * lastProjPt = projPt; lastDiff = diff; } } // AnchorFileWriter writer = new
	 * AnchorFileWriter(true); // if (maxBatch.equals(2)) //
	 * writer.outputResults(outputFileName + "_Lattice_" + desertKey + ".csv",
	 * reportPairs, "Batch02", "Batch01"); //else
	 * 
	 * backTrackedPairsByBatch.get(batchKey).addAll(reportPairs); }
	 * 
	 * return backTrackedPairsByBatch; }
	 * 
	 * 
	 * private Map<String, List<RtPair>>
	 * determineCandidateBackTrackPoints(PostProcessDataSet data, Map<String,
	 * List<RtPair>> desertRtMassMap, Boolean inPairMode) {
	 * 
	 * System.out.println(); System.out.println();
	 * System.out.println("After merging all batches, at mass tolerance " +
	 * String.format("%.3f", massTol) +
	 * " the following unmmatched features can be accounted for by backtracking... "
	 * ); System.out.println();
	 * 
	 * Collections.sort(data.getFeatures(), new FeatureByMassComparator());
	 * 
	 * List<String> orderedDesertKeys =
	 * ListUtils.makeListFromCollection(desertRtMassMap.keySet());
	 * Collections.sort(orderedDesertKeys);
	 * 
	 * Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBatchAndMassMap =
	 * mapFeaturesByBatchAndMass(data.getFeatures(), inPairMode); Map<String,
	 * List<RtPair>> suggestedTweakPoints = new HashMap<String, List<RtPair>>();
	 * 
	 * String keyFormat = "%.0f";
	 * 
	 * for (int i = 0; i < orderedDesertKeys.size(); i++ ) { int dashIdxForKey =
	 * orderedDesertKeys.get(i).indexOf("_"); String currBatchStr =
	 * orderedDesertKeys.get(i).substring(0, dashIdxForKey); Integer sourceBatch =
	 * Integer.parseInt(currBatchStr);
	 * 
	 * List<RtPair> desertPts = desertRtMassMap.get(orderedDesertKeys.get(i));
	 * List<RtPair> suggestedTweakPrs = new ArrayList<RtPair>();
	 * 
	 * System.out.println("To fill desert " + orderedDesertKeys.get(i));
	 * System.out.println("-------------------------------------------");
	 * 
	 * for (int j = 0; j < desertPts.size(); j++) {
	 * 
	 * Double targetMass = desertPts.get(j).getRt2(); Double targetRt =
	 * desertPts.get(j).getRt1(); String massKey = String.format(keyFormat,
	 * Math.floor(targetMass));
	 * 
	 * List<FeatureFromFile> featuresToScreen =
	 * featuresByBatchAndMassMap.get(sourceBatch).get(massKey);
	 * 
	 * if (featuresToScreen == null) continue;
	 * 
	 * Collections.sort(featuresToScreen, new FeatureByBatchAndMassComparator());
	 * Double deltaMass = null, deltaRt = null;
	 * 
	 * for (int k = 0; k < featuresToScreen.size(); k++) {
	 * 
	 * featuresToScreen.get(k).setDeltaMass(Double.POSITIVE_INFINITY);
	 * featuresToScreen.get(k).setDeltaRt(Double.POSITIVE_INFINITY);
	 * 
	 * if (!featuresToScreen.get(k).getBatchIdx().equals(sourceBatch)) continue;
	 * 
	 * if (!(featuresToScreen.get(k).getRedundancyGroup() == null)) continue;
	 * 
	 * if (featuresToScreen.get(k).getOldRt() == null)
	 * featuresToScreen.get(k).setOldRt(featuresToScreen.get(k).getRt());
	 * 
	 * deltaMass = targetMass - featuresToScreen.get(k).getMass(); deltaRt =
	 * targetRt - featuresToScreen.get(k).getOldRt();
	 * 
	 * if (Math.abs(deltaMass) > massTol) continue;
	 * 
	 * if (Math.abs(deltaRt) > 1.0 && targetRt < 10) continue;
	 * 
	 * if (Math.abs(deltaRt) > 2.0) continue;
	 * 
	 * featuresToScreen.get(k).setDeltaMass(deltaMass);
	 * featuresToScreen.get(k).setDeltaRt(deltaRt); }
	 * 
	 * //featuresByBatch Collections.sort(featuresToScreen, new
	 * FeatureByAbsDeltaRtComparator());
	 * 
	 * for (int k = 0; k < Math.min(4, featuresToScreen.size()); k++) { if
	 * (featuresToScreen.get(k).getDeltaRt() > 10.0) continue;
	 * 
	 * if (featuresToScreen.get(k).getRedundancyGroup() == null) {//||
	 * featuresToScreen.get(k).getBatchIdx().equals(targetBatch)) {
	 * 
	 * suggestedTweakPrs.add(new RtPair(featuresToScreen.get(k).getOldRt(),
	 * featuresToScreen.get(k).getDeltaRt()));
	 * 
	 * String targetRtStr = String.format("%7.5f", targetRt); String deltaRtStr =
	 * String.format("%7.5f", featuresToScreen.get(k).getDeltaRt()); String rtStr =
	 * String.format("%7.5f", featuresToScreen.get(k).getOldRt()); String massStr =
	 * String.format("%.4f", featuresToScreen.get(k).getMass()); String deltaMassStr
	 * = String.format("%5.4f", featuresToScreen.get(k).getDeltaMass());
	 * 
	 * if (featuresToScreen.get(k).getDeltaMass() < 1.0) {
	 * System.out.println("Shifting unmatched feature at " + rtStr + " by " +
	 * deltaRtStr + " would account for missed desert feature at RT " + targetRtStr
	 * + " and mass: " + massStr + ". Candidate point falls within " + deltaMassStr
	 * + " daltons of the missed target mass"); } break; } } }
	 * 
	 * suggestedTweakPoints.put(orderedDesertKeys.get(i), suggestedTweakPrs);
	 * System.out.println("\n"); } return suggestedTweakPoints; }
	 * 
	 * 
	 * // Pair version of createByDesertRtMass Map. Needs to return map with single
	 * key "2-1" (targetBatch (2) - desert id\x for target batch (1)) // pointing
	 * pointing to a list with mass/rt? targets for all unclaimed batch 1 features
	 * private Map<String, List<RtPair>>
	 * grabFakeMapFromUnMatchedTargetBatchFeatures(List<FeatureFromFile>
	 * featuresToMap, Integer targetIdx, Integer sourceIdx) {
	 * 
	 * Collections.sort(featuresToMap, new FeatureByRtOnlyComparator());
	 * 
	 * List<RtPair> unclaimedTargetBatchMassRtPairs = new ArrayList<RtPair>();
	 * List<RtPair> candidatePairs = new ArrayList<RtPair>();
	 * 
	 * int consecutiveCt = 0; FeatureFromFile f = null;
	 * 
	 * for (int j = 0; j < featuresToMap.size(); j++) { f = featuresToMap.get(j); if
	 * (f.getRedundancyGroup() != null) { if (f.getBatchIdx().equals(targetIdx)) {
	 * consecutiveCt = 0; candidatePairs = new ArrayList<RtPair>(); } continue; }
	 * 
	 * if (!f.getBatchIdx().equals(targetIdx)) continue;
	 * 
	 * if (++consecutiveCt > minDesertSize) { for (int i = 0; i <
	 * candidatePairs.size(); i++) {
	 * unclaimedTargetBatchMassRtPairs.add(candidatePairs.get(i)); } consecutiveCt =
	 * 0; candidatePairs = new ArrayList<RtPair>(); candidatePairs.add(new
	 * RtPair(1111111111.0, 111111111111.0)); } else candidatePairs.add(new
	 * RtPair(f.getOldRt() == null ? f.getRT() : f.getOldRt(), f.getMass())); }
	 * 
	 * Map<String, List<RtPair>> fakeDesertRtMassMapForUnclaimedTarget = new
	 * HashMap<String, List<RtPair>>();
	 * fakeDesertRtMassMapForUnclaimedTarget.put(sourceIdx + "_" + targetIdx,
	 * unclaimedTargetBatchMassRtPairs); return
	 * fakeDesertRtMassMapForUnclaimedTarget; }
	 */

	public static Map<Integer, Map<String, List<FeatureFromFile>>> mapFeaturesByBatchAndMass(
			List<FeatureFromFile> featuresToMap, Boolean unClaimedOnly) {

		Collections.sort(featuresToMap, new FeatureByMassComparator());

		String keyFormat = "%.0f";

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
			featuresByBatchAndMassMap.put(batchKeyForFeature, featuresByMassMap);
		}
		return featuresByBatchAndMassMap;
	}
	/*
	 * 
	 * private Map<String, List<RtPair>> createByDesertRtMassMap(Map<String,
	 * Integer> offByOneMatchGroups, // Map<Double, List<String>>
	 * matchGroupsByAverageRtMap) {
	 * 
	 * Integer missingBatch = null, prevMissingBatch = null, consecutiveCt = 0;
	 * Double rtForRangeStart = null, rtForRangeEnd = null;
	 * 
	 * Map<String, List<RtPair>> desertRtMassMap = new HashMap<String,
	 * List<RtPair>>(); List<RtPair> desertPts = new ArrayList<RtPair>();
	 * 
	 * List<String> offByOneMatchGroupsSortedByRt =
	 * sortMatchGroupMapByRt(offByOneMatchGroups, matchGroupsByAverageRtMap);
	 * 
	 * for (int i = 0; i < offByOneMatchGroupsSortedByRt.size(); i++) {
	 * 
	 * String matchGroup = offByOneMatchGroupsSortedByRt.get(i);
	 * 
	 * if (matchGroup == null) continue;
	 * 
	 * Integer matchGroupAsInt = Integer.parseInt(matchGroup); Double rtForGroup =
	 * this.avgRtByMatchGroupMap.get(matchGroupAsInt); Double massForGroup =
	 * this.avgMassByMMatchGroupMap.get(matchGroupAsInt);
	 * 
	 * if (rtForRangeStart == null) rtForRangeStart = rtForGroup;
	 * 
	 * missingBatch = determineMissingBatchForGroup(matchGroup, minBatch, maxBatch);
	 * // missingBatch = determineMissingBatchForGroup(matchGroup, minBatchId,
	 * maxBatchId);
	 * 
	 * if (prevMissingBatch != null) { if (missingBatch.equals(prevMissingBatch)) {
	 * consecutiveCt++; rtForRangeEnd = rtForGroup; desertPts.add(new
	 * RtPair(rtForGroup, massForGroup)); } else { if (consecutiveCt >=
	 * minDesertSize) { String nextKey = pullNextKey(desertRtMassMap,
	 * prevMissingBatch); desertRtMassMap.put(nextKey, desertPts);
	 * 
	 * String rtStartStr = String.format("%.4f", rtForRangeStart); String rtEndStr =
	 * String.format("%.4f", rtForRangeEnd); System.out.println(nextKey +
	 * ". Found a desert for Batch " + prevMissingBatch + ". Length: " +
	 * consecutiveCt + ". RT range: " + rtStartStr + " to " + rtEndStr); }
	 * consecutiveCt = 0; rtForRangeStart = rtForGroup; desertPts = new
	 * ArrayList<RtPair>(); } } prevMissingBatch = missingBatch; }
	 * 
	 * handleEndPoints(missingBatch, offByOneMatchGroups,
	 * offByOneMatchGroupsSortedByRt, desertRtMassMap);
	 * 
	 * return desertRtMassMap; }
	 * 
	 * 
	 * // private List<String> sortMatchGroupMapByRt(Map<String, Integer>
	 * matchGroupsMap, Map<Double, List<String>> matchGroupsByAverageRtMap) {
	 * 
	 * List<String> matchGroupsSortedByRt = new ArrayList<String>();
	 * 
	 * List<Double> sortedRts =
	 * ListUtils.makeListFromCollection(matchGroupsByAverageRtMap.keySet());
	 * Collections.sort(sortedRts);
	 * 
	 * for (int i = 0; i < sortedRts.size(); i++) {
	 * 
	 * List<String> matchGroupsForRt =
	 * matchGroupsByAverageRtMap.get(sortedRts.get(i));
	 * 
	 * for (int j = 0; j < matchGroupsForRt.size(); j++) { if
	 * (matchGroupsMap.containsKey(matchGroupsForRt.get(j))) {
	 * matchGroupsSortedByRt.add(matchGroupsForRt.get(j)); } } } return
	 * matchGroupsSortedByRt; }
	 * 
	 * 
	 * private Map<String, List<RtPair>> createByDesertRtMassMap2(Map<String,
	 * Integer> offByTwoMatchGroups, // Map<Double, List<String>>
	 * matchGroupsByAverageRtMap) {
	 * 
	 * 
	 * String missingBatch = null, prevMissingBatch = null; Integer consecutiveCt =
	 * 0; Double rtForRangeStart = null, rtForRangeEnd = null;
	 * 
	 * Map<String, List<RtPair>> desertRtMassMap = new HashMap<String,
	 * List<RtPair>>(); List<RtPair> desertPts = new ArrayList<RtPair>();
	 * 
	 * List<String> offByTwoMatchGroupsSortedByRt =
	 * sortMatchGroupMapByRt(offByTwoMatchGroups, matchGroupsByAverageRtMap);
	 * 
	 * for (int i = 0; i < offByTwoMatchGroupsSortedByRt.size(); i++) {
	 * 
	 * String matchGroup = offByTwoMatchGroupsSortedByRt.get(i);
	 * 
	 * if (matchGroup == null) continue;
	 * 
	 * Integer matchGroupAsInt = Integer.parseInt(matchGroup); Double rtForGroup =
	 * this.avgRtByMatchGroupMap.get(matchGroupAsInt); Double massForGroup =
	 * this.avgMassByMMatchGroupMap.get(matchGroupAsInt);
	 * 
	 * if (rtForRangeStart == null) rtForRangeStart = rtForGroup;
	 * 
	 * missingBatch = determineMissingBatchesForGroup(matchGroup, minBatch,
	 * maxBatch);
	 * 
	 * if (prevMissingBatch != null) { if (missingBatch.equals(prevMissingBatch)) {
	 * consecutiveCt++; rtForRangeEnd = rtForGroup; desertPts.add(new
	 * RtPair(rtForGroup, massForGroup)); } else { if (consecutiveCt >=
	 * minDesertSize) { String nextKey = "";// (desertRtMassMap, prevMissingBatch);
	 * desertRtMassMap.put(nextKey, desertPts);
	 * 
	 * String rtStartStr = String.format("%.4f", rtForRangeStart); String rtEndStr =
	 * String.format("%.4f", rtForRangeEnd); System.out.println(nextKey +
	 * ". Found a desert for Batch " + prevMissingBatch + ". Length: " +
	 * consecutiveCt + ". RT range: " + rtStartStr + " to " + rtEndStr); }
	 * consecutiveCt = 0; rtForRangeStart = rtForGroup; desertPts = new
	 * ArrayList<RtPair>(); } } prevMissingBatch = missingBatch; }
	 * 
	 * // handleEndPoints(missingBatch, offByOneMatchGroups,
	 * offByOneMatchGroupsSortedByRt, desertRtMassMap);
	 * 
	 * return desertRtMassMap; }
	 * 
	 * 
	 * private Integer determineMissingBatchForGroup(String matchGroup, Integer
	 * minBatchId, Integer maxBatchId) {
	 * 
	 * List<Integer> batchesForOffByOneGroup =
	 * matchGroupToBatchIdsMap.get(matchGroup);
	 * Collections.sort(batchesForOffByOneGroup);
	 * 
	 * Integer missingBatch = null;
	 * 
	 * if (batchesForOffByOneGroup.get(0).equals(minBatchId+1)) missingBatch =
	 * minBatchId; else { for (int j = 1; j < batchesForOffByOneGroup.size(); j++) {
	 * if (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
	 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
	 * 
	 * if (missingBatch == null) missingBatch = maxBatchId;
	 * 
	 * return missingBatch; }
	 * 
	 * private String determineMissingBatchesForGroup(String matchGroup, Integer
	 * minBatchId, Integer maxBatchId) {
	 * 
	 * List<Integer> batchesForGroup = matchGroupToBatchIdsMap.get(matchGroup);
	 * Collections.sort(batchesForGroup);
	 * 
	 * List<Integer> missingBatches = new ArrayList<Integer>();
	 * 
	 * if (batchesForGroup.get(0).equals(minBatchId+1))
	 * missingBatches.add(minBatchId);
	 * 
	 * for (int j = 1; j < batchesForGroup.size(); j++) { if (batchesForGroup.get(j)
	 * - batchesForGroup.get(j-1) > 1) { missingBatches.add(batchesForGroup.get(j-1)
	 * + 1); break; } }
	 * 
	 * if (missingBatches.size() == 0) missingBatches.add(maxBatchId);
	 * 
	 * 
	 * Collections.sort(missingBatches);
	 * 
	 * StringBuilder sb = new StringBuilder(); for (int i = 0; i <
	 * missingBatches.size(); i++) sb.append((i == 0 ? "" : "_") +
	 * missingBatches.get(i));
	 * 
	 * return sb.toString(); }
	 * 
	 * private void handleEndPoints(Integer missingBatch, Map<String, Integer>
	 * offByOneMatchGroups, List<String> offByOneMatchGroupsSortedByRt, Map<String,
	 * List<RtPair>> desertRtMassMap) {
	 * 
	 * List<String> orderedDesertKeys =
	 * ListUtils.makeListFromCollection(desertRtMassMap.keySet());
	 * Collections.sort(orderedDesertKeys);
	 * 
	 * /*List<RtPair> desertPts = desertRtMassMap.get(orderedDesertKeys.get(i));
	 * 
	 * for (int i = 0; i < offByOneMatchGroupsSortedByRt.size(); i++) {
	 * 
	 * String matchGroup = offByOneMatchGroupsSortedByRt.get(i);
	 * 
	 * if (matchGroup == null) continue;
	 * 
	 * Integer matchGroupAsInt = Integer.parseInt(matchGroup); Double rtForGroup =
	 * avgRts.get(matchGroupAsInt);//averageRtsByMatchGroupMap.get(matchGroupAsInt);
	 * Double massForGroup = avgMasses.get(matchGroupAsInt);
	 * //averageMassesByMatchGroupMap.get(matchGroupAsInt); if (rtForGroup <=
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
	 * if (missingBatch == null) missingBatch = maxBatch;
	 * 
	 * String nextKey = pullNextKey(desertRtMassMap, missingBatch);
	 * desertRtMassMap.put(nextKey, desertPts); desertPts = new ArrayList<RtPair>();
	 */
	// System.out.println("Adding end point " + rtForGroup + " and mass " +
	// massForGroup);
	// }
	/*
	 * }
	 * 
	 * 
	 * String pullNextKey(Map<String, List<RtPair>> desertRtMassMap, Integer
	 * missingBatch) {
	 * 
	 * Integer nCurrDesertsForMissingBatch = 0; for (String key :
	 * desertRtMassMap.keySet()) { if (key.startsWith(missingBatch.toString()))
	 * nCurrDesertsForMissingBatch++; }
	 * 
	 * Integer nextDesert = nCurrDesertsForMissingBatch + 1; String nextKey =
	 * missingBatch.toString() + "_" + nextDesert.toString(); return nextKey; }
	 */
}

//////////////////

/*
 * List<Integer> batchesForOffByOneGroup =
 * matchGroupToBatchIdsMap.get(matchGroup);
 * Collections.sort(batchesForOffByOneGroup);
 * 
 * 
 * missingBatch = null;
 * 
 * if (batchesForOffByOneGroup.get(0).equals(minBatchId+1)) missingBatch =
 * minBatchId; else { for (int j = 1; j < batchesForOffByOneGroup.size(); j++) {
 * if (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
 * 
 * if (missingBatch == null) missingBatch = maxBatchId;
 */

/*
 * 
 * /* new ArrayList<String>();
 * 
 * List<Double> sortedRts =
 * ListUtils.makeListFromCollection(matchGroupsByAverageRtMap.keySet());
 * Collections.sort(sortedRts);
 * 
 * for (int i = 0; i < sortedRts.size(); i++) {
 * 
 * List<String> matchGroupsForRt =
 * matchGroupsByAverageRtMap.get(sortedRts.get(i));
 * 
 * for (int j = 0; j < matchGroupsForRt.size(); j++) { if
 * (offByOneMatchGroups.containsKey(matchGroupsForRt.get(j))) {
 * offByOneMatchGroupsSortedByRt.add(matchGroupsForRt.get(j)); } } }
 */
