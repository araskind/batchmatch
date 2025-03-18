////////////////////////////////////////////////////////
// AbstractStickyFileLocationPanel.java
// Written by Jan Wigginton 
// February 2022
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.main.PostProccessConstants;
import edu.umich.mrc2.batchmatch.utils.PostProcessUtils;
import edu.umich.mrc2.batchmatch.utils.StringUtils;

public abstract class AbstractStickyFileLocationPanel extends StickySettingsPanel {

	private static final long serialVersionUID = 500752319418239107L;

	private TitledBorder fileLocWrapBorder;
	private JComboBox<String> fileLocComboBox;
	private JButton saveBinningButton;
	private File outputDirectory = null;
	private String panelTitle;
	private String panelTag;
	private GridBagLayout gridBagLayout;

	public AbstractStickyFileLocationPanel(String panelTitle) {
		this(panelTitle, PostProccessConstants.CLIENT_RESULTS_DIRECTORY_KEY);
	}

	public AbstractStickyFileLocationPanel(String panelTitle, String panelTag) {
		super();
		this.panelTitle = panelTitle;
		this.panelTag = panelTag;
		setBorder(new CompoundBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 255, 255), new Color(160, 160, 160)),
				panelTitle, TitledBorder.LEADING, TitledBorder.TOP, 
				boldFontForTitlePanel(fileLocWrapBorder, false), 
				BinnerConstants.TITLE_COLOR), 
				new EmptyBorder(10, 10, 10, 10)));
		gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
	}

	public void setupPanel() {
		
		fileLocComboBox = new JComboBox<String>();
		fileLocComboBox.setEditable(false);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 0;
		add(fileLocComboBox, gbc_comboBox);

		String mruOutputDirname = PostProcessUtils.getBinnerProperty(
				BatchMatchConstants.PROPS_FILE, panelTag);

		if (StringUtils.isEmptyOrNull(mruOutputDirname))
			mruOutputDirname = BatchMatchConstants.HOME_DIRECTORY;

		setOutputDirectory(mruOutputDirname);
		saveBinningButton = new JButton("Browse...");
		saveBinningButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showSaveDialog(null);
				outputDirectory = chooser.getSelectedFile();
				setOutputDirectory(outputDirectory);
				updateInterfaceForNewSelection();
			}
		});		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 0;
		add(saveBinningButton, gbc_btnNewButton);
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectoryName) {

		if (StringUtils.isEmptyOrNull(outputDirectoryName))
			return;

		File file = new File(outputDirectoryName);

		if (!file.exists()) {
			JOptionPane.showMessageDialog(null, "The specified output directory (" + outputDirectoryName
					+ ") does not exist. Output will be directed to your home directory.");
			file = new File(BinnerConstants.HOME_DIRECTORY);
		}
		setOutputDirectory(file);
	}

	public String getOutputDirectoryPath() {
		return outputDirectory.getAbsolutePath();
	}

	public void setOutputDirectory(File outputDirectory) {

		this.outputDirectory = outputDirectory;
		if (outputDirectory != null) {
			fileLocComboBox.removeAllItems();
			fileLocComboBox.insertItemAt(outputDirectory.getAbsolutePath(), 0);
			fileLocComboBox.setSelectedIndex(0);
			PostProcessUtils.setBinnerProperty(BatchMatchConstants.PROPS_FILE, panelTag, outputDirectory.getAbsolutePath());
		}
	}

	public void initializeOutputDirectory() {
		if (getOutputDirectory().exists())
			getOutputDirectory().mkdirs();
	}

	public String getPanelTitle() {
		return panelTitle;
	}

	protected abstract void updateInterfaceForNewSelection();
}
