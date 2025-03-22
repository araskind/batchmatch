////////////////////////////////////////////////////
// MassRangeGroup.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.data.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.FeatureByRtAndMassComparator;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;

public class MassRangeGroup {

	private List<FeatureFromFile> unmatchedFeatures;
	private Map<Integer, MatchedFeatureGroup> matchedFeatureGroups;
	private Boolean isOrganized = false;

	private Double minMass = null, maxMass = null;
	private Integer nFeatures;

	public MassRangeGroup() {
		super();
		isOrganized = false;
		unmatchedFeatures = new ArrayList<FeatureFromFile>();
		matchedFeatureGroups = new HashMap<Integer, MatchedFeatureGroup>();
	}

	public void addRawFeature(FeatureFromFile f) {
		if (unmatchedFeatures == null)
			unmatchedFeatures = new ArrayList<FeatureFromFile>();

		unmatchedFeatures.add(f);
		isOrganized = false;
	}

	public void organize() {
		unpack();

		Double currMass = null;
		for (FeatureFromFile f : unmatchedFeatures) {
			currMass = f.getMass();

			if (minMass == null)
				minMass = currMass;
			if (maxMass == null)
				maxMass = currMass;

			if (currMass > maxMass)
				maxMass = f.getMass();
			if (currMass < minMass)
				minMass = currMass;
		}
		nFeatures = unmatchedFeatures.size();

		Map<Integer, List<FeatureFromFile>> featuresByMatchGroupMap = new HashMap<Integer, List<FeatureFromFile>>();

		List<FeatureFromFile> newunmatchedFeatures = new ArrayList<FeatureFromFile>();
		for (FeatureFromFile f : unmatchedFeatures) {
			if (f.getRedundancyGroup() == null) {
				newunmatchedFeatures.add(f);
				continue;
			}
			if (!featuresByMatchGroupMap.containsKey(f.getRedundancyGroup()))
				featuresByMatchGroupMap.put(f.getRedundancyGroup(), new ArrayList<FeatureFromFile>());

			featuresByMatchGroupMap.get(f.getRedundancyGroup()).add(f);
		}

		matchedFeatureGroups.clear();

		for (Integer key : featuresByMatchGroupMap.keySet()) {
			MatchedFeatureGroup newFeatureGroup = new MatchedFeatureGroup(key, featuresByMatchGroupMap.get(key), true);
			matchedFeatureGroups.put(key, newFeatureGroup);
		}

		unmatchedFeatures = new ArrayList<FeatureFromFile>();
		for (FeatureFromFile f : newunmatchedFeatures)
			unmatchedFeatures.add(f);
		isOrganized = true;
	}

	private void unpack() {
		for (MatchedFeatureGroup matchGrp : matchedFeatureGroups.values()) {
			for (FeatureFromFile f : matchGrp.getFeaturesInGroup()) {
				unmatchedFeatures.add(f);
			}
			isOrganized = false;
			matchedFeatureGroups = new HashMap<Integer, MatchedFeatureGroup>();
		}
	}
	
	public int getMaxBatchCount() {
		int maxBatchCt = -1;

		Map<Integer, Integer> batchCtMap = getBatchCountMap();
		if (batchCtMap == null)
			return maxBatchCt;

		for (Integer value : batchCtMap.values())
			if (value > maxBatchCt)
				maxBatchCt = value;

		return maxBatchCt;
	}

	public Map<Integer, Integer> getFeatureCountMap() {

		Map<Integer, Integer> featureCtMap = new HashMap<Integer, Integer>();

		for (MatchedFeatureGroup fGroup : matchedFeatureGroups.values())
			featureCtMap.put(fGroup.getMatchGrpKey(), fGroup.getFeatureNames().size());

		return featureCtMap;
	}

	private Map<Integer, List<String>> getFeatureNamesForGroupsWithNBatches(Integer nBatches, Boolean withNamed) {
		Map<Integer, Integer> batchCtMap = getBatchCountMap();

		List<Integer> featureGroupsWithCount = new ArrayList<Integer>();
		for (Integer key : batchCtMap.keySet()) {
			if (batchCtMap.get(key) == nBatches)
				featureGroupsWithCount.add(key);
		}

		Map<Integer, List<String>> completeFeatureGroupNamesMap = new HashMap<Integer, List<String>>();

		for (Integer groupKey : featureGroupsWithCount) {

			MatchedFeatureGroup fGroup = matchedFeatureGroups.get(groupKey);

			if (fGroup.countMatchedTargetFeatures(null) < 1)
				continue;

			if (!completeFeatureGroupNamesMap.containsKey(groupKey))
				completeFeatureGroupNamesMap.put(groupKey, new ArrayList<String>());

			for (String fName : fGroup.getFeatureNames())
				completeFeatureGroupNamesMap.get(groupKey).add(fName);

		}
		return completeFeatureGroupNamesMap;
	}

