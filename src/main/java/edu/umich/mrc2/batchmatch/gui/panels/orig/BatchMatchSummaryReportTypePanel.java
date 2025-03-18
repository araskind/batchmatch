////////////////////////////////////////////////////
// BatchMatchSummaryReportTypePanel.java
// Written by Jan Wigginton November 2019
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;

public abstract class BatchMatchSummaryReportTypePanel extends StickySettingsPanel {

	private static final long serialVersionUID = -7223728363639057996L;

	private JPanel outputFormatWrapPanel;
	private JPanel outputFormatPanel;
	private JCheckBox binnerFilterStyleBox, preMergeQCStyleBox, collapseWithIntensityStyleBox, matchSummaryStyleBox,
			recursiveLatticeBox;

	public BatchMatchSummaryReportTypePanel(Boolean forMerge) {
		super();
		initializeStickySettings("batchReportTypesPanel", BatchMatchConstants.PROPS_FILE);
	}

	// MatchStatusInfo

	public void setupPanel() {

		initializeArrays();

		outputFormatPanel = new JPanel();
		outputFormatPanel.setLayout(new BoxLayout(outputFormatPanel, BoxLayout.X_AXIS));

		matchSummaryStyleBox = makeStickyCheckBox("Match Summary", "createReport", false, true);
		collapseWithIntensityStyleBox = makeStickyCheckBox("Collapsed With Intensity", "createCollapsed", false, true);
		recursiveLatticeBox = makeStickyCheckBox("Recursive Lattice", "createRecursiveLatticeData", false, true);
		binnerFilterStyleBox = makeStickyCheckBox("Filter Binner Input", "filterData", false, true);

		preMergeQCStyleBox = makeStickyCheckBox("Ungroup Features", "qcData", false, true);
		// preMergeQCStyleBox.setEnabled(false);

		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(matchSummaryStyleBox);
		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(collapseWithIntensityStyleBox);
		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(recursiveLatticeBox);

		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(preMergeQCStyleBox);

		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(binnerFilterStyleBox);

		setupCheckBoxes();

		outputFormatWrapPanel = new JPanel();
		outputFormatWrapPanel.setLayout(new BoxLayout(outputFormatWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder outputFormatWrapBorder = BorderFactory.createTitledBorder("Specify Report Type");
		outputFormatWrapBorder.setTitleFont(boldFontForTitlePanel(outputFormatWrapBorder, false));
		outputFormatWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		outputFormatWrapPanel.setBorder(outputFormatWrapBorder);
		outputFormatWrapPanel.add(outputFormatPanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(outputFormatWrapPanel);

		setupTextFieldListeners();
	}

	private void setupCheckBoxes() {

		matchSummaryStyleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (matchSummaryStyleBox.isSelected()) {
					recursiveLatticeBox.setSelected(false);
					collapseWithIntensityStyleBox.setSelected(false);
					preMergeQCStyleBox.setSelected(false);
					binnerFilterStyleBox.setSelected(false);

				}
				collapseWithIntensityStyleBox.setSelected(!matchSummaryStyleBox.isSelected());
				updateForReportTypeChange();
			}
		});

		collapseWithIntensityStyleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (collapseWithIntensityStyleBox.isSelected()) {
					matchSummaryStyleBox.setSelected(false);
					recursiveLatticeBox.setSelected(false);
					preMergeQCStyleBox.setSelected(false);
					binnerFilterStyleBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});

		recursiveLatticeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (recursiveLatticeBox.isSelected()) {
					matchSummaryStyleBox.setSelected(false);
					collapseWithIntensityStyleBox.setSelected(false);
					preMergeQCStyleBox.setSelected(false);
					binnerFilterStyleBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});

		preMergeQCStyleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (preMergeQCStyleBox.isSelected()) {
					matchSummaryStyleBox.setSelected(false);
					collapseWithIntensityStyleBox.setSelected(false);
					recursiveLatticeBox.setSelected(false);
					binnerFilterStyleBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});

		binnerFilterStyleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (binnerFilterStyleBox.isSelected()) {
					matchSummaryStyleBox.setSelected(false);
					collapseWithIntensityStyleBox.setSelected(false);
					recursiveLatticeBox.setSelected(false);
					preMergeQCStyleBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});
	}

	public Boolean createRecursiveLattice() {
		return this.recursiveLatticeBox.isSelected();
	}

	public Boolean createCollapsed() {
		return this.collapseWithIntensityStyleBox.isSelected();
	}

	public Boolean createMatchSummary() {
		return this.matchSummaryStyleBox.isSelected();
	}

	public Boolean createQCReport() {
		return this.preMergeQCStyleBox.isSelected();
	}

	public Boolean filterBinnerInput() {
		return this.binnerFilterStyleBox.isSelected();
	}

	public abstract void updateForReportTypeChange();
}
