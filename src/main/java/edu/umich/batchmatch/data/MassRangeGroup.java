////////////////////////////////////////////////////
// MassRangeGroup.java
// Written by Jan Wigginton February 2022
////////////////////////////////////////////////////

package edu.umich.batchmatch.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.batchmatch.data.comparators.FeatureByRtAndMassComparator;
import edu.umich.batchmatch.main.BatchMatchConstants;

public class MassRangeGroup {

	private List<FeatureFromFile> unmatchedFeatures;
	private Map<Integer, MatchedFeatureGroup> matchedFeatureGroups;
	private Boolean isOrganized = false;

	private Double minMass = null, maxMass = null;
	private Integer nFeatures;

	public MassRangeGroup() {
		super();
		isOrganized = false;
		unmatchedFeatures = new ArrayList<FeatureFromFile>();
		matchedFeatureGroups = new HashMap<Integer, MatchedFeatureGroup>();
	}

	public void addRawFeature(FeatureFromFile f) {
		if (unmatchedFeatures == null)
			unmatchedFeatures = new ArrayList<FeatureFromFile>();

		unmatchedFeatures.add(f);
		isOrganized = false;
	}

	public void organize() {
		unpack();

		Double currMass = null;
		for (FeatureFromFile f : unmatchedFeatures) {
			currMass = f.getMass();

			if (minMass == null)
				minMass = currMass;
			if (maxMass == null)
				maxMass = currMass;

			if (currMass > maxMass)
				maxMass = f.getMass();
			if (currMass < minMass)
				minMass = currMass;
		}
		nFeatures = unmatchedFeatures.size();

		Map<Integer, List<FeatureFromFile>> featuresByMatchGroupMap = new HashMap<Integer, List<FeatureFromFile>>();

		List<FeatureFromFile> newunmatchedFeatures = new ArrayList<FeatureFromFile>();
		for (FeatureFromFile f : unmatchedFeatures) {
			if (f.getRedundancyGroup() == null) {
				newunmatchedFeatures.add(f);
				continue;
			}
			if (!featuresByMatchGroupMap.containsKey(f.getRedundancyGroup()))
				featuresByMatchGroupMap.put(f.getRedundancyGroup(), new ArrayList<FeatureFromFile>());

			featuresByMatchGroupMap.get(f.getRedundancyGroup()).add(f);
		}

		matchedFeatureGroups.clear();

		for (Integer key : featuresByMatchGroupMap.keySet()) {
			MatchedFeatureGroup newFeatureGroup = new MatchedFeatureGroup(key, featuresByMatchGroupMap.get(key), true);
			matchedFeatureGroups.put(key, newFeatureGroup);
		}

		unmatchedFeatures = new ArrayList<FeatureFromFile>();
		for (FeatureFromFile f : newunmatchedFeatures)
			unmatchedFeatures.add(f);
		isOrganized = true;
	}

	/*
	 * private Map<Integer, List<FeatureFromFile>>
	 * assignMassRangeGroup(List<FeatureFromFile> allFeatures) {
	 * 
	 * Map<Integer, List<FeatureFromFile>> massRangeMap = new HashMap<Integer,
	 * List<FeatureFromFile>> ();
	 * 
	 * Collections.sort(allFeatures, new FeatureByMassComparator());
	 * 
	 * Double massGap = Double.NaN;
	 * 
	 * Integer rangeKey = 0; massRangeMap.put(rangeKey, new
	 * ArrayList<FeatureFromFile>());
	 * 
	 * for (int i = 1; i < allFeatures.size(); i++) {
	 * 
	 * massRangeMap.get(rangeKey).add(allFeatures.get(i-1));
	 * 
	 * massGap = Math.abs(allFeatures.get(i).getMass() -
	 * allFeatures.get(i-1).getMass()); if (massGap >
	 * BatchMatchConstants.MASS_RANGE_BREAK_GAP) { massRangeMap.put(++rangeKey, new
	 * ArrayList<FeatureFromFile>()); } } return massRangeMap; }
	 */

