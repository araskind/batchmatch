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

package edu.umich.med.mrc2.batchmatch.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;

public class MSUtils {

	public static final double NEUTRON_MASS = 1.003354838;
	public static final double CARBON_MASS = 12.0;
	public static final double CARBON_13_NATURAL_ABUNDANCE = 1.07;
	public static final double HYDROGEN_MASS = 1.007825032;
	public static final double PROTON_MASS = 1.007276;
	public static final double ELECTRON_MASS = 5.4857990943E-4;
	public static final double MIN_ISOTOPE_ABUNDANCE = 0.001;
	
	public static final NumberFormat spectrumMzFormat = new DecimalFormat("#.####");
	public static final NumberFormat spectrumMzExportFormat = new DecimalFormat("#.######");
	public static final NumberFormat spectrumIntensityFormat = new DecimalFormat("#.##");
	
	public static Range createPpmMassRange(double mz, double accuracyPpm) {

		double b1 = mz * (1 - accuracyPpm / 1000000);
		double b2 = mz * (1 + accuracyPpm / 1000000);
		Range mzRange = new Range(Math.min(b1, b2), Math.max(b1, b2));
		return mzRange;
	}

	public static Range createMassRange(
			double mz, double accuracy, MassErrorType errorType) {

		double b1 = mz;
		double b2 = mz;

		if(errorType.equals(MassErrorType.ppm)) {
			b1 = mz * (1 - accuracy / 1000000.0d);
			b2 = mz * (1 + accuracy / 1000000.0d);
		}
		if(errorType.equals(MassErrorType.mDa)) {
			b1 = mz - accuracy / 1000.0d;
			b2 = mz + accuracy / 1000.0d;
		}
		if(errorType.equals(MassErrorType.Da)) {
			b1 = mz - accuracy;
			b2 = mz + accuracy;
		}
		return new Range(Math.min(b1, b2), Math.max(b1, b2));
	}

	public static Range createMassRangeWithReference(
			double keyMass, double refMass, double massAccuracy) {

		double diff = refMass * massAccuracy / 1000000;
		Range mzRange = new Range(keyMass - diff, keyMass + diff);
		return mzRange;
	}
}
