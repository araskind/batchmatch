/*******************************************************************************
 *
 * (C) Copyright 2018-2020 MRC2 (http://mrc2.umich.edu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Alexander Raskind (araskind@med.umich.edu)
 *
 ******************************************************************************/

package edu.umich.med.mrc2.batchmatch.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchParametersContainer;
import edu.umich.med.mrc2.batchmatch.project.BatchMatchProject;

public class AlignmentSettingsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JFormattedTextField massToleranceField;
	private JFormattedTextField rtToleranceField;
	private JComboBox<MassErrorType> massErrorTypeComboBox;
	private JFormattedTextField annealingStretchFactorField;
	private JFormattedTextField maxSDfromCurveField;
	private JFormattedTextField upperDeltaRTexclusionLimitField;
	private JFormattedTextField lowerDeltaRTexclusionLimitField;
	private JFormattedTextField minSeparationField;
	private JSpinner latticeSizeSpinner;
	
	public AlignmentSettingsPanel() {
		super();
		setBorder(new CompoundBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 255, 255), new Color(160, 160, 160)),
				"Batch alignment parameters", TitledBorder.LEADING, TitledBorder.TOP, 
				BatchMatchConfiguration.panelTitleFont, BatchMatchConfiguration.panelTitleColor), 
				new EmptyBorder(10, 10, 10, 10)));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new CompoundBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), 
						new Color(160, 160, 160)), "Feature alignment parameters", 
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), 
				new EmptyBorder(10, 10, 10, 10)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Mass tolerance");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		massToleranceField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultPpmFormat);
		GridBagConstraints gbc_massToleranceField = new GridBagConstraints();
		gbc_massToleranceField.insets = new Insets(0, 0, 5, 5);
		gbc_massToleranceField.gridx = 1;
		gbc_massToleranceField.gridy = 0;
		panel.add(massToleranceField, gbc_massToleranceField);
		massToleranceField.setColumns(10);
		massToleranceField.setPreferredSize(new Dimension(80, 20));
		
		massErrorTypeComboBox = new JComboBox<MassErrorType>(
				new DefaultComboBoxModel<MassErrorType>(MassErrorType.values()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 0;
		panel.add(massErrorTypeComboBox, gbc_comboBox);
		massErrorTypeComboBox.setMinimumSize(new Dimension(70, 22));
		massErrorTypeComboBox.setPreferredSize(new Dimension(80, 22));
		
		JLabel lblNewLabel_1 = new JLabel("RT tolerance");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		rtToleranceField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultRtFormat);
		GridBagConstraints gbc_rtToleranceField = new GridBagConstraints();
		gbc_rtToleranceField.insets = new Insets(0, 0, 5, 5);
		gbc_rtToleranceField.gridx = 1;
		gbc_rtToleranceField.gridy = 1;
		panel.add(rtToleranceField, gbc_rtToleranceField);
		rtToleranceField.setColumns(10);
		rtToleranceField.setPreferredSize(new Dimension(80, 20));
		
		JLabel lblNewLabel_2 = new JLabel("min");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 2;
		gbc_lblNewLabel_2.gridy = 1;
		panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Annealing stretch factor");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 2;
		panel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		annealingStretchFactorField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultPpmFormat);
		annealingStretchFactorField.setColumns(10);
		annealingStretchFactorField.setPreferredSize(new Dimension(80, 20));
		GridBagConstraints gbc_formattedTextField = new GridBagConstraints();
		gbc_formattedTextField.insets = new Insets(0, 0, 5, 5);
		gbc_formattedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField.gridx = 1;
		gbc_formattedTextField.gridy = 2;
		panel.add(annealingStretchFactorField, gbc_formattedTextField);
		
		JLabel lblNewLabel_9 = new JLabel("Lattice size");
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_9.gridx = 0;
		gbc_lblNewLabel_9.gridy = 3;
		panel.add(lblNewLabel_9, gbc_lblNewLabel_9);
		
		latticeSizeSpinner = new JSpinner();
		latticeSizeSpinner.setModel(
				new SpinnerNumberModel(Integer.valueOf(30), 
						Integer.valueOf(10), null, Integer.valueOf(1)));
		latticeSizeSpinner.setPreferredSize(new Dimension(80, 20));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 0, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 3;
		panel.add(latticeSizeSpinner, gbc_spinner);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new CompoundBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), 
						new Color(160, 160, 160)), "Outlier filtering parameters", 
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), 
				new EmptyBorder(10, 10, 10, 10)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblNewLabel_4 = new JLabel("Maximum SD From Curve");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 0;
		panel_1.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		maxSDfromCurveField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultPpmFormat);
		maxSDfromCurveField.setMinimumSize(new Dimension(80, 20));
		maxSDfromCurveField.setPreferredSize(new Dimension(80, 20));
		maxSDfromCurveField.setColumns(10);
		GridBagConstraints gbc_formattedTextField_1 = new GridBagConstraints();
		gbc_formattedTextField_1.insets = new Insets(0, 0, 5, 5);
		gbc_formattedTextField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_1.gridx = 1;
		gbc_formattedTextField_1.gridy = 0;
		panel_1.add(maxSDfromCurveField, gbc_formattedTextField_1);
		
		JLabel lblNewLabel_5 = new JLabel("Minimum Separation");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 1;
		panel_1.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		minSeparationField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultPpmFormat);
		minSeparationField.setPreferredSize(new Dimension(80, 20));
		minSeparationField.setColumns(10);
		GridBagConstraints gbc_formattedTextField_2 = new GridBagConstraints();
		gbc_formattedTextField_2.insets = new Insets(0, 0, 5, 5);
		gbc_formattedTextField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_2.gridx = 1;
		gbc_formattedTextField_2.gridy = 1;
		panel_1.add(minSeparationField, gbc_formattedTextField_2);
		
		JLabel lblNewLabel_6 = new JLabel("Remove RT Pairs if  ΔRT > ");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 2;
		panel_1.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		upperDeltaRTexclusionLimitField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultRtFormat);
		upperDeltaRTexclusionLimitField.setPreferredSize(new Dimension(80, 20));
		upperDeltaRTexclusionLimitField.setColumns(10);
		GridBagConstraints gbc_formattedTextField_3 = new GridBagConstraints();
		gbc_formattedTextField_3.insets = new Insets(0, 0, 0, 5);
		gbc_formattedTextField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_3.gridx = 1;
		gbc_formattedTextField_3.gridy = 2;
		panel_1.add(upperDeltaRTexclusionLimitField, gbc_formattedTextField_3);
		
		JLabel lblNewLabel_7 = new JLabel(" or <= ");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_7.gridx = 2;
		gbc_lblNewLabel_7.gridy = 2;
		panel_1.add(lblNewLabel_7, gbc_lblNewLabel_7);
		
		lowerDeltaRTexclusionLimitField = 
				new JFormattedTextField(BatchMatchConfiguration.defaultRtFormat);
		lowerDeltaRTexclusionLimitField.setMinimumSize(new Dimension(80, 20));
		lowerDeltaRTexclusionLimitField.setPreferredSize(new Dimension(80, 20));
		lowerDeltaRTexclusionLimitField.setColumns(10);
		GridBagConstraints gbc_formattedTextField_4 = new GridBagConstraints();
		gbc_formattedTextField_4.insets = new Insets(0, 0, 0, 5);
		gbc_formattedTextField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_4.gridx = 3;
		gbc_formattedTextField_4.gridy = 2;
		panel_1.add(lowerDeltaRTexclusionLimitField, gbc_formattedTextField_4);
		
		JLabel lblNewLabel_8 = new JLabel("min");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_8.gridx = 4;
		gbc_lblNewLabel_8.gridy = 2;
		panel_1.add(lblNewLabel_8, gbc_lblNewLabel_8);
		
		loadDefaultSettings();
	}

	private void loadDefaultSettings() {		
		loadSettings(new BatchMatchParametersContainer());
	}
	
	public void loadSettings(BatchMatchParametersContainer settings) {
		
		massToleranceField.setText(Double.toString(settings.getMassTolerance()));		
		massErrorTypeComboBox.setSelectedItem(settings.getMassErrorType());
		rtToleranceField.setText(Double.toString(settings.getRtTolerance()));
		annealingStretchFactorField.setText(Double.toString(settings.getAnnealingStretchFactor()));
		maxSDfromCurveField.setText(Double.toString(settings.getMaxSDfromCurve()));
		minSeparationField.setText(Double.toString(settings.getMinSeparation()));
		upperDeltaRTexclusionLimitField.setText(Double.toString(settings.getUpperDeltaRTexclusionLimit()));
		lowerDeltaRTexclusionLimitField.setText(Double.toString(settings.getLowerDeltaRTexclusionLimit()));		
		latticeSizeSpinner.setValue(settings.getDefaultLatticeSize());
	}
	
	public void loadSettingsFromProject(BatchMatchProject project) {
		loadSettings(project.getAlignmentSettings());
	}
	
	public double getMassTolerance(){
		return Double.parseDouble(massToleranceField.getText().trim());
	}

	public MassErrorType getMassErrorType(){
		return (MassErrorType)massErrorTypeComboBox.getSelectedItem();
	}

	public double getRtTolerance(){
		return Double.parseDouble(rtToleranceField.getText().trim());
	}

	public double getAnnealingStretchFactor(){
		return Double.parseDouble(annealingStretchFactorField.getText().trim());
	}

	public double getMaxSDfromCurve(){
		return Double.parseDouble(maxSDfromCurveField.getText().trim());
	}

	public double getMinSeparation(){
		return Double.parseDouble(minSeparationField.getText().trim());
	}

	public double getUpperDeltaRTexclusionLimit(){
		return Double.parseDouble(upperDeltaRTexclusionLimitField.getText().trim());
	}

	public double getLowerDeltaRTexclusionLimit(){
		return Double.parseDouble(lowerDeltaRTexclusionLimitField.getText().trim());
	}
	
	public int getDefaultLatticeSize() {
		return (int)latticeSizeSpinner.getValue();
	}
	
	public Collection<String>validateProjectSetup(){
	    
	    Collection<String>errors = new ArrayList<String>();
	    
	    if(getMassTolerance() <= 0)
	        errors.add("Mass tolerance must be > 0.");
	    
	    if(getRtTolerance() <= 0)
	        errors.add("RT tolerance must be > 0.");
	    
	    if(getMaxSDfromCurve() <= 0)
	        errors.add("Annealing Stretch Factor must be > 0.");
	    
	    if(getRtTolerance() <= 0)
	        errors.add("Max SD From Curve must be > 0.");
	    
	    if(getMinSeparation() <= 0)
	        errors.add("Min. Separation must be > 0.");
	    
	    if(getUpperDeltaRTexclusionLimit() < getLowerDeltaRTexclusionLimit())
	        errors.add("For RT pair exclusion delta-RT upper limit must be larger than lower one.");
	
	    return errors;
	}
}
















