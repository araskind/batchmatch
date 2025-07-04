////////////////////////////////////////////////////
// BatchFileListLoaderPanel.java
// Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.io.sheetreaders.MetabolomicsIntensityDataLoader;

//Select All
public abstract class BatchFileListLoaderPanel extends FileListLoaderPanel {

	private static final long serialVersionUID = 6082733604962176299L;
	private List<String> allIntensityHeaders = null;

	public BatchFileListLoaderPanel() {
		this("", null);
	}

	public BatchFileListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		super(title, sharedAnalysisSettings);
	}

	public BatchFileListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings,
			Boolean readFileOrder) {
		super(title, sharedAnalysisSettings, readFileOrder);
	}

	public List<String> grabIntensityHeaders() {

		if (allIntensityHeaders != null)
			return allIntensityHeaders;

		Map<Integer, File> batchFileMap = grabFreshBatchFileMap();
		MetabolomicsIntensityDataLoader headerLoader = new MetabolomicsIntensityDataLoader();
		allIntensityHeaders = headerLoader.preReadIntensityHeaders(batchFileMap);

		return allIntensityHeaders;
	}
}
