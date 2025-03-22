////////////////////////////////////////////////////
// IntegerRangePickerPanel.java
// Written by Bill Duren and Jan Wigginton
// January 2020
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umich.med.mrc2.batchmatch.gui.LayoutGrid;
import edu.umich.med.mrc2.batchmatch.gui.LayoutItem;
import edu.umich.med.mrc2.batchmatch.gui.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;

public abstract class AnnealOptionsPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -1155577926039808370L;

	private JTextField minSelectedBox, maxSelectedBox;
	private JPanel intSelectorWrapPanel;
	private JSpinner minSelectedSpinner, maxSelectedSpinner;

	private int selectedMin = 10, selectedMax = 10, selectedStep = 1;
	private int defaultSelection = 1;
	private TitledBorder intSelectorWrapBorder;

	private Boolean showUpper = true;

	private String panelTitle, minSpinnerLabel, maxSpinnerLabel;

	public AnnealOptionsPanel(String tagForSettings) {
		super();
		initializeStickySettings(tagForSettings, BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel(String panelTitle, String spinnerLabel) {
		this.setupPanel(panelTitle, spinnerLabel, true);
	}

	public void setupPanel(String panelTitle, String spinnerLabel, Boolean showUpper) {
		this.panelTitle = panelTitle;
		this.minSpinnerLabel = showUpper ? "Minimum " + spinnerLabel : spinnerLabel;
		this.maxSpinnerLabel = showUpper ? "Maximum " + spinnerLabel : spinnerLabel;

		this.showUpper = showUpper;
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
		
		SpinnerModel spinnerModelCoresMin = 
				new SpinnerNumberModel(10, selectedMin, selectedMax, selectedStep);
		SpinnerModel spinnerModelCoresMax = 
				new SpinnerNumberModel(10, selectedMin, selectedMax, selectedStep);

		minSelectedSpinner = new JSpinner(spinnerModelCoresMin);
		minSelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				minSelectedBox.setText("" + val);
				updateForNewSelection(val, true);
			}
		});
		maxSelectedSpinner = new JSpinner(spinnerModelCoresMax);
		maxSelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				maxSelectedBox.setText("" + val);
				updateForNewSelection(val, false);
			}
		});
		maxSelectedSpinner.setVisible(showUpper);
	}

	private void initializeSpinnersFromTextBoxes() {

		try {
			Integer initialMinCores = Integer.parseInt(minSelectedBox.getText());
			if (initialMinCores < selectedMin || initialMinCores > selectedMax)
				throw new Exception("Illegal value");
			
			minSelectedSpinner.setValue(initialMinCores);
		} catch (Exception e) {
			minSelectedSpinner.setValue(1);
		}
		try {
			Integer initialMaxCores = Integer.parseInt(maxSelectedBox.getText());
			if (initialMaxCores < selectedMin || initialMaxCores > selectedMax)
				throw new Exception("Illegal value");
			maxSelectedSpinner.setValue(initialMaxCores);
		} catch (Exception e) {
			maxSelectedSpinner.setValue(1);
		}
	}

	private void setupParametersPanel() {
		
		JPanel intSelectorPanel = new JPanel();

		JLabel minSelectedLabel = new JLabel(this.minSpinnerLabel);
		JLabel maxSelectedLabel = new JLabel(this.maxSpinnerLabel);
		maxSelectedLabel.setVisible(showUpper);

		TitledBorder anyBorder = BorderFactory.createTitledBorder("00");
		anyBorder.setTitleColor(BinnerConstants.TITLE_COLOR);

		minSelectedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		maxSelectedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		minSelectedBox = makeStickyTextField("minSelected",
			Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED), true);
		maxSelectedBox = makeStickyTextField("maxSelected",
				Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED), true);

		minSelectedBox.setVisible(false);
		maxSelectedBox.setVisible(false);
		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel("   "), 0.40), new LayoutItem(minSelectedLabel, 0.03),
				new LayoutItem(minSelectedSpinner, 0.03), new LayoutItem(new JLabel("   "), 0.08),
				new LayoutItem(maxSelectedLabel, 0.03), new LayoutItem(maxSelectedSpinner, 0.03),
				new LayoutItem(new JLabel("   "), 0.40)));

		LayoutUtils.doGridLayout(intSelectorPanel, panelGrid);
		intSelectorPanel.add(minSelectedBox);
		intSelectorPanel.add(maxSelectedBox);

		intSelectorWrapPanel = new JPanel();
		intSelectorWrapPanel.setLayout(new BoxLayout(intSelectorWrapPanel, BoxLayout.Y_AXIS));
		intSelectorWrapBorder = BorderFactory.createTitledBorder(panelTitle);
		intSelectorWrapBorder.setTitleFont(boldFontForTitlePanel(intSelectorWrapBorder, false));
		intSelectorWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		intSelectorWrapPanel.setBorder(intSelectorWrapBorder);
		intSelectorWrapPanel.add(intSelectorPanel);
	}

	public Integer getMinSelected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.minSelectedBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public Integer getMaxSelected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.maxSelectedBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public void setPanelTitle(String title) {
		panelTitle = title;
		this.intSelectorWrapBorder.setTitle(panelTitle);
	}

	public void setSpinnerLimits(int spinnerMin, int spinnerMax) {
		selectedMin = spinnerMin;
		selectedMax = spinnerMax;
		setupSpinners();
	}

	protected abstract Boolean updateForNewSelection(Integer newSelection, Boolean updateMin);
}
