////////////////////////////////////////////////////
// InputMultiplicityPanel.java
// Created by Jan Wigginton August 2020
////////////////////////////////////////////////////
package edu.umich.batchmatch.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.main.BatchMatchConstants;

public abstract class LatticeFilterTypePanel extends StickySettingsPanel {

	private static final long serialVersionUID = -385664060785583897L;

	private JPanel inputTypeWrapPanel, inputTypePanel;
	private ButtonGroup inputTypeGroup;
	private JRadioButton pruneButton, removeButton;
	private String panelTitle;

	public LatticeFilterTypePanel() {
		this("");
	}

	public LatticeFilterTypePanel(String title) {
		super();
		panelTitle = title;
		initializeStickySettings("latticeMergeTypes", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		inputTypePanel = new JPanel();
		inputTypePanel.setLayout(new BoxLayout(inputTypePanel, BoxLayout.X_AXIS));
		inputTypeGroup = new ButtonGroup();

		pruneButton = makeStickyRadioButton("Merge and Smooth", "singleButton", false, true);
		pruneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		removeButton = makeStickyRadioButton("Merge and Remove Duplicates", "multipleButton", true, true);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		inputTypeGroup.add(pruneButton);
		inputTypeGroup.add(removeButton);

		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(removeButton);
		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(pruneButton);
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

	public void setPruneFormat() {
		pruneButton.setSelected(true);
	}

	public void setRemoveFormat() {
		removeButton.setSelected(true);
	}

	public Boolean usePruneFormat() {
		return pruneButton.isSelected();
	}

	public Boolean useRemoveFormat() {
		return removeButton.isSelected();
	}

	public abstract void updateForInputTypeChange();
}
