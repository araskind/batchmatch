////////////////////////////////////////////////////
// CompoundLibraryEntryComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////

package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.CompoundLibraryEntry;

public class CompoundLibraryEntryComparator implements Comparator<CompoundLibraryEntry>, Serializable {

	public int compare(CompoundLibraryEntry o1, CompoundLibraryEntry o2) {

		if (o1.getMass() == null || o2.getMass() == null)
			return -1;

		if (o1.getRetentionTime() == null || o2.getRetentionTime() == null)
			return -1;

		if (o1.getMass().equals(o2.getMass())) {
			Double value1 = o1.getRetentionTime();
			Double value2 = o2.getRetentionTime();
			return value1.compareTo(value2);
		}
		return o1.getMass().compareTo(o2.getMass());
	}
}
