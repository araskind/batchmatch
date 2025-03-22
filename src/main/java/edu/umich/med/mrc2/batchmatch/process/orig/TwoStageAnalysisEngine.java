////////////////////////////////////////////////////
// TwoStageAnalysisEngine.java
// Written by Jan Wigginton February 2023
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureInfoForMatchGroupMapping;
import edu.umich.med.mrc2.batchmatch.data.orig.RtPair;

public class TwoStageAnalysisEngine {

	public static void createStageTwoData(PostProcessDataSet data1, Integer newBatch, String initialDirectory) {
		// Here we need to write each aligned data set in binnerFormat using only full
		// match pairs
		// with the original featureGroup moved to the isotope column, matchgroup set to
		// null and
		// the batch changed to "1" or "2" as appropriate then write out a (possibly
		// redundant) blank fileMapping set
	}

	// This writes out the pair column portion of the two stage mapping. A header is
	// assumed specifying all
	// batches it pertains to --> thus all features in the original group are mapped
	// to new group, keeping the
	// the original batch index
	public static void createStageTwoDataSetMapping(PostProcessDataSet data, String initialDirectory) {

		// Need to add string for top of file to specify batches

		// here we read in two batch alignment with original match group written in
		// further annotation column
		// we need to write these out as a two column map

		Map<Integer, RtPair> mapping = new HashMap<Integer, RtPair>();
		Map<Integer, Double> newRTs = new HashMap<Integer, Double>();

		Double newRT = null;
		RtPair rtPair = null;
		for (FeatureFromFile f : data.getFeatures()) {

			// current group for pairwise mapping
			Integer groupForMappedPair = f.getRedundancyGroup();

			// the original match group for this feature
			String strMatchGroup = f.getFurtherAnnotation();
			Integer matchGroup = null;
			try {
				matchGroup = Integer.parseInt(strMatchGroup);
			} catch (Exception e) {
				continue;
			}

			// ensures only mapped group pairs
			if (f.getBatchIdx() == null)
				continue;

			if (!mapping.containsKey(groupForMappedPair))
				mapping.put(groupForMappedPair, new RtPair());

			if (f.getBatchIdx().equals(2)) {
				mapping.get(groupForMappedPair).setRt1(matchGroup * 1.0);
				newRTs.put(groupForMappedPair, f.getRT());
			} else
				mapping.get(groupForMappedPair).setRt2(matchGroup * 1.0);
		}

		for (Integer sourceGrp : mapping.keySet()) {

			rtPair = mapping.get(sourceGrp);
			newRT = newRTs.get(sourceGrp);

			double sourceBatch = rtPair.getRt1();
			double destBatch = rtPair.getRt2();

			Integer sourceAsInt = (int) sourceBatch;
			Integer destAsInt = (int) destBatch;
			String formattedRT = String.format("%.4f", (newRT == null ? "" : newRT));
			System.out.println(sourceAsInt + "," + destAsInt + "," + formattedRT);
		}
	}
	// Group:

	// Using just batch 2 data, go through all features all batches by match group,
	// locate and reassign maatch group and replace RT with projected Rt
	public static void translateFromDataSetMapping(
			Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> dataSetMapping,
			PostProcessDataSet dataToTranslate) {

		List<FeatureFromFile> newFeatures = new ArrayList<FeatureFromFile>();

		for (FeatureFromFile f : dataToTranslate.getFeatures()) {
			Integer group = f.getRedundancyGroup();
			Integer batch = f.getBatchIdx();
			FeatureFromFile newFeature = f.makeDeepCopy();

			// feature didn't group
			if (batch == null || group == null) {
				newFeatures.add(newFeature);
				continue;
			}

			// We're not mapping this batch
			if (!dataSetMapping.containsKey(batch)) {
				newFeatures.add(newFeature);
				continue;
			}

			// Mapping batch, but this group didn't map
			if (!dataSetMapping.get(batch).containsKey(group)) {
				// we don't want to assign the old match group as it may clash with a
				// destination match group #
				newFeature.setRedundancyGroup(null);
				newFeatures.add(newFeature);
				continue;
			}

			newFeature.setRedundancyGroup(dataSetMapping.get(batch).get(group).getDestinationBatch());
			newFeature.setRT(dataSetMapping.get(batch).get(group).getMappedRt());

			newFeatures.add(newFeature);
		}
		dataToTranslate.setFeatures(newFeatures);

		// for (FeatureFromFile f : dataToTranslate.getFeatures()) {
		// System.out.print(f);
		// }
	}
}