	public Map<String, Integer> getFeatureNamesForGroupsWithNBatchesByFeatureName(Integer nBatches, Boolean withNamed) {

		Map<Integer, List<String>> namesByGroup = getFeatureNamesForGroupsWithNBatches(nBatches, withNamed);
		Map<String, Integer> groupsByName = new HashMap<String, Integer>();

		for (Integer grpKey : namesByGroup.keySet()) {
			List<String> namesInGroup = namesByGroup.get(grpKey);

			for (String name : namesInGroup)
				groupsByName.put(name, grpKey);
		}
		return groupsByName;
	}

	public Map<Integer, Integer> getBatchCountMap() {

		Map<Integer, Integer> batchCtMap = new HashMap<Integer, Integer>();

		for (MatchedFeatureGroup fGroup : matchedFeatureGroups.values())
			batchCtMap.put(fGroup.getMatchGrpKey(), fGroup.getUniqueBatches().size());

		return batchCtMap;
	}

	public List<FeatureFromFile> grabAsFeatureList() {
		List<FeatureFromFile> allFeatures = new ArrayList<FeatureFromFile>();

		for (MatchedFeatureGroup matchGrp : matchedFeatureGroups.values()) {
			matchGrp.ensureHighestCorrs(true);
			for (FeatureFromFile f : matchGrp.getFeaturesInGroup()) {
				allFeatures.add(f);
			}
		}

		for (FeatureFromFile f : this.unmatchedFeatures)
			allFeatures.add(f);

		Collections.sort(allFeatures, new FeatureByRtAndMassComparator());

		return allFeatures;
	}

	public Boolean keepForAnalysis(Map<String, String> targetFeatures) {
		for (MatchedFeatureGroup grp : matchedFeatureGroups.values())
			if (grp.countMatchedTargetFeatures(targetFeatures) > 0)
				return true;
		return false;
	}

	public Double getMinMass() {
		if (!isOrganized)
			organize();
		return this.minMass;
	}

	public Double getMaxMass() {
		if (!isOrganized)
			organize();
		return this.maxMass;
	}

	public Integer getNFeatures() {
		if (!isOrganized)
			organize();
		return this.nFeatures;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("For mass range group starting at " + (minMass == null ? "unknown" : minMass));
		sb.append(BatchMatchConstants.LINE_SEPARATOR);
		sb.append("and ending at " + (maxMass == null ? "unknown" : maxMass)
				+ " the following feature groups are of interest ");
		sb.append(BatchMatchConstants.LINE_SEPARATOR);

		return ""; // sb.toString();
	}

	private Map<Integer, MatchedFeatureGroup> grabMatchedFeatureGroups() {
		if (this.matchedFeatureGroups == null) {
			return null; // need to update to rebuild
		}
		return matchedFeatureGroups;
	}

	private Map<Integer, MatchedFeatureGroup> grabMatchedNamedFeatureGroups() {
		grabMatchedFeatureGroups();

		Map<Integer, MatchedFeatureGroup> namedMatchGroups = new HashMap<Integer, MatchedFeatureGroup>();
		for (MatchedFeatureGroup grp : matchedFeatureGroups.values()) {
			if (grp.countMatchedTargetFeatures(null) > 0)
				namedMatchGroups.put(grp.getMatchGrpKey(), grp);
		}
		return namedMatchGroups;
	}

	public Map<Integer, MatchedFeatureGroup> grabMatchedNamedFeatureGroupsOfSize(int nBatches,
			Boolean includeAmbiguous) {
		Map<Integer, MatchedFeatureGroup> namedMatchGroups = grabMatchedNamedFeatureGroups();
		Map<Integer, MatchedFeatureGroup> namedMatchGroupsOfSize = new HashMap<Integer, MatchedFeatureGroup>();

		for (MatchedFeatureGroup grp : namedMatchGroups.values()) {
			if (grp.getBatches().size() == nBatches)
				if (includeAmbiguous || grp.getFeatureNames().size() == nBatches)
					namedMatchGroupsOfSize.put(grp.getMatchGrpKey(), grp);
		}
		return namedMatchGroupsOfSize;
	}
}
