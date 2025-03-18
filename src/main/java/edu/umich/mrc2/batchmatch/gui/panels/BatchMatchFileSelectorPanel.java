/*******************************************************************************
 *
 * (C) Copyright 2018-2020 MRC2 (http://mrc2.umich.edu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Alexander Raskind (araskind@med.umich.edu)
 *
 ******************************************************************************/

package edu.umich.mrc2.batchmatch.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JTextField;

import edu.umich.mrc2.batchmatch.gui.BatchMatchInputPanel;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.CommonFileTypes;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.FileChooserAction;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser.Mode;
import edu.umich.mrc2.batchmatch.main.BatchMatch;

public abstract class BatchMatchFileSelectorPanel extends BatchMatchInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JnaFileChooser fileChooser;
	protected File baseDirectory;
	protected File selectedFile;
	protected String preferencesNode;
	protected FileChooserAction fcAction;
	
	protected static final String BROWSE_COMMAND = "Browse ...";
	protected static final String BASE_DIRECTORY = "BASE_DIRECTORY";
	protected static final String SELECTED_FILE = "SELECTED_FILE";
	private JTextField selectedFilePathTextField;

	public BatchMatchFileSelectorPanel(
			String panelId, 
			String panelTitle, 
			JnaFileChooser.Mode selectionMode,
			CommonFileTypes fileType,
			FileChooserAction fcAction) {
		super(panelId, panelTitle);
		preferencesNode  = this.getClass().getName() + "." + panelId;
		this.fcAction = fcAction;
		
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		
		selectedFilePathTextField = new JTextField();
		selectedFilePathTextField.setEditable(false);
		GridBagConstraints gbc_selectedFilePathTextField = new GridBagConstraints();
		gbc_selectedFilePathTextField.insets = new Insets(0, 0, 0, 5);
		gbc_selectedFilePathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_selectedFilePathTextField.gridx = 0;
		gbc_selectedFilePathTextField.gridy = 0;
		add(selectedFilePathTextField, gbc_selectedFilePathTextField);
		selectedFilePathTextField.setColumns(10);
		
		JButton browseButton = new JButton(BROWSE_COMMAND);
		browseButton.setActionCommand(BROWSE_COMMAND);
		browseButton.addActionListener(this);
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 0;
		add(browseButton, gbc_btnNewButton);
		
		loadPreferences();
		initFileChooser(selectionMode, fileType);
	}
	
	protected void initFileChooser(
			Mode selectionMode, 
			CommonFileTypes fileType) {
		fileChooser = new JnaFileChooser(baseDirectory);
		fileChooser.setMode(JnaFileChooser.Mode.Files);
		
		if(fileType.equals(CommonFileTypes.COMMA_SEPARATED))
			fileChooser.addFilter("Comma-separated files", "csv", "CSV");
		
		if(fileType.equals(CommonFileTypes.TAB_SEPARATED))
			fileChooser.addFilter("Text files", "txt", "TXT", "tsv", "TSV");
		
		if(fileType.equals(CommonFileTypes.EXCEL))
			fileChooser.addFilter("Excel files", "xls", "XLS", "xlsx", "XLSX");
		
		fileChooser.setTitle(panelTitle);
		fileChooser.setMultiSelectionEnabled(false);
	}
	
	protected abstract void fileSelectionChanged();

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		if(command.equals(BROWSE_COMMAND)) {
			
			if(fcAction.equals(FileChooserAction.Open)) {
				
				if (fileChooser.showOpenDialog(this.getTopLevelAncestor()))
					selectFile();				
			}
			if(fcAction.equals(FileChooserAction.Save)) {
				
				if (fileChooser.showSaveDialog(this.getTopLevelAncestor()))
					selectFile();				
			}
		}
	}

	protected void selectFile() {
				
		selectedFile = fileChooser.getSelectedFile();
		selectedFilePathTextField.setText(selectedFile.getPath());
		baseDirectory = selectedFile.getParentFile();
		savePreferences();	
		fileSelectionChanged();
	}

	@Override
	public void loadPreferences(Preferences preferences) {
		
		panelPreferences = preferences;
		baseDirectory =  
				new File(preferences.get(BASE_DIRECTORY, BatchMatch.homeDirLocation));
		if(!baseDirectory.exists())
			baseDirectory = new File(BatchMatch.homeDirLocation);
		
		selectedFile =  
				new File(preferences.get(SELECTED_FILE, BatchMatch.homeDirLocation));
		if(!selectedFile.exists())
			selectedFile = null;
		
		if(selectedFile != null)
			selectedFilePathTextField.setText(selectedFile.getAbsolutePath());
	}

	@Override
	public void loadPreferences() {		
		loadPreferences(Preferences.userRoot().node(preferencesNode));
	}

	@Override
	public void savePreferences() {
		panelPreferences = Preferences.userRoot().node(preferencesNode);
		
		if(baseDirectory != null)
			panelPreferences.put(BASE_DIRECTORY, baseDirectory.getAbsolutePath());
		
		if(selectedFile != null)
			panelPreferences.put(SELECTED_FILE, selectedFile.getAbsolutePath());
	}
}
