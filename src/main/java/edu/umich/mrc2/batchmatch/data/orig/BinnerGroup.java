////////////////////////////////////////////////////
// BinnerCluster.java
// Written by Jan Wigginton, Jan 24, 2017
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.data.orig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BinnerGroup implements Serializable {
	private static final long serialVersionUID = 3025928671379351173L;

	private List<Integer> featureIndexList;

	public BinnerGroup() {
		featureIndexList = new ArrayList<Integer>();
	}

	public List<Integer> getFeatureIndexList() {
		return featureIndexList;
	}

	public void setFeatureIndexList(List<Integer> featureIndexList) {
		this.featureIndexList = featureIndexList;
	}
}
