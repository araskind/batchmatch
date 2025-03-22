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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import edu.umich.med.mrc2.batchmatch.main.ActionCommands;
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
		
		initProgressDialog();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if(command.equals(ActionCommands.CREATE_NEW_PROJECT_COMMAND.getName()))
			createNewProject();
		
		if(command.equals(ActionCommands.OPEN_PROJECT_COMMAND.getName()))
			openProject();
		
		if(command.equals(ActionCommands.SAVE_AND_CLOSE_PROJECT_COMMAND.getName()))
			saveAndCloseProject();
		
		if(command.equals(ActionCommands.SET_DEFAULT_PROJECT_DIRECTORY_COMMAND.getName()))
			setDefaultProjectDirectory();
		
		if(command.equals(ActionCommands.SHOW_ABOUT_DIALOG_COMMAND.getName()))
			showAboutDialog();
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
