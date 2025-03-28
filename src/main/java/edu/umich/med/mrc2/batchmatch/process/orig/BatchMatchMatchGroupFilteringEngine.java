package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.FeatureByBatchComparator;
import edu.umich.med.mrc2.batchmatch.data.comparators.orig.RtPairComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.RtPair;
import edu.umich.med.mrc2.batchmatch.utils.orig.BatchMatchDataUtils;

public class BatchMatchMatchGroupFilteringEngine {

	public static void disambiguateGroups(PostProcessDataSet data) {

		disambiguateGroupsByDisparateMissingness(data, true);
		disambiguateGroupsByIntensityDeviation(data);
		disambiguateGroupsByDisparateMissingnessPct(data);
	}

	public static void disambiguateGroupsByCurveProximity(PostProcessDataSet data) {

		Map<Integer, List<FeatureFromFile>> ambiguousMatchedFeaturesMap = data.grabFeatureByPossibleGroupMap(.005, .05);
		// List<FeatureFromFile> ambiguousMatchedFeatures =
		// data.grabFeatureByGroupAsRTSortedList(ambiguousMatchedFeaturesMap);

		Map<Integer, List<FeatureFromFile>> unambigousMatchedFeaturesMap = data.grabFeatureByGroupMap(true, false);
		List<FeatureFromFile> unambiguousMatchedFeatures = data
				.grabFeatureByGroupAsRTSortedList(unambigousMatchedFeaturesMap);

		Map<Integer, List<RtPair>> unambiguousShiftCurves = grabShiftCurve(unambiguousMatchedFeatures);

		Map<Integer, List<Integer>> dupBatchesByMatchGroup = grabDupBatchesByMatchGrp(ambiguousMatchedFeaturesMap);

		identifyClosestMatches(dupBatchesByMatchGroup, ambiguousMatchedFeaturesMap, unambiguousShiftCurves);
	}

	private static void identifyClosestMatches(Map<Integer, List<Integer>> dupBatchesByMatchGrp,
			Map<Integer, List<FeatureFromFile>> ambiguousFeaturesMap,
			Map<Integer, List<RtPair>> unambiguousShiftCurves) {

		for (List<RtPair> batchList : unambiguousShiftCurves.values()) {
			Collections.sort(batchList, new RtPairComparator());
		}

		List<Integer> matchGroupKeysInRtOrder = getMatchGroupKeysInRtOrder(ambiguousFeaturesMap); //

		Double shift1 = null, shift2 = null, drift1 = null, drift2 = null;
		List<RtPair> searchList = null;
		StringBuilder sb = null;
		List<FeatureFromFile> groupFeatures = null;

		for (int grpIdx = 0; grpIdx < matchGroupKeysInRtOrder.size(); grpIdx++) {

			Integer matchGrp = matchGroupKeysInRtOrder.get(grpIdx);
			groupFeatures = ambiguousFeaturesMap.get(matchGrp);
			List<Integer> targetBatches = dupBatchesByMatchGrp.get(matchGrp);

			for (int batchIdx = 0; batchIdx < targetBatches.size(); batchIdx++) {
				shift1 = null;
				shift2 = null;
				drift1 = null;
				drift2 = null;
				Integer targetBatch = targetBatches.get(batchIdx);

				FeatureFromFile f1 = null, f2 = null;
				for (FeatureFromFile f : groupFeatures) {
					if (f.getBatchIdx().equals(targetBatch)) {
						if (shift1 == null) {
							shift1 = f.getOldRt() == null ? 0.0 : f.getOldRt() - f.getRT();
							f1 = f;
						} else if (shift2 == null) {
							shift2 = f.getOldRt() == null ? 0.0 : f.getOldRt() - f.getRT();
							f2 = f;
						}
					}
				}
				if (f1 == null || f2 == null)
					continue;

				int i = 0;
				searchList = unambiguousShiftCurves.get(targetBatch);
				while (searchList.get(i).getRt1() < f1.getRT())
					i++;

				int windowStart = Math.max(0, i - 5); // windowToLeft;
				int windowEnd = Math.min(searchList.size() - 1, i + 5); // windowToRight;

				ArrayList<Double> rawWindowShifts = new ArrayList<Double>();
				for (int j = windowStart; j < windowEnd; j++)
					rawWindowShifts.add(searchList.get(j).getRt2());

				// Collections.sort(rawWindowShifts); //.sort(null);

				int upper = rawWindowShifts.size() < 8 ? 0 : 0;

				sb = new StringBuilder();
				Double windowAvg = 0.0;
				int nPts = 0;
				for (int j = upper; j < rawWindowShifts.size() - 1 - upper; j++) {
					windowAvg += rawWindowShifts.get(j);
					sb.append(String.format("%.3f  ", rawWindowShifts.get(j)));
					nPts++;
				}
				windowAvg /= (nPts * 1.0);

				drift1 = Math.abs(shift1 - windowAvg);
				drift2 = Math.abs(shift2 - windowAvg);
				Boolean choose1 = drift1 < drift2;
				Boolean definitely1 = choose1 && Math.abs(drift1 - drift2) > .02;
				Boolean definitely2 = !choose1 && Math.abs(drift1 - drift2) > .02;

				String choiceStr1 = definitely1 ? "**" : (choose1 ? "*" : "");
				String choiceStr2 = definitely2 ? "**" : (!choose1 ? "*" : "");

				String line = String.format("1, %d, %d, %.3f, (%s)", matchGrp, targetBatch, windowAvg, sb.toString());
				System.out.println(line);

				line = String.format("2, %s,%d,%d, %s ,%.3f, %.3f", choiceStr1, targetBatch, matchGrp, f1.getName(),
						shift1, drift1);
				System.out.println(line);

				line = String.format("2, %s,%d,%d, %s ,%.3f, %.3f", choiceStr2, targetBatch, matchGrp, f2.getName(),
						shift2, drift2);
				System.out.println(line);

				System.out.println("");
			}
		}
	}

