////////////////////////////////////////////////////
// FeatureByMassAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.mrc2.batchmatch.data.orig.FeatureFromFile;

public class FeatureByBatchComparator implements Comparator<FeatureFromFile>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getBatchIdx() == null || o2.getBatchIdx() == null)
			return -1;

		return o1.getBatchIdx().compareTo(o2.getBatchIdx());
	}
}
