////////////////////////////////////////////////////
// FeatureByMatchGroupComparator.java
// Written by Jan Wigginton March 2023
////////////////////////////////////////////////////
package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.FeatureFromFile;

public class FeatureByMatchGroupComparator implements Comparator<FeatureFromFile>, Serializable {

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getRedundancyGroup() == null || o2.getRedundancyGroup() == null)
			return -1;

		return o1.getRedundancyGroup().compareTo(o2.getRedundancyGroup());
	}
}