	private static List<String> identifyWorstMatches(Map<Integer, List<Integer>> dupBatchesByMatchGrp,
			Map<Integer, List<FeatureFromFile>> ambiguousFeaturesMap,
			Map<Integer, List<RtPair>> unambiguousShiftCurves) {

		List<String> featuresToRemove = new ArrayList<String>();

		for (List<RtPair> batchList : unambiguousShiftCurves.values()) {
			Collections.sort(batchList, new RtPairComparator());
		}

		List<Integer> matchGroupKeysInRtOrder = getMatchGroupKeysInRtOrder(ambiguousFeaturesMap); //

		Double shift1 = null, shift2 = null, drift1 = null, drift2 = null;
		List<RtPair> searchList = null;
		StringBuilder sb = null;
		List<FeatureFromFile> groupFeatures = null;

		for (int grpIdx = 0; grpIdx < matchGroupKeysInRtOrder.size(); grpIdx++) {

			Integer matchGrp = matchGroupKeysInRtOrder.get(grpIdx);
			groupFeatures = ambiguousFeaturesMap.get(matchGrp);
			List<Integer> targetBatches = dupBatchesByMatchGrp.get(matchGrp);

			for (int batchIdx = 0; batchIdx < targetBatches.size(); batchIdx++) {
				shift1 = null;
				shift2 = null;
				drift1 = null;
				drift2 = null;
				Integer targetBatch = targetBatches.get(batchIdx);

				FeatureFromFile f1 = null, f2 = null;
				for (FeatureFromFile f : groupFeatures) {
					if (f.getBatchIdx().equals(targetBatch)) {
						if (shift1 == null) {
							shift1 = f.getOldRt() == null ? 0.0 : f.getOldRt() - f.getRT();
							f1 = f;
						} else if (shift2 == null) {
							shift2 = f.getOldRt() == null ? 0.0 : f.getOldRt() - f.getRT();
							f2 = f;
						}
					}
				}
				if (f1 == null || f2 == null)
					continue;

				int i = 0;
				searchList = unambiguousShiftCurves.get(targetBatch);
				while (searchList.get(i).getRt1() < f1.getRT())
					i++;

				int windowStart = Math.max(0, i - 5); // windowToLeft;
				int windowEnd = Math.min(searchList.size() - 1, i + 5); // windowToRight;

				ArrayList<Double> rawWindowShifts = new ArrayList<Double>();
				for (int j = windowStart; j < windowEnd; j++)
					rawWindowShifts.add(searchList.get(j).getRt2());

				// Collections.sort(rawWindowShifts); //.sort(null);

				int upper = rawWindowShifts.size() < 8 ? 0 : 0;

				sb = new StringBuilder();
				Double windowAvg = 0.0;
				int nPts = 0;
				for (int j = upper; j < rawWindowShifts.size() - 1 - upper; j++) {
					windowAvg += rawWindowShifts.get(j);
					sb.append(String.format("%.3f  ", rawWindowShifts.get(j)));
					nPts++;
				}
				windowAvg /= (nPts * 1.0);

				drift1 = Math.abs(shift1 - windowAvg);
				drift2 = Math.abs(shift2 - windowAvg);
				Boolean choose1 = drift1 < drift2;
				Boolean definitely1 = choose1 && Math.abs(drift1 - drift2) > .02;
				Boolean definitely2 = !choose1 && Math.abs(drift1 - drift2) > .02;

				String choiceStr1 = definitely1 ? "**" : (choose1 ? "*" : "");
				String choiceStr2 = definitely2 ? "**" : (!choose1 ? "*" : "");

				if (definitely1)
					featuresToRemove.add(f1.getName());
				else if (definitely2)
					featuresToRemove.add(f2.getName());

				String line = String.format("1, %d, %d, %.3f, (%s)", matchGrp, targetBatch, windowAvg, sb.toString());
				System.out.println(line);

				line = String.format("2, %s,%d,%d, %s ,%.3f, %.3f", choiceStr1, targetBatch, matchGrp, f1.getName(),
						shift1, drift1);
				System.out.println(line);

				line = String.format("2, %s,%d,%d, %s ,%.3f, %.3f", choiceStr2, targetBatch, matchGrp, f2.getName(),
						shift2, drift2);
				System.out.println(line);

				System.out.println("");
			}
		}
		return featuresToRemove;
	}

