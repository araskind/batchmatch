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

package edu.umich.med.mrc2.batchmatch.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;

public class GuiUtils {
	
	/**
	 * Add a new button to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @return Created button
	 */
	public static JButton addButton(Container component, String text, Icon icon, ActionListener listener) {

		return addButton(component, text, icon, listener, null, 0, null);
	}

	/**
	 * Add a new button to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @param actionCommand Button's action command or null
	 * @return Created button
	 */
	public static JButton addButton(Container component, String text, Icon icon, ActionListener listener,
			String actionCommand) {

		return addButton(component, text, icon, listener, actionCommand, 0, null);
	}

	/**
	 * Add a new button to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @param actionCommand Button's action command or null
	 * @param mnemonic Button's mnemonic (virtual key code) or 0
	 * @param toolTip Button's tooltip text or null
	 * @return Created button
	 */
	public static JButton addButton(Container component, String text, Icon icon, ActionListener listener,
			String actionCommand, int mnemonic, String toolTip) {

		JButton button = new JButton(text, icon);
		if (listener != null)
			button.addActionListener(listener);
		if (actionCommand != null)
			button.setActionCommand(actionCommand);
		if (mnemonic > 0)
			button.setMnemonic(mnemonic);
		if (toolTip != null)
			button.setToolTipText(toolTip);
		if (component != null)
			component.add(button);
		return button;
	}

	/**
	 * Add a new button to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @param actionCommand Button's action command or null
	 * @param toolTip Button's tooltip text or null
	 * @return Created button
	 */
	public static JButton addButton(Container component, String text, Icon icon, ActionListener listener,
			String actionCommand, String toolTip) {

		return addButton(component, text, icon, listener, actionCommand, 0, toolTip);
	}

	/**
	 * Add a new button to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @param actionCommand Button's action command or null
	 * @param toolTip Button's tooltip text or null
	 * @param buttonDimension Button's dimension
	 * @return Created button
	 */
	public static JButton addButton(Container component, String text, Icon icon, ActionListener listener,
			String actionCommand, String toolTip, Dimension buttonDimension) {

		JButton button = new JButton(text, icon);

		if (listener != null)
			button.addActionListener(listener);
		if (actionCommand != null)
			button.setActionCommand(actionCommand);
		if (toolTip != null)
			button.setToolTipText(toolTip);
		if (component != null)
			component.add(button);

		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setPreferredSize(buttonDimension);
		button.setSize(buttonDimension);

		return button;
	}

	public static JToggleButton addToggleButton(
			Container component,
			Icon icon,
			ActionListener listener,
			String toolTip,
			Dimension buttonDimension) {

		JToggleButton button = new JToggleButton(icon);

		if (listener != null)
			button.addActionListener(listener);
		if (toolTip != null)
			button.setToolTipText(toolTip);

		if (component != null)
			component.add(button);

		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setPreferredSize(buttonDimension);
		button.setSize(buttonDimension);

		return button;
	}

	public static JButton addButton(
			Container component,
			Icon icon,
			ActionListener listener,
			String toolTip,
			Dimension buttonDimension) {

		JButton button = new JButton(icon);

		if (listener != null)
			button.addActionListener(listener);
		if (toolTip != null)
			button.setToolTipText(toolTip);

		if (component != null)
			component.add(button);

		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setPreferredSize(buttonDimension);
		button.setSize(buttonDimension);

		return button;
	}

	public static JButton addButton(
			Container component,
			Icon icon,
			Action action,
			String toolTip,
			Dimension buttonDimension) {

		JButton button = new JButton(icon);

		if (action != null) {
			button.setAction(action);
			button.setText(null);
		}
		if (toolTip != null)
			button.setToolTipText(toolTip);
		if (component != null)
			component.add(button);

		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setPreferredSize(buttonDimension);
		button.setSize(buttonDimension);

		return button;
	}

	/**
	 * Add a new button to a JPanel and then add the panel to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @return Created button
	 */
	public static JButton addButtonInPanel(Container component, String text, ActionListener listener) {

		return addButtonInPanel(component, text, listener, null);
	}

