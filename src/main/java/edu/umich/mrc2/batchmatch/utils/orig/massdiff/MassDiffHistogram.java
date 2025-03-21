////////////////////////////////////////////////////
// MassDiffHistogram.java
// Written by Jan Wigginton, October 14, 2017
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.utils.orig.massdiff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;

import edu.umich.mrc2.batchmatch.data.orig.AnnotationInfo;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.utils.orig.BinnerTestUtils;


//import edu.umich.wiggie.util.BinnerConstants;

public class MassDiffHistogram {
	private double multiplier = 1000.0, upperThreshold = BinnerConstants.LARGEST_MASS_DIFF, trackingCutoff;
	private int maxSlotCount, secondMaxSlotCount, topPercentile, nextPercentile;
	private Integer nAnnotatedValues = 0, nValues = 0, nBarSlots = 0, nTotalPoints;

	private List<MassDiffBarGroup> histogramGroups;
	private int[] countsBySlot;
	private Boolean isDeisotoped = true;

	public MassDiffHistogram() {
		countsBySlot = new int[(int) (1 + multiplier * upperThreshold)];
	}

	public void addCounts(RealMatrix rawMassDiffs, Double min, Double max) {
		if (rawMassDiffs == null)
			return;

		int nRows = rawMassDiffs.getRowDimension();

		for (int i = 0; i < nRows; i++)
			for (int j = 0; j < i; j++) {
				int valueSlot = getHistogramSlotForMassDiffValue(rawMassDiffs.getEntry(i, j));

				if (valueSlot != -1 && valueSlot <= upperThreshold * multiplier)
					countsBySlot[valueSlot]++;
			}
	}

	public int getHistogramSlotForMassDiffValue(double x) {
		double pos = Math.abs(x);

		BigDecimal trunc = new BigDecimal(String.valueOf(pos)).setScale(3, BigDecimal.ROUND_FLOOR);

		String strVal = trunc.toString();

		Double dblVal = Double.parseDouble(strVal);

		if (dblVal < Double.MIN_VALUE)
			return 0;

		dblVal *= 1000;

		// bump to deal with slight numeric discrepancy in parseDouble (leads to
		// X.99999999999999... instead of .0 for ints)
		dblVal += .000001;

		int returnVal = dblVal.intValue();

		return (returnVal > upperThreshold * multiplier ? -1 : returnVal);
	}

