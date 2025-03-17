////////////////////////////////////////////////////
// FeatureFromFile.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.io.sheetreaders.MetabolomicsDataReader;
import edu.umich.batchmatch.utils.StringUtils;

public class ManifestItem extends Feature {

	// sample_id sample_type
	// sample_order ms_mode raw_file MRC2 sample ID
	// Injection time Sample Position Sample Name Plate Code
	// RunCompletedFlag Wt/Vol InstrumentName Equilib Time (min)
	// SampleLockedRunMode Balance Override Method Inj Vol (µl)
	// Dilution Sample Type StreamInfo Method Type

	//// MRC2 sample ID sample_id raw_file Injection time
	// Sample Position Sample Name sample_type sample_order
	// time batch sampleType

	private String mrc2SampleId = "", sampleId = "", rawFile = "", injectionTime = "";
	private String samplePosition = "", sampleName = "", sampleType = "", otherSampleType = "";
	private String sampleOrder = "", time = "", batchNo = "";

	// protected List<String> addedColValues = new ArrayList<String>();
	private Map<String, String> additionalColValues;

	private String compoundName, compoundFormula;
	private Double compoundMass, compoundRT;
	private Double deltaMass = null, deltaRt = null;

	private Double rsd = null;
	private Double pctMissing = null;
	private int putativeCharge = 1;
	private Integer nMissingIntensityValues = null, nTotalIntensityValues = null;
	private Integer batchIdx = null;

	private Boolean fromPosMode = null;
	private Integer matchReplicate = 0, readOrder = 0, index = 0;
	// ct of unique batches in feature's match group, feature ct for feature's match
	// group
	private Integer nMatchReplicates = 22, nMatchFeatureReplicates = 1;
	private Boolean isPossibleMatch = false;

	private List<Integer> possibleMatchGroups = new ArrayList<Integer>();
	private Integer mergedIndex = null;

	private Boolean flaggedAsDuplicate = false;
	private String possibleRedundancies = "";
	private Integer redundancyGroup = null;
	private List<String> strIntensityValues = null;
	private List<Integer> outlierIndices = null;

	private Map<String, String> intensityValuesByHeaderMap;
	private Map<String, String> outliersByHeaderMap;
	private Map<String, String> additionalLibValues;

	String lctag = "";
	Double dblValue = 0.0;
	Integer intValue = 0;
	String tag = "";
	String featureType = "PI";

	public ManifestItem() {
		super();
		additionalColValues = new HashMap<String, String>();
	}

	public ManifestItem makeDeepCopy() {
		ManifestItem destFeature = new ManifestItem();

		// private String mrc2SampleId = "", sampleId = "", rawFile = "", injectionTime
		// = "";
		// private String samplePosition = "", sampleName = "", sampleType = "",
		// otherSampleType = "";
		// private String sampleOrder = "", time = "", batchNo = "";

		destFeature.mrc2SampleId = this.mrc2SampleId;
		destFeature.sampleId = this.sampleId;
		destFeature.rawFile = this.rawFile;
		destFeature.injectionTime = this.injectionTime;
		destFeature.samplePosition = this.samplePosition;
		destFeature.sampleName = this.sampleName;
		destFeature.sampleType = this.sampleType;
		destFeature.otherSampleType = this.otherSampleType;
		destFeature.sampleOrder = this.sampleOrder;
		destFeature.time = this.time;
		destFeature.batchNo = this.batchNo;

		/*
		 * 
		 * if (this.additionalColValues == null) { destFeature.additionalColValues =
		 * null; } else { Map<String, String> additionalColValues = new HashMap<String,
		 * String>(); for (String key : this.additionalColValues.keySet()) {
		 * additionalLibValues.put(key, this.additionalColValues.get(key)); }
		 * destFeature.additionalColValues = additionalColValues; }
		 * 
		 */

		return destFeature;
	}

	/*
	 * public void transferMatchStatistics(ManifestItem feature) { this.compoundName
	 * = feature.getCompoundName(); this.compoundFormula =
	 * feature.getCompoundFormula(); this.compoundMass = feature.getCompoundMass();
	 * this.compoundRT = feature.getCompoundRT(); this.matchReplicate =
	 * feature.getMatchReplicate(); this.deltaMass = feature.getDeltaMass();
	 * this.deltaRt = feature.getDeltaRt(); }
	 */