	/**
	 * Add a new button to a JPanel and then add the panel to a given component
	 *
	 * @param component Component to add the button to
	 * @param text Button's text or null
	 * @param icon Button's icon or null
	 * @param listener Button's ActionListener or null
	 * @param actionCommand Button's action command or null
	 * @return Created button
	 */
	public static JButton addButtonInPanel(Container component, String text, ActionListener listener,
			String actionCommand) {

		JPanel panel = new JPanel();
		JButton button = new JButton(text);
		if (listener != null)
			button.addActionListener(listener);
		if (actionCommand != null)
			button.setActionCommand(actionCommand);
		panel.add(button);
		if (component != null)
			component.add(panel);
		return button;
	}

	/**
	 * Add a new label to a given component
	 *
	 * @param component Component to add the label to
	 * @param text Label's text
	 * @return Created label
	 */
	public static JLabel addLabel(Container component, String text) {

		return addLabel(component, text, null, JLabel.LEFT, null);
	}

	/**
	 * Add a new label to a given component
	 *
	 * @param component Component to add the label to
	 * @param text Label's text
	 * @param icon Label's icon
	 * @param horizontalAlignment Label's horizontal alignment (e.g. JLabel.LEFT)
	 * @param font Label's font
	 * @return Created label
	 */
	public static JLabel addLabel(Container component, String text, Icon icon, int horizontalAlignment, Font font) {

		JLabel label = new JLabel(text, icon, horizontalAlignment);
		if (component != null)
			component.add(label);
		if (font != null)
			label.setFont(font);
		return label;
	}

	/**
	 * Add a new label to a given component
	 *
	 * @param component Component to add the label to
	 * @param text Label's text
	 * @param horizontalAlignment Label's horizontal alignment (e.g. JLabel.LEFT)
	 * @return Created label
	 */
	public static JLabel addLabel(Container component, String text, int horizontalAlignment) {

		return addLabel(component, text, null, horizontalAlignment, null);
	}

	/**
	 * Add a new label to a given component
	 *
	 * @param component Component to add the label to
	 * @param text Label's text
	 * @param horizontalAlignment Label's horizontal alignment (e.g. JLabel.LEFT)
	 * @param font Label's font
	 * @return Created label
	 */
	public static JLabel addLabel(Container component, String text, int horizontalAlignment, Font font) {

		return addLabel(component, text, null, horizontalAlignment, font);
	}

	/**
	 * Add a new label to a JPanel and then add the panel to a given component
	 *
	 * @param component Component to add the label to
	 * @param text Label's text
	 * @return Created label
	 */
	public static JLabel addLabelInPanel(Container component, String text) {

		JPanel panel = new JPanel();
		component.add(panel);
		return addLabel(panel, text);
	}

	/**
	 * Add a margin to a given component
	 *
	 * @param component Component to add the margin to
	 * @param margin Margin size
	 * @return Created border
	 */
	public static Border addMargin(JComponent component, int margin) {

		Border marginBorder = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
		component.setBorder(marginBorder);
		return marginBorder;
	}

	/**
	 * Add a margin and border to a given component
	 *
	 * @param component Component to add the margin to
	 * @param margin Margin size
	 * @return Created border
	 */
	public static Border addMarginAndBorder(JComponent component, int margin) {

		Border marginBorder = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		Border compoundBorder = BorderFactory.createCompoundBorder(etchedBorder, marginBorder);
		component.setBorder(compoundBorder);
		return compoundBorder;
	}

	/**
	 * Add a new menu item to a given menu
	 *
	 * @param menu Menu to add the item to
	 * @param text Menu item text
	 * @param listener Menu item's ActionListener or null
	 * @return Created menu item
	 */
	public static JMenuItem addMenuItem(Container menu, String text, ActionListener listener) {

		return addMenuItem(menu, text, listener, null, 0, false, null);
	}

