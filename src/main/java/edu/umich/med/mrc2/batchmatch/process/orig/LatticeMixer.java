////////////////////////////////////////////////////
// LatticeMixer.java
// Written by Jan Wigginton and Bill Duren
// September 2019
////////////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.LatticePointSetComparator;
import edu.umich.med.mrc2.batchmatch.data.comparators.orig.RtPairComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.AnchorMap;
import edu.umich.med.mrc2.batchmatch.data.orig.LatticePointSet;
import edu.umich.med.mrc2.batchmatch.data.orig.RtPair;

public class LatticeMixer {

	List<LatticePointSet> allSets;
	Map<String, List<RtPair>> clashMap = new HashMap<String, List<RtPair>>();

	Boolean chatty = false;

	public LatticeMixer() {
	}

	public List<RtPair> mergeAndScreenDuplicates(AnchorMap map1, AnchorMap map2, Integer windowSize) {
		return mergeAndScreenDuplicates(map1, map2, windowSize, null);
	}

	public List<RtPair> mergeAndScreenDuplicates(AnchorMap map1, AnchorMap map2, Integer windowSize, String batchStr) {

		List<RtPair> allPairs = map1.getAsRtPairs();
		allPairs.addAll(map2.getAsRtPairs());
		Collections.sort(allPairs, new RtPairComparator());

		Double runningAvg = null, runningSum = null;
		Integer denom = 0;
		List<Double> runningAverages = new ArrayList<Double>();

		// System.out.println("\n\n\n================================================================================");
		// System.out.println("Integrating backtrack with existing lattice");
		// System.out.println("================================================================================\n\n");

		// String batchLabel = batchStr == null ? "" : " " + batchStr;
		// System.out.println("\n\nWhile integrating batch" + batchLabel + " backtrack
		// points, the following duplicates were resolved:");

		for (int i = 0; i < allPairs.size(); i++) {

			runningSum = 0.0;
			denom = 0;
			for (int j = 0; j < windowSize; j++) {
				if (i - j >= 0) {
					runningSum += (allPairs.get(i - j).getDiff()); // - allPairs.get(i-j).getRt1());
					denom++;
				}
			}
			for (int j = 0; j < windowSize; j++) {
				if (i + j < allPairs.size()) {
					runningSum += (allPairs.get(i + j).getDiff()); // - allPairs.get(i+j).getRt1());
					denom++;
				}
			}
			runningAvg = runningSum / (1.0 * denom);
			runningAverages.add(runningAvg);
		}

		Map<String, RtPair> keepMap = new HashMap<String, RtPair>();
		Map<String, Double> averageMap = new HashMap<String, Double>();
		clashMap = new HashMap<String, List<RtPair>>();

		for (int i = 0; i < allPairs.size(); i++) {

			String testKey = String.format("%5.3f", allPairs.get(i).getRt1());

			if (!keepMap.containsKey(testKey)) {
				keepMap.put(testKey, allPairs.get(i));
				// averageMap.put(testKey, Math.abs(allPairs.get(i).getDiff() -
				// runningAverages.get(i)));
				averageMap.put(testKey, runningAverages.get(i));
			} else {
				Double currAvg = averageMap.get(testKey);
				RtPair currPr = keepMap.get(testKey);

				Double testDivergence = Math.abs(allPairs.get(i).getDiff() - runningAverages.get(i));
				Double currDiverge = Math.abs(currPr.getDiff() - currAvg);

				Double val = runningAverages.get(i);

				// Based On Window
				if (testDivergence < currDiverge) {

					if (chatty) {
						System.out.println("\nAt RT " + testKey + ", pair diverging from the running average by "
								+ String.format("%.4f", currDiverge)
								+ " was replaced by a pair diverging from the average by "
								+ String.format("%.4f", testDivergence));

						System.out.println("Replacing : (" + String.format("%.4f", currPr.getRt1()) + ", "
								+ String.format("%.4f", currPr.getRt2()) + ") = "
								+ String.format("%.4f", currPr.getDiff()) + " with ("
								+ String.format("%.4f", allPairs.get(i).getRt1()) + ","
								+ String.format("%.4f", allPairs.get(i).getRt2()) + ") = "
								+ String.format("%.4f", allPairs.get(i).getDiff()) + ". Running average is "
								+ String.format("%.4f", currAvg));

					}
					if (!clashMap.containsKey(testKey))
						clashMap.put(testKey, new ArrayList<RtPair>());
					clashMap.get(testKey).add(currPr);

					keepMap.put(testKey, allPairs.get(i));
					// averageMap.put(testKey, testDiff);
					averageMap.put(testKey, runningAverages.get(i));
				}
			}
		}

		if (chatty && clashMap.size() == 0)
			System.out.println("No duplicate points were found.");
		// System.out.println("===========================================================================");
		// System.out.println("\nLattice integration complete" + batchLabel + ". Ready
		// to re-run merge with updated lattices.");
		// System.out.println("==========================================================================");

		if (chatty)
			System.out.println();
		List<RtPair> prunedList = new ArrayList<RtPair>();
		for (RtPair pr : keepMap.values())
			prunedList.add(pr);
		Collections.sort(prunedList, new RtPairComparator());

		return prunedList;
	}

