////////////////////////////////////////////////////
// RTCutoffParametersPanel.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutGrid;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutItem;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.gui.orig.NumberVerifier;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.main.PostProccessConstants;

public class RTCutoffParametersPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 1L;
	private JTextField massSearchTolBox, rtDiffTrimThresholdtextBox;
	private JPanel searchParametersWrapPanel, overallSearchWrapPanel;
	private String panelTitle = "RT Pair Filtering";

	public RTCutoffParametersPanel(Boolean forMotrpac) {
		super();
		initializeStickySettings("rtCutoffsPars", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {
		
		initializeArrays();
		setupParametersPanel();
		setupOverallCriteriaPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(Box.createVerticalStrut(1));
		add(overallSearchWrapPanel);
		add(Box.createVerticalStrut(1));

		setupTextFieldListeners();
	}

	private void setupOverallCriteriaPanel() {
		
		JPanel overallLibraryPanel = new JPanel();
		overallLibraryPanel.setLayout(new BoxLayout(overallLibraryPanel, BoxLayout.Y_AXIS));
		overallLibraryPanel.add(Box.createVerticalStrut(3));

		overallSearchWrapPanel = new JPanel();
		overallSearchWrapPanel.setLayout(new BoxLayout(overallSearchWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder overallSearchWrapBorder = BorderFactory.createTitledBorder(getPanelTitle());
		overallSearchWrapBorder.setTitleFont(boldFontForTitlePanel(overallSearchWrapBorder, false));
		overallSearchWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		overallSearchWrapPanel.setBorder(overallSearchWrapBorder);

		overallSearchWrapPanel.add(Box.createVerticalStrut(1));
		overallSearchWrapPanel.add(searchParametersWrapPanel);
		overallSearchWrapPanel.add(Box.createVerticalStrut(1));
	}

	private void setupParametersPanel() {
		JPanel searchParametersPanel = new JPanel();

		JLabel massSearchTolLabel = new JLabel("Remove Pairs With RT Diff Greater Than:");
		massSearchTolBox = makeStickyTextField("searchMassTol",
				Double.toString(PostProccessConstants.DEFAULT_MASS_SEARCH_TOL), true);
		massSearchTolBox.setInputVerifier(new NumberVerifier(massSearchTolBox, "Tolerance for mass search"));
		massSearchTolBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel adductNLMassTolUnitsLabel = new JLabel("  ");

		JLabel rtSearchTolLabel = new JLabel("Trim RT Pairs With Diff Greater Than:");
		rtDiffTrimThresholdtextBox = makeStickyTextField("rtAnnotationTol", 
				Double.toString(PostProccessConstants.DEFAULT_RT_SEARCH_TOL), true);
		rtDiffTrimThresholdtextBox.setInputVerifier(new NumberVerifier(rtDiffTrimThresholdtextBox, "Tolerance for rt search"));
		rtDiffTrimThresholdtextBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel rtTolUnitsLabel = new JLabel("  ");

		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(massSearchTolLabel, 0.45), new LayoutItem(massSearchTolBox, 0.45),
				new LayoutItem(adductNLMassTolUnitsLabel, 0.10)));
		panelGrid.addRow(Arrays.asList(new LayoutItem(rtSearchTolLabel, 0.45), new LayoutItem(rtDiffTrimThresholdtextBox, 0.45),
				new LayoutItem(rtTolUnitsLabel, 0.10)));

		LayoutUtils.doGridLayout(searchParametersPanel, panelGrid);
		searchParametersWrapPanel = new JPanel();
		searchParametersWrapPanel.setLayout(new BoxLayout(searchParametersWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder searchParametersWrapBorder = BorderFactory.createTitledBorder("");
		searchParametersWrapBorder.setTitleFont(boldFontForTitlePanel(searchParametersWrapBorder, false));
		searchParametersWrapPanel.setBorder(searchParametersWrapBorder);
		searchParametersWrapPanel.add(searchParametersPanel);
	}

	public Double getRTDiffOutlierThreshold() {
		Double massTol = 0.0;
		try {
			massTol = Double.parseDouble(this.massSearchTolBox.getText());
		} catch (Exception e) {
			massTol = PostProccessConstants.DEFAULT_MASS_SEARCH_TOL;
		}
		return massTol;
	}

	public Double getRTDiffTrimThreshold() {
		Double rtTol = 0.0;

		try {
			rtTol = Double.parseDouble(this.rtDiffTrimThresholdtextBox.getText());
		} catch (Exception e) {
			rtTol = PostProccessConstants.DEFAULT_RT_SEARCH_TOL;
		}
		return rtTol;
	}

	public String getPanelTitle() {
		return panelTitle;
	}

	public void setPanelTitle(String panelTitle) {
		this.panelTitle = panelTitle;
	}
}
