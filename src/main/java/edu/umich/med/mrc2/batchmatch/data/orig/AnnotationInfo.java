package edu.umich.med.mrc2.batchmatch.data.orig;

import edu.umich.med.mrc2.batchmatch.utils.orig.ObjectHandler;

public class AnnotationInfo {
	private String annotation = null;
	private Double mass = null;
	private String mode = null;
	private String charge = null;
	// MERGE 05/08
	private String tier = null;

	public AnnotationInfo() {
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public String toString() {
		return ObjectHandler.printObject(this);
	}
}
