////////////////////////////////////////////////////
// CompoundLibraryEntry.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CompoundLibraryEntry implements Serializable {

	private String compoundName, formula;
	private Double mass, retentionTime;
	private String notes, spectra;

	Map<String, String> otherEntries;

	public CompoundLibraryEntry() {
		otherEntries = new HashMap<String, String>();
	}

	public String getCompoundName() {
		return compoundName;
	}

	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public Double getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(Double retentionTime) {
		this.retentionTime = retentionTime;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getSpectra() {
		return spectra;
	}

	public void setSpectra(String spectra) {
		this.spectra = spectra;
	}

	public Map<String, String> getOtherEntries() {
		return otherEntries;
	}

	public void setOtherEntries(Map<String, String> otherEntries) {
		this.otherEntries = otherEntries;
	}

	public String toString() {
		// String str = ObjectHandler.printObject(this);
		// if (this.otherEntries != null)
		// str += otherEntries.toString();
		return "";
	}
}
