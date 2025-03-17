////////////////////////////////////////////////////
// FeatureByMassAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.MatchedFeatureGroup;

public class MatchedFeatureGroupByAvgMassComparator implements Comparator<MatchedFeatureGroup>, Serializable {

	public int compare(MatchedFeatureGroup o1, MatchedFeatureGroup o2) {

		Double avgMass1 = o1.getAvgMass();
		Double avgMass2 = o2.getAvgMass();

		if (avgMass1 == null)
			avgMass1 = o1.updateAvgMass();

		if (avgMass2 == null)
			avgMass2 = o2.updateAvgMass();

		if (avgMass1 == null)
			return -1;

		if (avgMass2 == null)
			return 1;

		return (avgMass1.compareTo(avgMass2));
	}
}
