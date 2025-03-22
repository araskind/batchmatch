package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.FeatureByMassComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.MassRangeGroup;
import edu.umich.med.mrc2.batchmatch.data.orig.MatchedFeatureGroup;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;

public class BatchMatchNamedMassResults {

	private Double breakGap = BatchMatchConstants.MASS_RANGE_BREAK_GAP;

	public List<MassRangeGroup> massGroups = new ArrayList<MassRangeGroup>();
	// private Map<Integer, List<FeatureFromFile>> namedMatchGroups = null;

	public BatchMatchNamedMassResults(PostProcessDataSet data) {
		characterize(data);
	}

	public void characterize(PostProcessDataSet data) {
		Collections.sort(data.getFeatures(), new FeatureByMassComparator());

		massGroups = new ArrayList<MassRangeGroup>();

		List<FeatureFromFile> features = data.getFeatures();
		if (features.size() < 1)
			return;

		Double currMass = null;
		Double lastMass = features.get(0).getMass();

		MassRangeGroup currGroup = new MassRangeGroup();
		// currGroup.addRawFeature(features.get(0));

		for (int i = 1; i < features.size(); i++) {

			currGroup.addRawFeature(features.get(i - 1));
			currMass = features.get(i).getMass();

			if (Math.abs(currMass - lastMass) > breakGap) {

				// currGroup.addRawFeature(features.get(i-1));
				currGroup.organize();
				massGroups.add(currGroup);
				currGroup = new MassRangeGroup();
			}
			lastMass = currMass;
		}
		massGroups.add(currGroup);
	}

	public Map<Integer, MatchedFeatureGroup> grabNamedFeatureMatchGroupsOfSize(int size, Boolean includeAmbiguous) {

		Map<Integer, MatchedFeatureGroup> namedMatchGroupsOfSize = new HashMap<Integer, MatchedFeatureGroup>();

		for (MassRangeGroup grp : massGroups) {
			Map<Integer, MatchedFeatureGroup> newFeatureMatchGroups = grp.grabMatchedNamedFeatureGroupsOfSize(size,
					includeAmbiguous);
			for (MatchedFeatureGroup featureMatchGrp : newFeatureMatchGroups.values()) {
				namedMatchGroupsOfSize.put(featureMatchGrp.getMatchGrpKey(), featureMatchGrp);
			}
		}
		return namedMatchGroupsOfSize;
	}

	public Map<String, List<MatchedFeatureGroup>> grabNamedMatchGroupsOfSizeByCompound(Integer size,
			Boolean includeAmbiguous) {
		// setFurtherAnnotation
		Map<String, List<MatchedFeatureGroup>> namedMatchGroupsOfSize = new HashMap<String, List<MatchedFeatureGroup>>();

		for (MassRangeGroup grp : massGroups) {
			Map<Integer, MatchedFeatureGroup> newFeatureMatchGroups = grp.grabMatchedNamedFeatureGroupsOfSize(size,
					includeAmbiguous);
			for (MatchedFeatureGroup featureMatchGrp : newFeatureMatchGroups.values()) {

				if (!namedMatchGroupsOfSize.containsKey(featureMatchGrp.getNameTag()))
					namedMatchGroupsOfSize.put(featureMatchGrp.getNameTag(), new ArrayList<MatchedFeatureGroup>());

				namedMatchGroupsOfSize.get(featureMatchGrp.getNameTag()).add(featureMatchGrp);
			}
		}
		return namedMatchGroupsOfSize;
	}

	public Map<String, Map<Integer, List<MatchedFeatureGroup>>> grabCompoundsWithoutSignificantCompleteMatchGroup(
			Integer completeSetSize, Boolean includeAmbiguous) throws Exception {

		Map<String, Map<Integer, List<MatchedFeatureGroup>>> namedMatchGroupsLessThanSizeByCompoundAndSize = grabNamedMatchGroupsLessThanSizeByCompoundAndSize(
				completeSetSize + 1, true);

		Map<String, Map<Integer, List<MatchedFeatureGroup>>> insignificantGroups = new HashMap<String, Map<Integer, List<MatchedFeatureGroup>>>();

		Map<String, String> problemCompounds = new HashMap<String, String>();
		for (String name : namedMatchGroupsLessThanSizeByCompoundAndSize.keySet()) {

			Boolean problemCompound = true;
			if (namedMatchGroupsLessThanSizeByCompoundAndSize.get(name).containsKey(completeSetSize)) {
				List<MatchedFeatureGroup> completeSetsForCompound = namedMatchGroupsLessThanSizeByCompoundAndSize
						.get(name).get(completeSetSize);

				// compound is "problem" if there are no match groups for it that are
				// significant (>.9)
				for (MatchedFeatureGroup grp : completeSetsForCompound) {
					try {
						problemCompound &= (grp
								.getAvgCorrelationIfPossible(true) < BatchMatchConstants.SIGNIFICANT_MAP_CORRELATION);
					} catch (Exception e) {
					}
				}
			}
			if (problemCompound)
				problemCompounds.put(name, null);
		}

		for (String compound : namedMatchGroupsLessThanSizeByCompoundAndSize.keySet()) {

			if (!problemCompounds.containsKey(compound))
				continue;

			insignificantGroups.put(compound, new HashMap<Integer, List<MatchedFeatureGroup>>());

			for (Integer sz : namedMatchGroupsLessThanSizeByCompoundAndSize.get(compound).keySet()) {
				if (!insignificantGroups.get(compound).containsKey(sz))
					insignificantGroups.get(compound).put(sz, new ArrayList<MatchedFeatureGroup>());

				List<MatchedFeatureGroup> grpList = namedMatchGroupsLessThanSizeByCompoundAndSize.get(compound).get(sz);

				for (MatchedFeatureGroup grp : grpList) {
					insignificantGroups.get(compound).get(sz).add(grp);
				}
			}
		}
		return insignificantGroups;
	}

