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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.stream.IntStream;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class BasicTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Color ALTERNATE_ROW_COLOR = Color.decode("#E5FFCC");
	public static final Color WHITE_COLOR = Color.WHITE;
	
	protected BasicTableModel model;
	protected TableRowSorter<? extends TableModel> rowSorter;
	protected RadioButtonRenderer radioRenderer;
	protected RadioButtonEditor radioEditor;

	public BasicTable() {
		super();
		setBackground(WHITE_COLOR);
		initTable();;	
	}
	
	public BasicTable(AbstractTableModel tableModel) {

		setModel(tableModel);
		initTable();
	}
	
	private void initTable() {

		setBackground(WHITE_COLOR);
		setAlignmentY(Component.TOP_ALIGNMENT);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setAutoCreateRowSorter(true);

		setShowGrid(false);
		setRowMargin(2);
		setIntercellSpacing(new Dimension(2, 2));
		setRowHeight(25);

		setFillsViewportHeight(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

		Component returnComp = null;
		try {
			returnComp = super.prepareRenderer(renderer, row, column);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if (returnComp != null) {

			if (isRowSelected(row) && !getColumnSelectionAllowed()) {

				returnComp.setBackground(getSelectionBackground());
			} else if (row == getSelectedRow() && column == getSelectedColumn()) {

				returnComp.setBackground(getSelectionBackground());
			} else {

				Color bg = (row % 2 == 0 ? ALTERNATE_ROW_COLOR : WHITE_COLOR);
				returnComp.setBackground(bg);
				bg = null;
			}
		}
		return returnComp;
	}
	
	public void clearTable() {
		
		if(getModel().getRowCount() == 0)
			return;

		((BasicTableModel) this.getModel()).setRowCount(0);
	}
	
	public String getTableDataAsString() {
		int [] rows = IntStream.range(0, getRowCount()).toArray();
		return getTableDataAsString(rows);
	}
	
	public String getTableDataAsString(int[] rows) {
		
		if(rows.length == 0)
			return "";
		
		StringBuffer tableData = new StringBuffer();
		int numCols = getColumnCount();

		tableData.append(getColumnName(0));

		for(int i=1; i<numCols; i++)
			tableData.append("\t" + getColumnName(i));

		tableData.append("\n");

		for(int i : rows){

			for(int j=0; j<numCols; j++){
				
				tableData.append(getCellStringValue(i,j));

                if(j<numCols-1)
                	tableData.append("\t");
                else
                	tableData.append("\n");
			}
		}
		return tableData.toString();
	}
	
	private String getCellStringValue(int row, int col) {
		
        final TableCellRenderer renderer = getCellRenderer(row, col);
        final Component comp = prepareRenderer(renderer, row, col);
        String txt = null;
        if(comp == null) {
        	return "";
        }
        else {
            if(JLabel.class.isAssignableFrom(comp.getClass())) {  
            	
            	txt = ((JLabel) comp).getText();
            	if(txt == null || txt.isEmpty())
            		txt = ((JLabel) comp).getToolTipText();
            	
            	if(txt == null)
            		txt = "";
            }
            else if (JTextPane.class.isAssignableFrom(comp.getClass()))
            	txt = ((JTextPane) comp).getText();
            
            else if (JTextField.class.isAssignableFrom(comp.getClass()))
            	txt = ((JTextField) comp).getText();
            
            else if (JTextArea.class.isAssignableFrom(comp.getClass()))
            	txt = ((JTextArea) comp).getText();
            
            else if(JCheckBox.class.isAssignableFrom(comp.getClass()))
            	txt = Boolean.toString(((JCheckBox) comp).isSelected());
            
            else if(JRadioButton.class.isAssignableFrom(comp.getClass()))
            	txt = Boolean.toString(((JRadioButton) comp).isSelected());
            
            else {
            	Object o = getValueAt(row, col);
            	if(o != null)
            		txt = o.toString();
            }
        }
        if(txt != null) {
        	try {
        		//	Strip HTML code
				txt = txt.trim().replaceAll("<[^>]*>", ""); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return txt;
	}
	
	public void copyVisibleTableRowsToClipboard() {

		String dataString = getTableDataAsString();		
		if(dataString == null)
			dataString = "";

		StringSelection stringSelection = new StringSelection(dataString);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
		
	public void copySelectedRowsToClipboard() {
		
		int [] rows = getSelectedRows();
		String dataString = getTableDataAsString(rows);
		if(dataString == null)
			dataString = "";

		StringSelection stringSelection = new StringSelection(dataString);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}	
}
