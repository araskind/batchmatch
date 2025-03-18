package edu.umich.mrc2.batchmatch.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.utils.ListUtils;

public class AnnealingEngine {

	private Map<Integer, Integer> groupsOnLeft = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> groupsOnRight = new HashMap<Integer, Integer>();

	Map<Integer, List<Integer>> batchesByGrpMap = new HashMap<Integer, List<Integer>>();

	private Integer freqBreakPt;

	public AnnealingEngine() {
	}

	public Integer identifyFreqBreakPt(Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		Integer maxBatch = initializeMaxBatch(featuresByMatchGrpMap);
		if (maxBatch < 12)
			return -1;

		batchesByGrpMap = initializeBatchesByGroupMap(featuresByMatchGrpMap);

		List<Integer> orderedMatchGroupNames = ListUtils.makeListFromCollection(featuresByMatchGrpMap.keySet());
		Collections.sort(orderedMatchGroupNames);

		List<Integer> gapCountsByBatch = new ArrayList<Integer>();
		for (int i = 0; i < maxBatch; i++)
			gapCountsByBatch.add(0);

		Map<Integer, List<Integer>> gapsInGroupMap = new HashMap<Integer, List<Integer>>();
		Integer prevBatch, currBatch, currCt;

		for (Integer grp : batchesByGrpMap.keySet()) {

			List<Integer> batchesInGrp = batchesByGrpMap.get(grp);

			for (int i = 1; i < batchesInGrp.size(); i++) {

				gapsInGroupMap.put(grp, new ArrayList<Integer>());
				currBatch = batchesInGrp.get(i);
				prevBatch = batchesInGrp.get(i - 1);

				if (prevBatch < currBatch - 1) {
					gapsInGroupMap.get(grp).add(prevBatch);
					currCt = gapCountsByBatch.get(prevBatch);
				}
				if (i == batchesInGrp.size() - 1 && currBatch < maxBatch)
					gapsInGroupMap.get(grp).add(currBatch);
			}

			for (int i = 0; i < gapsInGroupMap.get(grp).size(); i++) {
				currBatch = gapsInGroupMap.get(grp).get(i);
				currCt = gapCountsByBatch.get(currBatch);
				gapCountsByBatch.set(currBatch - 1, currCt + 1);
			}
		}

		int maxCt = 0;
		int maxIdx = -1;
		for (int i = 0; i < gapCountsByBatch.size(); i++) {
			if (gapCountsByBatch.get(i) > maxCt) {
				maxCt = gapCountsByBatch.get(i);
				maxIdx = i;
			}
		}

		if (maxCt <= featuresByMatchGrpMap.keySet().size() * 0.2)
			return -1;

		freqBreakPt = maxIdx + 1;
		return freqBreakPt;
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

	public Integer countBatches(Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		Map<Integer, Integer> batchIdxMap = new HashMap<Integer, Integer>();
		for (Integer grp : featuresByMatchGrpMap.keySet()) {
			for (FeatureFromFile f : featuresByMatchGrpMap.get(grp)) {
				batchIdxMap.put(f.getBatchIdx(), null);
			}
		}
		return batchIdxMap.keySet().size();
	}

	public Map<Integer, List<FeatureFromFile>> annealFragments(Integer breakPt, Integer targetBatchToRunAnneal,
			Double rtTol, Double massTol, Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap) {

		this.freqBreakPt = (breakPt == null ? 4 : breakPt);

		Integer batchCt = countBatches(featuresByMatchGrpMap);

		if (batchCt < targetBatchToRunAnneal)
			return featuresByMatchGrpMap;

		this.initializeBatchesByGroupMap(featuresByMatchGrpMap);
		groupsOnLeft = new HashMap<Integer, Integer>();
		groupsOnRight = new HashMap<Integer, Integer>();

		for (Integer grp : batchesByGrpMap.keySet()) {

			if (grp == null)
				continue;

			List<Integer> batchesInGrp = batchesByGrpMap.get(grp);

			boolean haveLeft = false;
			boolean haveRight = false;

			for (int i = 0; i < batchesInGrp.size(); i++) {
				haveLeft = batchesInGrp.get(i) <= freqBreakPt;
				haveRight = batchesInGrp.get(i) > freqBreakPt;
			}

			if (haveLeft && haveRight)
				continue;

			if (haveLeft)
				groupsOnLeft.put(grp, null);

			if (haveRight)
				groupsOnRight.put(grp, null);
		}

		Double avgMass;
		Double avgRt;

		Map<Integer, Double> leftAvgMasses = new HashMap<Integer, Double>();
		Map<Integer, Double> leftAvgRts = new HashMap<Integer, Double>();

		for (Integer grp : groupsOnLeft.keySet()) {

			if (grp == null)
				continue;

			List<FeatureFromFile> featuresInGrp = featuresByMatchGrpMap.get(grp);

			avgMass = avgRt = 0.0;
			for (FeatureFromFile f : featuresInGrp) {
				avgMass += f.getMass();
				avgRt += f.getRT();
			}
			avgMass /= featuresInGrp.size();
			avgRt /= featuresInGrp.size();

			leftAvgRts.put(grp, avgRt);
			leftAvgMasses.put(grp, avgMass);
		}

		Map<Integer, Double> rightAvgRts = new HashMap<Integer, Double>();
		Map<Integer, Double> rightAvgMasses = new HashMap<Integer, Double>();

		for (Integer grp : groupsOnRight.keySet()) {
			if (grp == null)
				continue;
			List<FeatureFromFile> featuresInGrp = featuresByMatchGrpMap.get(grp);

			avgMass = avgRt = 0.0;
			for (FeatureFromFile f : featuresInGrp) {
				avgMass += f.getMass();
				avgRt += f.getRT();
			}
			avgMass /= featuresInGrp.size();
			avgRt /= featuresInGrp.size();

			rightAvgRts.put(grp, avgRt);
			rightAvgMasses.put(grp, avgMass);
		}

		Map<Integer, Integer> groupsToAnneal = new HashMap<Integer, Integer>();
		Double rtDiff = null, rtDiff2;
		Double massDiff = null;
		for (Integer grp1 : groupsOnRight.keySet()) {
			for (Integer grp2 : groupsOnLeft.keySet()) {
				massDiff = Math.abs(rightAvgMasses.get(grp1) - leftAvgMasses.get(grp2));
				if (massDiff < massTol) {
					rtDiff = Math.abs(rightAvgRts.get(grp1) - leftAvgRts.get(grp2));
					if (Math.abs(rtDiff) < 5 * rtTol) {
						System.out.println("Annealing groups " + grp1 + " and " + grp2);
						if (!groupsToAnneal.containsKey(grp1))
							groupsToAnneal.put(grp1, grp2);
						else {
							Integer grp3 = groupsToAnneal.get(grp1);
							rtDiff2 = Math.abs(rightAvgRts.get(grp1) - leftAvgRts.get(grp3));
							if (rtDiff < rtDiff2)
								groupsToAnneal.put(grp1, grp2);
						}
					}
				}
			}
		}
		System.out.println("Number of annealed groups: " + groupsToAnneal.keySet().size());
		for (Integer grp1 : groupsToAnneal.keySet()) {

			if (grp1 == null)
				continue;

			List<FeatureFromFile> featuresToMerge = featuresByMatchGrpMap.get(groupsToAnneal.get(grp1));

			for (FeatureFromFile f : featuresToMerge) {
				f.setRedundancyGroup(grp1);
				featuresByMatchGrpMap.get(grp1).add(f);
			}
		}
		return featuresByMatchGrpMap;
	}

}

/*
 * package edu.umich.wiggie.util;
 * 
 * import java.util.ArrayList; import java.util.Collections; import
 * java.util.HashMap; import java.util.List; import java.util.Map;
 * 
 * import edu.umich.wld.ListUtils;
 * 
 * public class AnnealingEngine {
 * 
 * 
 * private Map<Integer, Integer> groupsOnLeft = new HashMap<Integer, Integer>();
 * private Map<Integer, Integer> groupsOnRight = new HashMap<Integer,
 * Integer>();
 * 
 * Map<Integer, List<Integer>> batchesByGrpMap = new HashMap<Integer,
 * List<Integer>>();
 * 
 * private Integer freqBreakPt;
 * 
 * public AnnealingEngine() { }
 * 
 * public Integer identifyFreqBreakPt(Map<Integer, List<FeatureFromFile>>
 * featuresByMatchGrpMap) {
 * 
 * Integer maxBatch = initializeMaxBatch(featuresByMatchGrpMap); if (maxBatch <
 * 12) return -1;
 * 
 * batchesByGrpMap = initializeBatchesByGroupMap(featuresByMatchGrpMap);
 * 
 * List<Integer> orderedMatchGroupNames =
 * ListUtils.makeListFromCollection(featuresByMatchGrpMap.keySet());
 * Collections.sort(orderedMatchGroupNames);
 * 
 * List<Integer> gapCountsByBatch = new ArrayList<Integer>(); for (int i = 0; i
 * < maxBatch; i++) gapCountsByBatch.add(0);
 * 
 * Map<Integer, List<Integer>> gapsInGroupMap = new HashMap<Integer,
 * List<Integer>>(); Integer prevBatch, currBatch, currCt;
 * 
 * for (Integer grp : batchesByGrpMap.keySet()) {
 * 
 * List<Integer> batchesInGrp = batchesByGrpMap.get(grp);
 * 
 * for (int i = 1; i < batchesInGrp.size(); i++) {
 * 
 * gapsInGroupMap.put(grp, new ArrayList<Integer>()); currBatch =
 * batchesInGrp.get(i); prevBatch = batchesInGrp.get(i - 1);
 * 
 * if (prevBatch < currBatch - 1) { gapsInGroupMap.get(grp).add(prevBatch);
 * currCt = gapCountsByBatch.get(prevBatch); } if (i == batchesInGrp.size() - 1
 * && currBatch < maxBatch) gapsInGroupMap.get(grp).add(currBatch); }
 * 
 * for (int i = 0; i < gapsInGroupMap.get(grp).size(); i++) { currBatch =
 * gapsInGroupMap.get(grp).get(i); currCt = gapCountsByBatch.get(currBatch);
 * gapCountsByBatch.set(currBatch - 1, currCt+1); } }
 * 
 * Integer maxCt = 0, maxIdx = -1; for (int i = 0;i < gapCountsByBatch.size();
 * i++) { if (gapCountsByBatch.get(i) > maxCt) { maxCt =
 * gapCountsByBatch.get(i); maxIdx= i; } }
 * 
 * if (!(maxCt > featuresByMatchGrpMap.keySet().size() * 0.2)) return -1;
 * 
 * freqBreakPt = maxIdx + 1; return freqBreakPt; }
 * 
 * private Map<Integer, List<Integer>> initializeBatchesByGroupMap(Map<Integer,
 * List<FeatureFromFile>> featuresByMatchGrpMap) {
 * 
 * batchesByGrpMap = new HashMap<Integer, List<Integer>>();
 * 
 * for (Integer grp : featuresByMatchGrpMap.keySet()) {
 * 
 * List<Integer> batchesInGrp = new ArrayList<Integer>(); for (FeatureFromFile f
 * : featuresByMatchGrpMap.get(grp)) { batchesInGrp.add(f.getBatchIdx()); }
 * 
 * Collections.sort(batchesInGrp); batchesByGrpMap.put(grp, batchesInGrp); }
 * return batchesByGrpMap; }
 * 
 * 
 * public Integer initializeMaxBatch(Map<Integer, List<FeatureFromFile>>
 * featuresByMatchGrpMap) {
 * 
 * Integer maxBatch = -1;
 * 
 * for (Integer grp : featuresByMatchGrpMap.keySet()) { for (FeatureFromFile f :
 * featuresByMatchGrpMap.get(grp)) { if (f.getBatchIdx() > maxBatch) maxBatch =
 * f.getBatchIdx(); } } return maxBatch; }
 * 
 * public Integer countBatches(Map<Integer, List<FeatureFromFile>>
 * featuresByMatchGrpMap) {
 * 
 * Map<Integer, Integer> batchIdxMap = new HashMap<Integer, Integer>(); for
 * (Integer grp : featuresByMatchGrpMap.keySet()) { for (FeatureFromFile f :
 * featuresByMatchGrpMap.get(grp)) { batchIdxMap.put(f.getBatchIdx(), null); } }
 * return batchIdxMap.keySet().size(); }
 * 
 * public Map<Integer, List<FeatureFromFile>> annealFragments(Integer breakPt,
 * Integer targetBatchToRunAnneal, Double rtTol, Double massTol, Map<Integer,
 * List<FeatureFromFile>> featuresByMatchGrpMap) {
 * 
 * if (breakPt == null) this.freqBreakPt = breakPt;
 * 
 * Integer batchCt = countBatches(featuresByMatchGrpMap);
 * 
 * if (batchCt < targetBatchToRunAnneal) return featuresByMatchGrpMap;
 * 
 * this.initializeBatchesByGroupMap(featuresByMatchGrpMap); groupsOnLeft = new
 * HashMap<Integer, Integer>(); groupsOnRight = new HashMap<Integer, Integer>();
 * 
 * for (Integer grp : batchesByGrpMap.keySet()) {
 * 
 * if (grp == null) continue;
 * 
 * List<Integer> batchesInGrp = batchesByGrpMap.get(grp);
 * 
 * Boolean haveLeft = false, haveRight = false;
 * 
 * for (int i = 0; i < batchesInGrp.size(); i++) { haveLeft =
 * batchesInGrp.get(i) <= freqBreakPt; haveRight = batchesInGrp.get(i) >
 * freqBreakPt; }
 * 
 * if (haveLeft && haveRight) continue;
 * 
 * if (haveLeft) groupsOnLeft.put(grp, null);
 * 
 * if (haveRight) groupsOnRight.put(grp, null); }
 * 
 * Double avgMass = 0.0, avgRt = 0.0;
 * 
 * Map<Integer, Double> leftAvgMasses = new HashMap<Integer, Double>();
 * Map<Integer, Double> leftAvgRts = new HashMap<Integer, Double>();
 * 
 * for (Integer grp : groupsOnLeft.keySet()) { if (grp == null) continue;
 * List<FeatureFromFile> featuresInGrp = featuresByMatchGrpMap.get(grp);
 * 
 * for (FeatureFromFile f : featuresInGrp) { avgMass += f.getMass(); avgRt +=
 * f.getRt(); } avgMass /= featuresInGrp.size(); avgRt /= featuresInGrp.size();
 * 
 * leftAvgRts.put(grp, avgRt); leftAvgMasses.put(grp, avgMass); }
 * 
 * Map<Integer, Double> rightAvgRts = new HashMap<Integer, Double>();
 * Map<Integer, Double> rightAvgMasses = new HashMap<Integer, Double>();
 * 
 * avgMass = avgRt = 0.0;
 * 
 * for (Integer grp : groupsOnRight.keySet()) { if (grp ==null) continue;
 * List<FeatureFromFile> featuresInGrp = featuresByMatchGrpMap.get(grp);
 * 
 * for (FeatureFromFile f : featuresInGrp) { avgMass += f.getMass(); avgRt +=
 * f.getRt(); } avgMass /= featuresInGrp.size(); avgRt /= featuresInGrp.size();
 * 
 * rightAvgRts.put(grp, avgRt); rightAvgMasses.put(grp, avgMass); }
 * 
 * Map<Integer, Integer> groupsToAnneal = new HashMap<Integer, Integer>();
 * Double rtDiff = null, rtDiff2; for (Integer grp1 : groupsOnRight.keySet()) {
 * for (Integer grp2 : groupsOnLeft.keySet()) { if
 * (Math.abs(rightAvgMasses.get(grp1) - leftAvgMasses.get(grp2)) < massTol) {
 * 
 * rtDiff = Math.abs(rightAvgRts.get(grp1) - leftAvgRts.get(grp2)); if
 * (Math.abs(rtDiff) < 5 * rtTol) {
 * 
 * if (!groupsToAnneal.containsKey(grp1)) groupsToAnneal.put(grp1, grp2); else {
 * Integer grp3 = groupsToAnneal.get(grp1); rtDiff2 =
 * Math.abs(rightAvgRts.get(grp1) - leftAvgRts.get(grp3)); if (rtDiff < rtDiff2)
 * groupsToAnneal.put(grp1, grp2); } } } } }
 * 
 * System.out.println("Number of annealed groups" +
 * groupsToAnneal.keySet().size()); for (Integer grp1 : groupsToAnneal.keySet())
 * { if (grp1 == null) continue;
 * 
 * List<FeatureFromFile> featuresToMerge =
 * featuresByMatchGrpMap.get(groupsToAnneal.get(grp1));
 * 
 * for (FeatureFromFile f : featuresToMerge) { f.setRedundancyGroup(grp1);
 * featuresByMatchGrpMap.get(grp1).add(f); } }
 * 
 * return featuresByMatchGrpMap; }
 * 
 * }
 */