////////////////////////////////////////////////////
// FeatureByRtAndMassComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.med.mrc2.batchmatch.data.orig.Feature;

public class FeatureByRtOnlyComparator implements Comparator<Feature>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(Feature o1, Feature o2) {

		if (o1.getRT() == null || o2.getRT() == null)
			return -1;

		return o1.getRT().compareTo(o2.getRT());
	}
}
