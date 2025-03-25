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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import edu.umich.med.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;

public class GlobalSettingsDialog extends JDialog implements ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String BROWSE_COMMAND = "BROWSE_COMMAND";
	private JTextField defaultProjetDirTextField;
	private File defaultProjectDirectory;
	private JSpinner maxThreadsSpinner;
	
	public GlobalSettingsDialog() {
		super();
		setTitle("BatchMatch global settings");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(new Dimension(640, 250));
		setPreferredSize(new Dimension(640, 250));
		setResizable(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{176, 176, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Default project directory");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		defaultProjectDirectory = BatchMatchConfiguration.getProjectDirectory();
		defaultProjetDirTextField = new JTextField(
				defaultProjectDirectory.getAbsolutePath());
		defaultProjetDirTextField.setEditable(false);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 3;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		panel.add(defaultProjetDirTextField, gbc_textField);
		defaultProjetDirTextField.setColumns(10);
		
		JButton browseButton = new JButton("Browse ...");
		browseButton.setActionCommand(BROWSE_COMMAND);
		browseButton.addActionListener(this);
		
		GridBagConstraints gbc_browseButton = new GridBagConstraints();
		gbc_browseButton.insets = new Insets(0, 0, 5, 0);
		gbc_browseButton.gridx = 3;
		gbc_browseButton.gridy = 1;
		panel.add(browseButton, gbc_browseButton);
		
		JLabel lblNewLabel_1 = new JLabel("Max number of parallel tasks");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		maxThreadsSpinner = new JSpinner();
		maxThreadsSpinner.setPreferredSize(new Dimension(80, 20));
		maxThreadsSpinner.setModel(new SpinnerNumberModel(3, 1, 16, 1));
		maxThreadsSpinner.setValue(BatchMatchConfiguration.getMaxWorkingThreads());
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.anchor = GridBagConstraints.WEST;
		gbc_spinner.insets = new Insets(0, 0, 0, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 2;
		panel.add(maxThreadsSpinner, gbc_spinner);
		
		JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		panel_1.setBorder(new EmptyBorder(10, 5, 10, 5));
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JButton saveButton = new JButton(BMActionCommands.SAVE_SETTINGS_COMMAND.getName());
		saveButton.setActionCommand(BMActionCommands.SAVE_SETTINGS_COMMAND.getName());
		saveButton.addActionListener(this);
		panel_1.add(saveButton);
		
		JButton cancelButton = new JButton("Cancel");
		panel_1.add(cancelButton);		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		};
		cancelButton.addActionListener(al);
		
		JRootPane rootPane = SwingUtilities.getRootPane(saveButton);
		rootPane.registerKeyboardAction(al, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		rootPane.setDefaultButton(saveButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals(BROWSE_COMMAND))
				selectDefaultProjectDirectory();
		
		if(e.getActionCommand().equals(BMActionCommands.SAVE_SETTINGS_COMMAND.getName()))
			saveNewGlobalSettings();
	}

	private void selectDefaultProjectDirectory() {

		JnaFileChooser fileChooser = 
				new JnaFileChooser(BatchMatchConfiguration.getProjectDirectory());
		fileChooser.setMode(JnaFileChooser.Mode.Directories);
		fileChooser.setTitle("Select default project directory");
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(this)) {
			
			if(fileChooser.getSelectedFile() != null) {
				defaultProjectDirectory = fileChooser.getSelectedFile();
				defaultProjetDirTextField.setText(defaultProjectDirectory.getAbsolutePath());
			}
		}
	}

	private void saveNewGlobalSettings() {

		BatchMatchConfiguration.setDefaultProjectDirectory(defaultProjectDirectory);
		BatchMatchConfiguration.setMaxWorkingThreads((Integer)maxThreadsSpinner.getValue());
		this.dispose();
	}
}














