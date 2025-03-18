package edu.umich.mrc2.batchmatch.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.MassRangeGroup;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByMassComparator;

public class BatchMatchResults {

	private Double breakGap = 0.006;

	public BatchMatchResults(PostProcessDataSet data) {
		characterize(data);
	}

	public void characterize(PostProcessDataSet data) {
		Collections.sort(data.getFeatures(), new FeatureByMassComparator());

		List<MassRangeGroup> massGroups = new ArrayList<MassRangeGroup>();

		List<FeatureFromFile> features = data.getFeatures();
		if (features.size() < 1)
			return;

		Double currMass = null;
		Double lastMass = features.get(0).getMass();

		MassRangeGroup currGroup = new MassRangeGroup();
		currGroup.addRawFeature(features.get(0));

		for (int i = 1; i < features.size(); i++) {
			currMass = features.get(i).getMass();

			if (Math.abs(lastMass - currMass) > breakGap) {

				currGroup.organize();
				massGroups.add(currGroup);
				currGroup = new MassRangeGroup();
			}

			currGroup.addRawFeature(features.get(i));
			lastMass = currMass;
		}
		massGroups.add(currGroup);
	}

	public Double getBreakGap() {
		return breakGap;
	}

	public void setBreakGap(Double breakGap) {
		this.breakGap = breakGap;
	}

}
