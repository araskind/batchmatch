////////////////////////////////////////////////////////////
// BatchMatchSummaryInfo.java
// Written by Jan Wigginton and Bill Duren, November 2020
////////////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.process.orig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchMatchSummaryInfo {

	private String mergeFilePath = "";
	private String rawDataFileListPath = "";
	private Map<Integer, File> batchFileMap = new HashMap<Integer, File>();

	private String titleWithVersion = "";
	private Integer nSamples = null;
	private Integer nControls = null;
	private Integer nBatches = null;
	private Integer nTotalRawFeatures = null;
	private Integer nTotalFilteredFeatures = null;
	private Integer nTotalCollapsedFeatures = null;

	private List<Integer> nRawFeaturesByBatch = new ArrayList<Integer>();
	private List<Integer> nFilteredFeaturesByBatch = new ArrayList<Integer>();
	private List<Integer> nBatchSamples = new ArrayList<Integer>();
	private List<String> sampleNames = new ArrayList<String>();

	public BatchMatchSummaryInfo() {
		nBatches = 10;
		for (int i = 0; i < 10; i++) {
			nFilteredFeaturesByBatch.add(1000);
			nBatchSamples.add(50);
		}
	}

	public String getMergeFilePath() {
		return mergeFilePath;
	}

	public void setMergeFilePath(String mergeFilePath) {
		this.mergeFilePath = mergeFilePath;
	}

	public String getRawDataFileListPath() {
		return rawDataFileListPath;
	}

	public void setRawDataFileListPath(String rawDataFileListPath) {
		this.rawDataFileListPath = rawDataFileListPath;
	}

	public String getTitleWithVersion() {
		return titleWithVersion;
	}

	public void setTitleWithVersion(String titleWithVersion) {
		this.titleWithVersion = titleWithVersion;
	}

	public void setSampleNames(List<String> names) {
		sampleNames = new ArrayList<String>();

		for (int i = 0; i < names.size(); i++) {
			sampleNames.add(names.get(i));
		}
	}

	public void initializeNBatches(int nBatches) {
		
		this.nBatches = nBatches;
		this.nFilteredFeaturesByBatch = new ArrayList<Integer>();
		this.nBatchSamples = new ArrayList<Integer>();

		for (int i = 0; i < nBatches; i++) {
			nFilteredFeaturesByBatch.add(0);
			nBatchSamples.add(0);
		}
	}

	public void setNBatchSamples(List<Integer> counts) {
		this.nBatchSamples = new ArrayList<Integer>();

		for (int i = 0; i < counts.size(); i++) {
			nBatchSamples.add(counts.get(i));
		}
	}

	public void setNFilteredFeaturesByBatch(List<Integer> counts) {
		this.nFilteredFeaturesByBatch = new ArrayList<Integer>();

		for (int i = 0; i < counts.size(); i++) {
			nFilteredFeaturesByBatch.add(counts.get(i));
		}
	}

	public List<String> getSampleNames() {
		return this.sampleNames;
	}

	public Integer getnSamples() {
		return nSamples;
	}

	public void setnSamples(Integer nSamples) {
		this.nSamples = nSamples;
	}

	public Integer getnBatches() {
		return nBatches;
	}

	public void setnBatches(Integer nBatches) {
		this.nBatches = nBatches;
	}

	public void setnTotalRawFeatures(Integer nTotalRawFeatures) {
		this.nTotalRawFeatures = nTotalRawFeatures;
	}

	public Integer getnTotalFilteredFeatures() {
		return nTotalFilteredFeatures;
	}

	public void setnTotalFilteredFeatures(Integer nTotalFilteredFeatures) {
		this.nTotalFilteredFeatures = nTotalFilteredFeatures;
	}

	public List<Integer> getnFilteredFeaturesByBatch() {
		return nFilteredFeaturesByBatch;
	}

	public List<Integer> getnBatchSamples() {
		return nBatchSamples;
	}

	public Map<Integer, File> getBatchFileMap() {
		return batchFileMap;
	}

	public void setBatchFileMap(Map<Integer, File> batchFileMap) {
		this.batchFileMap = batchFileMap;
	}

	public List<Integer> getnRawFeaturesByBatch() {
		return nRawFeaturesByBatch;
	}

	public void setnRawFeaturesByBatch(List<Integer> nRawFeaturesByBatch) {
		this.nRawFeaturesByBatch = nRawFeaturesByBatch;
	}

	public Integer getNTotalRawFeatures() {
		if (this.nRawFeaturesByBatch == null)
			return 0;

		Integer nRawFeatures = 0;

		for (Integer value : this.nRawFeaturesByBatch)
			nRawFeatures += value;

		return nRawFeatures;
	}
}
