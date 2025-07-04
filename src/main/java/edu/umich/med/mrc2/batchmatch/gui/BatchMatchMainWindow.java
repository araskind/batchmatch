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

package edu.umich.med.mrc2.batchmatch.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.StringUtils;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser;
import edu.umich.med.mrc2.batchmatch.gui.panels.BatchMatchProjectSetupPanel;
import edu.umich.med.mrc2.batchmatch.gui.utils.GuiUtils;
import edu.umich.med.mrc2.batchmatch.gui.utils.MessageDialog;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;
import edu.umich.med.mrc2.batchmatch.project.BatchMatchProject;
import edu.umich.med.mrc2.batchmatch.taskcontrol.AbstractTask;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskControlListener;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskEvent;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskListener;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskStatus;
import edu.umich.med.mrc2.batchmatch.taskcontrol.gui.TaskProgressPanel;
import edu.umich.med.mrc2.batchmatch.taskcontrol.tasks.LatticeGenerationTask;
import edu.umich.med.mrc2.batchmatch.utils.FIOUtils;
import edu.umich.med.mrc2.batchmatch.utils.ProjectUtils;
import edu.umich.med.mrc2.batchmatch.utils.TextUtils;

public class BatchMatchMainWindow  extends JFrame 
	implements ActionListener, WindowListener, TaskListener, TaskControlListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static JDialog progressDialogue;
	private static TaskProgressPanel progressPanel;
	public static StatusBar statusBar;
	
	private JTabbedPane workflowTabbbedPanel;
	private BatchMatchProjectSetupPanel projectSetupPanel;
	
	public BatchMatchMainWindow() {
		
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		setTitle("BatchMatch v" + BatchMatchConstants.VERSION);
		setSize(1300, 900);
		setLocationRelativeTo(null);
		addWindowListener(this);
		setJMenuBar(new BatchMatchMenuBar(this));
		
		setLayout(new BorderLayout(0, 0));
		
		workflowTabbbedPanel = new JTabbedPane();
		
		projectSetupPanel = new BatchMatchProjectSetupPanel();

		JPanel wrapper = new JPanel(new BorderLayout(0, 0));
		wrapper.add(projectSetupPanel, BorderLayout.CENTER);
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton runBatchMatchButton = 
				new JButton(BMActionCommands.RUN_BATCH_MATCH_COMMAND.getName());
		runBatchMatchButton.setActionCommand(
				BMActionCommands.RUN_BATCH_MATCH_COMMAND.getName());
		runBatchMatchButton.setIcon(GuiUtils.getIcon("actions", 24));
		runBatchMatchButton.addActionListener(this);
		panel1.add(runBatchMatchButton);
		wrapper.add(panel1, BorderLayout.SOUTH);
		
		workflowTabbbedPanel.addTab("Project setup", wrapper);
			
		add(workflowTabbbedPanel, BorderLayout.CENTER);
		
		statusBar = new StatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		initProgressDialog();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if(command.equals(BMActionCommands.CREATE_NEW_PROJECT_COMMAND.getName()))
			createNewProject();
		
		if(command.equals(BMActionCommands.OPEN_PROJECT_COMMAND.getName()))
			openProject();
		
		if(command.equals(BMActionCommands.SAVE_PROJECT_COMMAND.getName()))
			saveProject();
		
		if(command.equals(BMActionCommands.SAVE_AND_CLOSE_PROJECT_COMMAND.getName()))
			saveAndCloseProject();
		
		if(command.equals(BMActionCommands.GO_TO_EXPERIMENT_FOLDER_COMMAND.getName()))
			goToProjectFolder();
		
		if(command.equals(BMActionCommands.GLOBAL_SETTINGS_COMMAND.getName()))
			adjustGlobalSettings();
		
		if(command.equals(BMActionCommands.SHOW_ABOUT_DIALOG_COMMAND.getName()))
			showAboutDialog();
		
		if(command.equals(BMActionCommands.EXIT_COMMAND.getName()))
			exitSoftware();
		
		if(command.equals(BMActionCommands.RUN_BATCH_MATCH_COMMAND.getName()))
			runBatchMatch();
	}
	
	private void goToProjectFolder() {

		File expDir = null;
		if (BatchMatch.getCurrentProject() != null) 			
			expDir = BatchMatch.getCurrentProject().getProjectFile().getParentFile();
		
		if(expDir != null && expDir.exists()) {
			
			try {
				Desktop.getDesktop().open(expDir);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void runBatchMatch() {

		//	Validate inputs, update parameters and save the project
		if(!saveProject())
			return;
		
		//	Initiate alignment run - generate lattices
		LatticeGenerationTask task = 
				new LatticeGenerationTask(BatchMatch.getCurrentProject());
		task.addTaskListener(this);
		BatchMatch.getTaskController().addTask(task);
	}

	private void exitSoftware() {
		
		// TODO Deal with existing project
		BatchMatch.shutDown();
	}

	public static void showProgressDialog() {

		if (!progressDialogue.isVisible() && !BatchMatch.getTaskController().getTaskQueue().isEmpty()) {

			try {
				progressDialogue.setLocationRelativeTo(BatchMatch.getMainWindow());
				progressDialogue.setVisible(true);
			} catch (Exception e) {

				// e.printStackTrace();
			}
		}
	}
	
	public static void hideProgressDialog() {
		progressDialogue.setVisible(false);
	}

	private void initProgressDialog() {

		progressPanel = BatchMatch.getTaskController().getTaskPanel();
		progressDialogue = new JDialog(this, "Task in progress...", ModalityType.APPLICATION_MODAL);
		progressDialogue.setTitle("Operation in progress ...");
		progressDialogue.setSize(new Dimension(600, 150));
		progressDialogue.setPreferredSize(new Dimension(600, 150));
		progressDialogue.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		progressDialogue.getContentPane().setLayout(new BorderLayout());
		progressDialogue.getContentPane().add(progressPanel, BorderLayout.CENTER);
		progressDialogue.setLocationRelativeTo(this);
		progressDialogue.pack();
		progressDialogue.setVisible(false);
	}
	
	private void createNewProject() {

		JnaFileChooser fileChooser = 
				new JnaFileChooser(BatchMatchConfiguration.getProjectDirectory());
		fileChooser.setMode(JnaFileChooser.Mode.Files);
		fileChooser.setTitle("Create new project");
		fileChooser.setSaveButtonText("Create new project");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDefaultFileName("New BatchMatch project " + 
		BatchMatchConfiguration.defaultFileTimeStampFormat.format(new Date()));
		if (fileChooser.showSaveDialog(this)) {
			
			if(fileChooser.getSelectedFile() != null) {

				BatchMatchProject proj = null;
				try {
					proj = new BatchMatchProject(fileChooser.getSelectedFile().getName(), 
							fileChooser.getSelectedFile().getParentFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(proj != null)
					ProjectUtils.saveProject(proj);
									
				BatchMatch.setCurrentProject(proj);
				setGUIfromProject(proj);
			}
		}
	}

	private void openProject() {
		
		if(BatchMatch.getCurrentProject() != null) {
			MessageDialog.showWarningMsg(
					"Please close the current project \"" + 
							BatchMatch.getCurrentProject().getProjectName() + "\" first.", this);
			return;
		}		
		File projectFile = null;

		JnaFileChooser fileChooser = 
				new JnaFileChooser(BatchMatchConfiguration.getProjectDirectory());
		fileChooser.setMode(JnaFileChooser.Mode.FilesAndDirectories);
		fileChooser.setTitle("Open project");
		fileChooser.setOpenButtonText("Open project");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addFilter("BatchMatch projects", BatchMatchConfiguration.BATCH_MATCH_PROJECT_FILE_EXTENSION);
		if (fileChooser.showOpenDialog(this)) {
			
			if(fileChooser.getSelectedFile() != null) {
				
				File selectedFile = fileChooser.getSelectedFile();
				if(selectedFile.isDirectory()) {
					List<Path> pfList = FIOUtils.findFilesByExtension(
							Paths.get(selectedFile.getAbsolutePath()), 
							BatchMatchConfiguration.BATCH_MATCH_PROJECT_FILE_EXTENSION);
					if(pfList == null || pfList.isEmpty()) {
						MessageDialog.showWarningMsg(selectedFile.getName() + 
								" is not a valid batchMatch project", BatchMatch.getMainWindow());
						return;
					}
					projectFile = pfList.get(0).toFile();
				}
				else {
					projectFile = selectedFile;
				}				
				BatchMatchProject proj = ProjectUtils.readProjectFromFile(projectFile);
				BatchMatch.setCurrentProject(proj);
				setGUIfromProject(proj);
			}
		}		
	}

	private void saveAndCloseProject() {

		if(!saveProject())
			return;
		
		BatchMatch.setCurrentProject(null);
		setGUIfromProject(null);
	}
	
	private boolean saveProject() {

		if(BatchMatch.getCurrentProject() == null)
			return false;
			
		Collection<String>errors = validateProjectSetup();
		if(!errors.isEmpty()) {
		    MessageDialog.showErrorMsg(StringUtils.join(errors, "\n"), this);
		    return false;
		}
		updateProjectWithNewParameters();
		ProjectUtils.saveProject(BatchMatch.getCurrentProject());		
		return true;
	}
	
	private void updateProjectWithNewParameters() {

		//	Input objects
		BatchMatchProject proj = BatchMatch.getCurrentProject();
		Collection<BatchMatchInputObject>bmioSet = 
				projectSetupPanel.getBatchMatchInputObject();
		
		proj.getInputObjects().clear();
		proj.getInputObjects().addAll(bmioSet);
		
		//	Alignment parameters
		projectSetupPanel.updateAlignmentSettingsForProject(proj);
	}
	
	private void setGUIfromProject(BatchMatchProject proj) {
		
		String title = "BatchMatch v" + BatchMatchConstants.VERSION;
		if(proj != null)
			title += " | " + proj.getProjectName();
		
		setTitle(title);
		StatusBar.showRawDataAnalysisExperimentData(proj);
		
		// 	Load project settings and data files		
		if(proj == null)
			projectSetupPanel.clearPanel();
		else
			projectSetupPanel.loadProjectData(proj);
	}

	private void adjustGlobalSettings() {

		GlobalSettingsDialog dialog = new GlobalSettingsDialog();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showAboutDialog() {
		AboutDialog ad = new AboutDialog();
		ad.setLocationRelativeTo(this);
		ad.setVisible(true);
	}
	
	public Collection<String>validateProjectSetup(){
	    
	    Collection<String>errors = new ArrayList<String>();
	    errors.addAll(projectSetupPanel.validateProjectSetup(false));
		
	    return errors;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		BatchMatch.shutDown();	
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void displayErrorMessage(String title, String msg) {

		assert msg != null;

		String wrappedMsg;
		if (msg.contains("\n"))
			wrappedMsg = msg;
		else
			wrappedMsg = TextUtils.wrapText(msg, 80);

		JOptionPane.showMessageDialog(BatchMatch.getMainWindow(), 
				wrappedMsg, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void statusChanged(TaskEvent e) {
		
		if (e.getStatus() == TaskStatus.FINISHED) {

			((AbstractTask) e.getSource()).removeTaskListener(this);
			if (e.getSource().getClass().equals(LatticeGenerationTask.class)) {
				MessageDialog.showInfoMsg("Lattice generation completed", this);
			}
		}
		if (e.getStatus() == TaskStatus.ERROR || e.getStatus() == TaskStatus.CANCELED) {
			BatchMatch.getTaskController().getTaskQueue().clear();
			hideProgressDialog();
		}
	}

	@Override
	public void allTasksFinished(boolean atf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void numberOfWaitingTasksChanged(int numOfTasks) {
		// TODO Auto-generated method stub
		
	}
}
