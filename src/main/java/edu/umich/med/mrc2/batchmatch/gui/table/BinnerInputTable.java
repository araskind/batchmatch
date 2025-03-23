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

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.table.TableRowSorter;

public class BinnerInputTable extends BasicTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FileDropdownEditor areaFilesEditor;
	private FileDropdownEditor binnerFilesEditor;

	public BinnerInputTable() {
		super();
		model = new BinnerInputTableModel();
		setModel(model);
		getTableHeader().setReorderingAllowed(false);
		rowSorter = new TableRowSorter<BinnerInputTableModel>(
				(BinnerInputTableModel)model);
		setRowSorter(rowSorter);
		
		areaFilesEditor = new FileDropdownEditor(this);		
		columnModel.getColumn(model.getColumnIndex(BinnerInputTableModel.PEAK_AREAS_FILE_COLUMN))
			.setCellEditor(areaFilesEditor);
		
		binnerFilesEditor = new FileDropdownEditor(this);	
		columnModel.getColumn(model.getColumnIndex(BinnerInputTableModel.BINNER_OUTPUT_FILE_COLUMN))
			.setCellEditor(binnerFilesEditor);
		setDefaultRenderer(File.class, new FileNameRenderer());
		setDefaultRenderer(Boolean.class, new RadioButtonRenderer());
		setDefaultEditor(Boolean.class, new RadioButtonEditor(new JCheckBox()));
		
		columnModel.getColumn(model.getColumnIndex(
				BinnerInputTableModel.BATCH_NUMBER_COLUMN)).setMaxWidth(80);
		columnModel.getColumn(model.getColumnIndex(
				BinnerInputTableModel.PRIMARY_BATCH_COLUMN)).setMaxWidth(80);
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		if(convertColumnIndexToModel(column) == 
				model.getColumnIndex(BinnerInputTableModel.BATCH_NUMBER_COLUMN))
			return row+1;
		else
			return super.getValueAt(row, column);
	}
	
	public void setPeakAreaFiles(File[]peakAreaFiles) {
		
		int inserted = 0;
		int colIndex = model.getColumnIndex(BinnerInputTableModel.PEAK_AREAS_FILE_COLUMN);
		
		//	Clear existing data
		for(int i=0; i<model.getRowCount(); i++)
			model.setValueAt(null, i, colIndex);
		
		int iteratorLength = Math.min(peakAreaFiles.length, model.getRowCount());
		for(int i=0; i<iteratorLength; i++) {
			model.setValueAt(peakAreaFiles[i], i, colIndex);
			inserted++;
		}
		if(peakAreaFiles.length > inserted + 1) {
			
			for(int i=inserted; i<peakAreaFiles.length; i++) {
				
				Object[]rowData = new Object[] {
						inserted + 1,
						peakAreaFiles[i],
						null,
						false,
				};
				model.addRow(rowData);
			}
		}
		removeEmptyRows();
		areaFilesEditor.setSelectorModelFromFiles(Arrays.asList(peakAreaFiles));
	}
	
	public void setBinnerFiles(File[]binnerFiles) {
		
		int inserted = 0;
		int colIndex = model.getColumnIndex(BinnerInputTableModel.BINNER_OUTPUT_FILE_COLUMN);
		
		//	Clear existing data
		for(int i=0; i<model.getRowCount(); i++)
			model.setValueAt(null, i, colIndex);
		
		int iteratorLength = Math.min(binnerFiles.length, model.getRowCount());
		for(int i=0; i<iteratorLength; i++) {
			model.setValueAt(binnerFiles[i], i, colIndex);
			inserted++;
		}
		if(binnerFiles.length > inserted + 1) {
			
			for(int i=inserted; i<binnerFiles.length; i++) {
				
				Object[]rowData = new Object[] {
						inserted + 1,
						null,
						binnerFiles[i],
						false,
				};
				model.addRow(rowData);
			}
		}
		removeEmptyRows();
		binnerFilesEditor.setSelectorModelFromFiles(Arrays.asList(binnerFiles));
	}
	
	private void removeEmptyRows() {
		
		int binnerColIndex = model.getColumnIndex(BinnerInputTableModel.BINNER_OUTPUT_FILE_COLUMN);
		int areaColIndex = model.getColumnIndex(BinnerInputTableModel.PEAK_AREAS_FILE_COLUMN);
		Set<Integer>toRemove = new TreeSet<Integer>();
		for(int i=0; i<model.getRowCount(); i++) {
			
			if(model.getValueAt(i, areaColIndex) == null 
					&& model.getValueAt(i, binnerColIndex) == null)
				toRemove.add(i);
		}
		if(!toRemove.isEmpty()) {
			int[] idx = toRemove.stream().mapToInt(Integer::intValue).toArray(); 
			model.removeRows(idx);
		}
	}
}