	private void recommendAmbiguousFeaturesToRemove(Map<Integer, List<Integer>> dupBatchesByMatchGrp,
			Map<Integer, List<FeatureFromFile>> ambiguousFeaturesMap,
			Map<Integer, List<RtPair>> unambiguousShiftCurves) {

		List<String> featuresToRemove = identifyWorstMatches(dupBatchesByMatchGrp, ambiguousFeaturesMap,
				unambiguousShiftCurves);
		Collections.sort(featuresToRemove);
		for (int i = 0; i < featuresToRemove.size(); i++)
			System.out.println(featuresToRemove.get(i));
	}

	private static List<Integer> getMatchGroupKeysInRtOrder(Map<Integer, List<FeatureFromFile>> ambiguousFeaturesMap) {

		List<Integer> sortedRTsForGroups = new ArrayList<Integer>();
		List<Integer> matchGroupKeysinRtOrder = new ArrayList<Integer>();

		Map<Integer, Integer> fakeKeysToMatchGroupsMap = new HashMap<Integer, Integer>();
		for (Integer matchGroup : ambiguousFeaturesMap.keySet()) {

			List<FeatureFromFile> listForGroup = ambiguousFeaturesMap.get(matchGroup);

			Integer fakeKey = ((int) (100000 * listForGroup.get(0).getRT()));
			sortedRTsForGroups.add(fakeKey);
			fakeKeysToMatchGroupsMap.put(fakeKey, matchGroup);

		}

		Collections.sort(sortedRTsForGroups);
		for (int i = 0; i < sortedRTsForGroups.size(); i++) {
			Integer fakeKey = sortedRTsForGroups.get(i);
			matchGroupKeysinRtOrder.add(fakeKeysToMatchGroupsMap.get(fakeKey));
		}

		return matchGroupKeysinRtOrder;
	}

	private static Map<Integer, List<Integer>> grabDupBatchesByMatchGrp(
			Map<Integer, List<FeatureFromFile>> matchedFeaturesMap) {

		Map<Integer, List<Integer>> dupBatchesByMatchGroup = new HashMap<Integer, List<Integer>>();
		;

		List<FeatureFromFile> groupFeatures = null;
		for (Integer matchGrp : matchedFeaturesMap.keySet()) {

			groupFeatures = matchedFeaturesMap.get(matchGrp);
			Collections.sort(groupFeatures, new FeatureByBatchComparator());

			FeatureFromFile curr = null, prev = null;
			Integer nDups = 0, lastDupBatch = -1;
			for (int i = 1; i < groupFeatures.size(); i++) {

				curr = groupFeatures.get(i);
				prev = groupFeatures.get(i - 1);

				if (curr.getBatchIdx().equals(prev.getBatchIdx())) {
					nDups++;
					lastDupBatch = curr.getBatchIdx();
				}

				if (nDups > 0) {
					if (!dupBatchesByMatchGroup.containsKey(matchGrp))
						dupBatchesByMatchGroup.put(matchGrp, new ArrayList<Integer>());
					dupBatchesByMatchGroup.get(matchGrp).add(lastDupBatch);
					nDups = 0;
				}
			}

			// dupBatchesByMatchGroup.put(matchGrp, lastDupBatch);
		}

		return dupBatchesByMatchGroup;
	}

