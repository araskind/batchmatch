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
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import edu.umich.med.mrc2.batchmatch.gui.panels.BatchMatchProjectSetupPanel;
import edu.umich.med.mrc2.batchmatch.gui.utils.GuiUtils;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskEvent;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskListener;
import edu.umich.med.mrc2.batchmatch.taskcontrol.gui.TaskProgressPanel;
import edu.umich.med.mrc2.batchmatch.taskcontrol.tasks.TestTask;
import edu.umich.med.mrc2.batchmatch.utils.TextUtils;

public class BatchMatchMainWindow  extends JFrame implements ActionListener, WindowListener, TaskListener {

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
		
		if(command.equals(BMActionCommands.SAVE_AND_CLOSE_PROJECT_COMMAND.getName()))
			saveAndCloseProject();
		
		if(command.equals(BMActionCommands.GLOBAL_SETTINGS_COMMAND.getName()))
			setDefaultProjectDirectory();
		
		if(command.equals(BMActionCommands.SHOW_ABOUT_DIALOG_COMMAND.getName()))
			showAboutDialog();
		
		if(command.equals(BMActionCommands.EXIT_COMMAND.getName()))
			exitSoftware();
		
		if(command.equals(BMActionCommands.RUN_BATCH_MATCH_COMMAND.getName()))
			runBatchMatch();
	}
	
	private void runBatchMatch() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		TestTask task = new TestTask();
		task.addTaskListener(this);
		BatchMatch.getTaskController().addTask(task);
	}

	private void openProject() {
		// TODO Auto-generated method stub
		
	}

	private void saveAndCloseProject() {
		// TODO Auto-generated method stub
		
	}

	private void setDefaultProjectDirectory() {

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
	    errors.addAll(projectSetupPanel.validateProjectSetup());
	    
	    
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
		// TODO Auto-generated method stub
		hideProgressDialog();
	}
}
