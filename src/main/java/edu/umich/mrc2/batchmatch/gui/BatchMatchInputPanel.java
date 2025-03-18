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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.preferences.BackedByPreferences;

public abstract class BatchMatchInputPanel extends JPanel implements BackedByPreferences, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String panelId;
	protected String panelTitle;
	protected GridBagLayout gridBagLayout;
	
	protected Preferences panelPreferences;

	public BatchMatchInputPanel(String panelId, String panelTitle, boolean isContainer) {
		super();
		this.panelId = panelId;
		this.panelTitle = panelTitle;
		Color titleColor = Color.BLACK;
		Font titleFont  = UIManager.getDefaults().getFont("TitledBorder.font");
		if(isContainer) {
			titleColor = BinnerConstants.TITLE_COLOR;
			titleFont = GUIUtils.boldFontForTitlePanel(null, false);
		}
		setBorder(new CompoundBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 255, 255), new Color(160, 160, 160)),
				panelTitle, TitledBorder.LEADING, TitledBorder.TOP, 
				titleFont, titleColor), 
				new EmptyBorder(10, 10, 10, 10)));
		gridBagLayout = new GridBagLayout();		
		setLayout(gridBagLayout);
	}
}