	private static Map<Integer, List<RtPair>> grabShiftCurve(List<FeatureFromFile> unambiguousMatchedFeatures) {
		Map<Integer, List<RtPair>> shiftCurves = new HashMap<Integer, List<RtPair>>();

		for (FeatureFromFile f : unambiguousMatchedFeatures) {

			if (f == null)
				continue;

			Integer batch = f.getBatchIdx();

			if (!shiftCurves.containsKey(batch))
				shiftCurves.put(batch, new ArrayList<RtPair>());

			Double rtDiff = f.getOldRt() == null ? 0.0 : f.getOldRt() - f.getRT();
			Double sourceRt = f.getRT();

			shiftCurves.get(batch).add(new RtPair(sourceRt, rtDiff));

		}

		for (List<RtPair> list : shiftCurves.values())
			Collections.sort(list, new RtPairComparator());

		return shiftCurves;

	}

	private static void disambiguateGroupsByIntensityDeviation(PostProcessDataSet data) {

		int fullSetSize = data.getMaxPossibleMatchCt();
		Map<Integer, List<FeatureFromFile>> ambiguousFeatureGroupsMap = data.grabFeatureByAmbiguousGroupMap(); // new
																												// HashMap<Integer,
																												// List<FeatureFromFile>>();

		for (Integer group : ambiguousFeatureGroupsMap.keySet()) {

			List<FeatureFromFile> groupList = ambiguousFeatureGroupsMap.get(group);

			// only consider groups made ambiguous by a single duplicated batch
			if (groupList.size() > groupList.get(0).getnMatchReplicates() + 1)
				continue;

			if (groupList.size() <= fullSetSize)
				continue;

			Collections.sort(groupList, new FeatureByBatchComparator());

			Integer duplicatedBatch = null;
			for (int i = 0; i < groupList.size() - 1; i++) {
				if (groupList.get(i).getBatchIdx().equals(groupList.get(i + 1).getBatchIdx())) {
					duplicatedBatch = i;
					break;
				}
			}

			List<Double> allIntensities = new ArrayList<Double>();

			for (int i = 0; i < groupList.size(); i++)
				if (groupList.get(i).getPctMissing() != null)
					allIntensities.add(groupList.get(i).getMedianIntensity());

			List<Integer> outlierIndices = BatchMatchDataUtils.getOutlierIndices(allIntensities, 3.0);

			if (outlierIndices == null || outlierIndices.size() != 1)
				continue;

			if (outlierIndices.get(0).equals(duplicatedBatch))
				groupList.get(duplicatedBatch).setOtherGroupAnnotation("Recommend delete");

			if (outlierIndices.get(0).equals(duplicatedBatch + 1))
				groupList.get(duplicatedBatch + 1).setOtherGroupAnnotation("Recommend delete");

		}
	}

	private static void disambiguateGroupsByDisparateMissingnessPct(PostProcessDataSet data) {

		data.initializeMissingnessPcts();

		int fullSetSize = data.getMaxPossibleMatchCt();
		Map<Integer, List<FeatureFromFile>> ambiguousFeatureGroupsMap = data.grabFeatureByAmbiguousGroupMap(); // new
																												// HashMap<Integer,
																												// List<FeatureFromFile>>();

		for (Integer group : ambiguousFeatureGroupsMap.keySet()) {

			List<FeatureFromFile> groupList = ambiguousFeatureGroupsMap.get(group);

			// only consider groups made ambiguous by a single duplicated batch
			if (groupList.size() > groupList.get(0).getnMatchReplicates() + 1)
				continue;

			if (groupList.size() <= fullSetSize)
				continue;

			Collections.sort(groupList, new FeatureByBatchComparator());

			Integer duplicatedBatch = null;
			for (int i = 0; i < groupList.size() - 1; i++) {
				if (groupList.get(i).getBatchIdx().equals(groupList.get(i + 1).getBatchIdx())) {
					duplicatedBatch = i;
					break;
				}
			}

			List<Double> allPctMissing = new ArrayList<Double>();

			for (int i = 0; i < groupList.size(); i++)
				if (groupList.get(i).getPctMissing() != null)
					allPctMissing.add(groupList.get(i).getPctMissing());

			List<Integer> outlierIndices = BatchMatchDataUtils.getOutlierIndices(allPctMissing, 3.0);

			if (outlierIndices == null || outlierIndices.size() != 1)
				continue;

			if (outlierIndices.get(0).equals(duplicatedBatch))
				groupList.get(duplicatedBatch).setOtherGroupAnnotation("Recommend delete");

			if (outlierIndices.get(0).equals(duplicatedBatch + 1))
				groupList.get(duplicatedBatch + 1).setOtherGroupAnnotation("Recommend delete");

		}
	}

