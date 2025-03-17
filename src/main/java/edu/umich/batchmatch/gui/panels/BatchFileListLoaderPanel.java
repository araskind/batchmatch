////////////////////////////////////////////////////
// BatchFileListLoaderPanel.java
// Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.batchmatch.gui.panels;

import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.data.SharedAnalysisSettings;
import edu.umich.batchmatch.io.sheetreaders.MetabolomicsIntensityDataLoader;

//Select All
public abstract class BatchFileListLoaderPanel extends FileListLoaderPanel {

	private static final long serialVersionUID = 6082733604962176299L;
	private List<String> allIntensityHeaders = null;

	public BatchFileListLoaderPanel() {
		this("", null);
	}

	public BatchFileListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings) {
		super(title, sharedAnalysisSettings);
	}

	public BatchFileListLoaderPanel(String title, SharedAnalysisSettings sharedAnalysisSettings,
			Boolean readFileOrder) {
		super(title, sharedAnalysisSettings, readFileOrder);
	}

	/*
	 * public void setupPanel () {
	 * 
	 * initializeArrays(); setupInputFilePanel();
	 * 
	 * setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	 * add(Box.createVerticalStrut(2)); add(inputFileWrapPanel);
	 * add(Box.createVerticalStrut(2));
	 * 
	 * setupTextFieldListeners(); }
	 * 
	 * private void setupInputFilePanel() { JPanel inputFilePanel = new JPanel();
	 * inputFilePanel.setLayout(new BoxLayout(inputFilePanel, BoxLayout.X_AXIS));
	 * inputFilePanel.setAlignmentX(Component.CENTER_ALIGNMENT); inputFileComboBox =
	 * new JComboBox<FileData>(); inputFileComboBox.setEditable(false);
	 * inputFileComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
	 * 
	 * inputFileComboBox.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent ae) { if
	 * ("comboBoxChanged".equals(ae.getActionCommand())) { } } });
	 * 
	 * inputFileButton = new JButton("Browse...");
	 * inputFileButton.addActionListener(new ActionListener() {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { File file =
	 * BinnerFileUtils.getFile("Select Batch File Map File", BinnerFileUtils.LOAD,
	 * "csv", "Comma-Separated Value Files", null);
	 * 
	 * if (file != null) { File File = new FileData();
	 * fileData.setName(file.getName()); fileData.setPath(file.getPath());
	 * 
	 * inputFileComboBox.removeAllItems(); inputFileComboBox.insertItemAt(fileData,
	 * 0); inputFileComboBox.setSelectedIndex(0); currentBatchFile = (FileData)
	 * inputFileComboBox.getSelectedItem();
	 * 
	 * if (openBatchFileFile()) { buildBatchFileMap(); } } } });
	 * 
	 * inputFilePanel.add(Box.createHorizontalStrut(8));
	 * inputFilePanel.add(inputFileComboBox);
	 * inputFilePanel.add(Box.createHorizontalStrut(8));
	 * inputFilePanel.add(inputFileButton);
	 * inputFilePanel.add(Box.createHorizontalStrut(8));
	 * 
	 * JPanel inputFileProgPanel = new JPanel(); inputFileProgPanel.setLayout(new
	 * BoxLayout(inputFileProgPanel, BoxLayout.X_AXIS));
	 * inputFileProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	 * inputFileProgBar = new JProgressBar(0, 500);
	 * inputFileProgBar.setIndeterminate(true);
	 * inputFileProgPanel.add(inputFileProgBar);
	 * inputFileProgPanel.setVisible(false);
	 * 
	 * inputFileWrapPanel = new JPanel(); inputFileWrapPanel.setLayout(new
	 * BoxLayout(inputFileWrapPanel, BoxLayout.Y_AXIS)); TitledBorder
	 * inputFileWrapBorder = BorderFactory.createTitledBorder(panelTitle);
	 * inputFileWrapBorder.setTitleFont(boldFontForTitlePanel(inputFileWrapBorder,
	 * false));
	 * inputFileWrapBorder.setTitleColor(PostProccessConstants.TITLE_COLOR);
	 * inputFileWrapPanel.setBorder(inputFileWrapBorder);
	 * inputFileWrapPanel.add(inputFilePanel);
	 * inputFileWrapPanel.add(inputFileProgPanel); }
	 * 
	 * private boolean openBatchFileFile() {
	 * 
	 * physicalMapData = new TextFile(); try { physicalMapData.open(new
	 * File(currentBatchFileData.getPath())); } catch (Exception e) {
	 * e.printStackTrace(); physicalMapData = null; return false; } return true; }
	 * 
	 * private Map<Integer, String> buildBatchFileMap() {
	 * 
	 * if (physicalMapData == null || physicalMapData.getEndRowIndex() < 2) return
	 * null;
	 * 
	 * batchFileMap = new HashMap<Integer,String>();
	 * 
	 * for (int i = 0; i <= physicalMapData.getEndRowIndex(); i++) {
	 * 
	 * String errMsg = "Error: Batch file list must specify a batch number and " +
	 * PostProccessConstants.LINE_SEPARATOR + "a batch file for each source .  " +
	 * "Row " + (i + 1) + " starting with " + physicalMapData.getString(i, 0) +
	 * " contains an invalid entry.    ";;
	 * 
	 * try { Integer batchNo = physicalMapData.getInteger(i, 0); String fileName =
	 * physicalMapData.getString(i, 1);
	 * 
	 * if (StringUtils.isEmptyOrNull(fileName)) continue;
	 * 
	 * if (batchNo != null && fileName != null) errMsg = "";
	 * 
	 * if (batchNo == null) batchNo = 14; batchFileMap.put(batchNo, fileName);
	 * System.out.println("Batch " + batchNo + " " + fileName); continue; } catch
	 * (Exception e) { }
	 * 
	 * if (!StringUtils.isEmptyOrNull(errMsg)) { JOptionPane.showMessageDialog(null,
	 * errMsg); return null; } } return batchFileMap; }
	 * 
	 * public String getFileName() { return currentBatchFile != null ?
	 * currentBatchFileData.getName() : ""; }
	 * 
	 * public Map<Integer, String> grabBatchFileMap() { if (batchFileMap == null)
	 * buildBatchFileMap(); return batchFileMap; }
	 * 
	 * public Map<Integer, String> grabFreshBatchFileMap() { return
	 * grabBatchFileMap(); }
	 */
	public List<String> grabIntensityHeaders() {

		if (allIntensityHeaders != null)
			return allIntensityHeaders;

		Map<Integer, String> batchFileMap = grabFreshBatchFileMap();
		MetabolomicsIntensityDataLoader headerLoader = new MetabolomicsIntensityDataLoader();
		allIntensityHeaders = headerLoader.preReadIntensityHeaders(batchFileMap);

		return allIntensityHeaders;
	}
	/*
	 * public Integer getNBatches() { grabBatchFileMap(); return batchFileMap ==
	 * null ? 0 : batchFileMap.keySet().size(); }
	 * 
	 * public SharedAnalysisSettings getSharedAnalysisSettings() { return
	 * sharedAnalysisSettings; }
	 * 
	 * public void setSharedAnalysisSettings(SharedAnalysisSettings
	 * sharedAnalysisSettings) { this.sharedAnalysisSettings =
	 * sharedAnalysisSettings; }
	 * 
	 * protected abstract void updateInterfaceForNewData();
	 */
}
