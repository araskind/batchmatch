////////////////////////////////////////////////////////
// AnchorMap.java
// Written by Jan Wigginton and Bill Duren
// September 2019
////////////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.data.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.RtPairComparator;
import edu.umich.med.mrc2.batchmatch.utils.orig.BatchMatchDataUtils;

public class AnchorMap {

	private List<RtPair> prList;
	private String batchName1 = null, batchName2 = null;

	public AnchorMap() {
		batchName1 = null;
		prList = new ArrayList<RtPair>();
	}

	public void addRTPair(Double rt1, Double rt2) {
		prList.add(new RtPair(rt1, rt2));
		Collections.sort(prList, new RtPairComparator());
	}

	public void addRtPairs(List<RtPair> prsToAdd) {

		for (RtPair pr : prsToAdd)
			prList.add(pr);

		Collections.sort(prList, new RtPairComparator());
	}

	public void filterOutliers(Double outlierThreshold, Integer keepWindowSize, Double outlierFilteringBuddyPctCutoff,
			Double maxYToKeep, Double minYToKeep, String batchStr, Boolean chatty) {

		List<RtPair> newPairs = getTrimmedRtPairs(outlierThreshold, keepWindowSize, outlierFilteringBuddyPctCutoff,
				maxYToKeep, minYToKeep, batchStr, chatty);
		prList = newPairs;
	}

	private List<RtPair> getTrimmedRtPairs(Double outlierThreshold, Integer keepWindowSize,
			Double getOutlierFilteringBuddyPctCutoff, Double maxYToKeep, Double minYToKeep, String batchStr,
			Boolean chatty) {
		List<Double> diffsToScreen = new ArrayList<Double>();
		List<Integer> trimIndices = new ArrayList<Integer>();
		List<Double> diffXs = new ArrayList<Double>();

		if (chatty)
			System.out.println("\nFiltering pairs with pair difference less than " + minYToKeep + " or greater than "
					+ maxYToKeep);

		for (int i = 0; i < prList.size(); i++) {
			Double diff = prList.get(i).getDiff();

			if (diff > maxYToKeep || diff < minYToKeep)
				trimIndices.add(i);
			else {
				diffsToScreen.add(diff);
				diffXs.add(prList.get(i).getRt1());
			}
		}

		if (chatty) {
			int nPts = trimIndices.size();
			if (nPts > 0)
				System.out.println("Detected and removed " + nPts + " point" + (nPts != 1 ? "s" : "")
						+ " outside the allowed range\n");
			else
				System.out.println("No out of bound points identified.\n");
		}

		List<Integer> outlierPairIndices = BatchMatchDataUtils.getFilteredOutlierIndices(diffsToScreen,
				outlierThreshold, keepWindowSize, getOutlierFilteringBuddyPctCutoff);

		List<RtPair> newPairs = new ArrayList<RtPair>();
		for (int i = 0; i < prList.size(); i++) {
			if (!outlierPairIndices.contains(i) && !trimIndices.contains(i)) {
				newPairs.add(new RtPair(prList.get(i).getRt1(), prList.get(i).getRt2()));
			}
		}
		// if (chatty) {
		// System.out.println("\nAfter filtering sd outliers, there are " +
		// newPairs.size() + " points in the lattice for " + batchStr);
		// System.out.println("\n----------------------------------------------------------------\n");
		// }
		return newPairs;
	}

	public List<RtPair> getAsRtPairs() {
		List<RtPair> listCopy = new ArrayList<RtPair>();

		for (RtPair pr : prList) {
			listCopy.add(pr);
		}
		Collections.sort(listCopy, new RtPairComparator());
		return listCopy;
	}

	public void updateForEnds() {

		Double projMin = Double.MAX_VALUE, projMax = Double.MIN_VALUE;
		Double maxX = Double.MIN_VALUE, absMaxX = 40.0; // Double.MIN_VALUE;

		for (RtPair rtPair : this.prList) {

			if (rtPair.getRt1() > 80)
				continue;
			if (rtPair.getRt1() < absMaxX) {
				if (rtPair.getRt1() > maxX)
					maxX = rtPair.getRt1();

				if (rtPair.getDiff() < projMin)
					projMin = rtPair.getDiff();

				if (rtPair.getDiff() > projMax)
					projMax = rtPair.getDiff();

			}
		}

		List<RtPair> addedPairs = null;
		if (maxX < absMaxX)
			addedPairs = fillInEnds(maxX, absMaxX, projMin, projMax);

		prList.addAll(addedPairs);

		Collections.sort(prList, new RtPairComparator()); // , keyType, valueType)
	}

	private List<RtPair> fillInEnds(Double startX, Double lastSimulatedX, Double projMin, Double projMax) {

		List<RtPair> addList = new ArrayList<RtPair>();
		Double nXSteps = 2.0, yStepsPerX = 5.0;

		Double ySpan = projMax - projMin;
		projMax += .1 * ySpan;
		projMin -= .1 * ySpan;
		ySpan = projMax - projMin;

		Double yIncrement = ySpan / yStepsPerX;

		Double endX = lastSimulatedX;
		Double xSpan = endX - startX;

		Double xIncrement = xSpan / nXSteps;
		Double xSubIncrement = xIncrement / yStepsPerX;

		Double nextX = startX, nextDeltaY = null, nextY;

		for (int i = 0; i < nXSteps; i++) {
			nextX = startX + i * xIncrement;
			nextDeltaY = projMin;
			nextY = nextX + nextDeltaY;

			for (int j = 0; j < yStepsPerX; j++) {
				nextX += xSubIncrement;
				nextDeltaY += yIncrement;
				nextY = nextX + nextDeltaY;
				addList.add(new RtPair(nextX, nextY));
			}
		}

		return addList;
	}

	public String getBatchName1() {
		return batchName1;
	}

	public void setBatchName1(String batchName1) {
		this.batchName1 = batchName1;
	}

	public String getBatchName2() {
		return batchName2;
	}

	public void setBatchName2(String batchName2) {
		this.batchName2 = batchName2;
	}

	public int getSize() {
		return prList == null ? 0 : prList.size();
	}
}
