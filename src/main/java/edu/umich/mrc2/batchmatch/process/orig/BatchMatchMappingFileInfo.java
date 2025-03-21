
package edu.umich.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.data.comparators.orig.FeatureMatchByNameStubAndMassComparator;
import edu.umich.mrc2.batchmatch.data.orig.FeatureMatch;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;

// Data structure to organize and access feature mappings by various characteristics (batch, etc)
// Useful as a function argument to avoid passing multiple hashmaps, arrays, etc.
public class BatchMatchMappingFileInfo {

	private List<FeatureMatch> nonMissingCompoundMappings = null;
	private List<FeatureMatch> missingCompoundMappings = null;
	private Map<Integer, List<FeatureMatch>> compoundMappingsByBatchMap = null;

	public BatchMatchMappingFileInfo() {
		nonMissingCompoundMappings = null;
		missingCompoundMappings = null;
		compoundMappingsByBatchMap = null;
		// FeatureMatch f;
	}

	public void initializeFromAllMappings(List<FeatureMatch> allFeatureMappings) {
		nonMissingCompoundMappings = new ArrayList<FeatureMatch>();
		missingCompoundMappings = new ArrayList<FeatureMatch>();

		for (FeatureMatch f : allFeatureMappings) {
			if (f.getIsUnmapped())
				missingCompoundMappings.add(f);
			else
				nonMissingCompoundMappings.add(f);
		}
		buildAllMappingsByBatchMap();
	}

	private Map<Integer, List<FeatureMatch>> buildAllMappingsByBatchMap() {
		if (this.nonMissingCompoundMappings == null)
			return null;

		Integer batch = null;
		compoundMappingsByBatchMap = new HashMap<Integer, List<FeatureMatch>>();
		for (FeatureMatch f : nonMissingCompoundMappings) {
			batch = f.getBatch();
			if (!compoundMappingsByBatchMap.containsKey(batch))
				compoundMappingsByBatchMap.put(batch, new ArrayList<FeatureMatch>());

			compoundMappingsByBatchMap.get(batch).add(f);
		}

		for (FeatureMatch f : missingCompoundMappings) {

			batch = f.getBatch();
			if (!compoundMappingsByBatchMap.containsKey(batch))
				compoundMappingsByBatchMap.put(batch, new ArrayList<FeatureMatch>());

			compoundMappingsByBatchMap.get(batch).add(f);
		}
		return compoundMappingsByBatchMap;
	}

	public List<FeatureMatch> getAllMappings() {
		return getAllMappings(true);
	}

	public List<FeatureMatch> getAllMappings(Boolean sort) {

		List<FeatureMatch> allMappings = new ArrayList<FeatureMatch>();

		for (FeatureMatch f : this.nonMissingCompoundMappings)
			allMappings.add(f);

		for (FeatureMatch f : this.missingCompoundMappings)
			allMappings.add(f);

		if (sort)
			Collections.sort(allMappings, new FeatureMatchByNameStubAndMassComparator());

		return allMappings;
	}

	public List<FeatureMatch> getNonMissingCompoundMappings() {

		return nonMissingCompoundMappings;
	}

	public List<FeatureMatch> getMissingCompoundMappings() {

		return missingCompoundMappings;
	}

	public Map<Integer, List<FeatureMatch>> grabAllFeaturesByBatchMap() {

		if (this.compoundMappingsByBatchMap == null)
			this.buildAllMappingsByBatchMap();

		return compoundMappingsByBatchMap;
	}

	public Map<String, List<Integer>> grabAllBatchesByFeatureMap() {

		grabAllFeaturesByBatchMap();

		Map<String, List<Integer>> allBatchesByFeatureMap = new HashMap<String, List<Integer>>();

		for (Integer batch : compoundMappingsByBatchMap.keySet()) {
			List<FeatureMatch> mappingsForBatch = compoundMappingsByBatchMap.get(batch);

			for (FeatureMatch mapping : mappingsForBatch) {
				if (!allBatchesByFeatureMap.containsKey(mapping.getNameStub()))
					allBatchesByFeatureMap.put(mapping.getNameStub(), new ArrayList<Integer>());
				allBatchesByFeatureMap.get(mapping.getNameStub()).add(mapping.getBatch());
			}
		}

		return allBatchesByFeatureMap;
	}

