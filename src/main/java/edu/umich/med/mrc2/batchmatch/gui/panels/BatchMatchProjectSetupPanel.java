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
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser;
import edu.umich.med.mrc2.batchmatch.gui.table.BatchMatchInputTable;
import edu.umich.med.mrc2.batchmatch.gui.table.BatchMatchInputTableModel;
import edu.umich.med.mrc2.batchmatch.gui.utils.MessageDialog;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;
import edu.umich.med.mrc2.batchmatch.project.BatchMatchProject;

public class BatchMatchProjectSetupPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BatchMatchInputTable batchMatchInputTable;
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
		
		batchMatchInputTable = new BatchMatchInputTable();
		tableWrapper.add(new JScrollPane(batchMatchInputTable), BorderLayout.CENTER);
		
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
		
		BatchMatchProject project = BatchMatch.getCurrentProject();
		if(project == null) {
			MessageDialog.showWarningMsg("No active project", this);
			return;
		}		
		if(command.equals(BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName())
				|| command.equals(BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName()))
			selectFiles(command);

		if(command.equals(BMActionCommands.CLEAR_FILE_SELECTION_COMMAND.getName()))
			clearFileSelectionTable();
	}

	private void clearFileSelectionTable() {

		String message = "Do you want to clear setup table?";
		int res = MessageDialog.showChoiceWithWarningMsg(message, batchMatchInputTable);
		if(res == JOptionPane.YES_OPTION)
			batchMatchInputTable.clearTable();
	}

	private void selectFiles(String command) {

		fileChooser = new JnaFileChooser(BatchMatchConfiguration.getProjectDirectory());
		fileChooser.setMode(JnaFileChooser.Mode.Files);
		
		if(command.equals(BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName())) {
			fileChooser.addFilter("Comma-separated files", "csv", "CSV");
			fileChooser.setCurrentDirectory(
					BatchMatch.getCurrentProject().getBinnerFilesDirectory().getAbsolutePath());
		}
		if(command.equals(BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName())) {
			fileChooser.addFilter("Text files", "txt", "TXT", "tsv", "TSV");
			fileChooser.setCurrentDirectory(
					BatchMatch.getCurrentProject().getRawInputFilesDirectory().getAbsolutePath());
		}				
		fileChooser.setTitle(command);
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(this.getTopLevelAncestor())) {
			
			File[]selectedFiles = fileChooser.getSelectedFiles();
			if(command.equals(BMActionCommands.SELECT_BINNER_FILES_COMMAND.getName()))
				batchMatchInputTable.setBinnerFiles(selectedFiles);
			
			if(command.equals(BMActionCommands.SELECT_PEAK_AREA_FILES_COMMAND.getName()))
				batchMatchInputTable.setPeakAreaFiles(selectedFiles);
		}
	}
	
	public Collection<String>validateProjectSetup(boolean ignoreNoInput){
	    
	    Collection<String>errors = new ArrayList<String>();
	    
	    Collection<BatchMatchInputObject>bmioSet = 
	    		batchMatchInputTable.getBatchMatchInputObject();
	    
	    if(bmioSet.isEmpty() && !ignoreNoInput)
	    	errors.add("No input data specified or specification incomplete:\n\n"
	    			+ "Peak area file and binnerized file must be present for each batch\n"
	    			+ "Primary batch for alignment must be selected\n");
	    
	    Set<File>distinctPeakAreaFiles = 
	    		bmioSet.stream().map(o -> o.getPeakAreasFile()).
	    		collect(Collectors.toSet());
	    if(distinctPeakAreaFiles.size() < bmioSet.size())
	    	errors.add("Duplicate values in \"" + 
	    			BatchMatchInputTableModel.PEAK_AREAS_FILE_COLUMN + "\" column");
		
	    Set<File>distinctBinnerFiles = 
	    		bmioSet.stream().map(o -> o.getBinnerizedDataFile()).
	    		collect(Collectors.toSet());
	    if(distinctBinnerFiles.size() < bmioSet.size())
	    	errors.add("Duplicate values in \"" + 
	    			BatchMatchInputTableModel.BINNER_OUTPUT_FILE_COLUMN + "\" column");
	    
	    BatchMatchInputObject targetBatch = 
	    		bmioSet.stream().filter(o -> o.isTargetBatch()).
	    		findFirst().orElse(null);
	    if(targetBatch == null)
	    	errors.add("Primary batch not selected");
	    
	    errors.addAll(alignmentSettingsPanel.validateProjectSetup());
	    
	    return errors;
	}
	
	public void clearPanel() {
		batchMatchInputTable.clearTable();
	}
	
	public void loadProjectData(BatchMatchProject project) {
		
		batchMatchInputTable.setTableModelFromBatchMatchInputObjectCollection(
				project.getInputObjects());
		alignmentSettingsPanel.loadSettingsFromProject(project);
	}
	
	public Collection<BatchMatchInputObject>getBatchMatchInputObject(){
	    return batchMatchInputTable.getBatchMatchInputObject();
	}

}













