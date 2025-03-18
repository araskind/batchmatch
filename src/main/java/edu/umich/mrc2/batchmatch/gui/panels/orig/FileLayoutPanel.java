////////////////////////////////////////////////////
// FileLayoutPanel.java
// Write by Jan Wigginton July 2019
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.gui.ChecklistParameters;
import edu.umich.mrc2.batchmatch.gui.PopupChecklist;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.utils.ListUtils;

public abstract class FileLayoutPanel extends StickySettingsPanel {

	private static final long serialVersionUID = -385664060785583897L;

	private JLabel controlCountLabel;

	private List<String> controlLabels, sampleLabels;
	private String controlCountDescriptor = "";
	private final String defaultDescriptor = "No control selection loaded         ";

	public FileLayoutPanel() {
		super();

		initializeStickySettings("batchMatchFileLayout", BatchMatchConstants.PROPS_FILE);
	}

	public void setupPanel() {

		initializeArrays();

		JPanel fileLayoutPanel = new JPanel();
		fileLayoutPanel.setLayout(new BoxLayout(fileLayoutPanel, BoxLayout.X_AXIS));
		fileLayoutPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton selectControlsButton = new JButton("Select Controls...");
		selectControlsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!dataFullySpecified()) {
					JOptionPane.showMessageDialog(null, "Please import intensity data before selecting controls.    "
							+ BinnerConstants.LINE_SEPARATOR);
					return;
				}
				selectControls(true);
			}
		});

		JButton selectMasterPoolsButton = new JButton("Select Master Pools...");
		selectMasterPoolsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!dataFullySpecified()) {
					JOptionPane.showMessageDialog(null, "Please import intensity data before selecting controls.    "
							+ BinnerConstants.LINE_SEPARATOR);
					return;
				}
				selectControls(true);
			}
		});
		controlCountDescriptor = defaultDescriptor;
		controlCountLabel = new JLabel(controlCountDescriptor);
		controlCountLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		controlCountLabel.setFont(new Font("Courier New", Font.ITALIC, 14));

		selectControlsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		selectMasterPoolsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

		fileLayoutPanel.add(Box.createHorizontalStrut(100));
		fileLayoutPanel.add(selectControlsButton);
		// fileLayoutPanel.add(Box.createHorizontalStrut(100));
		// fileLayoutPanel.add(selectMasterPoolsButton);

		fileLayoutPanel.add(Box.createHorizontalGlue());
		fileLayoutPanel.add(Box.createHorizontalStrut(20));
		fileLayoutPanel.add(controlCountLabel);
		fileLayoutPanel.add(Box.createHorizontalGlue());

		JPanel fileLayoutWrapPanel = new JPanel();
		fileLayoutWrapPanel.setLayout(new BoxLayout(fileLayoutWrapPanel, BoxLayout.Y_AXIS));
		TitledBorder inputTypeWrapBorder = BorderFactory
				.createTitledBorder("Specify file layout (for RSD calculation)");
		inputTypeWrapBorder.setTitleFont(boldFontForTitlePanel(inputTypeWrapBorder, false));
		inputTypeWrapBorder.setTitleColor(BinnerConstants.TITLE_COLOR);
		fileLayoutWrapPanel.setBorder(inputTypeWrapBorder);
		fileLayoutWrapPanel.add(fileLayoutPanel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(fileLayoutWrapPanel);
	}

	// Select all
	public List<String> selectControls(Boolean fShowDialog) {

		List<String> sampListForDialog = grabColumnList();

		ChecklistParameters params = new ChecklistParameters();
		params.setInputList(sampListForDialog);
		params.setDialogTitle("Specify Controls");
		params.setPanelTitle("Select Controls (for RSD)");
		params.setPropsFile("postprocess.recent.controls");
		params.setPropsValue("controls");
		PopupChecklist checklist = new PopupChecklist(null, params, fShowDialog);
		controlLabels = checklist.getSelections();

		int nPos = 0, nNeg = 0;
		for (int i = 0; i < controlLabels.size(); i++)
			if (controlLabels.get(i).endsWith("-P"))
				nPos++;
			else
				nNeg++;
		this.controlCountDescriptor = "" + controlLabels.size() + " controls selected";
		this.controlCountLabel.setText(controlCountDescriptor);

		return controlLabels;
	}

	public List<String> getControlLabels() {

		controlLabels = selectControls(false);
		if (ListUtils.isEmptyOrNull(controlLabels))
			controlLabels = selectControls(true);

		return controlLabels;
	}

	public List<String> getSampleLabels() {
		return sampleLabels;
	}

	public void setDefaultDescriptor() {
		this.controlCountDescriptor = defaultDescriptor;
		controlCountLabel.setText(controlCountDescriptor);
	}

	public abstract List<String> grabColumnList();

	public abstract Boolean dataFullySpecified();
}
