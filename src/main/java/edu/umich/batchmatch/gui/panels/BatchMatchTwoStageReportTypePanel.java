////////////////////////////////////////////////////
// BatchMatchTwoStageReportTypePanel.java
// Written by Jan Wigginton June 2023
////////////////////////////////////////////////////

package edu.umich.batchmatch.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.main.BatchMatchConstants;
import edu.umich.batchmatch.main.PostProccessConstants;

public abstract class BatchMatchTwoStageReportTypePanel extends StickySettingsPanel {

	private static final long serialVersionUID = -7223728363639057996L;

	private JPanel outputFormatWrapPanel;
	private JPanel outputFormatPanel;
	private JCheckBox projectDataBox, dataSetMappingBox, createMockDataBox;

	public BatchMatchTwoStageReportTypePanel(Boolean forMerge) {
		super();
		initializeStickySettings("batchTwoStageTypePanel", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		outputFormatPanel = new JPanel();
		outputFormatPanel.setLayout(new BoxLayout(outputFormatPanel, BoxLayout.X_AXIS));

		projectDataBox = makeStickyCheckBox("Project Source Data To Destination ", "projectData", false, true);
		dataSetMappingBox = makeStickyCheckBox("Data Set Mapping", "createDataMapping", false, true);
		createMockDataBox = makeStickyCheckBox("Create Mock Binner Data", "createMockData", false, true);

		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(projectDataBox);
		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(dataSetMappingBox);
		outputFormatPanel.add(Box.createHorizontalGlue());
		outputFormatPanel.add(createMockDataBox);
		outputFormatPanel.add(Box.createHorizontalGlue());

		setupCheckBoxes();

		outputFormatWrapPanel = new JPanel();
		outputFormatWrapPanel.setLayout(new BoxLayout(outputFormatWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder outputFormatWrapBorder = BorderFactory.createTitledBorder("Specify Report Type");
		outputFormatWrapBorder.setTitleFont(boldFontForTitlePanel(outputFormatWrapBorder, false));
		outputFormatWrapBorder.setTitleColor(PostProccessConstants.TITLE_COLOR);
		outputFormatWrapPanel.setBorder(outputFormatWrapBorder);
		outputFormatWrapPanel.add(outputFormatPanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(outputFormatWrapPanel);

		setupTextFieldListeners();
	}

	private void setupCheckBoxes() {

		projectDataBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (projectDataBox.isSelected()) {
					dataSetMappingBox.setSelected(false);
					createMockDataBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});

		dataSetMappingBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (dataSetMappingBox.isSelected()) {
					projectDataBox.setSelected(false);
					createMockDataBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});

		createMockDataBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (createMockDataBox.isSelected()) {
					dataSetMappingBox.setSelected(false);
					projectDataBox.setSelected(false);
				}
				updateForReportTypeChange();
			}
		});
	}

	public Boolean createProjectedData() {
		return this.projectDataBox.isSelected();
	}

	public Boolean createMockData() {
		return this.createMockDataBox.isSelected();
	}

	public Boolean createDataSetMapping() {
		return this.dataSetMappingBox.isSelected();
	}

	public abstract void updateForReportTypeChange();
}
