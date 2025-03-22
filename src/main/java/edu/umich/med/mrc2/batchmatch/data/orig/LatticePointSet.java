
////////////////////////////////////////////////////////
// LatticPointSet.java
// Written by Jan Wigginton and Bill Duren
// September 2019
////////////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.data.orig;

import java.util.ArrayList;
import java.util.List;

public class LatticePointSet {

	private Double base, repValue = null;
	private Double tolerance = .0000001;
	private List<Double> mappedPts;

	public LatticePointSet() {
		this(null, null);
	}

	public LatticePointSet(Double base, Double starterValue) {
		this.base = base;
		mappedPts = new ArrayList<Double>();
		if (starterValue != null) {
			// repValue = starterValue;
			mappedPts.add(starterValue);
		}
	}

	public Boolean haveExistingPoint(Double testValue) {

		if (testValue == null)
			return false;

		Double testDiff = Math.abs(testValue - base);
		return (testDiff.compareTo(tolerance) <= 0);
	}

	public Double fetchRepValue(Boolean getMin) {

		if (repValue != null)
			return repValue;

		if (mappedPts == null)
			return null;

		if (mappedPts.size() == 1)
			return mappedPts.get(0);

		return getARepByDistFromBase(getMin);
	}

	private Double getARepByDistFromBase(Boolean getMin) {

		Double fromBasePt = mappedPts.get(0);

		if (mappedPts.size() == 1)
			return fromBasePt;

		for (Double pt : mappedPts)
			if (!getMin && Math.abs(base - pt) > fromBasePt)
				fromBasePt = pt;
			else if (Math.abs(base - pt) < fromBasePt)
				fromBasePt = pt;

		return fromBasePt;
	}

	public void addValue(Double value) {

		if (mappedPts == null)
			mappedPts = new ArrayList<Double>();

		mappedPts.add(value);
		// if (repValue == null && value != null)
		// repValue = value;
	}

	public Double getBase() {
		return base;
	}

	public void setBase(Double base) {
		this.base = base;
	}

	public Double getTolerance() {
		return tolerance;
	}

	public void setTolerance(Double tolerance) {
		this.tolerance = tolerance;
	}

	public List<Double> getMappedPts() {
		return mappedPts;
	}

	public void setMappedPts(List<Double> mappedPts) {
		this.mappedPts = mappedPts;
	}

	public Double getRepValue() {
		return repValue;
	}

	public void setRepValue(Double repValue) {
		this.repValue = repValue;
	}

}