	/*
	 * public void clearAddedColValues() { this.addedColValues = new
	 * ArrayList<String>(); }
	 */

	public void initializeBasics(ManifestItem feature) {
		this.batchIdx = feature.getBatchIdx();
		this.redundancyGroup = feature.getRedundancyGroup();
		this.putativeCharge = feature.getPutativeCharge();
		this.fromPosMode = feature.getFromPosMode();
		this.rsd = feature.getRsd();
		this.pctMissing = feature.getPctMissing();
		this.setIndex(feature.getIndex());
		this.setFeatureType(feature.getFeatureType());
		this.setOldRt(feature.getOldRt());
		this.setReadOrder(feature.getReadOrder());
		this.setName(feature.getName());
		this.setMass(feature.getMass());
		this.setRT(feature.getRT());
		this.setMassDefectKendrick(feature.getMassDefectKendrick());
		this.setMedianIntensity(feature.getMedianIntensity());
		this.setnMissingIntensityValues(feature.getnMissingIntensityValues());
		this.setnTotalIntensityValues(feature.getnTotalIntensityValues());
		this.setAdductOrNL(feature.getAdductOrNL());
		this.setAnnotation(feature.getAnnotation());
		this.setNeutralMass(feature.getNeutralMass());
		this.setChargeCarrier(feature.getChargeCarrier());
		this.setMolecularIonNumber(feature.getMolecularIonNumber());
		this.setMassError(feature.getMassError());
		this.setIsotope(feature.getIsotope());
		this.setIsotopeGroup(feature.getIsotopeGroup());
		this.setOtherGroupIsotope(feature.getOtherGroupIsotope());
		this.setPutativeMolecularIon(feature.getPutativeMolecularIon());
		this.setPutativeMolecularMass(feature.getPutativeMolecularMass());
		this.setFurtherAnnotation(feature.getFurtherAnnotation());
		this.setOtherGroupAnnotation(feature.getOtherGroupAnnotation());
		this.setDerivation(feature.getDerivation());
		this.setBinIndex(feature.getBinIndex());
		this.setOldCluster(feature.getOldCluster());
		this.setNewCluster(feature.getNewCluster());
		this.setNewNewCluster(feature.getNewNewCluster());
		this.setPossibleRedundancies(feature.getPossibleRedundancies());
		this.setFlaggedAsDuplicate(feature.getFlaggedAsDuplicate());
		this.nMatchReplicates = feature.getnMatchReplicates();
		this.nMatchFeatureReplicates = feature.getnMatchFeatureReplicates();
	}

	public void initialize(ManifestItem feature) {
		initializeBasics(feature);

		for (int i = 0; i < feature.getAddedColValues().size(); i++)
			this.addedColValues.add(feature.getAddedColValues().get(i));

		outlierIndices = new ArrayList<Integer>();
		for (int i = 0; i < feature.getOutlierIndices().size(); i++)
			outlierIndices.add(feature.getOutlierIndices().get(i));

		outliersByHeaderMap = new HashMap<String, String>();
		if (feature.getOutliersByHeaderMap() != null)
			for (String header : feature.getOutliersByHeaderMap().keySet())
				outliersByHeaderMap.put(header, feature.getOutliersByHeaderMap().get(header));

		intensityValuesByHeaderMap = new HashMap<String, String>();
		if (feature.getIntensityValuesByHeaderMap() != null)
			for (String header : feature.getIntensityValuesByHeaderMap().keySet())
				intensityValuesByHeaderMap.put(header, feature.getIntensityValuesByHeaderMap().get(header));

		getAdditionalLibValues().clear();
		for (String colName : feature.getAdditionalLibValues().keySet())
			getAdditionalLibValues().put(colName, feature.getAdditionalLibValues().get(colName));
	}

	public void updateForLibrarySearchMatch(CompoundLibraryEntry entry) {
		setCompoundName(entry.getCompoundName() == null ? "" : entry.getCompoundName());
		setCompoundFormula(entry.getFormula() == null ? "" : entry.getFormula());
		setCompoundMass(entry.getMass());
		setCompoundRT(entry.getRetentionTime());
		// setDerivation
		getAdditionalLibValues().clear();
		for (String colName : entry.getOtherEntries().keySet())
			getAdditionalLibValues().put(colName, entry.getOtherEntries().get(colName));
	}

