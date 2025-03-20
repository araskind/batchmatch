////////////////////////////////////////////////////
// MatchedFeatureGroup.java
// Written by Jan Wigginton 
// August 2020
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.comparators.FeatureByBatchComparator;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.utils.ListUtils;
import edu.umich.mrc2.batchmatch.utils.StringUtils;

public class MatchedFeatureGroup {

	private Integer matchGrpKey;
	private String batchesStr = "";
	private String nameTag = null;
	private List<FeatureFromFile> featuresInGroup;

	private Double avgMass = null;
	private Double avgRt = null, avgOldRt = null;
	private Double massDiff = null, oldRtDiff = null, rtDiff = null;
	private Double avgMedIntensity = null, totalIntensity = null, adjustedTotalIntensity = null;

	public MatchedFeatureGroup(int key, List<FeatureFromFile> features) {
		this(key, features, false);
	}

	public MatchedFeatureGroup(int key, List<FeatureFromFile> features, Boolean handleCorrs) {
		this.matchGrpKey = key;
		this.featuresInGroup = features;
		StringBuilder sb = new StringBuilder();
		Collections.sort(features, new FeatureByBatchComparator());
		for (int i = 0; i < features.size(); i++) 
			sb.append(features.get(i).getBatchIdx() + "_");
		
		batchesStr = sb.toString();

		if (handleCorrs)
			ensureHighestCorrs(handleCorrs);
		
		updateStats();
	}

	public MatchedFeatureGroup(MatchedFeatureGroup grp1, MatchedFeatureGroup grp2) {
		this(grp1, grp2, false);
	}

	public MatchedFeatureGroup(MatchedFeatureGroup grp1, MatchedFeatureGroup grp2, Boolean handleCorrs) {
		matchGrpKey = grp1.getMatchGrpKey();
		featuresInGroup = new ArrayList<FeatureFromFile>();
		for (FeatureFromFile f : grp2.getFeaturesInGroup())
			f.setFurtherAnnotation(
					(StringUtils.isEmptyOrNull(f.getFurtherAnnotation()) ? "" : f.getFurtherAnnotation() + "; ")
							+ " Merged from match group " + grp2.getMatchGrpKey() + " ");

		// Signature is
		addFeatures(grp1.getFeaturesInGroup());
		addFeatures(grp2.getFeaturesInGroup());

		for (FeatureFromFile f : featuresInGroup)
			f.setRedundancyGroup(matchGrpKey);

		if (handleCorrs)
			ensureHighestCorrs();

		updateStats();
	}

	public int getNAnnotations() {
		int nAnnotations = 0;
		for (FeatureFromFile f : this.getFeaturesInGroup()) {
			if (!StringUtils.isEmptyOrNull(f.getAnnotation()))
				nAnnotations++;
		}
		return nAnnotations;
	}

	public int countPIsWith(String carrier) {

		int nNaPIs = 0;
		for (FeatureFromFile f : this.getFeaturesInGroup()) {
			if (f.isPIWith(carrier))
				nNaPIs++;
		}
		return nNaPIs;
	}

	private void ensureHighestCorrs() {
		ensureHighestCorrs(false);
	}

