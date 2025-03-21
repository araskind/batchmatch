////////////////////////////////////////////////////
// RtPairComparator.java
// Written by Jan Wigginton, October 2019
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.mrc2.batchmatch.data.orig.RtPair;

public class RtPairComparator implements Comparator<RtPair>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(RtPair pair1, RtPair pair2) {
		return (((Double) (1000000.0 * pair1.getRt1() + pair1.getRt2()))
				.compareTo(((Double) (1000000.0 * pair2.getRt1() + pair2.getRt2()))));
	}
}
