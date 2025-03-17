////////////////////////////////////////////////////
// FeatureByDeltaRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.FeatureFromFile;

public class FeatureByDeltaRtComparator implements Comparator<FeatureFromFile>, Serializable {

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getDeltaRt() == null || o2.getDeltaRt() == null)
			return -1;

		return o1.getDeltaRt().compareTo(o2.getDeltaRt());
	}
}