	private void unpack() {
		for (MatchedFeatureGroup matchGrp : matchedFeatureGroups.values()) {
			for (FeatureFromFile f : matchGrp.getFeaturesInGroup()) {
				unmatchedFeatures.add(f);
			}
			isOrganized = false;
			matchedFeatureGroups = new HashMap<Integer, MatchedFeatureGroup>();
		}
	}

	/*
	 * public Map<Integer, MatchedFeatureGroup>
	 * grabInterestingFeatureGroups(Map<String, String> targetFeatures) {
	 * 
	 * if (!isOrganized) organize();
	 * 
	 * Map<Integer, MatchedFeatureGroup> featureGroupsOfInterest = new
	 * HashMap<Integer, MatchedFeatureGroup>();
	 * 
	 * MatchedFeatureGroup grp = null; for (Integer key :
	 * matchedFeatureGroups.keySet()) { grp = matchedFeatureGroups.get(key); if
	 * (grp.countMatchedTargetFeatures(targetFeatures) > 4)
	 * featureGroupsOfInterest.put(key, grp); } return featureGroupsOfInterest; }
	 */

	public int getMaxBatchCount() {
		int maxBatchCt = -1;

		Map<Integer, Integer> batchCtMap = getBatchCountMap();
		if (batchCtMap == null)
			return maxBatchCt;

		for (Integer value : batchCtMap.values())
			if (value > maxBatchCt)
				maxBatchCt = value;

		return maxBatchCt;
	}

	public Map<Integer, Integer> getFeatureCountMap() {

		Map<Integer, Integer> featureCtMap = new HashMap<Integer, Integer>();

		for (MatchedFeatureGroup fGroup : matchedFeatureGroups.values())
			featureCtMap.put(fGroup.getMatchGrpKey(), fGroup.getFeatureNames().size());

		return featureCtMap;
	}

	public Map<Integer, List<String>> getFeatureNamesForGroupsWithNBatches(Integer nBatches, Boolean withNamed) {
		Map<Integer, Integer> batchCtMap = getBatchCountMap();

		List<Integer> featureGroupsWithCount = new ArrayList<Integer>();
		for (Integer key : batchCtMap.keySet()) {
			if (batchCtMap.get(key) == nBatches)
				featureGroupsWithCount.add(key);
		}

		Map<Integer, List<String>> completeFeatureGroupNamesMap = new HashMap<Integer, List<String>>();

		for (Integer groupKey : featureGroupsWithCount) {

			MatchedFeatureGroup fGroup = matchedFeatureGroups.get(groupKey);

			if (fGroup.countMatchedTargetFeatures(null) < 1)
				continue;

			if (!completeFeatureGroupNamesMap.containsKey(groupKey))
				completeFeatureGroupNamesMap.put(groupKey, new ArrayList<String>());

			for (String fName : fGroup.getFeatureNames())
				completeFeatureGroupNamesMap.get(groupKey).add(fName);

		}
		return completeFeatureGroupNamesMap;
	}

	public Map<String, Integer> getFeatureNamesForGroupsWithNBatchesByFeatureName(Integer nBatches, Boolean withNamed) {

		Map<Integer, List<String>> namesByGroup = getFeatureNamesForGroupsWithNBatches(nBatches, withNamed);
		Map<String, Integer> groupsByName = new HashMap<String, Integer>();

		for (Integer grpKey : namesByGroup.keySet()) {
			List<String> namesInGroup = namesByGroup.get(grpKey);

			for (String name : namesInGroup)
				groupsByName.put(name, grpKey);
		}
		return groupsByName;
	}

	public Map<Integer, Integer> getBatchCountMap() {

		Map<Integer, Integer> batchCtMap = new HashMap<Integer, Integer>();

		for (MatchedFeatureGroup fGroup : matchedFeatureGroups.values())
			batchCtMap.put(fGroup.getMatchGrpKey(), fGroup.getUniqueBatches().size());

		return batchCtMap;
	}

