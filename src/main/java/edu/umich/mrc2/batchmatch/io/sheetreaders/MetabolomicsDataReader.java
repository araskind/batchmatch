////////////////////////////////////////////////////////
// PostProcessDataReader.java
// Written by Jan Wigginton and Bill Duren
// September 2019
////////////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io.sheetreaders;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import edu.umich.mrc2.batchmatch.data.FeatureFromFile;
import edu.umich.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.mrc2.batchmatch.main.BinnerConstants;
import edu.umich.mrc2.batchmatch.main.PostProccessConstants;
import edu.umich.mrc2.batchmatch.utils.MyDataFormatter;

public class MetabolomicsDataReader {

	protected Boolean foundIndex, foundGroupIsotope, foundAnnotation, foundGroupAnnotation, foundFurtherAnnotation;
	protected Boolean foundFeature, foundRT, foundOldRT, foundMass, foundIntensity, foundKmd, foundIsotope, foundDerivation;
	protected Boolean foundPutative, foundBin, foundCluster, foundRebinCluster, foundRTCluster, foundMassError;
	protected Boolean foundCc, foundAdduct, foundMolecularIon;
	protected Boolean foundRedundancyGroup, foundPctMissing, foundRSD, foundBatch;
	protected Boolean foundMatchGroupCt, foundMatchGroupUniqueCt;

	public static Map<String, String> headerTagMap;

	protected List<String> intensityHeaders;
	protected List<String> nonStandardHeadersWithSpacing;

	protected Boolean readingIntensities;
	protected List<FeatureFromFile> featuresToSearch = null;

	protected Boolean forMergeOutput = false;
	protected SimpleDateFormat dateFmt = new SimpleDateFormat("M/d/yyyy");
	protected MyDataFormatter formatter;
	protected String pasteValue;
	protected String returnValue;
	protected boolean haveAlready = false;
	protected boolean haveNow = false;

	public MetabolomicsDataReader(String filename) {
		formatter = new MyDataFormatter();
		initializeHeaderTagMap();
	}

	protected void initializeHeaderTagMap() {

		headerTagMap = new HashMap<String, String>();

		for (String hdr : PostProccessConstants.BATCH_IDX_CHOICES_ARRAY)
			headerTagMap.put(hdr, "batch");

		for (String hdr : PostProccessConstants.RSD_CHOICES_ARRAY)
			headerTagMap.put(hdr, "rsd");

		for (String hdr : PostProccessConstants.PCT_MISSING_CHOICES_ARRAY)
			headerTagMap.put(hdr, "pctmissing");

		for (String hdr : PostProccessConstants.REDUNDANCY_GRP_CHOICES_ARRAY)
			headerTagMap.put(hdr, "rgroup");

		for (String hdr : PostProccessConstants.INDEX_CHOICES_ARRAY)
			headerTagMap.put(hdr, "index");

		for (String hdr : PostProccessConstants.COMPOUND_CHOICES_ARRAY)
			headerTagMap.put(hdr, "feature");

		for (String hdr : PostProccessConstants.MASS_CHOICES_ARRAY)
			headerTagMap.put(hdr, "mass");

		for (String hdr : PostProccessConstants.RT_CHOICES_ARRAY)
			headerTagMap.put(hdr, "rt");

		for (String hdr : BatchMatchConstants.OLDRT_CHOICES_ARRAY)
			headerTagMap.put(hdr, "oldrt");

		for (String hdr : PostProccessConstants.INTENSITY_CHOICES_ARRAY)
			headerTagMap.put(hdr, "intensity");

		for (String hdr : PostProccessConstants.KMD_CHOICES_ARRAY)
			headerTagMap.put(hdr, "kmd");

		for (String hdr : PostProccessConstants.ISOTOPE_CHOICES_ARRAY)
			headerTagMap.put(hdr, "isotope");

		for (String hdr : PostProccessConstants.GROUP_ISOTOPE_CHOICES_ARRAY)
			headerTagMap.put(hdr, "otherisotopesingroup");

		for (String hdr : PostProccessConstants.ANNOTATION_CHOICES_ARRAY)
			headerTagMap.put(hdr, "annotation");

		for (String hdr : PostProccessConstants.GROUP_ANNOTATION_CHOICES_ARRAY)
			headerTagMap.put(hdr, "otherannotationsingroup");

		for (String hdr : PostProccessConstants.FURTHER_ANNOTATION_CHOICES_ARRAY)
			headerTagMap.put(hdr, "furtherannotation");

		for (String hdr : PostProccessConstants.DERIVATION_CHOICES_ARRAY)
			headerTagMap.put(hdr, "derivation");

		for (String hdr : PostProccessConstants.PUTATIVEMASS_CHOICES_ARRAY)
			headerTagMap.put(hdr, "putativemolecularmass");

		for (String hdr : PostProccessConstants.MASS_ERROR_CHOICES_ARRAY)
			headerTagMap.put(hdr, "masserror");

		for (String hdr : PostProccessConstants.CHARGE_CARRIER_CHOICES_ARRAY)
			headerTagMap.put(hdr, "chargecarrier");

		for (String hdr : PostProccessConstants.ADDUCT_CHOICES_ARRAY)
			headerTagMap.put(hdr, "adductnl");

		for (String hdr : PostProccessConstants.MOLECULAR_ION_CHOICES_ARRAY)
			headerTagMap.put(hdr, "molecularion");

		for (String hdr : PostProccessConstants.BIN_CHOICES_ARRAY)
			headerTagMap.put(hdr, "bin");

		for (String hdr : PostProccessConstants.CLUSTER_CHOICES_ARRAY)
			headerTagMap.put(hdr, "cluster");

		for (String hdr : PostProccessConstants.REBINCLUSTER_CHOICES_ARRAY)
			headerTagMap.put(hdr, "rebincluster");

		for (String hdr : PostProccessConstants.RTCLUSTER_CHOICES_ARRAY)
			headerTagMap.put(hdr, "rtcluster");
	}

