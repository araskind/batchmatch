package edu.umich.mrc2.batchmatch.data.comparators.orig;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.mrc2.batchmatch.data.orig.FeatureFromFile;

public class FeatureByRedGrpMassAndRtComparator implements Comparator<FeatureFromFile>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(FeatureFromFile o1, FeatureFromFile o2) {

		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;

		Double o1RedTag = (o1.getRedundancyGroup() == null) ? 1000000.0 : o1.getRedundancyGroup();
		Double o2RedTag = (o2.getRedundancyGroup() == null) ? 1000000.0 : o2.getRedundancyGroup();

		if (o1RedTag != o2RedTag) {
			return o1RedTag.compareTo(o2RedTag);
		}
		if (o1.getMass() != o2.getMass()) {
			return o1.getMass().compareTo(o2.getMass());
		}
		return o1.getRT().compareTo(o2.getRT());
	}
}
