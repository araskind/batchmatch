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

package edu.umich.mrc2.batchmatch.gui.panels;

import edu.umich.mrc2.batchmatch.gui.MessageDialog;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.CommonFileTypes;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.FileChooserAction;
import edu.umich.mrc2.batchmatch.gui.jnafilechooser.api.JnaFileChooser.Mode;

public class BatchMatchTestFileSelectorPanel extends BatchMatchFileSelectorPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BatchMatchTestFileSelectorPanel(
			String panelId, 
			String panelTitle, 
			Mode selectionMode,
			CommonFileTypes fileType, 
			FileChooserAction fcAction) {
		super(panelId, panelTitle, selectionMode, fileType, fcAction);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void fileSelectionChanged() {
		MessageDialog.showInfoMsg(selectedFile.getPath());
	}
}
