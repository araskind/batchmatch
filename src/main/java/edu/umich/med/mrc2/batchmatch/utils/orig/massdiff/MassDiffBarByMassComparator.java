////////////////////////////////////////////////////
// MassDiffBarByMassComparator.java
// Written by Jan Wigginton, Oct 29, 2017
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.utils.orig.massdiff;

import java.util.Comparator;

public class MassDiffBarByMassComparator implements Comparator<MassDiffBar> {
	public int compare(MassDiffBar arg0, MassDiffBar arg1) {
		return new Integer(arg0.getIndexInCounts()).compareTo(new Integer(arg1.getIndexInCounts()));
	}
}