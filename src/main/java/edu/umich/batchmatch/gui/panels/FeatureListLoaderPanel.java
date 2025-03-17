package edu.umich.batchmatch.gui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.data.SharedAnalysisSettings;
import edu.umich.batchmatch.io.sheetreaders.FeatureListReader;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.utils.BinnerFileUtils;
import edu.umich.batchmatch.utils.StringUtils;

public abstract class FeatureListLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	private JComboBox<File> inputFileComboBox;
	private JPanel inputFileWrapPanel;
	private JProgressBar inputFileProgBar;
	private File currentBatchFile;
	private JButton inputFileButton;
	private String initialDirectory = null;

	private Map<Integer, Map<Integer, List<String>>> featureListMap = null;
	private String panelTitle = "Select Feature List File (must be .csv)";
	private SharedAnalysisSettings sharedAnalysisSettings = null;

	protected String openingMessage = null;

	public String getOpeningMessage() {
		return openingMessage;
	}

	public void setOpeningMessage(String openingMessage) {
		this.openingMessage = openingMessage;
	}

	public FeatureListLoaderPanel() {
		this("", null);
	}

	public FeatureListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		this(title, sharedAnalysisSettings, false);
	}

	public FeatureListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings, Boolean readFileOrder) {
		super();

		if (!StringUtils.isEmptyOrNull(title))
			panelTitle = title;
		this.sharedAnalysisSettings = sharedAnalysisSettings;
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
				File file = BinnerFileUtils.getFile("Select Feature List File", BinnerFileUtils.LOAD, "csv",
						"Comma-Separated Value Files", initialDirectory);

				if (file != null) {

					inputFileComboBox.removeAllItems();
					inputFileComboBox.insertItemAt(file, 0);
					inputFileComboBox.setSelectedIndex(0);
					currentBatchFile = (File) inputFileComboBox.getSelectedItem();

					try {
						FeatureListReader reader = new FeatureListReader();
						featureListMap = reader.readFeatures(currentBatchFile.getPath());
						updateInterfaceForNewData(featureListMap);
					} catch (Exception f) {
						String errMsg = "Error while reading feature list file";
						JOptionPane.showMessageDialog(null, errMsg);
					}
				}
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

	public Map<Integer, Map<Integer, List<String>>> buildFeatureListMap() {
		FeatureListReader reader = new FeatureListReader();
		featureListMap = reader.readFeatures(currentBatchFile.getPath());
		return featureListMap;
	}

	public String getInitialDirectory() {
		return initialDirectory;
	}

	public void setInitialDirectory(String initialDirectory) {
		this.initialDirectory = initialDirectory;
	}

	public String getFileName() {
		return currentBatchFile != null ? currentBatchFile.getName() : "";
	}

	public Map<Integer, Map<Integer, List<String>>> grabFeatureListMap() {
		if (featureListMap == null || featureListMap.size() < 1)
			buildFeatureListMap();
		return featureListMap;
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}

	public Boolean hasFileSelection() {
		return this.currentBatchFile != null;
	}

	protected abstract void updateInterfaceForNewData(Map<Integer, Map<Integer, List<String>>> featureListMap);
}
