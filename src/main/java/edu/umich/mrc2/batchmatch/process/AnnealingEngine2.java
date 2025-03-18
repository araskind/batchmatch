package edu.umich.mrc2.batchmatch.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.MatchedFeatureGroup;
import edu.umich.mrc2.batchmatch.data.comparators.MatchedFeatureGroupByAvgMassComparator;

public class AnnealingEngine2 {

	Map<Integer, List<Integer>> batchesByGrpMap = new HashMap<Integer, List<Integer>>();

	public AnnealingEngine2() {
	}

	private Map<Integer, List<Integer>> initializeBatchesByGroupMap(
			Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		batchesByGrpMap = new HashMap<Integer, List<Integer>>();

		for (Integer grp : featuresByMatchGrpMap.keySet()) {

			List<Integer> batchesInGrp = new ArrayList<Integer>();
			for (FeatureFromFile f : featuresByMatchGrpMap.get(grp)) {
				batchesInGrp.add(f.getBatchIdx());
			}
			Collections.sort(batchesInGrp);
			batchesByGrpMap.put(grp, batchesInGrp);
		}
		return batchesByGrpMap;
	}

	public Integer initializeMaxBatch(Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		Integer maxBatch = -1;

		for (Integer grp : featuresByMatchGrpMap.keySet()) {
			for (FeatureFromFile f : featuresByMatchGrpMap.get(grp)) {
				if (f.getBatchIdx() > maxBatch)
					maxBatch = f.getBatchIdx();
			}
		}
		return maxBatch;
	}

	public Integer countBatches(List<MatchedFeatureGroup> matchedFeatureList) {

		Map<Integer, Integer> batchIdxMap = new HashMap<Integer, Integer>();
		for (MatchedFeatureGroup featureGrp : matchedFeatureList) {
			for (FeatureFromFile f : featureGrp.getFeaturesInGroup()) {
				if (!batchIdxMap.containsKey(f.getBatchIdx())) {
					batchIdxMap.put(f.getBatchIdx(), null);
				}
			}
		}
		return batchIdxMap.keySet().size();
	}

	public List<MatchedFeatureGroup> createGroupsList(Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		List<MatchedFeatureGroup> matchedFeatureGroups = new ArrayList<MatchedFeatureGroup>();
		for (Integer key : featuresByMatchGrpMap.keySet()) {
			if (key != null) {
				MatchedFeatureGroup newGrp = new MatchedFeatureGroup(key, featuresByMatchGrpMap.get(key));
				matchedFeatureGroups.add(newGrp);
				// System.out.println("Signature is " + newGrp.getBatchesStr());
			}
		}
		return matchedFeatureGroups;
	}

	private Integer findMinRtDiff(Integer key, List<Integer> candidateList,
			List<MatchedFeatureGroup> matchedFeatureGroups, Integer valueToExclude) {
		Double minRtDiff = Double.MAX_VALUE;
		Integer minIdx = -1;
		for (Integer other : candidateList) {

			if (valueToExclude != null && other.equals(valueToExclude))
				continue;

			Double candidateDiff = Math
					.abs(matchedFeatureGroups.get(key).getAvgRt() - matchedFeatureGroups.get(other).getAvgRt());
			if (candidateDiff < minRtDiff) {
				minRtDiff = candidateDiff;
				minIdx = other;
			}
		}
		return minIdx;
	}

