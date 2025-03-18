////////////////////////////////////////////////////
//ChargeCarrierPreferencePanel.java
//Written by Jan Wigginton, May 2018
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.gui.panels.orig;

//import edu.umich.wld.ChargeCarrierPreferences;

//MERGE 05/08 -- New class
public class ChargeCarrierPreferencePanel { // extends BinnerPanel implements ItemListener
}
/*
 * private JPanel chargeCarrierPreferencesPanel,
 * chargeCarrierPreferencesWrapPanel; private TitledBorder
 * chargeCarrierPreferencesBorder; private JCheckBox allowAsBaseCheckBox,
 * allowAsMultimerBaseCheckBox; private JCheckBox
 * requireAloneBeforeCombinedCheckBox; private ChargeCarrierPreferences
 * preferences;
 * 
 * 
 * public ChargeCarrierPreferencePanel(ChargeCarrierPreferences preferences) {
 * this.preferences = preferences; setupPanel(); }
 * 
 * public void setupPanel() { allowAsBaseCheckBox = new
 * JCheckBox("Allow As Base    ");
 * allowAsBaseCheckBox.setSelected(preferences.getAllowAsBase());
 * allowAsBaseCheckBox.setName("ALLOWBASE");
 * allowAsBaseCheckBox.setEnabled(false);
 * allowAsBaseCheckBox.addItemListener(this);
 * 
 * allowAsBaseCheckBox.addActionListener(new ActionListener() { public void
 * actionPerformed(ActionEvent ae) { if (!allowAsBaseCheckBox.isSelected())
 * allowAsMultimerBaseCheckBox.setSelected(false);
 * allowAsMultimerBaseCheckBox.setEnabled(allowAsBaseCheckBox.isSelected()); }
 * });
 * 
 * allowAsMultimerBaseCheckBox = new JCheckBox("Allow As Multimer Base    ");
 * allowAsMultimerBaseCheckBox.setSelected(preferences.getAllowAsMultimerBase())
 * ; allowAsMultimerBaseCheckBox.setName("ALLOWMULTIMERBASE");
 * allowAsMultimerBaseCheckBox.setEnabled(false);
 * allowAsMultimerBaseCheckBox.addItemListener(this);
 * 
 * requireAloneBeforeCombinedCheckBox = new
 * JCheckBox("Require Alone Before Combining With Neutral Masses    ");
 * requireAloneBeforeCombinedCheckBox.setSelected(preferences.
 * getRequireAloneBeforeCombined());
 * requireAloneBeforeCombinedCheckBox.setName("REQUIREALONE");
 * requireAloneBeforeCombinedCheckBox.setEnabled(false);
 * requireAloneBeforeCombinedCheckBox.addItemListener(this);
 * 
 * chargeCarrierPreferencesPanel = new JPanel();
 * chargeCarrierPreferencesPanel.setLayout(new
 * BoxLayout(chargeCarrierPreferencesPanel, BoxLayout.Y_AXIS));
 * chargeCarrierPreferencesPanel.add(Box.createVerticalGlue());
 * chargeCarrierPreferencesPanel.add(allowAsBaseCheckBox);
 * chargeCarrierPreferencesPanel.add(Box.createVerticalStrut(4));
 * chargeCarrierPreferencesPanel.add(allowAsMultimerBaseCheckBox);
 * chargeCarrierPreferencesPanel.add(Box.createVerticalStrut(4));
 * chargeCarrierPreferencesPanel.add(requireAloneBeforeCombinedCheckBox);
 * chargeCarrierPreferencesPanel.add(Box.createVerticalGlue());
 * 
 * chargeCarrierPreferencesWrapPanel = new JPanel();
 * chargeCarrierPreferencesWrapPanel.setLayout(new
 * BoxLayout(chargeCarrierPreferencesWrapPanel, BoxLayout.Y_AXIS));
 * chargeCarrierPreferencesBorder =
 * BorderFactory.createTitledBorder("Options for Charge Carrier " +
 * preferences.getGroupName() + " (Charge = " + preferences.getCharge() + ")");
 * chargeCarrierPreferencesBorder.setTitleFont(boldFontForTitlePanel(
 * chargeCarrierPreferencesBorder, false));
 * chargeCarrierPreferencesWrapPanel.setBorder(chargeCarrierPreferencesBorder);
 * chargeCarrierPreferencesWrapPanel.add(chargeCarrierPreferencesPanel);
 * chargeCarrierPreferencesWrapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
 * chargeCarrierPreferencesWrapPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
 * 
 * setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
 * add(Box.createVerticalStrut(2)); add(chargeCarrierPreferencesWrapPanel);
 * add(Box.createVerticalStrut(2)); }
 * 
 * 
 * @Override public void itemStateChanged(ItemEvent e) {
 * preferences.setAllowAsBase(allowAsBaseCheckBox.isSelected());
 * preferences.setAllowAsMultimerBase(allowAsMultimerBaseCheckBox.isSelected());
 * preferences.setRequireAloneBeforeCombined(requireAloneBeforeCombinedCheckBox.
 * isSelected()); }
 * 
 * public ChargeCarrierPreferences getPreferences() { return preferences; } }
 * 
 */