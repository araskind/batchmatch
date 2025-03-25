////////////////////////////////////////////////////
// ToleranceParametersPanel.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutGrid;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutItem;
import edu.umich.med.mrc2.batchmatch.gui.orig.LayoutUtils;
import edu.umich.med.mrc2.batchmatch.gui.orig.NumberVerifier;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.main.PostProccessConstants;

public class ToleranceParametersPanel extends StickySettingsPanel {

	private JTextField massSearchTolBox, rtSearchTolBox, annealingStretchFactorBox, nResultsReturnedBox;
	private JTextField logisticMaxBox, logisticMidBox, logisticCurvatureBox;
	private JPanel searchParametersWrapPanel, searchTypeWrapPanel, logisticParametersWrapPanel, overallSearchWrapPanel;
	private JRadioButton logisticSearchButton, regularSearchButton;

	private String panelTitle = "Specify Search Parameters";

	public ToleranceParametersPanel(Boolean forMerge) {
		super();
		if (forMerge)
			initializeStickySettings("batchMatchMergePars", BatchMatchConstants.PROPS_FILE);
		else
			initializeStickySettings("batchMatchSearchPars", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {
		initializeArrays();

		setupParametersPanel();
		setupSearchTypePanel();
		setupLogisticParametersPanel();
		setupOverallSearchPanel();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(1));
		add(overallSearchWrapPanel);
		add(Box.createVerticalStrut(1));

		setupTextFieldListeners();
	}

	private void setupSearchTypePanel() {
		logisticSearchButton = makeStickyRadioButton("Logistic   ", "logisticForSearch", false, true);
		regularSearchButton = makeStickyRadioButton("Traditional  ", "traditionalForSearch", true, true);

		logisticSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				logisticParametersWrapPanel.setVisible(true);
				// sliderTitle.setEnabled(false);
				// cutoffSlider.setEnabled(false);
			}
		});

		regularSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				logisticParametersWrapPanel.setVisible(false);
			}
		});

		ButtonGroup searchTypeGroup = new ButtonGroup();
		searchTypeGroup.add(logisticSearchButton);
		searchTypeGroup.add(regularSearchButton);

		JPanel searchTypePanel = new JPanel();
		searchTypePanel.setLayout(new BoxLayout(searchTypePanel, BoxLayout.X_AXIS));
		searchTypePanel.add(Box.createHorizontalGlue());
		searchTypePanel.add(regularSearchButton);
		searchTypePanel.add(Box.createHorizontalGlue());
		searchTypePanel.add(logisticSearchButton);
		searchTypePanel.add(Box.createHorizontalGlue());

		searchTypeWrapPanel = new JPanel();
		searchTypeWrapPanel.setLayout(new BoxLayout(searchTypeWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder searchTypeWrapBorder = BorderFactory.createTitledBorder("");
		searchTypeWrapBorder.setTitleFont(boldFontForTitlePanel(searchTypeWrapBorder, false));
		searchTypeWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		searchTypeWrapPanel.setBorder(searchTypeWrapBorder);
		searchTypeWrapPanel.add(searchTypePanel);
		searchTypeWrapPanel.setVisible(false);
	}

	private void setupOverallSearchPanel() {
		JPanel overallLibraryPanel = new JPanel();
		overallLibraryPanel.setLayout(new BoxLayout(overallLibraryPanel, BoxLayout.Y_AXIS));
		overallLibraryPanel.add(Box.createVerticalStrut(3));

		overallSearchWrapPanel = new JPanel();
		overallSearchWrapPanel.setLayout(new BoxLayout(overallSearchWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder overallSearchWrapBorder = BorderFactory.createTitledBorder(getPanelTitle());
		overallSearchWrapBorder.setTitleFont(boldFontForTitlePanel(overallSearchWrapBorder, false));
		overallSearchWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		overallSearchWrapPanel.setBorder(overallSearchWrapBorder);

		add(Box.createVerticalStrut(1));
		overallSearchWrapPanel.add(searchTypeWrapPanel);
		add(Box.createVerticalStrut(1));
		overallSearchWrapPanel.add(searchParametersWrapPanel);
		add(Box.createVerticalStrut(1));
		overallSearchWrapPanel.add(logisticParametersWrapPanel);
		add(Box.createVerticalStrut(1));
	}

	private void setupLogisticParametersPanel() {
		JPanel logisticParametersPanel = new JPanel();

		JLabel logisticMaxLabel = new JLabel("Maximum ");
		logisticMaxBox = makeStickyTextField("logisticMax", Double.toString(PostProccessConstants.DEFAULT_LOGISTIC_MAX),
				true);
		logisticMaxBox.setInputVerifier(new NumberVerifier(logisticMaxBox, "Tolerance for mass search"));
		logisticMaxBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel adductNLMassTolUnitsLabel = new JLabel(" ");

		JLabel logisticMidLabel = new JLabel("Midpoint  ");
		logisticMidBox = makeStickyTextField("logisticMid", Double.toString(PostProccessConstants.DEFAULT_LOGISTIC_MID),
				true);
		logisticMidBox.setInputVerifier(new NumberVerifier(logisticMidBox, "Midpoint for RT Tolerance Curve"));
		logisticMidBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel rtTolUnitsLabel = new JLabel("  ");

		JLabel logisticCurveLabel = new JLabel("Curvature  ");
		logisticCurvatureBox = makeStickyTextField("logisticCurvature",
				Double.toString(PostProccessConstants.DEFAULT_LOGISTIC_CURVATURE), true);
		logisticCurvatureBox.setInputVerifier(new NumberVerifier(logisticCurvatureBox, "Logistic Curvature"));
		logisticCurvatureBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		logisticCurvatureBox.setEnabled(false);
//		if (!nResultsReturnedBox.isEnabled())
//			nResultsReturnedBox.setText(BinnerConstants.DEFAULT_N_SEARCH_RESULTS.toString());

		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(logisticMaxLabel, 0.45), new LayoutItem(logisticMaxBox, 0.45),
				new LayoutItem(adductNLMassTolUnitsLabel, 0.10)));
		panelGrid.addRow(Arrays.asList(new LayoutItem(logisticMidLabel, 0.45), new LayoutItem(logisticMidBox, 0.45),
				new LayoutItem(rtTolUnitsLabel, 0.10)));
		panelGrid.addRow(Arrays.asList(new LayoutItem(logisticCurveLabel, 0.45),
				new LayoutItem(logisticCurvatureBox, 0.45), new LayoutItem(rtTolUnitsLabel, 0.10)));

		LayoutUtils.doGridLayout(logisticParametersPanel, panelGrid);

		logisticParametersWrapPanel = new JPanel();
		logisticParametersWrapPanel.setLayout(new BoxLayout(logisticParametersWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder logisticParametersWrapBorder = BorderFactory.createTitledBorder("Logistic Curve Parameters");
		logisticParametersWrapBorder.setTitleFont(boldFontForTitlePanel(logisticParametersWrapBorder, false));
		// logisticParametersWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		logisticParametersWrapPanel.setBorder(logisticParametersWrapBorder);
		logisticParametersWrapPanel.add(logisticParametersPanel);

		logisticParametersWrapPanel.setVisible(logisticSearchButton.isSelected());
	}

	private void setupParametersPanel() {
		JPanel searchParametersPanel = new JPanel();

		JLabel massSearchTolLabel = new JLabel("Mass Tolerance ");
		massSearchTolBox = makeStickyTextField("searchMassTol",
				Double.toString(PostProccessConstants.DEFAULT_MASS_SEARCH_TOL), true);
		massSearchTolBox.setInputVerifier(new NumberVerifier(massSearchTolBox, "Tolerance for mass search"));
		massSearchTolBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel adductNLMassTolUnitsLabel = new JLabel(" Da ");

		JLabel rtSearchTolLabel = new JLabel("RT Tolerance  ");
		rtSearchTolBox = makeStickyTextField("rtAnnotationTol", Double.toString(PostProccessConstants.DEFAULT_RT_SEARCH_TOL),
				true);
		rtSearchTolBox.setInputVerifier(new NumberVerifier(rtSearchTolBox, "Tolerance for rt search"));
		rtSearchTolBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel rtTolUnitsLabel = new JLabel("  ");

		JLabel annealingStretchFactorLabel = new JLabel("Annealing Stretch Factor  ");
		annealingStretchFactorBox = makeStickyTextField("annealStretchFactor",
				Double.toString(PostProccessConstants.DEFAULT_ANNEALING_STRETCH_FACTOR), true);
		annealingStretchFactorBox
				.setInputVerifier(new NumberVerifier(annealingStretchFactorBox, "Annealing Stretch Factor"));
		annealingStretchFactorBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel annealingStretchUnitsLabel = new JLabel("  ");

		// JLabel nResultsReturnedLabel = new JLabel("Max # Results Per Feature ");
		nResultsReturnedBox = makeStickyTextField("nResultsReturned",
				Integer.toString(PostProccessConstants.DEFAULT_N_SEARCH_RESULTS), true);
		nResultsReturnedBox
				.setInputVerifier(new NumberVerifier(rtSearchTolBox, "Maximum # search results per feature"));
		nResultsReturnedBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		nResultsReturnedBox.setEnabled(false);

		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(massSearchTolLabel, 0.45), new LayoutItem(massSearchTolBox, 0.45),
				new LayoutItem(adductNLMassTolUnitsLabel, 0.10)));

		panelGrid.addRow(Arrays.asList(new LayoutItem(rtSearchTolLabel, 0.45), new LayoutItem(rtSearchTolBox, 0.45),
				new LayoutItem(rtTolUnitsLabel, 0.10)));

		panelGrid.addRow(Arrays.asList(new LayoutItem(annealingStretchFactorLabel, 0.45),
				new LayoutItem(annealingStretchFactorBox, 0.45), new LayoutItem(annealingStretchUnitsLabel, 0.10)));

		LayoutUtils.doGridLayout(searchParametersPanel, panelGrid);
		searchParametersWrapPanel = new JPanel();
		searchParametersWrapPanel.setLayout(new BoxLayout(searchParametersWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder searchParametersWrapBorder = BorderFactory.createTitledBorder("");
		searchParametersWrapBorder.setTitleFont(boldFontForTitlePanel(searchParametersWrapBorder, false));
		searchParametersWrapPanel.setBorder(searchParametersWrapBorder);
		searchParametersWrapPanel.add(searchParametersPanel);
	}

	public Integer getMaxNResults() {
		Integer maxN = null;
		try {
			maxN = Integer.parseInt(this.massSearchTolBox.getText());
		} catch (Exception e) {
			maxN = null;
		}
		return maxN;
	}

	public Double getMassTol() {
		Double massTol = 0.0;
		try {
			massTol = Double.parseDouble(this.massSearchTolBox.getText());
		} catch (Exception e) {
			massTol = PostProccessConstants.DEFAULT_MASS_SEARCH_TOL;
		}
		return massTol;
	}

	public Double getRTTol() {
		Double rtTol = 0.0;
		try {
			rtTol = Double.parseDouble(this.rtSearchTolBox.getText());
		} catch (Exception e) {
			rtTol = PostProccessConstants.DEFAULT_RT_SEARCH_TOL;
		}
		return rtTol;
	}

	public Double getAnnealingStretchFactor() {
		Double annealingStretchFactor = 0.0;
		try {
			annealingStretchFactor = Double.parseDouble(this.annealingStretchFactorBox.getText());
		} catch (Exception e) {
			annealingStretchFactor = PostProccessConstants.DEFAULT_ANNEALING_STRETCH_FACTOR;
		}
		return annealingStretchFactor;
	}

	public Double getLogisticMax() {
		Double max = 0.0;
		try {
			max = Double.parseDouble(this.logisticMaxBox.getText());
		} catch (Exception e) {
			max = 0.01;
		}
		return max;
	}

	public Double getLogisticMid() {
		Double mid = 0.0;
		try {
			mid = Double.parseDouble(this.logisticMidBox.getText());
		} catch (Exception e) {
			mid = 0.01;
		}
		return mid;
	}

	public Double getLogisticCurvature() {
		Double curvature = 0.0;
		try {
			curvature = Double.parseDouble(this.logisticCurvatureBox.getText());
		} catch (Exception e) {
			curvature = 0.01;
		}
		return curvature;
	}

	public boolean getDoLogisticSearch() {
		return logisticSearchButton.isSelected();
	}

	public boolean getDoRegularSearch() {
		return regularSearchButton.isSelected();
	}

	public String getPanelTitle() {
		return panelTitle;
	}

	public void setPanelTitle(String panelTitle) {
		this.panelTitle = panelTitle;
	}
}
