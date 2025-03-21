////////////////////////////////////////////////////
// FeatureMappingLoaderPanel.java
// Written by Jan Wigginton March 2023
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.data.orig.FeatureMatch;
import edu.umich.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.process.orig.BatchMatchMappingFileInfo;
import edu.umich.mrc2.batchmatch.utils.orig.BinnerFileUtils;
import edu.umich.mrc2.batchmatch.utils.orig.StringUtils;

public class FeatureMappingLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;
	private static int N_FEATURE_MAP_COLS = 8;

	private JComboBox<File> inputFileComboBox;
	private JPanel inputFileWrapPanel;
	private JProgressBar inputFileProgBar;
	private TextFile physicalFeatureMapData = null;
	private File currentFeatureMapFile;
	private JButton inputFileButton;

	private String initialDirectory;

	private String panelTitle = "Select Feature Mapping File (must be .csv)";
	private SharedAnalysisSettings sharedAnalysisSettings = null;

//	private List<FeatureMatch> featureMapping = null; 
//	private List<FeatureMatch> missingFeatureMap = null;
//	private Map<Integer, List<FeatureMatch>> allFeaturesByBatchMap = null;

	private BatchMatchMappingFileInfo namedUnnamedMapInfo = null;

	public FeatureMappingLoaderPanel() {
		this("", null);
	}

	public FeatureMappingLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		panelTitle = title;
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
		inputFileWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
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

			if (openFeatureMapFile()) {
				buildFeatureMap();
			}
		}
	}

	private boolean openFeatureMapFile() {

		physicalFeatureMapData = new TextFile();
		try {
			physicalFeatureMapData.open(new File(currentFeatureMapFile.getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			physicalFeatureMapData = null;
			return false;
		}
		return true;
	}

	private List<FeatureMatch> buildFeatureMap() {

		if (physicalFeatureMapData == null || physicalFeatureMapData.getEndRowIndex() < 2)
			return null;

		List<FeatureMatch> featureMappings = new ArrayList<FeatureMatch>();

		for (int i = 0; i <= physicalFeatureMapData.getEndRowIndex(); i++) {

			String namedFeature = null, unnamedFeature = null, annotation = null;
			Boolean isPI = null;
			Double corr = null, namedMass = null, namedRt = null;
			Double deltaCorr1 = null, deltaCorr2 = null, deltaCorr3 = null;

			String errMsg = "Error: Feature match file must specify a named feature and a matching"
					+ BinnerConstants.LINE_SEPARATOR
					+ "unnamed feature, corresponding annotation and correlation for each source.  "
					+ BinnerConstants.LINE_SEPARATOR + "Row " + (i + 1) + " starting with "
					+ physicalFeatureMapData.getString(i, 0) + " contains an invalid entry.    ";
			;
			try {
				if (physicalFeatureMapData.getEndColIndex(i) <= N_FEATURE_MAP_COLS) {
					namedFeature = physicalFeatureMapData.getString(i, 0);
					if (namedFeature.startsWith("Named") && i < 10)
						continue;

					unnamedFeature = physicalFeatureMapData.getString(i, 1);
					Boolean isUnmapped = unnamedFeature == null
							|| unnamedFeature.startsWith(BatchMatchConstants.NOT_MAPPED_MSG);

					String corrStr = physicalFeatureMapData.getString(i, 2);
					Boolean isUnnamedAllMissing = corrStr.startsWith(BatchMatchConstants.UNNAMED_ALL_MISSING_MSG);
					Boolean isNamedAllMissing = corrStr.startsWith(BatchMatchConstants.NAMED_ALL_MISSING_MSG);

					// why do we assign values differently?
					if (isUnnamedAllMissing)
						corr = BatchMatchConstants.UNNAMED_ALL_MISSING;
					else if (isNamedAllMissing)
						corr = BatchMatchConstants.NAMED_ALL_MISSING;
					else
						corr = isUnmapped ? null : physicalFeatureMapData.getDouble(i, 2);

					namedMass = physicalFeatureMapData.getDouble(i, 3);
					namedRt = physicalFeatureMapData.getDouble(i, 4);

					Boolean noCorrData = isUnmapped || isNamedAllMissing || isUnnamedAllMissing;
					deltaCorr1 = noCorrData ? null : physicalFeatureMapData.getDouble(i, 5);
					deltaCorr2 = noCorrData ? null : physicalFeatureMapData.getDouble(i, 6);
					deltaCorr3 = noCorrData ? null : physicalFeatureMapData.getDouble(i, 7);

					FeatureMatch f = new FeatureMatch();
					f.setNamedFeature(namedFeature);
					f.setUnnamedFeature(unnamedFeature);
					f.setAnnotation(annotation);
					f.setCorr(corr);
					f.setNamedMass(namedMass);
					f.setNamedRt(namedRt);
					f.setDeltaCorr1(deltaCorr1);
					f.setDeltaCorr2(deltaCorr2);
					f.setDeltaCorr3(deltaCorr3);
					f.setIsUnmapped(isUnmapped);

					featureMappings.add(f);

					errMsg = "";
				}
			} catch (Exception e) {
			}

			if (!StringUtils.isEmptyOrNull(errMsg)) {
				JOptionPane.showMessageDialog(null, errMsg);
				return null;
			}
		}

		namedUnnamedMapInfo = new BatchMatchMappingFileInfo();
		namedUnnamedMapInfo.initializeFromAllMappings(featureMappings);
		return namedUnnamedMapInfo.getNonMissingCompoundMappings();
	}

	public String getFileName() {
		return currentFeatureMapFile != null ? currentFeatureMapFile.getName() : "";
	}

	public BatchMatchMappingFileInfo getNamedUnnamedMapInfo() {

		if (namedUnnamedMapInfo == null)
			this.buildFeatureMap();
		return namedUnnamedMapInfo;
	}

	public List<FeatureMatch> grabNonMissingFeatureMapping() {
		if (namedUnnamedMapInfo == null)
			buildFeatureMap();

		return namedUnnamedMapInfo.getNonMissingCompoundMappings();
	}

	public List<FeatureMatch> grabMissingFeatureMap() {
		if (this.namedUnnamedMapInfo == null)
			buildFeatureMap();

		return namedUnnamedMapInfo.getMissingCompoundMappings();
	}

	public List<FeatureMatch> grabFreshFeatureMap() {
		return buildFeatureMap();
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
