package edu.umich.batchmatch.data;

public class FeatureInfoForMatchGroupMapping {

	private Integer sourceBatch, destinationBatch;
	private Double mappedRt, mappedMass;

	public FeatureInfoForMatchGroupMapping() {
	}

	public Integer getSourceBatch() {
		return sourceBatch;
	}

	public void setSourceBatch(Integer sourceBatch) {
		this.sourceBatch = sourceBatch;
	}

	public Integer getDestinationBatch() {
		return destinationBatch;
	}

	public void setDestinationBatch(Integer destinationBatch) {
		this.destinationBatch = destinationBatch;
	}

	public Double getMappedRt() {
		return mappedRt;
	}

	public void setMappedRt(Double mappedRt) {
		this.mappedRt = mappedRt;
	}

	public Double getMappedMass() {
		return mappedMass;
	}

	public void setMappedMass(Double mappedMass) {
		this.mappedMass = mappedMass;
	}
}
