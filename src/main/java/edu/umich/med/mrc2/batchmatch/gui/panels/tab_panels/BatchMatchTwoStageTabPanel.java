////////////////////////////////////////////////////
// BatchSummaryTabPanel.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.tab_panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureInfoForMatchGroupMapping;
import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.gui.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.AbstractStickyFileLocationPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BatchMatchCSVReportLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.BatchMatchTwoStageReportTypePanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.DataSetMappingLoaderPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.OutputFileNameTagPanel;
import edu.umich.med.mrc2.batchmatch.gui.panels.orig.StickySettingsPanel;
import edu.umich.med.mrc2.batchmatch.io.sheetwriters.BatchMatchExpandedFeatureCSVWriter;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.med.mrc2.batchmatch.process.orig.TwoStageAnalysisEngine;

public class BatchMatchTwoStageTabPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -6696965369657497804L;

	private BatchMatchCSVReportLoaderPanel batchMatchReportToConvertPanel, pairwiseReportToMapPanel;
	private DataSetMappingLoaderPanel dataSetMappingLoaderPanel;

	private AbstractStickyFileLocationPanel outputDirectoryPanel;
	private OutputFileNameTagPanel outputFileNameTagPanel;
	private BatchMatchTwoStageReportTypePanel reportTypePanel;

	private SharedAnalysisSettings sharedAnalysisSettings;

	private JButton runMergeButton;
	private JPanel batchReportWrapPanel, runMergeWrapPanel, progPanel;
	private JProgressBar progBar;

	private String reportFileName;

	public BatchMatchTwoStageTabPanel(SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		setSharedAnalysisSettings(sharedAnalysisSettings);
		initializeStickySettings("batchTwoStageSummaryTab", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		outputDirectoryPanel = new AbstractStickyFileLocationPanel("Specify Output Directory",
				"binnerizedfilelist.directory") {

			@Override
			protected void updateInterfaceForNewSelection() {
				batchMatchReportToConvertPanel
						.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());
				// batchFileListLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectoryPath());
			}
		};

		outputDirectoryPanel.setupPanel();

		batchMatchReportToConvertPanel = new BatchMatchCSVReportLoaderPanel(
				"Select Set 2 (Source) BatchMatch Report To Convert") {
			private static final long serialVersionUID = -7375967852968326658L;

			@Override
			protected Boolean updateInterfaceForNewFileSelection(String fileName, Integer min, Integer max) {

				return true;
			}
		};
		batchMatchReportToConvertPanel.setupPanel();
		batchMatchReportToConvertPanel.setMinTargetBatch(1);
		batchMatchReportToConvertPanel.setMaxTargetBatch(5);
		batchMatchReportToConvertPanel.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());

		pairwiseReportToMapPanel = new BatchMatchCSVReportLoaderPanel(
				"Select Pair/Set-wise BatchMatch Report To Derive Batch Set Mapping") {
			private static final long serialVersionUID = -7375967852968326658L;

			@Override
			protected Boolean updateInterfaceForNewFileSelection(String fileName, Integer min, Integer max) {

				return true;
			}
		};
		pairwiseReportToMapPanel.setupPanel();
		pairwiseReportToMapPanel.setInitialDirectoryForChooser(outputDirectoryPanel.getOutputDirectoryPath());
		pairwiseReportToMapPanel.setMinTargetBatch(1);
		pairwiseReportToMapPanel.setMaxTargetBatch(2);

		dataSetMappingLoaderPanel = new DataSetMappingLoaderPanel("Select Data Set Mapping", null);
		dataSetMappingLoaderPanel.setupPanel();
		dataSetMappingLoaderPanel.setInitialDirectory(outputDirectoryPanel.getOutputDirectoryPath());

		outputFileNameTagPanel = new OutputFileNameTagPanel("Summarize");
		outputFileNameTagPanel.setupPanel();

		reportTypePanel = new BatchMatchTwoStageReportTypePanel(true) {

			@Override
			public void updateForReportTypeChange() {
				pairwiseReportToMapPanel
						.setVisible(reportTypePanel.createDataSetMapping() || reportTypePanel.createMockData()); // &&
																													// !reportTypePanel.createMatchSummary()
																													// &&
																													// !reportTypePanel.filterBinnerInput());
				batchMatchReportToConvertPanel.setVisible(reportTypePanel.createProjectedData()); // !reportTypePanel.createDataSetMapping());//
																									// &&
																									// !reportTypePanel.filterBinnerInput());
				dataSetMappingLoaderPanel.setVisible(reportTypePanel.createProjectedData());
			}
		};
		reportTypePanel.setupPanel();

		setupRunReportPanel();

		outputFileNameTagPanel.setVisible(!reportTypePanel.createDataSetMapping()); // &&
																					// !reportTypePanel.createMatchSummary()
																					// &&
																					// !reportTypePanel.filterBinnerInput());
		batchMatchReportToConvertPanel.setVisible(true); // !reportTypePanel.createQCReport() &&
															// !reportTypePanel.filterBinnerInput());

		progPanel = new JPanel();
		progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.X_AXIS));
		progPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		progBar = new JProgressBar(0, 500);
		progBar.setIndeterminate(true);
		progPanel.add(progBar);
		progPanel.setVisible(false);

		pairwiseReportToMapPanel.setVisible(reportTypePanel.createDataSetMapping()); // &&
																						// !reportTypePanel.createMatchSummary()
																						// &&
																						// !reportTypePanel.filterBinnerInput());
		batchMatchReportToConvertPanel.setVisible(reportTypePanel.createProjectedData()); // !reportTypePanel.createDataSetMapping());//
																							// &&
																							// !reportTypePanel.filterBinnerInput());
		dataSetMappingLoaderPanel.setVisible(reportTypePanel.createProjectedData());

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
		batchReportWrapPanel.add(batchMatchReportToConvertPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(pairwiseReportToMapPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(dataSetMappingLoaderPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(outputFileNameTagPanel);

		batchReportWrapPanel.add(Box.createVerticalStrut(1));
		batchReportWrapPanel.add(runMergeWrapPanel);

		batchReportWrapPanel.add(progPanel);
		batchReportWrapPanel.add(Box.createVerticalStrut(1));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(1));
		add(batchReportWrapPanel);
		add(Box.createVerticalStrut(1));
		LayoutUtils.addBlankLines(this, 50);
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

	private Boolean dataFullySpecifiedOuter() {

		return true; // batchMatchReportToConvertPanel.getLoadedData() != null; // ||
						// reportTypePanel.filterBinnerInput(); //
						// binnerBatch2LoaderPanel.getLoadedData() != null;
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
										+ BinnerConstants.LINE_SEPARATOR + reportFileName
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

	private void runReport() throws Exception {

		String confirmDirMsg = "Are you sure that you want to write your report to "
				+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
				+ outputDirectoryPanel.getOutputDirectoryPath() + "?" + BinnerConstants.LINE_SEPARATOR;

		int answer = JOptionPane.showConfirmDialog(null, confirmDirMsg, "Confirm report location ",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.NO_OPTION)
			return;

		if (reportTypePanel.createProjectedData()) {
			// createMatchSummary();
			// Load full data
			PostProcessDataSet dataToMap = batchMatchReportToConvertPanel.getLoadedData();

			Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> dataSetMapping = this.dataSetMappingLoaderPanel
					.grabFreshDataSetMapping();

			TwoStageAnalysisEngine.translateFromDataSetMapping(dataSetMapping, dataToMap);

			String outputFileName = outputDirectoryPanel.getOutputDirectoryPath() + BatchMatchConstants.FILE_SEPARATOR
					+ outputFileNameTagPanel.getFileNameTag() + ".csv";
			BatchMatchExpandedFeatureCSVWriter csvWriter = new BatchMatchExpandedFeatureCSVWriter(null);
			csvWriter.writeExpandedFeatureSheet(outputFileName, dataToMap);
		}

		if (reportTypePanel.createDataSetMapping()) {
			pairwiseReportToMapPanel.setInterpretMiscCols(true);
			pairwiseReportToMapPanel.getFileFullPath();
			PostProcessDataSet data = pairwiseReportToMapPanel.getLoadedData(); // (pairwiseReportToMapPanel.getFileFullPath());
			String mapFileName = outputDirectoryPanel.getOutputDirectoryPath() + BatchMatchConstants.FILE_SEPARATOR
					+ outputFileNameTagPanel.getFileNameTag() + ".csv";
			TwoStageAnalysisEngine.createStageTwoDataSetMapping(data, mapFileName);
		}

		// disambiguate

		if (reportTypePanel.createMockData()) {
			PostProcessDataSet data = pairwiseReportToMapPanel.getLoadedData();
			// first identify complete match groups

			// collapse by creating averages

			// write with blank data
		}
	}

	private Boolean allFilesImported() {
		if (batchMatchReportToConvertPanel.getLoadedData() == null) // && !reportTypePanel.filterBinnerInput())
			return true;
		// else if (reportTypePanel.createCollapsed())
		// return batchFileListLoaderPanel.hasFileSelection();

		return true;
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}

}
