////////////////////////////////////////////////////
// FeatureMatchByNameStubAndMassComparator.java
// Written by Jan Wigginton September 2023
////////////////////////////////////////////////////
package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import edu.umich.batchmatch.data.FeatureMatch;

public class FeatureMatchByNameStubAndMassComparator implements Comparator<FeatureMatch>, Serializable {

	public int compare(FeatureMatch o1, FeatureMatch o2) {

		if (o1.getNameStub() == null)
			return -1;

		if (o2.getNameStub() == null)
			return 1;

		String strCompare1 = StringUtils.capitalize(o1.getNameStub());
		String strCompare2 = StringUtils.capitalize(o2.getNameStub());

		if (strCompare1 != null && strCompare1.equals(strCompare2)) {

			if (o1.getNamedMass() == null)
				return -1;

			if (o2.getNamedMass() == null)
				return 1;

			return o1.getNamedMass().compareTo(o2.getNamedMass());
		}
		return strCompare1.compareTo(strCompare2);
	}
}