	public Map<Integer, Map<String, List<MatchedFeatureGroup>>> grabNamedMatchGroupsLessThanSizeBySizeAndCompound(
			int upperSize, Boolean includeAmbiguous) {

		Map<Integer, Map<String, List<MatchedFeatureGroup>>> namedMatchGroupsByCompoundAndSize = new HashMap<Integer, Map<String, List<MatchedFeatureGroup>>>();

		for (Integer i = 1; i < upperSize; i++) {
			Map<String, List<MatchedFeatureGroup>> groupsOfSizeByCompound = grabNamedMatchGroupsOfSizeByCompound(i,
					includeAmbiguous);

			if (groupsOfSizeByCompound == null)
				continue;

			namedMatchGroupsByCompoundAndSize.put(i, groupsOfSizeByCompound);
		}

		return namedMatchGroupsByCompoundAndSize;
	}

	public Map<String, Map<Integer, List<MatchedFeatureGroup>>> grabNamedMatchGroupsLessThanSizeByCompoundAndSize(
			int upperSize, Boolean includeAmbiguous) {

		Map<Integer, Map<String, List<MatchedFeatureGroup>>> namedMatchGroupsBySizeAndCompound = grabNamedMatchGroupsLessThanSizeBySizeAndCompound(
				upperSize, includeAmbiguous);

		Map<String, Map<Integer, List<MatchedFeatureGroup>>> namedMatchGroupsByCompoundAndSize = new HashMap<String, Map<Integer, List<MatchedFeatureGroup>>>();

		for (Integer size : namedMatchGroupsBySizeAndCompound.keySet()) {

			Map<String, List<MatchedFeatureGroup>> ofSizeByCompoundMap = namedMatchGroupsBySizeAndCompound.get(size);

			for (String compound : ofSizeByCompoundMap.keySet()) {
				if (!namedMatchGroupsByCompoundAndSize.containsKey(compound))
					namedMatchGroupsByCompoundAndSize.put(compound, new HashMap<Integer, List<MatchedFeatureGroup>>());

				if (!namedMatchGroupsByCompoundAndSize.get(compound).containsKey(size))
					namedMatchGroupsByCompoundAndSize.get(compound).put(size, new ArrayList<MatchedFeatureGroup>());

				for (MatchedFeatureGroup grp : namedMatchGroupsBySizeAndCompound.get(size).get(compound))
					namedMatchGroupsByCompoundAndSize.get(compound).get(size).add(grp);
			}
		}
		return namedMatchGroupsByCompoundAndSize;
	}

	public List<MassRangeGroup> grabMassRangesToStudy(Map<String, String> targetFeatures) {

		List<MassRangeGroup> rangeGroupsToStudy = new ArrayList<MassRangeGroup>();

		for (MassRangeGroup grp : massGroups) {
			if (grp.keepForAnalysis(targetFeatures))
				rangeGroupsToStudy.add(grp);
		}

		for (MassRangeGroup grp : rangeGroupsToStudy) {
			System.out.println(grp);
		}
		return rangeGroupsToStudy;
	}

	public List<MassRangeGroup> getMassGroups() {
		return massGroups;
	}

	public Double getBreakGap() {
		return breakGap;
	}

	public void setBreakGap(Double breakGap) {
		this.breakGap = breakGap;
	}

}

/*
 * private Double breakGap = 0.02;
 * 
 * public List<MassRangeGroup> massGroups = new ArrayList<MassRangeGroup>();
 * 
 * 
 * public BatchMatchNamedMassResults(PostProcessDataSet data) {
 * characterize(data); }
 * 
 * public void characterize(PostProcessDataSet data) {
 * Collections.sort(data.getFeatures(), new FeatureByMassComparator());
 * 
 * massGroups = new ArrayList<MassRangeGroup>();
 * 
 * List<FeatureFromFile> features = data.getFeatures(); if (features.size() < 1)
 * return;
 * 
 * Double currMass = null; Double lastMass = features.get(0).getMass();
 * 
 * MassRangeGroup currGroup = new MassRangeGroup();
 * //currGroup.addRawFeature(features.get(0));
 * 
 * for (int i = 1; i < features.size(); i++) {
 * 
 * currGroup.addRawFeature(features.get(i-1)); currMass =
 * features.get(i).getMass();
 * 
 * if (Math.abs(currMass - lastMass) > breakGap) {
 * 
 * //currGroup.addRawFeature(features.get(i-1)); currGroup.organize();
 * massGroups.add(currGroup); currGroup = new MassRangeGroup(); }
 * 
 * lastMass = currMass; } massGroups.add(currGroup); }
 * 
 * public List<MassRangeGroup> grabMassRangesToStudy(Map<String, String>
 * targetFeatures) {
 * 
 * List<MassRangeGroup> rangeGroupsToStudy = new ArrayList<MassRangeGroup>();
 * 
 * for (MassRangeGroup grp : massGroups) { if
 * (grp.keepForAnalysis(targetFeatures)) rangeGroupsToStudy.add(grp); }
 * 
 * for (MassRangeGroup grp : rangeGroupsToStudy) { System.out.println(grp); }
 * return rangeGroupsToStudy; }
 * 
 * 
 * public List<MassRangeGroup> getMassGroups() { return massGroups; }
 * 
 * public Double getBreakGap() { return breakGap; }
 * 
 * public void setBreakGap(Double breakGap) { this.breakGap = breakGap; }
 * 
 * }
 */