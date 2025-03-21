////////////////////////////////////////////////////
// PostProcessDataSet.java
// Written by Jan Wigginton, May 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import edu.umich.mrc2.batchmatch.data.comparators.orig.FeatureByBatchComparator;
import edu.umich.mrc2.batchmatch.data.comparators.orig.FeatureByMassAndRtComparator;
import edu.umich.mrc2.batchmatch.data.comparators.orig.FeatureByNameComparator;
import edu.umich.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.mrc2.batchmatch.utils.orig.ListUtils;
import edu.umich.mrc2.batchmatch.utils.orig.PostProcessMergeNameExtractor;
import edu.umich.mrc2.batchmatch.utils.orig.StringUtils;

public class PostProcessDataSet {

	private List<FeatureFromFile> features;
	private List<String> orderedExtraLibFileHeaders, orderedNonStandardHeaders, orderedIntensityHeaders;
	private List<String> derivedSampleNames;
	private Map<String, String> derivedNameToHeaderMap;
	private Map<String, Double> featureNameToOldRtMap;
	private Boolean rsdPctMissPrecalculated = false;
	private Map<String, List<String>> redundantFeatureMap;

	private Integer maxPossibleMatchCt;
	private Map<Integer, Integer> rawFeatureCtsByBatch;
	private Boolean posMode, ifPrincipalIons;
	private String sourceReport, libFileSource, dataSetLabel;

	private Map<Integer, Double> avgRtsByMatchGrp = null, avgOldRtsByMatchGrp = null, avgMassesByMatchGrp = null;
	private Map<Integer, Double> minRtsByMatchGrp = null, minMassesByMatchGrp = null;
	private Map<Integer, Double> maxRtsByMatchGrp = null, maxMassesByMatchGrp = null;
	private Map<Integer, Double> avgMedIntensityByMatchGrp = null;
	private Map<Integer, Integer> grpSizeByMatchGrp = null;
	private Map<Integer, List<String>> colsForRSDByBatch = null;
	private Map<Integer, Integer> nameTypesByBatch = null;

	public PostProcessDataSet() {

		features = new ArrayList<FeatureFromFile>();
		nameTypesByBatch = new HashMap<Integer, Integer>();
		orderedExtraLibFileHeaders = new ArrayList<String>();
		orderedNonStandardHeaders = new ArrayList<String>();
		orderedIntensityHeaders = new ArrayList<String>();
		redundantFeatureMap = new HashMap<String, List<String>>();

		derivedSampleNames = null;
		derivedNameToHeaderMap = null;
		featureNameToOldRtMap = null;

		rawFeatureCtsByBatch = null;

		posMode = null;
		ifPrincipalIons = null;
		sourceReport = libFileSource = "";
	}
	
	public void updateNamesForBatch() {
		for (FeatureFromFile f : features) {
			if (!f.getName().endsWith("-" + f.getBatchIdx()))
				f.setName(f.getName() + "-" + f.getBatchIdx());
		}
	}

	public void updateNamesForBatchAndMZ() {
		for (FeatureFromFile f : features) {
			if (!f.getName().endsWith("-" + f.getBatchIdx())) {
				String strMass = String.format("%.5f", f.getMass());
				f.setName(f.getName() + "_" + strMass + "-" + f.getBatchIdx());
			}
		}
	}

	public PostProcessDataSet makeDeepCopy() {
		PostProcessDataSet destData = new PostProcessDataSet();

		if (this.features == null) {
			destData.features = null;
		} else {
			List<FeatureFromFile> features = new ArrayList<FeatureFromFile>();
			for (int i = 0; i < this.features.size(); i++) {
				if (this.features.get(i) == null) {
					features.add(null);
				} else {
					features.add(this.features.get(i).makeDeepCopy());
				}
			}
			destData.features = features;
		}

		if (this.orderedExtraLibFileHeaders == null) {
			destData.orderedExtraLibFileHeaders = null;
		} else {
			List<String> orderedExtraLibFileHeaders = new ArrayList<String>();
			for (int i = 0; i < this.orderedExtraLibFileHeaders.size(); i++) {
				orderedExtraLibFileHeaders.add(this.orderedExtraLibFileHeaders.get(i));
			}
			destData.orderedExtraLibFileHeaders = orderedExtraLibFileHeaders;
		}

		if (this.orderedNonStandardHeaders == null) {
			destData.orderedNonStandardHeaders = null;
		} else {
			List<String> orderedNonStandardHeaders = new ArrayList<String>();
			for (int i = 0; i < this.orderedNonStandardHeaders.size(); i++) {
				orderedNonStandardHeaders.add(this.orderedNonStandardHeaders.get(i));
			}
			destData.orderedNonStandardHeaders = orderedNonStandardHeaders;
		}

		if (this.orderedIntensityHeaders == null) {
			destData.orderedIntensityHeaders = null;
		} else {
			List<String> orderedIntensityHeaders = new ArrayList<String>();
			for (int i = 0; i < this.orderedIntensityHeaders.size(); i++) {
				orderedIntensityHeaders.add(this.orderedIntensityHeaders.get(i));
			}
			destData.orderedIntensityHeaders = orderedIntensityHeaders;
		}

		if (this.derivedSampleNames == null) {
			destData.derivedSampleNames = null;
		} else {
			List<String> derivedSampleNames = new ArrayList<String>();
			for (int i = 0; i < this.derivedSampleNames.size(); i++) {
				derivedSampleNames.add(this.derivedSampleNames.get(i));
			}
			destData.derivedSampleNames = derivedSampleNames;
		}

		if (this.derivedNameToHeaderMap == null) {
			destData.derivedNameToHeaderMap = null;
		} else {
			Map<String, String> derivedNameToHeaderMap = new HashMap<String, String>();
			for (String key : this.derivedNameToHeaderMap.keySet()) {
				derivedNameToHeaderMap.put(key, this.derivedNameToHeaderMap.get(key));
			}
			destData.derivedNameToHeaderMap = derivedNameToHeaderMap;
		}

		if (this.featureNameToOldRtMap == null) {
			destData.featureNameToOldRtMap = null;
		} else {
			Map<String, Double> featureNameToOldRtMap = new HashMap<String, Double>();
			for (String key : this.featureNameToOldRtMap.keySet()) {
				featureNameToOldRtMap.put(key, this.featureNameToOldRtMap.get(key));
			}
			destData.featureNameToOldRtMap = featureNameToOldRtMap;
		}

		destData.rsdPctMissPrecalculated = this.rsdPctMissPrecalculated;

		if (this.redundantFeatureMap == null) {
			destData.redundantFeatureMap = null;
		} else {
			Map<String, List<String>> redundantFeatureMap = new HashMap<String, List<String>>();
			for (String key : this.redundantFeatureMap.keySet()) {
				List<String> srcValueList = this.redundantFeatureMap.get(key);
				if (srcValueList == null) {
					redundantFeatureMap.put(key, null);
				} else {
					List<String> destValueList = new ArrayList<String>();
					for (int i = 0; i < srcValueList.size(); i++) {
						destValueList.add(srcValueList.get(i));
					}
					redundantFeatureMap.put(key, destValueList);
				}
			}
			destData.redundantFeatureMap = redundantFeatureMap;
		}

		destData.posMode = this.posMode;
		destData.ifPrincipalIons = this.ifPrincipalIons;
		destData.sourceReport = this.sourceReport;
		destData.libFileSource = this.libFileSource;
		destData.dataSetLabel = this.dataSetLabel;

		if (this.avgRtsByMatchGrp == null) {
			destData.avgRtsByMatchGrp = null;
		} else {
			Map<Integer, Double> avgRtsByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.avgRtsByMatchGrp.keySet()) {
				avgRtsByRedundancyGrp.put(key, this.avgRtsByMatchGrp.get(key));
			}
			destData.avgRtsByMatchGrp = avgRtsByRedundancyGrp;
		}

