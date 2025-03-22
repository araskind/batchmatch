package edu.umich.med.mrc2.batchmatch.utils.orig;

import edu.umich.med.mrc2.batchmatch.data.orig.AnnotationInfo;

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
