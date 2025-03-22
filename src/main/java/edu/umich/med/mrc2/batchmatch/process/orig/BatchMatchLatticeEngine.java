package edu.umich.med.mrc2.batchmatch.process.orig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.orig.AnchorMap;
import edu.umich.med.mrc2.batchmatch.data.orig.RtPair;

public class BatchMatchLatticeEngine {

	public BatchMatchLatticeEngine() {

	}

	public Map<Integer, AnchorMap> createBlankMaps(PostProcessDataSet data) {

		List<Integer> indices = data.getSortedUniqueBatchIndices();
		Map<Integer, AnchorMap> anchorMapMap = new HashMap<Integer, AnchorMap>();

		for (int i = 0; i < indices.size(); i++) {

			AnchorMap blankMap = new AnchorMap();
			blankMap.addRTPair(0.0, 0.0);
			blankMap.addRTPair(100.0, 100.0);
			anchorMapMap.put(indices.get(i), blankMap);
		}

		return anchorMapMap;
	}

	public void addLatticeTail(AnchorMap mapWithoutTail) {

		Double maxX = Double.MIN_VALUE;
		Double yForMaxX = null;

		for (RtPair pr : mapWithoutTail.getAsRtPairs()) {
			if (pr.getRt1() > maxX) {
				maxX = pr.getRt1();
				yForMaxX = pr.getRt2();
			}
		}

		Double baseXVal = maxX;
		Double baseYVal = yForMaxX;

		while (baseXVal < 18.8) {

			for (int i = 0; i < 5; i++) {
				baseXVal += .0001;
				baseYVal += .04;
				mapWithoutTail.addRTPair(baseXVal, baseYVal);
			}

			baseYVal = yForMaxX;

			for (int i = 0; i < 5; i++) {
				baseXVal += .0001;
				baseYVal -= .04;
				mapWithoutTail.addRTPair(baseXVal, baseYVal);
			}

			baseXVal += .2;
		}
	}

	public void addLatticeTail2(AnchorMap mapWithoutTail) {

		mapWithoutTail.updateForEnds();
		Double maxX = Double.MIN_VALUE;
		Double yForMaxX = null;

		for (RtPair pr : mapWithoutTail.getAsRtPairs()) {
			if (pr.getRt1() > maxX) {
				maxX = pr.getRt1();
				yForMaxX = pr.getRt2();
			}
		}

		Double baseXVal = maxX;
		Double baseYVal = yForMaxX;

		while (baseXVal < 18.8) {

			for (int i = 0; i < 5; i++) {
				baseXVal += .0001;
				baseYVal += .04;
				mapWithoutTail.addRTPair(baseXVal, baseYVal);
			}

			baseYVal = yForMaxX;

			for (int i = 0; i < 5; i++) {
				baseXVal += .0001;
				baseYVal -= .04;
				mapWithoutTail.addRTPair(baseXVal, baseYVal);
			}

			baseXVal += .2;
		}
	}
}