	public Map<Integer, List<FeatureFromFile>> annealFragments(Integer targetBatchToRunAnneal, Double stretchMultiplier,
			Integer batchCtLowerToler3ance3, Double rtTol, Double massTol,
			Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		List<MatchedFeatureGroup> matchedFeatureGroups = createGroupsList(featuresByMatchGrpMap);
		Collections.sort(matchedFeatureGroups, new MatchedFeatureGroupByAvgMassComparator());

		if (countBatches(matchedFeatureGroups) < targetBatchToRunAnneal) {
			// System.out.println("\nRunning anneal for level " + targetBatchToRunAnneal +
			// "............. ");
			// System.out.println("Not enough batches -- returning");
			System.out.println();
			return featuresByMatchGrpMap;
		}

		System.out.println("\n\n\n==================================================================================");
		System.out.println("Annealing -  merging complementary match groups.");
		System.out.println("=======================================================================================\n");

		System.out.println("\nLooking for sets of size " + targetBatchToRunAnneal + "...\n");
		Map<Integer, List<Integer>> candidateMatches = new HashMap<Integer, List<Integer>>();

		// Smooth
		Double scaledRtTol = stretchMultiplier * rtTol;
		Double scaledMassTol = stretchMultiplier * massTol;

		int start = 0;
		while (start < matchedFeatureGroups.size() - 1) {
			int k = 1;

			MatchedFeatureGroup grp1 = matchedFeatureGroups.get(start);
			MatchedFeatureGroup grp2 = matchedFeatureGroups.get(start + k);

			Double avgMassDiff = grp1.getAvgMass() - grp2.getAvgMass();
			Double avgRtDiff = null;

			// while (Math.abs(avgMassDiff) <= massTol) {
			while (Math.abs(avgMassDiff) <= scaledMassTol) {
				avgRtDiff = Math.abs(grp1.getAvgRt() - grp2.getAvgRt());
				List<Integer> batches1 = grp1.getBatches();
				List<Integer> batches2 = grp2.getBatches();

				Boolean haveCandidate = true;
				haveCandidate &= ((batches1.size() + batches2.size()) >= targetBatchToRunAnneal);
				haveCandidate &= (batches1.size() + batches2.size() <= (targetBatchToRunAnneal));

				Boolean haveMatch = false;
				for (Integer b1 : batches1)
					haveMatch |= (batches2.contains(b1));
				haveCandidate &= (!haveMatch);

				haveCandidate &= ((avgRtDiff < scaledRtTol && avgMassDiff < massTol)
						|| (avgRtDiff < rtTol && avgMassDiff < scaledMassTol));
				if (haveCandidate) {
					if (!candidateMatches.containsKey(start))
						candidateMatches.put(start, new ArrayList<Integer>());
					candidateMatches.get(start).add(start + k);
				}
				k++;

				if ((start + k) > matchedFeatureGroups.size() - 1)
					break;

				grp2 = matchedFeatureGroups.get(start + k);
				avgMassDiff = Math.abs(grp1.getAvgMass() - grp2.getAvgMass());
			}
			start++;
		}

		Map<Integer, Integer> bestMatches = new HashMap<Integer, Integer>();
		Map<Integer, Integer> reverseBestMatches = new HashMap<Integer, Integer>();

		for (Integer matchGrp : candidateMatches.keySet()) {

			Integer minIdx = this.findMinRtDiff(matchGrp, candidateMatches.get(matchGrp), matchedFeatureGroups, null);
			if (minIdx != -1) {
				bestMatches.put(matchGrp, minIdx);
				reverseBestMatches.put(minIdx, matchGrp);
			}
		}

		List<Integer> keysToResolve = new ArrayList<Integer>();

		for (Integer key : bestMatches.keySet()) {
			if (bestMatches.containsValue(key))
				keysToResolve.add(key);
		}

		for (Integer key : keysToResolve) {
			Integer key1 = key;
			Integer val1 = bestMatches.get(key);

			Integer key2 = reverseBestMatches.get(key1);
			if (key2 == null)
				continue;
			Integer val2 = bestMatches.get(key2);

			Double rtDiff1 = Math
					.abs(matchedFeatureGroups.get(key1).getAvgRt() - matchedFeatureGroups.get(val1).getAvgRt());
			Double rtDiff2 = Math
					.abs(matchedFeatureGroups.get(key2).getAvgRt() - matchedFeatureGroups.get(val2).getAvgRt());

			if (rtDiff1 < rtDiff2) {

				bestMatches.remove(key2, val2);
				reverseBestMatches.remove(val2, key2);

				Integer newMinIdx = findMinRtDiff(key2, candidateMatches.get(key2), matchedFeatureGroups, val2);
				if (!newMinIdx.equals(-1)) {
					bestMatches.put(key2, newMinIdx);
					reverseBestMatches.put(newMinIdx, key2);
				}
			}

			else {
				bestMatches.remove(key1, val1);
				reverseBestMatches.remove(val1, key1);

				Integer newMinIdx = findMinRtDiff(key1, candidateMatches.get(key1), matchedFeatureGroups, val1);
				if (!newMinIdx.equals(-1)) {
					bestMatches.put(key2, newMinIdx);
					reverseBestMatches.put(newMinIdx, key2);
				}
			}
		}

		List<MatchedFeatureGroup> finalGroups = new ArrayList<MatchedFeatureGroup>();

		try {
			for (int i = 0; i < matchedFeatureGroups.size(); i++) {
				// Integer key = matchedFeatureGroups.get(i).matchGrpKey;
				if (!bestMatches.containsKey(i) && !bestMatches.containsValue(i))
					finalGroups.add(matchedFeatureGroups.get(i));
				else if (bestMatches.containsKey(i)) {
					MatchedFeatureGroup grpToMerge = matchedFeatureGroups.get(bestMatches.get(i));
					MatchedFeatureGroup grpToMergeInto = matchedFeatureGroups.get(i);
					System.out.println("Annealing group " + grpToMerge.getMatchGrpKey() + " (RT: "
							+ String.format("%.4f", grpToMerge.getAvgRt()) + " Mass: "
							+ String.format("%.5f", grpToMerge.getAvgMass()));
					// + ") and group " + grpToMergeInto.getMatchGrpKey()
					// + " (RT: " + String.format("%.4f", grpToMergeInto.getAvgRt())
					// + " Mass: " + String.format("%.5f", grpToMergeInto.getAvgMass() + ")"));

					finalGroups.add(new MatchedFeatureGroup(grpToMergeInto, grpToMerge));
				}
			}
			if (matchedFeatureGroups.size() > 0)
				System.out.println("\n");
		} catch (Exception e) {
		}

		Map<Integer, List<FeatureFromFile>> newFeaturesByMatchGrpMap = new HashMap<Integer, List<FeatureFromFile>>();

		newFeaturesByMatchGrpMap.put(null, featuresByMatchGrpMap.get(null));
		for (MatchedFeatureGroup grp : finalGroups)
			newFeaturesByMatchGrpMap.put(grp.getMatchGrpKey(), grp.getFeaturesInGroup());

		return newFeaturesByMatchGrpMap;
	}
}
