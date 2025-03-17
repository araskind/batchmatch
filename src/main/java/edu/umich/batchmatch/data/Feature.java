package edu.umich.batchmatch.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.utils.ObjectHandler;
import edu.umich.batchmatch.utils.StringUtils;

public class Feature {
	private String name = null;
	private Double mass = null;
	private Double rt = null, oldRt = null;
	private Double percentDefect = null;
	private Double massDefectKendrick = null;
	private Double medianIntensity = null;
	private Integer medianIntensityIdx = null;
	private String isotope = "";
	private String otherGroupIsotope = "";
	private String furtherAnnotation = "";
	private String annotation = "";
	private String otherGroupAnnotation = "";
	private Integer isotopeGroup = -1;
	private Boolean removeForClustering = false;
	private String adductOrNL = "";
	private String molecularIonNumber = "";
	private String chargeCarrier = "";
	private String neutralMass = "";
	private Double massError = null;
	protected String derivation = "";
	private Integer binIndex = null;
	private Integer offsetWithinBin = null;
	private Integer oldCluster = null;
	private Integer newCluster = null;
	private Integer newNewCluster = null;
	protected List<String> addedColValues = new ArrayList<String>();
	private double[] unadjustedIntensityList = null;
	private double[] adjustedIntensityList = null;
	private Map<Integer, Double> outlierMap = new HashMap<Integer, Double>();
	private Boolean putativeMolecularIon = false;
	private Double putativeMolecularMass = null;

	private Integer putativeCharge = 1;

