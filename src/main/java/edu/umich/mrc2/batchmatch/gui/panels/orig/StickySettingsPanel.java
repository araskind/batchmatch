////////////////////////////////////////////////////
// StickySettingsPanel.java
// Written by Jan Wigginton December 2017
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.umich.mrc2.batchmatch.io.sheetwriters.BinnerDecimalTextFieldPair3;
import edu.umich.mrc2.batchmatch.preferences.BinnerStickySettings;
import edu.umich.mrc2.batchmatch.preferences.BinnerStickyStringSettings;
import edu.umich.mrc2.batchmatch.utils.PostProcessUtils;

public class StickySettingsPanel extends JPanel implements ItemListener {
	
	private static final long serialVersionUID = -6419694549783810858L;

	protected List<JTextField> flds;
	protected Map<String, String> stickyStringValues;

	protected List<JCheckBox> checkBoxes2;
	protected List<JRadioButton> radioButtons;
	protected Map<String, Boolean> stickyBooleanValues;

	protected String stickyGroupName = "group";
	protected String stickyBooleanGroupName = "booleangroup";
	protected String stickyFileName = "file";

	protected static final Color TITLE_COLOR = new Color(0, 0, 205);

	public StickySettingsPanel() {
		super();
	}

	protected void initializeStickySettings(String stickyGroupName, String stickyFileName) {
		
		this.stickyFileName = stickyFileName;
		this.stickyGroupName = stickyGroupName;		
		this.stickyBooleanGroupName = stickyGroupName + "Boolean";
		
		stickyStringValues = new HashMap<String, String>();
		stickyBooleanValues = new HashMap<String, Boolean>();

		try {
			BinnerStickyStringSettings stickyStringSettings = 
					PostProcessUtils.getBinnerObjectProperty(
							stickyFileName, stickyGroupName, BinnerStickyStringSettings.class);
			stickyStringValues = stickyStringSettings != null ?
					stickyStringSettings.getStringSettings() : new HashMap<String, String>();
		} catch (Exception e) {
		}
		try {
			BinnerStickySettings stickyBooleanSettings = 
					PostProcessUtils.getBinnerObjectProperty(
							stickyFileName, stickyBooleanGroupName, BinnerStickySettings.class);
			stickyBooleanValues = stickyBooleanSettings != null ?
					stickyBooleanSettings.getOutputTabPrefs() : new HashMap<String, Boolean>();
		} catch (Exception e) {
		}
	}

	protected JTextField makeStickyTextField(
			String name, String defaultValue, boolean addToArray) {
		
		String value = this.stickyStringValues.containsKey(name) ? stickyStringValues.get(name) : defaultValue;
		JTextField fld = new JTextField(value);
		fld.setName(name);
		if (addToArray)
			flds.add(fld);
		
		return fld;
	}

	protected BinnerDecimalTextFieldPair3 makeStickyDecimalTextFieldPair(
			String nameLower, 
			String nameUpper,
			Double lowerLimit, 
			Double lowerDefault, 
			Double upperDefault, 
			Double upperLimit, 
			Boolean initEnable) {
		
		String valueLower = this.stickyStringValues.containsKey(nameLower) ? 
				stickyStringValues.get(nameLower) : lowerDefault.toString();
		String valueUpper = this.stickyStringValues.containsKey(nameUpper) ? 
				stickyStringValues.get(nameUpper) : upperDefault.toString();

		Double initialLower = null;
		Double initialUpper = null;
		try {
			initialLower = Double.parseDouble(valueLower);
			if (initialLower < lowerLimit || initialLower > upperLimit)
				valueLower = lowerDefault.toString();
		} catch (Exception e) {
			valueLower = lowerDefault.toString();
		}

		try {
			initialUpper = Double.parseDouble(valueUpper);
			if (initialUpper > upperLimit || initialUpper < lowerLimit)
				valueUpper = upperDefault.toString();
		} catch (Exception e) {
			valueUpper = upperDefault.toString();
		}

		if (initialLower > initialUpper) {
			valueLower = lowerDefault.toString();
			valueUpper = upperDefault.toString();
		}
		BinnerDecimalTextFieldPair3 pair = 
				new BinnerDecimalTextFieldPair3(
						lowerLimit, lowerDefault, upperDefault,upperLimit, initEnable);

		pair.getLeftField().setName(nameLower);
		pair.getRightField().setName(nameUpper);

		pair.getLeftField().setText(valueLower);
		pair.getRightField().setText(valueUpper);

		flds.add(pair.getLeftField());
		flds.add(pair.getRightField());

		return pair;
	}

	protected void initializeArrays() {
		flds = new ArrayList<JTextField>();
		checkBoxes2 = new ArrayList<JCheckBox>();
		radioButtons = new ArrayList<JRadioButton>();
	}

	protected JCheckBox makeStickyCheckBox(
			String label, 
			String name, 
			boolean defaultValue, 
			boolean addToArray) {
		
		JCheckBox box = new JCheckBox(label);
		box.setName(name);
		if (stickyBooleanValues.containsKey(name))
			box.setSelected(stickyBooleanValues.get(name));
		else
			box.setSelected(defaultValue);

		box.addItemListener(this);

		if (addToArray)
			this.checkBoxes2.add(box);
		return box;
	}

	protected JRadioButton makeStickyRadioButton(
			String label, 
			String name, 
			boolean defaultValue, 
			boolean addToArray) {
		JRadioButton button = new JRadioButton(label);
		button.setName(name);
		if (stickyBooleanValues.containsKey(name))
			button.setSelected(stickyBooleanValues.get(name));
		else
			button.setSelected(defaultValue);

		button.addItemListener(this);

		if (addToArray)
			this.radioButtons.add(button);
		return button;
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		stickyBooleanValues.put(((JToggleButton) source).getName(), ((JToggleButton) source).isSelected());
		BinnerStickySettings settings = new BinnerStickySettings(stickyBooleanValues);
		PostProcessUtils.setBinnerObjectProperty(stickyFileName, stickyBooleanGroupName, settings);
	}

	protected void setupTextFieldListeners() {
		
		if (flds == null || flds.isEmpty())
			return;

		for (JTextField fld : flds) {

			fld.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					updateStoredProps(stickyFileName, fld, stickyGroupName);
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					updateStoredProps(stickyFileName, fld, stickyGroupName);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					updateStoredProps(stickyFileName, fld, stickyGroupName);
				}
			});
		}
	}

	private void updateStoredProps(String prefFileName, JTextField fld, String stickyTag) {
		
		stickyStringValues.put(fld.getName(), fld.getText());
		BinnerStickyStringSettings settings = new BinnerStickyStringSettings(stickyStringValues);
		PostProcessUtils.setBinnerObjectProperty(prefFileName, stickyTag, settings);
	}

	public Font boldFontForTitlePanel(TitledBorder border, boolean makeEvenLarger) {
		
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