	private static Integer determineMissingnessCount(List<FeatureFromFile> groupList) {
		Integer nMissing = 0;
		for (FeatureFromFile f : groupList) {
			if (f.getMedianIntensity() == null)
				nMissing++;
		}
		return nMissing;
	}

	public static void disambiguateGroupsByDisparateMissingness(PostProcessDataSet data, Boolean printAnnotation) {

		int fullSetSize = data.getMaxPossibleMatchCt();
		Map<Integer, List<FeatureFromFile>> ambiguousFeatureGroupsMap = data.grabFeatureByAmbiguousGroupMap(); // new
																												// HashMap<Integer,
																												// List<FeatureFromFile>>();

		int nSaves = 0;
		for (Integer group : ambiguousFeatureGroupsMap.keySet()) {

			List<FeatureFromFile> groupList = ambiguousFeatureGroupsMap.get(group);

			// only consider groups made ambiguous by a single duplicated batch
			if (groupList.size() > groupList.get(0).getnMatchReplicates() + 1)
				continue;

			if (groupList.size() <= fullSetSize)
				continue;

			Collections.sort(groupList, new FeatureByBatchComparator());

			Integer duplicatedBatch = null;
			for (int i = 0; i < groupList.size() - 1; i++) {
				if (groupList.get(i).getBatchIdx().equals(groupList.get(i + 1).getBatchIdx())) {
					duplicatedBatch = i;
					break;
				}
			}

			FeatureFromFile dup1 = groupList.get(duplicatedBatch);
			FeatureFromFile dup2 = groupList.get(duplicatedBatch + 1);

			if (dup1.getMedianIntensity() == null && dup2.getMedianIntensity() == null)
				continue;

			if (dup1.getMedianIntensity() != null && dup2.getMedianIntensity() != null)
				continue;

			Integer nEmpty = determineMissingnessCount(groupList);

			if (Math.abs(nEmpty - fullSetSize) > 0.3 * fullSetSize
					&& Math.abs(nEmpty - fullSetSize) < 0.8 * fullSetSize)
				continue;

			// if (dup1.getPctMissing() != null)
			// System.out.println(dup1.getPctMissing());
			// else
			// System.out.println(" Null missing pct");

			Boolean dontReverse = (nEmpty > fullSetSize - 2);

			Integer idxToDelete = null;
			if (dup1.getMedianIntensity() == null)
				idxToDelete = dontReverse ? duplicatedBatch + 1 : duplicatedBatch;
			else
				idxToDelete = dontReverse ? duplicatedBatch : duplicatedBatch + 1;

			// for (FeatureFromFile f : groupList) {
			// f.setnMatchFeatureReplicates(f.getnMatchFeatureReplicates() - 1);
			// f.setNMatch Replicates(f.getnMatchReplicates() - 1);
			// }

			// System.out.println(dup1);
			// System.out.println();
			// System.out.println();
			// System.out.println(dup2);
			// System.out.println();
			// System.out.println();

			if (printAnnotation)
				System.out.println("Existing annotation  " + groupList.get(idxToDelete).getOtherGroupAnnotation());
			else {
				groupList.get(idxToDelete).setOtherGroupAnnotation("Recommend delete");
				groupList.get(idxToDelete).setFurtherAnnotation("Recommend Delete");
			}

			System.out.println("Recommend delete feature " + (idxToDelete + 1) + " for ambiguous group " + group);
			nSaves++;
			groupList.get(idxToDelete).setRedundancyGroup(null);
			groupList.get(idxToDelete).setNMatchReplicates(null);
			// groupList.get(idxToDelete).setnMatchFeatureReplicates(null);
		}
		System.out.println("# Disambiguated features " + nSaves);
	}
}
