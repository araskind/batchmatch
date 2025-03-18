////////////////////////////////////////////////////
// FeatureByRtAndMassComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.mrc2.batchmatch.data.Feature;

public class FeatureByRtAndMassComparator implements Comparator<Feature>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(Feature o1, Feature o2) {

		if (o1.getRT() == null || o2.getRT() == null)
			return -1;

		if (o1.getMass() == null || o2.getMass() == null)
			return -1;

		if (o1.getRT().equals(o2.getRT())) {
			Double value1 = o1.getMass();
			Double value2 = o2.getMass();
			return value1.compareTo(value2);
		}
		return o1.getRT().compareTo(o2.getRT());
	}
}
