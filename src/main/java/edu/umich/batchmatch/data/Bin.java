package edu.umich.batchmatch.data;

import java.util.List;

public class Bin {
	private List<Integer> featureOrderByRT = null;
	private List<Integer> featureOrderByCluster = null;

	public Bin() {
	}

	public List<Integer> getFeatureOrderByRT() {
		return featureOrderByRT;
	}

	public void setFeatureOrderByRT(List<Integer> featureOrderByRT) {
		this.featureOrderByRT = featureOrderByRT;
	}

	public List<Integer> getFeatureOrderByCluster() {
		return featureOrderByCluster;
	}

	public void setFeatureOrderByCluster(List<Integer> featureOrderByCluster) {
		this.featureOrderByCluster = featureOrderByCluster;
	}
}
