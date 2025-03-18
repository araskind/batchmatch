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

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class GUIUtils {

	public static Font boldFontForTitlePanel(TitledBorder border, boolean makeEvenLarger) {
		
		Font font = null;
		if(border != null)
			font = border.getTitleFont();
		
		if (font == null) {
			font = UIManager.getDefaults().getFont("TitledBorder.font");
			if (font == null) {
				font = new Font("SansSerif", Font.BOLD, 12);
			} else {
				font = font.deriveFont(Font.BOLD);
			}
		} else {
			font = font.deriveFont(Font.BOLD);
		}
		Font biggerFont = new Font(font.getName(), font.getStyle(), font.getSize() + (makeEvenLarger ? 3 : 1));
		return biggerFont;
	}
}
