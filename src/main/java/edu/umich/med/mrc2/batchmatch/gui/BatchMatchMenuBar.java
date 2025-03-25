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

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenu;

import edu.umich.med.mrc2.batchmatch.gui.utils.GuiUtils;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;

public class BatchMatchMenuBar extends CommonMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Icon newProjectIcon = GuiUtils.getIcon("newProject", 24);
	private static final Icon openProjectIcon = GuiUtils.getIcon("open", 24);
	private static final Icon saveProjectIcon = GuiUtils.getIcon("save", 24);
	private static final Icon settingsIcon = GuiUtils.getIcon("preferences", 24);
	private static final Icon aboutIcon = GuiUtils.getIcon("help", 24);
	private static final Icon exitIcon = GuiUtils.getIcon("shutDown", 24);

	public BatchMatchMenuBar(ActionListener listener) {
		
		super(listener);
		
		JMenu projectMenu = new JMenu("Project");
		addItem(projectMenu, BMActionCommands.CREATE_NEW_PROJECT_COMMAND, newProjectIcon);

		projectMenu.addSeparator();
		
		addItem(projectMenu, BMActionCommands.OPEN_PROJECT_COMMAND, openProjectIcon);
		addItem(projectMenu, BMActionCommands.SAVE_AND_CLOSE_PROJECT_COMMAND, saveProjectIcon);
		
		projectMenu.addSeparator();
		
		addItem(projectMenu, BMActionCommands.GLOBAL_SETTINGS_COMMAND, settingsIcon);
		
		projectMenu.addSeparator();
		
		addItem(projectMenu, BMActionCommands.EXIT_COMMAND, exitIcon);
		
		add(projectMenu);

		JMenu helpMenu = new JMenu("Help");
		addItem(helpMenu, BMActionCommands.SHOW_ABOUT_DIALOG_COMMAND, aboutIcon);		
		add(helpMenu);
	}
}
