////////////////////////////////////////////////////
// BacktrackingEngine.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.FeatureByMassComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;

public class BacktrackingEngine {

	/**
	 * Map batch # to to the lists of features in this batch split in one mass unit intervals
	 * massKeyAsFloorDbl converted to string is used as key
	 * 
	 * @param featuresToMap
	 * @param unClaimedOnly
	 * @return
	 */
	public static Map<Integer, Map<String, List<FeatureFromFile>>> mapFeaturesByBatchAndMass(
			List<FeatureFromFile> featuresToMap, Boolean unClaimedOnly) {

		Collections.sort(featuresToMap, new FeatureByMassComparator());

		String keyFormat = "%.0f";

		Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBatchAndMassMap = 
				new HashMap<Integer, Map<String, List<FeatureFromFile>>>();

		for (FeatureFromFile f : featuresToMap) {
			
			if (unClaimedOnly && f.getRedundancyGroup() != null)
				continue;

			Integer batchKeyForFeature = f.getBatchIdx();
			if (!featuresByBatchAndMassMap.containsKey(batchKeyForFeature))
				featuresByBatchAndMassMap.put(batchKeyForFeature, new HashMap<String, List<FeatureFromFile>>());

			Double massKeyAsFloorDbl = Math.floor(f.getMass());
			String massKeyForFeature = String.format(keyFormat, massKeyAsFloorDbl);

			Map<String, List<FeatureFromFile>> featuresByMassMap = featuresByBatchAndMassMap.get(batchKeyForFeature);

			if (!featuresByMassMap.containsKey(massKeyForFeature))
				featuresByMassMap.put(massKeyForFeature, new ArrayList<FeatureFromFile>());

			featuresByMassMap.get(massKeyForFeature).add(f);
			featuresByBatchAndMassMap.put(batchKeyForFeature, featuresByMassMap);
		}
		return featuresByBatchAndMassMap;
	}
}
