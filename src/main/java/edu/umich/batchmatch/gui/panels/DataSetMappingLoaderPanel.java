////////////////////////////////////////////////////
// DataSetMappingLoaderPanel.java
// Written by Jan Wigginton March 2023
////////////////////////////////////////////////////
package edu.umich.batchmatch.gui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.data.FeatureInfoForMatchGroupMapping;
import edu.umich.batchmatch.data.SharedAnalysisSettings;
import edu.umich.batchmatch.io.sheetreaders.DataSetMappingReader;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.utils.BinnerFileUtils;


public class DataSetMappingLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	private JComboBox<File> inputFileComboBox;
	private JPanel inputFileWrapPanel;
	private JProgressBar inputFileProgBar;
	// private TextFile physicalFeatureMapData = null;
	private File currentFeatureMapFile;
	private JButton inputFileButton;

	private String initialDirectory;

	private String panelTitle = "Select Feature Mapping File (must be .csv)";
	private SharedAnalysisSettings sharedAnalysisSettings = null;

	private Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> dataSetMapping;

	public DataSetMappingLoaderPanel() {
		this("", null);
	}

	public DataSetMappingLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		panelTitle = title;
		dataSetMapping = null;
		this.sharedAnalysisSettings = sharedAnalysisSettings;
//		initializeStickySettings("dneaIntensityFile", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {
		setupPanel(null);
	}

	public void setupPanel(String initialDirectory) {
		this.initialDirectory = initialDirectory;
		initializeArrays();
		setupInputFilePanel();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(2));
		add(inputFileWrapPanel);
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
				File file = BinnerFileUtils.getFile("Select Feature Mappping File", BinnerFileUtils.LOAD, "csv",
						"Comma-Separated Value Files", initialDirectory);

				updateForNewFile(file);
			}
		});

		inputFilePanel.add(Box.createHorizontalStrut(8));
		inputFilePanel.add(inputFileComboBox);
		inputFilePanel.add(Box.createHorizontalStrut(8));
		inputFilePanel.add(inputFileButton);
		inputFilePanel.add(Box.createHorizontalStrut(8));

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
		inputFileWrapBorder.setTitleColor(PostProccessConstants.TITLE_COLOR);
		inputFileWrapPanel.setBorder(inputFileWrapBorder);
		inputFileWrapPanel.add(inputFilePanel);
		inputFileWrapPanel.add(inputFileProgPanel);
	}

	public void updateForNewFileName(String fileName) {

		String outputFileName = fileName;
		File file = new File(outputFileName);
		updateForNewFile(file);
	}

	private void updateForNewFile(File file) {

		if (file != null) {

			inputFileComboBox.removeAllItems();
			inputFileComboBox.insertItemAt(file, 0);
			inputFileComboBox.setSelectedIndex(0);
			currentFeatureMapFile = (File) inputFileComboBox.getSelectedItem();

			dataSetMapping = buildDataSetMapping();
		}
	}

	private Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> buildDataSetMapping() {
		dataSetMapping = DataSetMappingReader.readDataSetMapping(currentFeatureMapFile.getPath());
		return dataSetMapping;
	}

	public String getFileName() {
		return currentFeatureMapFile != null ? currentFeatureMapFile.getName() : "";
	}

	private Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> grabDataSetMapping() {
		if (dataSetMapping == null)
			buildDataSetMapping();

		return dataSetMapping;
	}

	public Map<Integer, Map<Integer, FeatureInfoForMatchGroupMapping>> grabFreshDataSetMapping() {
		return buildDataSetMapping();
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}

	public String getInitialDirectory() {
		return initialDirectory;
	}

	public void setInitialDirectory(String initialDirectory) {
		this.initialDirectory = initialDirectory;
	}
}