	public Map<String, List<FeatureMatch>> buildAllMappingsByCompoundNameMap() {

		grabAllFeaturesByBatchMap();

		Map<String, List<FeatureMatch>> allMappingsByCompoundNameMap = new HashMap<String, List<FeatureMatch>>();

		for (Integer batch : compoundMappingsByBatchMap.keySet()) {
			List<FeatureMatch> matchesForBatch = compoundMappingsByBatchMap.get(batch);

			for (FeatureMatch match : matchesForBatch) {
				if (!allMappingsByCompoundNameMap.containsKey(match.getNameStub()))
					allMappingsByCompoundNameMap.put(match.getNameStub(), new ArrayList<FeatureMatch>());

				allMappingsByCompoundNameMap.get(match.getNameStub()).add(match);
			}
		}
		return allMappingsByCompoundNameMap;
	}

	public Map<String, Double> getMaxAvgCorrelationsByCompoundName() {

		Map<String, List<FeatureMatch>> mappingsByCompoundNameMap = buildAllMappingsByCompoundNameMap();

		Map<String, Double> maxCorrByCompoundNameMap = new HashMap<String, Double>();

		for (String name : mappingsByCompoundNameMap.keySet()) {

			List<FeatureMatch> matchesForCompound = mappingsByCompoundNameMap.get(name);
			Map<Integer, Double> maxCorrByBatch = new HashMap<Integer, Double>();

			for (FeatureMatch f : matchesForCompound) {
				Integer batch = f.getBatch();

				if (f.getCorr() == null || f.getCorr().isNaN())
					continue;

				if (f.getCorr().equals(BatchMatchConstants.NAMED_ALL_MISSING))
					continue;

				if (f.getCorr().equals(BatchMatchConstants.UNNAMED_ALL_MISSING))
					continue;

				if (!maxCorrByBatch.containsKey(batch))
					maxCorrByBatch.put(batch, Double.MIN_VALUE);

				if (f.getCorr() > maxCorrByBatch.get(batch))
					maxCorrByBatch.put(batch, f.getCorr());
			}

			Double maxAvgCorrForCompound = 0.0;
			for (Double corr : maxCorrByBatch.values()) {
				if (corr == null || corr.isNaN()) {
					maxAvgCorrForCompound = null;
					break;
				}
				maxAvgCorrForCompound += corr;
			}

			if (maxAvgCorrForCompound != null)
				maxAvgCorrForCompound /= (1.0 * maxCorrByBatch.size());

			maxCorrByCompoundNameMap.put(name, maxAvgCorrForCompound);
		}

		return maxCorrByCompoundNameMap;
	}

	public Map<String, Double> getCompoundsWithoutSignificantCorrSet(Double sigCutoff) {

		Map<String, Double> notSignificantMaxCorrsByCompoundName = new HashMap<String, Double>();
		Map<String, Double> maxCorrByCompoundNameMap = getMaxAvgCorrelationsByCompoundName();

		for (String compound : maxCorrByCompoundNameMap.keySet()) {
			if (maxCorrByCompoundNameMap.get(compound) != null && maxCorrByCompoundNameMap.get(compound) < sigCutoff)
				notSignificantMaxCorrsByCompoundName.put(compound, maxCorrByCompoundNameMap.get(compound));
		}
		return notSignificantMaxCorrsByCompoundName;
	}

	public Map<String, List<FeatureMatch>> getUnmappedCompoundsMap() {

		Map<String, List<FeatureMatch>> unmappedByCompoundNameMap = new HashMap<String, List<FeatureMatch>>();
		for (FeatureMatch match : missingCompoundMappings) {

			if (match.getUnnamedFeature().startsWith("UNK"))
				continue;
			// if (!match.getUnnamedFeature().startsWith("UNK")) {
			System.out.println("Not missing " + match.getUnnamedFeature() + match.getNamedFeature());
			// continue;
			// }

			if (!unmappedByCompoundNameMap.containsKey(match.getNameStub()))
				unmappedByCompoundNameMap.put(match.getNameStub(), new ArrayList<FeatureMatch>());

			unmappedByCompoundNameMap.get(match.getNameStub()).add(match);
		}
		return unmappedByCompoundNameMap;
	}
}
