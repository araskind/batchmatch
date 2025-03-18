///////////////////////////////////////////////////
//BatchDataMerger.java
//Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.io.StringListMerger;

public class BatchDataMerger {

	public BatchDataMerger() {
	}

	public PostProcessDataSet mergeDataSetHeaders(PostProcessDataSet dataSet1, PostProcessDataSet dataSet2,
			Boolean useSampleNames, Double massTol, Double rtTol) {

		List<String> mergedNonStandardLibHeader = StringListMerger.createMergedStringList(
				dataSet1.getOrderedExtraLibFileHeaders(), dataSet2.getOrderedExtraLibFileHeaders());
		List<String> mergedNonStandardHeader = StringListMerger.createMergedStringList(
				dataSet1.getOrderedNonStandardHeaders(), dataSet2.getOrderedNonStandardHeaders());

		List<String> list1 = useSampleNames ? dataSet1.getDerivedSampleNames() : dataSet1.getOrderedIntensityHeaders();
		List<String> list2 = useSampleNames ? dataSet2.getDerivedSampleNames() : dataSet2.getOrderedIntensityHeaders();
		List<String> mergedIntensityHeader = StringListMerger.createMergedStringList(list1, list2);

		Map<String, String> mergedNameMap = null;
		if (useSampleNames) {
			mergedNameMap = new HashMap<String, String>();
			mergedNameMap.putAll(dataSet1.getDerivedNameToHeaderMap());
			mergedNameMap.putAll(dataSet2.getDerivedNameToHeaderMap());
		}

		Map<Integer, Integer> mergeNonStandardColumnMap = StringListMerger
				.createColumnMergeMap(dataSet1.getOrderedNonStandardHeaders(), dataSet2.getOrderedNonStandardHeaders());
		List<FeatureFromFile> jointFeatures = mergeFeaturesFromColumnMap(mergeNonStandardColumnMap,
				dataSet2.getFeatures(), dataSet1.getFeatures(), dataSet1.getDataSetLabel(), dataSet2.getDataSetLabel());

		PostProcessDataSet mergedSet = new PostProcessDataSet();
		mergedSet.initializeHeaders(jointFeatures, mergedNonStandardHeader, mergedIntensityHeader,
				mergedNonStandardLibHeader, // dataSet1.getOrderedExtraLibFileHeaders(),
				dataSet1.getPosMode(), false, dataSet1.getSourceReport(), dataSet1.getLibFileSource(), mergedNameMap);

		return mergedSet;
	}

	public PostProcessDataSet identifyMatchedFeaturesAndMergeHeaders(PostProcessDataSet batch1Data,
			PostProcessDataSet batch2Data, Boolean pis, Double massTol, Double rtTol, Double stretchFactor,
			Integer annealingTargetStep, Boolean useDerivedSampleIds) {

		if (batch1Data == null)
			return null;
		if (batch2Data == null)
			return null;

		batch1Data.setDataSetLabel("BATCH1");
		batch1Data.setPosMode(true);
		batch2Data.setDataSetLabel("BATCH2");
		batch2Data.setPosMode(false);

		PostProcessDataSet mergedData = mergeDataSetHeaders(batch1Data, batch2Data, true, massTol, rtTol);
		mergedData.identifyMatchedFeatures(annealingTargetStep, stretchFactor, massTol, rtTol, true);

		return mergedData;
	}

	public List<FeatureFromFile> mergeFeaturesFromColumnMap(Map<Integer, Integer> mergedColumnMap,
			List<FeatureFromFile> featureList2, List<FeatureFromFile> featureList1, String type1Label,
			String type2Label) {

		List<FeatureFromFile> mergedList = new ArrayList<FeatureFromFile>();

		int lastCol = -1;

		for (Integer destinationCol : mergedColumnMap.values())
			if (destinationCol > lastCol)
				lastCol = destinationCol;

		if (lastCol == -1 && featureList1.size() > 0)
			lastCol = featureList1.get(0).getAddedColValues().size() - 1;

		// This assumes a single intensity header set
		int lastFeatureIdx = -1;
		FeatureFromFile nextFeature = null;
		for (int i = 0; i < featureList1.size(); i++) {
			nextFeature = featureList1.get(i);
			FeatureFromFile addedFeature = new FeatureFromFile();
			addedFeature.initialize(nextFeature);
			addedFeature.transferMatchStatistics(nextFeature);

			addedFeature.getAddedColValues().clear();
			for (int j = 0; j < nextFeature.getAddedColValues().size(); j++)
				addedFeature.getAddedColValues().add(nextFeature.getAddedColValues().get(j));

			for (int j = nextFeature.getAddedColValues().size(); j < lastCol + 1; j++)
				addedFeature.getAddedColValues().add(null);

			addedFeature.setMergedIndex(nextFeature.getIndex());
			addedFeature.setFeatureType(type1Label);
			if (nextFeature.getIndex() > lastFeatureIdx)
				lastFeatureIdx = nextFeature.getIndex();
			mergedList.add(addedFeature);
		}

		for (FeatureFromFile featureToMerge : featureList2) {
			FeatureFromFile addedFeature = new FeatureFromFile();
			addedFeature.initialize(featureToMerge);
			addedFeature.transferMatchStatistics(featureToMerge);
			addedFeature.setFeatureType(type2Label);
			addedFeature.setMergedIndex(lastFeatureIdx + featureToMerge.getIndex());

			addedFeature.getAddedColValues().clear();
			for (int i = 0; i < lastCol + 1; i++)
				addedFeature.getAddedColValues().add(null);

			String valueToPlace = null;
			Integer destinationCol;
			for (Integer origin : mergedColumnMap.keySet()) {
				try {
					valueToPlace = featureToMerge.getAddedColValues().get(origin);
					destinationCol = mergedColumnMap.get(origin);
					addedFeature.getAddedColValues().set(destinationCol, valueToPlace);
				} catch (Exception e) {
					System.out.println("Error on origin " + origin);
				}
			}
			mergedList.add(addedFeature);
		}
		return mergedList;
	}
}
