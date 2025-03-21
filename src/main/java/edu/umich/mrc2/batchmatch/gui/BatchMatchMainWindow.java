////////////////////////////////////////////////////////
// AnalysisDialogBatchMatch.java
// Written by Jan Wigginton
// September 2019
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umich.mrc2.batchmatch.data.orig.SharedAnalysisSettings;
import edu.umich.mrc2.batchmatch.gui.panels.tab_panels.BatchMatchAutomationTabPanel;
import edu.umich.mrc2.batchmatch.gui.panels.tab_panels.BatchMatchNamedUnnamedComparisonTabPanel;
import edu.umich.mrc2.batchmatch.gui.panels.tab_panels.BatchMatchNewWorkflowTabPanel;
import edu.umich.mrc2.batchmatch.gui.panels.tab_panels.BatchMatchSummaryTabPanel;
import edu.umich.mrc2.batchmatch.gui.panels.tab_panels.BatchMatchTwoStageTabPanel;
import edu.umich.mrc2.batchmatch.main.ActionCommands;
import edu.umich.mrc2.batchmatch.main.BatchMatch;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;

public class BatchMatchMainWindow extends JFrame implements ActionListener, WindowListener {

	private static final long serialVersionUID = 8821642008453169492L;

	public BatchMatchMainWindow() {
		
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		setTitle("BatchMatch v" + BatchMatchConstants.VERSION);
		setSize(1300, 900);
		setLocationRelativeTo(null);
		addWindowListener(this);
		setJMenuBar(new BatchMatchMenuBar(this));
		
		setLayout(new BorderLayout(0, 0));

		SharedAnalysisSettings sharedAnalysisSettings = new SharedAnalysisSettings();
		
		JTabbedPane workflowTabPane = new JTabbedPane(SwingConstants.TOP);
		BatchMatchNewWorkflowTabPanel batchNewWorkflowPanel = 
				new BatchMatchNewWorkflowTabPanel(sharedAnalysisSettings);
		batchNewWorkflowPanel.setupPanel(sharedAnalysisSettings);

		BatchMatchSummaryTabPanel batchSummaryTabWrapPanel = 
				new BatchMatchSummaryTabPanel(sharedAnalysisSettings);
		batchSummaryTabWrapPanel.setupPanel();

		BatchMatchAutomationTabPanel convertAndMergeTabWrapPanel = 
				new BatchMatchAutomationTabPanel(sharedAnalysisSettings);
		convertAndMergeTabWrapPanel.setupPanel();

		BatchMatchNamedUnnamedComparisonTabPanel namedUnnamedComparisonTabPanel = 
				new BatchMatchNamedUnnamedComparisonTabPanel(sharedAnalysisSettings);
		namedUnnamedComparisonTabPanel.setupPanel(sharedAnalysisSettings);

		BatchMatchTwoStageTabPanel twoStageAnalysisTabPanel = 
				new BatchMatchTwoStageTabPanel(sharedAnalysisSettings);
		twoStageAnalysisTabPanel.setupPanel();

		workflowTabPane.addTab("Workflow Setup ", null, batchNewWorkflowPanel, "Workflow Setup");
		workflowTabPane.addTab("Run Merge ", null, convertAndMergeTabWrapPanel, "Run Merge ");
		workflowTabPane.addTab("Reports/Diagnostics ", null, batchSummaryTabWrapPanel, "Data Reports");

		add(new JScrollPane(workflowTabPane), BorderLayout.CENTER);
		
		JPanel tabSwitchPanel = new JPanel();
		tabSwitchPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
		add(tabSwitchPanel, BorderLayout.SOUTH);
		tabSwitchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton prevButton = new JButton("<< Previous");
		tabSwitchPanel.add(prevButton);
		
		JButton nextButton = new JButton("Next >>");
		tabSwitchPanel.add(nextButton);
		
		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				workflowTabPane.setSelectedIndex(workflowTabPane.getSelectedIndex() - 1);
			}
		});
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				workflowTabPane.setSelectedIndex(workflowTabPane.getSelectedIndex() + 1);
			}
		});	
		workflowTabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				if (workflowTabPane.getSelectedIndex() == 0) {
					prevButton.setEnabled(false);
					nextButton.setEnabled(true);
				} else if (workflowTabPane.getSelectedIndex() == workflowTabPane.getTabCount() - 1) {
					prevButton.setEnabled(true);
					nextButton.setEnabled(false);
				} else {
					prevButton.setEnabled(true);
					nextButton.setEnabled(true);
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		if(command.equals(ActionCommands.SHOW_ABOUT_DIALOG_COMMAND.getName()))
			showAboutDialog();
	}
	
	private void showAboutDialog() {
		AboutDialog ad = new AboutDialog();
		ad.setLocationRelativeTo(this);
		ad.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		BatchMatch.shutDown();;		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
