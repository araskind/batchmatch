////////////////////////////////////////////////////
// FeatureByBinClusterAndRtComparator.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.batchmatch.data.Feature;

public class FeatureByBinClusterAndRtComparator implements Comparator<Feature>, Serializable {

	public int compare(Feature o1, Feature o2) {
		if (o1 == null || o2 == null)
			return -1;

		if (o1.getBinIndex() == null) {
			o1.setBinIndex(1);
		}

		if (o2.getBinIndex() == null) {
			o2.setBinIndex(1);
		}

		if (o1.getOldCluster() == null || o2.getOldCluster() == null) {
			return -1;
		}

		if (o1.getNewCluster() == null || o2.getNewCluster() == null) {
			return -1;
		}

		if (o1.getRT() == null || o2.getRT() == null) {
			return -1;
		}

		if (o1.getBinIndex().equals(o2.getBinIndex())) {
			if (o1.getOldCluster().equals(o2.getOldCluster())) {
				if (o1.getNewCluster().equals(o2.getNewCluster())) {
					if (o1.getNewNewCluster() == null || o2.getNewNewCluster() == null) {
						if (o1.getRT().equals(o2.getRT()))
							return -1;

						return o1.getRT().compareTo(o2.getRT());
					}

					if (o1.getNewNewCluster().equals(o2.getNewNewCluster())) {
						if (o1.getRT().equals(o2.getRT()))
							return -1;

						return o1.getRT().compareTo(o2.getRT());
					}
					return o1.getNewNewCluster().compareTo(o2.getNewNewCluster());
				}
				return o1.getNewCluster().compareTo(o2.getNewCluster());
			}
			return o1.getOldCluster().compareTo(o2.getOldCluster());
		}
		return o1.getBinIndex().compareTo(o2.getBinIndex());
	}
}
