package edu.umich.mrc2.batchmatch.utils;

import edu.umich.mrc2.batchmatch.data.AnnotationInfo;

public class AnnotationUtils {
	public static Double getReferenceMass(Double featureMass, AnnotationInfo adduct, Integer massMultiple) {
		Double adductMass = adduct.getMass();
		Integer adductCharge = getAdductCharge(adduct);
		if (adductCharge == null) {
			return null;
		}

		return (Math.abs(adductCharge) * featureMass - adductMass) / (massMultiple + 0.0);
	}

	public static Integer getAdductCharge(AnnotationInfo adduct) {
		Integer adductCharge = null;
		try {
			adductCharge = Integer.parseInt(adduct.getCharge());
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}

		return adductCharge;
	}
}
