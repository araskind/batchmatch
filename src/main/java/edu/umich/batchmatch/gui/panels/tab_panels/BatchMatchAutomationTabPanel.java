////////////////////////////////////////////////////
// BatchMatchAutomationTabPanel.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////
package edu.umich.batchmatch.gui.panels.tab_panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.data.AnchorMap;
import edu.umich.batchmatch.data.FeatureFromFile;
import edu.umich.batchmatch.data.FeatureMatch;
import edu.umich.batchmatch.data.PostProcessDataSet;
import edu.umich.batchmatch.data.RtPair;
import edu.umich.batchmatch.data.SharedAnalysisSettings;
import edu.umich.batchmatch.data.comparators.RtPairComparator;
import edu.umich.batchmatch.gui.panels.AbstractStickyFileLocationPanel;
import edu.umich.batchmatch.gui.panels.AnchorLoaderPanel;
import edu.umich.batchmatch.gui.panels.BatchFileListLoaderPanel;
import edu.umich.batchmatch.gui.panels.BinnerBatchFileLoaderPanel;
import edu.umich.batchmatch.gui.panels.FeatureMappingLoaderPanel;
import edu.umich.batchmatch.gui.panels.IntegerPickerPanel;
import edu.umich.batchmatch.gui.panels.MultipleIntegerPickerPanel;
import edu.umich.batchmatch.gui.panels.MultipleParametersPanel;
import edu.umich.batchmatch.gui.panels.OutputFileNameTagPanel;
import edu.umich.batchmatch.gui.panels.StickySettingsPanel;
import edu.umich.batchmatch.io.sheetwriters.AnchorFileWriter;
import edu.umich.batchmatch.io.sheetwriters.BatchMatchDataSetSummaryCSVWriter;
import edu.umich.batchmatch.io.sheetwriters.BatchMatchExcelOutputContainer;
import edu.umich.batchmatch.io.sheetwriters.BatchMatchExpandedFeatureCSVWriter;
import edu.umich.batchmatch.io.sheetwriters.BatchMatchNamedResultsWriter;
import edu.umich.batchmatch.io.sheetwriters.RecursiveLatticeFileWriter;
import edu.umich.batchmatch.main.BatchMatchConstants;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.process.BatchDataMerger;
import edu.umich.batchmatch.process.BatchDataShifter;
import edu.umich.batchmatch.process.BatchMatchMappingFileInfo;
import edu.umich.batchmatch.process.BatchNamedDataIntegrator;
import edu.umich.batchmatch.process.CompoundMatchDisambiguationEngine;
import edu.umich.batchmatch.process.LatticeMixer;
import edu.umich.batchmatch.utils.ListUtils;
import edu.umich.batchmatch.utils.PostProcessUtils;
import edu.umich.batchmatch.utils.StringUtils;

public class BatchMatchAutomationTabPanel extends StickySettingsPanel {

	private Boolean chatty = false;

	private static final long serialVersionUID = 4694936443709269167L;

	private AnchorLoaderPanel anchorLoaderPanel;
	private FeatureMappingLoaderPanel namedUnnamedMappingPanel;
	private AbstractStickyFileLocationPanel outputDirectoryPanel;
	private BinnerBatchFileLoaderPanel binnerResultLoaderPanel;
	private BatchFileListLoaderPanel batchFileListLoaderPanel, anchorFileListLoaderPanel;
	private OutputFileNameTagPanel outputFileNameTagPanel;
	private SharedAnalysisSettings sharedAnalysisSettings;
	private IntegerPickerPanel annealTargetStepPanel;
	private MultipleIntegerPickerPanel backtrackOptionPanel, runProgressPanel;
	private MultipleParametersPanel outlierFilteringPars, matchTolerancesPanel;
	private JPanel batchShiftingWrapPanel, runConvertWrapPanel, progPanel;
	private JProgressBar progBar;

	private JCheckBox anchorColumnSecond;

	private String reportFileName;
	private Integer breakIncrement = 8;

	private String originalLatticeSpace = null, temporaryLatticeSpace;

	private Map<String, List<RtPair>> filteredPairs;
	private List<String> temporaryLatticeFiles;
	private Map<Integer, String> tempLatticeFilesToKeep;
	private List<Integer> stepWiseUnambiguous, stepWiseAmbiguous, stepWiseBestAt1, stepWiseBestAt2;

