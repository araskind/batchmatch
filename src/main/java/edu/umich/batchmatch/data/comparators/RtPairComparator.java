////////////////////////////////////////////////////
// RtPairComparator.java
// Written by Jan Wigginton, October 2019
////////////////////////////////////////////////////

package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.RtPair;

public class RtPairComparator implements Comparator<RtPair>, Serializable {

	@Override
	public int compare(RtPair pair1, RtPair pair2) {
		return (((Double) (1000000.0 * pair1.getRt1() + pair1.getRt2()))
				.compareTo(((Double) (1000000.0 * pair2.getRt1() + pair2.getRt2()))));
	}
}