	public boolean valueIsNumeric(String tag) {

		switch (tag) {
		case "feature":
		case "isotope":
		case "otherisotopesingroup":
		case "annotation":
		case "furtherannotation":
		case "otherannotationsingroup":
		case "derivation":
		case "chargecarrier":
		case "adductnl":
		case "compoundname":
		case "formula":
			return false;
		default:
			return true;
		}
	}

	public boolean valueIsInteger(String tag) {
		switch (tag) {

		case "index":
		case "batch":
		case "rgroup":
		case "molecularion":
		case "bin":
		case "cluster":
		case "rebincluster":
		case "matchgroup":
		case "matchreplicate":
		case "rtcluster":
			return true;
		default:
			return false;
		}
	}

	public void addIntensityValue(String value) {
		this.strIntensityValues.add(value);
	}

	public List<String> getStrIntensityValues() {
		return this.strIntensityValues;
	}

	public void setValueForHeaderTag(String tagoriginal, String value) {
		setValueForHeaderTag(tagoriginal, value, false);
	}

	public void setValueForHeaderTag(String tagoriginal, String value, Boolean trustTag) {

		if (StringUtils.isEmptyOrNull(tagoriginal))
			return;

		lctag = trustTag ? tagoriginal : StringUtils.removeSpaces(tagoriginal).toLowerCase();
		dblValue = null;
		intValue = null;
		tag = null;

		try {
			tag = MetabolomicsDataReader.headerTagMap.get(lctag);
			if (tag == null)
				tag = lctag;
			if (valueIsNumeric(tag)) {
				if (!StringUtils.isEmptyOrNull(value)) {
					try {
						if (valueIsInteger(tag))
							intValue = Integer.parseInt(value);
						else
							dblValue = Double.parseDouble(value);
					} catch (Exception e) {
						if (valueIsInteger(tag)) {
							try {
								dblValue = Double.parseDouble(value);
								intValue = (int) Math.round(dblValue);
							} catch (Exception f) {
								throw e;
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}

		switch (tag) {

		case "batch":
			this.setBatchIdx(intValue);
			break;
		case "rsd":
			this.setRsd(dblValue);
			break;
		case "pctmissing":
			this.setPctMissing(dblValue);
			break;
		case "rgroup":
			this.setRedundancyGroup(intValue);
			break;
		case "index":
			this.setIndex(intValue);
			break;
		case "feature":
			this.setName(value);
			break;
		case "mass":
			this.setMass(dblValue);
			break;
		case "rt":
			this.setRT(dblValue);
			break;
		case "oldrt":
			this.setOldRt(dblValue);
			break;
		case "intensity":
			this.setMedianIntensity(dblValue);
			break;
		case "kmd":
			this.setMassDefectKendrick(dblValue);
			break;
		case "isotope":
			this.setIsotope(value);
			break;
		case "otherisotopesingroup":
			this.setOtherGroupIsotope(value);
			break;
		case "annotation":
			this.setAnnotation(value);
			break;
		case "furtherannotation":
			this.setFurtherAnnotation(value);
			break;
		case "otherannotationsingroup":
			this.setOtherGroupAnnotation(value);
			break;
		case "derivation":
			this.setDerivation(value);
			break;
		case "putativemolecularmass":
			this.setPutativeMolecularMass(dblValue);
			break;
		case "masserror":
			this.setMassError(dblValue);
			break;
		case "molecularion":
			this.setMolecularIonNumber(value);
			break;
		case "chargecarrier":
			this.setChargeCarrier(value);
			break;
		case "adductnl":
			this.setNeutralMass(value);
			break;
		case "bin":
			this.setBinIndex(intValue);
			break;
		case "cluster":
			this.setOldCluster(intValue);
			break;
		case "rebincluster":
			this.setNewCluster(intValue);
			break;
		case "rtcluster":
			this.setNewNewCluster(intValue);
			break;

		default:
			this.getAddedColValues().add(value); // System.out.println("Added value " + value + " for feature " +
													// getName());
		}
	}

	public void setValueForCompoundHeaderTag(String tagoriginal, String value) {

		if (StringUtils.isEmptyOrNull(tagoriginal))
			return;

		lctag = StringUtils.removeSpaces(tagoriginal).toLowerCase();
		dblValue = null;
		intValue = null;
		tag = null;

		try {
			tag = MetabolomicsDataReader.headerTagMap.get(lctag);
			if (tag == null)
				tag = lctag;
			if (valueIsNumeric(tag)) {
				if (!StringUtils.isEmptyOrNull(value)) {
					try {
						if (valueIsInteger(tag))
							intValue = Integer.parseInt(value);
						else
							dblValue = Double.parseDouble(value);
					} catch (Exception e) {
						if (valueIsInteger(tag)) {
							try {
								dblValue = Double.parseDouble(value);
								intValue = (int) Math.round(dblValue);
							} catch (Exception f) {
								throw e;
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}

		switch (tag) {
		case "matchgroup":
		case "matchreplicate":
			this.setMatchReplicate(intValue);
			break;
		case "compoundname":
			this.setCompoundName(value);
			break;
		case "formula":
			this.setCompoundFormula(value);
			break;
		case "compoundmass":
			this.setCompoundMass(dblValue);
			break;
		case "compoundrt":
			this.setCompoundRT(dblValue);
			break;
		case "deltart":
			this.setDeltaRt(dblValue);
			break;
		case "deltamass":
			this.setDeltaMass(dblValue);
			break;
		}
	}

	public String getCompoundName() {
		return compoundName;
	}

	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}

	public String getCompoundFormula() {
		return compoundFormula;
	}

	public void setCompoundFormula(String compoundFormula) {
		this.compoundFormula = compoundFormula;
	}

	public Double getCompoundMass() {
		return compoundMass;
	}

	public void setCompoundMass(Double compoundMass) {
		this.compoundMass = compoundMass;
	}

	public Double getCompoundRT() {
		return compoundRT;
	}

	public void setCompoundRT(Double compoundRT) {
		this.compoundRT = compoundRT;
	}

	public Map<String, String> getAdditionalLibValues() {
		return additionalLibValues;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getMatchReplicate() {
		return matchReplicate;
	}

	public void setMatchReplicate(Integer matchReplicate) {
		this.matchReplicate = matchReplicate;
	}

	public Integer getReadOrder() {
		return readOrder;
	}

	public void setReadOrder(Integer readOrder) {
		this.readOrder = readOrder;
	}

	public Integer getnMatchReplicates() {
		return nMatchReplicates;
	}

	public void setNMatchReplicates(Integer n) {
		this.nMatchReplicates = n;
	}

	public void incrementNReplicates() {

		if (nMatchReplicates == null)
			nMatchReplicates = 1;
		else
			nMatchReplicates++;
	}

	public Integer getnMatchFeatureReplicates() {
		return nMatchFeatureReplicates;
	}

	public void setnMatchFeatureReplicates(Integer nMatchFeatureReplicates) {
		this.nMatchFeatureReplicates = nMatchFeatureReplicates;
	}

	public void incrementNMatchFeatureReplicates() {

		if (nMatchFeatureReplicates == null)
			nMatchFeatureReplicates = 1;
		else
			nMatchFeatureReplicates++;
	}

	public Double getDeltaMass() {
		return deltaMass;
	}

	public void setDeltaMass(Double deltaMass) {
		this.deltaMass = deltaMass;
	}

	public Double getDeltaRt() {
		return deltaRt;
	}

	public void setDeltaRt(Double deltaRt) {
		this.deltaRt = deltaRt;
	}

	public List<Integer> getOutlierIndices() {
		return outlierIndices;
	}

	public Integer getMergedIndex() {
		return mergedIndex;
	}

	public void setMergedIndex(Integer mergedIndex) {
		this.mergedIndex = mergedIndex;
	}

	public String getFeatureType() {
		return featureType;
	}

	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	public Boolean getFromPosMode() {
		return fromPosMode;
	}

	public void setFromPosMode(Boolean fromPosMode) {
		this.fromPosMode = fromPosMode;
	}

	public Map<String, String> getIntensityValuesByHeaderMap() {
		return intensityValuesByHeaderMap;
	}

	public Map<String, String> getOutliersByHeaderMap() {
		return outliersByHeaderMap;
	}

	public String getValueForIntensityHeader(String intensityHeader) {
		return getValueForIntensityHeader(intensityHeader, null);
	}

	public String getValueForIntensityHeader(String intensityHeader, Map<String, String> derivedColNameMap) {
		if (StringUtils.isEmptyOrNull(intensityHeader))
			return "";

		String key = StringUtils.removeSpaces(intensityHeader).toLowerCase();

		if (this.intensityValuesByHeaderMap.containsKey(key)) {
			return intensityValuesByHeaderMap.get(key);
		}

		if (derivedColNameMap != null) {
			String searchKey = intensityHeader + (this.getFromPosMode() ? "N" : "N");
			if (derivedColNameMap.containsKey(searchKey)) {
				String originalHeader = derivedColNameMap.get(searchKey);
				String originalKey = StringUtils.removeSpaces(originalHeader).toLowerCase();
				// System.out.println("In second intensity makp "+ originalHeader + " and key "
				// + originalKey);

				if (intensityValuesByHeaderMap.containsKey(originalKey))
					return intensityValuesByHeaderMap.get(originalKey);
			}

			searchKey = intensityHeader + (this.getFromPosMode() ? "P" : "P");
			if (derivedColNameMap.containsKey(searchKey)) {
				String originalHeader = derivedColNameMap.get(searchKey);
				String originalKey = StringUtils.removeSpaces(originalHeader).toLowerCase();
				// System.out.println("In second intensity makp "+ originalHeader + " and key "
				// + originalKey);

				if (intensityValuesByHeaderMap.containsKey(originalKey))
					return intensityValuesByHeaderMap.get(originalKey);
			}
		}
		return "";
	}

	public Boolean valueForHeaderIsOutlier(String intensityHeader) {
		return this.valueForHeaderIsOutlier(intensityHeader, null);
	}

	public Boolean valueForHeaderIsOutlier(String intensityHeader, Map<String, String> derivedColNameMap) {
		if (StringUtils.isEmptyOrNull(intensityHeader))
			return false;

		String key = StringUtils.removeSpaces(intensityHeader).toLowerCase();

		if (outliersByHeaderMap.containsKey(key))
			return true;

		if (derivedColNameMap != null) {
			if (derivedColNameMap.containsKey(intensityHeader + (this.getFromPosMode() ? "P" : "N"))) {
				String originalHeader = derivedColNameMap.get(intensityHeader);
				key = StringUtils.removeSpaces(originalHeader).toLowerCase();
			}
		}
		return this.outliersByHeaderMap.containsKey(key);
	}

	public Double getRsd() {
		return rsd;
	}

	public void setRsd(Double rsd) {
		this.rsd = rsd;
	}

	public Double getPctMissing() {
		return pctMissing;
	}

	public void setPctMissing(Double pctMissing) {
		this.pctMissing = pctMissing;
	}

	public Boolean getFlaggedAsDuplicate() {
		return flaggedAsDuplicate;
	}

	public void setFlaggedAsDuplicate(Boolean flaggedAsDuplicate) {
		this.flaggedAsDuplicate = flaggedAsDuplicate;
	}

	public String getPossibleRedundancies() {

		String modifier = "";
		if (possibleRedundancies.length() > 0 && !possibleRedundancies.startsWith("Possible"))
			modifier = "Possible redundancies : ";

		return (modifier + possibleRedundancies);
	}

	public void setPossibleRedundancies(String possibleRedundancies) {
		this.possibleRedundancies = possibleRedundancies;
	}

	public void cleanupDerivation() {
		if (StringUtils.isEmptyOrNull(derivation))
			return;

		String[] tokens = derivation.split("+");
	}

	public Integer getnTotalIntensityValues() {
		return nTotalIntensityValues;
	}

	public void setnTotalIntensityValues(Integer nTotalIntensityValues) {
		this.nTotalIntensityValues = nTotalIntensityValues;
	}

	public Integer getnMissingIntensityValues() {
		return nMissingIntensityValues;
	}

	public void setnMissingIntensityValues(Integer nMissingIntensityValues) {
		this.nMissingIntensityValues = nMissingIntensityValues;
	}

	public Integer getRedundancyGroup() {
		return redundancyGroup;
	}

	public void setRedundancyGroup(Integer redundancyGroup) {
		this.redundancyGroup = redundancyGroup;
	}

	public Integer getBatchIdx() {
		return batchIdx;
	}

	public void setBatchIdx(Integer batchIdx) {
		this.batchIdx = batchIdx;
	}

	public String toString() {
		String parentStr = super.toString();
		return (parentStr);// + ObjectHandler.printObject(this)); //this.getName() + " " + this.getMass() +
							// " " + this.getRT() + " " + this.getDerivation();
	}
}
