////////////////////////////////////////////////////
// AnchorLoaderPanel.java
// Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.data.orig.AnchorMap;
import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.utils.orig.BinnerFileUtils;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class AnchorLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	private JComboBox<File> inputFileComboBox;
	private JPanel inputFileWrapPanel;
	private JProgressBar inputFileProgBar;
	private TextFile physicalAnchorData = null;
	private File currentAnchorFile;
	private JButton inputFileButton;

	private AnchorMap anchorMap = null;
	private String panelTitle = "Select Anchor File (must be .csv)";
	private SharedAnalysisSettings sharedAnalysisSettings = null;

	public AnchorLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		super();
		panelTitle = title;
		this.sharedAnalysisSettings = sharedAnalysisSettings;
//		initializeStickySettings("dneaIntensityFile", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {
		
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
				File file = BinnerFileUtils.getFile("Select RT Anchor Map File", BinnerFileUtils.LOAD, "csv",
						"Comma-Separated Value Files", null);

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
			currentAnchorFile = (File) inputFileComboBox.getSelectedItem();

			if (openAnchorFile()) {
				buildAnchorMap();
			}
		}
	}

	private boolean openAnchorFile() {

		physicalAnchorData = new TextFile();
		try {
			physicalAnchorData.open(new File(currentAnchorFile.getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			physicalAnchorData = null;
			return false;
		}
		return true;
	}

	private AnchorMap buildAnchorMap() {

		if (physicalAnchorData == null || physicalAnchorData.getEndRowIndex() < 2)
			return null;

		anchorMap = new AnchorMap();
		int sourceCol = 0;
		int destCol = 1;
		if (!sharedAnalysisSettings.isSecondLatticeColIsDest()) {
			sourceCol = 1;
			destCol = 0;
		}

		for (int i = 0; i <= physicalAnchorData.getEndRowIndex(); i++) {
			if (i == 0) {
				anchorMap.setBatchName1(physicalAnchorData.getString(i, sourceCol));
				anchorMap.setBatchName2(physicalAnchorData.getString(i, destCol));
				continue;
			}

			Double rt1 = null, rt2 = null;

			String errMsg = "Error: Anchor file must specify a source rt and " + BinnerConstants.LINE_SEPARATOR
					+ "a target rt for each source .  " + "Row " + (i + 1) + " starting with "
					+ physicalAnchorData.getString(i, 0) + " contains an invalid entry.    ";
			;
			try {
				rt1 = physicalAnchorData.getDouble(i, sourceCol);
				rt2 = physicalAnchorData.getDouble(i, destCol);

				if (rt1 != null && rt1 != null)
					errMsg = "";

				anchorMap.addRTPair(rt1, rt2);
			} catch (Exception e) {
			}

			if (!StringUtils.isEmptyOrNull(errMsg)) {
				JOptionPane.showMessageDialog(null, errMsg);
				return null;
			}
		}
		return anchorMap;
	}

	public String getFileName() {
		return currentAnchorFile != null ? currentAnchorFile.getName() : "";
	}

	public AnchorMap grabAnchorMap() {
		if (anchorMap == null)
			buildAnchorMap();

		return anchorMap;
	}

	public AnchorMap grabFreshAnchorMap() {
		return buildAnchorMap();
	}

	public SharedAnalysisSettings getSharedAnalysisSettings() {
		return sharedAnalysisSettings;
	}

	public void setSharedAnalysisSettings(SharedAnalysisSettings sharedAnalysisSettings) {
		this.sharedAnalysisSettings = sharedAnalysisSettings;
	}
}
