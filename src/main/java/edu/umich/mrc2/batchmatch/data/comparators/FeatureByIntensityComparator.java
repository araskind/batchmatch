////////////////////////////////////////////////////
// FeatureByMassAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.mrc2.batchmatch.data.Feature;

public class FeatureByIntensityComparator implements Comparator<Feature>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(Feature o1, Feature o2) {

		if (o1 == null || o1.getMedianIntensity() == null)
			return 1;

		if (o2 == null || o2.getMedianIntensity() == null)
			return -1;

		return o1.getMedianIntensity().compareTo(o2.getMedianIntensity());
	}

}
