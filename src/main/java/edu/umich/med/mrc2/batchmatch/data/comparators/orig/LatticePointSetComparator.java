////////////////////////////////////////////////////
// LatticePointSetComparator.java
// Written by Jan Wigginton 
// October 2019
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.med.mrc2.batchmatch.data.orig.LatticePointSet;

public class LatticePointSetComparator implements Comparator<LatticePointSet>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(LatticePointSet set1, LatticePointSet set2) {

		if (set1 == null)
			return 1;

		if (set2 == null)
			return -1;

		Double base1 = 1000000.0 * set1.getBase();
		Double base2 = 1000000.0 * set2.getBase();

		return base1.compareTo(base2);
	}
}