	public List<FeatureFromFile> renewAllFeatures() {
		unpack();
		List<FeatureFromFile> allFeatures = new ArrayList<FeatureFromFile>();

		for (FeatureFromFile f : unmatchedFeatures)
			allFeatures.add(f);

		for (MatchedFeatureGroup grp : matchedFeatureGroups.values())
			for (FeatureFromFile f : grp.getFeaturesInGroup())
				allFeatures.add(f);

		Collections.sort(allFeatures, new FeatureByRtAndMassComparator());
		return allFeatures;
	}

	public List<FeatureFromFile> grabAsFeatureList() {
		List<FeatureFromFile> allFeatures = new ArrayList<FeatureFromFile>();

		for (MatchedFeatureGroup matchGrp : matchedFeatureGroups.values()) {
			matchGrp.ensureHighestCorrs(true);
			for (FeatureFromFile f : matchGrp.getFeaturesInGroup()) {
				allFeatures.add(f);
			}
		}

		for (FeatureFromFile f : this.unmatchedFeatures)
			allFeatures.add(f);

		Collections.sort(allFeatures, new FeatureByRtAndMassComparator());

		return allFeatures;
	}

	public Boolean keepForAnalysis(Map<String, String> targetFeatures) {
		for (MatchedFeatureGroup grp : matchedFeatureGroups.values())
			if (grp.countMatchedTargetFeatures(targetFeatures) > 0)
				return true;
		return false;
	}

	public Double getMinMass() {
		if (!isOrganized)
			organize();
		return this.minMass;
	}

	public Double getMaxMass() {
		if (!isOrganized)
			organize();
		return this.maxMass;
	}

	public Integer getNFeatures() {
		if (!isOrganized)
			organize();
		return this.nFeatures;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("For mass range group starting at " + (minMass == null ? "unknown" : minMass));
		sb.append(BatchMatchConstants.LINE_SEPARATOR);
		sb.append("and ending at " + (maxMass == null ? "unknown" : maxMass)
				+ " the following feature groups are of interest ");
		sb.append(BatchMatchConstants.LINE_SEPARATOR);

		return ""; // sb.toString();
	}

	public Map<Integer, MatchedFeatureGroup> grabMatchedFeatureGroups() {
		if (this.matchedFeatureGroups == null) {
			return null; // need to update to rebuild
		}
		return matchedFeatureGroups;
	}

	public Map<Integer, MatchedFeatureGroup> grabMatchedNamedFeatureGroups() {
		grabMatchedFeatureGroups();

		Map<Integer, MatchedFeatureGroup> namedMatchGroups = new HashMap<Integer, MatchedFeatureGroup>();
		for (MatchedFeatureGroup grp : matchedFeatureGroups.values()) {
			if (grp.countMatchedTargetFeatures(null) > 0)
				namedMatchGroups.put(grp.getMatchGrpKey(), grp);
		}
		return namedMatchGroups;
	}

	public Map<Integer, MatchedFeatureGroup> grabMatchedNamedFeatureGroupsOfSize(int nBatches,
			Boolean includeAmbiguous) {
		Map<Integer, MatchedFeatureGroup> namedMatchGroups = grabMatchedNamedFeatureGroups();
		Map<Integer, MatchedFeatureGroup> namedMatchGroupsOfSize = new HashMap<Integer, MatchedFeatureGroup>();

		for (MatchedFeatureGroup grp : namedMatchGroups.values()) {
			if (grp.getBatches().size() == nBatches)
				if (includeAmbiguous || grp.getFeatureNames().size() == nBatches)
					namedMatchGroupsOfSize.put(grp.getMatchGrpKey(), grp);
		}
		return namedMatchGroupsOfSize;
	}
}

