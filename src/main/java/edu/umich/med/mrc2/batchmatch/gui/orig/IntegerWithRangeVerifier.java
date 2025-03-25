package edu.umich.med.mrc2.batchmatch.gui.orig;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class IntegerWithRangeVerifier extends InputVerifier {
	private String lastGood;
	private Integer min;
	private Integer max;

	public IntegerWithRangeVerifier(JTextField textField, Integer min, Integer max) {
		this.lastGood = textField.getText();
		if (min <= max) {
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;
		}
	}

	public boolean shouldYieldFocus(JComponent input) {
		JTextField textField = (JTextField) input;
		if (verify(input)) {
			lastGood = textField.getText();
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Please enter an integer value between " + min + " and " + max,
					"Invalid Input", JOptionPane.ERROR_MESSAGE);
			try {
				Integer value = Integer.parseInt(lastGood);
				textField.setText(value.toString());
			} catch (Throwable ignore) {
			}
			textField.selectAll();
			return false;
		}
	}

	public boolean verify(JComponent input) {
		JTextField textField = (JTextField) input;
		Integer value = null;
		try {
			value = Integer.parseInt(textField.getText());
		} catch (NumberFormatException nfe) {
		}
		return (value != null && value >= min && value <= max);
	}

	public String getLastGood() {
		return lastGood;
	}

	public void setLastGood(String lastGood) {
		this.lastGood = lastGood;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer value) {
		this.min = value;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer value) {
		this.max = value;
	}
}
