////////////////////////////////////////////////////
// BinnerDataUtils.java
// Written by Jan Wigginton, March 2018
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.utils.orig;

import java.util.ArrayList;
import java.util.List;

public class BatchMatchDataUtils {

	// Given RtList with marked outliers, screen for runs that indicate a rise in
	// the curve

	static Boolean chatty = false;

	public static List<Integer> getFilteredOutlierIndices(List<Double> allEntries, Double outlierThreshold,
			Integer proximityWindowSize, Double getOutlierFilteringBuddyCutoff) {

		return getFilteredOutlierIndices(allEntries, outlierThreshold, proximityWindowSize,
				getOutlierFilteringBuddyCutoff, null);
	}

	public static List<Integer> getFilteredOutlierIndices(List<Double> allEntries,

			Double outlierThreshold, Integer proximityWindowSize, Double getOutlierFilteringBuddyCutoff,
			List<Double> sourceXs) {

		ArrayList<Integer> outlierIndices = getOutlierIndices(allEntries, outlierThreshold);

		// TO DO : Need to work with X differences for this one.
		int nOut = outlierIndices.size();
		if (nOut == 0) {
			// System.out.println("No outliers identified.");
			return outlierIndices;
		}

		if (nOut < 2 * proximityWindowSize + 1) {

			Double minDiff = Double.MAX_VALUE;
			for (int i = 1; i < outlierIndices.size(); i++) {
				Double diff = allEntries.get(outlierIndices.get(i)) - allEntries.get(outlierIndices.get(i - 1));
				if (Math.abs(diff) < minDiff)
					minDiff = diff;
			}

			if (minDiff > getOutlierFilteringBuddyCutoff) {
				System.out.println("Filtering apparent outliers ");

				for (int i = 0; i < outlierIndices.size(); i++) {
					String val = String.format("%.4f", allEntries.get(outlierIndices.get(i)));
					System.out.println(outlierIndices.get(i) + " =>  " + val);
				}
				System.out.println("");
				return outlierIndices;
			}
		}

		List<Double> forwardDiffs = new ArrayList<Double>();
		List<Double> backwardDiffs = new ArrayList<Double>();

		// for candidate outliers need to determine if they are close to neighbors,
		// basic criterion is simple -- is it far from forward and backward neighbors
		for (int i = 0; i < outlierIndices.size() - 1; i++) {
			forwardDiffs
					.add(Math.abs(allEntries.get(outlierIndices.get(i)) - allEntries.get(outlierIndices.get(i + 1))));
		}
		forwardDiffs.add(null);
		backwardDiffs.add(null);
		for (int i = 1; i < outlierIndices.size(); i++) {
			backwardDiffs
					.add(Math.abs(allEntries.get(outlierIndices.get(i)) - allEntries.get(outlierIndices.get(i - 1))));
		}

		// minDiffAverag = min(avg spacing among pts going forward, avg spacing among
		// pts going backward)
		List<Double> minDiffAverages = new ArrayList<Double>();
		for (int j = 0; j < outlierIndices.size(); j++) {
			Double avg = 0.0;

			// int maxK = j > proximityWindowSize ? proximityWindowSize : j;
			int divisor = 0;
			for (int k = 1; k < proximityWindowSize; k++) {
				if ((j - k) <= 0)
					continue;
				avg += Math.abs(backwardDiffs.get(j - k));
				divisor++;
			}
			if (divisor > 0)
				avg /= (1.0 * divisor);

			minDiffAverages.add(avg);
			avg = 0.0;
			divisor = 0;
			for (int k = 1; k < proximityWindowSize; k++) {
				if (j + k >= forwardDiffs.size() - 1)
					continue;

				avg += Math.abs(forwardDiffs.get(j + k));
				divisor++;
			}
			if (divisor > 0)
				avg /= (1.0 * divisor);

			if (avg < minDiffAverages.get(j))
				minDiffAverages.set(j, avg);
		}

		ArrayList<Integer> filteredOutlierIndices = new ArrayList<Integer>();

		if (chatty) {
			System.out.println(
					"Numerous putative outliers have been identified. Screening candidates that are not well separated from other outlier points.");
			System.out.println("Calculating distances between neighboring points. Retain candidates that are less than "
					+ getOutlierFilteringBuddyCutoff + " minutes from neighbors\n");
		}
		// absolute difference between the point and it's nearest neighbors
		for (int w = 0; w < outlierIndices.size(); w++) {
			// no additional outliers in the neighborhood (either side)
			Boolean keepOutlier = true;

			if (w == 0) {
				if (forwardDiffs.get(w) != null)
					keepOutlier = forwardDiffs.get(w) > getOutlierFilteringBuddyCutoff;
			} else if (w == outlierIndices.size() - 1) {
				if (backwardDiffs.get(w) != null)
					keepOutlier = backwardDiffs.get(w) > getOutlierFilteringBuddyCutoff;
			} else
				keepOutlier = forwardDiffs.get(w) > getOutlierFilteringBuddyCutoff
						&& backwardDiffs.get(w) > getOutlierFilteringBuddyCutoff;

			Boolean printed = false;
			if (keepOutlier) {
				if (chatty)
					System.out.println("Keep classifying point "
							+ String.format("%.3f", allEntries.get(outlierIndices.get(w)))
							+ " as an outlier because it is well separated from left and right adjacent points. ");
				printed = true;
			}

			// or if it close on one side or the other, but there aren't a ton of clustered
			// points
			keepOutlier |= (minDiffAverages.get(w) > getOutlierFilteringBuddyCutoff);
			if (keepOutlier && !printed && chatty) {
				String val = String.format("%.5f", minDiffAverages.get(w));
				System.out.println("Although an adjacent point groups with it, keep classifying "
						+ String.format("%.3f", allEntries.get(outlierIndices.get(w)))
						+ " as an outlier because the immediate region has few other outliers.");
				System.out.println(
						"The average distance between the next " + proximityWindowSize + " candidate points is " + val);
			}

			if (!keepOutlier) {
				String val = String.format("%.5f", minDiffAverages.get(w));
				// System.out.println(val + " will remain in the lattice because it is not
				// clearly separated from adjacent, putative outliers.");
			}
			if (keepOutlier)
				filteredOutlierIndices.add(outlierIndices.get(w));
		}

		// for (int z = 0; z < filteredOutlierIndices.size(); z++)
		// System.out.print(filteredOutlierIndices.get(z) + ", ");
		if (chatty)
			System.out.println();

		return filteredOutlierIndices;
	}

