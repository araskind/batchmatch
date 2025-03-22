////////////////////////////////////////////////////
// BatchMatchSummaryReportTypePanel.java
// Written by Jan Wigginton November 2019
////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;

public abstract class BatchMatchSummaryDiagnosicsPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -7223728363639057996L;

	private JPanel outputFormatWrapPanel;
	private JPanel outputFormatPanel;
	private JCheckBox preMergeQCStyleBox, collapseWithIntensityStyleBox, matchSummaryStyleBox, recursiveLatticeBox;

	public BatchMatchSummaryDiagnosicsPanel(Boolean forMerge) {
		super();
		initializeStickySettings("batchReportTypesPanel", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		outputFormatPanel = new JPanel();
		outputFormatPanel.setLayout(new BoxLayout(outputFormatPanel, BoxLayout.X_AXIS));

		matchSummaryStyleBox = makeStickyCheckBox("Match Summary", "createReport", false, true);
		collapseWithIntensityStyleBox = makeStickyCheckBox("Collapsed With Intensity", "createCollapsed", false, true);
		recursiveLatticeBox = makeStickyCheckBox("Recursive Lattice", "createRecursiveLatticeData", false, true);
		preMergeQCStyleBox = makeStickyCheckBox("Pre-Merge Data QC", "qcData", false, true);
		preMergeQCStyleBox.setEnabled(false);

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
				}
				updateForReportTypeChange();
			}
		});

		preMergeQCStyleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (recursiveLatticeBox.isSelected()) {
					matchSummaryStyleBox.setSelected(false);
					collapseWithIntensityStyleBox.setSelected(false);
					recursiveLatticeBox.setSelected(false);
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

	public abstract void updateForReportTypeChange();
}
