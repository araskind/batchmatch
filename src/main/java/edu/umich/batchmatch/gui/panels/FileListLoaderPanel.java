////////////////////////////////////////////////////
// FileListLoaderPanel.java
// Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.batchmatch.gui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
import edu.umich.batchmatch.data.TextFile;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.utils.BinnerFileUtils;
import edu.umich.batchmatch.utils.StringUtils;

public abstract class FileListLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	private JComboBox<File> inputFileComboBox;
	private JPanel inputFileWrapPanel;
	private JProgressBar inputFileProgBar;
	private TextFile physicalMapData = null;
	private File currentBatchFile;
	private JButton inputFileButton;
	private Boolean readOrder;
	private String initialDirectory = null;

	private List<Integer> fileOrderList;
	private Map<Integer, Integer> fileOrderMap;
	private Map<Integer, String> batchFileMap = null;
	private String panelTitle = "Select Batch File Map File (must be .csv)";
	private SharedAnalysisSettings sharedAnalysisSettings = null;

	protected String openingMessage = null;

	public String getOpeningMessage() {
		return openingMessage;
	}

	public void setOpeningMessage(String openingMessage) {
		this.openingMessage = openingMessage;
	}

	public FileListLoaderPanel() {
		this("", null);
	}

	public FileListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		this(title, sharedAnalysisSettings, false);
	}

	public FileListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings, Boolean readFileOrder) {
		super();
		this.readOrder = readFileOrder;

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
				File file = BinnerFileUtils.getFile("Select Batch File Map File", BinnerFileUtils.LOAD, "csv",
						"Comma-Separated Value Files", initialDirectory);

				if (file != null) {

					inputFileComboBox.removeAllItems();
					inputFileComboBox.insertItemAt(file, 0);
					inputFileComboBox.setSelectedIndex(0);
					currentBatchFile = (File) inputFileComboBox.getSelectedItem();

					if (openBatchFileFile()) {
						if (openingMessage != null)
							System.out.println(openingMessage);
						buildBatchFileMap();
						updateInterfaceForNewData();
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

	private boolean openBatchFileFile() {

		physicalMapData = new TextFile();
		currentBatchFile = (File) inputFileComboBox.getSelectedItem();

		try {
			physicalMapData.open(new File(currentBatchFile.getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			physicalMapData = null;
			return false;
		}
		return true;
	}

	private Map<Integer, String> buildBatchFileMap() {

		// Reminder set only
		// Boolean readOrder = sharedAnalysisSettings.isReadFileOrder();

		if ((physicalMapData == null && !openBatchFileFile()) || physicalMapData.getEndRowIndex() < 0)
			return null;

		if (readOrder && physicalMapData.getEndColIndex(0) < 2) {
			System.out.println("Missing merge order");
			return null;
		}

		// System.out
		batchFileMap = new HashMap<Integer, String>();
		fileOrderList = new ArrayList<Integer>();
		fileOrderMap = new HashMap<Integer, Integer>();

		for (int i = 0; i <= physicalMapData.getEndRowIndex(); i++) {

			String errMsg = "Error: Batch file list must specify a batch number and "
					+ PostProccessConstants.LINE_SEPARATOR + "a batch file for each source .  " + "Row " + (i + 1)
					+ " starting with " + physicalMapData.getString(i, 0) + " contains an invalid entry.    ";
			;

			try {

				Integer batchNo = physicalMapData.getInteger(i, 0);

				if (batchNo == null)
					continue;

				String fileName = physicalMapData.getString(i, 1);
				Integer batchOrderRank = readOrder ? physicalMapData.getInteger(i, 2) : null;

				fileOrderList.add(readOrder ? batchOrderRank : batchNo);
				fileOrderMap.put(batchOrderRank, batchNo);

				if (StringUtils.isEmptyOrNull(fileName))
					continue;

				if (batchNo != null && fileName != null && (!readOrder || batchOrderRank != null))
					errMsg = "";

				if (batchNo == null)
					batchNo = 14;
				batchFileMap.put(batchNo, fileName);
				System.out.println("Batch " + batchNo + " " + fileName);
				continue;
			} catch (Exception e) {
			}

			if (!StringUtils.isEmptyOrNull(errMsg)) {
				JOptionPane.showMessageDialog(null, errMsg);
				return null;
			}
		}
		System.out.println();
		return batchFileMap;
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

	public Map<Integer, String> grabBatchFileMap() {
		if (batchFileMap == null || batchFileMap.size() < 1)
			buildBatchFileMap();
		return batchFileMap;
	}

	public List<Integer> grabFileOrderList() {
		grabBatchFileMap();
		return this.fileOrderList;
	}

	public Map<Integer, String> grabBatchFileMapSegment(int firstNEntries) {
		grabFileOrderMap();
		Map<Integer, String> orderedMap = new HashMap<Integer, String>();
		for (int i = 0; i < firstNEntries; i++) {
			Integer key = i;
			orderedMap.put(fileOrderMap.get(key), batchFileMap.get(fileOrderMap.get(key)));
		}
		return orderedMap;
	}

	public Map<Integer, Integer> grabFileOrderMapSegment(int firstNEntries) {
		grabFileOrderMap();
		Map<Integer, Integer> orderedMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < firstNEntries; i++) {
			Integer key = i;
			orderedMap.put(key, fileOrderMap.get(key));
		}
		return orderedMap;
	}

	public Map<Integer, Integer> grabFileOrderMap() {
		grabBatchFileMap();
		return this.fileOrderMap;
	}

	public Map<Integer, String> grabFreshBatchFileMap() {
		return grabBatchFileMap();
	}

	public Integer getNBatches() {
		grabBatchFileMap();
		return batchFileMap == null ? 0 : batchFileMap.keySet().size();
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

	protected abstract void updateInterfaceForNewData();
}
