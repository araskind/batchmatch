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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ListReorderListener extends MouseAdapter {

	   private JList list;
	   private int pressIndex = 0;
	   private int releaseIndex = 0;

	   public ListReorderListener(JList list) {
	      if (!(list.getModel() instanceof DefaultListModel)) {
	         throw new IllegalArgumentException("List must have a DefaultListModel");
	      }
	      this.list = list;
	   }

	   @Override
	   public void mousePressed(MouseEvent e) {
	      pressIndex = list.locationToIndex(e.getPoint());
	   }

	   @Override
	   public void mouseReleased(MouseEvent e) {
	      releaseIndex = list.locationToIndex(e.getPoint());
	      if (releaseIndex != pressIndex && releaseIndex != -1) {
	         reorder();
	      }
	   }

	   @Override
	   public void mouseDragged(MouseEvent e) {
	      mouseReleased(e);
	      pressIndex = releaseIndex;      
	   }

	   private void reorder() {
	      DefaultListModel model = (DefaultListModel) list.getModel();
	      Object dragee = model.elementAt(pressIndex);
	      model.removeElementAt(pressIndex);
	      model.insertElementAt(dragee, releaseIndex);
	   }
}
