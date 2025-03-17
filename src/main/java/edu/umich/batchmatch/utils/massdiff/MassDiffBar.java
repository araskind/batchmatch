////////////////////////////////////////////////////
// MassDiffBar.java
// Written by Jan Wigginton, Oct 27, 2017
////////////////////////////////////////////////////
package edu.umich.batchmatch.utils.massdiff;

public class MassDiffBar {
	private double binValue;
	private int count;
	private int binGroup;
	private int indexInCounts;
	private boolean isAnnotated = false;
	private boolean isForced = false;

	public MassDiffBar(double value, int count, int i) {
		this(value, count, i, -1);
	}

	public MassDiffBar(double value, int count, int idx, int group) {
		this(value, count, idx, group, false);
	}

	public MassDiffBar(double value, int count, int idx, int group, boolean forced) {
		this.binValue = value;
		this.count = count;
		this.binGroup = group;
		this.indexInCounts = idx;
		this.isForced = forced;
	}

	public double getBinValue() {
		return binValue;
	}

	public int getCount() {
		return count;
	}

	public int getBinGroup() {
		return binGroup;
	}

	public void setBinValue(double binValue) {
		this.binValue = binValue;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setBinGroup(int binGroup) {
		this.binGroup = binGroup;
	}

	public int getIndexInCounts() {
		return indexInCounts;
	}

	public boolean isForced() {
		return isForced;
	}

	public void setForced(boolean isForced) {
		this.isForced = isForced;
	}
}
