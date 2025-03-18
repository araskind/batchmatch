////////////////////////////////////////////////////
// BinnerBatchFileLoaderPanel.java
// Written by Jan Wigginton September 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.data.TextFile;
import edu.umich.mrc2.batchmatch.data.comparators.FeatureByMassAndRtComparator;
import edu.umich.mrc2.batchmatch.io.sheetreaders.BatchMatchTextToDataSetReader;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.process.PostProcessDataSet;
import edu.umich.mrc2.batchmatch.utils.BinnerFileUtils;
import edu.umich.mrc2.batchmatch.utils.StringUtils;

public abstract class BatchMatchCSVReportLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	private JComboBox<File> inputFileComboBox;
	private JPanel inputFileWrapPanel, progPanel;
	private JProgressBar progBar;
	private JButton inputFileButton;
	private TitledBorder inputFileWrapBorder;

	private Boolean rsdPctMissingPreCalculated = false;
	private File currentBatchFile;
	private PostProcessDataSet loadedData = null;
	private String panelTitle = "Select Batch Data File (must be .csv)";
	private String fileExtension = "csv", fileDescription = "Comma-Separated Value Files";
	private Integer minTargetBatch = -1, maxTargetBatch = -1;

	private String initialDirectoryForChooser = null;
	private Boolean interpretMiscCols = true;

	public BatchMatchCSVReportLoaderPanel() {
		this("");
	}

	public BatchMatchCSVReportLoaderPanel(String title) {
		super();
		panelTitle = title;
	}

	public BatchMatchCSVReportLoaderPanel(String title, String extension, String description) {
		super();
		panelTitle = title;
		fileExtension = extension;
		fileDescription = description;
	}

	public void setupPanel() {

		initializeArrays();
		setupInputFilePanel();

		progPanel = new JPanel();
		progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.X_AXIS));
		progPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		progBar = new JProgressBar(0, 500);
		progBar.setIndeterminate(true);
		progPanel.add(progBar);
		progPanel.setVisible(false);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(2));
		add(inputFileWrapPanel);
		add(Box.createVerticalStrut(2));
		add(progPanel);
		add(Box.createVerticalStrut(2));

		setupTextFieldListeners();
	}

	private void setupInputFilePanel() {
		JPanel inputFilePanel = new JPanel();
		inputFilePanel.setLayout(new BoxLayout(inputFilePanel, BoxLayout.X_AXIS));
		inputFilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputFileComboBox = new JComboBox<File>();
		inputFileComboBox.setEditable(false);
		inputFileComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		inputFileComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ("comboBoxChanged".equals(ae.getActionCommand())) {
				}
			}
		});

		inputFileButton = new JButton("Browse...");
		inputFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = BinnerFileUtils.getFile("Select Metabolite Data Input File", BinnerFileUtils.LOAD,
						fileExtension, fileDescription, initialDirectoryForChooser);

				if (file != null) {

					inputFileComboBox.removeAllItems();
					inputFileComboBox.insertItemAt(file, 0);
					inputFileComboBox.setSelectedIndex(0);
					currentBatchFile = (File) inputFileComboBox.getSelectedItem();

					for (int i = 0; i < 1000; i++)
						i++;
					/*
					 * if (!screenFileNames(fileData.getName())) { currentBatchFile = new
					 * File(); inputFileComboBox.removeAllItems(); File = new File();
					 * inputFileComboBox.insertItemAt(fileData, 0);
					 * inputFileComboBox.setSelectedIndex(0); return; }
					 */
					loadDataInWorkerThread(true, false, null);
					// updateInterfaceForNewFileSelection(fileData.getName(), 0,
					// loadedData.getMaxPossibleMatchCt());
				}
			}
		});

		inputFilePanel.add(Box.createHorizontalStrut(8));
		inputFilePanel.add(inputFileComboBox);
		inputFilePanel.add(Box.createHorizontalStrut(8));
		inputFilePanel.add(inputFileButton);
		inputFilePanel.add(Box.createHorizontalStrut(8));

		inputFileWrapPanel = new JPanel();
		inputFileWrapPanel.setLayout(new BoxLayout(inputFileWrapPanel, BoxLayout.Y_AXIS));
		inputFileWrapBorder = BorderFactory.createTitledBorder(getTitle());
		inputFileWrapBorder.setTitleFont(boldFontForTitlePanel(inputFileWrapBorder, false));
		inputFileWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		inputFileWrapPanel.setBorder(inputFileWrapBorder);
		inputFileWrapPanel.add(inputFilePanel);
		// inputFileWrapPanel.add(inputFileProgPanel);
	}

	private void loadDataInWorkerThread(final Boolean molecularIon, final Boolean unAnnotated,
			final File existingFile) {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			public Boolean doInBackground() {
				loadFeatureData(existingFile);
				return true;
			}

			@Override
			public void done() {
				try {
					if (!get()) {
						JOptionPane.showMessageDialog(null, "Error during data load  ");
					} else {
						// BatchMatchDataSetSummaryWriter writer = new BatchMatchDataSetSummaryWriter();
						// writer.writeSummaryToFile(getLoadedData(), "batch_freqs.csv");

					}
					progPanel.setVisible(false);
				} catch (java.lang.OutOfMemoryError e) {
					JOptionPane.showMessageDialog(null,
							"BatchMatch is having trouble opening your file. If your file is larger than 40M and memory limits on your computer are  "
									+ BinnerConstants.LINE_SEPARATOR
									+ "not explicitly set to handle high memory loads,  the spreadsheet library may have trouble opening your file. In this  "
									+ BinnerConstants.LINE_SEPARATOR
									+ "circumstance, deleting everything but the \'Unannotated Feature\' and \'Principal Ion\' tabs from your Binner  "
									+ BinnerConstants.LINE_SEPARATOR
									+ "report should fix the problem. If you've already reduced your file size,  resetting memory allocations on your computer  "
									+ BinnerConstants.LINE_SEPARATOR
									+ "will help. If that is not possible, you may wish to convert to csv and use the PostProcess csv loader.");

					progPanel.setVisible(false);

				} catch (InterruptedException ignore) {
					JOptionPane.showMessageDialog(null, "Error during data load   " + ignore.getMessage());
					progPanel.setVisible(false);
				} catch (ExecutionException ee) {
					String why = null;
					Throwable cause = ee.getCause();
					if (cause != null) {
						why = cause.getMessage();
					} else {
						why = ee.getMessage();
					}
					JOptionPane.showMessageDialog(null, "Error during data load   " + ee.getMessage());
					ee.printStackTrace();
					progPanel.setVisible(false);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error during data load   " + e.getMessage());
					progPanel.setVisible(false);
				}
			}
		};
		worker.execute();
	}

	// Initializing tag map
	public void loadFeatureData(File existingFile2) {

		progPanel.setVisible(true);

		File inputFile = new File(currentBatchFile.getPath());

		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return; // false;
		}

		System.out.println("Reading feature data from " + currentBatchFile.getPath());

		BatchMatchTextToDataSetReader processor = new BatchMatchTextToDataSetReader();
		try {
			processor.setReadMiscCols(interpretMiscCols);
			this.loadedData = processor.readFeatureData(rawTextData, false, getMinTargetBatch(), getMaxTargetBatch());
		} catch (Exception e) {
			progPanel.setVisible(false);
			JOptionPane.showMessageDialog(null, "Analysis aborted due to file read error");
			return;
		}
		if (loadedData != null)
			Collections.sort(loadedData.getFeatures(), new FeatureByMassAndRtComparator());
	}

	public List<String> getAvailableSampleHeaders() {
		List<String> rawList = this.loadedData.getOrderedIntensityHeaders();

		List<String> filteredList = new ArrayList<String>();
		for (int i = 0; i < rawList.size(); i++)
			if (!StringUtils.isEmptyOrNull(rawList.get(i).trim()))
				filteredList.add(rawList.get(i));

		return filteredList;
	}

	public String getFileName() {
		return currentBatchFile != null ? currentBatchFile.getName() : "";
	}

	public String getFileFullPath() {
		return currentBatchFile != null ? currentBatchFile.getPath() : "";
	}

	public String getTitle() {
		return panelTitle;
	}

	public void setTitle(String title) {
		panelTitle = title;
		inputFileWrapBorder.setTitle(title);

	}

	public PostProcessDataSet getLoadedData() {
		return this.loadedData;
	}

	public void clearSelection() {
		this.inputFileComboBox.removeAllItems();
	}

	public void clearData() {
		this.loadedData = null;
	}

	public Boolean getRsdPctMissingPreCalculated() {
		return rsdPctMissingPreCalculated;
	}

	public void setRsdPctMissingPreCalculated(Boolean rsdPctMissingPreCalculated) {
		this.rsdPctMissingPreCalculated = rsdPctMissingPreCalculated;
	}

	public int getMinTargetBatch() {
		return minTargetBatch;
	}

	public void setMinTargetBatch(int targetBatch) {
		this.minTargetBatch = targetBatch;
	}

	// public Map<Integer, Map<Integer, Integer>> grabFreshData(String fileName) {
	// this.loadedData = null;
	// return readFeatureData(fileName);
	// }

	public int getMaxTargetBatch() {
		return this.maxTargetBatch;
	}

	public void setInitialDirectoryForChooser(String dir) {
		this.initialDirectoryForChooser = dir;
	}

	public void setMaxTargetBatch(int targetBatch) {
		this.maxTargetBatch = targetBatch;
	}

	public void setInterpretMiscCols(Boolean ifRead) {
		this.interpretMiscCols = ifRead;
	}

	protected abstract Boolean updateInterfaceForNewFileSelection(String fileName, Integer minBatchNo,
			Integer maxBatchNo);
	// protected abstract Boolean screenFileNames(String fileName);

}
