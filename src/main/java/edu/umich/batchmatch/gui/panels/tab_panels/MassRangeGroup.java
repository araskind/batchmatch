package edu.umich.batchmatch.gui.panels.tab_panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.data.FeatureFromFile;
import edu.umich.batchmatch.data.MatchedFeatureGroup;

public class MassRangeGroup {

	private List<FeatureFromFile> unmatchedFeatures;
	private Map<Integer, MatchedFeatureGroup> matchedFeatures;

	public MassRangeGroup() {

		unmatchedFeatures = new ArrayList<FeatureFromFile>();
		matchedFeatures = new HashMap<Integer, MatchedFeatureGroup>();
	}

	public void addRawFeature(FeatureFromFile f) {
		if (unmatchedFeatures == null)
			unmatchedFeatures = new ArrayList<FeatureFromFile>();

		unmatchedFeatures.add(f);
	}

	public void organize() {

		unpack();

		Map<Integer, List<FeatureFromFile>> featuresByMatchGroupMap = new HashMap<Integer, List<FeatureFromFile>>();

		for (FeatureFromFile f : unmatchedFeatures) {
			if (f.getRedundancyGroup() == null)
				continue;

			if (!featuresByMatchGroupMap.containsKey(f.getRedundancyGroup()))
				featuresByMatchGroupMap.put(f.getRedundancyGroup(), new ArrayList<FeatureFromFile>());

			featuresByMatchGroupMap.get(f.getRedundancyGroup()).add(f);
		}
	}

	private void unpack() {
		for (MatchedFeatureGroup matchGrp : matchedFeatures.values()) {
			for (FeatureFromFile f : matchGrp.getFeaturesInGroup()) {
				unmatchedFeatures.add(f);
			}
			matchedFeatures = new HashMap<Integer, MatchedFeatureGroup>();
		}
	}
}
