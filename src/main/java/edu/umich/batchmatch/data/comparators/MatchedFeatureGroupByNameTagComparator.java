////////////////////////////////////////////////////
// FeatureByMassAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import edu.umich.batchmatch.data.MatchedFeatureGroup;

public class MatchedFeatureGroupByNameTagComparator implements Comparator<MatchedFeatureGroup>, Serializable {

	public int compare(MatchedFeatureGroup o1, MatchedFeatureGroup o2) {

		String name1 = StringUtils.capitalize(o1.getNameTag());
		String name2 = StringUtils.capitalize(o2.getNameTag());

		if (name1 == null)
			return -1;

		if (name1.equals(name2)) {
			Double avgMass1 = o1.getAvgMass();
			Double avgMass2 = o2.getAvgMass();

			if (avgMass1 != null)
				return avgMass1.compareTo(avgMass2);

			else
				return name1.compareTo(name2);
		}
		return (name1.compareTo(name2));
	}
}