	public BatchMatchAutomationTabPanel(SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		setSharedAnalysisSettings(sharedAnalysisSettings);
		initializeStickySettings("batchMatchConvertAndMergeTab", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		temporaryLatticeSpace = PostProcessUtils.getTempDirectoryName();

		outputDirectoryPanel = new AbstractStickyFileLocationPanel("Specify Report Output Directory ",
				"batchmatch_reports.directory") {

			@Override
			protected void updateInterfaceForNewSelection() {
				binnerResultLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectoryPath());
				batchFileListLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectoryPath());
				namedUnnamedMappingPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectoryPath());// .setInitialDirectory(
			}
		};
		outputDirectoryPanel.setupPanel();

		binnerResultLoaderPanel = new BinnerBatchFileLoaderPanel("Select Single Batch File To RT Convert") {

			private static final long serialVersionUID = 6166721792628584925L;

			@Override
			protected Boolean updateInterfaceForNewFileSelection(String fileName) {
				return true;
			}
		};
		binnerResultLoaderPanel.setupPanel();
		binnerResultLoaderPanel.setVisible(false); // !multiplicityPanel.useMultipleFormat());

		batchFileListLoaderPanel = new BatchFileListLoaderPanel(
				"Specify File Containing List of Batch Files to Convert and Merge (With Path)", null, true) {

			@Override
			protected void updateInterfaceForNewData() {
				Map<Integer, String> batchFileMap = batchFileListLoaderPanel.grabBatchFileMap();
			}
		};
		batchFileListLoaderPanel.setupPanel(outputDirectoryPanel.getOutputDirectoryPath());
		batchFileListLoaderPanel.setOpeningMessage(BatchMatchConstants.LINE_SEPARATOR + "Batch files"
				+ BatchMatchConstants.LINE_SEPARATOR + "=======================================");

		anchorFileListLoaderPanel = new BatchFileListLoaderPanel(
				"Specify File Containing List of Anchor File Names (With Path)", null) {

			@Override
			protected void updateInterfaceForNewData() {
				Map<Integer, String> batchFileMap = anchorFileListLoaderPanel.grabBatchFileMap();
			}
		};
		anchorFileListLoaderPanel.setupPanel(outputDirectoryPanel.getOutputDirectoryPath());
		anchorFileListLoaderPanel.setVisible(true);
		anchorFileListLoaderPanel.setOpeningMessage(
				"Anchor files" + BatchMatchConstants.LINE_SEPARATOR + "=======================================");

		anchorLoaderPanel = new AnchorLoaderPanel("Select Single Anchor File", sharedAnalysisSettings);
		anchorLoaderPanel.setupPanel();
		anchorLoaderPanel.setVisible(false); // !multiplicityPanel.useMultipleFormat());

		matchTolerancesPanel = new MultipleParametersPanel("matchTolerancesPar") {
		};
		matchTolerancesPanel.setupPanel("Feature Alignment Criteria", "Mass Tolerance", "RT Tolerance",
				"Annealing Stretch Factor");
		matchTolerancesPanel.setEnabled(false);

		namedUnnamedMappingPanel = new FeatureMappingLoaderPanel("Specify a Feature Mapping File", null);
		namedUnnamedMappingPanel.setupPanel(outputDirectoryPanel.getOutputDirectoryPath());

		annealTargetStepPanel = new IntegerPickerPanel("annealTargetStep") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {

				return true;
			}
		};
		annealTargetStepPanel.setupPanel("Batch Block Size for Annealing", "Annealing Runs on Batch Block Size");

		backtrackOptionPanel = new MultipleIntegerPickerPanel("backtrackOptions") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {
				return true;
			}
		};
		backtrackOptionPanel.setupPanel("BackTracking Options", "Backtrack All Deserts Longer Than",
				"Calculate Running RT Avg Over Window Size ", "Maximum Backtrack Iterations",
				"Minimum # Corrections to Trigger Backtrack");

		runProgressPanel = new MultipleIntegerPickerPanel("runProgressPanel") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {
				return true;
			}
		};
		runProgressPanel.setupPanel("Merge Status", "Number of Batches Merged (Current Iteration) ", "Iteration",
				"Number of Match Groups at End of Previous Iteration:   Unambiguous", "Ambiguous");
		runProgressPanel.clearValues();
		runProgressPanel.setEnabled(false);

		outlierFilteringPars = new MultipleParametersPanel("runProgressPanel2") {
		};
		outlierFilteringPars.setupPanel("Outlier Filtering", "Maximum SD From Curve", "Minimum Separation",
				"Remove Pairs with RT Diff Greater Than", "Remove Pairs with RT Diff Less Than");

		outputFileNameTagPanel = new OutputFileNameTagPanel("Shift");
		outputFileNameTagPanel.setPanelTitle(true ? "Specify Tag As Prefix for Merge File" : "Specify Merge File Name");
		outputFileNameTagPanel.setupPanel();

		add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel = new JPanel();

		batchShiftingWrapPanel.setLayout(new BoxLayout(batchShiftingWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder overallPostProcessingWrapBorder = BorderFactory.createTitledBorder("Run BatchMatch Workflow");
		overallPostProcessingWrapBorder.setTitleFont(boldFontForTitlePanel(overallPostProcessingWrapBorder, false));
		overallPostProcessingWrapBorder.setTitleColor(PostProccessConstants.TITLE_COLOR);
		batchShiftingWrapPanel.setBorder(overallPostProcessingWrapBorder);

		batchShiftingWrapPanel.add(outputDirectoryPanel);
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(binnerResultLoaderPanel);
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(anchorLoaderPanel);
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));

		// batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		// batchShiftingWrapPanel.add(namedUnnamedMappingPanel);

		batchShiftingWrapPanel.add(batchFileListLoaderPanel);
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(anchorFileListLoaderPanel);
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));

		// batchShiftingWrapPanel.add(namedUnnamedMappingPanel);
		// batchShiftingWrapPanel.add(Box.createVerticalStrut(1));

		batchShiftingWrapPanel.add(matchTolerancesPanel);

		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(annealTargetStepPanel);
		// batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		// batchShiftingWrapPanel.add(backtrackOptionPanel);
		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(outlierFilteringPars);

		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(outputFileNameTagPanel);

		batchShiftingWrapPanel.add(Box.createVerticalStrut(1));
		batchShiftingWrapPanel.add(runProgressPanel);

		setupRunConvertPanel();

		progPanel = new JPanel();
		progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.X_AXIS));
		progPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		progBar = new JProgressBar(0, 500);
		progBar.setIndeterminate(true);
		progPanel.add(progBar);
		progPanel.setVisible(false);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(1));
		add(batchShiftingWrapPanel);
		add(Box.createVerticalStrut(1));
		add(runConvertWrapPanel);
		add(Box.createVerticalStrut(1));
		add(progPanel);
		add(Box.createVerticalStrut(1));
	}

	private Boolean verifyInputFiles(Map<Integer, String> filesToConvertByBatchNoMap,
			Map<Integer, String> anchorFilesNamesByBatchNoMap, Map<Integer, Integer> fileOrderMap,
			Integer targetBatchNo) {

		List<Integer> missingFilesToConvertBatchList = new ArrayList<Integer>();

		for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
			if (!batchNo.equals(targetBatchNo))
				if (!anchorFilesNamesByBatchNoMap.containsKey(batchNo))
					missingFilesToConvertBatchList.add(batchNo);
		}
		if (missingFilesToConvertBatchList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++) {
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			}
			JOptionPane.showMessageDialog(null,
					"A file name for converted data was not specified for batch " + (sb.length() > 1 ? "es" : "")
							+ sb.toString() + PostProccessConstants.LINE_SEPARATOR
							+ PostProccessConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}

		for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
			if (!anchorFilesNamesByBatchNoMap.containsKey(batchNo))
				if (!batchNo.equals(targetBatchNo))
					missingFilesToConvertBatchList.add(batchNo);
		}

		if (missingFilesToConvertBatchList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++) {
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			}
			JOptionPane.showMessageDialog(null,
					"An anchor file name was not specified for batch " + (sb.length() > 1 ? "es" : "") + sb.toString()
							+ PostProccessConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}

		return true;
	}

	// AndMZ

	private Boolean mergeDataSetsSerially() throws Exception {
		try {
			temporaryLatticeFiles = new ArrayList<String>();
			tempLatticeFilesToKeep = new HashMap<Integer, String>();

			Map<Integer, String> filesToConvertByBatchNoMap = this.batchFileListLoaderPanel.grabBatchFileMap();
			Integer completeMergeSize = filesToConvertByBatchNoMap.size();

			stepWiseBestAt1 = new ArrayList<Integer>();
			stepWiseBestAt2 = new ArrayList<Integer>();
			stepWiseAmbiguous = new ArrayList<Integer>();
			stepWiseUnambiguous = new ArrayList<Integer>();

			PostProcessDataSet mergedData = null;
			for (int i = 2; i <= completeMergeSize; i++) {
				System.out.println("Setting up merge with " + (i) + " files");
				mergedData = runSerialMergeStepCodeClean(i, completeMergeSize.equals(i));
				// justUpdateForNamed(mergedData);
			}

			if (mergedData != null) {

				mergedData.setMaxPossibleMatchCt(completeMergeSize);

				// BatchMatchMatchGroupFilteringEngine.disambiguateGroups(mergedData);

				// checkForNamedDataAndReport(mergedData, completeMergeSize);
				cleanUpLatticeSpace();
				// %.3f
				CompoundMatchDisambiguationEngine.recommendAmbiguousFeaturesToRemove(mergedData, completeMergeSize);
				// gap size

				// justUpdateForNamed(mergedData);
				writeMergedData(mergedData);

				// BatchMatchMatchGroupFilteringEngine.disambiguateGroups(mergedData);

				for (int i = 0; i < stepWiseBestAt1.size(); i++) {
					Double OneARatio = (100.0 * stepWiseAmbiguous.get(i)) / (1.0 * stepWiseBestAt1.get(i));
					Double OneURatio = (100.0 * stepWiseUnambiguous.get(i)) / (1.0 * stepWiseBestAt1.get(i));
					Double TwoARatio = (100.0 * stepWiseAmbiguous.get(i)) / (1.0 * stepWiseBestAt2.get(i));
					Double TwoURatio = (100.0 * stepWiseUnambiguous.get(i)) / (1.0 * stepWiseBestAt2.get(i));

					String reportLine = String.format(
							"RT Window 1.0: %5d/%5d  = %5.2f%% Unambiguous,  %5d/%5d  = %5.2f%% Ambiguous,   RT Window 2.0: %5d/%5d  = %5.2f%% Unambiguous,  %5d/%5d  = %5.2f%% Ambiguous",
							stepWiseUnambiguous.get(i), stepWiseBestAt1.get(i), OneURatio, stepWiseAmbiguous.get(i),
							stepWiseBestAt1.get(i), OneARatio, stepWiseUnambiguous.get(i), stepWiseBestAt2.get(i),
							TwoURatio, stepWiseAmbiguous.get(i), stepWiseBestAt2.get(i), TwoARatio);

					System.out.println(reportLine);
				}
			}
			if (mergedData == null)
				throw new Exception("Error -- merged data is null");

			// checkForNamedDataAndReport(mergedData, completeMergeSize);
			// CompoundMatchDisambiguationEngine.recommendAmbiguousFeaturesToRemove(mergedData,
			// completeMergeSize);

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void justUpdateForNamed(PostProcessDataSet dataSet) {
		List<FeatureMatch> namedUnnamedFeatureMatches = namedUnnamedMappingPanel.grabNonMissingFeatureMapping();

		for (int i = 0; i < namedUnnamedFeatureMatches.size(); i++)
			System.out.println(namedUnnamedFeatureMatches.get(i));
		dataSet.updateNamesForBatchAndMZ();

		if (namedUnnamedFeatureMatches != null && namedUnnamedFeatureMatches.size() > 0) {
			BatchNamedDataIntegrator integrator = new BatchNamedDataIntegrator();
			dataSet = integrator.updateInfoTheOtherWay(dataSet, namedUnnamedFeatureMatches);
		}
	}

	private void checkForNamedDataAndReport(PostProcessDataSet dataSet, int completeMergeSize) {

		List<FeatureMatch> namedUnnamedFeatureMatches = namedUnnamedMappingPanel.grabNonMissingFeatureMapping();

		for (int i = 0; i < namedUnnamedFeatureMatches.size(); i++)
			System.out.println(namedUnnamedFeatureMatches.get(i));
		dataSet.updateNamesForBatchAndMZ();

		if (namedUnnamedFeatureMatches != null && namedUnnamedFeatureMatches.size() > 0) {
			BatchNamedDataIntegrator integrator = new BatchNamedDataIntegrator();
			dataSet = integrator.updateInfoTheOtherWay(dataSet, namedUnnamedFeatureMatches);
		}

		String fileNameTag = "";
		if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
			fileNameTag = outputFileNameTagPanel.getFileNameTag() + "_named.xlsx";

		String namedReportName = fileNameTag;
		if (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectoryPath()))
			namedReportName = outputDirectoryPanel.getOutputDirectoryPath() + PostProccessConstants.FILE_SEPARATOR
					+ fileNameTag;

		System.out.println("Report will be written to " + namedReportName == null ? "unknown_named" : namedReportName);
		BatchMatchNamedResultsWriter namedHandler = new BatchMatchNamedResultsWriter();
		namedHandler.setNamedResultFileName(namedReportName);

		try {
			BatchMatchMappingFileInfo mapInfo = namedUnnamedMappingPanel.getNamedUnnamedMapInfo();
			// BatchMatchSummaryInfo summaryInfo = initializeSummaryInfo(dataSet,
			// this.binnerFileListLoaderPanel.getBatchFileMap());
			namedHandler.reportOnTargetFeaturesToExcel(dataSet, null, completeMergeSize, mapInfo, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		fileNameTag = "";
		if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
			fileNameTag = outputFileNameTagPanel.getFileNameTag() + "_shifts.csv";

		String shiftReportName = fileNameTag;
		if (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectoryPath()))
			shiftReportName = outputDirectoryPanel.getOutputDirectoryPath() + PostProccessConstants.FILE_SEPARATOR
					+ fileNameTag;

		RecursiveLatticeFileWriter shiftWriter = new RecursiveLatticeFileWriter(completeMergeSize);
		shiftWriter.writeShiftMapForMerge(shiftReportName, dataSet);
	}
	/*
	 * private void checkForNamedDataAndReport(PostProcessDataSet dataSet, int
	 * completeMergeSize) {
	 * 
	 * List<FeatureMatch> namedUnnamedFeatureMatches =
	 * namedUnnamedMappingPanel.grabFeatureMapping(); dataSet.updateNamesForBatch();
	 * 
	 * if (namedUnnamedFeatureMatches != null && namedUnnamedFeatureMatches.size() >
	 * 0) { BatchNamedDataIntegrator integrator = new BatchNamedDataIntegrator();
	 * dataSet = integrator.updateInfoTheOtherWay(dataSet,
	 * namedUnnamedFeatureMatches); }
	 * 
	 * String fileNameTag = ""; if
	 * (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
	 * fileNameTag = outputFileNameTagPanel.getFileNameTag() + "_named.xlsx";
	 * 
	 * String namedReportName = fileNameTag; if
	 * (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectoryPath()))
	 * namedReportName = outputDirectoryPanel.getOutputDirectoryPath() +
	 * PostProccessConstants.FILE_SEPARATOR + fileNameTag;
	 * 
	 * System.out.println("Report will be written to " + namedReportName == null ?
	 * "unknown_named" : namedReportName); BatchMatchNamedResultsWriter namedHandler
	 * = new BatchMatchNamedResultsWriter();
	 * namedHandler.setNamedResultFileName(namedReportName); try {
	 * namedHandler.reportOnTargetFeaturesToExcel(dataSet, null); } catch (Exception
	 * e) { e.printStackTrace(); }
	 * 
	 * fileNameTag = ""; if
	 * (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
	 * fileNameTag = outputFileNameTagPanel.getFileNameTag() + "_shifts.csv";
	 * 
	 * String shiftReportName = fileNameTag; if
	 * (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectoryPath()))
	 * shiftReportName = outputDirectoryPanel.getOutputDirectoryPath() +
	 * PostProccessConstants.FILE_SEPARATOR + fileNameTag;
	 * 
	 * RecursiveLatticeFileWriter shiftWriter = new
	 * RecursiveLatticeFileWriter(completeMergeSize);
	 * shiftWriter.writeShiftMapForMerge(shiftReportName, dataSet); }
	 */

	private PostProcessDataSet runSerialMergeStepCodeClean(int nthMerge, Boolean finalMerge) throws Exception {

		Map<Integer, Integer> fileOrderMap = batchFileListLoaderPanel.grabFileOrderMap();
		Map<Integer, String> filesToConvertByBatchNoMap = this.batchFileListLoaderPanel
				.grabBatchFileMapSegment(nthMerge);

		Boolean haveUpdates = true, doBreaks = false;
		;
		Integer nRepeats = 0, targetBatchNo = null, nSmallUpdates = 0;
		Map<Integer, String> anchorFilesNamesByBatchNoMap = null;
		PostProcessDataSet mergedData = null;

		filesToConvertByBatchNoMap = null;
		fileOrderMap = null;
		filteredPairs = new HashMap<String, List<RtPair>>();

		System.out.println("Starting merge " + nthMerge);

		while (haveUpdates) {

			if (nRepeats.equals(0)) {
				filesToConvertByBatchNoMap = this.batchFileListLoaderPanel.grabBatchFileMapSegment(nthMerge);
				anchorFilesNamesByBatchNoMap = this.anchorFileListLoaderPanel.grabBatchFileMap();
				fileOrderMap = this.batchFileListLoaderPanel.grabFileOrderMapSegment(nthMerge);
			}

			updateBoards(nRepeats);

			targetBatchNo = fileOrderMap.get(0);
			String mergeTargetFileName = filesToConvertByBatchNoMap.get(targetBatchNo);

			if (!screenFiles(targetBatchNo, filesToConvertByBatchNoMap, anchorFilesNamesByBatchNoMap))
				return null; // false;

			updateLatticeSpace(targetBatchNo, anchorFilesNamesByBatchNoMap, filesToConvertByBatchNoMap);
			binnerResultLoaderPanel.handleFileNameSelection2(mergeTargetFileName);
			mergedData = binnerResultLoaderPanel.getLoadedData();

			mergedData.updateNamesForBatchAndMZ();

			List<Integer> sortedKeys = ListUtils.makeListFromCollection(fileOrderMap.keySet());
			Collections.sort(sortedKeys);

			int nProcessed = 0;
			for (int i = 0; i < sortedKeys.size(); i++) {

				Integer currOrderIdx = sortedKeys.get(i);

				if (currOrderIdx == null || currOrderIdx.equals(0))
					continue;

				Integer batchNo = fileOrderMap.get(currOrderIdx);
				if (batchNo == null || batchNo.equals(targetBatchNo))
					continue;

				if (filesToConvertByBatchNoMap.get(batchNo).startsWith("None"))
					continue;

				this.anchorLoaderPanel.updateForNewFileName(anchorFilesNamesByBatchNoMap.get(batchNo));
				this.binnerResultLoaderPanel.handleFileNameSelection2(filesToConvertByBatchNoMap.get(batchNo));

				PostProcessDataSet shiftedData = getShiftedData();
				PostProcessDataSet newMergedData = justRunMerge(shiftedData, mergedData);

				runProgressPanel.setInt1Selected(++nProcessed + 1);

				mergedData = newMergedData;
				mergedData.updateNamesForBatchAndMZ();

			}

			String freqsFileNameTag = "";
			if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
				freqsFileNameTag = outputFileNameTagPanel.getFileNameTag() + ".xlsx";

			Integer nFullMatchesBeforeUpdate = this.getNFullMatchesLastIter();

			Map<String, List<RtPair>> backTrackedPairsByBatch = createBatchSummaryReport(mergedData,
					freqsFileNameTag + "-" + nRepeats, filesToConvertByBatchNoMap);

			haveUpdates = (backTrackedPairsByBatch != null && backTrackedPairsByBatch.size() > 0);

			if (this.getNFullMatchesLastIter() - nFullMatchesBeforeUpdate < breakIncrement)
				nSmallUpdates++;

			int maxUpdatePts = 0;

			for (String batchStr : backTrackedPairsByBatch.keySet()) {

				if (backTrackedPairsByBatch.get(batchStr) == null || backTrackedPairsByBatch.get(batchStr).size() < 1) {
					continue;
				}

				AnchorMap mapForThinning = new AnchorMap();
				mapForThinning.addRtPairs(backTrackedPairsByBatch.get(batchStr));

				if (mapForThinning == null || mapForThinning.getSize() < 1)
					continue;

				mapForThinning.filterOutliers(getOutlierFilteringThreshold(), 3,
						this.getOutlierFilteringBuddySpacingCutoff(), getMaximumYLatticeThreshold(),
						getMinimumYLatticeThreshold(), batchStr, false);

				if (maxUpdatePts < mapForThinning.getSize())
					maxUpdatePts = mapForThinning.getSize();
			}

			if (maxUpdatePts < getMinNBacktrackPts() && nRepeats > 2)
				haveUpdates = false;

			if (!haveUpdates)
				break;

			writeNewLatticesAndUpdateAnchorFileNameMap(anchorFilesNamesByBatchNoMap, backTrackedPairsByBatch, nRepeats);

			tempLatticeFilesToKeep = anchorFilesNamesByBatchNoMap;
			runProgressPanel.setInt1Selected(0);

			if (nRepeats.equals(0)) {
				// QualityDiagnosticsEngine engine = new QualityDiagnosticsEngine();
				// engine.runLimitDiagnostics(mergedData, targetBatchNo,this.stepWiseBestAt1,
				// this.stepWiseBestAt2);

				// System.out.println("Running sampled diagnostics");
				// SampledDiagnosticsEngine sEngine = new SampledDiagnosticsEngine();
				// sEngine.runSampledMultiBatchDiagnostics(mergedData.getFeatures(),
				// getMassTolerance(), targetBatchNo);
			}

			if (++nRepeats > getMaxRtIterations() || nSmallUpdates >= 7) {
				if (nthMerge > 2) {
					stepWiseAmbiguous.add(this.runProgressPanel.getInt4Selected());
					stepWiseUnambiguous.add(this.runProgressPanel.getInt3Selected());
				}
				nSmallUpdates = 0;
				break;
			}

			System.out.println();
			System.out.println();
		}

		return mergedData;
	}

	// .005
	private Boolean screenFiles(Integer targetBatchNo, Map<Integer, String> filesToConvertByBatchNoMap,
			Map<Integer, String> anchorFilesNamesByBatchNoMap) {

		List<Integer> missingFilesToConvertBatchList = new ArrayList<Integer>();

		for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
			if (!batchNo.equals(targetBatchNo))
				if (!anchorFilesNamesByBatchNoMap.containsKey(batchNo))
					missingFilesToConvertBatchList.add(batchNo);
		}

		if (missingFilesToConvertBatchList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++) {
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			}
			JOptionPane.showMessageDialog(null,
					"A file name for converted data was not specified for batch " + (sb.length() > 1 ? "es" : "")
							+ sb.toString() + PostProccessConstants.LINE_SEPARATOR
							+ PostProccessConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}

		for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
			if (!anchorFilesNamesByBatchNoMap.containsKey(batchNo))
				if (!batchNo.equals(targetBatchNo))
					missingFilesToConvertBatchList.add(batchNo);
		}

		if (missingFilesToConvertBatchList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++) {
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			}
			JOptionPane.showMessageDialog(null,
					"An anchor file name was not specified for batch " + (sb.length() > 1 ? "es" : "") + sb.toString()
							+ PostProccessConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}

		return true;
	}

	private void updateLatticeSpace(Integer targetBatchNo, Map<Integer, String> anchorFilesNamesByBatchNoMap,
			Map<Integer, String> filesToConvertByBatchNoMap) {
		if (originalLatticeSpace == null) {
			for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
				if (!batchNo.equals(targetBatchNo)) {
					if (!anchorFilesNamesByBatchNoMap.containsKey(batchNo))
						continue;
					else {
						String firstLatticeName = anchorFilesNamesByBatchNoMap.get(batchNo);
						originalLatticeSpace = firstLatticeName.substring(0, firstLatticeName.lastIndexOf("/") + 1);
						if (originalLatticeSpace != null)
							break;
					}
				}
			}
		}
	}

	private void writeNewLatticesAndUpdateAnchorFileNameMap(Map<Integer, String> anchorFileNamesByBatchNoMap,
			Map<String, List<RtPair>> backTrackedPairsByBatch, Integer nRepeats) {

		List<String> outputFileNames = new ArrayList<String>();
		List<Integer> outputBatchIndices = new ArrayList<Integer>();

		int i = 0;
		for (String batchStr : backTrackedPairsByBatch.keySet()) {

			Integer batch = null;
			try {
				batch = Integer.parseInt(batchStr);
			} catch (Exception e) {
				continue;
			}

			String oldFileName = anchorFileNamesByBatchNoMap.get(batch);
			if (oldFileName == null)
				continue;

			String newFileName = grabNewFileName(oldFileName, nRepeats);

			List<RtPair> pairsToMerge = backTrackedPairsByBatch.get(batchStr);

			anchorLoaderPanel.updateForNewFileName(oldFileName);

			AnchorMap oldAnchorMap = anchorLoaderPanel.grabFreshAnchorMap();
			oldAnchorMap.filterOutliers(getOutlierFilteringThreshold(), 3, getOutlierFilteringBuddySpacingCutoff(),
					getMaximumYLatticeThreshold(), getMinimumYLatticeThreshold(), batchStr, false);

			AnchorMap backtrackAnchorPoints = new AnchorMap();
			backtrackAnchorPoints.addRtPairs(pairsToMerge);

			if (oldAnchorMap == null || backtrackAnchorPoints == null) {
				JOptionPane.showMessageDialog(null, "Something wrong with anchor read" + reportFileName);
				return;
			}

			LatticeMixer mixer = new LatticeMixer();
			List<RtPair> pairsToPrint = mixer.mergeAndScreenDuplicates(oldAnchorMap, backtrackAnchorPoints,
					getOutlierFilteringWindow(), batchStr);
			addNewFilteredPairsToClashMap(mixer.getClashMap());

			AnchorMap combinedMap = new AnchorMap();
			combinedMap.addRtPairs(pairsToPrint);
			// combinedMap.updateForEnds();

			combinedMap.filterOutliers(getOutlierFilteringThreshold(), getOutlierFilteringWindow(),
					getOutlierFilteringBuddySpacingCutoff(), getMaximumYLatticeThreshold(),
					getMinimumYLatticeThreshold(), batchStr, false);

			i++;

			AnchorFileWriter writer = new AnchorFileWriter(true);
			writer.outputResults(newFileName, combinedMap.getAsRtPairs());

			anchorFileNamesByBatchNoMap.put(batch, newFileName);
			outputBatchIndices.add(batch);
			outputFileNames.add(newFileName);
		}

		AnchorFileWriter writer = new AnchorFileWriter(true);
		writer.outputFileList(outputDirectoryPanel.getOutputDirectoryPath(), anchorFileNamesByBatchNoMap,
				"most_recent_lattice_list.csv");
	}

	private void updateBoards(Integer nRepeats) {

		if (nRepeats.equals(0)) {
			this.setMinDessertSize(10);
		} else if (nRepeats.equals(1)) {
			this.setMinNBacktrackPts(2);
			this.setMinDessertSize(5);
		} else {
			this.setMinNBacktrackPts(1);
			this.setMinDessertSize(2);
		}
		runProgressPanel.setInt2Selected(nRepeats);
	}

	private String grabNewFileName(String oldFileName, Integer nRepeats) {

		String oldFileRoot = oldFileName.substring(oldFileName.lastIndexOf(BatchMatchConstants.FILE_SEPARATOR) + 1,
				oldFileName.length());

		String newFileRoot = oldFileRoot.substring(0, oldFileRoot.lastIndexOf(".")) + "." + nRepeats + ".csv";

		String newFileName = this.temporaryLatticeSpace + BatchMatchConstants.FILE_SEPARATOR + newFileRoot;

		if (newFileName.length() > 180) {

			// String.valueOf((char)(i + 64))
			String suffix = ".b.";
			if (oldFileName.contains(".b."))
				suffix = ".c.";
			else if (oldFileName.contains(".c."))
				suffix = ".d.";
			else if (oldFileName.contains(".d."))
				suffix = ".e.";
			else if (oldFileName.contains(".e."))
				suffix = ".f.";
			else if (oldFileName.contains(".f."))
				suffix = ".g.";
			else if (oldFileName.contains(".g."))
				suffix = ".h.";

			newFileRoot = oldFileRoot.substring(0, oldFileRoot.lastIndexOf("_") + 6) + suffix + nRepeats + ".csv";
			newFileName = this.temporaryLatticeSpace + BatchMatchConstants.FILE_SEPARATOR + newFileRoot;
		}
		this.temporaryLatticeFiles.add(newFileName);
		return newFileName;
	}

	private void cleanUpLatticeSpace() {

		for (int i = 0; i < this.temporaryLatticeFiles.size(); i++) {

			String name = temporaryLatticeFiles.get(i);
			if (name != null && !tempLatticeFilesToKeep.containsValue(name)) {
				File f = new File(name);
				if (f.delete()) {
				}
				// else {
				// System.err.printf("Unable to delete file or directory : %s", f.getPath());
				// }
			}
		}

		for (String fileName : tempLatticeFilesToKeep.values()) {
			File file = new File(fileName);
			String oldFileRoot = fileName.substring(fileName.lastIndexOf(BatchMatchConstants.FILE_SEPARATOR) + 1,
					fileName.length());
			String oldStub = oldFileRoot.substring(0, oldFileRoot.indexOf("."));
			String newLabelName = "Most_Recent_" + oldStub + ".csv";

			String newFileName = this.originalLatticeSpace + newLabelName;

			if (file.renameTo(new File(newFileName))) {
				file.delete();
			} else {
				System.out.println("Failed to move the file" + fileName + " to location " + newFileName);
			}
		}
	}

	private void addNewFilteredPairsToClashMap(Map<String, List<RtPair>> newFilteredPairs) {

		for (String key : newFilteredPairs.keySet()) {
			if (!this.filteredPairs.containsKey(key))
				;
			filteredPairs.put(key, new ArrayList<RtPair>());

			for (int i = 0; i < newFilteredPairs.get(key).size(); i++) {
				filteredPairs.get(key).add(newFilteredPairs.get(key).get(i));
			}
			Collections.sort(filteredPairs.get(key), new RtPairComparator());
		}
	}

	private Map<String, List<RtPair>> createBatchSummaryReport(PostProcessDataSet data, String fileName,
			Map<Integer, String> filesToConvertByBatchNoMap) {

		BatchMatchDataSetSummaryCSVWriter writer = new BatchMatchDataSetSummaryCSVWriter(this.getMassTolerance());

		Map<String, List<RtPair>> backTrackedPairsByBatch = writer.writeSummaryToFile(
				data, outputDirectoryPanel.getOutputDirectoryPath() + BatchMatchConstants.FILE_SEPARATOR
						+ "batch_freqs_" + fileName.toLowerCase() + ".csv",
				getMinDesertSize(), filesToConvertByBatchNoMap);

		setNFullMatchesLastIter(writer.getnFullMatches());
		setNAmbiguousMatchesLastIter(writer.getnAmbiguousFullMatches());

		return backTrackedPairsByBatch;
	}
	/*
	 * BatchMatchProgressLogWriter writer = new
	 * BatchMatchProgressLogWriter(this.getMassTolerance());
	 * 
	 * Map<String, List<RtPair>> backTrackedPairsByBatch =
	 * writer.logProgressToScreen(data,
	 * outputDirectoryPanel.getOutputDirectoryPath() +
	 * BatchMatchConstants.FILE_SEPARATOR + "batch_freqs_" + fileName.toLowerCase()
	 * + ".csv", getMinDesertSize(), filesToConvertByBatchNoMap);
	 * 
	 * setNFullMatchesLastIter(writer.getnFullMatches());
	 * setNAmbiguousMatchesLastIter(writer.getnAmbiguousFullMatches());
	 * 
	 * return backTrackedPairsByBatch; }
	 * 
	 */

	private PostProcessDataSet getShiftedData() throws Exception {

		PostProcessDataSet shiftedData = null;
		AnchorMap anchorMap = anchorLoaderPanel.grabFreshAnchorMap();

		if (anchorMap != null) {
			BatchDataShifter shifter = new BatchDataShifter();
			PostProcessDataSet data = binnerResultLoaderPanel.getLoadedData();
			shiftedData = shifter.shiftSet(data, this.anchorLoaderPanel.grabAnchorMap());
		}
		return shiftedData;
	}

	private PostProcessDataSet justRunMerge(PostProcessDataSet mergingData, PostProcessDataSet targetData)
			throws Exception {

		// IMPORTANT : Batch 1 is merged into existing batch so we remove any prior
		// redundancy information
		for (FeatureFromFile feature : mergingData.getFeatures())
			feature.setRedundancyGroup(null);

		BatchDataMerger merger = new BatchDataMerger();
		PostProcessDataSet batch1Batch2Data = merger.identifyMatchedFeaturesAndMergeHeaders(mergingData, targetData,
				true, getMassTolerance(), this.getRTTolerance(), this.getAnnealingStretch(),
				annealTargetStepPanel.getIntSelected(), true);

		return batch1Batch2Data;
	}

	private void writeMergedData(PostProcessDataSet mergeData) throws Exception {
		String fileNameTag = "merged.xlsx";
		if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
			fileNameTag = outputFileNameTagPanel.getFileNameTag() + ".xlsx";

		String completeFileName = fileNameTag;
		if (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectoryPath()))
			completeFileName = outputDirectoryPanel.getOutputDirectoryPath() + PostProccessConstants.FILE_SEPARATOR
					+ fileNameTag;

		BatchMatchExcelOutputContainer outputContainer = new BatchMatchExcelOutputContainer(completeFileName);

		FileOutputStream output = null;
		try {
			reportFileName = outputContainer.grabIncrementedOutputName();
			output = new FileOutputStream(reportFileName);
		} catch (FileNotFoundException f) {
			throw f;
		}

		outputContainer.writeBatchMatchDataSet(mergeData, null, output, false);

		BatchMatchExpandedFeatureCSVWriter csvWriter = new BatchMatchExpandedFeatureCSVWriter(null);
		csvWriter.setDerivedColNameMapping(mergeData.getDerivedNameToHeaderMap());

		String csvReportName = csvWriter.grabCSVforXLSXName(reportFileName);
		csvWriter.writeExpandedFeatureSheet(csvReportName, mergeData);
	}

	private void setupRunConvertPanel() {
		JPanel runConvertPanel = new JPanel();
		runConvertPanel.setLayout(new BoxLayout(runConvertPanel, BoxLayout.X_AXIS));
		anchorColumnSecond = new JCheckBox("Map first anchor file column onto second    ");
		anchorColumnSecond.setSelected(true);
		sharedAnalysisSettings.setSecondLatticeColIsDest(true);
		anchorColumnSecond.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				getSharedAnalysisSettings().setSecondLatticeColIsDest(anchorColumnSecond.isSelected());
			}
		});
		anchorColumnSecond.setEnabled(false);

		JButton runConvertButton = new JButton("Write");
		runConvertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!allFilesImported()) {
					JOptionPane.showMessageDialog(null, "Please import data files before running     "
							+ PostProccessConstants.LINE_SEPARATOR + "the analysis.    ");
					return;
				}
				runConvertAndOutputResultsInWorkerThread();
			}
		});

		runConvertPanel = new JPanel();
		runConvertPanel.add(Box.createHorizontalGlue());
		runConvertPanel.add(anchorColumnSecond);
		runConvertPanel.add(Box.createHorizontalGlue());
		runConvertPanel.add(runConvertButton);
		runConvertPanel.add(Box.createHorizontalGlue());

		runConvertWrapPanel = new JPanel();
		runConvertWrapPanel.setLayout(new BoxLayout(runConvertWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder runConvertWrapBorder = BorderFactory.createTitledBorder("Convert Data ");
		runConvertWrapBorder.setTitleFont(boldFontForTitlePanel(runConvertWrapBorder, false));
		runConvertWrapBorder.setTitleColor(PostProccessConstants.TITLE_COLOR);
		runConvertWrapPanel.setBorder(runConvertWrapBorder);
		runConvertWrapPanel.add(runConvertPanel);
	}

	private void runConvertAndOutputResultsInWorkerThread() {

		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			public Boolean doInBackground() {

				runConvertWrapPanel.setVisible(false);

				progPanel.setVisible(true);

				Boolean dataWritten = false;
				try {
					dataWritten = mergeDataSetsSerially();
					if (!dataWritten) {
						progPanel.setVisible(false);
						JOptionPane.showMessageDialog(null, "Error while merging.");
						return false;
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error while merging.");
					progPanel.setVisible(false);
					return false;
				}
				progPanel.setVisible(false);
				return true;
			}

			@Override
			public void done() {
				try {
					if (!get()) {
						JOptionPane.showMessageDialog(null, "Error while merging batches ");
					} else {
						JOptionPane.showMessageDialog(null,
								"Merge complete and results have been written to "
										+ PostProccessConstants.LINE_SEPARATOR + reportFileName
										+ PostProccessConstants.LINE_SEPARATOR);
					}
				} catch (InterruptedException ignore) {
				} catch (ExecutionException ee) {
					String why = null;
					Throwable cause = ee.getCause();
					if (cause != null) {
						why = cause.getMessage();
					} else {
						why = ee.getMessage();
					}
					System.err.println("Error while merging batch files: " + why);
				}
				runConvertWrapPanel.setVisible(true);
			}
		};
		worker.execute();
	}

	public Integer getOutlierFilteringWindow() {
		return BatchMatchConstants.OUTLIER_FILTERING_WINDOW;
	}

	private Integer getMinDesertSize() {
		return this.backtrackOptionPanel.getInt1Selected();
	}

	private void setMinDessertSize(Integer val) {
		this.backtrackOptionPanel.setInt1Selected(val);
	}

	private Integer getRtWindowSize() {
		return this.backtrackOptionPanel.getInt2Selected();
	}

	private Integer getMaxRtIterations() {
		return this.backtrackOptionPanel.getInt3Selected();
	}

	private Integer getMinNBacktrackPts() {
		return this.backtrackOptionPanel.getInt4Selected();
	}

	private void setMinNBacktrackPts(Integer val) {
		this.backtrackOptionPanel.setInt4Selected(val);
	}

	private void setNFullMatchesLastIter(Integer nMatches) {
		runProgressPanel.setInt3Selected(nMatches);
	}

	private Integer getNFullMatchesLastIter() {
		return runProgressPanel.getInt3Selected();
	}

	private void setNAmbiguousMatchesLastIter(Integer nMatches) {
		runProgressPanel.setInt4Selected(nMatches);
	}

	private Double getOutlierFilteringBuddySpacingCutoff() {
		return this.outlierFilteringPars.getVal2();
	}

	private Double getOutlierFilteringThreshold() {
		return this.outlierFilteringPars.getVal1();
	}

	private Double getMaximumYLatticeThreshold() {
		return this.outlierFilteringPars.getVal3();
	}

	private Double getMinimumYLatticeThreshold() {
		return this.outlierFilteringPars.getVal4();
	}

	private Double getMassTolerance() {
		return this.matchTolerancesPanel.getVal1();
	}

	private Double getRTTolerance() {
		return this.matchTolerancesPanel.getVal2();
	}

	private Double getAnnealingStretch() {
		return this.matchTolerancesPanel.getVal3();
	}

	private Boolean allFilesImported() {
		return true;
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}

	private void createRecursiveLattices(PostProcessDataSet mergedData, Integer targetBatch, Integer completeSetSize) {
		RecursiveLatticeFileWriter writer = new RecursiveLatticeFileWriter(completeSetSize);
		writer.writeLatticeSetRelativeTo(targetBatch, outputDirectoryPanel.getOutputDirectoryPath(), mergedData);
	}
}
