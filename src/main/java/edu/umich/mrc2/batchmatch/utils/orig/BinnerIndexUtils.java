package edu.umich.mrc2.batchmatch.utils.orig;

import edu.umich.mrc2.batchmatch.data.orig.Feature;

public class BinnerIndexUtils {
	public static int getFeatureIndexFromRTSortedFullBin(int binIndex, int offsetWithinBin) {
		return 1;
		// return
		// AnalysisDialog.getAnalysisData().getBinContents().get(binIndex).get(offsetWithinBin);
	}

	public static Feature getFeatureFromRTSortedFullBin(int binIndex, int offsetWithinBin) {
		return null;
		// return
		// AnalysisDialog.getAnalysisData().getNonMissingFeaturesInOriginalOrder().
		// get(AnalysisDialog.getAnalysisData().getBinContents().get(binIndex).get(offsetWithinBin));
	}

	public static int getFeatureIndexFromClusterSortedDeisotopedBin(int binIndex, int offsetWithinBin) {
		return 1;
		// return
		// AnalysisDialog.getAnalysisData().getBinwiseFeaturesForClustering().get(binIndex).
		// get(AnalysisDialog.getAnalysisData().getIndexedClustersForRebinning().get(offsetWithinBin).getIndex());
	}

	public static Feature getFeatureFromClusterSortedDeisotopedBin(int binIndex, int offsetWithinBin) {
		return null;
		// return
		// AnalysisDialog.getAnalysisData().getNonMissingFeaturesInOriginalOrder().
		// get(AnalysisDialog.getAnalysisData().getBinwiseFeaturesForClustering().get(binIndex).get(offsetWithinBin));
	}

	public static double getFeatureCorrelationsFromFeatureIndex(int featureIndex) {
		return 1.0;
		// Feature feature =
		// AnalysisDialog.getAnalysisData().getNonMissingFeaturesInOriginalOrder().get(featureIndex);
		// return
		// AnalysisDialog.getAnalysisData().getBinwiseCorrelations().get(feature.getBinIndex()).
		// getRow(feature.getOffsetWithinBin());
	}

	public static double getFeatureCorrelationsFromFeature(Feature feature) {
		return 1.0;
		// return
		// AnalysisDialog.getAnalysisData().getBinwiseCorrelations().get(feature.getBinIndex()).
		// getRow(feature.getOffsetWithinBin());
	}

	public static double getFeatureMassDiffsFromFeature(Feature feature) {
		return 1.0;
//		return AnalysisDialog.getAnalysisData().getBinwiseMassDiffs().get(feature.getBinIndex()).
//				getRow(feature.getOffsetWithinBin());
	}

	public static int getFeatureMassDiffClassesFromFeature(Feature feature) {
		return 1;
		// return
		// AnalysisDialog.getAnalysisData().getBinwiseMassDiffClasses().get(feature.getBinIndex())
		// [feature.getOffsetWithinBin()];
	}
}