	private List<RtPair> screenOutliers(AnchorMap map, Double cutoffDiff) {

		List<RtPair> prunedList = new ArrayList<RtPair>();
		if (map == null)
			return prunedList;

		List<RtPair> rtPairsToScreen = map.getAsRtPairs();

		Double baseDiff = 0.0;
		int nPts = 0;

		for (RtPair pr : rtPairsToScreen) {
			baseDiff += (pr.getRt2() - pr.getRt1());
			nPts++;
		}
		baseDiff /= (nPts * 1.0);

		Double upperCutoff = baseDiff + cutoffDiff;
		Double lowerCutoff = baseDiff - cutoffDiff;

		for (RtPair pr : rtPairsToScreen) {
			System.out.println("Screening for outliers " + pr.getRt1() + "," + pr.getRt2() + ","
					+ String.format("%.3f", (pr.getRt2() - pr.getRt1())));

			if ((pr.getRt2() - pr.getRt1()) > upperCutoff || (pr.getRt2() - pr.getRt1()) < lowerCutoff) {
				if (chatty)
					System.out.println("Removing " + pr.getRt1() + "," + pr.getRt2() + ","
							+ String.format("%.3f", (pr.getRt2() - pr.getRt1())));
				continue;
			}
			prunedList.add(pr);
		}
		return prunedList;
	}

