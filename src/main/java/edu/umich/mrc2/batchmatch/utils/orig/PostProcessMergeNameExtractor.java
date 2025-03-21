////////////////////////////////////////////////////////
// PostProcessMergeNameExtractor.java
// Written by Jan Wigginton and Bill Duren
// September 2019
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.utils.orig;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import edu.umich.mrc2.batchmatch.main.PostProccessConstants;

public class PostProcessMergeNameExtractor {

	public static Boolean isSampleName(String fileName) {

		if (fileName.length() < 9)
			return false;

		if (fileName.length() == 9)
			return FormatVerifier.verifyFormat(PostProccessConstants.SAMPLE_ID_FORMAT, fileName);

		// int lastDash = fileName.lastIndexOf('-');
		// if (lastDash < 9)
		// return false;

		int lastDash = fileName.length() - 1;

		while (lastDash > 9) {
			String candidate = fileName.substring(lastDash - 9, lastDash);
			if (FormatVerifier.verifyFormat(PostProccessConstants.SAMPLE_ID_FORMAT, candidate))
				return true;
			lastDash--;
		}
		return false;
	}

	public static Boolean isMasterPool(String fileName) {
		return fileName.contains("CS00000MP");
	}

	/*
	 * 
	 * public static representsMasterPool(String fileName) {
	 * 
	 * if (StringUtils.isEmptyOrNull(fileName)) return false;
	 * 
	 * if (!fileName.contains("CS00000MP")) return false;
	 * 
	 * // Must have at least -\d at end if (fileName.length() < 11) return false;
	 * 
	 * //CS000BPM2-2 if (fileName.length() == 11) return
	 * FormatVerifier.verifyFormat(PostProccessConstants.MASTER_POOL_ID_FORMAT,
	 * fileName);
	 * 
	 * //CS000BPM2-11 if (fileName.length() == 12 &&
	 * FormatVerifier.verifyFormat(PostProccessConstants.MASTER_POOL_ID_FORMAT,
	 * fileName)) return true;
	 * 
	 * int lastDash = fileName.lastIndexOf('-'); if (lastDash < 9) return false;
	 * 
	 * ///CS00000MP-1 or CS00000MP-11 String candidate1 =
	 * fileName.substring(lastDash-9, lastDash); if
	 * (FormatVerifier.verifyFormat(PostProccessConstants.MASTER_POOL_ID_FORMAT,
	 * candidate1)) return true;
	 * 
	 * 
	 * 
	 * 
	 * //CS000BPM2-2-N -- > CS000BPM1-2 if (candidate.length() == 11) return
	 * FormatVerifier.verifyFormat(PostProccessConstants.MASTER_POOL_ID_FORMAT,
	 * candidate);
	 * 
	 * // CS000BPM2-22-N -- > CS000BPM1-22 if (candidate.length() == 12 &&
	 * FormatVerifier.verifyFormat(PostProccessConstants.MASTER_POOL_ID_FORMAT,
	 * candidate)) return true;
	 * 
	 * String candidateTrim = candidate.substring(lastIndex-11, lastIndex)
	 * //ABCDAGASW-11 lastDash = candidate.lastIndexOf('-'); if (lastDash < 11)
	 * return false;
	 * 
	 * 
	 * String candidateTrim = candidate.substring(lastDash-11, lastDash); if
	 * (FormatVerifier.verifyFormat(PostProccessConstants.MASTER_POOL_ID_FORMAT,
	 * candidateTrim)) return true;
	 * 
	 * 
	 * 
	 * 
	 * String finalCandidate = candidateTrim(lastDash - )
	 * 
	 * 
	 * 
	 * 
	 * }
	 */
	public static List<String> determineNonSampleCols(List<String> intensityHeaders) {

		List<String> nonSampleCols = new ArrayList<String>();
		for (int i = 0; i < intensityHeaders.size(); i++) {
			if (StringUtils.isEmptyOrNull(intensityHeaders.get(i)))
				continue;
			Boolean isSample = isSampleName(intensityHeaders.get(i));
			if (!isSample)
				nonSampleCols.add(intensityHeaders.get(i));
		}
		return nonSampleCols;
	}

	public static String extractMergeName(String fileName) {

		fileName = fileName.trim();
		if (FormatVerifier.verifyFormat(PostProccessConstants.SAMPLE_ID_FORMAT, fileName))
			return fileName;

		int lastDash = fileName.lastIndexOf('-');

		String mergeName = fileName; // .substring(lastDash - 9, lastDash);

		if (lastDash >= 9 && fileName.length() >= 9 && FormatVerifier
				.verifyFormat(PostProccessConstants.SAMPLE_ID_FORMAT, fileName.substring(lastDash - 9, lastDash))) {
			mergeName = fileName.substring(lastDash - 9, lastDash);
			// System.out.println("Sample id is " + Double.NaN);

		}
		// < 3
		else {
			int firstDash = fileName.indexOf('-');
			Boolean haveLeadingDate = false;
			try {
				DateUtils.parseDate(fileName.substring(0, firstDash), new String[] { "yyyyMMdd" });
				haveLeadingDate = true;
			} catch (Exception e) {
			}

			if (haveLeadingDate) {
				String dateStr = fileName.substring(0, firstDash);
				String rest = fileName.substring(firstDash, lastDash);
				if (rest.indexOf('-') == rest.lastIndexOf('-'))
					return fileName;

				String controlId1 = rest.substring(0, rest.lastIndexOf('-'));
				String suffix = rest.substring(rest.lastIndexOf('-'));

				String controlId = controlId1.substring(controlId1.lastIndexOf('-'));
				Boolean haveControlId = (!StringUtils.isEmptyOrNull(controlId));
				mergeName = haveControlId ? dateStr + controlId + suffix : fileName;
			}

			lastDash = mergeName.lastIndexOf('-');

			// System.out.println("Merge name is " + mergeName);
			if (lastDash > 0 && mergeName.length() > 2
					&& ("-N".equals(mergeName.substring(lastDash)) || "-P".equals(mergeName.substring(lastDash)))) {
				mergeName = mergeName.substring(0, lastDash);
			}
		}
		return mergeName;
	}
}
