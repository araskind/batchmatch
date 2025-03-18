////////////////////////////////////////////////////
// FeatureByDeltaRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;

public class FeatureByAbsDeltaRtComparator implements Comparator<FeatureFromFile>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getDeltaRt() == null || o2.getDeltaRt() == null)
			return -1;

		Double val1 = Math.abs(o1.getDeltaRt());
		Double val2 = Math.abs(o2.getDeltaRt());

		return val1.compareTo(val2);
	}
}