	public Feature() {
		addedColValues = new ArrayList<String>();
		putativeCharge = 1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public Double getRT() {
		return rt;
	}

	public void setRT(Double rt) {
		this.rt = rt;
	}

	public Double getOldRt() {
		return oldRt;
	}

	public void setOldRt(Double oldRt) {
		this.oldRt = oldRt;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Double getPercentDefect() {
		return percentDefect;
	}

	public void setPercentDefect(Double percentDefect) {
		this.percentDefect = percentDefect;
	}

	public Double getMassDefectKendrick() {
		return massDefectKendrick;
	}

	public void setMassDefectKendrick(Double massDefectKendrick) {
		this.massDefectKendrick = massDefectKendrick;
	}

	public Double getMedianIntensity() {
		return medianIntensity;
	}

	public void setMedianIntensity(Double medianIntensity) {
		this.medianIntensity = medianIntensity;
	}

	public Integer getMedianIntensityIdx() {
		return medianIntensityIdx;
	}

	public void setMedianIntensityIdx(Integer medianIntensityIdx) {
		this.medianIntensityIdx = medianIntensityIdx;
	}

	public Integer getOldCluster() {
		return oldCluster;
	}

	public void setOldCluster(Integer oldCluster) {
		this.oldCluster = oldCluster;
	}

	public Integer getNewCluster() {
		return newCluster;
	}

	public void setNewCluster(Integer newCluster) {
		this.newCluster = newCluster;
	}

	public Integer getNewNewCluster() {
		return newNewCluster;
	}

	public void setNewNewCluster(Integer newNewCluster) {
		this.newNewCluster = newNewCluster;
	}

	public List<String> getAddedColValues() {
		return addedColValues;
	}

	public void setAddedColValues(List<String> addedColValues) {
		this.addedColValues = addedColValues;
	}

	public double[] getUnadjustedIntensityList() {
		return unadjustedIntensityList;
	}

	public void addOutlier(Integer key, Double object) {
		if (outlierMap == null)
			outlierMap = new HashMap<Integer, Double>();

		outlierMap.put(key, object);
	}

	public Map<Integer, Double> getOutlierMap() {
		return outlierMap;
	}

	public double[] getUnadjustedIntensityListWithOutliers() {
		double[] withOutlierList = new double[unadjustedIntensityList.length];

		for (int i = 0; i < unadjustedIntensityList.length; i++)
			withOutlierList[i] = outlierMap.containsKey(i) ? outlierMap.get(i) : unadjustedIntensityList[i];

		return withOutlierList;
	}

	public void setUnadjustedIntensityList(double[] unadjustedIntensityList) {
		this.unadjustedIntensityList = unadjustedIntensityList;
	}

	public double[] getAdjustedIntensityList() {
		return adjustedIntensityList;
	}

	public void setAdjustedIntensityList(double[] adjustedIntensityList) {
		this.adjustedIntensityList = adjustedIntensityList;
	}

	public String getIsotope() {
		return isotope;
	}

	public void setIsotope(String isotope) {

		this.putativeCharge = 1;
		if (isotope.contains("z"))
			try {
				int zpos = isotope.lastIndexOf("z");
				if (zpos < isotope.length() - 1) {
					Character zputstr = isotope.charAt(zpos + 1);
					if (zputstr.equals('2'))
						this.putativeCharge = 2;
					else if (zputstr.equals('3'))
						this.putativeCharge = 3;
				}
			} catch (Exception e) {
				this.putativeCharge = 1;
			}
		this.isotope = isotope;
	}

	public Integer getIsotopeGroup() {
		return isotopeGroup;
	}

	public void setIsotopeGroup(Integer isotopeGroup) {
		this.isotopeGroup = isotopeGroup;
	}

	public Boolean getRemoveForClustering() {
		return removeForClustering;
	}

	public void setRemoveForClustering(Boolean removeForClustering) {
		this.removeForClustering = removeForClustering;
	}

	public String getAdductOrNL() {
		return adductOrNL;
	}

	public void setAdductOrNL(String adductOrNL) {
		this.adductOrNL = adductOrNL;
	}

	public String getMolecularIonNumber() {
		return molecularIonNumber;
	}

	public void setMolecularIonNumber(String num) {
		this.molecularIonNumber = num;
	}

	public void setChargeCarrier(String chg) {
		this.chargeCarrier = chg;
	}

	public String getChargeCarrier() {
		return chargeCarrier;
	}

	public void setNeutralMass(String nm) {
		this.neutralMass = nm;
	}

	public String getNeutralMass() {
		return neutralMass;
	}

	public Double getMassError() {
		return massError;
	}

	public void setMassError(Double massError) {
		this.massError = massError;
	}

	public String getDerivation() {
		return derivation;
	}

	public void setDerivation(String derivation) {
		this.derivation = derivation;
	}

	public Integer getBinIndex() {
		return binIndex;
	}

	public void setBinIndex(Integer binIndex) {
		this.binIndex = binIndex;
	}

	public Integer getOffsetWithinBin() {
		return offsetWithinBin;
	}

	public void setOffsetWithinBin(Integer offsetWithinBin) {
		this.offsetWithinBin = offsetWithinBin;
	}

	public Boolean isPutativeMolecularIon() {
		return putativeMolecularIon;
	}

	public void markAsPutativeMolecularIon(Boolean putativeMolecularIon) {
		this.putativeMolecularIon = putativeMolecularIon;
	}

	public String getFurtherAnnotation() {
		return furtherAnnotation;
	}

	public void setFurtherAnnotation(String furtherAnnotation) {
		this.furtherAnnotation = furtherAnnotation;
	}

	public String printFeature() {
		return ObjectHandler.printObject(this);
	}

	public String getOtherGroupIsotope() {
		return otherGroupIsotope;
	}

	public void setOtherGroupIsotope(String otherGroupIsotope) {
		this.otherGroupIsotope = otherGroupIsotope;
	}

	public String getOtherGroupAnnotation() {
		return otherGroupAnnotation;
	}

	public void setOtherGroupAnnotation(String otherGroupAnnotation) {
		this.otherGroupAnnotation = otherGroupAnnotation;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Name " + this.getName() + " Mass = " + this.getMass() + " RT = " + this.getRT() + " Intensity "
				+ (this.getMedianIntensity() == null ? " " : this.getMedianIntensity()) + "\n");
		// sb.append("Old Cluster = " + this() + " Rebin cluster = " +
		// this.getNewCluster() + " RT Cluster cluster " + this.getNewNewCluster() +
		// "\n");

		// sb.append("Bin " + this.getBinIndex() + " Old Cluster = " +
		// this.getOldCluster() + " Rebin cluster = " + this.getNewCluster() + " RT
		// Cluster cluster " + this.getNewNewCluster() + "\n");
		// sb.append("KMD = " + this.getMassDefectKendrick() + " Adduct " +
		// this.getAdductOrNL() + " Charge carrier = " + this.getChargeCarrier() + "
		// Neutral mass = " + this.getNeutralMass()+ "\n");
		return sb.toString();
	}

	public Boolean getPutativeMolecularIon() {
		return putativeMolecularIon;
	}

	public void setPutativeMolecularIon(Boolean putativeMolecularIon) {
		this.putativeMolecularIon = putativeMolecularIon;
	}

	public Double getPutativeMolecularMass() {
		return putativeMolecularMass;
	}

	public void setPutativeMolecularMass(Double putativeMolecularMass) {
		this.putativeMolecularMass = putativeMolecularMass;
	}

	public Double initializePutativeMassByCalculating() {
		if (putativeMolecularMass != null)
			return putativeMolecularMass;

		putativeMolecularMass = null;

		try {
			if (StringUtils.isEmptyOrNull(derivation))
				return null;

			if (derivation.lastIndexOf(']') == -1)
				return null;

			Boolean haveCharge = (derivation.lastIndexOf(')') != -1);
			Character parseChar = haveCharge ? ')' : ']';
			Character beginChar = haveCharge ? '(' : '[';

			int firstChar = derivation.indexOf(beginChar);
			int lastChar = derivation.lastIndexOf(parseChar);

			String toParse = "";
			if (firstChar != -1 && lastChar != -1 && firstChar < lastChar)
				toParse = derivation.substring(firstChar + 1, lastChar);

			String[] tokens = StringUtils.splitAndTrim(toParse, "\\+", false);

			if (tokens.length < 0)
				return null;

			if (tokens.length < 2)
				tokens = StringUtils.splitAndTrim(toParse, "\\-", false);

			String rawCcToken = tokens[tokens.length - 1];

			String rawPutMassToken = tokens[0];

			if (rawPutMassToken == null || rawPutMassToken.length() < 1)
				return null;

			String trimToken = rawPutMassToken.trim();

			Double putMass = StringUtils.interpretAsDouble(trimToken.trim());
			if (putMass != null)
				this.setPutativeMolecularMass(putMass);
		} catch (Exception e) {
		}

		return putativeMolecularMass;
	}

	public void setOutlierMap(Map<Integer, Double> outlierMap) {
		this.outlierMap = outlierMap;
	}

	public Integer getPutativeCharge() {
		return putativeCharge;
	}

	protected void setPutativeCharge(Integer putativeCharge) {
		this.putativeCharge = putativeCharge;
	}
}