	void ensureHighestCorrs(Boolean allowIncomplete) {

		if (this.countMatchedTargetFeatures(null) < 1)
			return;

		Boolean haveAlternateMatches = false;
		for (FeatureFromFile f : this.getFeaturesInGroup()) {
			if (StringUtils.isEmptyOrNull(f.getIsotope()))
				continue;
			haveAlternateMatches = true;
			break;
		}

		if (!haveAlternateMatches)
			return;

		int completeSetSize = this.getUniqueBatches().size();
		int nFeatures = this.getFeatureNames().size();

		// ambiguous sets not presently handled
		if (nFeatures > completeSetSize)
			return;

		Map<String, Map<Integer, Double>> compoundBatchCorrMap = buildCompoundBatchCorrMap();

		List<String> fullyMatchedCompounds = new ArrayList<String>();
		for (String compound : compoundBatchCorrMap.keySet()) {
			Map<Integer, Double> batchCorrMap = compoundBatchCorrMap.get(compound);

			if (allowIncomplete || batchCorrMap.keySet().size() == completeSetSize)
				fullyMatchedCompounds.add(compound);
		}

		if (fullyMatchedCompounds.size() == 0)
			return;

		Double maxCompoundCorr = Double.MIN_VALUE;
		String compoundToKeep = null;
		for (String compound : fullyMatchedCompounds) {

			Double avgCompoundCorr = 0.0;
			Map<Integer, Double> batchCorrMap = compoundBatchCorrMap.get(compound);
			Integer nCorrs = 0;
			for (Double corr : batchCorrMap.values()) {
				if (corr == null || corr.isNaN())
					continue;

				avgCompoundCorr += corr;
				nCorrs++;
			}
			if (nCorrs > 1)
				avgCompoundCorr /= (1.0 * nCorrs);

			if (avgCompoundCorr > maxCompoundCorr) {
				maxCompoundCorr = avgCompoundCorr;
				compoundToKeep = compound;
			}
		}

		Map<Integer, Double> batchCorrMap = compoundBatchCorrMap.get(compoundToKeep);
		if (batchCorrMap == null)
			return;

		for (FeatureFromFile f : this.featuresInGroup) {
			String oldIsotopeStr = f.getIsotope();
			if (StringUtils.isEmptyOrNull(oldIsotopeStr))
				continue;

			String oldName = f.getName();
			Double corr = batchCorrMap.get(f.getBatchIdx());
			String corrString = String.format("%.3f", corr);

			String newName = compoundToKeep + "-" + f.getBatchIdx() + "_" + corrString;
			f.setName(newName);
			// f.setIsotope(oldIsotopeStr + BatchMatchConstants.ALTERNATE_ISOTOPE_SEP +
			// oldName);
		}
	}

	private Map<String, Map<Integer, Double>> buildCompoundBatchCorrMap() {

		Map<String, Map<Integer, Double>> compoundBatchCorrMap = new HashMap<String, Map<Integer, Double>>();

		for (FeatureFromFile f : this.getFeaturesInGroup()) {

			parseMatchEntry(f.getName(), compoundBatchCorrMap);

			if (!StringUtils.isEmptyOrNull(f.getIsotope())) {
				List<String> alternateMatchTokens = StringUtils.getAsArrayList(f.getIsotope(),
						BatchMatchConstants.ALTERNATE_ISOTOPE_SEP);

				for (int i = 0; i < alternateMatchTokens.size(); i++) {
					parseMatchEntry(alternateMatchTokens.get(i), compoundBatchCorrMap);
				}
			}
		}
		return compoundBatchCorrMap;
	}

	private void parseMatchEntry(String stringToParse, Map<String, Map<Integer, Double>> compoundBatchCorrMap) {

		if (StringUtils.isEmptyOrNull(stringToParse))
			return;

		// nul
		// First shave off corrrelation
		List<String> allTokens = StringUtils.getAsArrayList(stringToParse, "_");
		if (allTokens.size() < 2)
			return;

		String corrString = allTokens.get(allTokens.size() - 1);
		if (StringUtils.isEmptyOrNull(corrString))
			return;

		// rest of string should contain compound name_batch
		String shavedTokenToParse = stringToParse.substring(0, stringToParse.lastIndexOf('_'));
		if (StringUtils.isEmptyOrNull(corrString) || StringUtils.isEmptyOrNull(stringToParse))
			return;

		List<String> nameBatchTokens = StringUtils.getAsArrayList(shavedTokenToParse, "-");
		if (nameBatchTokens.size() < 2)
			return;

		// "nul"
		String batchString = nameBatchTokens.get(nameBatchTokens.size() - 1);
		if (StringUtils.isEmptyOrNull(batchString))
			return;

		String compoundString = shavedTokenToParse.substring(0, shavedTokenToParse.lastIndexOf('-'));
		if (StringUtils.isEmptyOrNull(compoundString))
			return;

		try {
			Integer batch = Integer.parseInt(batchString);
			Double corr = Double.parseDouble(corrString);

			if (!compoundBatchCorrMap.containsKey(compoundString))
				compoundBatchCorrMap.put(compoundString, new HashMap<Integer, Double>());

			compoundBatchCorrMap.get(compoundString).put(batch, corr);
		} catch (Exception e) {
			System.out.println(
					"Error while parsing named tokens" + compoundString + " " + corrString + " " + batchString);
			return;
		}
	}

