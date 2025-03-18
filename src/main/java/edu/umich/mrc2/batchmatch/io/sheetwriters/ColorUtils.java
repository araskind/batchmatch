////////////////////////////////////////////////////
// ColorUtils.java
// Written by Jan Wigginton, Jan 25, 2017
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.awt.Color;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class ColorUtils {
	public static double[] ratios = new double[] { 0, 0.016666667, 0.033333333, 0.05, 0.066666667, 0.083333333, 0.1,
			0.116666667, 0.133333333, 0.15, 0.166666667, 0.183333333, 0.2, 0.216666667, 0.233333333, 0.25, 0.266666667,
			0.283333333, 0.3, 0.316666667, 0.333333333, 0.35, 0.366666667, 0.383333333, 0.4, 0.416666667, 0.433333333,
			0.45, 0.466666667, 0.483333333, 0.5, 0.516666667, 0.533333333, 0.55, 0.566666667, 0.583333333, 0.6,
			0.616666667, 0.633333333, 0.65, 0.666666667, 0.683333333, 0.7, 0.716666667, 0.733333333, 0.75, 0.766666667,
			0.783333333, 0.8, 0.816666667, 0.833333333, 0.85, 0.866666667, 0.883333333, 0.9, 0.916666667, 0.933333333,
			0.95, 0.966666667, 0.983333333, 1, 1.016666667, 1.033333333, 1.05, 1.066666667, 1.083333333, 1.1,
			1.116666667, 1.133333333, 1.15, 1.166666667, 1.183333333, 1.2, 1.216666667, 1.233333333, 1.25, 1.266666667,
			1.283333333, 1.3, 1.316666667, 1.333333333, 1.35, 1.366666667, 1.383333333, 1.4, 1.416666667, 1.433333333,
			1.45, 1.466666667, 1.483333333, 1.5, 1.516666667, 1.533333333, 1.55, 1.566666667, 1.583333333, 1.6,
			1.616666667, 1.633333333, 1.65, 1.666666667, 1.683333333, 1.7, 1.716666667, 1.733333333, 1.75, 1.766666667,
			1.783333333, 1.8, 1.816666667, 1.833333333, 1.85, 1.866666667 };

	public static XSSFColor HSVtoRGB(Integer hue, Double saturation, Double vibrance) throws Exception {
		if (hue < 0 || hue > 360)
			throw new Exception("Hue value must be between 0 and 360 inclusive. Current value is " + hue);

		if (saturation < 0.0 || saturation > 1.0)
			throw new Exception("Hue value must be between 0 and 1.0 inclusive. Current value is " + saturation);

		if (vibrance < 0.0 || vibrance > 1.0)
			throw new Exception("Hue value must be between 0 and 1.0 inclusive. Current value is " + vibrance);

		Double C = vibrance * saturation;
		Double hueRatio = hue / 60.0;
		Double hueModf = hueRatio % 2;
		Double hueAbsDiff = Math.abs(hueModf - 1);
		Double X = C * (1 - hueAbsDiff);

		Integer hueRange = hueRatio.intValue() + 1;

		Double rPrime, gPrime, bPrime;
		switch (hueRange) {
		case 1:
			rPrime = C;
			gPrime = X;
			bPrime = 0.0;
			break;
		case 2:
			rPrime = X;
			gPrime = C;
			bPrime = 0.0;
			break;
		case 3:
			rPrime = 0.0;
			gPrime = C;
			bPrime = X;
			break;
		case 4:
			rPrime = 0.0;
			gPrime = X;
			bPrime = C;
			break;
		case 5:
			rPrime = X;
			gPrime = 0.0;
			bPrime = C;
			break;
		case 6:
			rPrime = C;
			gPrime = 0.0;
			bPrime = X;
			break;
		default:
			throw new Exception("Hue range error");
		}

		Double m = vibrance * (1.0 - saturation);

		rPrime += m;
		gPrime += m;
		bPrime += m;

		rPrime *= 255;
		gPrime *= 255;
		bPrime *= 255;

		return grabRGBColor(rPrime.intValue(), gPrime.intValue(), bPrime.intValue());
	}

	public static XSSFColor HSVtoRGBSimple(Integer hue, Double saturation, Double vibrance) {
		Double hueRatio = ratios[hue];
		Double hueModf = hueRatio; // 1.2; //hueRatio;
		Double hueAbsDiff = Math.abs(hueModf - 1);
		Double X = (1 - hueAbsDiff);

		Integer hueRange = hueRatio.intValue() + 1;

		Double rPrime, gPrime;
		switch (hueRange) {
		case 1:
			rPrime = 1.0;
			gPrime = X;
			break;
		case 2:
			rPrime = X;
			gPrime = 1.0;
			break;
		case 3:
			rPrime = 0.0;
			gPrime = 1.0;
			break;
		case 4:
			rPrime = 0.0;
			gPrime = X;
			break;
		case 5:
			rPrime = X;
			gPrime = 0.0;
			break;
		case 6:
			rPrime = 1.0;
			gPrime = 0.0;
			break;
		default:
			rPrime = 255.0;
			gPrime = 255.0;
			break;
		}

		rPrime *= 255;
		gPrime *= 255;

		return grabRGBColor(rPrime.intValue(), gPrime.intValue(), 0);
	}

	public static XSSFColor grabFromHtml(String hexColor) {
		Color color = Color.decode(hexColor);

		return grabRGBColor(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static XSSFColor grabRGBColor(int red, int green, int blue) {
		byte[] rgb = new byte[3];
		rgb[0] = (byte) red; // red
		rgb[1] = (byte) green; // green
		rgb[2] = (byte) blue; // blue
		return new XSSFColor(rgb); //
	}
}
