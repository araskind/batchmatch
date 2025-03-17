////////////////////////////////////////////////////
// FeatureByMassAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.FeatureFromFile;

public class FeatureByReadOrderComparator implements Comparator<FeatureFromFile>, Serializable {

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getReadOrder() == null || o2.getReadOrder() == null)
			return -1;

		return o1.getReadOrder().compareTo(o2.getReadOrder());
	}
}