	public List<String> getFeatureNames() {
		List<String> featureNames = new ArrayList<String>();

		for (int i = 0; i < featuresInGroup.size(); i++) {
			featureNames.add(featuresInGroup.get(i).getName());
		}
		return featureNames;
	}

	public String getNameTag() {

		if (!StringUtils.isEmptyOrNull(nameTag))
			return nameTag;

		List<String> featureNames = getFeatureNames();

		String nameTag = null;
		for (int i = 0; i < featureNames.size(); i++) {
			if (featureNames.get(i).startsWith("UNK"))
				continue;
			// nameTag = featureNames.get(i).substring(0, featureNames.get(i).length() - 8);

			nameTag = featureNames.get(i).substring(0, featureNames.get(i).lastIndexOf("-"));

		}
		if (StringUtils.isEmptyOrNull(nameTag))
			nameTag = featureNames.get(0);

		return nameTag;
	}

	public Double getAvgMass() {

		int nNulls = 0;
		Boolean isNull = false;
		if (avgMass == null) {
			avgMass = 0.0;
			for (FeatureFromFile f : featuresInGroup) {
				isNull = (f.getMass() == null);

				avgMass += (isNull ? 0.0 : f.getMass());

				if (isNull)
					nNulls++;
			}

			if (nNulls < featuresInGroup.size())
				avgMass /= 1.0 * (featuresInGroup.size() - nNulls);
			else
				avgMass = null;
		}
		return avgMass;
	}

	public Double updateAvgMass() {
		avgMass = null;
		return getAvgMass();
	}

	public List<Integer> getBatches() {

		List<Integer> list = new ArrayList<Integer>();
		for (FeatureFromFile f : featuresInGroup) {
			list.add(f.getBatchIdx());
		}
		return list;
	}

	public List<Integer> getUniqueBatches() {

		List<Integer> allBatches = getBatches();

		return ListUtils.uniqueEntries(allBatches);
	}

	public String getBatchesStr() {
		return batchesStr;
	}

	public void setBatchesStr(String batchesStr) {
		this.batchesStr = batchesStr;
	}

	public Double getAvgRt() {

		if (avgRt == null) {
			avgRt = 0.0;
			for (FeatureFromFile f : featuresInGroup)
				avgRt += f.getRT();

			avgRt /= featuresInGroup.size();
		}

		return avgRt;
	}

	public Double getTotalIntensity() {

		if (totalIntensity == null) {
			totalIntensity = 0.0;

			for (FeatureFromFile f : featuresInGroup) {
				totalIntensity += (f.getMedianIntensity() == null ? 0 : f.getMedianIntensity());
			}
		}
		return totalIntensity;
	}

	private Double updateTotalIntensity() {
		totalIntensity = null;
		return getTotalIntensity();
	}

	public Double getAvgMedIntensity() {

		if (avgMedIntensity == null) {
			avgMedIntensity = 0.0;

			int nMeasured = 0;
			for (FeatureFromFile f : featuresInGroup) {
				avgMedIntensity += (f.getMedianIntensity() == null ? 0 : f.getMedianIntensity());
				nMeasured++;
			}
			if (nMeasured > 0)
				avgMedIntensity /= (1.0 * nMeasured);
		}
		return avgMedIntensity;
	}

	private Double updateAvgMedIntensity() {
		avgMedIntensity = null;
		return getAvgMedIntensity();
	}

	private Double updateAvgRt() {
		avgRt = null;
		return getAvgRt();
	}

