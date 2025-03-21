////////////////////////////////////////////////////
// OutputFileNameTagPanel.java
// Written by Jan Wigginton August 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.gui.LayoutGrid;
import edu.umich.mrc2.batchmatch.gui.LayoutItem;
import edu.umich.mrc2.batchmatch.gui.LayoutUtils;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.utils.orig.StringUtils;

public class OutputFileNameTagPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -385664060785583897L;

	private JTextField fileNameTagBox;
	private JPanel fileNameTagWrapPanel;

	private String panelTitle = "Specify Tag for Merge File Name";

	public OutputFileNameTagPanel(String tagForPanel) {
		this(tagForPanel, StringUtils.isEmptyOrNull(tagForPanel) ? 0 : (tagForPanel.contains("Create") ? 1 : 2));
	}

	public OutputFileNameTagPanel(String tagForPanel, int instanceId) {
		super();

		if (instanceId == 0)
			initializeStickySettings("batchProcessOutput" + tagForPanel + "Tag", BatchMatchConstants.PROPS_FILE);
		else
			initializeStickySettings("batchProcessOutput" + tagForPanel + instanceId, BatchMatchConstants.PROPS_FILE);

		if (tagForPanel.contains("Summarize"))
			panelTitle = "Specify Tag for Report File Name";
	}

	public void setupPanel() {

		initializeArrays();

		JPanel fileNameTagPanel = new JPanel();
		fileNameTagPanel.setLayout(new BoxLayout(fileNameTagPanel, BoxLayout.X_AXIS));

		JLabel fileNameTagLabel = new JLabel("File Name Tag  ");
		fileNameTagBox = makeStickyTextField("fileNameTag", "merge_output", true);
		fileNameTagBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		fileNameTagLabel.setHorizontalAlignment(JLabel.RIGHT);

		JLabel rtTolUnitsLabel = new JLabel("  ");

		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(fileNameTagLabel, 0.45), new LayoutItem(fileNameTagBox, 0.45),
				new LayoutItem(rtTolUnitsLabel, 0.10)));

		LayoutUtils.doGridLayout(fileNameTagPanel, panelGrid);

		fileNameTagWrapPanel = new JPanel();
		fileNameTagWrapPanel.setLayout(new BoxLayout(fileNameTagWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder fileNameTagWrapBorder = BorderFactory.createTitledBorder(panelTitle);
		fileNameTagWrapBorder.setTitleFont(boldFontForTitlePanel(fileNameTagWrapBorder, false));
		fileNameTagWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		fileNameTagWrapPanel.setBorder(fileNameTagWrapBorder);
		fileNameTagWrapPanel.add(fileNameTagPanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(fileNameTagWrapPanel);
		setupTextFieldListeners();
	}

	public void setPanelTitle(String title) {
		this.panelTitle = title;
	}

	public String getFileNameTag() {
		return fileNameTagBox.getText();
	}

	public void setFileNameTag(String tag) {
		fileNameTagBox.setText(tag);
	}
}
