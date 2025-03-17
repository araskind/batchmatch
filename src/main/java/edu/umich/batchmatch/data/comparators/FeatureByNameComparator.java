////////////////////////////////////////////////////
// FeatureByNameComparator.java
// Written by Jan Wigginton November 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.FeatureFromFile;

public class FeatureByNameComparator implements Comparator<FeatureFromFile>, Serializable {

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getName() == null || o2.getName() == null)
			return -1;

		return o1.getName().compareTo(o2.getName());
	}
}