	public Double getAvgOldRt() {

		if (avgOldRt == null) {
			avgOldRt = 0.0;
			for (FeatureFromFile f : featuresInGroup)
				avgOldRt += f.getOldRt() == null ? f.getRT() : f.getOldRt();

			avgOldRt /= featuresInGroup.size();
		}
		return avgOldRt;
	}

	public Double getAdjustedTotalIntensity() {
		return getAvgMedIntensity() * featuresInGroup.size();
	}

	private Double updateAvgOldRt() {
		avgOldRt = null;
		return getAvgOldRt();
	}

	private void addFeatures(List<FeatureFromFile> lst) {

		for (FeatureFromFile f : lst)
			featuresInGroup.add(f);
		updateStats();
	}

	private void updateStats() {
		nameTag = null;
		updateAvgOldRt();
		updateAvgRt();
		updateAvgMass();
		updateAvgMedIntensity();
		updateTotalIntensity();

		updatePairStats();
		nameTag = getNameTag();
	}

	private void updatePairStats() {
		if (featuresInGroup.size() == 2) {
			massDiff = featuresInGroup.get(1).getMass() - featuresInGroup.get(0).getMass();
			if (featuresInGroup.get(1).getOldRt() != null && featuresInGroup.get(0).getOldRt() != null)
				oldRtDiff = featuresInGroup.get(1).getOldRt() - featuresInGroup.get(0).getOldRt();

			rtDiff = featuresInGroup.get(1).getRT() - featuresInGroup.get(0).getRT();
		}
	}
	
	public Integer getMatchGrpKey() {
		return matchGrpKey;
	}

	public void setMatchGrpKey(Integer matchGrpKey) {
		this.matchGrpKey = matchGrpKey;
	}

	public List<FeatureFromFile> getFeaturesInGroup() {
		return featuresInGroup;
	}

	public void setFeaturesInGroup(List<FeatureFromFile> featuresInGroup) {
		this.featuresInGroup = featuresInGroup;
	}

	public void setAvgMass(Double avgMass) {
		this.avgMass = avgMass;
	}

	public void setAvgRt(Double avgRt) {
		this.avgRt = avgRt;
	}

	public void setAvgOldRt(Double avgOldRt) {
		this.avgOldRt = avgOldRt;
	}

	public Double getMassDiff() {
		updatePairStats();
		return massDiff;
	}

	public Double getOldRtDiff() {
		updatePairStats();
		return oldRtDiff;
	}

	public Double getRtDiff() {
		updatePairStats();
		return rtDiff;
	}
	
	public Integer countMatchedTargetFeatures(Map<String, String> targetFeatures) {

		Integer count = 0;
		if (targetFeatures != null) {
			for (FeatureFromFile f : featuresInGroup) {
				if (targetFeatures.containsKey(f.getName()))
					count++;
			}
		} else {
			for (FeatureFromFile f : featuresInGroup) {
				if (!f.getName().startsWith("UNK_"))
					count++;
			}
		}
		return count;
	}

	public Double getAvgCorrelationIfPossible(Boolean allowUnnamed) throws Exception {

		if (featuresInGroup == null)
			return Double.NaN;

		Double avgCorr = 0.0, featureCorr = Double.NaN;
		Integer nNamedFeatures = 0;

		for (FeatureFromFile f : featuresInGroup) {

			if (!f.getName().startsWith("UNK_")) {

				try {
					List<String> nameTokens = StringUtils.getAsArrayList(f.getName(), "_");

					if (nameTokens.size() < 2)
						continue;

					featureCorr = Double.parseDouble(nameTokens.get(nameTokens.size() - 1));

					// if (avgCorr.isNaN())
					// avgCorr = 0.0;

					if (featureCorr != null && !featureCorr.isNaN()) {
						avgCorr += featureCorr;
						nNamedFeatures++;
					}
				} catch (NumberFormatException e1) {
					if (allowUnnamed != null && allowUnnamed)
						continue;

					throw e1;
				} catch (Exception e2) {
					throw e2;
				}
			}
		}

		if (nNamedFeatures > 0)
			avgCorr /= (1.0 * nNamedFeatures);
		return avgCorr;
	}
}
