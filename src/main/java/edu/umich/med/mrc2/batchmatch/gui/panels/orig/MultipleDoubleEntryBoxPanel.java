////////////////////////////////////////////////////
// MultipleIntegerPickerPanel.java
// Written by Bill Duren and Jan Wigginton
// November 2019
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutGrid;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutItem;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;

public abstract class MultipleDoubleEntryBoxPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -1155577926039808370L;

	private JTextField n1SelectedBox, n2SelectedBox, n3SelectedBox, n4SelectedBox;
	private JPanel intSelectorWrapPanel;
	private JSpinner n1SelectedSpinner, n2SelectedSpinner, n3SelectedSpinner, n4SelectedSpinner;

	private int selectedMin = 1, selectedMax = 20000, selectedStep = 1;
	private int defaultSelection = 1;
	private TitledBorder intSelectorWrapBorder;

	private String panelTitle, spinnerLabel1, spinnerLabel2, spinnerLabel3, spinnerLabel4;

	public MultipleDoubleEntryBoxPanel(String tagForSettings) {
		super();
		initializeStickySettings(tagForSettings, BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel(String panelTitle, String spinnerLabel1, String spinnerLabel2, String spinnerLabel3) {
		setupPanel(panelTitle, spinnerLabel1, spinnerLabel2, spinnerLabel3, "");
	}

	public void setupPanel(String panelTitle, String spinnerLabel1, String spinnerLabel2) {
		setupPanel(panelTitle, spinnerLabel1, spinnerLabel2, "", "");
	}

	public void setupPanel(String panelTitle, String spinnerLabel1) {
		setupPanel(panelTitle, spinnerLabel1, "", "", "");
	}

	public void setupPanel(String panelTitle, String spinnerLabel1, String spinnerLabel2, String spinnerLabel3,
			String spinnerLabel4) {

		this.panelTitle = panelTitle;
		this.spinnerLabel1 = spinnerLabel1;
		this.spinnerLabel2 = spinnerLabel2;
		this.spinnerLabel3 = spinnerLabel3;
		this.spinnerLabel4 = spinnerLabel4;

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

		n1SelectedSpinner = new JSpinner(spinnerModelCores);
		n1SelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				n1SelectedBox.setText("" + val);
				updateForNewSelection(val);
			}
		});

		SpinnerModel spinnerModelCores2 = new SpinnerNumberModel(defaultSelection, selectedMin, selectedMax,
				selectedStep);

		n2SelectedSpinner = new JSpinner(spinnerModelCores2);
		n2SelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				n2SelectedBox.setText("" + val);
				updateForNewSelection(val);
			}
		});
		n2SelectedSpinner.setVisible(!spinnerLabel2.isEmpty());

		SpinnerModel spinnerModelCores3 = new SpinnerNumberModel(defaultSelection, selectedMin, selectedMax,
				selectedStep);

		n3SelectedSpinner = new JSpinner(spinnerModelCores3);
		n3SelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				n3SelectedBox.setText("" + val);
				updateForNewSelection(val);
			}
		});
		n3SelectedSpinner.setVisible(!spinnerLabel3.isEmpty());

		SpinnerModel spinnerModelCores4 = new SpinnerNumberModel(defaultSelection, selectedMin, selectedMax,
				selectedStep);

		n4SelectedSpinner = new JSpinner(spinnerModelCores4);
		n4SelectedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer val = (Integer) ((JSpinner) e.getSource()).getValue();
				n4SelectedBox.setText("" + val);
				updateForNewSelection(val);
			}
		});
		n4SelectedSpinner.setVisible(!spinnerLabel4.isEmpty());
	}

	private void initializeSpinnersFromTextBoxes() {

		try {
			Integer nInitialCores = Integer.parseInt(n1SelectedBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			n1SelectedSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			n1SelectedSpinner.setValue(1);
		}

		try {
			Integer nInitialCores = Integer.parseInt(n2SelectedBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			n2SelectedSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			n2SelectedSpinner.setValue(1);
		}

		try {
			Integer nInitialCores = Integer.parseInt(n3SelectedBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			n3SelectedSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			n3SelectedSpinner.setValue(1);
		}

		try {
			Integer nInitialCores = Integer.parseInt(n4SelectedBox.getText());
			if (nInitialCores < selectedMin || nInitialCores > selectedMax)
				throw new Exception("Illegal value");
			n4SelectedSpinner.setValue(nInitialCores);
		} catch (Exception e) {
			n4SelectedSpinner.setValue(1);
		}
	}
	// Target Batch

	private void setupParametersPanel() {
		JPanel intSelectorPanel = new JPanel();

		JLabel n1SelectedLabel = new JLabel(this.spinnerLabel1);
		// TitledBorder anyBorder2 = BorderFactory.createTitledBorder("00");
		// anyBorder.setTitleColor(BinnerConstants.TITLE_COLOR);

		n1SelectedLabel.setHorizontalAlignment(JLabel.RIGHT);
		n1SelectedBox = makeStickyTextField("nSelected1", Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED),
				true);
		// intSelectorPanel.add(n1SelectedBox);
		// n1SelectedBox.setVisible(false);

		JLabel n2SelectedLabel = new JLabel(this.spinnerLabel2);
		n2SelectedLabel.setHorizontalAlignment(JLabel.RIGHT);
		n2SelectedBox = makeStickyTextField("nSelected2", Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED),
				true);
		n2SelectedBox.setVisible(!spinnerLabel2.isEmpty());

		JLabel n3SelectedLabel = new JLabel(this.spinnerLabel3);
		n3SelectedLabel.setHorizontalAlignment(JLabel.RIGHT);
		n3SelectedBox = makeStickyTextField("nSelected3", Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED),
				true);
		n3SelectedBox.setVisible(!spinnerLabel3.isEmpty());

		JLabel n4SelectedLabel = new JLabel(this.spinnerLabel4);
		n4SelectedLabel.setHorizontalAlignment(JLabel.RIGHT);
		n4SelectedBox = makeStickyTextField("nSelected4", Integer.toString(BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED),
				true);
		n4SelectedBox.setVisible(!spinnerLabel4.isEmpty());

		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel("   "), 0.41), new LayoutItem(n1SelectedLabel, 0.03),
				new LayoutItem(n1SelectedSpinner, 0.03), new LayoutItem(new JLabel("   "), 0.06),
				new LayoutItem(n2SelectedLabel, 0.03), new LayoutItem(n2SelectedSpinner, 0.03),
				new LayoutItem(new JLabel("   "), 0.41)));

		panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel("   "), 0.41), new LayoutItem(n3SelectedLabel, 0.03),
				new LayoutItem(n3SelectedSpinner, 0.03), new LayoutItem(new JLabel("   "), 0.06),
				new LayoutItem(n4SelectedLabel, 0.03), new LayoutItem(n4SelectedSpinner, 0.03),
				new LayoutItem(new JLabel("   "), 0.41)));

		// panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel(" "), 0.47), new
		// LayoutItem(n2SelectedLabel, 0.03),
		// new LayoutItem(n2SelectedSpinner, 0.03), new LayoutItem(new JLabel(" "),
		// 0.47)));
		//
		/// panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel(" "), 0.47), new
		// LayoutItem(n3SelectedLabel, 0.03),
		// new LayoutItem(n3SelectedSpinner, 0.03), new LayoutItem(new JLabel(" "),
		// 0.47)));

		// panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel(" "), 0.47), new
		// LayoutItem(n4SelectedLabel, 0.03),
		// new LayoutItem(n4SelectedSpinner, 0.03), new LayoutItem(new JLabel(" "),
		// 0.47)));

		LayoutUtils.doGridLayout(intSelectorPanel, panelGrid);

		intSelectorPanel.add(n1SelectedBox);
		n1SelectedBox.setVisible(false);

		intSelectorPanel.add(n2SelectedBox);
		n2SelectedBox.setVisible(false);

		intSelectorPanel.add(n3SelectedBox);
		n3SelectedBox.setVisible(false);

		intSelectorPanel.add(n4SelectedBox);
		n4SelectedBox.setVisible(false);

		intSelectorWrapPanel = new JPanel();
		intSelectorWrapPanel.setLayout(new BoxLayout(intSelectorWrapPanel, BoxLayout.Y_AXIS));
		intSelectorWrapBorder = BorderFactory.createTitledBorder(panelTitle);
		intSelectorWrapBorder.setTitleFont(boldFontForTitlePanel(intSelectorWrapBorder, false));
		intSelectorWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		intSelectorWrapPanel.setBorder(intSelectorWrapBorder);
		intSelectorWrapPanel.add(intSelectorPanel);
	}

	public void setInt1Selected(Integer val) {
		n1SelectedBox.setText(val.toString());
	}

	public void setInt2Selected(Integer val) {
		n2SelectedBox.setText(val.toString());
	}

	public void setInt3Selected(Integer val) {
		n3SelectedBox.setText(val.toString());
	}

	public void setInt4Selected(Integer val) {
		n4SelectedBox.setText(val.toString());
	}

	public Integer getInt1Selected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.n1SelectedBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public Integer getInt2Selected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.n2SelectedBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public Integer getInt3Selected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.n3SelectedBox.getText());
		} catch (Exception e) {
			intSelected = BinnerConstants.DEFAULT_BATCH_LEVEL_REPORTED;
		}
		return intSelected;
	}

	public Integer getInt4Selected() {
		Integer intSelected = defaultSelection;
		try {
			intSelected = Integer.parseInt(this.n4SelectedBox.getText());
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
