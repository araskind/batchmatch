////////////////////////////////////////////////////
// InputMultiplicityPanel.java
// Created by Jan Wigginton August 2020
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;

public abstract class InputMultiplicityPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -385664060785583897L;

	private JPanel inputTypeWrapPanel, inputTypePanel;
	private ButtonGroup inputTypeGroup;
	private JRadioButton singleButton, multipleButton;
	private String panelTitle;

	public InputMultiplicityPanel() {
		this("");
	}

	public InputMultiplicityPanel(String title) {
		super();
		panelTitle = title;
		initializeStickySettings("multiplicityTypes", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		inputTypePanel = new JPanel();
		inputTypePanel.setLayout(new BoxLayout(inputTypePanel, BoxLayout.X_AXIS));
		inputTypeGroup = new ButtonGroup();

		singleButton = makeStickyRadioButton("Single File", "singleButton", false, true);
		singleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		multipleButton = makeStickyRadioButton("Multiple Files", "multipleButton", true, true);
		multipleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		inputTypeGroup.add(singleButton);
		inputTypeGroup.add(multipleButton);

		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(multipleButton);
		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(singleButton);
		inputTypePanel.add(Box.createHorizontalGlue());

		inputTypeWrapPanel = new JPanel();
		inputTypeWrapPanel.setLayout(new BoxLayout(inputTypeWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder inputTypeWrapBorder = BorderFactory.createTitledBorder(panelTitle);
		inputTypeWrapBorder.setTitleFont(boldFontForTitlePanel(inputTypeWrapBorder, false));
		inputTypeWrapBorder.setTitleColor(BatchMatchConstants.TITLE_COLOR);
		inputTypeWrapPanel.setBorder(inputTypeWrapBorder);
		inputTypeWrapPanel.add(inputTypePanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(inputTypeWrapPanel);
	}

	public void setSingleFormat() {
		singleButton.setSelected(true);
	}

	public void setMultipleFormat() {
		multipleButton.setSelected(true);
	}

	public Boolean useSingleFormat() {
		return singleButton.isSelected();
	}

	public Boolean useMultipleFormat() {
		return multipleButton.isSelected();
	}

	public abstract void updateForInputTypeChange();
}
