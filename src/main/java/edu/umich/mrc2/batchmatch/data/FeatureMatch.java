
////////////////////////////////////////////////////
// FeatureMatch.java
// Written by Jan Wigginton February 2023
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.data;

import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;

public class FeatureMatch {

	private String namedFeature, unnamedFeature, nameStub;
	private Boolean isPrincipalIon;
	private String annotation;
	private Double corr;
	private Integer batch;

	private Double namedMass, namedRt;
	private Double deltaCorr1, deltaCorr2, deltaCorr3;
	private Boolean isUnmapped;

	public FeatureMatch() {

	}

	public String getNamedFeature() {
		return namedFeature;
	}

	public void setNamedFeature(String namedFeature) {
		this.namedFeature = namedFeature;

		String nameMinusCorr = namedFeature.substring(0, namedFeature.lastIndexOf("_"));
		String batchStr = nameMinusCorr.substring(nameMinusCorr.lastIndexOf("-") + 1, nameMinusCorr.length());

		try {
			batch = Integer.parseInt(batchStr);
		} catch (NumberFormatException e) {
			batch = null;
		}

		try {
			// currRootName = namedName.substring(0, namedName.lastIndexOf("-"));
			nameStub = nameMinusCorr.substring(0, nameMinusCorr.lastIndexOf("-"));
			if (nameStub.startsWith("acid"))
				System.out.println("Here");
		} catch (Exception e) {
			nameStub = null;
		}
	}
	// IfPoss

	public String getUnnamedFeature() {
		return unnamedFeature;
	}

	public void setUnnamedFeature(String unnamedFeature) {
		this.unnamedFeature = unnamedFeature;
	}

	public Boolean getIsPrincipalIon() {
		return isPrincipalIon;
	}

	public void setIsPrincipalIon(Boolean isPrincipalIon) {
		this.isPrincipalIon = isPrincipalIon;
	}

	public Double getCorr() {
		return corr;
	}

	public void setCorr(Double corr) {
		this.corr = corr;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Double getNamedMass() {
		return namedMass;
	}

	public void setNamedMass(Double namedMass) {
		this.namedMass = namedMass;
	}

	public Double getNamedRt() {
		return namedRt;
	}

	public void setNamedRt(Double namedRt) {
		this.namedRt = namedRt;
	}

	public Double getDeltaCorr1() {
		return deltaCorr1;
	}

	public void setDeltaCorr1(Double deltaCorr1) {
		this.deltaCorr1 = deltaCorr1;
	}

	public Double getDeltaCorr2() {
		return deltaCorr2;
	}

	public void setDeltaCorr2(Double deltaCorr2) {
		this.deltaCorr2 = deltaCorr2;
	}

	public Double getDeltaCorr3() {
		return deltaCorr3;
	}

	public void setDeltaCorr3(Double deltaCorr3) {
		this.deltaCorr3 = deltaCorr3;
	}

	public Boolean getIsUnmapped() {
		return isUnmapped;
	}

	public void setIsUnmapped(Boolean isUnmapped) {
		this.isUnmapped = isUnmapped;
	}

	public Integer getBatch() {
		return batch;
	}

	public String getNameStub() {
		return nameStub;
	}

	public String toString() {

		String corrStr = getCorrStr();
		String namedMassStr = getNamedMassStr();
		String namedRtStr = getNamedRtStr();

		StringBuilder sb = new StringBuilder();

		sb.append(namedFeature + ", ");
		sb.append(unnamedFeature + ", ");
		sb.append(corrStr + ",");
		sb.append(namedMassStr + ",");
		sb.append(namedRtStr + ",");

		String deltaCorrStr = getDeltaCorrStr(1);
		sb.append(deltaCorrStr + ",");

		deltaCorrStr = getDeltaCorrStr(2);
		sb.append(deltaCorrStr + ",");

		deltaCorrStr = getDeltaCorrStr(3);
		sb.append(deltaCorrStr + ",");

		return sb.toString();
	}

	public String getDeltaCorrStr(int idx) {
		String deltaCorrStr = "";

		if (idx == 1)
			deltaCorrStr = (deltaCorr1 == null ? "NaN" : String.format("%.3f", deltaCorr1));

		else if (idx == 2)
			deltaCorrStr = (deltaCorr2 == null ? "NaN" : String.format("%.3f", deltaCorr2));

		else if (idx == 3)
			deltaCorrStr = (deltaCorr3 == null ? "NaN" : String.format("%.3f", deltaCorr3));

		return deltaCorrStr;
	}

	public String getNamedRtStr() {

		String namedRtStr = null;
		try {
			namedRtStr = (namedRt == null ? "NaN" : String.format("%.3f", namedRt));
		} catch (Exception e) {
			namedRtStr = "NaN";
		}
		return namedRtStr;
	}

	public String getNamedMassStr() {

		String namedMassStr = null;
		try {
			namedMassStr = (namedMass == null ? "NaN" : String.format("%.3f", namedMass));
		} catch (Exception e) {
			namedMassStr = "NaN";
		}
		return namedMassStr;
	}

	public String getCorrStr() {

		String corrStr = null;
		try {
			if (corr.equals(BatchMatchConstants.UNNAMED_ALL_MISSING))
				return BatchMatchConstants.UNNAMED_ALL_MISSING_MSG;

			if (corr.equals(BatchMatchConstants.NAMED_ALL_MISSING))
				return BatchMatchConstants.NAMED_ALL_MISSING_MSG;

			corrStr = (corr == null ? "NaN" : String.format("%.3f", corr));
		} catch (Exception e) {
			corrStr = "NaN";
		}
		return corrStr;
	}
}
