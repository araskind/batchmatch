package edu.umich.med.mrc2.batchmatch.data.orig;

import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;

public class BatchMatchFeatureInfo {
	private Double mass = null;
	private Double rtFromBinnerName = null;
	private Double rtFromBatchExpected = null;
	private Double rtFromBatchObserved = null;
	private Double rtFromConversion = null;
	private Double intensity = null;
	private Integer batch = null;

	public BatchMatchFeatureInfo() {
	}

	public Double getSpecifiedRt(Integer rtToUse) {
		if (BatchMatchConstants.RT_FROM_BINNER_NAME.equals(rtToUse)) {
			return getRtFromBinnerName();
		}
		if (BatchMatchConstants.RT_FROM_BATCH_EXPECTED.equals(rtToUse)) {
			return getRtFromBatchExpected();
		}
		if (BatchMatchConstants.RT_FROM_BATCH_OBSERVED.equals(rtToUse)) {
			return getRtFromBatchObserved();
		}
		if (BatchMatchConstants.RT_FROM_CONVERSION.equals(rtToUse)) {
			return getRtFromConversion();
		}
		return null;
	}

	public String getAppropriateRtTag(Integer rtToUse) {
		if (BatchMatchConstants.RT_FROM_BINNER_NAME.equals(rtToUse)) {
			return "PAR";
		}
		if (BatchMatchConstants.RT_FROM_BATCH_EXPECTED.equals(rtToUse)) {
			return "EXP";
		}
		if (BatchMatchConstants.RT_FROM_BATCH_OBSERVED.equals(rtToUse)) {
			return "OBS";
		}
		if (BatchMatchConstants.RT_FROM_CONVERSION.equals(rtToUse)) {
			return "CON";
		}
		return "UNK";
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public Double getRtFromBinnerName() {
		return rtFromBinnerName;
	}

	public void setRtFromBinnerName(Double rtFromBinnerName) {
		this.rtFromBinnerName = rtFromBinnerName;
	}

	public Double getRtFromBatchExpected() {
		return rtFromBatchExpected;
	}

	public void setRtFromBatchExpected(Double rtFromBatchExpected) {
		this.rtFromBatchExpected = rtFromBatchExpected;
	}

	public Double getRtFromBatchObserved() {
		return rtFromBatchObserved;
	}

	public void setRtFromBatchObserved(Double rtFromBatchObserved) {
		this.rtFromBatchObserved = rtFromBatchObserved;
	}

	public Double getRtFromConversion() {
		return rtFromConversion;
	}

	public void setRtFromConversion(Double rtFromConversion) {
		this.rtFromConversion = rtFromConversion;
	}

	public Double getIntensity() {
		return intensity;
	}

	public void setIntensity(Double intensity) {
		this.intensity = intensity;
	}

	public Integer getBatch() {
		return batch;
	}

	public void setBatch(Integer batch) {
		this.batch = batch;
	}
}
