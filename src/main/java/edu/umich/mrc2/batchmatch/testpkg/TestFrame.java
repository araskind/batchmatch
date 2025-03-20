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

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.umich.mrc2.batchmatch.gui.jnafilechooser.CommonFileTypes;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.FileChooserAction;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser.Mode;
import edu.umich.mrc2.batchmatch.gui.panels.BatchMatchTestFileSelectorPanel;
import edu.umich.mrc2.batchmatch.gui.panels.DraggableListViewPanel;

public class TestFrame extends JFrame implements WindowListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TestFrame() throws HeadlessException {
		super();
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		setTitle("BatchMatch GUI test");
		setSize(800,640);
		
		BatchMatchTestFileSelectorPanel tp = new BatchMatchTestFileSelectorPanel(
				"openCsv", "Open CSV", Mode.Files, CommonFileTypes.COMMA_SEPARATED, FileChooserAction.Open);
		add(tp, BorderLayout.NORTH);
		
		ArrayList<String>values = new ArrayList<String>();
		for(Integer i=100; i<200; i++)
			values.add(i.toString());
		
		DraggableListViewPanel dlp = new DraggableListViewPanel("testList", "test list", true, false, values);
		add(dlp, BorderLayout.CENTER);
		
		BatchMatchTestFileSelectorPanel tp2 = new BatchMatchTestFileSelectorPanel(
				"openTsv", "Open tab-separated", Mode.Files, CommonFileTypes.TAB_SEPARATED, FileChooserAction.Open);
		add(tp2, BorderLayout.SOUTH);
		
		setLocationRelativeTo(null);
		addWindowListener(this);
	}

	private LayoutManager BorderLayout(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		GUITestClass.shutDown();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