	public static ArrayList<Integer> getOutlierIndices(List<Double> allEntries, Double outlierThreshold) {

		double mean = 0.0, sqTot = 0.0, stDev;
		int nNonMissing = 0;

		for (Double val : allEntries) {
			if (val == null || val < 0.0)
				continue;
			mean += val;
			sqTot += (val * val);
			nNonMissing++;
		}
		mean /= nNonMissing;
		stDev = Math.sqrt(sqTot / (nNonMissing - 1) - mean * mean);

		ArrayList<Integer> outlierIndices = new ArrayList<Integer>();
		for (int i = 0; i < allEntries.size(); i++) {
			Double val = allEntries.get(i);
			if (val == null) // || val < 0.0)
				continue;

			Double normalized = Math.abs((val - mean) / stDev);
			if (Math.abs(normalized) > outlierThreshold)
				outlierIndices.add(i);
		}
		return outlierIndices;
	}

	private static Integer getIdxPairIdxDiff(int i, int j, List<Integer> outlierIndices) {
		if (i < 0 || j < 0)
			return null;
		Integer firstIdx = outlierIndices.get(i);
		Integer secondIdx = outlierIndices.get(j);
		return firstIdx - secondIdx;
	}
}

/*
 * for (int i = 1; i < outlierIndices.size() - 1; i++) { Integer backDiff = i >
 * 0 ? getIdxPairIdxDiff(i, i-1, outlierIndices) : null; Integer forwardDiffs =
 * i < outlierIndices.size() - 1 ? getIdxPairIdxDiff(i+1, i, outlierIndices) :
 * null;
 * 
 * // keep calling it an outlier if the outlier point is isolated if ( backDiff
 * > keepWindowSize && forwardDiff > keepWindowSize) {
 * filteredOutlierIndices.add(outlierIndices.get(i)); continue; } // otherwise,
 * check for outliers points in immediate neighborhood int startIdx =
 * Math.max(0, i - keepWindowSize); int endIdx = Math.min(outlierIndices.size(),
 * i + keepWindowSize); int nPtsKeep = 0, nPtsPolled = endIdx - startIdx;
 * 
 * for (int j = startIdx; j < i-1; j++) { Integer pairDiff =
 * getIdxPairIdxDiff(i, j, outlierIndices); Boolean keepPoint =
 * Math.abs(pairDiff) < keepWindowSize; nPtsKeep += (keepPoint ? 1 : 0); }
 * 
 * for (int j = i+1; j < endIdx; j++) { Integer pairDiff = getIdxPairIdxDiff(j,
 * i, outlierIndices); Boolean keepPoint = Math.abs(pairDiff) < keepWindowSize;
 * nPtsKeep += (keepPoint ? 1 : 0); }
 * 
 * if ((1.0 * nPtsKeep)/(1.0 * nPtsPolled) < keepWindowPct)
 * filteredOutlierIndices.add(outlierIndices.get(i)); }
 * 
 * filteredOutlierIndices.add(outlierIndices.get(outlierIndices.size() - 1));
 */
//}