		if (this.avgOldRtsByMatchGrp == null) {
			destData.avgOldRtsByMatchGrp = null;
		} else {
			Map<Integer, Double> avgOldRtsByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.avgOldRtsByMatchGrp.keySet()) {
				avgOldRtsByRedundancyGrp.put(key, this.avgOldRtsByMatchGrp.get(key));
			}
			destData.avgOldRtsByMatchGrp = avgOldRtsByRedundancyGrp;
		}

		if (this.avgMassesByMatchGrp == null) {
			destData.avgMassesByMatchGrp = null;
		} else {
			Map<Integer, Double> avgPutMassesByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.avgMassesByMatchGrp.keySet()) {
				avgPutMassesByRedundancyGrp.put(key, this.avgMassesByMatchGrp.get(key));
			}
			destData.avgMassesByMatchGrp = avgPutMassesByRedundancyGrp;
		}

		if (this.minRtsByMatchGrp == null) {
			destData.minRtsByMatchGrp = null;
		} else {
			Map<Integer, Double> minRtsByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.minRtsByMatchGrp.keySet()) {
				minRtsByRedundancyGrp.put(key, this.minRtsByMatchGrp.get(key));
			}
			destData.minRtsByMatchGrp = minRtsByRedundancyGrp;
		}

		if (this.minMassesByMatchGrp == null) {
			destData.minMassesByMatchGrp = null;
		} else {
			Map<Integer, Double> minMassesByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.minMassesByMatchGrp.keySet()) {
				minMassesByRedundancyGrp.put(key, this.minMassesByMatchGrp.get(key));
			}
			destData.minMassesByMatchGrp = minMassesByRedundancyGrp;
		}

		if (this.maxRtsByMatchGrp == null) {
			destData.maxRtsByMatchGrp = null;
		} else {
			Map<Integer, Double> maxRtsByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.maxRtsByMatchGrp.keySet()) {
				maxRtsByRedundancyGrp.put(key, this.maxRtsByMatchGrp.get(key));
			}
			destData.maxRtsByMatchGrp = maxRtsByRedundancyGrp;
		}

		if (this.maxMassesByMatchGrp == null) {
			destData.maxMassesByMatchGrp = null;
		} else {
			Map<Integer, Double> maxMassesByRedundancyGrp = new HashMap<Integer, Double>();
			for (Integer key : this.maxMassesByMatchGrp.keySet()) {
				maxMassesByRedundancyGrp.put(key, this.maxMassesByMatchGrp.get(key));
			}
			destData.maxMassesByMatchGrp = maxMassesByRedundancyGrp;
		}

		if (this.grpSizeByMatchGrp == null) {
			destData.grpSizeByMatchGrp = null;
		} else {
			Map<Integer, Integer> grpSizeByRedundancyGrp = new HashMap<Integer, Integer>();
			for (Integer key : this.grpSizeByMatchGrp.keySet()) {
				grpSizeByRedundancyGrp.put(key, this.grpSizeByMatchGrp.get(key));
			}
			destData.grpSizeByMatchGrp = grpSizeByRedundancyGrp;
		}

		destData.rawFeatureCtsByBatch = null;

		if (this.rawFeatureCtsByBatch != null) {
			destData.rawFeatureCtsByBatch = new HashMap<Integer, Integer>();
			for (Integer key : this.rawFeatureCtsByBatch.keySet())
				destData.rawFeatureCtsByBatch.put(key, this.rawFeatureCtsByBatch.get(key));
		}

		if (this.colsForRSDByBatch == null) {
			destData.colsForRSDByBatch = null;
		} else {
			Map<Integer, List<String>> newColsForRSDByBatch = new HashMap<Integer, List<String>>();
			for (Integer key : this.colsForRSDByBatch.keySet()) {
				List<String> newColNames = new ArrayList<String>();
				for (int i = 0; i < this.colsForRSDByBatch.get(key).size(); i++)
					newColNames.add(this.colsForRSDByBatch.get(key).get(i));

				newColsForRSDByBatch.put(key, newColNames);
			}
			destData.colsForRSDByBatch = newColsForRSDByBatch;
		}
		return destData;
	}

	public Map<String, List<FeatureFromFile>> getFeaturesByNameMap() {

		Map<String, List<FeatureFromFile>> featuresByNameMap = new HashMap<String, List<FeatureFromFile>>();
		for (FeatureFromFile feature : features) {
			if (!featuresByNameMap.containsKey(feature.getName().trim()))
				featuresByNameMap.put(feature.getName().trim(), new ArrayList<FeatureFromFile>());

			featuresByNameMap.get(feature.getName().trim()).add(feature);
		}
		return featuresByNameMap;
	}

	private Map<Integer, List<FeatureFromFile>> createFeatureByRedundancyGroupMap() {

		Map<Integer, List<FeatureFromFile>> featuresByRedundancyGrpMap = new HashMap<Integer, List<FeatureFromFile>>();
		for (FeatureFromFile feature : features) {
			if (!featuresByRedundancyGrpMap.containsKey(feature.getRedundancyGroup()))
				featuresByRedundancyGrpMap.put(feature.getRedundancyGroup(), new ArrayList<FeatureFromFile>());

			featuresByRedundancyGrpMap.get(feature.getRedundancyGroup()).add(feature);
		}
		return featuresByRedundancyGrpMap;
	}

	public Map<String, List<Integer>> buildMatchGroupToBatchIdsMap(Boolean uniqueBatchIds) {

		Map<String, List<Integer>> featureMatchGroupToBatchIdsMap = new HashMap<String, List<Integer>>();

		String name = null;
		for (FeatureFromFile feature : getFeatures()) {

			if (feature.getBatchIdx() == null)
				continue;
			if (feature.getRedundancyGroup() == null)
				continue;

			name = feature.getRedundancyGroup().toString();
			if (!featureMatchGroupToBatchIdsMap.containsKey(name))
				featureMatchGroupToBatchIdsMap.put(name, new ArrayList<Integer>());

			List<Integer> existingIds = featureMatchGroupToBatchIdsMap.get(name);

			if (!uniqueBatchIds || !existingIds.contains(feature.getBatchIdx()))
				featureMatchGroupToBatchIdsMap.get(name).add(feature.getBatchIdx());
		}
		return featureMatchGroupToBatchIdsMap;
	}

	public List<Integer> getSortedUniqueBatchIndices() {

		Map<Integer, String> batchIdsMap = new HashMap<Integer, String>();

		for (FeatureFromFile feature : getFeatures()) {

			if (feature.getBatchIdx() == null)
				continue;
			if (feature.getRedundancyGroup() == null)
				continue;

			if (!batchIdsMap.containsKey(feature.getBatchIdx()))
				batchIdsMap.put(feature.getBatchIdx(), null);
		}

		List<Integer> sortedBatchIds = ListUtils.makeListFromCollection(batchIdsMap.keySet());

		Collections.sort(sortedBatchIds);

		return sortedBatchIds;
	}

	public Integer getLastBatchIdx() {
		List<Integer> batchList = getSortedUniqueBatchIndices();

		if (!batchList.isEmpty())
			return batchList.get(batchList.size() - 1);
		
		return 0;
	}

	public Map<Integer, Double> buildMatchGroupToRtRangeMap() {

		Map<Integer, List<Double>> matchGroupToFeatureRtsMap = new HashMap<Integer, List<Double>>();

		Integer group = null;
		for (FeatureFromFile feature : getFeatures()) {
			if (feature.getBatchIdx() == null)
				continue;

			if (feature.getRedundancyGroup() == null)
				continue;

			group = feature.getRedundancyGroup();
			if (!matchGroupToFeatureRtsMap.containsKey(group))
				matchGroupToFeatureRtsMap.put(group, new ArrayList<Double>());

			matchGroupToFeatureRtsMap.get(group).add(feature.getRT());
		}

		Map<Integer, Double> matchGrpToRtRangeMap = new HashMap<Integer, Double>();
		for (Integer matchGrp : matchGroupToFeatureRtsMap.keySet()) {
			List<Double> values = matchGroupToFeatureRtsMap.get(matchGrp);
			Double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

			for (int i = 0; i < values.size(); i++) {
				if (values.get(i) < min)
					min = values.get(i);
				if (values.get(i) > max)
					max = values.get(i);
			}
			matchGrpToRtRangeMap.put(matchGrp, max - min);
		}
		return matchGrpToRtRangeMap;
	}

	public Map<String, List<String>> buildMatchGroupToFeatureNamesMap(boolean uniqueBatchIds) {

		Map<String, List<String>> redundancyGroupToFeatureNamesMap = new HashMap<String, List<String>>();

		String name = null;
		for (FeatureFromFile feature : getFeatures()) {
			if (feature.getBatchIdx() == null)
				continue;

			if (feature.getRedundancyGroup() == null)
				continue;

			name = feature.getRedundancyGroup().toString();
			if (!redundancyGroupToFeatureNamesMap.containsKey(name))
				redundancyGroupToFeatureNamesMap.put(name, new ArrayList<String>());

			List<String> existingIds = redundancyGroupToFeatureNamesMap.get(name);

			if (!uniqueBatchIds || !existingIds.contains(feature.getBatchIdx()))
				redundancyGroupToFeatureNamesMap.get(name).add(feature.getName().trim());
		}
		return redundancyGroupToFeatureNamesMap;
	}

	private Map<String, List<String>> buildDetailedRtsByRedundancyGroupMap() {
		Map<String, Map<Integer, List<Double>>> rawMapForGroupRts = new HashMap<String, Map<Integer, List<Double>>>();
		String currGrp;
		FeatureFromFile feature;
		Collections.sort(features, new FeatureByBatchComparator());

		for (int f = 0; f < features.size(); f++) {

			feature = features.get(f);
			if (feature.getRedundancyGroup() == null)
				continue;

			currGrp = feature.getRedundancyGroup().toString();
			if (!rawMapForGroupRts.containsKey(currGrp))
				rawMapForGroupRts.put(currGrp, new HashMap<Integer, List<Double>>());

			Map<Integer, List<Double>> mapForCurrentGroup = rawMapForGroupRts.get(currGrp);
			if (!mapForCurrentGroup.containsKey(feature.getBatchIdx()))
				mapForCurrentGroup.put(feature.getBatchIdx(), new ArrayList<Double>());

			Double rt = null;
			try {
				rt = Double.parseDouble(feature.getAddedColValues().get(0));
			} catch (Exception e) {
			}
			mapForCurrentGroup.get(feature.getBatchIdx()).add(rt == null ? Double.NaN : rt);
		}

		Map<String, List<String>> mapForGroupRts = new HashMap<String, List<String>>();
		Integer batch = null;

		for (String group : rawMapForGroupRts.keySet()) {

			Map<Integer, List<Double>> rtsForGrp = rawMapForGroupRts.get(group);

			if (!mapForGroupRts.containsKey(group))
				mapForGroupRts.put(group, new ArrayList<String>());

			for (int i = 1; i <= 14; i++) {
				batch = i;
				if (rtsForGrp.get(batch) == null)
					continue;

				if (rtsForGrp.get(batch).size() < 2)
					mapForGroupRts.get(group).add(rtsForGrp.get(batch).get(0).toString());

				else {
					Double rtTot = 0.0;
					for (int r = 0; r < rtsForGrp.get(batch).size(); r++)
						rtTot += rtsForGrp.get(batch).get(r);
					rtTot /= (1.0 * rtsForGrp.get(batch).size());
					mapForGroupRts.get(group).add(rtTot.toString());
				}
			}
		}
		return mapForGroupRts;
	}

	public void buildAvgRtMassByMatchGrpMap(Boolean rtsOnly) {

		Map<Integer, List<Double>> rtsByRedundancyGrp = new HashMap<Integer, List<Double>>();
		Map<Integer, List<Double>> oldRtsByRedundancyGrp = new HashMap<Integer, List<Double>>();

		Map<Integer, List<Double>> massesByRedundancyGrp = new HashMap<Integer, List<Double>>();
		Map<Integer, List<Double>> medIntensityByRedundancyGrp = new HashMap<Integer, List<Double>>();

		Integer currGrp = null;
		for (FeatureFromFile feature : features) {
			if (feature.getRedundancyGroup() == null)
				continue;

			currGrp = feature.getRedundancyGroup();
			if (!rtsByRedundancyGrp.containsKey(currGrp))
				rtsByRedundancyGrp.put(currGrp, new ArrayList<Double>());

			if (feature.getOldRt() != null) {
				if (!oldRtsByRedundancyGrp.containsKey(currGrp))
					oldRtsByRedundancyGrp.put(currGrp, new ArrayList<Double>());
			}

			if (!massesByRedundancyGrp.containsKey(currGrp))
				massesByRedundancyGrp.put(currGrp, new ArrayList<Double>());

			if (!medIntensityByRedundancyGrp.containsKey(currGrp))
				medIntensityByRedundancyGrp.put(currGrp, new ArrayList<Double>());

			rtsByRedundancyGrp.get(currGrp).add(feature.getRT());
			if (feature.getOldRt() != null)
				oldRtsByRedundancyGrp.get(currGrp).add(feature.getOldRt());

			massesByRedundancyGrp.get(currGrp).add(feature.getMass());
			medIntensityByRedundancyGrp.get(currGrp).add(feature.getMedianIntensity());
		}

		grpSizeByMatchGrp = new HashMap<Integer, Integer>();
		avgRtsByMatchGrp = new HashMap<Integer, Double>();
		avgOldRtsByMatchGrp = new HashMap<Integer, Double>();
		avgMassesByMatchGrp = new HashMap<Integer, Double>();
		avgMedIntensityByMatchGrp = new HashMap<Integer, Double>();

		minRtsByMatchGrp = new HashMap<Integer, Double>();
		minMassesByMatchGrp = new HashMap<Integer, Double>();
		maxRtsByMatchGrp = new HashMap<Integer, Double>();
		maxMassesByMatchGrp = new HashMap<Integer, Double>();

		double avg, max = -1000000.0, min = Double.MAX_VALUE;
		for (Integer grp : rtsByRedundancyGrp.keySet()) {
			avg = 0.0;
			max = -1000000.0;
			min = Double.MAX_VALUE;
			for (Double rt : rtsByRedundancyGrp.get(grp)) {
				if (rt < min)
					min = rt;
				if (rt > max)
					max = rt;
				avg += rt;
			}
			avg /= (1.0 * rtsByRedundancyGrp.get(grp).size());

			avgRtsByMatchGrp.put(grp, avg);
			minRtsByMatchGrp.put(grp, min);
			maxRtsByMatchGrp.put(grp, max);
			// 12
			grpSizeByMatchGrp.put(grp, rtsByRedundancyGrp.get(grp).size());
		}

		if (!rtsOnly) {

			for (Integer grp : oldRtsByRedundancyGrp.keySet()) {
				avg = 0.0;
				for (Double oldrt : oldRtsByRedundancyGrp.get(grp))
					avg += oldrt;

				avg /= (1.0 * oldRtsByRedundancyGrp.get(grp).size());

				avgOldRtsByMatchGrp.put(grp, avg);
			}

			for (Integer grp : massesByRedundancyGrp.keySet()) {
				avg = 0.0;
				max = -1000000.0;
				min = Double.MAX_VALUE;

				for (Double mass : massesByRedundancyGrp.get(grp)) {
					avg += mass;
					if (mass < min)
						min = mass;
					if (mass > max)
						max = mass;
				}
				avg /= (1.0 * massesByRedundancyGrp.get(grp).size());

				avgMassesByMatchGrp.put(grp, avg);
				minMassesByMatchGrp.put(grp, min);
				maxMassesByMatchGrp.put(grp, max);
			}

			int nNullInt = 0;
			;
			for (Integer grp : medIntensityByRedundancyGrp.keySet()) {
				avg = 0.0;

				for (Double medInt : medIntensityByRedundancyGrp.get(grp)) {
					avg += (medInt == null ? 0.0 : medInt);
					if (medInt == null)
						nNullInt++;
				}
				avg /= (1.0 * medIntensityByRedundancyGrp.get(grp).size() - nNullInt);

				double[] validIntensities = new double[medIntensityByRedundancyGrp.get(grp).size()];
				Median median = new Median();

				int k = 0;
				Boolean foundDbl = false;
				for (int i = 0; i < medIntensityByRedundancyGrp.get(grp).size(); i++) {

					Double value = null;
					try {
						value = (medIntensityByRedundancyGrp.get(grp).get(i));
						if (value != null)
							foundDbl = true;
					} catch (Exception e) {
					}

					if (value == null)
						continue;
					validIntensities[k++] = value;
				}

				if (foundDbl) {
					Double featureMedian = median.evaluate(Arrays.copyOfRange(validIntensities, 0, k));
					avgMedIntensityByMatchGrp.put(grp, featureMedian);
				}
			}
		}
	}

	private int getMaxRedundancyGroup() {

		int maxRedundancyGrp = 0;
		for (FeatureFromFile feature : features) {
			if (feature.getRedundancyGroup() != null && feature.getRedundancyGroup() > maxRedundancyGrp)
				maxRedundancyGrp = feature.getRedundancyGroup();
		}
		return maxRedundancyGrp;
	}

	public void identifyMatchedFeatures(Integer annealingStep, double stretchFactor, double massTol, double rtTol,
			Boolean dupsAcrossType) {
		
		int maxMatchGrp = getMaxRedundancyGroup(); // ensurePutativeMasses(imputeMassFromH);

		buildAvgRtMassByMatchGrpMap(false);

		Map<Integer, List<FeatureFromFile>> featuresByMatchGrpMap = createFeatureByRedundancyGroupMap();

		identifyMatchedFeaturesByAvg(featuresByMatchGrpMap, maxMatchGrp, massTol, rtTol, true);

		Map<Integer, List<FeatureFromFile>> newFeaturesByMatchGrp = createFeatureByRedundancyGroupMap();
		AnnealingEngine2 engine = new AnnealingEngine2();
		engine.annealFragments(annealingStep, stretchFactor, 1, rtTol, massTol, newFeaturesByMatchGrp);
	}

	private void identifyMatchedFeaturesByAvg(Map<Integer, List<FeatureFromFile>> map, int maxRedundancyGrp,
			double massTol, double rtTol, Boolean combineGroups) {

		Collections.sort(features, new FeatureByMassAndRtComparator());

		Double massTolToUse = massTol, highMassCutoff = 700.0, highMassTolCutoff = .019, massTolMultiplier = 1.5;

		Boolean noDups = true;

		Double mass1, mass2, rt1, rt2;
		FeatureFromFile feature1, feature2;
		int nRedundancyGroups = maxRedundancyGrp + 1;
		Integer grp1, grp2;

		for (int i = 0; i < features.size() - 1; i++) {
			feature1 = features.get(i);
			mass1 = feature1.getMass();
			rt1 = feature1.getRT();

			if (mass1 == null || rt1 == null)
				continue;

			for (int j = i + 1; j < features.size(); j++) {

				feature2 = features.get(j);

				if (feature2.getMass() == null || feature2.getRT() == null)
					continue;

				if (massTol < highMassTolCutoff
						&& (feature2.getMass() > highMassCutoff || feature1.getMass() > highMassCutoff))
					massTolToUse = massTolMultiplier * massTol;
				else
					massTolToUse = massTol;

				massTolToUse = massTol;

				// short-circuit: relies on features being sorted by mass and loop written s.t.
				// j > i
				if (feature2.getMass() - mass1 > 5.0 * massTolToUse)
					break;

				grp1 = feature1.getRedundancyGroup();
				grp2 = feature2.getRedundancyGroup();

				Boolean newGroup = (grp1 == null && grp2 == null);

				if (newGroup && noDups)
					if (feature1.getBatchIdx().equals(feature2.getBatchIdx()))
						continue;

				mass1 = (grp1 == null ? feature1.getMass() : this.avgMassesByMatchGrp.get(grp1));
				mass2 = (grp2 == null ? feature2.getMass() : this.avgMassesByMatchGrp.get(grp2));

				if (Math.abs(mass2 - mass1) > massTolToUse)
					continue;

				rt1 = (grp1 == null ? feature1.getRT() : avgRtsByMatchGrp.get(grp1));
				rt2 = (grp2 == null ? feature2.getRT() : avgRtsByMatchGrp.get(grp2));

				if (Math.abs(rt1 - rt2) > rtTol)
					continue;

				Boolean printMsg = false; // i >= printLower && i <= printUpper;

				if (!redundantFeatureMap.containsKey(feature1.getName()))
					redundantFeatureMap.put(feature1.getName(), new ArrayList<String>());

				redundantFeatureMap.get(feature1.getName()).add(feature2.getName());

				feature1.setFlaggedAsDuplicate(true);
				feature2.setFlaggedAsDuplicate(true);

				if (newGroup) {
					feature1.setRedundancyGroup(nRedundancyGroups);
					feature2.setRedundancyGroup(nRedundancyGroups);

					grpSizeByMatchGrp.put(nRedundancyGroups, 2);
					avgRtsByMatchGrp.put(nRedundancyGroups, 0.5 * (feature1.getRT() + feature2.getRT()));
					// avgOldRtsByRedundancyGrp.put(nRedundancyGroups, 0.5 * (feature1.getOldRt() +
					// feature2.getOldRt()));
					avgMassesByMatchGrp.put(nRedundancyGroups, 0.5 * (feature1.getMass() + feature2.getMass()));

					// REVIEW
					if (map.get(nRedundancyGroups) == null) {
						map.put(nRedundancyGroups, new ArrayList<FeatureFromFile>());
						map.get(nRedundancyGroups).add(feature1);
						map.get(nRedundancyGroups).add(feature2);
					}

					nRedundancyGroups++;
				} else if (grp1 == null) {

					int grp = feature2.getRedundancyGroup();
					feature1.setRedundancyGroup(grp);
					int oldSize = this.grpSizeByMatchGrp.get(grp);
					double oldMassAvg = this.avgMassesByMatchGrp.get(grp);
					double oldRtAvg = this.avgRtsByMatchGrp.get(grp);
					double newMassAvg = oldMassAvg * oldSize + feature1.getMass();
					double newRtAvg = oldRtAvg * oldSize + feature1.getRT();
					oldSize++;
					newMassAvg /= (1.0 * oldSize);
					newRtAvg /= (1.0 * oldSize);

					this.grpSizeByMatchGrp.put(grp, oldSize);
					this.avgMassesByMatchGrp.put(grp, newMassAvg);
					this.avgRtsByMatchGrp.put(grp, newRtAvg);
				} else if (grp2 == null) {
					int grp = feature1.getRedundancyGroup();
					feature2.setRedundancyGroup(grp);
					int oldSize = this.grpSizeByMatchGrp.get(grp);
					double oldMassAvg = this.avgMassesByMatchGrp.get(grp);
					double oldRtAvg = this.avgRtsByMatchGrp.get(grp);
					double newMassAvg = oldMassAvg * oldSize + feature2.getMass();
					double newRtAvg = oldRtAvg * oldSize + feature2.getRT();
					oldSize++;
					newMassAvg /= (1.0 * oldSize);
					newRtAvg /= (1.0 * oldSize);

					this.grpSizeByMatchGrp.put(grp, oldSize);
					this.avgMassesByMatchGrp.put(grp, newMassAvg);
					this.avgRtsByMatchGrp.put(grp, newRtAvg);
				} else if (grp1 != null && grp2 != null && combineGroups && map != null) {

					if (grp1.equals(grp2))
						continue;

					Integer grpToKeep = grp1 > grp2 ? grp2 : grp1;
					Integer grpToMerge = grp1 > grp2 ? grp1 : grp2;

					int oldSize = this.grpSizeByMatchGrp.get(grpToKeep);
					double oldMassAvg = this.avgMassesByMatchGrp.get(grpToKeep);
					double oldRtAvg = this.avgRtsByMatchGrp.get(grpToKeep);

					int mergedSize = this.grpSizeByMatchGrp.get(grpToMerge);
					double mergedMassAvg = this.avgMassesByMatchGrp.get(grpToMerge);
					double mergedRtAvg = this.avgRtsByMatchGrp.get(grpToMerge);

					double newMassAvg = oldMassAvg * oldSize + mergedMassAvg * mergedSize;
					double newRtAvg = oldRtAvg * oldSize + mergedRtAvg * mergedSize;
					newMassAvg /= (1.0 * (oldSize + mergedSize));
					newRtAvg /= (1.0 * (oldSize + mergedSize));

					this.grpSizeByMatchGrp.put(grpToKeep, oldSize + mergedSize);
					this.avgMassesByMatchGrp.put(grpToKeep, newMassAvg);
					this.avgRtsByMatchGrp.put(grpToKeep, newRtAvg);

					if (map.containsKey(grpToMerge)) {

						for (FeatureFromFile feature : map.get(grpToMerge)) {
							feature.setRedundancyGroup(grpToKeep);
							feature.setAnnotation("Merged from " + grpToMerge + ";" + feature.getAnnotation());
							if (map.get(grpToKeep) != null)
								map.get(grpToKeep).add(feature);
							else
								System.out.println("Didn't merge to null map entry");
						}
						if (map.get(grpToKeep) != null)
							map.remove(grpToMerge);
					}
				}
			}
		}

	}

	public Map<String, String> getDerivedNameToHeaderMap() {
		return derivedNameToHeaderMap;
	}

	public List<String> getDerivedSampleNames() {
		if (derivedSampleNames != null && derivedSampleNames.size() > 3)
			return derivedSampleNames;

		derivedSampleNames = new ArrayList<String>();
		derivedNameToHeaderMap = new HashMap<String, String>();

		for (String fileName : orderedIntensityHeaders) {
			if (StringUtils.isEmptyOrNull(fileName)) {
				derivedSampleNames.add("");
				derivedNameToHeaderMap.put("", "");
				continue;
			}
			String mergeName = PostProcessMergeNameExtractor.extractMergeName(fileName);
			derivedSampleNames.add(mergeName);
			derivedNameToHeaderMap.put(mergeName + (this.getPosMode() ? "P" : "N"), fileName);
		}
		return derivedSampleNames;
	}

	public Map<Integer, List<FeatureFromFile>> grabFeatureByGroupMap(Boolean removeAmbiguous,
			Boolean removeUnambiguous) {

		Map<Integer, List<FeatureFromFile>> featuresByGroupMap = new HashMap<Integer, List<FeatureFromFile>>();

		for (int i = 0; i < features.size(); i++) {
			FeatureFromFile feature = features.get(i);

			if (feature.getRedundancyGroup() != null) {
				if (!featuresByGroupMap.containsKey(feature.getRedundancyGroup()))
					featuresByGroupMap.put(feature.getRedundancyGroup(), new ArrayList<FeatureFromFile>());

				featuresByGroupMap.get(feature.getRedundancyGroup()).add(feature);
			}
		}

		if (!removeAmbiguous && !removeUnambiguous)
			return featuresByGroupMap;

		Map<Integer, List<FeatureFromFile>> filteredFeaturesByGroupMap = new HashMap<Integer, List<FeatureFromFile>>();
		for (Integer group : featuresByGroupMap.keySet()) {

			List<FeatureFromFile> groupList = featuresByGroupMap.get(group);

			if (removeAmbiguous)
				if (groupList.size() > groupList.get(0).getnMatchReplicates())
					continue;

			if (removeUnambiguous)
				if (groupList.size() <= groupList.get(0).getnMatchReplicates())
					continue;

			filteredFeaturesByGroupMap.put(group, groupList);
		}
		return filteredFeaturesByGroupMap;
	}

	public List<FeatureFromFile> grabFeatureByGroupAsRTSortedList(
			Map<Integer, List<FeatureFromFile>> ambigousMatchedFeaturesMap) {

		List<FeatureFromFile> ambiguousMatchedFeatures = new ArrayList<FeatureFromFile>();
		Map<Integer, List<FeatureFromFile>> sortedAmbigousMatchedFeaturesMap = new HashMap<Integer, List<FeatureFromFile>>();

		for (Integer grp : ambigousMatchedFeaturesMap.keySet()) {
			List<FeatureFromFile> grpFeatures = ambigousMatchedFeaturesMap.get(grp);
			Double avgOldRT = 0.0;
			for (int i = 0; i < grpFeatures.size(); i++)
				avgOldRT += grpFeatures.get(i).getOldRt() == null ? grpFeatures.get(i).getRT()
						: grpFeatures.get(i).getOldRt();

			avgOldRT /= (grpFeatures.size() * 1.0);

			Integer key = (int) (avgOldRT * 100000);

			int reps = 0;
			while (sortedAmbigousMatchedFeaturesMap.containsKey(key) && reps < 100) {
				key += 1;
				reps++;
			}

			sortedAmbigousMatchedFeaturesMap.put(key, grpFeatures);
		}

		List<Integer> sortedKeys = ListUtils.makeListFromCollection(sortedAmbigousMatchedFeaturesMap.keySet());
		Collections.sort(sortedKeys);

		for (int j = 0; j < sortedKeys.size(); j++) {
			List<FeatureFromFile> grpFeatures = sortedAmbigousMatchedFeaturesMap.get(sortedKeys.get(j));

			Collections.sort(grpFeatures, new FeatureByBatchComparator());
			for (int i = 0; i < grpFeatures.size(); i++) {
				ambiguousMatchedFeatures.add(grpFeatures.get(i));
			}
			// ambiguousMatchedFeatures.add(null);
		}
		return ambiguousMatchedFeatures;
	}

	public Map<Integer, List<FeatureFromFile>> grabFeatureByPossibleGroupMap(Double massTol, Double rtTol) {

		Map<Integer, List<FeatureFromFile>> featuresByGroupMap = grabFeatureByGroupMap(false, false);
		Map<Integer, List<FeatureFromFile>> featuresByPossibleGroupMap = new HashMap<Integer, List<FeatureFromFile>>();
		Collections.sort(features, new FeatureByMassAndRtComparator());

		double currOldRt, currRt;
		for (Integer group : featuresByGroupMap.keySet()) {

			List<FeatureFromFile> groupList = featuresByGroupMap.get(group);
			if (groupList.size() <= groupList.get(0).getnMatchReplicates())
				continue;

			double minOldRt = Double.MAX_VALUE, minRt = Double.MAX_VALUE;
			double maxOldRt = Double.MIN_VALUE, maxRt = Double.MIN_VALUE;

			for (int i = 0; i < groupList.size(); i++) {

				currOldRt = groupList.get(i).getOldRt();
				currRt = groupList.get(i).getRT();

				minOldRt = Math.min(currOldRt, minOldRt);
				maxOldRt = Math.max(currOldRt, maxOldRt);

				minRt = Math.min(currRt, minRt);
				maxRt = Math.max(currRt, maxRt);
			}
			List<FeatureFromFile> possibleGroupFeatures = new ArrayList<FeatureFromFile>();

			Map<String, String> existingNames = new HashMap<String, String>();
			for (FeatureFromFile f : groupList) {
				possibleGroupFeatures.add(f.makeDeepCopy());
				existingNames.put(f.getName(), null);
			}

			minOldRt -= rtTol;
			minRt -= rtTol;
			maxOldRt += rtTol;
			maxRt += rtTol;

			Collections.sort(groupList, new FeatureByMassAndRtComparator());
			double minMass = groupList.get(0).getMass();
			double maxMass = groupList.get(groupList.size() - 1).getMass();
			double currMass = 0.0, oldRt = 0.0, newRt = 0.0;
			for (int i = 0; i < features.size(); i++) {

				if (existingNames.containsKey(features.get(i).getName()))
					continue;

				currMass = features.get(i).getMass();
				if (currMass > (maxMass + massTol))
					break;

				if (currMass < (minMass - massTol))
					continue;

				currOldRt = features.get(i).getOldRt();
				currRt = features.get(i).getRT();

				if (currOldRt > maxOldRt)
					if (currRt > maxRt)
						break;

				if (currOldRt < minOldRt)
					if (currRt < minRt)
						break;

				possibleGroupFeatures.add(features.get(i).makeDeepCopy());
			}
			Collections.sort(possibleGroupFeatures, new FeatureByMassAndRtComparator());
			featuresByPossibleGroupMap.put(group, possibleGroupFeatures);
		}
		return featuresByPossibleGroupMap;
	}

	public Map<Integer, List<FeatureFromFile>> grabUnambigousByPruningAmbiguousGroupMap() {

		Map<Integer, List<FeatureFromFile>> featuresByGroupMap = grabFeatureByGroupMap(false, false);

		Map<Integer, List<FeatureFromFile>> ambiguousFeaturesMap = new HashMap<Integer, List<FeatureFromFile>>();
		Map<Integer, List<FeatureFromFile>> newUnambiguousFeaturesMap = new HashMap<Integer, List<FeatureFromFile>>();

		for (Integer group : featuresByGroupMap.keySet()) {

			List<FeatureFromFile> groupList = featuresByGroupMap.get(group);
			if (groupList.size() <= groupList.get(0).getnMatchReplicates())
				continue;

			ambiguousFeaturesMap.put(group, groupList);
		}

		for (Integer group : ambiguousFeaturesMap.keySet()) {

			List<FeatureFromFile> groupList = ambiguousFeaturesMap.get(group);
			if (groupList.size() > groupList.get(0).getnMatchReplicates() + 1)
				continue;

			Collections.sort(groupList, new FeatureByBatchComparator());

			Integer duplicatedBatch = null;
			for (int i = 0; i < groupList.size() - 1; i++) {
				if (groupList.get(i).getBatchIdx().equals(groupList.get(i + 1).getBatchIdx())) {
					duplicatedBatch = i;
					break;
				}
			}

			FeatureFromFile dup1 = groupList.get(duplicatedBatch);
			FeatureFromFile dup2 = groupList.get(duplicatedBatch + 1);

			if (dup1.getMedianIntensity() == null && dup2.getMedianIntensity() == null)
				continue;

			if (dup1.getMedianIntensity() != null && dup2.getMedianIntensity() != null)
				continue;

			Integer idxToDelete = null;
			if (dup1.getMedianIntensity() == null)
				idxToDelete = duplicatedBatch;
			else
				idxToDelete = duplicatedBatch + 1;

			List<FeatureFromFile> prunedGroupList = new ArrayList<FeatureFromFile>();
			for (int i = 0; i < groupList.size(); i++) {
				if (!idxToDelete.equals(i))
					prunedGroupList.add(groupList.get(i));
			}
			newUnambiguousFeaturesMap.put(group, prunedGroupList);
		}

		return newUnambiguousFeaturesMap;
	}

	public Map<Integer, List<FeatureFromFile>> grabFeatureByAmbiguousGroupMap() {

		Map<Integer, List<FeatureFromFile>> featuresByGroupMap = grabFeatureByGroupMap(false, false);
		Map<Integer, List<FeatureFromFile>> featuresByAmbiguousGroupMap = new HashMap<Integer, List<FeatureFromFile>>();
		Collections.sort(features, new FeatureByMassAndRtComparator());

		for (Integer group : featuresByGroupMap.keySet()) {

			List<FeatureFromFile> groupList = featuresByGroupMap.get(group);
			if (groupList.size() <= groupList.get(0).getnMatchReplicates())
				continue;

			Collections.sort(groupList, new FeatureByMassAndRtComparator());
			featuresByAmbiguousGroupMap.put(group, groupList);
		}
		return featuresByAmbiguousGroupMap;
	}

	private Map<Integer, List<FeatureFromFile>> grabFeaturesByBatchMap() {

		Map<Integer, List<FeatureFromFile>> byBatchMap = new HashMap<Integer, List<FeatureFromFile>>();

		Integer currBatch = null;

		for (FeatureFromFile f : features) {
			currBatch = f.getBatchIdx();

			if (!byBatchMap.containsKey(currBatch))
				byBatchMap.put(currBatch, new ArrayList<FeatureFromFile>());

			byBatchMap.get(currBatch).add(f);
		}
		return byBatchMap;
	}

	private Integer determineCorrectPrintTargetBasedOnName() {

		Map<Integer, List<FeatureFromFile>> featuresByBatchMap = this.grabFeaturesByBatchMap();

		List<Integer> sortedBatchIndices = this.getSortedUniqueBatchIndices();

		Integer targetBatch = 0, nChecked = null;
		String featureName;

		for (int i = 0; i < sortedBatchIndices.size(); i++) {
			targetBatch = sortedBatchIndices.get(i);

			if (isTargetBatchCorrectForNameFormat(targetBatch, false))
				break;
		}
		return targetBatch;
	}

	private Boolean isTargetBatchCorrectForNameFormat(Integer targetBatch, Boolean printDiagnostics) {

		Map<Integer, List<FeatureFromFile>> featuresByBatchMap = this.grabFeaturesByBatchMap();
		if (featuresByBatchMap.get(targetBatch) == null || featuresByBatchMap.get(targetBatch).size() < 1) {
			if (printDiagnostics)
				System.out.println("No features for batch" + targetBatch);
			return false;
		}

		String featureName;
		Integer nChecked;
		for (nChecked = 0; nChecked < 10; nChecked++) {
			featureName = featuresByBatchMap.get(targetBatch).get(nChecked).getName();

			if (featureName.contains("@"))
				return false;
		}
		return true;
	}

	public void collapseFeatureGroups() {

		for (int i = 1; i <= this.maxPossibleMatchCt; i++)
			this.orderedNonStandardHeaders.add("Original RT" + i);

		Map<Integer, List<FeatureFromFile>> featuresByGroupMap = grabFeatureByGroupMap(false, false);
		int nameTargetBatch = determineCorrectPrintTargetBasedOnName();

		List<FeatureFromFile> newFeatureList = new ArrayList<FeatureFromFile>();
		buildAvgRtMassByMatchGrpMap(false);
		Map<String, List<String>> rtsByRedundancyGrpMap = buildDetailedRtsByRedundancyGroupMap();

		Map<Integer, Integer> collapsedGroups = new HashMap<Integer, Integer>();

		Integer lastNameTargetIdx = 0;
		Integer nNameTargetWarningsPrinted = 0;
		for (int i = 0; i < features.size(); i++) {
			FeatureFromFile feature = features.get(i);
			if (feature.getRedundancyGroup() == null)
				newFeatureList.add(feature);
			else if (!collapsedGroups.containsKey(feature.getRedundancyGroup())) {
				List<FeatureFromFile> featuresForGroup = featuresByGroupMap.get(feature.getRedundancyGroup());
				Collections.sort(featuresForGroup, new FeatureByBatchComparator());

				FeatureFromFile newFeature = new FeatureFromFile();

				Integer nameTargetIdx = 0;
				for (int fn = 0; fn < featuresForGroup.size(); fn++) {
					if (featuresForGroup.get(fn).getBatchIdx().equals(nameTargetBatch))
						nameTargetIdx = fn;
				}

				if (i > 0 && nNameTargetWarningsPrinted < 20 && !nameTargetIdx.equals(lastNameTargetIdx)) {

					System.out.println("Warning : Batch for feature name label is inconsistent -- now using batch "
							+ nameTargetIdx);
					lastNameTargetIdx = nameTargetIdx;
					nNameTargetWarningsPrinted++;
				}

				newFeature.initialize(featuresForGroup.get(nameTargetIdx));
				newFeature.setPctMissing(null);
				newFeature.setRsd(null);
				newFeature.setPutativeMolecularMass(avgMassesByMatchGrp.get(feature.getRedundancyGroup()));
				newFeature.setRT(avgRtsByMatchGrp.get(feature.getRedundancyGroup()));
				newFeature.setOldRt(avgOldRtsByMatchGrp.get(feature.getRedundancyGroup()));
				newFeature.setMass(avgMassesByMatchGrp.get(feature.getRedundancyGroup()));
				newFeature.setMedianIntensity(avgMedIntensityByMatchGrp.get(feature.getRedundancyGroup()));
				collapsedGroups.put(feature.getRedundancyGroup(), null);
				collapseIntensities(newFeature, featuresForGroup, orderedIntensityHeaders);

				List<String> rtsForGroup = rtsByRedundancyGrpMap.get(feature.getRedundancyGroup().toString());
				for (int j = 0; j < rtsForGroup.size(); j++) {
					newFeature.getAddedColValues().add(rtsForGroup.get(j));
				}
				newFeatureList.add(newFeature);
			}
		}
		features.clear();
		features.addAll(newFeatureList);
		initializeRSDsForCollapsedFeatures();
	}

	private void collapseIntensities(FeatureFromFile newFeature, List<FeatureFromFile> redundantFeatures,
			List<String> orderedIntensityHeaders) {

		HashMap<String, String> mergedIntensityMap = new HashMap<String, String>();
		HashMap<String, List<String>> multiValuedHeaderMap = new HashMap<String, List<String>>();

		for (int j = 0; j < redundantFeatures.size(); j++) {

			FeatureFromFile redundantFeature = redundantFeatures.get(j);

			for (String key : redundantFeature.getIntensityValuesByHeaderMap().keySet()) {

				if (!mergedIntensityMap.containsKey(key)) {
					if (!StringUtils.isEmptyOrNull(redundantFeature.getIntensityValuesByHeaderMap().get(key)))
						mergedIntensityMap.put(key, redundantFeature.getIntensityValuesByHeaderMap().get(key));
				} else {
					if (!multiValuedHeaderMap.containsKey(key)) {
						multiValuedHeaderMap.put(key, new ArrayList<String>());
						multiValuedHeaderMap.get(key).add(mergedIntensityMap.get(key));
					}
					multiValuedHeaderMap.get(key).add(redundantFeature.getIntensityValuesByHeaderMap().get(key));
				}
			}
		}

		// for value clashes (due to multiple intensity measure for a single
		// sample/control), substitute the median; //average the intensities
		List<String> valuesForKey = null;

		for (String key : multiValuedHeaderMap.keySet()) {

			valuesForKey = multiValuedHeaderMap.get(key);
			double[] validIntensities = new double[valuesForKey.size()];
			Median median = new Median();

			int k = 0;
			Boolean foundDbl = false;
			for (int i = 0; i < valuesForKey.size(); i++) {

				Double value = null;

				try {
					value = Double.parseDouble(valuesForKey.get(i));
					foundDbl = true;
				} catch (Exception e) {
				} // [j];

				if (value == null || value < 0.0)
					continue;

				validIntensities[k++] = value;
			}

			if (foundDbl) {
				Double featureMedian = median.evaluate(Arrays.copyOfRange(validIntensities, 0, k));
				mergedIntensityMap.put(key, featureMedian.toString());
			}
		}
		newFeature.getIntensityValuesByHeaderMap().clear();
		newFeature.getIntensityValuesByHeaderMap().putAll(mergedIntensityMap);
	}

	public void updateForNewIntensityColumns(List<String> newHeaders) {
		if (this.orderedIntensityHeaders == null)
			orderedIntensityHeaders = new ArrayList<String>();

		for (int i = 0; i < newHeaders.size(); i++) {
			if (!orderedIntensityHeaders.contains(newHeaders.get(i)))
				orderedIntensityHeaders.add(newHeaders.get(i));
		}
	}

	public void initializeHeaders(List<FeatureFromFile> features, List<String> orderedNonStandardHeaders,
			List<String> orderedIntensityHeaders, List<String> orderedExtraLibFileHeaders, Boolean posMode,
			Boolean ifPrincipalIons, String sourceReport, String libFileSource, Map<String, String> derivedNameMap) {

		this.features = features;

		this.orderedNonStandardHeaders = orderedNonStandardHeaders;
		this.orderedIntensityHeaders = orderedIntensityHeaders;
		this.derivedNameToHeaderMap = derivedNameMap;

		if (orderedExtraLibFileHeaders == null)
			this.orderedExtraLibFileHeaders = new ArrayList<String>();
		else
			this.orderedExtraLibFileHeaders = orderedExtraLibFileHeaders;

		this.posMode = posMode;
		// this.ifPrincipalIons = ifPrincipalIons;
		this.sourceReport = sourceReport;
		this.libFileSource = libFileSource;
	}

	public int getMaxBatch() {

		int maxBatch = 0;
		for (FeatureFromFile feature : features) {
			if (feature.getBatchIdx() != null && feature.getBatchIdx() > maxBatch)
				maxBatch = feature.getBatchIdx();
		}
		return maxBatch;
	}

	public List<Integer> getZeroPaddedFilteredFeaturesByBatchCts() {

		List<Integer> batchCts = new ArrayList<Integer>();

		int maxBatchIdx = getMaxBatch();
		for (int i = 0; i < maxBatchIdx; i++)
			batchCts.add(0);

		for (FeatureFromFile f : features) {
			if (f.getBatchIdx() != null) {
				int currCt = batchCts.get(f.getBatchIdx() - 1);
				batchCts.set(f.getBatchIdx() - 1, currCt + 1);
			}
		}
		return batchCts;
	}

	public void filterPutativeSalts(Double missingCutoff, Double mdCutoff, Double highMassCutoff,
			Double earlyRtCutoff) {

		List<FeatureFromFile> newFeatures = new ArrayList<FeatureFromFile>();

		List<String> poolCols = getPoolColNames();

		Double pctMissing = null;
		for (FeatureFromFile feature : this.features) {
			// feature.initializeMissingnessPct(orderedIntensityHeaders,
			// derivedNameToHeaderMap, nValuesPossible);
			pctMissing = feature.getPoolMissingnessPct(poolCols, derivedNameToHeaderMap);

			if (pctMissing > missingCutoff)
				continue;

			if (feature.getRT() < earlyRtCutoff
					&& (feature.getMassDefect() > mdCutoff || feature.getMass() > highMassCutoff))
				continue;

			FeatureFromFile newFeature = feature.makeDeepCopy();
			newFeatures.add(newFeature);
		}

		this.features.clear();
		for (int i = 0; i < newFeatures.size(); i++)
			this.features.add(newFeatures.get(i));
	}

	public void filterDuplicates(Integer batchIdx) {

		List<FeatureFromFile> newFeatures = new ArrayList<FeatureFromFile>();

		Collections.sort(this.features, new FeatureByNameComparator());

		for (int i = 0; i < features.size(); i++) {
			// feature.initializeMissingnessPct(orderedIntensityHeaders,
			// derivedNameToHeaderMap, nValuesPossible);
			String oldName = features.get(i).getName();
			String newName = features.get(i).getName() + "-" + features.get(i).getMass() + "-"
					+ (features.get(i).getBatchIdx() == null ? batchIdx : features.get(i).getBatchIdx());
			features.get(i).setName(newName);

			FeatureFromFile newFeature = features.get(i).makeDeepCopy();
			newFeatures.add(newFeature);
		}

		this.features.clear();
		for (int i = 0; i < newFeatures.size(); i++)
			this.features.add(newFeatures.get(i));
	}

	// CS00
	public List<String> getPoolColNames() {

		List<String> poolColNames = new ArrayList<String>();

		for (String col : this.orderedIntensityHeaders) {
			if (PostProcessMergeNameExtractor.isMasterPool(col))
				poolColNames.add(col);
		}
		return poolColNames;
	}

	public Integer getNTotalFilteredFeatures() {
		List<Integer> nFilterdFeaturesByBatch = this.getZeroPaddedFilteredFeaturesByBatchCts();

		Integer nTotalFilteredFeatures = 0;
		for (Integer ct : nFilterdFeaturesByBatch)
			nTotalFilteredFeatures += ct;

		return nTotalFilteredFeatures;
	}

	public Integer getNTotalRawFeatures() {
		if (this.rawFeatureCtsByBatch == null)
			return 0;

		Integer nTotalRawFeatures = 0;
		for (Integer key : this.rawFeatureCtsByBatch.keySet())
			nTotalRawFeatures += rawFeatureCtsByBatch.get(key);

		return nTotalRawFeatures;
	}

	public List<Integer> getZeroPaddedRawFeaturesByBatchCts() {
		List<Integer> rawFeatureCts = ListUtils.makeListFromCollection(this.rawFeatureCtsByBatch.keySet());

		Collections.sort(rawFeatureCts);
		int maxCt = rawFeatureCts.get(rawFeatureCts.size() - 1);

		List<Integer> zeroPaddedRawFeatureCts = new ArrayList<Integer>();
		for (Integer i = 0; i < maxCt + 1; i++) {
			Integer ct = 0;
			if (rawFeatureCtsByBatch.containsKey(i))
				ct = rawFeatureCtsByBatch.get(i);
			zeroPaddedRawFeatureCts.add(ct);
		}
		return zeroPaddedRawFeatureCts;
	}

	public List<Integer> getSampleCtsByBatch() {

		List<Integer> sampleCtsByBatch = new ArrayList<Integer>();

		int maxBatchIdx = getMaxBatch();
		for (int i = 0; i < maxBatchIdx; i++)
			sampleCtsByBatch.add(0);

		for (FeatureFromFile f : features) {
			if (f.getBatchIdx() != null) {
				sampleCtsByBatch.set(f.getBatchIdx() - 1, f.getIntensityValuesByHeaderMap().keySet().size());
			}
		}
		return sampleCtsByBatch;
	}

	public Map<Integer, Integer> getFeatureCtsByBatchMap() {

		Map<Integer, Integer> featureCtsByBatchMap = new HashMap<Integer, Integer>();

		Integer currBatch = null, currCt = null;
		for (FeatureFromFile f : features) {
			currBatch = f.getBatchIdx();

			if (!featureCtsByBatchMap.containsKey(currBatch))
				featureCtsByBatchMap.put(currBatch, 0); // new ArrayList<FeatureFromFile>());

			currCt = featureCtsByBatchMap.get(currBatch);
			featureCtsByBatchMap.put(currBatch, currCt + 1);
		}
		return featureCtsByBatchMap;
	}

	public Integer getMinimumBatchSize() {
		Integer minCt = Integer.MAX_VALUE;

		Map<Integer, Integer> ctByBatchMap = getFeatureCtsByBatchMap(); // new HashMap<Integer, Integer>();

		for (Integer ct : ctByBatchMap.values()) {
			if (ct < minCt)
				minCt = ct;
		}
		return minCt;
	}

	private void initializeRSDsForCollapsedFeatures() {

		int nValuesPossible = orderedIntensityHeaders.size();
		for (String hdr : orderedIntensityHeaders)
			if (StringUtils.isEmptyOrNull(hdr))
				nValuesPossible--;

		for (FeatureFromFile feature : features)
			feature.initializeMissingnessPct(orderedIntensityHeaders, derivedNameToHeaderMap, nValuesPossible);
		

		List<String> colsForRSD = new ArrayList<String>();
		for (Integer batchNo : colsForRSDByBatch.keySet())
			colsForRSD.addAll(colsForRSDByBatch.get(batchNo));

		calculateRSDsFromSampleList(colsForRSD, null);
		for (Integer batchNo : colsForRSDByBatch.keySet()) 
			calculateRSDsFromSampleList(colsForRSDByBatch.get(batchNo), batchNo);
		
	}

	public void initializeMissingnessPcts() {
		int nValuesPossible = orderedIntensityHeaders.size();
		for (String hdr : orderedIntensityHeaders)
			if (StringUtils.isEmptyOrNull(hdr))
				nValuesPossible--;

		for (FeatureFromFile feature : features)
			feature.initializeMissingnessPct(orderedIntensityHeaders, derivedNameToHeaderMap, nValuesPossible);
	}

	private void calculateRSDsFromSampleList(List<String> colsForRSD, Integer batchNo) {

		if (colsForRSD != null && colsForRSD.size() < 1) {
			for (FeatureFromFile feature : features)
				feature.setRsd(null);
		}

		List<String> relevantHeaders = new ArrayList<String>();
		if (colsForRSD != null) {
			for (int i = 0; i < this.orderedIntensityHeaders.size(); i++) {
				if (colsForRSD.contains(orderedIntensityHeaders.get(i)))
					relevantHeaders.add(orderedIntensityHeaders.get(i));
			}
		} else
			relevantHeaders.addAll(PostProcessMergeNameExtractor.determineNonSampleCols(orderedIntensityHeaders));

		StandardDeviation stdev = null;
		int currentIdx = 0;
		double mean = 0.0, rsd = 0.0;
		Double dblRSD = null, candidateVal = null;
		String currIntensity;

		for (FeatureFromFile feature : features) {

			double[] dblValues = new double[relevantHeaders.size()];
			currentIdx = 0;
			mean = 0.0;

			for (int i = 0; i < relevantHeaders.size(); i++) {
				try {
					// values in original cols are guaranteed to be "-" if missing
					currIntensity = feature.getValueForIntensityHeader(relevantHeaders.get(i), derivedNameToHeaderMap);
					if (StringUtils.isEmptyOrNull(currIntensity) || "-".equals(currIntensity)
							|| ".".equals(currIntensity))
						continue;

					candidateVal = null;

					try {
						candidateVal = Double.parseDouble(currIntensity);
					} catch (Exception e) {
						continue;
					}
					dblValues[currentIdx++] = candidateVal;
					mean += candidateVal;
				} catch (Exception e) {
					continue;
				}
			}

			dblRSD = null;
			if (currentIdx > 0) {
				mean /= (currentIdx * 1.0);
				stdev = new StandardDeviation();
				rsd = stdev.evaluate(dblValues, mean, 0, currentIdx) * 100.0 / mean;
				dblRSD = rsd;
			}
			if (batchNo == null)
				feature.setRsd(dblRSD);
			else {
				feature.setBatchwiseRSD(batchNo, dblRSD);
			}
		}
	}

	public Boolean getRsdPctMissPrecalculated() {
		return rsdPctMissPrecalculated;
	}

	public void setRsdPctMissPrecalculated(Boolean rsdPctMissPrecalculated) {
		this.rsdPctMissPrecalculated = rsdPctMissPrecalculated;
	}

	public Map<String, Double> getFeatureNameToOldRtMap() {
		return featureNameToOldRtMap;
	}

	public void setFeatureNameToOldRtMap(Map<String, Double> featureNameToOldRtMap) {
		this.featureNameToOldRtMap = featureNameToOldRtMap;
	}

	public Map<Integer, Double> getAvgRtsByMatchGrp() {
		return avgRtsByMatchGrp;
	}

	public Map<Integer, Double> getAvgMassesByMatchGrp() {
		return avgMassesByMatchGrp;
	}

	public void setAvgMassesByMatchGrp(Map<Integer, Double> avgMassesByMatchGrp) {
		this.avgMassesByMatchGrp = avgMassesByMatchGrp;
	}

	public void setAvgRtsByMatchGrp(Map<Integer, Double> avgRtsByRedundancyGrp) {
		this.avgRtsByMatchGrp = avgRtsByRedundancyGrp;
	}

	public Map<Integer, Double> getAvgOldRtsByRedundancyGrp() {
		return avgOldRtsByMatchGrp;
	}

	public void setAvgOldRtsByMatchGrp(Map<Integer, Double> avgOldRtsByRedundancyGrp) {
		this.avgOldRtsByMatchGrp = avgOldRtsByRedundancyGrp;
	}

	public Integer getMaxPossibleMatchCt() {
		return maxPossibleMatchCt;
	}

	public void setMaxPossibleMatchCt(Integer maxPossibleMatchCt) {
		this.maxPossibleMatchCt = maxPossibleMatchCt;
	}

	public Map<Integer, Integer> getRawFeatureCtsByBatch() {
		return rawFeatureCtsByBatch;
	}

	public void setRawFeatureCtsByBatch(Map<Integer, Integer> rawFeatureCtsByBatch) {
		this.rawFeatureCtsByBatch = rawFeatureCtsByBatch;
	}

	public Map<Integer, List<String>> getColsForRSDByBatch() {
		return colsForRSDByBatch;
	}

	public void setColsForRSDByBatch(Map<Integer, List<String>> colsForRSDByBatch) {
		this.colsForRSDByBatch = colsForRSDByBatch;
	}

	public List<FeatureFromFile> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureFromFile> features) {
		this.features = features;
	}

	public List<String> getOrderedExtraLibFileHeaders() {
		return orderedExtraLibFileHeaders;
	}

	public void setOrderedExtraLibFileHeaders(List<String> orderedExtraLibFileHeaders) {
		this.orderedExtraLibFileHeaders = orderedExtraLibFileHeaders;
	}

	public List<String> getOrderedNonStandardHeaders() {
		return orderedNonStandardHeaders;
	}

	public void setOrderedNonStandardHeaders(List<String> orderedNonStandardHeaders) {
		this.orderedNonStandardHeaders = orderedNonStandardHeaders;
	}

	public List<String> getOrderedIntensityHeaders() {
		return orderedIntensityHeaders;
	}

	public void setOrderedIntensityHeaders(List<String> orderedIntensityHeaders) {
		this.orderedIntensityHeaders = orderedIntensityHeaders;
	}

	public Boolean getPosMode() {
		return posMode;
	}

	public void setPosMode(Boolean posMode) {
		this.posMode = posMode;
	}

	public Boolean getIfPrincipalIons() {
		return ifPrincipalIons;
	}

	public void setIfPrincipalIons(Boolean ifPrincipalIons) {
		this.ifPrincipalIons = ifPrincipalIons;
	}

	public String getSourceReport() {
		return sourceReport;
	}

	public void setSourceReport(String sourceReport) {
		this.sourceReport = sourceReport;
	}

	public String getLibFileSource() {
		return libFileSource;
	}

	public void setLibFileSource(String libFileSource) {
		this.libFileSource = libFileSource;
	}

	public String getDataSetLabel() {
		return dataSetLabel;
	}

	public void setDataSetLabel(String dataSetLabel) {
		this.dataSetLabel = dataSetLabel;
	}
}