/*
 * private List<FeatureFromFile> unmatchedFeatures; private Map<Integer,
 * MatchedFeatureGroup> matchedFeatureGroups; private Boolean isOrganized =
 * false;
 * 
 * private Double minMass = null, maxMass = null; private Integer nFeatures;
 * 
 * public MassRangeGroup() { super(); isOrganized = false; unmatchedFeatures =
 * new ArrayList<FeatureFromFile>(); matchedFeatureGroups = new HashMap<Integer,
 * MatchedFeatureGroup>(); }
 * 
 * public void addRawFeature(FeatureFromFile f) { if (unmatchedFeatures == null)
 * unmatchedFeatures = new ArrayList<FeatureFromFile>();
 * 
 * unmatchedFeatures.add(f); isOrganized = false; }
 * 
 * 
 * public void organize() { unpack();
 * 
 * Double currMass = null; for (FeatureFromFile f : unmatchedFeatures) {
 * currMass = f.getMass();
 * 
 * if (minMass == null) minMass = currMass; if (maxMass == null) maxMass =
 * currMass;
 * 
 * if (currMass > maxMass) maxMass = f.getMass(); if (currMass < minMass)
 * minMass = currMass; } nFeatures = unmatchedFeatures.size();
 * 
 * Map<Integer, List<FeatureFromFile>> featuresByMatchGroupMap = new
 * HashMap<Integer, List<FeatureFromFile>>();
 * 
 * List<FeatureFromFile> newunmatchedFeatures = new
 * ArrayList<FeatureFromFile>(); for (FeatureFromFile f: unmatchedFeatures) { if
 * (f.getRedundancyGroup() == null) { newunmatchedFeatures.add(f); continue; }
 * if (!featuresByMatchGroupMap.containsKey(f.getRedundancyGroup()))
 * featuresByMatchGroupMap.put(f.getRedundancyGroup(), new
 * ArrayList<FeatureFromFile>());
 * 
 * featuresByMatchGroupMap.get(f.getRedundancyGroup()).add(f); }
 * 
 * matchedFeatureGroups.clear(); for (Integer key :
 * featuresByMatchGroupMap.keySet()) { MatchedFeatureGroup newFeatureGroup = new
 * MatchedFeatureGroup(key, featuresByMatchGroupMap.get(key));
 * matchedFeatureGroups.put(key, newFeatureGroup); }
 * 
 * unmatchedFeatures = new ArrayList<FeatureFromFile>(); for (FeatureFromFile f
 * : newunmatchedFeatures) unmatchedFeatures.add(f); isOrganized = true; }
 * 
 * 
 * private Map<Integer, List<FeatureFromFile>>
 * assignMassRangeGroup(List<FeatureFromFile> allFeatures) {
 * 
 * Map<Integer, List<FeatureFromFile>> massRangeMap = new HashMap<Integer,
 * List<FeatureFromFile>> ();
 * 
 * Collections.sort(allFeatures, new FeatureByMassComparator());
 * 
 * Double massGap = Double.NaN;
 * 
 * Integer rangeKey = 0; massRangeMap.put(rangeKey, new
 * ArrayList<FeatureFromFile>());
 * 
 * for (int i = 1; i < allFeatures.size(); i++) {
 * 
 * massRangeMap.get(rangeKey).add(allFeatures.get(i-1));
 * 
 * massGap = Math.abs(allFeatures.get(i).getMass() -
 * allFeatures.get(i-1).getMass()); if (massGap > .02) {
 * massRangeMap.put(++rangeKey, new ArrayList<FeatureFromFile>()); } } return
 * massRangeMap; }
 * 
 * 
 * private void unpack() { for (MatchedFeatureGroup matchGrp :
 * matchedFeatureGroups.values()) { for (FeatureFromFile f :
 * matchGrp.getFeaturesInGroup()) { unmatchedFeatures.add(f); } isOrganized =
 * false; matchedFeatureGroups = new HashMap<Integer, MatchedFeatureGroup>(); }
 * }
 * 
 * 
 * public Map<Integer, MatchedFeatureGroup>
 * grabInterestingFeatureGroups(Map<String, String> targetFeatures) {
 * 
 * if (!isOrganized) organize();
 * 
 * Map<Integer, MatchedFeatureGroup> featureGroupsOfInterest = new
 * HashMap<Integer, MatchedFeatureGroup>();
 * 
 * MatchedFeatureGroup grp = null; for (Integer key :
 * matchedFeatureGroups.keySet()) { grp = matchedFeatureGroups.get(key); if
 * (grp.countMatchedTargetFeatures(targetFeatures) > 4)
 * featureGroupsOfInterest.put(key, grp); } return featureGroupsOfInterest; }
 * 
 * 
 * public List<FeatureFromFile> renewAllFeatures() { unpack();
 * List<FeatureFromFile> allFeatures = new ArrayList<FeatureFromFile>();
 * 
 * for (FeatureFromFile f : unmatchedFeatures) allFeatures.add(f);
 * 
 * for (MatchedFeatureGroup grp : matchedFeatureGroups.values()) for
 * (FeatureFromFile f : grp.featuresInGroup ) allFeatures.add(f);
 * 
 * Collections.sort(allFeatures, new FeatureByRtAndMassComparator()); return
 * allFeatures; }
 * 
 * 
 * public Boolean keepForAnalysis(Map <String, String> targetFeatures) { for
 * (MatchedFeatureGroup grp : matchedFeatureGroups.values()) if
 * (grp.countMatchedTargetFeatures(targetFeatures) > 0) return true; return
 * false; }
 * 
 * public Double getMinMass() { if (!isOrganized) organize(); return
 * this.minMass; }
 * 
 * public Double getMaxMass() { if (!isOrganized) organize(); return
 * this.maxMass; }
 * 
 * public Integer getNFeatures() { if (!isOrganized) organize(); return
 * this.nFeatures; }
 * 
 * public String toString() { StringBuilder sb = new StringBuilder();
 * 
 * sb.append("For mass range group starting at " + (minMass == null ? "unknown"
 * : minMass)); sb.append(BatchMatchConstants.LINE_SEPARATOR);
 * sb.append("and ending at " + (maxMass == null ? "unknown" : maxMass) +
 * " the following feature groups are of interest " );
 * sb.append(BatchMatchConstants.LINE_SEPARATOR);
 * 
 * return ""; //sb.toString(); } }
 * 
 * 
 * //////////////////////////// SCRAP ////////////////////////////////////
 * 
 * /* public void sortAndPrintByRT(BufferedOutputStream bos) throws IOException
 * {
 * 
 * List<FeatureFromFile> allFeatures = renewAllFeatures();
 * 
 * Double massDiff = null, rtNewDiff = null, rtOldDiff = null, shiftDiff = null;
 * FeatureFromFile f = null, prevFeature = null;
 * 
 * for (int i = 0; i < allFeatures.size(); i++) {
 * 
 * StringBuilder sb = new StringBuilder();
 * 
 * f = allFeatures.get(i); if (f.getOldRt() == null) f.setOldRt(f.getRt());
 * 
 * if (i > 0) { prevFeature = allFeatures.get(i-1); massDiff =
 * Math.abs(f.getMass() - prevFeature.getMass()); rtNewDiff = Math.abs(f.getRt()
 * - prevFeature.getRt()); rtOldDiff = Math.abs(f.getOldRt() -
 * prevFeature.getOldRt()); } if (f.getOldRt() == null || f.getRt() == null)
 * shiftDiff = 0.0; else shiftDiff = f.getRt() - f.getOldRt();
 * 
 * String separator = "\t"; int loop = 0;
 * 
 * while (loop < 2) {
 * 
 * sb.append((f.getBatchIdx() == null ? "" : f.getBatchIdx()) + separator);
 * sb.append((f.getRedundancyGroup() == null ? "" : f.getRedundancyGroup()) +
 * separator); sb.append((f.getName() == null ? "" : f.getName()) + separator);
 * sb.append((f.getMass() == null ? "" : String.format("%8.4f", f.getMass())) +
 * separator); sb.append(massDiff == null ? "" : String.format("%5.3f",
 * massDiff) + separator); sb.append((f.getRt() == null ? "" :
 * String.format("%5.3f", f.getRt())) + separator); sb.append((rtNewDiff == null
 * ? "" : String.format("%5.3f", rtNewDiff)) + separator);
 * sb.append((f.getOldRt() == null ? String.format("%5.3f", f.getRt()) :
 * String.format("%5.3f",f.getOldRt()))+ separator); sb.append((rtOldDiff ==
 * null ? "" : String.format("%5.3f", rtOldDiff)) + separator);
 * sb.append((String.format("%5.3f", shiftDiff)) + separator);
 * sb.append((f.getMedianIntensity() == null ? "" : String.format("%f",
 * f.getMedianIntensity())) + separator); sb.append((f.getAnnotation() == null ?
 * "" : f.getAnnotation()) + separator); sb.append((f.getIsotope() == null ? ""
 * : f.getIsotope()) + separator); sb.append((f.getFurtherAnnotation() == null ?
 * "" : f.getFurtherAnnotation()) + separator);
 * 
 * if (loop++ == 0) { System.out.println(sb.toString()); sb = new
 * StringBuilder(); separator = ","; } else { bos.write((sb.toString() +
 * BatchMatchConstants.LINE_SEPARATOR).getBytes()); } } } }
 */
