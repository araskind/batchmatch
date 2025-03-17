////////////////////////////////////////////////////
// MultipleParametersPanel.java
// Written by Jan Wigginton September 2018
////////////////////////////////////////////////////
package edu.umich.batchmatch.gui.panels;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.gui.LayoutGrid;
import edu.umich.batchmatch.gui.LayoutItem;
import edu.umich.batchmatch.gui.LayoutUtils;
import edu.umich.batchmatch.gui.NumberVerifier;
import edu.umich.batchmatch.main.BatchMatchConstants;
import edu.umich.batchmatch.main.PostProccessConstants;
import edu.umich.batchmatch.utils.StringUtils;

public class MultipleParametersPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 1L;
	private JTextField valueBox1, valueBox2, valueBox3, valueBox4;
	private String panelTitle, label1, label2, label3, label4;
	private JPanel searchParametersWrapPanel;
	private JPanel overallSearchWrapPanel;

	public MultipleParametersPanel(String tagForSettings) {
		super();
		initializeStickySettings(tagForSettings, BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel(String panelTitle, String boxLabel1, String boxLabel2, String boxLabel3) {
		setupPanel(panelTitle, boxLabel1, boxLabel2, boxLabel3, "");
	}

	public void setupPanel(String panelTitle, String boxLabel1, String boxLabel2) {
		setupPanel(panelTitle, boxLabel1, boxLabel2, "", "");
	}

	public void setupPanel(String panelTitle, String boxLabel1) {
		setupPanel(panelTitle, boxLabel1, "", "", "");
	}

	public void setupPanel(String panelTitle, String label1, String label2, String label3, String label4) {

		this.panelTitle = panelTitle;
		this.label1 = label1;
		this.label2 = label2;
		this.label3 = label3;
		this.label4 = label4;

		initializeArrays();
		setupPanel();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(2));
		// add(intSelectorWrapPanel);
		add(Box.createVerticalStrut(2));

		setupTextFieldListeners();
	}

	public void setupPanel() {
		initializeArrays();

		setupParametersPanel();
		// setupSearchTypePanel();
		// setupLogisticParametersPanel();
		setupOverallSearchPanel();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(1));
		add(overallSearchWrapPanel);
		add(Box.createVerticalStrut(1));

		setupTextFieldListeners();
	}

	private void setupOverallSearchPanel() {

		overallSearchWrapPanel = new JPanel();
		overallSearchWrapPanel.setLayout(new BoxLayout(overallSearchWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder overallSearchWrapBorder = BorderFactory.createTitledBorder(getPanelTitle());
		overallSearchWrapBorder.setTitleFont(boldFontForTitlePanel(overallSearchWrapBorder, false));
		overallSearchWrapBorder.setTitleColor(PostProccessConstants.TITLE_COLOR);
		overallSearchWrapPanel.setBorder(overallSearchWrapBorder);

		add(Box.createVerticalStrut(1));
		overallSearchWrapPanel.add(searchParametersWrapPanel);
		add(Box.createVerticalStrut(1));
	}

	private void setupParametersPanel() {
		JPanel searchParametersPanel = new JPanel();

		JLabel labelBox1 = new JLabel(label1 + " ");
		valueBox1 = makeStickyTextField("val1", Double.toString(PostProccessConstants.DEFAULT_MASS_SEARCH_TOL), true);
		valueBox1.setInputVerifier(new NumberVerifier(this.valueBox1, "Tolerance for mass search"));
		valueBox1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JLabel labelBox2 = new JLabel(label2 + " ");
		valueBox2 = makeStickyTextField("val2", Double.toString(PostProccessConstants.DEFAULT_RT_SEARCH_TOL), true);
		valueBox2.setInputVerifier(new NumberVerifier(valueBox2, "Tolerance for rt search"));
		valueBox2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JLabel labelBox3 = new JLabel(label3 + " ");
		valueBox3 = makeStickyTextField("val3", Double.toString(PostProccessConstants.DEFAULT_ANNEALING_STRETCH_FACTOR),
				true);
		valueBox3.setInputVerifier(new NumberVerifier(valueBox3, "Annealing Stretch Factor"));
		valueBox3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JLabel labelBox4 = new JLabel(label4 + " ");
		valueBox4 = makeStickyTextField("val4", Double.toString(PostProccessConstants.DEFAULT_N_SEARCH_RESULTS), true);
		valueBox4.setInputVerifier(new NumberVerifier(valueBox4, "Maximum # search results per feature"));
		valueBox4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		LayoutGrid panelGrid = new LayoutGrid();

		boolean useTwoByTwo = !StringUtils.isNullOrEmpty(label2);

		labelBox1.setHorizontalAlignment(SwingConstants.RIGHT);
		labelBox2.setHorizontalAlignment(SwingConstants.RIGHT);
		labelBox3.setHorizontalAlignment(SwingConstants.RIGHT);
		labelBox4.setHorizontalAlignment(SwingConstants.RIGHT);

		if (!useTwoByTwo) {
			panelGrid.addRow(Arrays.asList(new LayoutItem(labelBox1, 0.45), new LayoutItem(valueBox1, 0.45),
					new LayoutItem(new JLabel(" "), 0.10)));
		} else {
			panelGrid.addRow(Arrays.asList(new LayoutItem(new JLabel(" "), 0.05), new LayoutItem(labelBox1, 0.2),
					new LayoutItem(valueBox1, 0.2), new LayoutItem(labelBox2, 0.2), new LayoutItem(valueBox2, 0.2),
					new LayoutItem(new JLabel(" "), 0.05)));

			boolean haveFourth = !StringUtils.isNullOrEmpty(label4);
			boolean haveThird = !StringUtils.isNullOrEmpty(label3);

			if (haveThird) {
				if (haveFourth) {
					panelGrid
							.addRow(Arrays.asList(new LayoutItem(new JLabel(" "), 0.05), new LayoutItem(labelBox3, 0.2),
									new LayoutItem(valueBox3, 0.2), new LayoutItem(labelBox4, 0.2),
									new LayoutItem(valueBox4, 0.2), new LayoutItem(new JLabel(" "), 0.05)));
				} else {
					panelGrid
							.addRow(Arrays.asList(new LayoutItem(new JLabel(" "), 0.05), new LayoutItem(labelBox3, 0.2),
									new LayoutItem(valueBox3, 0.2), new LayoutItem(new JLabel(" "), 0.2),
									new LayoutItem(new JLabel(" "), 0.2), new LayoutItem(new JLabel(" "), 0.05)));
				}
			}
		}
		LayoutUtils.doGridLayout(searchParametersPanel, panelGrid);
		searchParametersWrapPanel = new JPanel();
		searchParametersWrapPanel.setLayout(new BoxLayout(searchParametersWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder searchParametersWrapBorder = BorderFactory.createTitledBorder("");
		searchParametersWrapBorder.setTitleFont(boldFontForTitlePanel(searchParametersWrapBorder, false));
		searchParametersWrapPanel.setBorder(searchParametersWrapBorder);
		searchParametersWrapPanel.add(searchParametersPanel);
	}

	public Double getVal1() {
		Double maxN = null;
		try {
			maxN = Double.parseDouble(this.valueBox1.getText());
		} catch (Exception e) {
			maxN = null;
		}
		return maxN;
	}

	public Double getVal2() {
		Double massTol = 0.0;
		try {
			massTol = Double.parseDouble(this.valueBox2.getText());
		} catch (Exception e) {
			massTol = PostProccessConstants.DEFAULT_MASS_SEARCH_TOL;
		}
		return massTol;
	}

	public Double getVal3() {
		Double val = 0.0;
		try {
			val = Double.parseDouble(this.valueBox3.getText());
		} catch (Exception e) {
			val = PostProccessConstants.DEFAULT_RT_SEARCH_TOL;
		}
		return val;
	}

	public Double getVal4() {
		Double val = 0.0;
		try {
			val = Double.parseDouble(this.valueBox4.getText());
		} catch (Exception e) {
			val = PostProccessConstants.DEFAULT_RT_SEARCH_TOL;
		}
		return val;
	}

	public String getPanelTitle() {
		return panelTitle;
	}

	public void setPanelTitle(String panelTitle) {
		this.panelTitle = panelTitle;
	}
}
