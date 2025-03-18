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

package edu.umich.mrc2.batchmatch.testpkg;

import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class DnDtest extends JFrame {
	
    protected DnDtest() {
        super("Simple Rearrangeable List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createPanel();
        setBounds(10, 10, 350, 500);
        setVisible(true);
    }

    private void createPanel() {
        DefaultListModel<String> strings = new DefaultListModel<String>();

        for(int i = 1; i <= 100; i++) {
            strings.addElement("Item " + i);
        }

        JList<String> dndList = new JList<String>(strings);
        dndList.setDragEnabled(true);
        dndList.setDropMode(DropMode.INSERT);
        dndList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dndList.setTransferHandler(new TransferHandler() {
            private int index;
            private boolean beforeIndex = false; //Start with `false` therefore if it is removed from or added to the list it still works

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
                    if(beforeIndex)
                        strings.remove(index + 1);
                    else
                        strings.remove(index);
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
                    strings.add(dl.getIndex(), s);
                    beforeIndex = dl.getIndex() < index ? true : false;
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        JScrollPane scrollPane = new JScrollPane(dndList);
        getContentPane().add(scrollPane);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new DnDtest());
    }
}
