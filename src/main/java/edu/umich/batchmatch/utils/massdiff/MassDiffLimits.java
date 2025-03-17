////////////////////////////////////////////////////
// MassDiffLimits.java
// Written by Jan Wigginton, Oct 14, 2017
////////////////////////////////////////////////////
package edu.umich.batchmatch.utils.massdiff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.data.AnnotationInfo;

public class MassDiffLimits {
	private Map<Integer, AnnotationInfo> massDiffTargetMap;
	private List<Double> lowerLimits, upperLimits;
	private Double matchTolerance = 0.03;

	public MassDiffLimits() {
	}

	{
		massDiffTargetMap = new HashMap<Integer, AnnotationInfo>();
	}

	public MassDiffLimits(Map<Integer, AnnotationInfo> annotationMap, double massTolerance) {
		this();
		this.matchTolerance = massTolerance;
		this.initializeTargetMap(annotationMap);
	}

	public void initializeTargetMap(Map<Integer, AnnotationInfo> annotationMap) {
		lowerLimits = new ArrayList<Double>();
		upperLimits = new ArrayList<Double>();
		List<Double> values = new ArrayList<Double>();

		for (Integer key : annotationMap.keySet()) {
			AnnotationInfo info = annotationMap.get(key);
			massDiffTargetMap.put(key, info);
			values.add(Math.abs(info.getMass()));
		}

		Collections.sort(values);
		for (int i = 0; i < values.size(); i++) {
			lowerLimits.add(values.get(i) - matchTolerance);
			upperLimits.add(values.get(i) + matchTolerance);
		}
	}

	public int assignClass(double value) {
		Double compareValue = Math.abs(value);
		if (compareValue < Double.MIN_VALUE)
			return -1;
		if (lowerLimits == null)
			return -1;

		int midPt = lowerLimits.size() / 2;
		int start = compareValue >= lowerLimits.get(midPt) ? midPt : 0;

		int end = start == 0 ? midPt : lowerLimits.size();
		for (int i = start; i < end; i++)
			if (compareValue > lowerLimits.get(i))
				if (compareValue <= upperLimits.get(i))
					return i;

		return -1;
	}

	public Map<Integer, AnnotationInfo> getMassDiffTargetMap() {
		return massDiffTargetMap;
	}

	public void setMassDiffTargetMap(Map<Integer, AnnotationInfo> massDiffTargetMap) {
		this.massDiffTargetMap = massDiffTargetMap;
	}
}
