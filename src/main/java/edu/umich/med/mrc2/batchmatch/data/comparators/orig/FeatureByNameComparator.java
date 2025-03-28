////////////////////////////////////////////////////
// FeatureByNameComparator.java
// Written by Jan Wigginton November 2018
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;

public class FeatureByNameComparator implements Comparator<FeatureFromFile>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1.getName() == null || o2.getName() == null)
			return -1;

		return o1.getName().compareTo(o2.getName());
	}
}
