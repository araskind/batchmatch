////////////////////////////////////////////////////
// FeatureByRtAndMassComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.Feature;

public class FeatureByRtOnlyComparator implements Comparator<Feature>, Serializable {

	public int compare(Feature o1, Feature o2) {

		if (o1.getRT() == null || o2.getRT() == null)
			return -1;

		return o1.getRT().compareTo(o2.getRT());
	}
}
