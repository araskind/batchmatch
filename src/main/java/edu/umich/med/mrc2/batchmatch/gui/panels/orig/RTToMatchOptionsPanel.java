////////////////////////////////////////////////////
// RTToMatchOptionsPanel.java
// Written by Jan Wigginton May 2019
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.gui.panels.orig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;

public class RTToMatchOptionsPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -1288221113226320905L;

	private JRadioButton matchByExpectedRTButton, matchByObservedRTButton, matchByParsedRTButton;

	public RTToMatchOptionsPanel() {
		super();
		initializeStickySettings("batchMatchRTMatchOptions", BatchMatchConstants.PROPS_FILE);

	}

	public void setupPanel() {

		initializeArrays();

		JPanel rtOptionPanel = new JPanel();
		rtOptionPanel.setLayout(new BoxLayout(rtOptionPanel, BoxLayout.X_AXIS));

		matchByExpectedRTButton = makeStickyRadioButton("Expected RT ", "matchByExpectedRT", false, true);
		matchByExpectedRTButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// updateForInputTypeChange();
			}
		});
		// matchByExpectedRTButton.setSelected(true);

		matchByObservedRTButton = makeStickyRadioButton("Observed RT ", "matchByObservedRT", false, true);
		matchByObservedRTButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// updateForInputTypeChange();
			}
		});
		// matchByObservedRTButton.setEnabled(false);

		matchByParsedRTButton = makeStickyRadioButton("Parsed RT ", "matchByParsedRT", false, true);
		matchByParsedRTButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// updateForInputTypeChange();
			}
		});
		// matchByParsedRTButton.setEnabled(false);

		ButtonGroup matchTypeGroup = new ButtonGroup();
		matchTypeGroup.add(matchByExpectedRTButton);
		matchTypeGroup.add(matchByObservedRTButton);
		matchTypeGroup.add(matchByParsedRTButton);

		rtOptionPanel.add(Box.createHorizontalGlue());
		rtOptionPanel.add(matchByExpectedRTButton);
		rtOptionPanel.add(Box.createHorizontalGlue());
		rtOptionPanel.add(matchByObservedRTButton);
		rtOptionPanel.add(Box.createHorizontalGlue());
		rtOptionPanel.add(matchByParsedRTButton);
		rtOptionPanel.add(Box.createHorizontalGlue());

		JPanel rtOptionWrapPanel = new JPanel();
		rtOptionWrapPanel.setLayout(new BoxLayout(rtOptionWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder rtOptionWrapBorder = BorderFactory.createTitledBorder("Match Feature RT By");
		rtOptionWrapBorder.setTitleFont(boldFontForTitlePanel(rtOptionWrapBorder, false));
		rtOptionWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		rtOptionWrapPanel.setBorder(rtOptionWrapBorder);
		rtOptionWrapPanel.add(rtOptionPanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(rtOptionWrapPanel);
	}

	public Boolean useExpectedForMatch() {
		return matchByExpectedRTButton.isSelected();
	}

	public void setUseExpectedForMatch() {
		matchByExpectedRTButton.setSelected(true);
	}

	public void setUseObservedForMatch() {
		matchByObservedRTButton.setSelected(true);
	}

	public void setUseParsedForMatch() {
		matchByParsedRTButton.setSelected(true);
	}

	public Boolean useObservedForMatch() {
		return matchByObservedRTButton.isSelected();
	}

	public Boolean useParsedForMatch() {
		return matchByParsedRTButton.isSelected();
	}

	public String tagForSelectedMatchType() {
		if (useExpectedForMatch())
			return "expected";
		if (useObservedForMatch())
			return "observed";

		return "parsed";

	}
}
