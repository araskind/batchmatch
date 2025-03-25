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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.prefs.Preferences;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import edu.umich.med.mrc2.batchmatch.gui.BatchMatchInputPanel;
import edu.umich.med.mrc2.batchmatch.gui.ListDNDHandler;
import edu.umich.med.mrc2.batchmatch.gui.utils.AltColorListCellRenderer;

public class DraggableListViewPanel extends BatchMatchInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean enableDrag = true;
	private boolean centerText = false;
	private DefaultListModel<String> strings;
	private JList<String> dndList;

	public DraggableListViewPanel(
			String panelId, 
			String panelTitle,
			boolean enableDrag, 
			boolean centerText,
			Collection<String> names) {
		super(panelId, panelTitle, false);


		this.enableDrag = enableDrag;
		this.centerText = centerText;

		strings = new DefaultListModel<String>();
		if(names != null && !names.isEmpty())
			strings.addAll(names);		

		dndList = new JList<String>(strings);
		dndList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (enableDrag) {
		      
	        dndList.setDragEnabled(true);
	        dndList.setDropMode(DropMode.INSERT);
	        dndList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        dndList.setTransferHandler(new ListDNDHandler(dndList));
		} 
		if (centerText) {
			((DefaultListCellRenderer) dndList.getCellRenderer()).
				setHorizontalAlignment(SwingConstants.CENTER);
		}
//		
//		DefaultListCellRenderer ren = new DefaultListCellRenderer();
//		ren.setFont(new Font("Arial", Font.BOLD, 16));
		dndList.setCellRenderer(new AltColorListCellRenderer());
		
		gridBagLayout.rowWeights = new double[]{1.0};
		gridBagLayout.columnWeights = new double[]{1.0};
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		add(new JScrollPane(dndList), gbc_list);
	}

	@Override
	public void loadPreferences(Preferences preferences) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadPreferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void savePreferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
