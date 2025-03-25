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

package edu.umich.med.mrc2.batchmatch.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser;
import edu.umich.med.mrc2.batchmatch.gui.table.BinnerInputTable;
import edu.umich.med.mrc2.batchmatch.gui.utils.MessageDialog;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;

public class BatchMatchProjectSetupPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BinnerInputTable binnerInputTable;
	private JnaFileChooser fileChooser;
	private AlignmentSettingsPanel alignmentSettingsPanel;

	public BatchMatchProjectSetupPanel() {
		
		super(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(10, 0, 10, 10));
		
		alignmentSettingsPanel = new AlignmentSettingsPanel();
		add(alignmentSettingsPanel, BorderLayout.NORTH);
		
		JPanel tableWrapper = new JPanel(new BorderLayout(0,0));
		tableWrapper.setBorder(new CompoundBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 255, 255), new Color(160, 160, 160)),
				"Select input files", TitledBorder.LEADING, TitledBorder.TOP, 
				BatchMatchConfiguration.panelTitleFont, BatchMatchConfiguration.panelTitleColor), 
				new EmptyBorder(10, 10, 10, 10)));
		
		binnerInputTable = new BinnerInputTable();
		tableWrapper.add(new JScrollPane(binnerInputTable), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();		
		JButton selectAreaFilesButton = new JButton(
				BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName());
		selectAreaFilesButton.setActionCommand(
				BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName());
		selectAreaFilesButton.addActionListener(this);
		buttonPanel.add(selectAreaFilesButton);
		
		JButton selectBinnerFilesButton = new JButton(
				BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName());
		selectBinnerFilesButton.setActionCommand(
				BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName());
		selectBinnerFilesButton.addActionListener(this);
		buttonPanel.add(selectBinnerFilesButton);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);	
		
		horizontalStrut.setPreferredSize(new Dimension(80, 0));
		buttonPanel.add(horizontalStrut);
		
		JButton clearTableButton = new JButton(
				BMActionCommands.CLEAR_FILE_SELECTION_COMMAND.getName());
		clearTableButton.setActionCommand(
				BMActionCommands.CLEAR_FILE_SELECTION_COMMAND.getName());
		clearTableButton.addActionListener(this);
		buttonPanel.add(clearTableButton);
		tableWrapper.add(buttonPanel, BorderLayout.SOUTH);
		add(tableWrapper, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if(command.equals(BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName())
				|| command.equals(BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName()))
			selectFiles(command);

		if(command.equals(BMActionCommands.CLEAR_FILE_SELECTION_COMMAND.getName()))
			clearFileSelectionTable();
	}

	private void clearFileSelectionTable() {

		String message = "Do you want to clear setup table?";
		int res = MessageDialog.showChoiceWithWarningMsg(message, binnerInputTable);
		if(res == JOptionPane.YES_OPTION)
			binnerInputTable.clearTable();
	}

	private void selectFiles(String command) {

		fileChooser = new JnaFileChooser(BatchMatchConfiguration.getProjectDirectory());
		fileChooser.setMode(JnaFileChooser.Mode.Files);
		
		if(command.equals(BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName()))
			fileChooser.addFilter("Comma-separated files", "csv", "CSV");
		
		if(command.equals(BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName()))
			fileChooser.addFilter("Text files", "txt", "TXT", "tsv", "TSV");
				
		fileChooser.setTitle(command);
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(this.getTopLevelAncestor())) {
			
			File[]selectedFiles = fileChooser.getSelectedFiles();
			if(command.equals(BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName()))
				binnerInputTable.setBinnerFiles(selectedFiles);
			
			if(command.equals(BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName()))
				binnerInputTable.setPeakAreaFiles(selectedFiles);
		}
	}
	
	public Collection<String>validateProjectSetup(){
	    
	    Collection<String>errors = new ArrayList<String>();
	    
//	    if(getProjectName().isEmpty())
//	        errors.add("Project name cannot be empty.");
//	    
//	    if(baseDirectory == null || !baseDirectory.exists())
//	        errors.add("Invalid project directory.");
//	    
//	    if(!getProjectName().isEmpty() && baseDirectory != null) {
//	        
//	        File newProjectDir = 
//	                Paths.get(baseDirectory.getAbsolutePath(), getProjectName()).toFile();
//	        if(newProjectDir.exists()) {
//	            errors.add("Project \"" + getProjectName() + "\" already exists\n"
//	                    + "in the directory \"" + baseDirectory.getAbsolutePath() + "\"");
//	        }
//	    }		
	    return errors;
	}

}













