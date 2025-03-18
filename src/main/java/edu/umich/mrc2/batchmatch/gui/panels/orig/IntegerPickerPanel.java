////////////////////////////////////////////////////
// IntegerPickerPanel.java
// Written by Bill Duren and Jan Wigginton
// November 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umich.mrc2.batchmatch.gui.LayoutGrid;
import edu.umich.mrc2.batchmatch.gui.LayoutItem;
import edu.umich.mrc2.batchmatch.gui.LayoutUtils;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.main.PostProccessConstants;

public abstract class IntegerPickerPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -1155577926039808370L;

	private JTextField nSelectedBox;
	private JPanel intSelectorWrapPanel;
	private JSpinner nSelectedSpinner;

	private int selectedMin = 1, selectedMax = 200, selectedStep = 1;
	private int defaultSelection = 1;
	private TitledBorder intSelectorWrapBorder;

	private String panelTitle, spinnerLabel;

	public IntegerPickerPanel(String tagForSettings) {
		super();
		initializeStickySettings(tagForSettings, BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel(String panelTitle, String spinnerLabel) {
		this.panelTitle = panelTitle;
		this.spinnerLabel = spinnerLabel;
		initializeArrays();
		setupSpinners();
		setupParametersPanel();
		initializeSpinnersFromTextBoxes();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(2));
		add(intSelectorWrapPanel);
		add(Box.createVerticalStrut(2));

		setupTextFieldListeners();
	}

	private void setupSpinners() {
		SpinnerModel spinnerModelCores = new SpinnerNumberModel(defaultSelection, selectedMin, selectedMax,
				selectedStep);

		nSelectedSpinner = new JSpinner(spinnerModelCores);
		nSelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				nSelectedBox.setText("" + val);
				updateForNewSelection(val);
			}
		});
	}

	private void initializeSpinnersFromTextBoxes() {

		try {
			Integer nInitialCores = Integer.parseInt(nSelectedBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			nSelectedSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			nSelectedSpinner.setValue(1);
		}
	}
	// Target Batch

	private void setupParametersPanel() {
		JPanel intSelectorPanel = new JPanel();

		JLabel nSelectedLabel = new JLabel(this.spinnerLabel);
		TitledBorder anyBorder = BorderFactory.createTitledBorder("00");
		anyBorder.setTitleColor(BinnerConstants.TITLE_COLOR);

		nSelectedLabel.setHorizontalAlignment(JLabel.RIGHT);
		nSelectedBox = makeStickyTextField(
				"nSelected", Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED),true);

		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel("   "), 0.47), new LayoutItem(nSelectedLabel, 0.03),
				new LayoutItem(nSelectedSpinner, 0.03), new LayoutItem(new JLabel("   "), 0.47)));

		LayoutUtils.doGridLayout(intSelectorPanel, panelGrid);
		intSelectorPanel.add(nSelectedBox);
		nSelectedBox.setVisible(false);

		intSelectorWrapPanel = new JPanel();
		intSelectorWrapPanel.setLayout(new BoxLayout(intSelectorWrapPanel, BoxLayout.Y_AXIS));
		intSelectorWrapBorder = BorderFactory.createTitledBorder(panelTitle);
		intSelectorWrapBorder.setTitleFont(boldFontForTitlePanel(intSelectorWrapBorder, false));
		intSelectorWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		intSelectorWrapPanel.setBorder(intSelectorWrapBorder);
		intSelectorWrapPanel.add(intSelectorPanel);
	}

	public Integer getIntSelected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.nSelectedBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public void setPanelTitle(String title) {
		panelTitle = title;
		this.intSelectorWrapBorder.setTitle(panelTitle);
	}

	public int getSelectedMin() {
		return selectedMin;
	}

	public void setSelectedMin(int selectedMin) {
		this.selectedMin = selectedMin;
	}

	public int getSelectedMax() {
		return selectedMax;
	}

	public void setSelectedMax(int selectedMax) {
		this.selectedMax = selectedMax;
	}

	public int getSelectedStep() {
		return selectedStep;
	}

	public void setSelectedStep(int selectedStep) {
		this.selectedStep = selectedStep;
	}

	public void setEnableState(Boolean state) {
		if (nSelectedSpinner != null)
			nSelectedSpinner.setEnabled(state);
	}

	public int getDefaultSelection() {
		return defaultSelection;
	}

	public void setDefaultSelection(int defaultSelection) {
		this.defaultSelection = defaultSelection;
	}

	public void setSpinnerLimits(int spinnerMin, int spinnerMax) {
		selectedMin = spinnerMin;
		selectedMax = spinnerMax;
		setupSpinners();
	}

	protected abstract Boolean updateForNewSelection(Integer newSelection);
}
