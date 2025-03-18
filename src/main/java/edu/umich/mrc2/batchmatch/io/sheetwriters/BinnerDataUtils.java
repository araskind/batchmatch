////////////////////////////////////////////////////
// BinnerDataUtils.java
// Written by Jan Wigginton, March 2018
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.util.ArrayList;
import java.util.List;

public class BinnerDataUtils {
	public static List<Integer> getOutlierIndices(List<Double> allEntries, Double outlierThreshold) {
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
			if (val == null || val < 0.0)
				continue;

			Double normalized = Math.abs((val - mean) / stDev);
			if (Math.abs(normalized) > outlierThreshold)
				outlierIndices.add(i);
		}

		return outlierIndices;
	}
}
