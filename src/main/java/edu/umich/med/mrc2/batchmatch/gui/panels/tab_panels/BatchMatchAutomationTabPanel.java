////////////////////////////////////////////////////
// BatchMatchAutomationTabPanel.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.tab_panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import org.apache.commons.compress.utils.FileNameUtils;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.RtPairComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.AnchorMap;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureMatch;
import edu.umich.med.mrc2.batchmatch.data.orig.RtPair;
import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.AbstractStickyFileLocationPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.AnchorLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BatchFileListLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BinnerBatchFileLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.FeatureMappingLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.IntegerPickerPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.MultipleIntegerPickerPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.MultipleParametersPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.OutputFileNameTagPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.StickySettingsPanel;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.AnchorFileWriter;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchDataSetSummaryCSVWriter;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchExcelOutputContainer;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchExpandedFeatureCSVWriter;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchNamedResultsWriter;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.RecursiveLatticeFileWriter;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchDataMerger;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchDataShifter;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchMatchMappingFileInfo;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchNamedDataIntegrator;
import edu.umich.med.mrc2.batchmatch.process.orig.CompoundMatchDisambiguationEngine;
import edu.umich.med.mrc2.batchmatch.process.orig.LatticeMixer;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.med.mrc2.batchmatch.utils.orig.ListUtils;
import edu.umich.med.mrc2.batchmatch.utils.orig.PostProcessUtils;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

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

	private File reportFile;
	private int breakIncrement = 8;

	private File originalLatticeSpace = null;
	private File temporaryLatticeSpace = null;

	private Map<String, List<RtPair>> filteredPairs;
	private List<File> temporaryLatticeFiles;
	private Map<Integer, File> tempLatticeFilesToKeep;
	private List<Integer> stepWiseUnambiguous, stepWiseAmbiguous, stepWiseBestAt1, stepWiseBestAt2;

	public BatchMatchAutomationTabPanel(SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		setSharedAnalysisSettings(sharedAnalysisSettings);
		initializeStickySettings("batchMatchConvertAndMergeTab", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		temporaryLatticeSpace = BatchMatch.tmpDir;

		outputDirectoryPanel = new AbstractStickyFileLocationPanel(
				"Specify Report Output Directory ",
				"batchmatch_reports.directory") {

			@Override
			protected void updateInterfaceForNewSelection() {
				binnerResultLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectory());
				batchFileListLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectory());
				namedUnnamedMappingPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectory());
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

		//	Load the list of binnerized files with assigned batch numbers and selected primary batch
		//	
		batchFileListLoaderPanel = new BatchFileListLoaderPanel(
				"Specify File Containing List of Batch Files to Convert and Merge (With Path)", null, true) {

			@Override
			protected void updateInterfaceForNewData() {
				Map<Integer, File> batchFileMap = batchFileListLoaderPanel.grabBatchFileMap();
			}
		};
		batchFileListLoaderPanel.setupPanel(outputDirectoryPanel.getOutputDirectory());
		batchFileListLoaderPanel.setOpeningMessage(BatchMatchConstants.LINE_SEPARATOR + "Batch files"
				+ BatchMatchConstants.LINE_SEPARATOR + "=======================================");

		anchorFileListLoaderPanel = new BatchFileListLoaderPanel(
				"Specify File Containing List of Anchor File Names (With Path)", null) {

			@Override
			protected void updateInterfaceForNewData() {
				Map<Integer, File> batchFileMap = anchorFileListLoaderPanel.grabBatchFileMap();
			}
		};
		anchorFileListLoaderPanel.setupPanel(outputDirectoryPanel.getOutputDirectory());
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
		namedUnnamedMappingPanel.setupPanel(outputDirectoryPanel.getOutputDirectory());

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
		overallPostProcessingWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
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
			Map<Integer, String> anchorFilesByBatchNoMap, Map<Integer, Integer> fileOrderMap,
			Integer targetBatchNo) {

		List<Integer> missingFilesToConvertBatchList = new ArrayList<Integer>();

		for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
			if (!batchNo.equals(targetBatchNo))
				if (!anchorFilesByBatchNoMap.containsKey(batchNo))
					missingFilesToConvertBatchList.add(batchNo);
		}
		if (missingFilesToConvertBatchList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++) {
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			}
			JOptionPane.showMessageDialog(null,
					"A file name for converted data was not specified for batch " + (sb.length() > 1 ? "es" : "")
							+ sb.toString() + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}

		for (Integer batchNo : filesToConvertByBatchNoMap.keySet()) {
			if (!anchorFilesByBatchNoMap.containsKey(batchNo))
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
							+ BinnerConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}

		return true;
	}

	// AndMZ

	/**
	 * Iterative merge of Binner output files
	 * @return
	 * @throws Exception
	 */
	private Boolean mergeDataSetsSerially() throws Exception {
		try {
			temporaryLatticeFiles = new ArrayList<File>();
			tempLatticeFilesToKeep = new HashMap<Integer, File>();

			Map<Integer, File> filesToConvertByBatchNoMap = this.batchFileListLoaderPanel.grabBatchFileMap();
			Integer completeMergeSize = filesToConvertByBatchNoMap.size();	//	Total # of batches to merge

			stepWiseBestAt1 = new ArrayList<Integer>();
			stepWiseBestAt2 = new ArrayList<Integer>();
			stepWiseAmbiguous = new ArrayList<Integer>();
			stepWiseUnambiguous = new ArrayList<Integer>();

			PostProcessDataSet mergedData = null;
			
			//	Add batches one by one
			for (int i = 2; i <= completeMergeSize; i++) {
				System.out.println("Setting up merge with " + (i) + " files");
				mergedData = runSerialMergeStepCodeClean(i, completeMergeSize.equals(i));
				// justUpdateForNamed(mergedData);
			}

			if (mergedData != null) {

				mergedData.setMaxPossibleMatchCt(completeMergeSize);
				cleanUpLatticeSpace();
				CompoundMatchDisambiguationEngine.recommendAmbiguousFeaturesToRemove(mergedData, completeMergeSize);
				writeMergedData(mergedData);

				for (int i = 0; i < stepWiseBestAt1.size(); i++) {
					Double OneARatio = (100.0 * stepWiseAmbiguous.get(i)) / (1.0 * stepWiseBestAt1.get(i));
					Double OneURatio = (100.0 * stepWiseUnambiguous.get(i)) / (1.0 * stepWiseBestAt1.get(i));
					Double TwoARatio = (100.0 * stepWiseAmbiguous.get(i)) / (1.0 * stepWiseBestAt2.get(i));
					Double TwoURatio = (100.0 * stepWiseUnambiguous.get(i)) / (1.0 * stepWiseBestAt2.get(i));

					String reportLine = String.format(
							"RT Window 1.0: %5d/%5d  = %5.2f%% Unambiguous,  %5d/%5d  = %5.2f%% Ambiguous,   "
							+ "RT Window 2.0: %5d/%5d  = %5.2f%% Unambiguous,  %5d/%5d  = %5.2f%% Ambiguous",
							stepWiseUnambiguous.get(i), stepWiseBestAt1.get(i), OneURatio, stepWiseAmbiguous.get(i),
							stepWiseBestAt1.get(i), OneARatio, stepWiseUnambiguous.get(i), stepWiseBestAt2.get(i),
							TwoURatio, stepWiseAmbiguous.get(i), stepWiseBestAt2.get(i), TwoARatio);

					System.out.println(reportLine);
				}
			}
			if (mergedData == null)
				throw new Exception("Error -- merged data is null");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
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
			namedReportName = outputDirectoryPanel.getOutputDirectoryPath() + BinnerConstants.FILE_SEPARATOR
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
			shiftReportName = outputDirectoryPanel.getOutputDirectoryPath() + BinnerConstants.FILE_SEPARATOR
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
	 * BinnerConstants.FILE_SEPARATOR + fileNameTag;
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
	 * BinnerConstants.FILE_SEPARATOR + fileNameTag;
	 * 
	 * RecursiveLatticeFileWriter shiftWriter = new
	 * RecursiveLatticeFileWriter(completeMergeSize);
	 * shiftWriter.writeShiftMapForMerge(shiftReportName, dataSet); }
	 */

	/**
	 * Iterative merge of two PostProcessDataSet objects
	 * @param nthMerge
	 * @param finalMerge
	 * @return
	 * @throws Exception
	 */
	private PostProcessDataSet runSerialMergeStepCodeClean(int nthMerge, Boolean finalMerge) throws Exception {

		Map<Integer, Integer> fileOrderMap = batchFileListLoaderPanel.grabFileOrderMap();
		Map<Integer, File> filesToConvertByBatchNoMap = 
				this.batchFileListLoaderPanel.grabBatchFileMapSegment(nthMerge);

		Boolean haveUpdates = true, doBreaks = false;
		Integer nRepeats = 0, targetBatchNo = null, nSmallUpdates = 0;
		Map<Integer, File> anchorFilesByBatchNoMap = null;
		PostProcessDataSet mergedData = null;

		filesToConvertByBatchNoMap = null;
		fileOrderMap = null;
		filteredPairs = new HashMap<String, List<RtPair>>();

		System.out.println("Starting merge " + nthMerge);

		while (haveUpdates) {

			if (nRepeats.equals(0)) {
				filesToConvertByBatchNoMap = this.batchFileListLoaderPanel.grabBatchFileMapSegment(nthMerge);
				anchorFilesByBatchNoMap = this.anchorFileListLoaderPanel.grabBatchFileMap();
				fileOrderMap = this.batchFileListLoaderPanel.grabFileOrderMapSegment(nthMerge);
			}

			updateBoards(nRepeats);	//	GUI update to monitor progress

			targetBatchNo = fileOrderMap.get(0);
			File mergeTargetFile = filesToConvertByBatchNoMap.get(targetBatchNo);

			//	Check if all necessary files are present
			if (!screenFiles(targetBatchNo, filesToConvertByBatchNoMap, anchorFilesByBatchNoMap))
				return null; // false;

			//	Update location of lattice (anchor) files
			updateLatticeSpace(targetBatchNo, anchorFilesByBatchNoMap, filesToConvertByBatchNoMap);
			
			//	 Read Binner4batchMatch output file into PostProcessDataSet object mergedData
			binnerResultLoaderPanel.handleFileSelection2(mergeTargetFile);
			mergedData = binnerResultLoaderPanel.getLoadedData();

			//	Add MZ and batch # to each feature name in the batch
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

				if (filesToConvertByBatchNoMap.get(batchNo).getName().startsWith("None"))
					continue;

				//	Build anchor map from selected anchor file
				this.anchorLoaderPanel.updateForNewFile(anchorFilesByBatchNoMap.get(batchNo));
				
				//	Read Binner4batchMatch output file for the new batch to mergr into PostProcessDataSet object
				this.binnerResultLoaderPanel.handleFileSelection2(filesToConvertByBatchNoMap.get(batchNo));

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

			Map<String, List<RtPair>> backTrackedPairsByBatch = 
					createBatchSummaryReport(
						mergedData,
						freqsFileNameTag + "-" + nRepeats,
						filesToConvertByBatchNoMap);

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

			writeNewLatticesAndUpdateAnchorFileNameMap(
					anchorFilesByBatchNoMap, backTrackedPairsByBatch, nRepeats);

			tempLatticeFilesToKeep = anchorFilesByBatchNoMap;
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
		}
		return mergedData;
	}

	private Boolean screenFiles(
			int targetBatchNo, 
			Map<Integer, File> filesToConvertByBatchNoMap,
			Map<Integer, File> anchorFilesByBatchNoMap) {

		List<Integer> missingFilesToConvertBatchList = new ArrayList<Integer>();

		for (int batchNo : filesToConvertByBatchNoMap.keySet()) {
			
			if (batchNo != targetBatchNo && !anchorFilesByBatchNoMap.containsKey(batchNo))
					missingFilesToConvertBatchList.add(batchNo);
		}
		if (!missingFilesToConvertBatchList.isEmpty()) {
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++)
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			
			JOptionPane.showMessageDialog(null,
					"A file name for converted data was not specified for batch " + (sb.length() > 1 ? "es" : "")
							+ sb.toString() + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}
		for (int batchNo : filesToConvertByBatchNoMap.keySet()) {
			
			if (!anchorFilesByBatchNoMap.containsKey(batchNo) && batchNo != targetBatchNo)
					missingFilesToConvertBatchList.add(batchNo);
		}
		if (!missingFilesToConvertBatchList.isEmpty()) {
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < missingFilesToConvertBatchList.size(); i++) {
				sb.append(missingFilesToConvertBatchList.get(i) + (i > 0 ? ", " : ""));
			}
			JOptionPane.showMessageDialog(null,
					"An anchor file name was not specified for batch " + (sb.length() > 1 ? "es" : "") + sb.toString()
							+ BinnerConstants.LINE_SEPARATOR + "was not specified.");
			return false;
		}
		return true;
	}

	private void updateLatticeSpace(
			int targetBatchNo, 
			Map<Integer, File> anchorFilesByBatchNoMap,
			Map<Integer, File> filesToConvertByBatchNoMap) {
		
		if (originalLatticeSpace == null) {
			
			for (int  batchNo : filesToConvertByBatchNoMap.keySet()) {
				
				if (batchNo != targetBatchNo) {
					
					if (!anchorFilesByBatchNoMap.containsKey(batchNo))
						continue;
					else {
						File firstLatticeFile = anchorFilesByBatchNoMap.get(batchNo);
						originalLatticeSpace = firstLatticeFile.getParentFile();
						if (originalLatticeSpace != null)
							break;
					}
				}
			}
		}
	}

	private void writeNewLatticesAndUpdateAnchorFileNameMap(
			Map<Integer, File> anchorFilesByBatchNoMap,
			Map<String, List<RtPair>> backTrackedPairsByBatch, 
			int nRepeats) {

		List<Integer> outputBatchIndices = new ArrayList<Integer>();

		int i = 0;
		for (String batchStr : backTrackedPairsByBatch.keySet()) {

			Integer batch = null;
			try {
				batch = Integer.parseInt(batchStr);
			} catch (Exception e) {
				continue;
			}

			File oldFile = anchorFilesByBatchNoMap.get(batch);
			if (oldFile == null)
				continue;

			List<RtPair> pairsToMerge = backTrackedPairsByBatch.get(batchStr);
			anchorLoaderPanel.updateForNewFile(oldFile);

			AnchorMap oldAnchorMap = anchorLoaderPanel.grabFreshAnchorMap();
			oldAnchorMap.filterOutliers(getOutlierFilteringThreshold(), 3, getOutlierFilteringBuddySpacingCutoff(),
					getMaximumYLatticeThreshold(), getMinimumYLatticeThreshold(), batchStr, false);

			AnchorMap backtrackAnchorPoints = new AnchorMap();
			backtrackAnchorPoints.addRtPairs(pairsToMerge);

			if (oldAnchorMap == null || backtrackAnchorPoints == null) {
				JOptionPane.showMessageDialog(null, "Something wrong with anchor read" + reportFile);
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

			File newFile = grabNewFile(oldFile, nRepeats);
			AnchorFileWriter writer = new AnchorFileWriter(true);
			writer.outputResults(newFile, combinedMap.getAsRtPairs());

			anchorFilesByBatchNoMap.put(batch, newFile);
			outputBatchIndices.add(batch);
		}

		AnchorFileWriter writer = new AnchorFileWriter(true);
		writer.outputFileList(
				outputDirectoryPanel.getOutputDirectory(), 
				anchorFilesByBatchNoMap,
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

	private File grabNewFile(File oldFile, int nRepeats) {

		File oldFileRoot = oldFile.getParentFile();
		String newFileName = FileNameUtils.getBaseName(oldFile.toPath()) + "." + nRepeats + ".csv";		
		File newFile = Paths.get(oldFileRoot.getAbsolutePath(), newFileName).toFile();

//		String newFileRoot = oldFileRoot.substring(0, oldFileRoot.lastIndexOf(".")) + "." + nRepeats + ".csv";
//
//		String newFileName = this.temporaryLatticeSpace + BatchMatchConstants.FILE_SEPARATOR + newFileRoot;

//		if (newFileName.length() > 180) {
//
//			// String.valueOf((char)(i + 64))
//			String suffix = ".b.";
//			if (oldFileName.contains(".b."))
//				suffix = ".c.";
//			else if (oldFileName.contains(".c."))
//				suffix = ".d.";
//			else if (oldFileName.contains(".d."))
//				suffix = ".e.";
//			else if (oldFileName.contains(".e."))
//				suffix = ".f.";
//			else if (oldFileName.contains(".f."))
//				suffix = ".g.";
//			else if (oldFileName.contains(".g."))
//				suffix = ".h.";
//
//			newFileRoot = oldFileRoot.substring(0, oldFileRoot.lastIndexOf("_") + 6) + suffix + nRepeats + ".csv";
//			newFileName = this.temporaryLatticeSpace + BatchMatchConstants.FILE_SEPARATOR + newFileRoot;
//		}
		
		this.temporaryLatticeFiles.add(newFile);
		
		return newFile;
	}

	private void cleanUpLatticeSpace() {

		for (int i = 0; i < this.temporaryLatticeFiles.size(); i++) {

			File existingLatticeFile = temporaryLatticeFiles.get(i);
			if (existingLatticeFile != null && !tempLatticeFilesToKeep.containsValue(existingLatticeFile)) {

				try {
					Files.delete(existingLatticeFile.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (File file : tempLatticeFilesToKeep.values()) {

			String oldStub = FileNameUtils.getBaseName(file.toPath());  //	fileName.substring(fileName.lastIndexOf(BatchMatchConstants.FILE_SEPARATOR) + 1, fileName.length());
			// String oldStub = oldFileRoot.substring(0, oldFileRoot.indexOf("."));
			String newFileName = "Most_Recent_" + oldStub + ".csv";
			//	String newFileName = this.originalLatticeSpace + newLabelName;
			
			Path newPath = Paths.get(originalLatticeSpace.getAbsolutePath(), newFileName);
			try {
				Files.move(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println("Failed to rename the file" + file.getName() + " to " + newFileName);
				e.printStackTrace();
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

	/**
	 * Summarize alignment results
	 * @param data
	 * @param fileName
	 * @param filesToConvertByBatchNoMap
	 * @return
	 */
	private Map<String, List<RtPair>> createBatchSummaryReport(
			PostProcessDataSet data, 
			String fileName,
			Map<Integer, File> filesToConvertByBatchNoMap) {

		BatchMatchDataSetSummaryCSVWriter writer = 
				new BatchMatchDataSetSummaryCSVWriter(this.getMassTolerance());
		
		File outputFile = Paths.get(outputDirectoryPanel.getOutputDirectory().getAbsolutePath(), 
				"batch_freqs_" + fileName.toLowerCase() + ".csv").toFile();

		Map<String, List<RtPair>> backTrackedPairsByBatch = 
				writer.writeSummaryToFile(
						data, 
						outputFile,
						getMinDesertSize(), //	Not clear what it is
						filesToConvertByBatchNoMap);

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

	/**
	 * Shift RT values in the new batch to be matched using supplied anchor map
	 * @return
	 * @throws Exception
	 */
	private PostProcessDataSet getShiftedData() throws Exception {

		PostProcessDataSet shiftedData = null;
		AnchorMap anchorMap = anchorLoaderPanel.grabFreshAnchorMap();

		if (anchorMap != null) {
			BatchDataShifter shifter = new BatchDataShifter();
			PostProcessDataSet data = binnerResultLoaderPanel.getLoadedData();
			shiftedData = shifter.shiftSet(data, this.anchorLoaderPanel.grabAnchorMap());
		}
		else {
			System.err.println("No anchor map");
		}
		return shiftedData;
	}

	/**
	 * @param mergingData - new data set to merge
	 * @param targetData - previously merged data set
	 * @return
	 * @throws Exception
	 */
	private PostProcessDataSet justRunMerge(
			PostProcessDataSet mergingData, 
			PostProcessDataSet targetData) throws Exception {

		// IMPORTANT : Batch 1 is merged into existing batch so we remove any prior
		// redundancy information
		for (FeatureFromFile feature : mergingData.getFeatures())
			feature.setRedundancyGroup(null);

		BatchDataMerger merger = new BatchDataMerger();
		PostProcessDataSet batch1Batch2Data = 
				merger.identifyMatchedFeaturesAndMergeHeaders(
						mergingData, 
						targetData,
						true, getMassTolerance(), 
						this.getRTTolerance(), 
						this.getAnnealingStretch(),
						annealTargetStepPanel.getIntSelected(), 
						true);

		return batch1Batch2Data;
	}

	private void writeMergedData(PostProcessDataSet mergeData) throws Exception {
		
		File outputDir = outputDirectoryPanel.getOutputDirectory();
		if(outputDir == null || !outputDir.exists()) {
			
			JOptionPane.showMessageDialog(null, "Invalid or missing output directory!");
			return;
		}
		String fileNameTag = "merged.xlsx";
		if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
			fileNameTag = outputFileNameTagPanel.getFileNameTag() + ".xlsx";

//		String completeFileName = fileNameTag;
//		if (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectoryPath()))
//			completeFileName = outputDirectoryPanel.getOutputDirectoryPath() + BinnerConstants.FILE_SEPARATOR
//					+ fileNameTag;
		
		File outputFile = Paths.get(outputDir.getAbsolutePath(), fileNameTag).toFile();
		BatchMatchExcelOutputContainer outputContainer = 
				new BatchMatchExcelOutputContainer(outputFile);
		reportFile = outputContainer.grabIncrementedOutputFile();
		try(FileOutputStream output = new FileOutputStream(reportFile)) {
			outputContainer.writeBatchMatchDataSet(mergeData, null, output, false);			
		} catch (Exception f) {
			f.printStackTrace();
		}
		BatchMatchExpandedFeatureCSVWriter csvWriter = 
				new BatchMatchExpandedFeatureCSVWriter(null);
		csvWriter.setDerivedColNameMapping(mergeData.getDerivedNameToHeaderMap());
		File csvReportFile = csvWriter.grabCSVforXLSXName(reportFile);
		csvWriter.writeExpandedFeatureSheet(csvReportFile, mergeData);
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
							+ BinnerConstants.LINE_SEPARATOR + "the analysis.    ");
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
		runConvertWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
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
										+ BinnerConstants.LINE_SEPARATOR + reportFile
										+ BinnerConstants.LINE_SEPARATOR);
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

	private void createRecursiveLattices(
			PostProcessDataSet mergedData, 
			int targetBatch, 
			int completeSetSize) {
		
		RecursiveLatticeFileWriter writer = new RecursiveLatticeFileWriter(completeSetSize);
		writer.writeLatticeSetRelativeTo(targetBatch, outputDirectoryPanel.getOutputDirectory(), mergedData);
	}
}
