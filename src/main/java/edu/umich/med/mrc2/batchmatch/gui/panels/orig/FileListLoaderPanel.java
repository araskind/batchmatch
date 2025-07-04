////////////////////////////////////////////////////
// FileListLoaderPanel.java
// Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

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

import edu.umich.med.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.med.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.utils.orig.BinnerFileUtils;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public abstract class FileListLoaderPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 6082733604962176299L;

	protected JComboBox<File> inputFileComboBox;
	protected JPanel inputFileWrapPanel;
	protected JProgressBar inputFileProgBar;
	protected TextFile physicalMapData = null;
	protected File currentBatchFile;
	protected JButton inputFileButton;
	protected Boolean readOrder;
	protected File initialDirectory = null;

	protected List<Integer> fileOrderList;
	protected Map<Integer, Integer> fileOrderMap;
	protected Map<Integer, File> batchFileMap = null;
	protected String panelTitle = "Select Batch File Map File (must be .csv)";
	protected SharedAnalysisSettings sharedAnalysisSettings = null;

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

	public void setupPanel(File initialDirectory) {

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
						"Comma-Separated Value Files", initialDirectory.getAbsolutePath());

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
		inputFileWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
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

	private Map<Integer, File> buildBatchFileMap() {

		// Reminder set only
		// Boolean readOrder = sharedAnalysisSettings.isReadFileOrder();

		if ((physicalMapData == null && !openBatchFileFile()) || physicalMapData.getEndRowIndex() < 0)
			return null;

		if (readOrder && physicalMapData.getEndColIndex(0) < 2) {
			System.out.println("Missing merge order");
			return null;
		}

		// System.out
		batchFileMap = new HashMap<Integer, File>();
		fileOrderList = new ArrayList<Integer>();
		fileOrderMap = new HashMap<Integer, Integer>();

		for (int i = 0; i <= physicalMapData.getEndRowIndex(); i++) {

			String errMsg = "Error: Batch file list must specify a batch number and "
					+ BinnerConstants.LINE_SEPARATOR + "a batch file for each source .  " + "Row " + (i + 1)
					+ " starting with " + physicalMapData.getString(i, 0) + " contains an invalid entry.    ";
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
				
				File dataFile = new File(fileName);
				if(!dataFile.exists()) {
					String msg = "Data file \"" + dataFile.getAbsolutePath() + "\" for batch " + batchNo + " does not exist";
					JOptionPane.showMessageDialog(null, msg);
					return null;
				}				
				batchFileMap.put(batchNo, new File(fileName));
				System.out.println("Batch " + batchNo + " " + fileName);
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!StringUtils.isEmptyOrNull(errMsg)) {
				JOptionPane.showMessageDialog(null, errMsg);
				return null;
			}
		}
		System.out.println();
		return batchFileMap;
	}

	public File getInitialDirectory() {
		return initialDirectory;
	}

	public void setInitialDirectory(File initialDirectory) {
		this.initialDirectory = initialDirectory;
	}

	public String getFileName() {
		return currentBatchFile != null ? currentBatchFile.getName() : "";
	}

	public Map<Integer, File> grabBatchFileMap() {
		
		if (batchFileMap == null || batchFileMap.size() < 1)
			buildBatchFileMap();
		
		return batchFileMap;
	}

	public List<Integer> grabFileOrderList() {
		grabBatchFileMap();
		return this.fileOrderList;
	}

	public Map<Integer, File> grabBatchFileMapSegment(int firstNEntries) {
		grabFileOrderMap();
		Map<Integer, File> orderedMap = new HashMap<Integer, File>();
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

	public Map<Integer, File> grabFreshBatchFileMap() {
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
