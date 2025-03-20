////////////////////////////////////////////////////
// BatchMergeReportTypePanel.java
// Created August 22, 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;

//> 2

public abstract class BatchMatchLatticeTypePanel extends StickySettingsPanel {
	private static final long serialVersionUID = -385664060785583897L;

	private JPanel inputTypeWrapPanel;
	private JPanel inputTypePanel;
	private ButtonGroup inputTypeGroup;
	private JRadioButton customLatticeButton, ddLatticeButton, namedLatticeButton, blankLatticeButton;

	private Integer selectedMin = 1, selectedMax = 100;
	private JTextField targetBatchBox;
	private JSpinner targetBatchSpinner;

	public BatchMatchLatticeTypePanel() {
		super();

		initializeStickySettings("batchMatchLatticeTypes", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		inputTypePanel = new JPanel();
		inputTypePanel.setLayout(new BoxLayout(inputTypePanel, BoxLayout.X_AXIS));
		inputTypeGroup = new ButtonGroup();

		customLatticeButton = makeStickyRadioButton("Recursive/Custom", "customLatticeBtn", false, true);
		customLatticeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		ddLatticeButton = makeStickyRadioButton("Data Driven", "ddLatticeBtn", true, true);
		ddLatticeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		namedLatticeButton = makeStickyRadioButton("Named Compounds", "namedLatticeBtn", true, true);
		namedLatticeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		blankLatticeButton = makeStickyRadioButton("Blank/No Lattice", "blankLatticeBtn", true, true);
		blankLatticeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateForInputTypeChange();
			}
		});

		setupSpinner();
		initializeSpinnerFromTextBox();

		inputTypeGroup.add(customLatticeButton);
		inputTypeGroup.add(ddLatticeButton);
		inputTypeGroup.add(namedLatticeButton);
		inputTypeGroup.add(blankLatticeButton);

		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(this.ddLatticeButton);
		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(this.namedLatticeButton);
		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(this.customLatticeButton);

		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(this.blankLatticeButton);
		inputTypePanel.add(Box.createHorizontalGlue());
		inputTypePanel.add(new JLabel("Target Batch:"));
		inputTypePanel.add(this.targetBatchSpinner);

		inputTypePanel.add(targetBatchBox);
		targetBatchBox.setVisible(false);

		inputTypeWrapPanel = new JPanel();
		inputTypeWrapPanel.setLayout(new BoxLayout(inputTypeWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder inputTypeWrapBorder = BorderFactory.createTitledBorder("Specify Lattice Type");
		inputTypeWrapBorder.setTitleFont(boldFontForTitlePanel(inputTypeWrapBorder, false));
		inputTypeWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		inputTypeWrapPanel.setBorder(inputTypeWrapBorder);
		inputTypeWrapPanel.add(inputTypePanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(inputTypeWrapPanel);
	}

	private void setupSpinner() {

		SpinnerModel spinnerModelCores = new SpinnerNumberModel(1, 1, 100, 1);

		targetBatchSpinner = new JSpinner(spinnerModelCores);
		targetBatchSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				targetBatchBox.setText("" + val);
				// updateForNewSelection(val);
			}
		});
		targetBatchBox = makeStickyTextField("targetLattice", "1", true);
		// PostProccessConstants.DEFAULT_BATCH_LEVEL_REPORTED.toString(), true);
		// intSelectorPanel.add(n1SelectedBox);
		targetBatchBox.setVisible(false);

		initializeSpinnerFromTextBox();
	}

	private void initializeSpinnerFromTextBox() {

		try {
			Integer nInitialCores = Integer.parseInt(targetBatchBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			targetBatchSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			targetBatchSpinner.setValue(1);
		}
	}

	public void setDDLattice() {
		ddLatticeButton.setSelected(true);
	}

	public void setCustomLattice() {
		customLatticeButton.setSelected(true);
	}

	public void setRecursiveLattice() {
		customLatticeButton.setSelected(true);
	}

	public void setNamedLattice() {
		namedLatticeButton.setSelected(true);
	}

	public void setBlankLattice() {
		blankLatticeButton.setSelected(true);
	}

	public Boolean useNamedLattice() {
		return namedLatticeButton.isSelected();
	}

	public Boolean useCustomLattice() {
		return customLatticeButton.isSelected();
	}

	public Boolean useDDLattice() {
		return ddLatticeButton.isSelected();
	}

	public Boolean useBlankLattice() {
		return blankLatticeButton.isSelected();
	}

	public Integer getTargetSelected() {
		Integer intSelected = 1;
		try {
			intSelected = Integer.parseInt(this.targetBatchBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public void setTargetSelected(Integer val) {
		targetBatchBox.setText(val.toString());
		targetBatchSpinner.setValue(val);
	}

	public void disableRecursiveOption() {
		this.customLatticeButton.setEnabled(false);
	}

	public void disableBlankOption() {
		this.blankLatticeButton.setEnabled(false);
	}

	public abstract void updateForInputTypeChange();
}
