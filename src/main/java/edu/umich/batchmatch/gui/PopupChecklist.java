package edu.umich.batchmatch.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import edu.umich.batchmatch.utils.PostProcessMergeNameExtractor;
import edu.umich.batchmatch.utils.RecentSelectionUtils;

public class PopupChecklist extends JDialog {

	private static final long serialVersionUID = 4384224248678352709L;

	private static final Color TITLE_COLOR = new Color(0, 0, 205);

	private ChecklistParameters params = null;
	private Properties recentlySelected = new Properties();

	private JDialog dialog;

	private boolean cancelled = false;
	private boolean allDisabled = false;

	private JPanel outerPanel;
	private JScrollPane scrollPane;
	private JPanel innerPanel;

	private JPanel listPanel;
	private TitledBorder listBorder;
	private JCheckBox[] columns;
	private JCheckBox allControlsSelectedChk, allMPsSelectedChk;

	private JPanel buttonPanel;

	public PopupChecklist(JFrame parent, ChecklistParameters params, boolean fShowDialog) {
		this(parent, params, fShowDialog, false);
	}

	public PopupChecklist(JFrame parent, ChecklistParameters params, boolean fShowDialog, Boolean disabled) {
		super(parent, true);
		this.params = params;
		allDisabled = disabled;

		dialog = new JDialog();
		createControls(dialog);
		dialog.setTitle(params.getDialogTitle());
		dialog.add(Box.createHorizontalStrut(8), BorderLayout.WEST);
		dialog.add(outerPanel, BorderLayout.CENTER);
		dialog.add(Box.createHorizontalStrut(8), BorderLayout.EAST);
		dialog.setModal(true);
		dialog.setSize(500, 500);
		dialog.setLocationRelativeTo(getOwner());
		dialog.setVisible(fShowDialog);

		if (disabled)
			this.selectEverythingAndDisable();
	}

	private void createControls(JDialog dialog) {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

		outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

		scrollPane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		outerPanel.add(scrollPane);

		listPanel = new JPanel();
		listBorder = BorderFactory.createTitledBorder(params.getPanelTitle());
		listBorder.setTitleFont(boldFontForTitlePanel(listBorder, true));
		listBorder.setTitleColor(TITLE_COLOR);
		listPanel.setBorder(listBorder);
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setAlignmentX(CENTER_ALIGNMENT);

		recentlySelected = RecentSelectionUtils.getRecentlySelected(params.getPropsFile());
		final String[] inputList = params.getInputList();
		columns = new JCheckBox[inputList.length];

		allControlsSelectedChk = new JCheckBox("Select All Non-Samples");
		allControlsSelectedChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (allControlsSelectedChk.isSelected())
					allMPsSelectedChk.setSelected(false);

				for (int i = 0; i < inputList.length; i++)
					if (!PostProcessMergeNameExtractor.isSampleName(inputList[i]))
						columns[i].setSelected(allControlsSelectedChk.isSelected());
			}
		});

		allControlsSelectedChk.setAlignmentX(LEFT_ALIGNMENT);
		listPanel.add(allControlsSelectedChk);

		allMPsSelectedChk = new JCheckBox("Select All Master Pools");
		allMPsSelectedChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (allMPsSelectedChk.isSelected())
					allControlsSelectedChk.setSelected(false);

				for (int i = 0; i < inputList.length; i++)
					if (PostProcessMergeNameExtractor.isMasterPool(inputList[i]))
						columns[i].setSelected(allMPsSelectedChk.isSelected());
			}
		});

		allMPsSelectedChk.setAlignmentX(LEFT_ALIGNMENT);
		listPanel.add(allMPsSelectedChk);

		Boolean allSelected = true;
		for (int i = 0; i < inputList.length; i++) {
			columns[i] = new JCheckBox(inputList[i]);
			columns[i].setAlignmentX(LEFT_ALIGNMENT);
			boolean isSelected = allDisabled ? true : params.getPropsValue().equals(recentlySelected.get(inputList[i]));
			if (allDisabled)
				columns[i].setEnabled(false);
			columns[i].setSelected(isSelected);
			allSelected &= isSelected;
			listPanel.add(columns[i]);
		}
		allControlsSelectedChk.setSelected(allSelected);
		if (allDisabled)
			allControlsSelectedChk.setEnabled(false);

		allMPsSelectedChk.setSelected(allSelected);
		if (allDisabled)
			allMPsSelectedChk.setEnabled(false);

		buttonPanel = createButtonPanel();

		innerPanel.add(Box.createVerticalStrut(5));
		innerPanel.add(listPanel);
		innerPanel.add(Box.createVerticalStrut(5));

		outerPanel.add(Box.createVerticalStrut(5));
		outerPanel.add(buttonPanel);
		outerPanel.add(Box.createVerticalStrut(5));
	}

	private JPanel createButtonPanel() {
		JPanel ret = new JPanel();
		ret.add(Box.createHorizontalStrut(10));
		ret.add(makeActionButton("   OK   ", makeOKActionListener(), true));
		ret.add(Box.createHorizontalStrut(25));
		ret.add(makeActionButton("Cancel", makeCancelActionListener(), false));
		ret.add(Box.createHorizontalStrut(10));
		return ret;
	}

	private JButton makeActionButton(String label, ActionListener l, boolean isDefault) {
		JButton button = new JButton(label);
		button.addActionListener(l);
		if (isDefault) {
			getRootPane().setDefaultButton(button);
		}

		return button;
	}

	private ActionListener makeCancelActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCancelled(true);
				dialog.setVisible(false);
			}
		};
	}

	private ActionListener makeOKActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setCancelled(false);
				dialog.setVisible(false);
			}
		};
	}

	public Font boldFontForTitlePanel(TitledBorder border, boolean makeEvenLarger) {
		// see http://bugs.sun.com/view_bug.do?bug_id=7022041 - getTitleFont() can
		// return null - tew 8/14/12
		// A special thanks to zq (signed 'thomas') from gdufs.edu.cn and Dr. Zaho at
		// kiz.ac.cn for spotting
		// the bug and assisting with the fix.
		Font font = border.getTitleFont();
		if (font == null) {
			font = UIManager.getDefaults().getFont("TitledBorder.font");
			if (font == null) {
				font = new Font("SansSerif", Font.BOLD, 12);
			} else {
				font = font.deriveFont(Font.BOLD);
			}
		} else {
			font = font.deriveFont(Font.BOLD);
		}
		Font biggerFont = new Font(font.getName(), font.getStyle(), font.getSize() + (makeEvenLarger ? 3 : 1));
		return biggerFont;
	}

	public List<String> getSelections() {
		if (isCancelled()) {
			return new ArrayList<String>(recentlySelected.stringPropertyNames());
		}
		return makeReturnMapFromData();
	}

	private List<String> makeReturnMapFromData() {
		List<String> ret = new ArrayList<String>();
		String[] inputList = params.getInputList();

		recentlySelected.clear();
		for (int i = 0; i < inputList.length; i++) {
			if (columns[i].isSelected()) {
				ret.add(inputList[i]);
				recentlySelected.put(inputList[i], params.getPropsValue());
			}
		}
		RecentSelectionUtils.setRecentlySelected(recentlySelected, params.getPropsFile());

		return ret;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public void selectEverythingAndDisable() {
		allControlsSelectedChk.setSelected(true);
		allControlsSelectedChk.setEnabled(false);

		allMPsSelectedChk.setSelected(true);
		allMPsSelectedChk.setEnabled(false);

		for (int i = 0; i < columns.length; i++) {
			columns[i].setSelected(true);
			columns[i].setEnabled(false);
		}
		;
	}
}
