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

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;

public class CommonMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final Dimension preferredSize = new Dimension(80, 20);
	protected ActionListener alistener;
	
	public CommonMenuBar(ActionListener alistener) {
		super();
		this.alistener = alistener;
	}
	
	protected JMenuItem addItem(JMenu menu, BMActionCommands command) {

		JMenuItem item = new JMenuItem(command.getName());
		item.setActionCommand(command.getName());
		item.addActionListener(alistener);
		item.setIcon(null);
		menu.add(item);
		return item;
	}
	
	protected JMenuItem addItem(JMenu menu, BMActionCommands command, Icon defaultIcon) {

		JMenuItem item = new JMenuItem(command.getName());
		item.setActionCommand(command.getName());
		item.addActionListener(alistener);
		item.setIcon(defaultIcon);
		menu.add(item);
		return item;
	}
	
	protected JMenuItem addItem(JMenu menu, String title, BMActionCommands command, Icon defaultIcon) {

		JMenuItem item = new JMenuItem(title);
		item.setActionCommand(command.getName());
		item.addActionListener(alistener);
		item.setIcon(defaultIcon);
		menu.add(item);
		return item;
	}
	
	protected JMenuItem addItem(JMenu menu, String title, String command, Icon defaultIcon) {

		JMenuItem item = new JMenuItem(title);
		item.setActionCommand(command);
		item.addActionListener(alistener);
		item.setIcon(defaultIcon);
		menu.add(item);
		return item;
	}
	
	protected JMenuItem addExpandableItem(JMenu menu, String title, Icon defaultIcon) {

		JMenuItem item = new JMenuItem(title);
		item.setIcon(defaultIcon);
		menu.add(item);
		return item;
	}
}
