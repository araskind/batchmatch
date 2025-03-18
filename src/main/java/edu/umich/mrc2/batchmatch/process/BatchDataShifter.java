////////////////////////////////////////////////////
//BatchDataShifter.java
//Written by Jan Wigginton August 2019
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.AnchorMap;
import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.RtPair;

public class BatchDataShifter {

	public BatchDataShifter() {
	}

	public PostProcessDataSet shiftSet(PostProcessDataSet data, AnchorMap anchorMap) {

		PostProcessDataSet shiftedData = data.makeDeepCopy();

		Map<String, Double> featureNameToOldRtMap = new HashMap<String, Double>();

		for (FeatureFromFile feature : shiftedData.getFeatures()) {
			Double rtOld = feature.getRT();
			Double rtNew = calculateRTShift(rtOld, anchorMap);
			featureNameToOldRtMap.put(feature.getName(), rtOld);
			feature.setRT(rtNew);
			feature.setOldRt(rtOld);
		}
		shiftedData.setFeatureNameToOldRtMap(featureNameToOldRtMap);
		return shiftedData;
	}

	private Double calculateRTShift(Double rtOld, AnchorMap anchorMap) {
		double rtOldLower, rtOldUpper;
		double rtNewLower, rtNewUpper;
		double oldSpan, newSpan, pctChange;
		Double rtNew = null;

		ArrayList<RtPair> rtPairs = (ArrayList<RtPair>) anchorMap.getAsRtPairs();

		RtPair pr = null, prNext = null;
		RtPair prPrev = rtPairs.size() > 0 ? rtPairs.get(0) : null;

		for (int i = 0; i < rtPairs.size(); i++) {

			pr = rtPairs.get(i);
			if (i < rtPairs.size() - 1) {
				prNext = rtPairs.get(i + 1);
				if (pr.getRt1().equals(prNext.getRt1()))
					continue;
			}
			if (rtOld > pr.getRt1()) {
				prPrev = rtPairs.get(i);
				continue;
			}

			rtOldLower = prPrev.getRt1();
			rtOldUpper = pr.getRt1();
			oldSpan = rtOldUpper - rtOldLower;

			rtNewLower = prPrev.getRt2();
			rtNewUpper = pr.getRt2();
			newSpan = rtNewUpper - rtNewLower;

			pctChange = Math.abs(oldSpan) > 0 ? (rtOld - rtOldLower) / oldSpan : 0.0;
			rtNew = rtNewLower + pctChange * newSpan;

			break;
		}
		return rtNew;
	}
}
