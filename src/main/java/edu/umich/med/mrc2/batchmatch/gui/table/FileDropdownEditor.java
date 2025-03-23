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

package edu.umich.med.mrc2.batchmatch.gui.table;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import edu.umich.med.mrc2.batchmatch.gui.FileNameComboboxRenderer;
import edu.umich.med.mrc2.batchmatch.gui.SortedComboBoxModel;

public class FileDropdownEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FileDropdownEditor(JTable table) {
		this(new ArrayList<File>(), table);
	}
	
	@SuppressWarnings("unchecked")
	public FileDropdownEditor(Collection<File>files, JTable table) {

		super(new JComboBox<File>());
		JComboBox<File> cBox = ((JComboBox<File>)editorComponent);
		cBox.setRenderer(new FileNameComboboxRenderer());
		cBox.setModel(new SortedComboBoxModel(files));
		cBox.setToolTipText("Click to select value");
		cBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {

				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					stopCellEditing();
					((DefaultTableModel) table.getModel()).fireTableDataChanged();
				}
			}
		});
		cBox.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				cBox.showPopup();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getCellEditorValue() {
		return ((JComboBox<File>)editorComponent).getSelectedItem();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellEditorComponent(
			JTable table, Object value, boolean isSelected, int row, int column) {

		if (value instanceof File || value == null) {

			JComboBox cBox = ((JComboBox)editorComponent);
			cBox.setSelectedIndex(-1);

			File selectedFile = (File) value;
			if (selectedFile != null)
				cBox.setSelectedItem(selectedFile);
		}
		return editorComponent;
	}
	
	public void setSelectorModelFromFiles(Collection<? extends File>files) {
		((JComboBox)editorComponent).setModel(new SortedComboBoxModel(files));
	}
}
