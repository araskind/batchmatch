////////////////////////////////////////////////////
// FileListLoaderDisplayPanel.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutGrid;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutItem;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.utils.orig.BinnerFileUtils;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public abstract class FileListLoaderDisplayPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	private String panelTitle = "";

	private JPanel inputFileWrapPanel, sideBySidePanel, buttonPanel;
	private JProgressBar inputFileProgBar;
	private JButton inputFileButton, createListButton, updateBatchLabelsButton;
	private DraggableListViewPanel batchIdxPanel, filePathPanel, mergeIdxPanel;
	private JSpinner batchLabelUpdateSpinner;
	private JTextField targetBatchBox;
	private JCheckBox updateAddBox;

	private Boolean showMergeOrderPanel = true;
	private Map<String, String> fileOrderMap = null;
	private Map<String, String> batchFileMap = null;
	private ArrayList<String> fileNames = new ArrayList<String>();
	private String initialDirectoryForChooser = null;
	private String buttonLabel = "Create List", secondButtonLabel = "Update Labels";
	private String fileExtension = "csv", fileNameBase = "Blank_Lattice_";
	private Boolean showBatchLabelBtn = false;
	private Integer selectedMin = 1, selectedMax = 100;

	private Map<Integer, Integer> deletedBatchLabels;

	private SharedAnalysisSettings sharedAnalysisSettings = null;

	public FileListLoaderDisplayPanel() {
		this("", null);
	}

	public FileListLoaderDisplayPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		this(title, sharedAnalysisSettings, false);
	}

	public FileListLoaderDisplayPanel(String title, SharedAnalysisSettings sharedAnalysisSettings,
			Boolean readFileOrder) {

		super();

		String tag = StringUtils.cleanAndTrim(title).toLowerCase();
		Double tagSuffix = Math.random();

		tag += String.format("%.3f", tagSuffix);

		initializeStickySettings(tag, BatchMatchConstants.PROPS_FILE);

		this.fileOrderMap = new HashMap<String, String>();
		this.batchFileMap = new HashMap<String, String>();
		this.deletedBatchLabels = new HashMap<Integer, Integer>();
		if (!StringUtils.isEmptyOrNull(title))
			panelTitle = title;
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}

	public void initializeForNFiles(String pathName, Integer nEntries, Integer chosenTarget) {

		this.fileNames = new ArrayList<String>();

		String chosenTargetStr = String.format("%2s", chosenTarget).replace(' ', '0');

		for (int i = 0; i < nEntries; i++) {
			Integer sourceIdx = i + 1;
			String sourceIdxStr = String.format("%2s", sourceIdx).replace(' ', '0');
			if (chosenTarget.equals(i + 1))
				continue;

			fileNames.add(pathName + BatchMatchConstants.FILE_SEPARATOR + fileNameBase + "_" + sourceIdxStr + "_"
					+ chosenTargetStr);
		}
		this.updateInterface();
	}
	// JCheckBox

	public void setupPanel(String title, String buttonLabel, String initialDirectory, Boolean showMergePanel) {
		setupPanel(title, buttonLabel, initialDirectory, showMergePanel, "Update Labels");
	}

	public void setupPanel(String title, String buttonLabel, String initialDirectory, Boolean showMergePanel,
			String secondButtonLabel) {

		this.initialDirectoryForChooser = initialDirectory;
		panelTitle = title;
		this.buttonLabel = buttonLabel;
		this.secondButtonLabel = secondButtonLabel;

		this.showMergeOrderPanel = showMergePanel;
		fileNames = new ArrayList<String>();

		initializeArrays();
		setupInputFilePanel();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(inputFileWrapPanel);
		setupTextFieldListeners();
	}

	private JPanel createSideBySidePanel() {

		List<String> testList = new ArrayList<String>();
		List<String> batchIdxList = new ArrayList<String>();
		List<String> mergeIdxList = new ArrayList<String>();

		for (int i = 0; i < 5; i++) {
			if (i != 4)
				testList.add("");
			else
				testList.add(
						"                                                                              -----  Please select files -----");

			mergeIdxList.add(i == 0 ? "Target" : "" + i);
		}

		sideBySidePanel = new JPanel();

		batchIdxPanel = new DraggableListViewPanel(false, true) {

			@Override
			protected boolean updateForNewOrdering(DefaultListModel<String> strings, Integer newSelection) {

				updateMaps();

				return true;
			}

		};
		batchIdxPanel.setupPanel((ArrayList<String>) batchIdxList);
		batchIdxPanel.setAlignmentX(CENTER_ALIGNMENT);
		batchIdxPanel.setAlignmentY(CENTER_ALIGNMENT);
		TitledBorder currentWrapBorder = BorderFactory.createTitledBorder("Batch");
		currentWrapBorder.setTitleFont(boldFontForTitlePanel(currentWrapBorder, false));
		currentWrapBorder.setTitlePosition(TitledBorder.CENTER);
		batchIdxPanel.setBorder(currentWrapBorder);

		filePathPanel = new DraggableListViewPanel() {

			@Override
			protected boolean updateForNewOrdering(DefaultListModel<String> strings, Integer newSelection) {
				updateMaps();
				return true;
			}
		};
		filePathPanel.setOpaque(true);
		filePathPanel.setBackground(Color.WHITE);
		filePathPanel.setupPanel((ArrayList<String>) testList);

		TitledBorder currentWrapBorder3 = BorderFactory.createTitledBorder("File Path");
		currentWrapBorder3.setTitleFont(boldFontForTitlePanel(currentWrapBorder, false));
		currentWrapBorder3.setTitlePosition(TitledBorder.CENTER);
		filePathPanel.setBorder(currentWrapBorder3);

		mergeIdxPanel = new DraggableListViewPanel(true, true) {

			@Override
			protected boolean updateForNewOrdering(DefaultListModel<String> strings, Integer newSelection) {

				updateMaps();

				ArrayList<String> strStrings = new ArrayList<String>();
				Integer ns = 0;
				for (int i = 0; i < strings.size(); i++) {

					if (strings.get(i).startsWith("T"))
						ns = i + 1;
				}
				updateInterfaceForNewData(ns, strStrings);
				return true;
			}
		};

		mergeIdxPanel.setupPanel((ArrayList<String>) mergeIdxList);
		mergeIdxPanel.setVisible(this.showMergeOrderPanel);
		TitledBorder currentWrapBorder2 = BorderFactory.createTitledBorder("Merge");
		currentWrapBorder2.setTitleFont(boldFontForTitlePanel(currentWrapBorder, false));
		currentWrapBorder2.setTitlePosition(TitledBorder.CENTER);
		mergeIdxPanel.setBorder(currentWrapBorder2);
		mergeIdxPanel.setOpaque(true);
		mergeIdxPanel.setBackground(Color.WHITE);

		LayoutGrid panelGrid = new LayoutGrid();

		if (this.showMergeOrderPanel) {
			panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel("   "), 0.01), new LayoutItem(batchIdxPanel, 0.05),
					new LayoutItem(filePathPanel, 0.869), new LayoutItem(new JLabel("   "), 0.001),
					new LayoutItem(mergeIdxPanel, 0.06), new LayoutItem(new JLabel("   "), 0.01)));
		} else {
			panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel("   "), 0.01), new LayoutItem(batchIdxPanel, 0.05),
					new LayoutItem(filePathPanel, 0.93), new LayoutItem(new JLabel("   "), 0.01)));
		}
		LayoutUtils.doGridLayout(sideBySidePanel, panelGrid);

		return sideBySidePanel;
	}

	public void initializeOrdering(String orderingType) {

		switch (orderingType) {
		case BatchMatchConstants.MERGE_TYPE_CENTER_OUT:
			break;
		case BatchMatchConstants.MERGE_TYPE_BOTTOM_UP:
			break;
		case BatchMatchConstants.MERGE_TYPE_TOP_DOWN:
			break;
		default:
		}
	}

	public void updateMaps() {
		batchFileMap = new HashMap<String, String>();
		fileOrderMap = new HashMap<String, String>();

		List<String> batchNames = batchIdxPanel.getStrings();
		List<String> fileNames = filePathPanel.getStrings();
		List<String> mergeIndices = mergeIdxPanel.getStrings();

		for (int i = 0; i < batchNames.size(); i++) {
			batchFileMap.put(batchNames.get(i), fileNames.get(i));
			fileOrderMap.put(batchNames.get(i), mergeIndices.get(i).startsWith("T") ? "0" : mergeIndices.get(i));
		}
	}

	private void createButtonPanel() {

		setupSpinner();
		initializeSpinnerFromTextBox();

		updateAddBox = new JCheckBox("Add"); // makeStickyCheckBox("Report", "createReport", false, true);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		createListButton = new JButton(buttonLabel);
		createListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				processNewData();
			}
		});

		inputFileButton = new JButton("Browse Files...");
		inputFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				fileNames = BinnerFileUtils.choseMultipleFiles(initialDirectoryForChooser, fileExtension,
						fileExtension);
				deletedBatchLabels = new HashMap<Integer, Integer>();
				updateInterface();
			}
		});

		updateBatchLabelsButton = new JButton(secondButtonLabel);
		updateBatchLabelsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				updateInterface();
				// processNewData();
			}
		});
		// updateBatchLabelsButton.setEnabled(false);
		// updateBatchLabelsButton.setVisible(this.showBatchLabelBtn);

		buttonPanel.add(Box.createVerticalStrut(5));
		buttonPanel.add(inputFileButton);
		buttonPanel.add(Box.createVerticalStrut(5));
		buttonPanel.add(new JLabel("Add/Delete Batch Label :"));

		Component mySpinnerEditor = batchLabelUpdateSpinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(1);

		buttonPanel.add(this.batchLabelUpdateSpinner);
		buttonPanel.add(targetBatchBox);
		targetBatchBox.setVisible(false);
		// buttonPanel.add(Box.createVerticalStrut(1));
		buttonPanel.add(new JLabel("  "));
		buttonPanel.add(this.updateAddBox);
		buttonPanel.add(new JLabel("  "));
		buttonPanel.add(updateBatchLabelsButton);

		buttonPanel.add(Box.createVerticalStrut(5));

		buttonPanel.add(createListButton);
		buttonPanel.add(Box.createVerticalStrut(5));
	}

	private void setupInputFilePanel() {

		JPanel inputFilePanel = new JPanel();
		inputFilePanel.setLayout(new BoxLayout(inputFilePanel, BoxLayout.X_AXIS));
		inputFilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		createSideBySidePanel();
		createButtonPanel();

		JPanel inputFileProgPanel = new JPanel();
		inputFileProgPanel.setLayout(new BoxLayout(inputFileProgPanel, BoxLayout.X_AXIS));
		inputFileProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		inputFileProgBar = new JProgressBar(0, 500);
		inputFileProgBar.setIndeterminate(true);
		inputFileProgPanel.add(inputFileProgBar);
		inputFileProgPanel.setVisible(false);

		inputFileWrapPanel = new JPanel();
		inputFileWrapPanel.setLayout(new BoxLayout(inputFileWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder inputFileWrapBorder = BorderFactory.createTitledBorder(panelTitle);
		inputFileWrapBorder.setTitleFont(boldFontForTitlePanel(inputFileWrapBorder, false));
		inputFileWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		inputFileWrapPanel.setBorder(inputFileWrapBorder);
		inputFileWrapPanel.add(sideBySidePanel);

		inputFileWrapPanel.add(buttonPanel);
	}

	private void setupSpinner() {

		SpinnerModel spinnerModelCores = new SpinnerNumberModel(0, 0, 100, 1);

		batchLabelUpdateSpinner = new JSpinner(spinnerModelCores);
		batchLabelUpdateSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				targetBatchBox.setText("" + val);
				// updateForNewSelection(val);
			}
		});

		Component mySpinnerEditor = batchLabelUpdateSpinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(1);

		targetBatchBox = makeStickyTextField("targetLattice2", "1", true);
		targetBatchBox.setText("0");
		// PostProccessConstants.DEFAULT_BATCH_LEVEL_REPORTED.toString(), true);
		// intSelectorPanel.add(n1SelectedBox);
		targetBatchBox.setVisible(false);

		initializeSpinnerFromTextBox();
	}

	private void initializeSpinnerFromTextBox() {

		try {
			Integer nInitialCores = Integer.parseInt(targetBatchBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			batchLabelUpdateSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			batchLabelUpdateSpinner.setValue(1);
		}
	}

	private void updateInterface() {

		ArrayList<String> batchIdxList = new ArrayList<String>();
		ArrayList<String> mergeOrderList = new ArrayList<String>();

		Integer indexToUpdate = Integer.parseInt(this.targetBatchBox.getText());

		if (!this.updateAddBox.isSelected() && indexToUpdate != null && !indexToUpdate.equals(0))
			this.deletedBatchLabels.put(indexToUpdate, null);

		System.out.println("Index to update " + indexToUpdate);

		int nLabelsCreated = 0;
		int i = 0, trialVal = -1;
		if (fileNames != null) {

			while (nLabelsCreated < fileNames.size()) {

				trialVal = i + 1;
				if (deletedBatchLabels.containsKey(trialVal)) {
					if (this.updateAddBox.isSelected()) {
						this.deletedBatchLabels.remove(trialVal);
					} else {
						i++;
						continue;
					}
				}

				batchIdxList.add("" + (trialVal));
				mergeOrderList.add(nLabelsCreated == 0 ? "Target" : "" + nLabelsCreated); // + (i == 0 ? " (Target)" :
																							// ""));
				i++;
				nLabelsCreated++;
			}
		}
		batchIdxPanel.refreshList(batchIdxList);
		filePathPanel.refreshList(fileNames);
		mergeIdxPanel.refreshList(mergeOrderList);

		updateMaps();
		updateInterfaceForNewData(1, fileNames);
	}

	public Map<String, String> getFileOrderMap() {
		updateMaps();
		return this.fileOrderMap;
	}

	public Map<String, String> getBatchFileMap() {
		updateMaps();
		return this.batchFileMap;
	}

	public Integer getNBatches() {
		return batchFileMap == null ? 0 : this.batchFileMap.keySet().size();
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}

	public void setInitialDirectoryForChooser(String dir) {
		this.initialDirectoryForChooser = dir;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void updateToCreateList() {
		this.createListButton.setEnabled(true);
		this.updateBatchLabelsButton.setEnabled(false);
	}

	public void updateToCreateFiles() {
		this.createListButton.setEnabled(false);
		this.updateBatchLabelsButton.setEnabled(true);
	}

	public void disableListButton() {
		this.createListButton.setEnabled(false);
	}

	// public void disableFileCreateButton() {
	// this.inputFileButton.setEnabled(false);
	// }

	public Boolean getShowBatchLabelBtn() {
		return showBatchLabelBtn;
	}

	public void setShowBatchLabelBtn(Boolean showBatchLabelBtn) {
		this.showBatchLabelBtn = showBatchLabelBtn;
	}

	public void setTargetSelected(Integer val) {
		targetBatchBox.setText(val.toString());
		batchLabelUpdateSpinner.setValue(val);
	}

	protected abstract void processNewData();

	protected abstract void updateInterfaceForNewData(int chosen, ArrayList<String> fileNames);
}