	public void generateHistogram(Map<Integer, AnnotationInfo> annotFileMap, Double minValue, Double maxValue) {
		Map<Integer, Integer> annotatedHistogramSlots = new HashMap<Integer, Integer>();
		Map<Integer, Integer> slotsForColoredValues = new HashMap<Integer, Integer>();

		System.out.println("Tracking cutoff was " + trackingCutoff);
		int maxValueSlot = -1;
		if (annotFileMap != null)
			for (AnnotationInfo info : annotFileMap.values()) {
				if (Math.abs(info.getMass()) > this.trackingCutoff)
					continue;

				Integer valueSlot = getHistogramSlotForMassDiffValue(Math.abs(info.getMass()));
				annotatedHistogramSlots.put(valueSlot, 0);
				for (Integer i = 0; i < 10; i++) {
					annotatedHistogramSlots.put(valueSlot + i, 0);
					annotatedHistogramSlots.put(valueSlot - i, 0);
					slotsForColoredValues.put(valueSlot + i, 0);
					slotsForColoredValues.put(valueSlot + i, 0);
				}
				maxValueSlot = Math.max(maxValueSlot, valueSlot + 10);

				for (Integer i = 10; i < 30; i++) {
					slotsForColoredValues.put(valueSlot + i, 0);
					slotsForColoredValues.put(valueSlot + i, 0);
				}
			}

		maxValueSlot = Math.min(countsBySlot.length, maxValueSlot);

		nTotalPoints = 0;
		int denseCutoffIdx = Math.min(((int) (BinnerConstants.DENSE_MASS_DIFF_THRESHOLD * multiplier)),
				countsBySlot.length);
		int trackingCutoffIdx = maxValueSlot;
		denseCutoffIdx = (int) Math.min(denseCutoffIdx, trackingCutoffIdx);

		for (int i = 0; i < countsBySlot.length; i++)
			nTotalPoints += countsBySlot[i];

		BinnerTestUtils.printArray(countsBySlot);

		double expectedCount = nTotalPoints / denseCutoffIdx + 0.5;

		System.out.println("The tracking cutoff is " + trackingCutoff);
		System.out.println("Total count " + nTotalPoints + " nSlots " + countsBySlot.length + " implies expectation "
				+ expectedCount);
		List<MassDiffBar> ungroupedBars = new ArrayList<MassDiffBar>();
		nValues = 0;
		nAnnotatedValues = 0;
		for (int i = 0; i < countsBySlot.length; i++) {
			if (i > trackingCutoffIdx)
				break;

			if (countsBySlot[i] > 3 * expectedCount) {
				nValues += countsBySlot[i];
				ungroupedBars.add(new MassDiffBar(i / multiplier, countsBySlot[i], i));
			} else if (annotatedHistogramSlots.get(new Integer(i)) != null) {
				nValues += countsBySlot[i];
				ungroupedBars.add(new MassDiffBar(i / multiplier, countsBySlot[i], i));
			}

			if (slotsForColoredValues.get(new Integer(i)) != null)
				nAnnotatedValues += countsBySlot[i];
		}

		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		int maxPtsPerSlot = 0;
		for (MassDiffBar bar : ungroupedBars) {
			if (countMap.get(bar.getCount()) == null)
				countMap.put(bar.getCount(), 0);

			int nBars = countMap.get(bar.getCount());
			maxPtsPerSlot = Math.max(bar.getCount(), maxPtsPerSlot);
			countMap.put(bar.getCount(), nBars + 1);
		}

		// for (Integer key : countMap.keySet())
		// System.out.println(countMap.get(key) + " bins have " + key + " points");

		Integer cutoff25 = 0, cutoff50, cutoff75, cutoff90;
		Integer slotMembers = maxPtsPerSlot;
		int totalLessThan = 0;

		while (totalLessThan < 0.02 * nValues) {
			totalLessThan += (countMap.get(slotMembers) == null ? 0 : countMap.get(slotMembers) * slotMembers);
			slotMembers--;
		}
		cutoff90 = slotMembers;
		this.topPercentile = cutoff90;
		System.out.println(
				totalLessThan + " (" + (totalLessThan * 1.0) / (nValues * 1.0) + ") are greater than " + cutoff90);

		while (totalLessThan < 0.05 * nValues) {
			totalLessThan += countMap.get(slotMembers) == null ? 0 : countMap.get(slotMembers) * slotMembers;
			slotMembers--;
		}
		cutoff75 = slotMembers;
		this.nextPercentile = cutoff75;
		System.out.println(
				totalLessThan + " (" + (totalLessThan * 1.0) / (nValues * 1.0) + ") are greater than " + cutoff75);

		while (totalLessThan < 0.5 * nValues) {
			totalLessThan += countMap.get(slotMembers) == null ? 0 : countMap.get(slotMembers) * slotMembers;
			slotMembers--;
		}
		cutoff50 = slotMembers;
		System.out.println(
				totalLessThan + " (" + (totalLessThan * 1.0) / (nValues * 1.0) + ") are greater than " + cutoff50);

		while (totalLessThan < 0.75 * nValues) {
			totalLessThan += countMap.get(slotMembers) == null ? 0 : countMap.get(slotMembers) * slotMembers;
			slotMembers--;
		}
		cutoff25 = slotMembers;
		System.out.println(
				totalLessThan + " (" + (totalLessThan * 1.0) / (nValues * 1.0) + ") are greater than " + cutoff25);

		nBarSlots = 0;
		ungroupedBars = new ArrayList<MassDiffBar>();
		for (int i = 0; i < countsBySlot.length; i++)
			if (countsBySlot[i] > cutoff75) {
				ungroupedBars.add(new MassDiffBar(i / multiplier, countsBySlot[i], i));
				nBarSlots += countsBySlot[i];
			} else if (annotatedHistogramSlots.get(new Integer(i)) != null) {
				ungroupedBars.add(new MassDiffBar(i / multiplier, countsBySlot[i], i, -1, true));
			}

		groupBars(ungroupedBars);
		if (annotFileMap != null)
			annotateGroups(annotFileMap);
	}

