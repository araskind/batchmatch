package edu.umich.mrc2.batchmatch.data;

import java.util.ArrayList;
import java.util.List;

public class IsotopeGroup {
	
	private List<Integer> featureIndexList = new ArrayList<Integer>();
	private Integer baseFeatureIndex = null;
	private Integer charge = null;

	public IsotopeGroup() {
		
	}

	public List<Integer> getFeatureIndexList() {
		return featureIndexList;
	}

	public void setFeatureIndexList(List<Integer> featureIndexList) {
		this.featureIndexList = featureIndexList;
	}

	public Integer getBaseFeatureIndex() {
		return baseFeatureIndex;
	}

	public void setBaseFeatureIndex(Integer baseFeatureIndex) {
		this.baseFeatureIndex = baseFeatureIndex;
	}

	public Integer getCharge() {
		return charge;
	}

	public void setCharge(Integer charge) {
		this.charge = charge;
	}
}
