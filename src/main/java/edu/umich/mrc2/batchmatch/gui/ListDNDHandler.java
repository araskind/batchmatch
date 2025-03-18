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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class ListDNDHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int index;
	private boolean beforeIndex = false;

	private JList<String> dndList;
	private DefaultListModel<String> listModel;

	public ListDNDHandler(JList<String> dndList) {
		super();
		this.dndList = dndList;
		this.listModel = (DefaultListModel<String>) dndList.getModel();
	}

	@Override
	public int getSourceActions(JComponent comp) {
		return MOVE;
	}

	@Override
	public Transferable createTransferable(JComponent comp) {
		index = dndList.getSelectedIndex();
		return new StringSelection(dndList.getSelectedValue());
	}

	@Override
	public void exportDone(JComponent comp, Transferable trans, int action) {
		if (action == MOVE) {
			if (beforeIndex)
				listModel.remove(index + 1);
			else
				listModel.remove(index);
		}
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		return support.isDataFlavorSupported(DataFlavor.stringFlavor);
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		try {
			String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			listModel.add(dl.getIndex(), s);
			beforeIndex = dl.getIndex() < index ? true : false;
			return true;
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