	private void annotateGroups(Map<Integer, AnnotationInfo> annotFileMap) {
		for (MassDiffBarGroup group : this.histogramGroups) {
			for (AnnotationInfo info : annotFileMap.values()) {
				double annotatedValue = Math.abs(info.getMass());
				if (annotatedValue > trackingCutoff)
					continue;
				if (annotatedValue < group.getGroupMin())
					continue;

				if (annotatedValue > group.getGroupMax())
					continue;

				group.setAnnotation(info.getAnnotation());
				group.setAnnotatedValue(String.format("%.3f", info.getMass()));
			}
		}
	}

	private void groupBars(List<MassDiffBar> significantBars) {
		if (significantBars == null || significantBars.size() < 1)
			return;

		secondMaxSlotCount = -1;
		histogramGroups = new ArrayList<MassDiffBarGroup>();
		MassDiffBarGroup firstGroup = new MassDiffBarGroup();
		histogramGroups.add(firstGroup);
		firstGroup.addSlot(significantBars.get(0));

		MassDiffBar prevBar = significantBars.get(0);
		int nGroups = 1;

		Collections.sort(significantBars, new MassDiffBarByMassComparator());
		for (int bar = 1; bar < significantBars.size(); bar++) {
			MassDiffBar currentBar = significantBars.get(bar);

			if (currentBar.getIndexInCounts() - prevBar.getIndexInCounts() > 10) {
				nGroups++;
				histogramGroups.add(new MassDiffBarGroup());
			}
			MassDiffBarGroup currentGroup = histogramGroups.get(nGroups - 1);
			currentGroup.addSlot(currentBar);
			if (currentBar.getCount() > maxSlotCount) {
				secondMaxSlotCount = maxSlotCount;
				maxSlotCount = currentBar.getCount();
			} else
				secondMaxSlotCount = Math.max(currentBar.getCount(), secondMaxSlotCount);

			prevBar = currentBar;
		}

		for (int group = 0; group < histogramGroups.size(); group++)
			histogramGroups.get(group).fillInBarRange();
	}

	public List<MassDiffBarGroup> getHistogramGroups() {
		return histogramGroups;
	}

	public void setHistogramGroups(List<MassDiffBarGroup> histogramGroups) {
		this.histogramGroups = histogramGroups;
	}

	public int getMaxSlotCount() {
		return maxSlotCount;
	}

	public int getSecondMaxSlotCount() {
		return secondMaxSlotCount;
	}

	public void setSecondMaxSlotCount(int secondMaxSlotCount) {
		this.secondMaxSlotCount = secondMaxSlotCount;
	}

	public void setMaxSlotCount(int maxSlotCount) {
		this.maxSlotCount = maxSlotCount;
	}

	public int getTopPercentile() {
		return topPercentile;
	}

	public int getNextPercentile() {
		return nextPercentile;
	}

	public void setTopPercentile(int topPercentile) {
		this.topPercentile = topPercentile;
	}

	public void setNextPercentile(int nextPercentile) {
		this.nextPercentile = nextPercentile;
	}

	public Integer getnAnnotatedValues() {
		return nAnnotatedValues;
	}

	public void setnAnnotatedValues(Integer nAnnotatedValues) {
		this.nAnnotatedValues = nAnnotatedValues;
	}

	public Integer getnValues() {
		return nValues;
	}

	public void setnValues(Integer nValues) {
		this.nValues = nValues;
	}

	public Integer getnBarSlots() {
		return nBarSlots;
	}

	public void setnBarSlots(Integer nBarSlots) {
		this.nBarSlots = nBarSlots;
	}

	public Integer getnTotalPoints() {
		return nTotalPoints;
	}

	public void setnTotalPoints(Integer nTotalPoints) {
		this.nTotalPoints = nTotalPoints;
	}

	public Boolean getIsDeisotoped() {
		return isDeisotoped;
	}

	public void setIsDeisotoped(Boolean isDeisotoped) {
		this.isDeisotoped = isDeisotoped;
	}

	// MERGE 03/31
	public double getTrackingCutoff() {
		return trackingCutoff;
	}

	public void setTrackingCutoff(double trackingCutoff) {
		this.trackingCutoff = Math.min(upperThreshold, trackingCutoff);
	}
}
