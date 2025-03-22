////////////////////////////////////////////////////
// FeatureByMassAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.med.mrc2.batchmatch.data.orig.Feature;

public class FeatureByMassAndRtComparator implements Comparator<Feature>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(Feature o1, Feature o2) {

		if (o1 == null || o1.getMass() == null)
			return -1;

		if (o2 == null || o2.getMass() == null)
			return 1;

		if (o1.getRT() == null)
			return -1;

		if (o2.getRT() == null)
			return 1;

		if (o1.getMass().equals(o2.getMass())) {
			Double value1 = o1.getRT();
			Double value2 = o2.getRT();
			return value1.compareTo(value2);
		}

		return o1.getMass().compareTo(o2.getMass());
	}

}
