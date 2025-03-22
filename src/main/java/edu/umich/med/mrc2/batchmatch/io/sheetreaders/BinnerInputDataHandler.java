////////////////////////////////////////////////////
// BinnerInputDataHandler.java
// Written by Jan Wigginton, June 2023
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetreaders;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import edu.umich.med.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.med.mrc2.batchmatch.data.orig.TextFile;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.med.mrc2.batchmatch.main.PostProccessConstants;
import edu.umich.med.mrc2.batchmatch.process.orig.PostProcessDataSet;
import edu.umich.med.mrc2.batchmatch.utils.orig.StringUtils;

public class BinnerInputDataHandler {

	private boolean foundFeature;
	private boolean foundBinnerMass;
	private boolean foundNeutralMass;
	private boolean foundRtExpected;
	private boolean foundRtObserved;
	private boolean foundMz;
	private boolean foundCharge;

	private List<String> intensityHeaders;
	public static Map<String, String> headerTagMap;

	public BinnerInputDataHandler() {
		initializeHeaderTagMap();
	}

	public static void initializeHeaderTagMap() {

		headerTagMap = new HashMap<String, String>();

		headerTagMap.put("featurename", "featurename");
		headerTagMap.put("neutralmass", "neutralmass");
		headerTagMap.put("binnerm/z", "binnerm/z");
		headerTagMap.put("rtexpected", "rtexpected");
		headerTagMap.put("rtobserved", "rtobserved");
		headerTagMap.put("monoisotopicm/z", "monoisotopicm/z");
		headerTagMap.put("charge", "charge");
	}

	protected void initializeFoundStatus() {
		foundFeature = false;
		foundBinnerMass = false;
		foundNeutralMass = false;
		foundRtExpected = false;
		foundRtObserved = false;
		foundMz = false;
		foundCharge = false;
	}

	public PostProcessDataSet readFeatureDataAndFilterSalts(String fileName) {

		PostProcessDataSet data = readFeatureData(fileName);
		data.filterPutativeSalts(50.0, .9, 500.0, 5.0);
		return data;
	}