	/**
	 * Add a new menu item to a given menu
	 *
	 * @param menu Menu to add the item to
	 * @param text Menu item text
	 * @param listener Menu item's ActionListener or null
	 * @param mnemonic Menu item's mnemonic (virtual key code) or 0
	 * @return Created menu item
	 */
	public static JMenuItem addMenuItem(Container menu, String text, ActionListener listener, int mnemonic) {

		return addMenuItem(menu, text, listener, null, mnemonic, false, null);
	}

	/**
	 * Add a new menu item to a given menu
	 *
	 * @param menu Menu to add the item to
	 * @param text Menu item text
	 * @param listener Menu item's ActionListener or null
	 * @param mnemonic Menu item's mnemonic (virtual key code) or 0
	 * @param setAccelerator Indicates whether to set key accelerator to CTRL + the key specified by mnemonic parameter
	 * @return Created menu item
	 */
	public static JMenuItem addMenuItem(Container menu, String text, ActionListener listener, int mnemonic,
			boolean setAccelerator) {

		return addMenuItem(menu, text, listener, null, mnemonic, setAccelerator, null);
	}

	/**
	 * Add a new menu item to a given menu
	 *
	 * @param menu Menu to add the item to
	 * @param text Menu item text
	 * @param listener Menu item's ActionListener or null
	 * @param actionCommand Menu item's action command or null
	 * @return Created menu item
	 */
	public static JMenuItem addMenuItem(Container menu, String text, ActionListener listener, String actionCommand) {
		return addMenuItem(menu, text, listener, actionCommand, 0, false, null);
	}

	/**
	 * Add a new menu item to a given menu
	 *
	 * @param menu Menu to add the item to
	 * @param text Menu item text
	 * @param listener Menu item's ActionListener or null
	 * @param actionCommand Menu item's action command or null
	 * @param icon Menu icon
	 * @return Created menu item
	 */
	public static JMenuItem addMenuItem(Container menu, String text, ActionListener listener, String actionCommand,
			Icon icon) {

		return addMenuItem(menu, text, listener, actionCommand, 0, false, icon);
	}

	/**
	 * Add a new menu item to a given menu
	 *
	 * @param menu Menu to add the item to
	 * @param text Menu item text
	 * @param listener Menu item's ActionListener or null
	 * @param actionCommand Menu item's action command or null
	 * @param mnemonic Menu item's mnemonic (virtual key code) or 0
	 * @param setAccelerator Indicates whether to set key accelerator to CTRL + the key specified by mnemonic parameter
	 * @param icon Menu icon
	 * @return Created menu item
	 */
	public static JMenuItem addMenuItem(Container menu, String text, ActionListener listener, String actionCommand,
			int mnemonic, boolean setAccelerator, Icon icon) {

		JMenuItem item = new JMenuItem(text);
		if (listener != null)
			item.addActionListener(listener);
		if (actionCommand != null)
			item.setActionCommand(actionCommand);
		if (mnemonic > 0)
			item.setMnemonic(mnemonic);
		if (setAccelerator)
			item.setAccelerator(KeyStroke.getKeyStroke(mnemonic, ActionEvent.CTRL_MASK));
		if (icon != null)
			item.setIcon(icon);

		if (menu != null)
			menu.add(item);
		return item;
	}

	/**
	 * Add a separator to a given component
	 *
	 * @param component Component to add the separator to
	 * @return Created separator
	 */
	public static JSeparator addSeparator(Container component) {

		return addSeparator(component, 0);
	}

	/**
	 * Add a separator to a given component
	 *
	 * @param component Component to add the separator to
	 * @param margin Margin around the separator
	 * @return Created separator
	 */
	public static JSeparator addSeparator(Container component, int margin) {

		JSeparator separator = new JSeparator();
		if (margin > 0)
			addMargin(separator, margin);
		if (component != null)
			component.add(separator);
		return separator;
	}

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
	
	public static Icon getIcon(String iconName, int size) {

		File iconFile = Paths.get(
				BatchMatchConfiguration.iconsDirectory.getAbsolutePath(), iconName + ".png").toFile();
		if(iconFile.exists()) {
			
			ImageIcon original = new ImageIcon(iconFile.getAbsolutePath());
			Image newImage = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
			return new ImageIcon(newImage);
		}
		else
			return null;
	}
}


