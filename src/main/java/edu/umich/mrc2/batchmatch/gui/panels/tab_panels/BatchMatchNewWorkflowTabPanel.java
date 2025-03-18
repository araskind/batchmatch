////////////////////////////////////////////////////
// BatchMatchLatticeCreationTabPanel.java
// Written by Jan Wigginton March 2022
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.tab_panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.data.RtPair;
import edu.umich.mrc2.batchmatch.data.SharedAnalysisSettings;
import edu.umich.mrc2.batchmatch.data.comparators.RtPairComparator;
import edu.umich.mrc2.batchmatch.gui.LayoutUtils;
import edu.umich.mrc2.batchmatch.gui.panels.orig.AbstractStickyFileLocationPanel;
import edu.umich.mrc2.batchmatch.gui.panels.orig.BatchMatchLatticeTypePanel;
import edu.umich.mrc2.batchmatch.gui.panels.orig.FileListLoaderDisplayPanel;
import edu.umich.mrc2.batchmatch.gui.panels.orig.IntegerPickerPanel;
import edu.umich.mrc2.batchmatch.gui.panels.orig.OutputFileNameTagPanel;
import edu.umich.mrc2.batchmatch.gui.panels.orig.StickySettingsPanel;
import edu.umich.mrc2.batchmatch.io.sheetreaders.MetabolomicsTargetedDataLoader;
import edu.umich.mrc2.batchmatch.io.sheetwriters.AnchorFileWriter;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.utils.BatchMatchLatticeBuilder;
import edu.umich.mrc2.batchmatch.utils.ListUtils;

public class BatchMatchNewWorkflowTabPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 1L;
	
	private AbstractStickyFileLocationPanel outputDirectoryPanelBinnerData;
	private AbstractStickyFileLocationPanel outputDirectoryPanel;
	private OutputFileNameTagPanel outputFileNameTagPanel;
	private IntegerPickerPanel poolSampleSizePanel;
	private FileListLoaderDisplayPanel binnerFileListLoaderPanel, rawFileListLoaderPanel, namedFileListLoaderPanel;
	private BatchMatchLatticeTypePanel latticeTypePanel;

	private JButton createLatticeSetButton;
	private JPanel batchLatticeWrapPanel, createLatticeSetWrapPanel, progPanel;
	private JProgressBar progBar;
	private String fileFileListName = "dd_lattice_list.csv";

	public BatchMatchNewWorkflowTabPanel(SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		// setSharedAnalysisSettings(sharedAnalysisSettings);
		initializeStickySettings("batchMatchNewWorkflowTab", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel(SharedAnalysisSettings sharedAnalysisSettings) {

		initializeArrays();

		outputDirectoryPanelBinnerData = new AbstractStickyFileLocationPanel("Specify Output Directory ", "binnerizedfilelist.directory") {

			@Override
			protected void updateInterfaceForNewSelection() {
				binnerFileListLoaderPanel
						.setInitialDirectoryForChooser(outputDirectoryPanelBinnerData.getOutputDirectoryPath());
			}
		};
		outputDirectoryPanelBinnerData.setupPanel();

		binnerFileListLoaderPanel = new FileListLoaderDisplayPanel() {

			@Override
			protected void updateInterfaceForNewData(int chosen, ArrayList<String> fileNames) {

				System.out.println("Workflow panel : New batch target selected was " + chosen);
				latticeTypePanel.setTargetSelected(chosen);
				binnerFileListLoaderPanel
						.setInitialDirectoryForChooser(outputDirectoryPanelBinnerData.getOutputDirectoryPath());
			}
			@Override
			protected void processNewData() {

				Map<String, String> batchFileNameMap = binnerFileListLoaderPanel.getBatchFileMap();
				Map<String, String> batchFileOrderMap = binnerFileListLoaderPanel.getFileOrderMap();

				printBinnerFileMap("binnerized_files.csv", batchFileNameMap, batchFileOrderMap, true);

				JOptionPane.showMessageDialog(null,
						"Binnerized file list written to " + outputDirectoryPanelBinnerData.getOutputDirectoryPath()
								+ BatchMatchConstants.FILE_SEPARATOR + "files.csv");
			}
		};
		binnerFileListLoaderPanel.setFileExtension("csv");

		binnerFileListLoaderPanel.setupPanel(
				"Specify Binner Files, Batch Numbers and Merge Order (Drag and drop to map filename to batch and specify merge order)",
				"Create Merge List", outputDirectoryPanelBinnerData.getOutputDirectoryPath(), true);

		outputDirectoryPanel = new AbstractStickyFileLocationPanel("Specify Lattice File Directory ",
				"latticefile.directory") {

			@Override
			protected void updateInterfaceForNewSelection() {
				if (rawFileListLoaderPanel != null)
					rawFileListLoaderPanel.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());

				if (namedFileListLoaderPanel != null)
					namedFileListLoaderPanel
							.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());
			}
		};
		outputDirectoryPanel.setupPanel();

		rawFileListLoaderPanel = new FileListLoaderDisplayPanel() {

			@Override
			protected void updateInterfaceForNewData(int chosen, ArrayList<String> fileNames) {
				if (rawFileListLoaderPanel != null)
					rawFileListLoaderPanel.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());
			}

			@Override
			protected void processNewData() {
				Map<String, String> batchFileNameMap = rawFileListLoaderPanel.getBatchFileMap();
				createLatticeSet();
			}
		};
		rawFileListLoaderPanel.setFileExtension("txt");
		rawFileListLoaderPanel.setShowBatchLabelBtn(false);
		rawFileListLoaderPanel.setupPanel(
				"Select Raw Feature Data Files To Compute Lattice Set and Write Lattice  List (Drag and drop to map filename to batch)",
				"Create Lattice Files", outputDirectoryPanel.getOutputDirectoryPath(), false, "Update Labels");

		namedFileListLoaderPanel = new FileListLoaderDisplayPanel() {

			@Override
			protected void updateInterfaceForNewData(int chosen, ArrayList<String> fileNames) {

				namedFileListLoaderPanel.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());
			}

			@Override
			protected void processNewData() {

				Map<String, String> batchFileNameMap = namedFileListLoaderPanel.getBatchFileMap();
				createNamedLatticeSet();
				createNamedMassMap();

				// printFileMap("named_data_files.csv", batchFileNameMap, null, false);
				// JOptionPane.showMessageDialog(null, "File mapping named feature files to
				// batch was written to " + outputDirectoryPanel2.getOutputDirectoryPath()
				// + BatchMatchConstants.FILE_SEPARATOR + "named_data_files.csv");
			}
		};
		namedFileListLoaderPanel.setFileExtension("csv");
		namedFileListLoaderPanel.setShowBatchLabelBtn(false);
		namedFileListLoaderPanel.setupPanel(
				"Select Profinder Files To Compute Lattice Set and Build Lattice List (Drag and drop to map filename to batch)",
				"Create Lattice Files", outputDirectoryPanel.getOutputDirectoryPath(), false, "Update Labels");

		outputFileNameTagPanel = new OutputFileNameTagPanel("Summarize");
		outputFileNameTagPanel.setupPanel();

		latticeTypePanel = new BatchMatchLatticeTypePanel() {

			@Override
			public void updateForInputTypeChange() {

				rawFileListLoaderPanel.setVisible(latticeTypePanel.useDDLattice());
				namedFileListLoaderPanel.setVisible(latticeTypePanel.useNamedLattice());
				poolSampleSizePanel.setEnableState(latticeTypePanel.useDDLattice());
			}
		};
		latticeTypePanel.setupPanel();
		latticeTypePanel.disableBlankOption();
		latticeTypePanel.disableRecursiveOption();

		rawFileListLoaderPanel.setVisible(latticeTypePanel.useDDLattice());
		namedFileListLoaderPanel.setVisible(latticeTypePanel.useNamedLattice());

		setupCreateLatticeSetPanel();

		progPanel = new JPanel();
		progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.X_AXIS));
		progPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		progBar = new JProgressBar(0, 500);
		progBar.setIndeterminate(true);
		progPanel.add(progBar);
		progPanel.setVisible(false);

		add(Box.createVerticalStrut(1));
		batchLatticeWrapPanel = new JPanel();

		batchLatticeWrapPanel.setLayout(new BoxLayout(batchLatticeWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder overallPostProcessingWrapBorder = BorderFactory
				.createTitledBorder("Specify Data Merge Order and Create Lattice Sets");
		overallPostProcessingWrapBorder.setTitleFont(boldFontForTitlePanel(overallPostProcessingWrapBorder, false));
		overallPostProcessingWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		batchLatticeWrapPanel.setBorder(overallPostProcessingWrapBorder);

		poolSampleSizePanel = new IntegerPickerPanel("poolSampleSize") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {

				return true;
			}
		};
		poolSampleSizePanel.setupPanel("Pool Sample Size", "Pool Sample Size");
		poolSampleSizePanel.setEnableState(latticeTypePanel.useDDLattice());

		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));
		batchLatticeWrapPanel.add(outputDirectoryPanelBinnerData);

		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));
		batchLatticeWrapPanel.add(binnerFileListLoaderPanel);

		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));
		batchLatticeWrapPanel.add(outputDirectoryPanel);
		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));

		batchLatticeWrapPanel.add(latticeTypePanel);
		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));

		batchLatticeWrapPanel.add(rawFileListLoaderPanel);
		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));

		batchLatticeWrapPanel.add(namedFileListLoaderPanel);
		batchLatticeWrapPanel.add(progPanel);
		batchLatticeWrapPanel.add(Box.createVerticalStrut(1));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(1));
		add(batchLatticeWrapPanel);
		add(Box.createVerticalStrut(1));
		LayoutUtils.addBlankLines(this, 5);
	}

	private void setupCreateLatticeSetPanel() {

		JPanel createLatticeSetPanel = new JPanel();
		createLatticeSetPanel.setLayout(new BoxLayout(createLatticeSetPanel, BoxLayout.X_AXIS));
		createLatticeSetButton = new JButton("Create Lattice Set");
		createLatticeSetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!allFilesImported()) {
					JOptionPane.showMessageDialog(null, "Please import data files before running     "
							+ BinnerConstants.LINE_SEPARATOR + "the analysis.    ");
					return;
				}
				runReportAndOutputResultsInWorkerThread();
			}
		});

		createLatticeSetPanel = new JPanel();
		createLatticeSetPanel.add(Box.createHorizontalGlue());
		createLatticeSetPanel.add(createLatticeSetButton);
		createLatticeSetPanel.add(Box.createHorizontalGlue());

		createLatticeSetWrapPanel = new JPanel();
		createLatticeSetWrapPanel.setLayout(new BoxLayout(createLatticeSetWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder runConvertWrapBorder = BorderFactory.createTitledBorder("Create Lattice Set ");
		runConvertWrapBorder.setTitleFont(boldFontForTitlePanel(runConvertWrapBorder, false));
		runConvertWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		createLatticeSetWrapPanel.setBorder(runConvertWrapBorder);
		createLatticeSetWrapPanel.add(createLatticeSetPanel);
	}

	private void runReportAndOutputResultsInWorkerThread() {

		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			public Boolean doInBackground() {

				createLatticeSetWrapPanel.setVisible(false);

				progPanel.setVisible(true);
				try {
					if (latticeTypePanel.useNamedLattice()) {
						createNamedLatticeSet();
						createNamedMassMap();
					} else
						createLatticeSet();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error during merge operation.");
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
						JOptionPane.showMessageDialog(null, "Error during lattice creation");
					} else {
						JOptionPane.showMessageDialog(null,
								"Load complete and lattice files have been written to "
										+ outputDirectoryPanel.getOutputDirectoryPath()
										+ BatchMatchConstants.FILE_SEPARATOR + fileFileListName
										+ BatchMatchConstants.LINE_SEPARATOR + BatchMatchConstants.LINE_SEPARATOR);
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
					System.err.println("Error while merging data: " + why);
				}
				createLatticeSetWrapPanel.setVisible(true);
			}
		};
		worker.execute();
	}

	private Boolean createNamedLatticeSet() {

		Map<String, String> batchFileTagMap = namedFileListLoaderPanel.getBatchFileMap();
		String outputDirectory = outputDirectoryPanel.getOutputDirectoryPath();

		MetabolomicsTargetedDataLoader rtLoader = new MetabolomicsTargetedDataLoader();
		Map<String, Map<String, Double>> latticeAverages = new HashMap<String, Map<String, Double>>();
		for (String fileName : batchFileTagMap.values())
			latticeAverages.put(fileName, rtLoader.locateRTsAndLoadAverageMap(fileName, true)); // useControlsOnly.isSelected()));

		Map<String, Map<String, Integer>> latticeSampleCts = new HashMap<String, Map<String, Integer>>();
		for (String fileName : batchFileTagMap.values())
			latticeSampleCts.put(fileName, rtLoader.locateRTsAndLoadSampleCtMap(fileName, true)); // useControlsOnly.isSelected()));

		String targetKey = latticeTypePanel.getTargetSelected() == null ? "0"
				: latticeTypePanel.getTargetSelected().toString();
		; // batchLabelPanel.getIntSelected().toString();
		Integer targetKeyAsInt = latticeTypePanel.getTargetSelected();
		for (String fileName : latticeAverages.keySet()) {
			System.out.println("Filename : " + fileName);
		}

		Map<String, List<RtPair>> latticeMapRelativeToKeyed = new HashMap<String, List<RtPair>>();

		String targetedFile = batchFileTagMap.get(targetKey);
		String targetLabel = String.format("Batch%02d", targetKeyAsInt), currentLabel = "";
		String fileTagTarget = String.format("%02d", targetKeyAsInt);

		Map<String, Double> keyedMap = latticeAverages.get(targetedFile);

		Map<String, String> completeLatticeFileNames = new HashMap<String, String>();
		String fileTagCurrent = null;
		String currIdx = null;

		for (String fileName : latticeAverages.keySet()) {

			if (fileName.equals(targetedFile))
				continue;

			Map<String, Double> testedMap = latticeAverages.get(fileName);
			List<RtPair> matchedRtPairs = new ArrayList<RtPair>();

			for (String featureName : keyedMap.keySet()) {
				if (testedMap.containsKey(featureName))
					matchedRtPairs.add(new RtPair(testedMap.get(featureName), keyedMap.get(featureName)));
			}

			matchedRtPairs.add(new RtPair(100.0, 100.0));

			Double diff2 = matchedRtPairs.get(0).getRt1() - matchedRtPairs.get(0).getRt2();
			if (diff2 > 0)
				matchedRtPairs.get(0).setRt1(diff2);
			else
				matchedRtPairs.get(0).setRt2(-1.0 * diff2);

			Collections.sort(matchedRtPairs, new RtPairComparator());

			latticeMapRelativeToKeyed.put(fileName, matchedRtPairs);

			for (String idx : batchFileTagMap.keySet()) {
				if (fileName.equals(batchFileTagMap.get(idx))) {
					Integer intValue = 0;
					try {
						intValue = Integer.parseInt(idx);
					} catch (Exception e) {
					}
					currentLabel = String.format("Batch%02d", intValue);
					fileTagCurrent = String.format("%02d", intValue);
					currIdx = idx;
					break;
				}
			}

			System.out.println("File : " + fileName);
			System.out.println(currentLabel + " " + targetLabel);

			List<RtPair> pairs = latticeMapRelativeToKeyed.get(fileName);
			AnchorFileWriter writer = new AnchorFileWriter(true);
			String fileNameBase = "Auto_Named_Lattice_" + fileTagCurrent + "_" + fileTagTarget + "_MP"; // (useControlsOnly.isSelected()
																										// ? "_MP" :
																										// "");

			String latticeFileName = fileNameBase + ".csv";

			writer.outputResultsToFile(latticeFileName, matchedRtPairs, outputDirectory, currentLabel, targetLabel,
					true);
			completeLatticeFileNames.put(currIdx,
					outputDirectory + BatchMatchConstants.FILE_SEPARATOR + latticeFileName);
		}
		writeFileFile(outputDirectory, completeLatticeFileNames);

		return true;
	}

	private Boolean createNamedMassMap() {

		Map<String, String> batchFileTagMap = namedFileListLoaderPanel.getBatchFileMap();
		String outputDirectory = outputDirectoryPanel.getOutputDirectoryPath();

		MetabolomicsTargetedDataLoader rtLoader = new MetabolomicsTargetedDataLoader();
		Map<String, Map<String, Double>> latticeMassAverages = new HashMap<String, Map<String, Double>>();
		for (String fileName : batchFileTagMap.values())
			latticeMassAverages.put(fileName, rtLoader.locateMassesAndLoadAverageMap(fileName, false)); // useControlsOnly.isSelected()));

		Map<String, Map<String, Double>> latticeRtAverages = new HashMap<String, Map<String, Double>>();
		for (String fileName : batchFileTagMap.values())
			latticeRtAverages.put(fileName, rtLoader.locateRTsAndLoadAverageMap(fileName, false));

		String targetKey = latticeTypePanel.getTargetSelected() == null ? "0"
				: latticeTypePanel.getTargetSelected().toString();
		; // batchLabelPanel.getIntSelected().toString();
		Integer targetKeyAsInt = latticeTypePanel.getTargetSelected();
		for (String fileName : latticeMassAverages.keySet()) {
			System.out.println("Filename : " + fileName);
		}

		Map<String, List<RtPair>> latticeMapRelativeToKeyed = new HashMap<String, List<RtPair>>();

		String targetedFile = batchFileTagMap.get(targetKey);
		String targetLabel = String.format("Batch%02d", targetKeyAsInt), currentLabel = "";
		String fileTagTarget = String.format("%02d", targetKeyAsInt);

		Map<String, Double> keyedMap = latticeMassAverages.get(targetedFile);

		Map<String, String> completeLatticeFileNames = new HashMap<String, String>();
		String fileTagCurrent = null;
		String currIdx = null;

		for (String fileName : latticeMassAverages.keySet()) {

			if (fileName.equals(targetedFile))
				continue;

			Map<String, Double> testedMap = latticeMassAverages.get(fileName);
			Map<String, Double> avgRtsForTestedMap = latticeRtAverages.get(fileName);

			List<RtPair> matchedMassPairs = new ArrayList<RtPair>();
			List<Double> avgRtsForMatchedMassPairs = new ArrayList<Double>();

			for (String featureName : keyedMap.keySet()) {
				if (testedMap.containsKey(featureName)) {
					matchedMassPairs.add(new RtPair(testedMap.get(featureName), keyedMap.get(featureName)));
					avgRtsForMatchedMassPairs.add(avgRtsForTestedMap.get(featureName));
				}
			}

			// matchedRtPairs.add(new RtPair(100.0, 100.0));

			// Double diff2 = matchedRtPairs.get(0).getRt1() -
			// matchedRtPairs.get(0).getRt2();
			// if (diff2 > 0)
			// matchedRtPairs.get(0).setRt1(diff2);
			// else
			// matchedRtPairs.get(0).setRt2(-1.0 * diff2);

			// Collections.sort(matchedRtPairs, new RtPairComparator());

			latticeMapRelativeToKeyed.put(fileName, matchedMassPairs);

			for (String idx : batchFileTagMap.keySet()) {
				if (fileName.equals(batchFileTagMap.get(idx))) {
					Integer intValue = 0;
					try {
						intValue = Integer.parseInt(idx);
					} catch (Exception e) {
					}
					currentLabel = String.format("Batch%02d", intValue);
					fileTagCurrent = String.format("%02d", intValue);
					currIdx = idx;
					break;
				}
			}

			System.out.println("File : " + fileName);
			System.out.println(currentLabel + " " + targetLabel);

			List<RtPair> pairs = latticeMapRelativeToKeyed.get(fileName);
			AnchorFileWriter writer = new AnchorFileWriter(true);
			String fileNameBase = "Mass_Named_Lattice_" + fileTagCurrent + "_" + fileTagTarget + "_MP"; // (useControlsOnly.isSelected()
																										// ? "_MP" :
																										// "");

			String latticeFileName = fileNameBase + ".csv";

			writer.outputResultsToFile(latticeFileName, matchedMassPairs, outputDirectory, currentLabel, targetLabel,
					true);
			// writer.outputResultsWithTagsToFile(latticeFileName,
			// avgRtsForMatchedMassPairs, matchedMassPairs, outputDirectory, currentLabel,
			// targetLabel, true);
			completeLatticeFileNames.put(currIdx,
					outputDirectory + BatchMatchConstants.FILE_SEPARATOR + latticeFileName);
		}
		// writeFileFile(outputDirectory, completeLatticeFileNames);

		return true;
	}

	private void writeFileFile(String outputDirectory, Map<String, String> completeLatticeFileNames) {

		List<String> indices = ListUtils.makeListFromCollection(completeLatticeFileNames.keySet());

		try {
			// if numeric, sort
			List<Integer> keys = new ArrayList<Integer>();

			for (String key : indices)
				keys.add(Integer.parseInt(key));
			Collections.sort(keys);

			indices.clear();
			for (int i = 0; i < keys.size(); i++)
				indices.add(keys.get(i).toString());
		} catch (Exception e) {
			indices = ListUtils.makeListFromCollection(completeLatticeFileNames.keySet());
			Collections.sort(indices);
		}

		List<String> batchTags = new ArrayList<String>();
		List<String> batchNames = new ArrayList<String>();

		for (int i = 0; i < indices.size(); i++) {
			String line = String.format("%s,%s", indices.get(i), completeLatticeFileNames.get(indices.get(i)));
			batchTags.add(indices.get(i));
			batchNames.add(completeLatticeFileNames.get(indices.get(i)));
		}

		AnchorFileWriter writer = new AnchorFileWriter();
		writer.outputFileListByTag(outputDirectory, batchNames, batchTags, "named_lattice_files.csv");
	}

	private Boolean createLatticeSet() {

		Map<String, String> batchFileMap = rawFileListLoaderPanel.getBatchFileMap();

		String outputDirectory = outputDirectoryPanel.getOutputDirectoryPath();

		String targetKey = latticeTypePanel.getTargetSelected() == null ? "0"
				: latticeTypePanel.getTargetSelected().toString();
		Integer poolSampleSize = poolSampleSizePanel.getIntSelected();
		String targetedFileName = batchFileMap.get(targetKey);

		Integer rtToUse = BatchMatchConstants.RT_FROM_BATCH_EXPECTED; // atchMatchConstants.RT_FROM_BINNER_NAME;
		Boolean dataWritten = false;

		List<String> outputFileNames = new ArrayList<String>();
		List<String> fileTags = new ArrayList<String>();

		List<String> keyStrings = new ArrayList<String>();

		try {
			// if numeric, sort
			List<Integer> keys = new ArrayList<Integer>();

			for (String key : batchFileMap.keySet())
				keys.add(Integer.parseInt(key));
			Collections.sort(keys);

			for (int i = 0; i < keys.size(); i++)
				keyStrings.add(keys.get(i).toString());
		} catch (Exception e) {
			for (String key : batchFileMap.keySet())
				keyStrings.add(key);
		}

		for (int i = 0; i < keyStrings.size(); i++) {

			String key = keyStrings.get(i);// .toString();
			String fileName = batchFileMap.get(key);
			if (fileName.equals(targetedFileName))
				continue;

			BatchMatchLatticeBuilder batchMatch = new BatchMatchLatticeBuilder();

			dataWritten = batchMatch.buildLatticeFile(fileName, targetedFileName, outputDirectory, key,
					targetKey.toString(), poolSampleSize, rtToUse);

			if (!dataWritten)
				return false;

			outputFileNames.add(batchMatch.getOutputFileName());
			fileTags.add(key);
		}

		AnchorFileWriter writer = new AnchorFileWriter();
		writer.outputFileListByTag(outputDirectory, outputFileNames, fileTags, fileFileListName);

		List<String> fileNames = new ArrayList<String>();
		List<String> fileTags2 = new ArrayList<String>();
		for (String key : batchFileMap.keySet()) {
			fileNames.add(batchFileMap.get(key));
			fileTags2.add(key);
		}

		String rawFileListName = "raw_data_files.csv";
		writer.outputFileListByTag(outputDirectory, fileNames, fileTags2, rawFileListName);

		JOptionPane.showMessageDialog(null, "Lattice set created and written to " + outputDirectory);

		return true;
	}

	private void printBinnerFileMap(String fileName, Map<String, String> batchFileNameMap,
			Map<String, String> batchFileOrderMap, Boolean useBinnerDir) {

		String outputFileName = null;
		if (useBinnerDir)
			outputFileName = outputDirectoryPanelBinnerData.getOutputDirectoryPath()
					+ BatchMatchConstants.FILE_SEPARATOR + fileName;
		else
			outputFileName = outputDirectoryPanel.getOutputDirectoryPath() + BatchMatchConstants.FILE_SEPARATOR
					+ fileName;

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));

			List<Integer> sortedBatchNumbers = new ArrayList<Integer>();

			for (String batchNo : batchFileNameMap.keySet())
				sortedBatchNumbers.add(Integer.parseInt(batchNo));

			Collections.sort(sortedBatchNumbers);

			for (int i = 0; i < sortedBatchNumbers.size(); i++) {
				String line = "";
				String batchKey = sortedBatchNumbers.get(i).toString();
				if (batchFileOrderMap != null) {
					line = String.format("%s,%s,%s", batchKey, batchFileNameMap.get(batchKey),
							batchFileOrderMap.get(batchKey));
				} else {
					line = String.format("%s,%s", batchKey, batchFileNameMap.get(batchKey));
				}
				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private Boolean allFilesImported() {
		return true;
	}

}