	public PostProcessDataSet readFeatureData(String binnerInputFile) {

		File inputFile = new File(binnerInputFile);
		TextFile rawTextData = new TextFile();
		try {
			rawTextData.open(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return readFeatureData(rawTextData);
	}

	public PostProcessDataSet readFeatureData(TextFile txtData) {

		initializeFoundStatus();

		intensityHeaders = new ArrayList<String>();
		List<FeatureFromFile> featuresToSearch = new ArrayList<FeatureFromFile>();
		List<String> standardHeaderTags = new ArrayList<String>();
		String value = null;
		boolean readingIntensities = false;

		// Column indices corresponding to (standard) columns where info will be used to
		// initialize the featureFromFile -- these values will be reported via the
		// standard header set
		List<Integer> standardHeaderIndices = new ArrayList<Integer>();

		// Column indices of columns corresponding to intensity values
		List<Integer> intensityHeaderIndices = new ArrayList<Integer>();

		List<String> rowContents = null;
		List<Integer> blankHeaderIndices = new ArrayList<Integer>();

		try {
			int rowStart = 0, rowEnd = txtData.getEndRowIndex() + 1;
			int dataStart = 0;
			boolean firstNonBlankHeaderFound = false;
			int headerCt = 0;
			for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);
				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) && StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				int nConsecutiveBlanks = 0;

				for (int col = 0; col < rowContents.size(); col++) {

					// Fixes Index issue
					value = StringUtils.removeNonPrintable(rowContents.get(col).trim());

					// skip blank cols at beginning
					if (!firstNonBlankHeaderFound && StringUtils.checkEmptyOrNull(value))
						continue;
					
					firstNonBlankHeaderFound = true;

					if (!StringUtils.isEmptyOrNull(value)) {

						nConsecutiveBlanks = 0;
						String tag = StringUtils.removeSpaces(value).toLowerCase();

						if (readingIntensities) {
							intensityHeaderIndices.add(col);
							intensityHeaders.add(value);
						} else {
							standardHeaderIndices.add(col);
							standardHeaderTags.add(tag);
						}

						if (nConsecutiveBlanks > 4)
							break;

						if ("charge".equals(tag))
							readingIntensities = true;

					}
				}
				for (int headercol = 0; headercol < rowContents.size(); headercol++) {

					String cv = rowContents.get(headercol);

					if (StringUtils.isEmptyOrNull(cv) || cv.equals("-") 
							|| StringUtils.removeSpaces(cv).isEmpty())
						blankHeaderIndices.add(headercol);
				}
				dataStart = rowNum + 1;
				headerCt = rowContents.size();
				break;
			}

			System.out.println("Data start is " + dataStart);
			String keyForIntensity;

			for (int rowNum = dataStart; rowNum < rowEnd; rowNum++) {

				rowContents = txtData.getRawStringRow(rowNum);

				if (rowContents == null || rowContents.size() < 2)
					continue;

				if (StringUtils.isEmptyOrNull(rowContents.get(0)) 
						&& StringUtils.isEmptyOrNull(rowContents.get(1)))
					continue;

				if (rowContents.size() < headerCt) {
					
					for (int i = 0; i < blankHeaderIndices.size(); i++) {
						
						if (!rowContents.get(blankHeaderIndices.size()).equals("-")) {
							
							rowContents.set(blankHeaderIndices.get(i),"");
							if (rowContents.size() >= headerCt)
								break;
						}
					}
				}
				while (rowContents.size() < headerCt)
					rowContents.add("");

				FeatureFromFile featureFromFile = new FeatureFromFile();

				for (int i = 0; i < standardHeaderIndices.size(); i++) {
					value = rowContents.get(standardHeaderIndices.get(i));
					featureFromFile.setValueForBinnerHeaderTag(standardHeaderTags.get(i), value, true);

				}
				// (standardHeaderTags.get(i), value);

				Boolean addFeature = (!StringUtils.isEmptyOrNull(featureFromFile.getName())
						&& featureFromFile.getRT() != null
						&& !StringUtils.isEmptyOrNull(featureFromFile.getRT().toString())
						&& featureFromFile.getMass() != null
						&& !StringUtils.isEmptyOrNull(featureFromFile.getMass().toString()));

				if (addFeature) {
					for (int i = 0; i < intensityHeaderIndices.size(); i++) {
						value = rowContents.get(intensityHeaderIndices.get(i));

						keyForIntensity = intensityHeaders.get(i);
						if (!StringUtils.isEmptyOrNull(keyForIntensity))
							// keyForIntensity =
							// StringUtils.removeSpaces(intensityHeaders.get(i)).toLowerCase();
							keyForIntensity = StringUtils.removeSpaces(intensityHeaders.get(i)); // .toLowerCase();
						else
							keyForIntensity = "";

						featureFromFile.getIntensityValuesByHeaderMap().put(keyForIntensity, value);
					}

					featuresToSearch.add(featureFromFile);
				}
			} // rowNumLoop
		} // try
		catch (Exception e) {
			e.printStackTrace();
		}

		PostProcessDataSet processedData = new PostProcessDataSet();
		processedData.initializeHeaders(featuresToSearch, new ArrayList<String>(), intensityHeaders, null, false, false,
				"", "", null);

