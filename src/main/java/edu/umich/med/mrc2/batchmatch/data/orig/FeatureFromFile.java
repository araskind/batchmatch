////////////////////////////////////////////////////
// FeatureFromFile.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.data.orig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.io.sheetreaders.BinnerInputDataHandler;
import edu.umich.med.mrc2.batchmatch.io.sheetreaders.MetabolomicsDataReader;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class FeatureFromFile extends Feature {

	private String compoundName;
	private String compoundFormula;
	private Double compoundMass;
	private Double compoundRT;
	private Double deltaMass = null;
	private Double deltaRt = null;

	private Double rsd = null;
	private Double pctMissing = null;
	private Integer nMissingIntensityValues = null;
	private Integer nTotalIntensityValues = null;
	private Integer batchIdx = null;

	private boolean fromPosMode = false;
	private int matchReplicate = 0;
	private int readOrder = 0; 
	private int index = 0;
	
	private int nMatchReplicates = 22;	// Count of unique batches in feature's match group
	private int nMatchFeatureReplicates = 1;	// Feature count for feature's match group	
	private Integer mergedIndex = null;

	private boolean flaggedAsDuplicate = false;
	private String possibleRedundancies = "";
	private Integer redundancyGroup = null;
	private List<String> strIntensityValues = null;
	private List<Integer> outlierIndices = null;

	private Map<String, String> intensityValuesByHeaderMap;
	private Map<String, String> outliersByHeaderMap;
	private Map<String, String> additionalLibValues;
	private Map<Integer, Double> batchwiseRSDs;

	private String lctag = "";
	private Double dblValue = 0.0;
	private Integer intValue = 0;
	private String tag = "";
	private String featureType = "PI";

	public FeatureFromFile() {
		super();
		strIntensityValues = new ArrayList<String>();
		additionalLibValues = new HashMap<String, String>();
		batchwiseRSDs = new HashMap<Integer, Double>();
		outlierIndices = new ArrayList<Integer>();
		intensityValuesByHeaderMap = new HashMap<String, String>();
		outliersByHeaderMap = new HashMap<String, String>();
	}

	/**
	 * Clone object
	 * @return
	 */
	public FeatureFromFile makeDeepCopy() {
		
		FeatureFromFile destFeature = new FeatureFromFile();

		destFeature.compoundName = this.compoundName;
		destFeature.compoundFormula = this.compoundFormula;
		destFeature.compoundMass = this.compoundMass;
		destFeature.compoundRT = this.compoundRT;
		destFeature.deltaMass = this.deltaMass;
		destFeature.deltaRt = this.deltaRt;
		destFeature.rsd = this.rsd;
		destFeature.pctMissing = this.pctMissing;
		destFeature.putativeCharge = this.putativeCharge;
		destFeature.nMissingIntensityValues = this.nMissingIntensityValues;
		destFeature.nTotalIntensityValues = this.nTotalIntensityValues;
		destFeature.batchIdx = this.batchIdx;
		destFeature.fromPosMode = this.fromPosMode;
		destFeature.matchReplicate = this.matchReplicate;
		destFeature.readOrder = this.readOrder;
		destFeature.index = this.index;
		destFeature.nMatchReplicates = this.nMatchReplicates;
		destFeature.nMatchFeatureReplicates = this.nMatchFeatureReplicates;
		destFeature.mergedIndex = this.mergedIndex;
		destFeature.flaggedAsDuplicate = this.flaggedAsDuplicate;
		destFeature.possibleRedundancies = this.possibleRedundancies;
		destFeature.redundancyGroup = this.redundancyGroup;

		if (this.strIntensityValues == null) {
			destFeature.strIntensityValues = null;
		} else {
			List<String> strIntensityValues = new ArrayList<String>();
			for (int i = 0; i < this.strIntensityValues.size(); i++) {
				strIntensityValues.add(this.strIntensityValues.get(i));
			}
			destFeature.strIntensityValues = strIntensityValues;
		}

		if (this.outlierIndices == null) {
			destFeature.outlierIndices = null;
		} else {
			List<Integer> outlierIndices = new ArrayList<Integer>();
			for (int i = 0; i < this.outlierIndices.size(); i++) {
				outlierIndices.add(this.outlierIndices.get(i));
			}
			destFeature.outlierIndices = outlierIndices;
		}

		if (this.intensityValuesByHeaderMap == null) {
			destFeature.intensityValuesByHeaderMap = null;
		} else {
			Map<String, String> intensityValuesByHeaderMap = new HashMap<String, String>();
			for (String key : this.intensityValuesByHeaderMap.keySet()) {
				intensityValuesByHeaderMap.put(key, this.intensityValuesByHeaderMap.get(key));
			}
			destFeature.intensityValuesByHeaderMap = intensityValuesByHeaderMap;
		}

		if (this.outliersByHeaderMap == null) {
			destFeature.outliersByHeaderMap = null;
		} else {
			Map<String, String> outliersByHeaderMap = new HashMap<String, String>();
			for (String key : this.outliersByHeaderMap.keySet()) {
				outliersByHeaderMap.put(key, this.outliersByHeaderMap.get(key));
			}
			destFeature.outliersByHeaderMap = outliersByHeaderMap;
		}

		if (this.additionalLibValues == null) {
			destFeature.additionalLibValues = null;
		} else {
			Map<String, String> additionalLibValues = new HashMap<String, String>();
			for (String key : this.additionalLibValues.keySet()) {
				additionalLibValues.put(key, this.additionalLibValues.get(key));
			}
			destFeature.additionalLibValues = additionalLibValues;
		}

		if (this.batchwiseRSDs == null) {
			destFeature.batchwiseRSDs = null;
		} else {
			Map<Integer, Double> tmpBatchwiseRSDs = new HashMap<Integer, Double>();
			for (Integer key : this.batchwiseRSDs.keySet()) {
				tmpBatchwiseRSDs.put(key, this.batchwiseRSDs.get(key));
			}
			destFeature.batchwiseRSDs = tmpBatchwiseRSDs;
		}

		destFeature.lctag = this.lctag;
		destFeature.dblValue = this.dblValue;
		destFeature.intValue = this.intValue;
		destFeature.tag = this.tag;
		destFeature.featureType = this.featureType;

		destFeature.setName(this.getName());
		destFeature.setMass(this.getMass());
		destFeature.setRT(this.getRT());
		destFeature.setOldRt(this.getOldRt());
		destFeature.setPercentDefect(this.getPercentDefect());
		destFeature.setMassDefectKendrick(this.getMassDefectKendrick());
		destFeature.setMedianIntensity(this.getMedianIntensity());
		destFeature.setMedianIntensityIdx(this.getMedianIntensityIdx());
		destFeature.setIsotope(this.getIsotope());
		destFeature.setOtherGroupIsotope(this.getOtherGroupIsotope());
		destFeature.setFurtherAnnotation(this.getFurtherAnnotation());
		destFeature.setAnnotation(this.getAnnotation());
		destFeature.setOtherGroupAnnotation(this.getOtherGroupAnnotation());
		destFeature.setIsotopeGroup(this.getIsotopeGroup());
		destFeature.setRemoveForClustering(this.getRemoveForClustering());
		destFeature.setAdductOrNL(this.getAdductOrNL());
		destFeature.setMolecularIonNumber(this.getMolecularIonNumber());
		destFeature.setChargeCarrier(this.getChargeCarrier());
		destFeature.setNeutralMass(this.getNeutralMass());
		destFeature.setMassError(this.getMassError());
		destFeature.derivation = this.derivation;
		destFeature.setBinIndex(this.getBinIndex());
		destFeature.setOffsetWithinBin(this.getOffsetWithinBin());
		destFeature.setOldCluster(this.getOldCluster());
		destFeature.setNewCluster(this.getNewCluster());
		destFeature.setNewNewCluster(this.getNewNewCluster());

		if (this.addedColValues == null) {
			destFeature.addedColValues = null;
		} else {
			List<String> addedColValues = new ArrayList<String>();
			for (int i = 0; i < this.addedColValues.size(); i++) {
				addedColValues.add(this.addedColValues.get(i));
			}
			destFeature.addedColValues = addedColValues;
		}

		if (this.getUnadjustedIntensityList() == null) {
			destFeature.setUnadjustedIntensityList(null);
		} else {
			double[] unadjustedIntensityList = new double[this.getUnadjustedIntensityList().length];
			for (int i = 0; i < this.getUnadjustedIntensityList().length; i++) {
				unadjustedIntensityList[i] = this.getUnadjustedIntensityList()[i];
			}
			destFeature.setUnadjustedIntensityList(unadjustedIntensityList);
		}

		if (this.getAdjustedIntensityList() == null) {
			destFeature.setAdjustedIntensityList(null);
		} else {
			double[] adjustedIntensityList = new double[this.getAdjustedIntensityList().length];
			for (int i = 0; i < this.getAdjustedIntensityList().length; i++) {
				adjustedIntensityList[i] = this.getAdjustedIntensityList()[i];
			}
			destFeature.setAdjustedIntensityList(adjustedIntensityList);
		}

		if (this.getOutlierMap() == null) {
			destFeature.setOutlierMap(null);
		} else {
			Map<Integer, Double> outlierMap = new HashMap<Integer, Double>();
			for (Integer key : this.getOutlierMap().keySet()) {
				outlierMap.put(key, this.getOutlierMap().get(key));
			}
			destFeature.setOutlierMap(outlierMap);
		}

		destFeature.setPutativeMolecularIon(this.getPutativeMolecularIon());
		destFeature.setPutativeMolecularMass(this.getPutativeMolecularMass());
		destFeature.setPutativeCharge(this.getPutativeCharge());

		return destFeature;
	}

	public void transferMatchStatistics(FeatureFromFile feature) {
		this.compoundName = feature.getCompoundName();
		this.compoundFormula = feature.getCompoundFormula();
		this.compoundMass = feature.getCompoundMass();
		this.compoundRT = feature.getCompoundRT();
		this.matchReplicate = feature.getMatchReplicate();
		this.deltaMass = feature.getDeltaMass();
		this.deltaRt = feature.getDeltaRt();
	}

	private void initializeBasics(FeatureFromFile feature) {
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

	public void initialize(FeatureFromFile feature) {
		
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

		batchwiseRSDs.clear();
		for (Integer batchNo : feature.batchwiseRSDs.keySet())
			batchwiseRSDs.put(batchNo, feature.getBatchwiseRSDs().get(batchNo));

	}

	private boolean valueIsNumeric(String tag) {

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
		case "featurename":
			return false;
		default:
			return true;
		}
	}

	private boolean valueIsInteger(String tag) {
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
		case "charge":
		case "rtcluster":
			return true;
		default:
			return false;
		}
	}

	public List<String> getStrIntensityValues() {
		return this.strIntensityValues;
	}

	public void setValueForHeaderTag(String tagoriginal, String value) {
		setValueForHeaderTag(tagoriginal, value, false);
	}

	private void setValueForHeaderTag(String tagoriginal, String value, boolean trustTag) {

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
			e.printStackTrace();
		}

		if(tag == null)
			return;
		
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
			this.getAddedColValues().add(value);
			break;
		}
	}

	public void setValueForBinnerHeaderTag(String tagoriginal, String value, boolean trustTag) {

		if (StringUtils.isEmptyOrNull(tagoriginal))
			return;

		lctag = trustTag ? tagoriginal : StringUtils.removeSpaces(tagoriginal).toLowerCase();
		dblValue = null;
		intValue = null;
		tag = null;

		try {
			tag = BinnerInputDataHandler.headerTagMap.get(lctag);
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
			e.printStackTrace();
		}

		if(tag == null)
			return;
		
		switch (tag) {

		case "featurename":
			this.setName(value);
			break;
		case "neutralmass":
			this.setNeutralMass(value);
			break;
		case "binnerm/z":
			this.setPutativeMolecularMass(dblValue);
			break;
		case "binnermass":
			this.setPutativeMolecularMass(dblValue);
			break;
		case "rtexpected":
			this.setRT(dblValue);
			break;
		case "rtobserved":
			this.setOldRt(dblValue);
			break;
		case "monoisotopicm/z":
			this.setMass(dblValue);
			break;
		case "charge":
			this.setPutativeCharge(intValue);
			break;

		default:
			System.out.println("Error: unrecognized binner header tag" + tag);
			break;
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
		if(tag == null)
			return;
		
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

	public Map<Integer, Double> getBatchwiseRSDs() {
		return this.batchwiseRSDs;
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
	
	public Integer getnMatchFeatureReplicates() {
		return nMatchFeatureReplicates;
	}

	public void setnMatchFeatureReplicates(Integer nMatchFeatureReplicates) {
		this.nMatchFeatureReplicates = nMatchFeatureReplicates;
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

	public boolean getFromPosMode() {
		return fromPosMode;
	}

	public void setFromPosMode(boolean fromPosMode) {
		this.fromPosMode = fromPosMode;
	}

	public Map<String, String> getIntensityValuesByHeaderMap() {
		return intensityValuesByHeaderMap;
	}

	public Map<String, String> getOutliersByHeaderMap() {
		return outliersByHeaderMap;
	}
	
	public String getValueForIntensityHeader(String intensityHeader, Map<String, String> derivedColNameMap) {
		
		if (StringUtils.isEmptyOrNull(intensityHeader))
			return "";

		String key = StringUtils.removeSpaces(intensityHeader).toLowerCase();

		if (this.intensityValuesByHeaderMap.containsKey(key)) {
			return intensityValuesByHeaderMap.get(key);
		}

		if (this.intensityValuesByHeaderMap.containsKey(intensityHeader)) {
			return intensityValuesByHeaderMap.get(intensityHeader);
		}
		//	This block is really weird, what's the point of checking for getFromPosMode() 
		//	if the same value is returned no matter what?
		if (derivedColNameMap != null) {
			String searchKey = intensityHeader + (this.getFromPosMode() ? "N" : "N");  
			if (derivedColNameMap.containsKey(searchKey)) {
				
				String originalHeader = derivedColNameMap.get(searchKey);
				String originalKey = StringUtils.removeSpaces(originalHeader).toLowerCase();
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

	public boolean valueForHeaderIsOutlier(String intensityHeader, Map<String, String> derivedColNameMap) {
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

	public void setBatchwiseRSD(Integer batchNo, Double rsd) {
		
		if (batchwiseRSDs == null)
			batchwiseRSDs = new HashMap<Integer, Double>();

		batchwiseRSDs.put(batchNo, rsd);
	}

	public void initializeMissingnessPct(List<String> orderedIntensityHeaders,
			Map<String, String> derivedColNameToHeaderMap, Integer nValuesPossible) {

		if (nValuesPossible == null) {
			nValuesPossible = orderedIntensityHeaders.size();
			for (String hdr : orderedIntensityHeaders)
				if (StringUtils.isEmptyOrNull(hdr))
					nValuesPossible--;
		}

		int nMissing = 0;
		for (String hdr : orderedIntensityHeaders) {
			if (StringUtils.isEmptyOrNull(hdr))
				continue;
			if (StringUtils.isEmptyOrNull(getValueForIntensityHeader(hdr, derivedColNameToHeaderMap)))
				nMissing++;
		}
		setPctMissing((100.0 * nMissing) / (1.0 * nValuesPossible));
	}

	public Double getPoolMissingnessPct(List<String> poolHeaders, Map<String, String> derivedColNameToHeaderMap) {

		int nMissing = 0, nValuesPossible = poolHeaders.size();
		for (String hdr : poolHeaders) {
			if (StringUtils.isEmptyOrNull(hdr))
				continue;
			if (StringUtils.isEmptyOrNull(getValueForIntensityHeader(hdr, derivedColNameToHeaderMap)))
				nMissing++;
		}
		Double missingPct = 100.0 * nMissing / (1.0 * nValuesPossible);

		return missingPct;
	}

	public Double getMassDefect() {
		Double massDefect = this.getMass() - Math.floor(this.getMass());
		return massDefect;
	}

	public void setBatchwiseRSDs(Map<Integer, Double> batchwiseRSDs) {
		this.batchwiseRSDs = batchwiseRSDs;
	}

	public Double getPctMissing() {
		return pctMissing;
	}

	public void setPctMissing(Double pctMissing) {
		this.pctMissing = pctMissing;
	}

	public boolean getFlaggedAsDuplicate() {
		return flaggedAsDuplicate;
	}

	public void setFlaggedAsDuplicate(boolean flaggedAsDuplicate) {
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

	private Integer getnTotalIntensityValues() {
		return nTotalIntensityValues;
	}

	public void setnTotalIntensityValues(Integer nTotalIntensityValues) {
		this.nTotalIntensityValues = nTotalIntensityValues;
	}

	private Integer getnMissingIntensityValues() {
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

	boolean isPIWith(String carrier) {

		if (StringUtils.isEmptyOrNull(carrier))
			return false;

		if (StringUtils.isEmptyOrNull(getAnnotation()))
			return false;

		String tryThis = getAnnotation().replace('+', 'y');

		String[] tokens = StringUtils.splitAndTrim(tryThis, "y", false);

		if (tokens == null || tokens.length != 2)
			return false;

		if (tokens[1] == null)
			return false;

		return (carrier.equals(tokens[1]));
	}

	String toCSVString2() {

		StringBuilder sb = new StringBuilder();

		sb.append((getBatchIdx() == null ? "" : getBatchIdx()) + "\t");
		sb.append((getRedundancyGroup() == null ? "" : getRedundancyGroup()) + "\t");
		sb.append(getName() == null ? "" : getName() + "\t");
		sb.append((getMass() == null ? "" : String.format("%8.4f", getMass())) + "\t");
		sb.append((getRT() == null ? "" : String.format("%5.3f", getRT())) + "\t");
		sb.append((getOldRt() == null ? String.format("%5.3f", getRT()) : String.format("%5.3f", getOldRt())) + "\t");
		sb.append(getAnnotation() == null ? "" : getAnnotation());
		return sb.toString();
	}

	public String toString() {
		String parentStr = super.toString();
		return "Group " + this.getRedundancyGroup() + "Batch " + this.getBatchIdx() + " Intensity "
				+ this.getMedianIntensity() == null ? "" : this.getMedianIntensity() + " " + (parentStr);// +
	}
}
