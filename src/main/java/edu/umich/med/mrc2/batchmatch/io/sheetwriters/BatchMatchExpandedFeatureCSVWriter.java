////////////////////////////////////////////////////
// BatchMatchExpandedFeatureCSVWriter.java
// Written by Jan Wigginton, August 2020
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.FeatureByRedGrpMassAndRtComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class BatchMatchExpandedFeatureCSVWriter extends CSVWriter {

	private Map<String, String> derivedColNameMapping;
	private Map<String, Double> featureNameToOldRtMap;
	private String sheetName = null;

	public BatchMatchExpandedFeatureCSVWriter(Map<String, String> derivedColNameMapping) {
		this.derivedColNameMapping = derivedColNameMapping;
	}

	public void writeExpandedFeatureSheet(File outputFile, PostProcessDataSet data) {

		Collections.sort(data.getFeatures(), new FeatureByRedGrpMassAndRtComparator());
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
			
			createCSVHeader(bos, data.getOrderedNonStandardHeaders(), data.getOrderedIntensityHeaders()); // ,
			createCSVFeatureList(bos, data.getFeatures(), data.getOrderedIntensityHeaders());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void createCSVFeatureList(
			BufferedOutputStream bos, 
			List<FeatureFromFile> features,
			List<String> intensityHeaders) throws IOException {

		String formatStringNumeric = "%.6f";
		String formatStringNumericShorter = "%.4f";
		String formatStringInteger = "%d";

		for (int j = 0; j < features.size(); j++) {

			FeatureFromFile feature = features.get(j);
			FeatureFromFile nextFeature = j < features.size() - 1 ? features.get(j + 1) : null;

			if (feature.getRedundancyGroup() != null)
				feature.setFlaggedAsDuplicate(true);

			StringBuilder sb = new StringBuilder();
			writeFeatureCSVEntry(sb, feature, nextFeature, formatStringNumeric, formatStringNumericShorter,
					formatStringInteger);
			createIntensityCSVSection(sb, feature, intensityHeaders, formatStringNumeric, formatStringNumericShorter,
					formatStringInteger);

			bos.write((sb.toString() + BatchMatchConstants.LINE_SEPARATOR).getBytes());
		}
	}

	private void createCSVHeader(
			BufferedOutputStream bos, 
			List<String> nonStandardHeadersRead,
			List<String> intensityHeaders) throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s,%s", "Batch", "Match Group"));

		String header;
		for (int i = 0; i < BatchMatchConstants.REGULAR_OUTPUT_FIXED_COLUMN_LABELS.length; i++) {
			header = BatchMatchConstants.REGULAR_OUTPUT_FIXED_COLUMN_LABELS[i];
			sb.append(String.format(",%s", createCSVEntry(header)));
		}

		for (int i = 0; i < nonStandardHeadersRead.size(); i++) {
			header = nonStandardHeadersRead.get(i).length() > 0 ? nonStandardHeadersRead.get(i).substring(1) : "";
			sb.append(String.format(",%s", createCSVEntry(header)));
		}

		if (intensityHeaders != null && intensityHeaders.size() > 0
				&& !StringUtils.isEmptyOrNull(intensityHeaders.get(0)))
			sb.append(",");

		for (int i = 0; i < intensityHeaders.size(); i++) {
			if (!StringUtils.isEmptyOrNull(intensityHeaders.get(i)))
				sb.append(String.format(",%s", createCSVEntry(intensityHeaders.get(i))));
			else if (i > 0)
				sb.append(String.format(",%s", ""));
		}
		bos.write((sb.toString() + BatchMatchConstants.LINE_SEPARATOR).getBytes());
	}

	private void writeFeatureCSVEntry(
			StringBuilder sb, 
			FeatureFromFile feature, 
			FeatureFromFile nextFeature,
			String formatStringNumeric, 
			String formatStringNumericShorter, 
			String formatStringInteger) {

		String value = createAppropriateRowEntry(
				feature.getBatchIdx() == null ? "" : String.valueOf(feature.getBatchIdx()), formatStringNumeric,
				formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				feature.getRedundancyGroup() == null ? "" : String.valueOf(feature.getRedundancyGroup()),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		sb.append("\"" + feature.getName() + "\",");

		value = createAppropriateRowEntry(feature.getMass() == null ? "" : String.valueOf(feature.getMass()),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(feature.getRT() == null ? "" : String.valueOf(feature.getRT()),
				formatStringNumericShorter, formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(feature.getOldRt() == null ? "" : String.valueOf(feature.getOldRt()),
				formatStringNumericShorter, formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				feature.getMedianIntensity() == null ? "" : String.valueOf(Math.round(feature.getMedianIntensity())),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				feature.getMassDefectKendrick() == null ? "" : String.valueOf(feature.getMassDefectKendrick()),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		sb.append(createCSVEntry(feature.getIsotope()) + ", ");
		sb.append(createCSVEntry(feature.getOtherGroupIsotope()) + ", ");
		sb.append(createCSVEntry(feature.getAnnotation()) + ", ");
		sb.append(createCSVEntry(feature.getOtherGroupAnnotation()) + ", ");
		sb.append((StringUtils.isEmptyOrNull(feature.getFurtherAnnotation())
				? createCSVEntry(feature.getPossibleRedundancies())
				: createCSVEntry(feature.getFurtherAnnotation() + "; " + feature.getPossibleRedundancies())) + ",");
		sb.append((feature.getDerivation() == null ? "" : createCSVEntry(feature.getDerivation())) + ",");

		value = createAppropriateRowEntry(
				feature.getPutativeMolecularMass() == null ? "-" : String.valueOf((feature.getPutativeMolecularMass())),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				feature.getMassError() == null ? "-" : String.valueOf((feature.getMassError())), formatStringNumeric,
				formatStringInteger);
		sb.append(value + ",");

		sb.append((StringUtils.isEmptyOrNull(feature.getMolecularIonNumber()) ? "-"
				: createCSVEntry(feature.getMolecularIonNumber())) + ",");
		sb.append((StringUtils.isEmptyOrNull(feature.getChargeCarrier()) ? "-"
				: createCSVEntry(feature.getChargeCarrier())) + ",");
		sb.append((StringUtils.isEmptyOrNull(feature.getNeutralMass()) ? "-" : createCSVEntry(feature.getNeutralMass()))
				+ ",");

		value = createAppropriateRowEntry(feature.getBinIndex() == null ? "-" : String.valueOf(feature.getBinIndex()),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				feature.getOldCluster() == null ? "-" : String.valueOf(feature.getOldCluster()), formatStringNumeric,
				formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				feature.getNewCluster() == null ? "-" : String.valueOf(feature.getNewCluster()), formatStringNumeric,
				formatStringInteger);
		sb.append(value + ",");

		value = createAppropriateRowEntry(
				(feature.getNewNewCluster() == null ? "-" : String.valueOf(feature.getNewNewCluster())),
				formatStringNumeric, formatStringInteger);
		sb.append(value + ",");

		for (int j = 0; j < feature.getAddedColValues().size(); j++) {
			value = createAppropriateRowEntry(
					feature.getAddedColValues().get(j) == null ? "-" : feature.getAddedColValues().get(j),
					formatStringNumeric, formatStringInteger);
			sb.append(value + ",");
		}
	}

	private void createIntensityCSVSection(
			StringBuilder sb, 
			FeatureFromFile feature, 
			List<String> intensityHeaders,
			String formatStringNumeric, 
			String formatStringNumericShorter, 
			String formatStringInteger) {

		String value = null;

		for (int idx = 0; idx < intensityHeaders.size(); idx++) {
			value = feature.getValueForIntensityHeader(intensityHeaders.get(idx), this.getDerivedColNameMapping());

			if (StringUtils.isEmptyOrNull(intensityHeaders.get(idx)))
				sb.append("");
			else if (StringUtils.isEmptyOrNull(value))
				sb.append("");
			else if (".".equals(value.trim()))
				sb.append(value);
			else if (feature.valueForHeaderIsOutlier(intensityHeaders.get(idx), derivedColNameMapping))
				sb.append(createAppropriateRowEntry(String.valueOf(value), formatStringNumeric, formatStringInteger));
			else
				sb.append(createAppropriateRowEntry(String.valueOf(value), formatStringNumeric, formatStringInteger));

			if (idx < intensityHeaders.size() - 1)
				sb.append(",");
		}
	}

	public Map<String, String> getDerivedColNameMapping() {
		return derivedColNameMapping;
	}

	public Map<String, Double> getFeatureNameToOldRtMap() {
		return featureNameToOldRtMap;
	}

	public void setFeatureNameToOldRtMap(Map<String, Double> map) {
		this.featureNameToOldRtMap = map;
	}

	public void setDerivedColNameMapping(Map<String, String> derivedColNameMapping) {
		this.derivedColNameMapping = derivedColNameMapping;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