	public List<RtPair> mixLatices(AnchorMap anchorMap1, AnchorMap anchorMap2, Double cutoffDiff, Double trimCutoff) {

		List<LatticePointSet> allSets = new ArrayList<LatticePointSet>();

		List<RtPair> rawPairs = anchorMap1.getAsRtPairs();

		for (int i = 0; i < anchorMap1.getAsRtPairs().size(); i++)
			if (chatty)
				System.out
						.println("RT Pairs  with outliers " + rawPairs.get(i).getRt1() + " " + rawPairs.get(i).getRt2()
								+ ", " + String.format("%.3f", (rawPairs.get(i).getRt1() - rawPairs.get(i).getRt2())));

		List<RtPair> rtPairsToMap = screenOutliers(anchorMap1, cutoffDiff);
		// based on
		System.out.println("\n\n");
		for (int i = 0; i < rtPairsToMap.size(); i++)
			System.out.println("RT Pairs to without outliers -- to integrate " + rtPairsToMap.get(i).getRt1() + " "
					+ rtPairsToMap.get(i).getRt2());

		integratePtsWithSetList(rtPairsToMap, allSets);

		System.out.println("\n\n");
		for (int i = 0; i < allSets.size(); i++)
			System.out.println("Lattice set Before integration with 2 " + allSets.get(i).getBase() + " with "
					+ allSets.get(i).getMappedPts());

		rawPairs = anchorMap2.getAsRtPairs();
		for (int i = 0; i < rawPairs.size(); i++)
			System.out.println("RT2 Pairs  with outliers " + rawPairs.get(i).getRt1() + " " + rawPairs.get(i).getRt2()
					+ ", " + String.format("%.3f", (rawPairs.get(i).getRt1() - rawPairs.get(i).getRt2())));

		List<RtPair> rtPairsToMap2 = screenOutliers(anchorMap2, cutoffDiff);
		for (int i = 0; i < rtPairsToMap2.size(); i++)
			System.out.println(
					"RT2 Pairs to integrate " + rtPairsToMap2.get(i).getRt1() + " " + rtPairsToMap2.get(i).getRt2());

		integratePtsWithSetList(rtPairsToMap2, allSets);

		System.out.println("\n\n");
		for (int i = 0; i < allSets.size(); i++) {
			System.out
					.println("Lattice set with both " + allSets.get(i).getBase() + " with " + allSets.get(i).getMappedPts());
		}

		removeDuplicates(allSets);

		System.out.println("\n\n");
		for (int i = 0; i < allSets.size(); i++) {
			System.out.println("lattce set after removng duplicates " + allSets.get(i).getBase() + " with "
					+ allSets.get(i).getMappedPts());
		}
		pruneExtremePoints(allSets, trimCutoff);

		System.out.println("\n\n");
		for (int i = 0; i < allSets.size(); i++) {
			System.out.println("lattce set after pruning extremes " + allSets.get(i).getBase() + " with "
					+ allSets.get(i).getMappedPts());
		}
		List<RtPair> prunedList = new ArrayList<RtPair>();

		for (LatticePointSet set : allSets) {
			prunedList.add(new RtPair(set.getBase(), set.fetchRepValue(true)));
		}

		System.out.println("\n\n Pruned RT Pairs");
		for (int i = 0; i < prunedList.size(); i++)
			System.out.println(prunedList.get(i).getRt1() + ", " + prunedList.get(i).getRt2() + ",  "
					+ String.format("%.3f", (prunedList.get(i).getRt2() - prunedList.get(i).getRt1())));

		return prunedList;
	}

	private void pruneExtremePoints(List<LatticePointSet> allSets, Double trimCutoff) {

		double avgDiff = 0.0;
		int nPts = 0;
		double avgLTDiff = 0.0, avgGTDiff = 0.0;
		int nPtsGT = 0, nPtsLT = 0;

		for (LatticePointSet ptSet : allSets) {
			if (ptSet == null || ptSet.getMappedPts() == null || ptSet.getMappedPts().size() == 0)
				continue;

			Double baseRt = ptSet.getBase();
			Double finalRt = ptSet.fetchRepValue(true);
			Double diff = finalRt - baseRt; // baseRt - finalRt;
			avgDiff += diff;

			if (baseRt > 10.0) {
				avgGTDiff += diff;
				nPtsGT++;
			} else {
				avgLTDiff += diff;
				nPtsLT++;
			}
			nPts++;
			System.out.println("Avg Diff is " + avgDiff / (1.0 * nPts));
		}
		avgDiff /= (1.0 * nPts);
		avgLTDiff /= (1.0 * nPtsLT);
		avgGTDiff /= (1.0 * nPtsGT);

		double upperCutoffLT = avgLTDiff + trimCutoff;
		double upperCutoffGT = avgGTDiff + trimCutoff;

		double lowerCutoffLT = avgLTDiff - trimCutoff;
		double lowerCutoffGT = avgGTDiff - trimCutoff;

		double lowerCutoff = avgDiff - trimCutoff;
		double upperCutoff = avgDiff + trimCutoff;

		for (LatticePointSet ptSet : allSets) {

			if (ptSet == null || ptSet.getMappedPts() == null || ptSet.getMappedPts().size() == 0)
				continue;

			Double baseRt = ptSet.getBase();
			Double finalRt = ptSet.fetchRepValue(true);
			Double diff = finalRt - baseRt; // baseRt - finalRt ;

			upperCutoff = baseRt > 10.0 ? upperCutoffGT : upperCutoffLT;
			lowerCutoff = baseRt > 10.0 ? lowerCutoffGT : lowerCutoffLT;
			avgDiff = baseRt > 10.0 ? avgGTDiff : avgLTDiff;

			if (diff < upperCutoff && diff > lowerCutoff)
				continue;

			if (diff >= avgDiff)
				finalRt = baseRt + avgDiff + trimCutoff;
			else
				finalRt = baseRt - trimCutoff + avgDiff;

			if (finalRt < 0.0)
				finalRt = 0.0;

			ptSet.setRepValue(finalRt);
		}
	}

