package edu.umich.mrc2.batchmatch.data.orig;

public class RtPair {
	private Double rt1;
	private Double rt2;

	// Pair
	public RtPair() {
	}

	public RtPair(Double rt1, Double rt2) {
		this.rt1 = rt1;
		this.rt2 = rt2;
	}

	public Double getRt1() {
		return rt1;
	}

	public void setRt1(Double rt1) {
		this.rt1 = rt1;
	}

	public Double getRt2() {
		return rt2;
	}

	public void setRt2(Double rt2) {
		this.rt2 = rt2;
	}

	public Double getDiff() {
		return rt2 - rt1;
	}

	public Double getReverseDiff() {
		return rt1 - rt2;
	}
}