	protected void initializeFoundStatus() {
		foundMatchGroupCt = foundMatchGroupUniqueCt = false;
		foundIndex = foundFeature = foundMass = foundRT = foundOldRT = foundIntensity = foundKmd = false;
		foundIsotope = foundGroupIsotope = foundGroupAnnotation = foundAnnotation = foundFurtherAnnotation = false;
		foundDerivation = foundPutative = false;
		foundBin = foundCluster = foundRebinCluster = foundRTCluster = false;
		foundMassError = foundCc = foundAdduct = foundMolecularIon = false;
		foundRedundancyGroup = foundPctMissing = foundRSD = foundBatch = false;
	}

	protected Boolean haveDuplicateClassName(String tag) {

		haveAlready = (this.foundMatchGroupCt);
		haveNow = PostProccessConstants.BATCH_MATCHGRP_CT_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			this.foundMatchGroupCt = true;
			return haveAlready;
		}

		haveAlready = (this.foundMatchGroupUniqueCt);
		haveNow = PostProccessConstants.BATCH_MATCHGRP_UNIQUECT_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			this.foundMatchGroupUniqueCt = true;
			return haveAlready;
		}

		haveAlready = (this.foundBatch.equals(true));
		haveNow = PostProccessConstants.BATCH_IDX_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			this.foundBatch = true;
			return haveAlready;
		}

		haveAlready = (this.foundRSD);
		haveNow = PostProccessConstants.RSD_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			this.foundRSD = true;
			return haveAlready;
		}

		haveAlready = (this.foundPctMissing.equals(true));
		haveNow = PostProccessConstants.PCT_MISSING_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			this.foundPctMissing = true;
			return haveAlready;
		}

		haveAlready = (this.foundRedundancyGroup.equals(true));
		haveNow = PostProccessConstants.REDUNDANCY_GRP_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			this.foundRedundancyGroup = true;
			return haveAlready;
		}

		haveAlready = (this.foundIndex.equals(true));
		haveNow = PostProccessConstants.INDEX_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundIndex = true;
			return haveAlready;
		}

		haveAlready = (this.foundFeature.equals(true));
		haveNow = PostProccessConstants.COMPOUND_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundFeature = true;
			return haveAlready;
		}

		haveAlready = (this.foundRT);
		haveNow = PostProccessConstants.RT_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundRT = true;
			return haveAlready;
		}

		haveAlready = (this.foundOldRT);
		haveNow = BatchMatchConstants.OLDRT_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundOldRT = true;
			return haveAlready;
		}

		haveAlready = (this.foundMass);
		haveNow = PostProccessConstants.MASS_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundMass = true;
			return haveAlready;
		}

		haveAlready = (this.foundIntensity);
		haveNow = PostProccessConstants.INTENSITY_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundIntensity = true;
			return haveAlready;
		}

		haveAlready = (this.foundKmd);
		haveNow = PostProccessConstants.KMD_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundKmd = true;
			return haveAlready;
		}

		haveAlready = (this.foundIsotope);
		haveNow = PostProccessConstants.ISOTOPE_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundIsotope = true;
			return haveAlready;
		}

		haveAlready = (this.foundGroupIsotope);
		haveNow = PostProccessConstants.GROUP_ISOTOPE_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundGroupIsotope = true;
			return haveAlready;
		}

		haveAlready = (this.foundAnnotation);
		haveNow = PostProccessConstants.ANNOTATION_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundAnnotation = true;
			return haveAlready;
		}

		haveAlready = (this.foundGroupAnnotation);
		haveNow = PostProccessConstants.GROUP_ANNOTATION_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundGroupAnnotation = true;
			return haveAlready;
		}

		haveAlready = (this.foundFurtherAnnotation);
		haveNow = PostProccessConstants.FURTHER_ANNOTATION_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundFurtherAnnotation = true;
			return haveAlready;
		}

		haveAlready = (this.foundDerivation);
		haveNow = PostProccessConstants.DERIVATION_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundDerivation = true;
			return haveAlready;
		}

		haveAlready = (this.foundPutative);
		haveNow = PostProccessConstants.PUTATIVEMASS_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundPutative = true;
			return haveAlready;
		}

		haveAlready = (this.foundMassError);
		haveNow = PostProccessConstants.MASS_ERROR_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundMassError = true;
			return haveAlready;
		}

		haveAlready = (this.foundCc);
		haveNow = PostProccessConstants.CHARGE_CARRIER_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundCc = true;
			return haveAlready;
		}

		haveAlready = (this.foundAdduct);
		haveNow = PostProccessConstants.ADDUCT_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundAdduct = true;
			return haveAlready;
		}

		haveAlready = (this.foundMolecularIon);
		haveNow = PostProccessConstants.MOLECULAR_ION_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundMolecularIon = true;
			return haveAlready;
		}

		haveAlready = (this.foundBin);
		haveNow = PostProccessConstants.BIN_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundBin = true;
			return haveAlready;
		}

		haveAlready = (this.foundCluster);
		haveNow = PostProccessConstants.CLUSTER_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundCluster = true;
			return haveAlready;
		}

		haveAlready = (this.foundRebinCluster);
		haveNow = PostProccessConstants.REBINCLUSTER_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundRebinCluster = true;
			return haveAlready;
		}

		haveAlready = (this.foundRTCluster);
		haveNow = PostProccessConstants.RTCLUSTER_CHOICES_ARRAY.contains(tag);
		if (haveNow) {
			foundRTCluster = true;
			return haveAlready;
		}
		return false;
	}

	protected String handleCell(Cell cell) {

		// String
		pasteValue = null;
		returnValue = null;

		// cellExtension.setCell(cell);

		switch (cell.getCellType()) {
		case BOOLEAN:
			returnValue = "" + cell.getBooleanCellValue();
			break;

		case STRING:
			// bug?
		default:
			pasteValue = cell.getStringCellValue();
			pasteValue = pasteValue.replace("\"", "");
			pasteValue = pasteValue.replace("=", "");
			returnValue = pasteValue;
			break;
		case BLANK:
			returnValue = "";
			break;

		case NUMERIC:

			if (DateUtil.isCellDateFormatted(cell)) {

				returnValue = dateFmt.format(cell.getDateCellValue());
			} else {
				formatter = new MyDataFormatter();
				returnValue = formatter.formatCellValue(cell);
			}
			break;

		}
		return returnValue;
	}

	protected Boolean screenForEssentialCols(Boolean readingMolecularIons) {

		if (readingMolecularIons && !foundDerivation && !foundPutative) {
			JOptionPane.showMessageDialog(null,
					"PostProcess cannot calculate a neutral mass because your Binner report does not have a \"Derivations\" or a \"Derived Molecular Mass\""
							+ BinnerConstants.LINE_SEPARATOR
							+ "column. Ignoring case and spacing, the former (\"Derivations\") column can be tagged by one of the following headers :"
							+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR + "     "
							+ PostProccessConstants.DERIVATION_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR
							+ "Alternatively, a \"Derived Molecular Mass\" column containing the calculated neutral mass for each feature and tagged by one of the headers "
							+ BinnerConstants.LINE_SEPARATOR + BinnerConstants.LINE_SEPARATOR + "     "
							+ PostProccessConstants.PUTATIVEMASS_CHOICES_ARRAY + BinnerConstants.LINE_SEPARATOR
							+ BinnerConstants.LINE_SEPARATOR
							+ "will enable the neutral mass search.  Please update the headers in your report file to clearly indicate which column contains"
							+ BinnerConstants.LINE_SEPARATOR
							+ "neutral mass information for the features you'd like to put through a library search.");
			return false;
		}

		if (!foundFeature) {

			JOptionPane.showMessageDialog(null,
					"PostProcess is unable to locate the feature name column in your Binner Report.  "
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

		if (!foundMass) {

			JOptionPane.showMessageDialog(null,
					"PostProcess is unable to locate the mass column in your Binner Report.  "
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

		if (!foundRT) {

			JOptionPane.showMessageDialog(null,
					"PostProcess is unable to locate the retention time column in your Binner Report.  "
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

	protected Boolean checkForOutlier(Row r, int col) {

		return false;

		// Cell c = r.getCell(col, Row.CREATE_NULL_AS_BLANK);
		/*
		 * if (r.getCell(col, Row.CREATE_NULL_AS_BLANK) != null && r.getCell(col,
		 * Row.CREATE_NULL_AS_BLANK).getCellStyle() != null && r.getCell(col,
		 * Row.CREATE_NULL_AS_BLANK).getCellStyle().getFillForegroundColorColor() !=
		 * null) if ("FFCCCCFF".equals(((XSSFColor) r.getCell(col,
		 * Row.CREATE_NULL_AS_BLANK).getCellStyle().getFillForegroundColorColor()).
		 * getARGBHex())) return true; return false;
		 */
	}

	protected String pullValue(Row r, int col) {

		try {
			return handleCell(r.getCell(col, MissingCellPolicy.CREATE_NULL_AS_BLANK));
		} catch (Exception e) {
			System.out.println("Null cell");
		}
		return null;
	}

	protected void initializeMissingValueInfo(FeatureFromFile feature) {
		// "Batch
		Collection<String> rawIntensities = feature.getIntensityValuesByHeaderMap().values();

		int arraySize = rawIntensities.size();
		if (feature.getIntensityValuesByHeaderMap().containsKey("-")
				|| feature.getIntensityValuesByHeaderMap().containsKey(""))
			arraySize--;

		int nMissing = 0;
		for (String value : rawIntensities) {
			try {
				Double.parseDouble(value);
			} catch (Exception e) {
				nMissing++;
			}
		}

		if (feature.getIntensityValuesByHeaderMap().containsKey("-")
				|| feature.getIntensityValuesByHeaderMap().containsKey(""))
			;
		nMissing--;

		feature.setnMissingIntensityValues(nMissing);
		feature.setnTotalIntensityValues(arraySize);
		feature.setPctMissing(100.0 * (nMissing * 1.0) / (arraySize * 1.0));
	}

	public List<String> getIntensityHeadersRead() {
		return this.intensityHeaders;
	}

	public Boolean getForMergeOutput() {
		return forMergeOutput;
	}

	public void setForMergeOutput(Boolean forMergeOutput) {
		this.forMergeOutput = forMergeOutput;
	}
}
