////////////////////////////////////////////////////////////
// AnchorLoaderPanel.java
// Written by Jan Wigginton and Bill Duren November 2019
///////////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class AboutDialog extends JDialog {
	
	public AboutDialog() {
		
		setTitle("About BatchMatch");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 300);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		add(Box.createRigidArea(new Dimension(0, 10)));

		Font newFont = new Font("Arial", Font.PLAIN, 18);
		
		JLabel name = new JLabel("BatchMatch v.0.1a");
		//	JLabel handsoff = new JLabel("Created by Jan Wigginton, Bill Duren and Maureen Kachman");
		JLabel handsoff = new JLabel("Work in progress ...");
		JLabel handsoff2 = new JLabel("2019 - 2025 ...");

		name.setFont(newFont);
		handsoff.setFont(newFont);
		handsoff2.setFont(newFont);
		
		name.setAlignmentX(0.5f);
		handsoff.setAlignmentX(0.5f);
		handsoff2.setAlignmentX(0.5f);

		add(new JLabel(" "));
		add(new JLabel(" "));
		add(Box.createRigidArea(new Dimension(0, 25)));
		add(name);
		add(new JLabel(" "));
		add(handsoff);
		add(new JLabel(" "));
		add(handsoff2);

		add(new JLabel(" "));

		add(Box.createRigidArea(new Dimension(0, 50)));

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		close.setAlignmentX(0.5f);
		add(close);

	}
}
