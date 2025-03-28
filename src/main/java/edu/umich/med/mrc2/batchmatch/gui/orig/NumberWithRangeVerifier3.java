package edu.umich.med.mrc2.batchmatch.gui.orig;

import java.text.NumberFormat;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.umich.med.mrc2.batchmatch.utils.orig.BinnerNumUtils;

public class NumberWithRangeVerifier3 extends InputVerifier {
	private String lastGood;
	private Double min;
	private Double max;
	private static NumberFormat defaultFormat = NumberFormat.getNumberInstance();

	private final static int DEFAULT_DIGITS = 3;
	private int defaultDigits = -1;

	public NumberWithRangeVerifier3(JTextField textField, Double min, Double max) {
		defaultFormat.setMinimumFractionDigits(0);
		defaultFormat.setMaximumFractionDigits(defaultDigits == -1 ? DEFAULT_DIGITS : getDefaultDigits());
		this.lastGood = textField.getText();
		// System.out.println("NumberWithRangeVerifier: Set last good to " +
		// this.lastGood);
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
			// System.out.println("NumberWithRangeVerifier: Set last good to " +
			// this.lastGood);
			return true;
		} else {
			// System.out.println("Max is " + max + " and min is " + min);
			JOptionPane.showMessageDialog(null, "Please enter a numeric value between " + defaultFormat.format(min)
					+ " and " + defaultFormat.format(max), "Invalid Input", JOptionPane.ERROR_MESSAGE);
			try {
				Double value = Double.parseDouble(lastGood);
				textField.setText(defaultFormat.format(value));
				// System.out.println("Setting text to " + defaultFormat.format(value) + "
				// (parsed as " + value.toString() + ")");
			} catch (Throwable ignore) {
			}
			textField.selectAll();
			return false;
		}
	}

	public boolean verify(JComponent input) {
		JTextField textField = (JTextField) input;
		Double value = BinnerNumUtils.toDouble(textField.getText());
		return (value != null && value >= min && value <= max);
	}

	public String getLastGood() {
		return lastGood;
	}

	public void setLastGood(String lastGood) {
		this.lastGood = lastGood;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double value) {
		this.min = value;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double value) {
		this.max = value;
	}

	private int getDefaultDigits() {
		return defaultDigits;
	}

	public void setDefaultDigits(int digits) {
		defaultDigits = digits;

		defaultFormat.setMinimumFractionDigits(0);
		defaultFormat.setMaximumFractionDigits(digits);
	}
}
