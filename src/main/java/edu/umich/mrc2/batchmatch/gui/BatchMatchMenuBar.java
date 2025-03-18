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

package edu.umich.mrc2.batchmatch.gui;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.umich.mrc2.batchmatch.main.ActionCommands;

public class BatchMatchMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatchMatchMenuBar(ActionListener listener) {
		
		super();
		
		setFont(new Font("Courier New", Font.BOLD, 18));
		
//		JMenu file = new JMenu(" ");
//		// file.setMnemonic(KeyEvent.VK_F);
//
//		JMenuItem blank = new JMenuItem(" ");
//		file.add(blank);

		JMenu help = new JMenu("Info");
		// help.setMnemonic(KeyEvent.VK_H);

		JMenuItem about = new JMenuItem(ActionCommands.SHOW_ABOUT_DIALOG_COMMAND.getName());
		about.setActionCommand(ActionCommands.SHOW_ABOUT_DIALOG_COMMAND.getName());
		about.addActionListener(listener);
		help.add(about);

		//	add(Box.createHorizontalGlue());
		add(help);
//		add(file);
	}

}