/*
 * 
 * public Integer sortByRTAndWriteExcel(XSSFWorkbook wb, XSSFSheet sheet,
 * Integer rowCt, XSSFCellStyle styleNumeric, XSSFCellStyle styleInteger,
 * XSSFCellStyle styleBoring) throws IOException {
 * 
 * List<FeatureFromFile> allFeatures = renewAllFeatures(); Integer startRow =
 * rowCt, endRow = startRow + allFeatures.size(); Double shiftDiff = null;
 * FeatureFromFile f = null;
 * 
 * sheet.createFreezePane(8, 1); SheetConditionalFormatting sheetCF =
 * sheet.getSheetConditionalFormatting();
 * 
 * ConditionalFormattingRule rule1 =
 * sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.1",
 * ".1"); PatternFormatting patternFmt = rule1.createPatternFormatting();
 * patternFmt.setFillBackgroundColor(IndexedColors.ORANGE.index);
 * 
 * ConditionalFormattingRule rule2 =
 * sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.1",
 * ".1"); PatternFormatting patternFmt2 = rule2.createPatternFormatting();
 * patternFmt2.setFillBackgroundColor(IndexedColors.ORANGE.index);
 * 
 * 
 * ConditionalFormattingRule rule3 =
 * sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "-.01",
 * ".01"); PatternFormatting patternFmt3 = rule3.createPatternFormatting();
 * patternFmt3.setFillBackgroundColor(IndexedColors.RED.index);
 * 
 * CellRangeAddress[] regions = { CellRangeAddress.valueOf("H" + startRow + ":H"
 * + endRow) };
 * 
 * CellRangeAddress[] regions2 = { CellRangeAddress.valueOf("J" + startRow +
 * ":J" + endRow) };
 * 
 * CellRangeAddress[] regions3 = { CellRangeAddress.valueOf("F" + startRow +
 * ":F" + endRow) };
 * 
 * sheetCF.addConditionalFormatting(regions, rule1);
 * sheetCF.addConditionalFormatting(regions2, rule2);
 * sheetCF.addConditionalFormatting(regions3, rule3);
 * 
 * for (int j = 0; j < allFeatures.size(); j++) { f = allFeatures.get(j); if
 * (f.getOldRt() == null) f.setOldRt(f.getRt());
 * 
 * if (f.getOldRt() == null || f.getRt() == null) shiftDiff = 0.0; else
 * shiftDiff = f.getRt() - f.getOldRt();
 * 
 * XSSFRow row = (XSSFRow) sheet.createRow(rowCt); int i = 0; //Freeze
 * createAppropriateRowEntry(rowCt, i++, sheet, rowCt.toString(), styleInteger,
 * styleNumeric, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getBatchIdx() == null ? "" :
 * String.valueOf(f.getBatchIdx()), styleBoring, styleBoring, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getRedundancyGroup() == null ?
 * "" : String.valueOf(f.getRedundancyGroup()), styleBoring, styleBoring,
 * styleBoring);
 * 
 * PoiUtils.createRowEntry(rowCt, i++, sheet, "   " + f.getName(), styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getMass() == null ? "" :
 * String.valueOf(f.getMass()), styleInteger, styleNumeric, styleBoring);
 * 
 * 
 * Integer prev = rowCt -1; XSSFCell formulaCell3 = row.createCell(i++); if
 * (rowCt > 1) formulaCell3.setCellFormula("ABS(E" + (rowCt+1) + "-" + "E" +
 * rowCt + ")"); else formulaCell3.setCellValue("");
 * 
 * formulaCell3.setCellStyle(styleNumeric);
 * 
 * XSSFFormulaEvaluator formulaEvaluator3 =
 * wb.getCreationHelper().createFormulaEvaluator();
 * formulaEvaluator3.evaluateFormulaCell(formulaCell3);
 * 
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getRT() == null ? "" :
 * String.valueOf(f.getRT()), styleInteger, styleNumeric, styleBoring);
 * 
 * XSSFCell formulaCell = row.createCell(i++); if (rowCt > 1)
 * formulaCell.setCellFormula("ABS(G" + (rowCt+1) + "-" + "G" + rowCt + ")");
 * else formulaCell.setCellValue("");
 * 
 * formulaCell.setCellStyle(styleNumeric);
 * 
 * XSSFFormulaEvaluator formulaEvaluator =
 * wb.getCreationHelper().createFormulaEvaluator();
 * formulaEvaluator.evaluateFormulaCell(formulaCell);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, f.getOldRt() == null ?
 * String.valueOf(f.getRt()) : String.valueOf(f.getOldRt()), styleInteger,
 * styleNumeric, styleBoring);
 * 
 * 
 * 
 * XSSFCell formulaCell2 = row.createCell(i++); if (rowCt > 1)
 * formulaCell2.setCellFormula("ABS(I" + (rowCt +1) + "-" + "I" + rowCt + ")");
 * else formulaCell2.setCellValue("");
 * 
 * formulaCell2.setCellStyle(styleNumeric);
 * 
 * XSSFFormulaEvaluator formulaEvaluator2 =
 * wb.getCreationHelper().createFormulaEvaluator();
 * formulaEvaluator2.evaluateFormulaCell(formulaCell2);
 * 
 * //createAppropriateRowEntry(rowCt, i++, sheet, rtOldDiff == null ? "" : //
 * String.valueOf(rtOldDiff), // styleInteger, styleNumeric, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, shiftDiff == null ? "" :
 * String.valueOf(shiftDiff), styleInteger, styleNumeric, styleBoring);
 * 
 * createAppropriateRowEntry(rowCt, i++, sheet, (f.getMedianIntensity() == null
 * ? "" : String.valueOf(Math.round(f.getMedianIntensity()))), styleInteger,
 * styleNumeric, styleBoring);
 * 
 * PoiUtils.createRowEntry(rowCt, i++, sheet, f.getAnnotation(), styleBoring);
 * PoiUtils.createRowEntry(rowCt, i++, sheet, f.getIsotope(), styleBoring);
 * PoiUtils.createRowEntry(rowCt, i++, sheet,
 * StringUtils.isEmptyOrNull(f.getFurtherAnnotation()) ? "" :
 * f.getFurtherAnnotation() + ", " + f.getPossibleRedundancies(), styleBoring);
 * 
 * rowCt++; } return rowCt; }
 */
