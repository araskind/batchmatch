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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class BasicTableModel extends DefaultTableModel implements Reorderable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean suppressEvents;
	protected ColumnContext[] columnArray;

	public BasicTableModel() {
		super();
		columnArray = new ColumnContext[0] ;
	}

	@Override
	public Class<?> getColumnClass(int modelIndex) {
		return columnArray[modelIndex].columnClass;
	}

	@Override
	public int getColumnCount() {
		return columnArray.length;
	}

	@Override
	public String getColumnName(int modelIndex) {
		return columnArray[modelIndex].columnName;
	}
	
	public String getColumnTooltip(int modelIndex) {
		return columnArray[modelIndex].columnTooltip;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return columnArray[col].isEditable;
	}

	public int getColumnIndex(String columnName) {

		int index = -1;

		for (int i = 0; i < columnArray.length; i++) {

			if (columnName.equalsIgnoreCase(columnArray[i].columnName))
				index = i;
		}
		return index;
	}
	
	public ColumnContext[] getColumnArray() {
		return columnArray;
	}
	
	public void addRows(List<Object[]>rowData) {
		
        int rowCount = getRowCount();
        List<Vector<Object>>vRowData = new ArrayList<Vector<Object>>();
        rowData.stream().forEach(r -> vRowData.add(convertToVector(r)));
		dataVector.addAll(vRowData);
		fireTableRowsInserted(rowCount, getRowCount() - 1);
	}

	public boolean isSuppressEvents() {
		return suppressEvents;
	}

	public void setSuppressEvents(boolean suppressEvents) {
		this.suppressEvents = suppressEvents;
	}
	
	@Override
    public void setValueAt(Object aValue, int row, int column) {
        @SuppressWarnings("unchecked")
        Vector<Object> rowVector = dataVector.elementAt(row);
        rowVector.setElementAt(aValue, column);
        if(suppressEvents)
        	return;
        else
        	fireTableCellUpdated(row, column);
    }
	
	@Override
	public void reorder(int fromIndex, int toIndex) {
		moveRow(fromIndex, fromIndex, toIndex);
	}
	
	public void removeRows(int[] indices) {
	    Arrays.sort(indices);
	    for (int i = indices.length - 1; i >= 0; i--)
	        this.dataVector.remove(indices[i]);
	        
	    
	    fireTableRowsDeleted(indices[0], indices[indices.length - 1]);
	}	
}