		return processedData;
	}

	protected Boolean screenForEssentialCols() {

		if (!foundFeature) {

			JOptionPane.showMessageDialog(null,
					"Application is unable to locate the feature name column in your Binner Report.  "
							+ BinnerConstants.LINE_SEPARATOR
							+ "Ignoring case and spacing, the \"feature\" column must be tagged by one of the following headers :"
							+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
							+ PostProccessConstants.COMPOUND_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR
							+ "Please update the headers in your report file to clearly indicate which column contains "
							+ BinnerConstants.LINE_SEPARATOR
							+ "names of the features you'd like to put through a library search.");
			return false;
		}

		if (!foundBinnerMass || !foundNeutralMass || !foundMz) {

			JOptionPane.showMessageDialog(null, "Application is unable to locate a mass column in your Binner Report.  "
					+ BinnerConstants.LINE_SEPARATOR
					+ "Ignoring case and spacing, the \"mass\" column must be tagged by one of the following headers :"
					+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
					+ PostProccessConstants.MASS_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
					+ BinnerConstants.LINE_SEPARATOR
					+ "Please update the headers in your report file to clearly indicate which column contains the feature "
					+ BinnerConstants.LINE_SEPARATOR
					+ "mass of the features you'd like to put through a library search.");
			return false;
		}

		if (!foundRtExpected) {

			JOptionPane.showMessageDialog(null,
					"Application is unable to locate the expected retention time column in your Binner Report.  "
							+ BinnerConstants.LINE_SEPARATOR
							+ "Ignoring case and spacing, the \"rt\" column must be tagged by one of the following headers :"
							+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
							+ PostProccessConstants.RT_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR
							+ "Please update the headers in your report file to clearly indicate which column contains retention "
							+ BinnerConstants.LINE_SEPARATOR
							+ "times of the features you'd like to put through a library search.");
			return false;
		}

		if (!foundRtObserved) {

			JOptionPane.showMessageDialog(null,
					"Application is unable to locate the expected retention time column in your Binner Report.  "
							+ BinnerConstants.LINE_SEPARATOR
							+ "Ignoring case and spacing, the \"rt\" column must be tagged by one of the following headers :"
							+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
							+ PostProccessConstants.RT_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR
							+ "Please update the headers in your report file to clearly indicate which column contains retention "
							+ BinnerConstants.LINE_SEPARATOR
							+ "times of the features you'd like to put through a library search.");
			return false;
		}

		if (!foundCharge) {

			JOptionPane.showMessageDialog(null,
					"Application is unable to locate the charge column in your Binner Report.  "
							+ BinnerConstants.LINE_SEPARATOR
							+ "Ignoring case and spacing, the \"rt\" column must be tagged by one of the following headers :"
							+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR
							+ PostProccessConstants.RT_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR
							+ "Please update the headers in your report file to clearly indicate which column contains retention "
							+ BinnerConstants.LINE_SEPARATOR
							+ "times of the features you'd like to put through a library search.");
			return false;
		}
		return true;
	}

	public void writeDataInBinnerInputFormat(PostProcessDataSet data, String outputDirectory, String fileName) {

		String outputFileName = outputDirectory + BatchMatchConstants.FILE_SEPARATOR + fileName;

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));

			bos.write(String
					.format("%s",
							"Feature name,Neutral mass,Binner M/Z,RT expected,RT observed,Monoisotopic M/Z,Charge")
					.getBytes());

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < data.getOrderedIntensityHeaders().size(); i++)
				sb.append("," + data.getOrderedIntensityHeaders().get(i));
			bos.write((sb.toString() + BatchMatchConstants.LINE_SEPARATOR).getBytes());

			for (int i = 0; i < data.getFeatures().size(); i++) {

				FeatureFromFile f = data.getFeatures().get(i);
				sb = new StringBuilder();

				sb.append(f.getName() + "," + f.getNeutralMass() + "," + f.getPutativeMolecularMass() + ", " + f.getRT()
						+ "," + f.getOldRt() + "," + f.getMass() + "," + f.getPutativeCharge());

				for (int j = 0; j < data.getOrderedIntensityHeaders().size(); j++) {
					String key = data.getOrderedIntensityHeaders().get(j);
					String value = f.getIntensityValuesByHeaderMap().get(key);

					String entry = String.format(",%s", (value == null ? "0" : value)); // f.getIntensityValuesByHeaderMap().get(key));
					sb.append(entry);
				}

				sb.append(BatchMatchConstants.LINE_SEPARATOR);
				bos.write(sb.toString().getBytes());
			}
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
