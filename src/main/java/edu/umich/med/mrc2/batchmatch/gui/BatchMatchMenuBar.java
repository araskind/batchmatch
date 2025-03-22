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

import javax.swing.JMenu;

import edu.umich.med.mrc2.batchmatch.main.ActionCommands;

public class BatchMatchMenuBar extends CommonMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatchMatchMenuBar(ActionListener listener) {
		
		super(listener);
		
		JMenu projectMenu = new JMenu("Project");
		addItem(projectMenu, ActionCommands.CREATE_NEW_PROJECT_COMMAND);

		projectMenu.addSeparator();
		
		addItem(projectMenu, ActionCommands.OPEN_PROJECT_COMMAND);
		addItem(projectMenu, ActionCommands.SAVE_AND_CLOSE_PROJECT_COMMAND);
		
		projectMenu.addSeparator();
		
		addItem(projectMenu, ActionCommands.SET_DEFAULT_PROJECT_DIRECTORY_COMMAND);
		
		add(projectMenu);

		JMenu helpMenu = new JMenu("Help");
		addItem(helpMenu, ActionCommands.SHOW_ABOUT_DIALOG_COMMAND);		
		add(helpMenu);
	}
}