	private void integratePtsWithSetList(List<RtPair> prsToAdd, List<LatticePointSet> existingSets) {

		List<LatticePointSet> setsToAdd = new ArrayList<LatticePointSet>();
		Collections.sort(prsToAdd, new RtPairComparator());

		for (RtPair pr : prsToAdd) {
			for (LatticePointSet set : existingSets) {
				if (set.haveExistingPoint(pr.getRt1())) {
					set.addValue(pr.getRt2());
					break;
				}
			}

			if (setsToAdd.size() > 1) {
				LatticePointSet lastPointSet = setsToAdd.get(setsToAdd.size() - 1);
				if (lastPointSet.getBase().equals(pr.getRt1())) {
					lastPointSet.addValue(pr.getRt2());
					continue;
				}
			}
			LatticePointSet newPointSet = new LatticePointSet(pr.getRt1(), pr.getRt2());
			setsToAdd.add(newPointSet);
		}

		for (LatticePointSet set : setsToAdd) {
			existingSets.add(set);
		}
	}

	private void removeDuplicates(List<LatticePointSet> lattice) {

		Collections.sort(lattice, new LatticePointSetComparator());

		for (int i = 0; i < lattice.size(); i++) {
			if (i > 0 && Math.abs(lattice.get(i).getBase() - lattice.get(i - 1).getBase()) < .000001) {
				for (int j = 0; j < lattice.get(i).getMappedPts().size(); j++)
					lattice.get(i - 1).getMappedPts().add(lattice.get(i).getMappedPts().get(j));

				lattice.get(i).getMappedPts().clear();
			}
		}

		ArrayList<LatticePointSet> thinnedPoints = new ArrayList<LatticePointSet>();
		for (int k = 0; k < lattice.size(); k++) {
			if (lattice.get(k).getMappedPts().size() > 0)
				thinnedPoints.add(lattice.get(k));
		}

		lattice.clear();
		for (int i = 0; i < thinnedPoints.size(); i++)
			lattice.add(thinnedPoints.get(i));

		Double prevBase = 0.0, nextBase, setRepValue = null, interpolatedB = null;
		LatticePointSet set = null, prevSet, nextSet;

		for (int i = 0; i < lattice.size(); i++) {
			set = lattice.get(i);

			int sz = set.getMappedPts().size();
			if (sz < 2)
				continue;

			prevSet = i > 0 ? lattice.get(i - 1) : null;
			nextSet = i >= (lattice.size() - 1) ? null : lattice.get(i + 1);

			prevBase = prevSet == null ? 0.0 : prevSet.getBase();
			nextBase = nextSet == null ? Double.NaN : nextSet.getBase();

			Double ASpan = nextBase - prevBase;
			Double startPt = prevSet == null ? 0.0 : prevSet.fetchRepValue(true);
			Double BSpan = nextSet.fetchRepValue(true) - startPt;

			Double pctChange = ASpan > 0.0 ? (set.getBase() - prevBase) / ASpan : 0.0;

			interpolatedB = startPt + pctChange * BSpan;

			Double minDistFromInterpolated = Double.MAX_VALUE;
			Double minPt = null;
			for (Double pt : set.getMappedPts()) {
				if (Math.abs(pt - interpolatedB) < minDistFromInterpolated) {
					minDistFromInterpolated = Math.abs(pt - interpolatedB);
					minPt = pt;
				}
			}
			set.setRepValue(minPt);
		}
	}

	public Map<String, List<RtPair>> getClashMap() {
		return clashMap;
	}

	public void setClashMap(Map<String, List<RtPair>> clashMap) {
		this.clashMap = clashMap;
	}

}
