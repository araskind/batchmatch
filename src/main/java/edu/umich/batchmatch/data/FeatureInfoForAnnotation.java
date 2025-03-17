package edu.umich.batchmatch.data;

public class FeatureInfoForAnnotation {

	private Double mass = null;
	private Double medianIntensity = null;
	private Double rt = null;
	private Boolean annotated = false;
	private Integer chargeBasedOnIsotope = 0;
	private Boolean mostIntenseOfCluster = false;

	public FeatureInfoForAnnotation() {
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public Double getRt() {
		return rt;
	}

	public void setRt(Double rt) {
		this.rt = rt;
	}

	public Double getMedianIntensity() {
		return medianIntensity;
	}

	public void setMedianIntensity(Double medianIntensity) {
		this.medianIntensity = medianIntensity;
	}

	public Boolean isAnnotated() {
		return annotated;
	}

	public void markAsAnnotated() {
		this.annotated = true;
	}

	public Integer getChargeBasedOnIsotope() {
		return chargeBasedOnIsotope;
	}

	public void setChargeBasedOnIsotope(Integer chargeBasedOnIsotope) {
		this.chargeBasedOnIsotope = chargeBasedOnIsotope;
	}

	public Boolean isMostIntenseOfCluster() {
		return mostIntenseOfCluster;
	}

	public void markAsMostIntenseOfCluster(Boolean mostIntenseOfCluster) {
		this.mostIntenseOfCluster = mostIntenseOfCluster;
	}
}
