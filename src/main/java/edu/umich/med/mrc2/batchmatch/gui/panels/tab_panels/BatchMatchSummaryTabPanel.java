////////////////////////////////////////////////////
// BatchSummaryTabPanel.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.tab_panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.AbstractStickyFileLocationPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BatchFileListLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BatchMatchCSVReportLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BatchMatchSummaryReportTypePanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.FeatureListLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.FileLayoutPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.IntegerPickerPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.IntegerRangePickerPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.OutputFileNameTagPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.StickySettingsPanel;
import edu.umich.med.mrc2.batchmatch.io.sheetreaders.BinnerInputDataHandler;
import edu.umich.med.mrc2.batchmatch.io.sheetreaders.MetabolomicsIntensityDataLoader;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchDataSetSummaryCSVWriter;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchExcelOutputContainer;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchExpandedFeatureCSVWriter;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.RecursiveLatticeFileWriter;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchMatchMatchGroupFilteringEngine;
import edu.umich.med.mrc2.batchmatch.process.orig.BatchMatchSummaryInfo;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.med.mrc2.batchmatch.utils.orig.PostProcessMergeNameExtractor;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class BatchMatchSummaryTabPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -6696965369657497804L;

	private BatchMatchCSVReportLoaderPanel batchMatchReportLoaderPanel;
	private BatchFileListLoaderPanel batchFileListLoaderPanel;
	private BatchMatchSummaryReportTypePanel reportTypePanel;
	private FileLayoutPanel fileLayoutPanel;
	private AbstractStickyFileLocationPanel outputDirectoryPanel;
	private OutputFileNameTagPanel outputFileNameTagPanel;
	private IntegerRangePickerPanel targetedMatchRangePanel;
	private IntegerPickerPanel minDesertSizePanel, recursiveLatticeBaseSizePanel, recursiveBatchTargetPanel;
	private FeatureListLoaderPanel featureListLoaderPanel;

	private SharedAnalysisSettings sharedAnalysisSettings;

	private JButton runMergeButton;
	private JPanel batchReportWrapPanel, runMergeWrapPanel, progPanel;
	private JProgressBar progBar;

	private File reportFile;

	public BatchMatchSummaryTabPanel(SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		setSharedAnalysisSettings(sharedAnalysisSettings);
		initializeStickySettings("batchMatchSummaryTab", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		outputDirectoryPanel = new AbstractStickyFileLocationPanel("Specify Output Directory",
				"binnerizedfilelist.directory") {

			@Override
			protected void updateInterfaceForNewSelection() {
				batchMatchReportLoaderPanel
						.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectory());
				batchFileListLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectory());
			}
		};

		outputDirectoryPanel.setupPanel();

		batchFileListLoaderPanel = new BatchFileListLoaderPanel() {

			@Override
			protected void updateInterfaceForNewData() {
				Map<Integer, File> batchFileMap = batchFileListLoaderPanel.grabBatchFileMap();

				MetabolomicsIntensityDataLoader intensitiesLoader = new MetabolomicsIntensityDataLoader();
				List<String> intensityHeaders = intensitiesLoader.preReadIntensityHeaders(batchFileMap);
			}
		};
		batchFileListLoaderPanel.setupPanel();
		batchFileListLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectory());

		batchMatchReportLoaderPanel = new BatchMatchCSVReportLoaderPanel("Select BatchMatch Report") {
			private static final long serialVersionUID = -7375967852968326658L;

			@Override
			protected Boolean updateInterfaceForNewFileSelection(String fileName, Integer min, Integer max) {
				targetedMatchRangePanel.setSpinnerLimits(min, max);
				return true;
			}
		};
		batchMatchReportLoaderPanel.setupPanel();
		batchMatchReportLoaderPanel.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectory());

		targetedMatchRangePanel = new IntegerRangePickerPanel("targetedMatchRange") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection, Boolean updateMin) {
				if (updateMin)
					batchMatchReportLoaderPanel.setMinTargetBatch(newSelection);
				else
					batchMatchReportLoaderPanel.setMaxTargetBatch(newSelection);

				batchMatchReportLoaderPanel.clearSelection();
				batchMatchReportLoaderPanel.clearData();
				return true;
			}
		};
		targetedMatchRangePanel.setupPanel("Report Match Groups of Size", "Match Level");

		recursiveLatticeBaseSizePanel = new IntegerPickerPanel("recursiveLatticeBaseSize") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {

				return true;
			}
		};
		recursiveLatticeBaseSizePanel.setupPanel("Batch Block Size for Recursive Lattice",
				"Base Recursive Lattice Points on Match Blocks of Size ");

		minDesertSizePanel = new IntegerPickerPanel("minDesertSize") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {

				return true;
			}
		};
		minDesertSizePanel.setupPanel("RT Deserts", "Backtrack RT Deserts Longer Than ");

		recursiveBatchTargetPanel = new IntegerPickerPanel("recuriveBatchTarget") {

			@Override
			protected Boolean updateForNewSelection(Integer newSelection) {

				return true;
			}
		};
		recursiveBatchTargetPanel.setupPanel("Target Batch for Recursive Lattice",
				"Target Batch for Recursive Lattice");

		featureListLoaderPanel = new FeatureListLoaderPanel() {

			@Override
			protected void updateInterfaceForNewData(Map<Integer, Map<Integer, List<String>>> featureListMap) {
				if (featureListMap == null)
					return;

				for (Integer batch : featureListMap.keySet()) {
					Map<Integer, List<String>> groupToFeatureMap = featureListMap.get(batch);
					for (Integer group : groupToFeatureMap.keySet()) {
						String line = String.format("%d %d %s", batch, group, groupToFeatureMap.get(group));
						System.out.println(line);
					}
				}
			}
		};
		featureListLoaderPanel.setupPanel();

		outputFileNameTagPanel = new OutputFileNameTagPanel("Summarize");
		outputFileNameTagPanel.setupPanel();

		reportTypePanel = new BatchMatchSummaryReportTypePanel(true) {

			@Override
			public void updateForReportTypeChange() {
				batchFileListLoaderPanel
						.setVisible(reportTypePanel.createCollapsed() || reportTypePanel.filterBinnerInput());
				fileLayoutPanel.setVisible(reportTypePanel.createCollapsed());
				targetedMatchRangePanel.setVisible(!reportTypePanel.createRecursiveLattice()
						&& !reportTypePanel.createQCReport() && !reportTypePanel.filterBinnerInput());
				outputFileNameTagPanel.setVisible(!reportTypePanel.createRecursiveLattice()
						&& !reportTypePanel.createMatchSummary() && !reportTypePanel.filterBinnerInput());
				recursiveLatticeBaseSizePanel
						.setVisible(reportTypePanel.createRecursiveLattice() && !reportTypePanel.filterBinnerInput());
				minDesertSizePanel
						.setVisible(reportTypePanel.createMatchSummary() && !reportTypePanel.filterBinnerInput());
				recursiveBatchTargetPanel
						.setVisible(reportTypePanel.createRecursiveLattice() && !reportTypePanel.filterBinnerInput());
				batchMatchReportLoaderPanel.setVisible(true); // !reportTypePanel.createQCReport() &&
																// !reportTypePanel.filterBinnerInput());
				featureListLoaderPanel.setVisible(reportTypePanel.createQCReport());
			}
		};
		reportTypePanel.setupPanel();

		setupRunReportPanel();
		setupFileLayoutPanel();

		batchFileListLoaderPanel.setVisible(reportTypePanel.createCollapsed() || reportTypePanel.filterBinnerInput());
		fileLayoutPanel.setVisible(reportTypePanel.createCollapsed());
		targetedMatchRangePanel
				.setVisible(!reportTypePanel.createRecursiveLattice() && !reportTypePanel.filterBinnerInput());
		outputFileNameTagPanel.setVisible(!reportTypePanel.createRecursiveLattice()
				&& !reportTypePanel.createMatchSummary() && !reportTypePanel.filterBinnerInput());
		recursiveLatticeBaseSizePanel.setVisible(this.reportTypePanel.createRecursiveLattice());
		minDesertSizePanel.setVisible(reportTypePanel.createMatchSummary());
		recursiveBatchTargetPanel.setVisible(reportTypePanel.createRecursiveLattice());
		batchMatchReportLoaderPanel.setVisible(!reportTypePanel.filterBinnerInput());
		featureListLoaderPanel.setVisible(reportTypePanel.createQCReport());

		progPanel = new JPanel();
		progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.X_AXIS));
		progPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		progBar = new JProgressBar(0, 500);
		progBar.setIndeterminate(true);
		progPanel.add(progBar);
		progPanel.setVisible(false);

		add(Box.createVerticalStrut(1));
		batchReportWrapPanel = new JPanel();

		batchReportWrapPanel.setLayout(new BoxLayout(batchReportWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder overallPostProcessingWrapBorder = BorderFactory.createTitledBorder("Create Batch Reports");
		overallPostProcessingWrapBorder.setTitleFont(boldFontForTitlePanel(overallPostProcessingWrapBorder, false));
		overallPostProcessingWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		batchReportWrapPanel.setBorder(overallPostProcessingWrapBorder);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(outputDirectoryPanel);
		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(reportTypePanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(batchMatchReportLoaderPanel);
		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(batchFileListLoaderPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(targetedMatchRangePanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(recursiveLatticeBaseSizePanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(recursiveBatchTargetPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(minDesertSizePanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(fileLayoutPanel);
		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(outputFileNameTagPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(featureListLoaderPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(runMergeWrapPanel);

		batchReportWrapPanel.add(progPanel);
		batchReportWrapPanel.add(Box.createVerticalStrut(1));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(1));
		add(batchReportWrapPanel);
		add(Box.createVerticalStrut(1));
		LayoutUtils.addBlankLines(this, 5);
	}

	private void setupRunReportPanel() {
		JPanel runMergePanel = new JPanel();
		runMergePanel.setLayout(new BoxLayout(runMergePanel, BoxLayout.X_AXIS));

		JCheckBox makeSummaryCheckBox = makeStickyCheckBox("Disambiguate Groups", "addSummaryTab", false, true);

		runMergeButton = new JButton("Create Report");
		runMergeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!allFilesImported()) {
					JOptionPane.showMessageDialog(null, "Please import data files before running     "
							+ BinnerConstants.LINE_SEPARATOR + "a report.    ");
					return;
				}
				runReportAndOutputResultsInWorkerThread();
			}
		});

		runMergePanel = new JPanel();
		runMergePanel.add(Box.createHorizontalGlue());
		runMergePanel.add(runMergeButton);
		runMergePanel.add(Box.createHorizontalGlue());
		runMergePanel.add(makeSummaryCheckBox);
		runMergePanel.add(Box.createHorizontalGlue());

		runMergePanel.setLayout(new BoxLayout(runMergePanel, BoxLayout.X_AXIS));

		runMergeWrapPanel = new JPanel();
		runMergeWrapPanel.setLayout(new BoxLayout(runMergeWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder runConvertWrapBorder = BorderFactory.createTitledBorder("Create Report ");
		runConvertWrapBorder.setTitleFont(boldFontForTitlePanel(runConvertWrapBorder, false));
		runConvertWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		runMergeWrapPanel.setBorder(runConvertWrapBorder);
		runMergeWrapPanel.add(runMergePanel);
	}

	private void setupFileLayoutPanel() {

		fileLayoutPanel = new FileLayoutPanel() {

			private static final long serialVersionUID = -264970691145177455L;

			@Override
			public List<String> grabColumnList() {

				List<String> rawList = batchFileListLoaderPanel.grabIntensityHeaders();

				List<String> orderedList = new ArrayList<String>();
				for (int i = 0; i < rawList.size(); i++)
					if (!PostProcessMergeNameExtractor.isSampleName(rawList.get(i)))
						orderedList.add(rawList.get(i));

				for (int i = 0; i < rawList.size(); i++)
					if (PostProcessMergeNameExtractor.isSampleName(rawList.get(i)))
						orderedList.add(rawList.get(i));

				return orderedList;
			}

			@Override
			public Boolean dataFullySpecified() {
				return dataFullySpecifiedOuter();
			}
		};
		fileLayoutPanel.setupPanel();
	}

	private Boolean dataFullySpecifiedOuter() {

		return batchMatchReportLoaderPanel.getLoadedData() != null || reportTypePanel.filterBinnerInput(); // binnerBatch2LoaderPanel.getLoadedData()
																											// != null;
	}

	private void runReportAndOutputResultsInWorkerThread() {

		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			public Boolean doInBackground() {

				runMergeWrapPanel.setVisible(false);

				progPanel.setVisible(true);
				if (!dataFullySpecifiedOuter())
					return false;

				try {
					runReport();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error during merge operation." + e.getMessage());

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
						JOptionPane.showMessageDialog(null, "Error during merge operation");
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
					System.err.println("Error while merging data: " + ee.getMessage());
				} catch (Exception ee) {
					String why = null;
					Throwable cause = ee.getCause();
					if (cause != null) {
						why = cause.getMessage();
					} else {
						why = ee.getMessage();
					}
					System.err.println("Error details : " + ee.getMessage());
				}
				runMergeWrapPanel.setVisible(true);
			}
		};
		worker.execute();
	}

	private void createMatchSummary() {
		
		BatchMatchDataSetSummaryCSVWriter writer = new BatchMatchDataSetSummaryCSVWriter();
		//	String fileName = batchMatchReportLoaderPanel.getFileName(); // updateControlsForNewFileSelection(fileName);
		// PostProcessDataSet data = batchMatchReportLoaderPanel.getLoadedData();
		
		String baseName = FileNameUtils.getBaseName(
				batchMatchReportLoaderPanel.getCurrentBatchFile().toPath());
		String summaryFileName = "batch_freqs_" + baseName.toLowerCase() + ".csv";
		
		File summaryOutputFile = Paths.get(outputDirectoryPanel.getOutputDirectory().getAbsolutePath(), 
				summaryFileName).toFile();
		writer.writeSummaryToFile(
				batchMatchReportLoaderPanel.getLoadedData(), 
				summaryOutputFile,
				minDesertSizePanel.getIntSelected());
	}

	private void filterBinnerFiles() {
		Map<Integer, File> batchFileMap = batchFileListLoaderPanel.grabBatchFileMap();

		for (Integer batch : batchFileMap.keySet()) {
			
			File binnerFile = batchFileMap.get(batch);

			BinnerInputDataHandler handler = new BinnerInputDataHandler();
			PostProcessDataSet data = handler.readFeatureData(binnerFile);
			data.filterPutativeSalts(50.0, .5, 500.0, 5.0);
			data.filterDuplicates(batch);
			String filteredFileName = "SaltDupFiltered_" + batch + ".csv";
			handler.writeDataInBinnerInputFormat(data, outputDirectoryPanel.getOutputDirectory(), filteredFileName);
		}
	}

	private void ungroupAmbiguousFeatures() throws Exception {

		Map<Integer, Map<Integer, List<String>>> featuresToUngroup = featureListLoaderPanel.buildFeatureListMap();

		PostProcessDataSet data = batchMatchReportLoaderPanel.getLoadedData();
		for (FeatureFromFile f : data.getFeatures()) {
			if (f.getRedundancyGroup() == null)
				continue;
			Integer matchGroup = f.getRedundancyGroup();
			Integer batch = f.getBatchIdx();
			String featureName = StringUtils.removeSpaces(f.getName());

			if (featuresToUngroup.containsKey(batch)) {
				if (featuresToUngroup.get(batch).containsKey(matchGroup)) {
					if (featuresToUngroup.get(batch).get(matchGroup) != null) {
						List<String> featuresToSearch = featuresToUngroup.get(batch).get(matchGroup);
						if (featuresToSearch.contains(featureName)) {
							f.setRedundancyGroup(null);
							System.out.println("Ungrouping feature " + f.getName() + " in batch " + batch
									+ " with match group " + matchGroup);
						}
					}
				}
			}
		}
		writeMergedData(data);
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
		
		File outputFile = Paths.get(outputDir.getAbsolutePath(), fileNameTag).toFile();

//		String completeFileName = fileNameTag;
//		if (outputDirectoryPanel.getOutputDirectory() != null)
//			completeFileName = outputDirectoryPanel.getOutputDirectory() + BinnerConstants.FILE_SEPARATOR
//					+ fileNameTag;

		BatchMatchExcelOutputContainer outputContainer = 
				new BatchMatchExcelOutputContainer(outputFile);

		reportFile = outputContainer.grabIncrementedOutputFile();
		try(FileOutputStream output = new FileOutputStream(reportFile)) {
			outputContainer.writeBatchMatchDataSet(mergeData, null, output, false);			
		} catch (Exception f) {
			f.printStackTrace();
		}
		BatchMatchExpandedFeatureCSVWriter csvWriter = new BatchMatchExpandedFeatureCSVWriter(null);
		csvWriter.setDerivedColNameMapping(mergeData.getDerivedNameToHeaderMap());
		File csvReportFile = csvWriter.grabCSVforXLSXName(reportFile);
		csvWriter.writeExpandedFeatureSheet(csvReportFile, mergeData);
	}

	private void filterDuplicates() {
		
		Map<Integer, File> batchFileMap = batchFileListLoaderPanel.grabBatchFileMap();

		for (Integer batch : batchFileMap.keySet()) {
			
			File binnerFile = batchFileMap.get(batch);
			BinnerInputDataHandler handler = new BinnerInputDataHandler();
			PostProcessDataSet data = handler.readFeatureData(binnerFile);
			// data.filterPutativeSalts(50.0, .5, 500.0, 5.0);
			data.filterDuplicates(batch);
			String filteredFileName = "DupFiltered_" + batch + ".csv";
			handler.writeDataInBinnerInputFormat(data, outputDirectoryPanel.getOutputDirectory(), filteredFileName);
		}
	}

	private void createRecursiveLattices() {
		
		RecursiveLatticeFileWriter writer = new RecursiveLatticeFileWriter(
				this.recursiveLatticeBaseSizePanel.getIntSelected());
		PostProcessDataSet data = batchMatchReportLoaderPanel.getLoadedData();
		// recursive rt lattice
		writer.writeLatticeSetRelativeTo(recursiveBatchTargetPanel.getIntSelected(),
				outputDirectoryPanel.getOutputDirectory(), data, false);
		// recursive mass lattice
		writer.writeLatticeSetRelativeTo(recursiveBatchTargetPanel.getIntSelected(),
				outputDirectoryPanel.getOutputDirectory(), data, true);
	}

	private void loadCollapseAndWriteData(PostProcessDataSet dataSet) {

		Map<Integer, File> batchFileMap = batchFileListLoaderPanel.grabBatchFileMap();
		MetabolomicsIntensityDataLoader intensitiesLoader = new MetabolomicsIntensityDataLoader();
		intensitiesLoader.completeDataSetFromBatchFilesList(
				dataSet, batchFileMap, fileLayoutPanel.getControlLabels());
		
		writeCompleteCollapsedDataSet(dataSet, batchFileMap); // Map<Integer, String> batchFileMap);
	}

	private void writeCompleteCollapsedDataSet(
			PostProcessDataSet dataSet, 
			Map<Integer, File> batchFileMap) {
		
		File outputDir = outputDirectoryPanel.getOutputDirectory();
		if(outputDir == null || !outputDir.exists()) {
			
			JOptionPane.showMessageDialog(null, "Invalid or missing output directory!");
			return;
		}
		String fileNameTag = "";
		if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
			fileNameTag = outputFileNameTagPanel.getFileNameTag() + ".xlsx";

//		String completeFileName = fileNameTag;
//		if (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectory()))
//			completeFileName = outputDirectoryPanel.getOutputDirectory() + BinnerConstants.FILE_SEPARATOR
//					+ fileNameTag;

		File outputFile = Paths.get(outputDir.getAbsolutePath(), fileNameTag).toFile();		
		BatchMatchSummaryInfo summaryInfo = initializeSummaryInfo(dataSet, batchFileMap);
		BatchMatchExcelOutputContainer outputContainer = new BatchMatchExcelOutputContainer(outputFile);
		reportFile = outputContainer.grabIncrementedOutputFile();
		try(FileOutputStream output = new FileOutputStream(reportFile)) {			
			outputContainer.writeBatchMatchDataSet(dataSet, summaryInfo, output, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fileNameTag = "";
		if (!StringUtils.isEmptyOrNull(outputFileNameTagPanel.getFileNameTag()))
			fileNameTag = outputFileNameTagPanel.getFileNameTag() + "_grid" + ".xlsx";

//		completeFileName = fileNameTag;
//		if (!StringUtils.isEmptyOrNull(outputDirectoryPanel.getOutputDirectory()))
//			completeFileName = outputDirectoryPanel.getOutputDirectory() + BinnerConstants.FILE_SEPARATOR
//					+ fileNameTag;
		
		outputFile = Paths.get(outputDir.getAbsolutePath(), fileNameTag).toFile();
		BatchMatchExcelOutputContainer outputContainer2 = new BatchMatchExcelOutputContainer(outputFile);
		reportFile = outputContainer2.grabIncrementedOutputFile();
		try(FileOutputStream output = new FileOutputStream(reportFile)) {
			outputContainer2.writeBatchMatchDataSet(dataSet, summaryInfo, output, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BatchMatchSummaryInfo initializeSummaryInfo(
			PostProcessDataSet dataSet, 
			Map<Integer, File> batchFileMap) {
		
		BatchMatchSummaryInfo summaryInfo = new BatchMatchSummaryInfo();

		summaryInfo.setTitleWithVersion("BatchMatch v" + BatchMatchConstants.VERSION);
		summaryInfo.setMergeFilePath(batchMatchReportLoaderPanel.getFileFullPath());
		summaryInfo.setRawDataFileListPath(batchFileListLoaderPanel.getFileName());

		summaryInfo.setSampleNames(dataSet.getOrderedIntensityHeaders());
		summaryInfo.setnSamples(dataSet.getOrderedIntensityHeaders().size());

		summaryInfo.setnTotalFilteredFeatures(dataSet.getNTotalFilteredFeatures());
		summaryInfo.setnTotalRawFeatures(dataSet.getNTotalRawFeatures());

		summaryInfo.setNFilteredFeaturesByBatch(dataSet.getZeroPaddedFilteredFeaturesByBatchCts());
		summaryInfo.setnRawFeaturesByBatch(dataSet.getZeroPaddedRawFeaturesByBatchCts());

		summaryInfo.setBatchFileMap(batchFileMap);
		summaryInfo.setnBatches(dataSet.getMaxBatch());
		summaryInfo.setNBatchSamples(dataSet.getSampleCtsByBatch());

		return summaryInfo;
	}

	private void runReport() throws Exception {

		String confirmDirMsg = "Are you sure that you want to write your report to "
				+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
				+ outputDirectoryPanel.getOutputDirectory().getAbsolutePath() + "?" + BinnerConstants.LINE_SEPARATOR;

		int answer = JOptionPane.showConfirmDialog(null, confirmDirMsg, "Confirm report location ",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.NO_OPTION)
			return;

		if (reportTypePanel.createMatchSummary()) {
			// createMatchSummary();
			// PostProcessDataSet data = batchMatchReportLoaderPanel.getLoadedData();
			// BatchMatchMatchGroupFilteringEngine.disambiguateGroups(data);
			// System.out.println("And again " + "\n\n\n");

			// BatchMatchMatchGroupFilteringEngine.disambiguateGroupsByDisparateMissingness(data,
			// true);

			// BatchMatchExpandedFeatureCSVWriter csvWriter = new
			// BatchMatchExpandedFeatureCSVWriter(null);
			// csvWriter.setDerivedColNameMapping(data.getDerivedNameToHeaderMap());

			// String csvReportName = "test_ambiguation.csv";
			// //csvWriter.grabCSVforXLSXName(reportFile);
			// csvWriter.writeExpandedFeatureSheet(csvReportName, data);

			BatchMatchMatchGroupFilteringEngine
					.disambiguateGroupsByCurveProximity(batchMatchReportLoaderPanel.getLoadedData());
			return;
		}
		if (reportTypePanel.createQCReport()) {
			ungroupAmbiguousFeatures();
		}
		// if (false) { // reportTypePanel.createMetaReport()
		// 		BatchMatchResultsMetaReporter reporter = new BatchMatchResultsMetaReporter();
		// }
		if (reportTypePanel.filterBinnerInput()) {
			filterBinnerFiles();
			// filterDuplicates();
			return;
		}
		if (reportTypePanel.createCollapsed()) {
			loadCollapseAndWriteData(batchMatchReportLoaderPanel.getLoadedData());
			return;
		}
		if (reportTypePanel.createRecursiveLattice())
			createRecursiveLattices();
	}

	private Boolean allFilesImported() {
		if (batchMatchReportLoaderPanel.getLoadedData() == null && !reportTypePanel.filterBinnerInput())
			return false;
		else if (reportTypePanel.createCollapsed())
			return batchFileListLoaderPanel.hasFileSelection();

		return true;
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}
}
