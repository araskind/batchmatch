////////////////////////////////////////////////////////
//BatchMatchDataSetSummaryCSVWriter.java
//Written by Jan Wigginton and Bill Duren
//September 2019
////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.io.sheetwriters;

public class BatchMatchDataSetSummaryScratch {
	/*
	 * 
	 * public BatchMatchDataSetSummaryScratch() {}
	 * 
	 * private Integer nFullMatches = 0, nAmbiguousFullMatches = 0;
	 * 
	 * private Integer getMinimumBatchSize(PostProcessDataSet data) { Integer minCt
	 * = Integer.MAX_VALUE;
	 * 
	 * Map<Integer, Integer> ctByBatchMap = new HashMap<Integer, Integer>();
	 * 
	 * for (FeatureFromFile f : data.getFeatures()) { if
	 * (!ctByBatchMap.containsKey(f.getBatchIdx()))
	 * ctByBatchMap.put(f.getBatchIdx(), 0);
	 * 
	 * int currCt = ctByBatchMap.get(f.getBatchIdx()); currCt++;
	 * ctByBatchMap.put(f.getBatchIdx(), currCt); }
	 * 
	 * for (Integer ct : ctByBatchMap.values()) { if (ct < minCt) minCt = ct; }
	 * return minCt; }
	 * 
	 * public void runLimitDiagnosstics(PostProcessDataSet data) { Integer
	 * minBatchSize = this.getMinimumBatchSize(data);
	 * 
	 * System.out.println(); System.out.println("Dataset characteristics : ");
	 * System.out.println("-----------------------------------");
	 * System.out.println(); runDiagnostics(data.getFeatures(), 1.5, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .1, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .05, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .01, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .009, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .008, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .007, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .006, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .005, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .004, minBatchSize);
	 * runDiagnostics(data.getFeatures(), .003, minBatchSize); }
	 * 
	 * /* public Map<String, List<RtPair>> writeSummaryToFile(PostProcessDataSet
	 * data, String outputFileName, Integer minDesertSize) {
	 * 
	 * if (data == null) return null;
	 * 
	 * //runLimitDiagnosstics(data);
	 * 
	 * File outputFile = new File(outputFileName);
	 * 
	 * List<Integer> batchList = data.getSortedUniqueBatchIndices(); Integer
	 * maxBatch = batchList.get(batchList.size() - 1); Integer minBatch =
	 * batchList.get(0);
	 * 
	 * StringBuilder sb = new StringBuilder(); //System.out.println("Writing " );
	 * System.out.println(); sb.append("MATCH GROUP, "); for (int i = 1; i <=
	 * maxBatch; i++) sb.append("BATCH " + i + ","); sb.append("# BATCHES MATCHED" +
	 * ", "); sb.append("# FEATURES" + ", ");
	 * 
	 * sb.append("AVG RT" + ", "); sb.append("AVG MONOISOTOPIC M/Z" + ", ");
	 * 
	 * sb.append(BinnerConstants.LINE_SEPARATOR); Map<String, List<Integer>>
	 * matchGroupToBatchIdsMap = data.buildMatchGroupToBatchIdsMap(false);
	 * Map<String, List<String>> redundancyGroupToFeatureNamesMap =
	 * data.buildMatchGroupToFeatureNamesMap(false);
	 * 
	 * data.buildAvgRtMassByMatchGrpMap(false); Map<Integer, Double> avgRts =
	 * data.getAvgRtsByMatchGrp(); Map<Integer, Double> avgMasses =
	 * data.getAvgMassesByMatchGrp();
	 * 
	 * // Map<Double,List<String>> matchGroupsByAverageRtMap = new HashMap<Double,
	 * List<String>>();
	 * 
	 * int idx = 0, fullMatches = 0, ambiguousFullMatches = 0; Map<String, Integer>
	 * offByOneMatchGroups = new HashMap<String, Integer>(); Map<String, Integer>
	 * offByTwoMatchGroups = new HashMap<String, Integer>();
	 * 
	 * for (String matchGroupName : matchGroupToBatchIdsMap.keySet()) {
	 * 
	 * List<Integer> batchIdsForGroup = matchGroupToBatchIdsMap.get(matchGroupName);
	 * 
	 * if (batchIdsForGroup.size() < 1) continue;
	 * 
	 * Collections.sort(batchIdsForGroup);
	 * 
	 * idx = 0; sb.append(matchGroupName + ", "); int ct = 0, overallCt = 0; Integer
	 * nextId = idx < batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;
	 * 
	 * for (int i = minBatch; i <= maxBatch; i++) {
	 * 
	 * ct = nextId.equals(i) ? 1 : 0; if (ct > 0) nextId = idx <
	 * batchIdsForGroup.size() ? batchIdsForGroup.get(idx++) : -1;
	 * 
	 * while (nextId.equals(i)) { ct++; nextId = idx < batchIdsForGroup.size() ?
	 * batchIdsForGroup.get(idx++) : -1; } if (ct > 0) { sb.append(ct + ", ");
	 * overallCt++; } else sb.append("-, "); } sb.append(overallCt + ", ");
	 * 
	 * int featureCt = redundancyGroupToFeatureNamesMap.get(matchGroupName).size();
	 * sb.append(featureCt + ", ");
	 * 
	 * if (overallCt == batchList.size() - 1 && featureCt == batchList.size() - 1) {
	 * offByOneMatchGroups.put(matchGroupName, null); }
	 * 
	 * if (overallCt == batchList.size() - 2 && featureCt == batchList.size() - 2) {
	 * offByTwoMatchGroups.put(matchGroupName, null); } if (overallCt ==
	 * batchList.size()) { fullMatches++; if (featureCt > overallCt)
	 * ambiguousFullMatches++; }
	 * 
	 * Integer redInt = null; Double rt = null, mass = null; try { redInt =
	 * Integer.parseInt(matchGroupName); rt = avgRts.get(redInt); mass =
	 * avgMasses.get(redInt); } catch (Exception e) { redInt = null; rt =null; mass
	 * = null; }
	 * 
	 * if (!matchGroupsByAverageRtMap.containsKey(rt))
	 * matchGroupsByAverageRtMap.put(rt, new ArrayList<String>());
	 * matchGroupsByAverageRtMap.get(rt).add(matchGroupName);
	 * 
	 * sb.append(rt == null ? ", " : rt + ", "); sb.append( mass == null ? ", " :
	 * mass + ", ");
	 * 
	 * for (String featureName :
	 * redundancyGroupToFeatureNamesMap.get(matchGroupName)) sb.append(featureName +
	 * "       " );
	 * 
	 * sb.append(BinnerConstants.LINE_SEPARATOR); }
	 * 
	 * try { FileUtils.writeStringToFile(outputFile, sb.toString(), null, false); }
	 * catch (IOException ioe) { ioe.printStackTrace(); return null; }
	 * 
	 * System.out.println("Number of full matches: " + fullMatches);
	 * System.out.println("Number of ambiguous full matches: " +
	 * ambiguousFullMatches);
	 * 
	 * Map<String, List<RtPair>> desertRtMassMap = null;
	 * 
	 * Collections.sort(batchList); if (maxBatch.equals(2) || batchList.size() == 2)
	 * desertRtMassMap =
	 * grabFakeMapFromUnMatchedTargetBatchFeatures(data.getFeatures(),
	 * batchList.get(0), batchList.get(1), minDesertSize); else desertRtMassMap =
	 * createByDesertRtMassMap(offByOneMatchGroups, matchGroupToBatchIdsMap,
	 * matchGroupsByAverageRtMap, avgRts, avgMasses, minDesertSize, maxBatch,
	 * minBatch);
	 * 
	 * Map<String, List<RtPair>> candidateBackTrackPoints =
	 * determineCandidateBackTrackPoints(data, desertRtMassMap, 0.003,
	 * batchList.size() == 2); Map<String, List<RtPair>> backTrackedPairsByBatch =
	 * new HashMap<String, List<RtPair>>();
	 * 
	 * for (String desertKey : candidateBackTrackPoints.keySet()) { String batchKey
	 * = desertKey.substring(0, desertKey.indexOf("_"));
	 * 
	 * if (!backTrackedPairsByBatch.containsKey(batchKey))
	 * backTrackedPairsByBatch.put(batchKey, new ArrayList<RtPair>());
	 * 
	 * List<RtPair> desertPairs = candidateBackTrackPoints.get(desertKey);
	 * Collections.sort(desertPairs, new RtPairComparator());
	 * 
	 * System.out.println("\nCandidate lattice points to fill desert " + desertKey +
	 * " (based on " + desertPairs.size() + " backtracked features).");
	 * System.out.println(
	 * "---------------------------------------------------------------------------------------------------"
	 * );
	 * 
	 * List<RtPair> reportPairs = new ArrayList<RtPair>(); Double lastProjPt = null,
	 * lastDiff = null; for (int w = 0; w < desertPairs.size(); w++) { if
	 * (Math.abs(desertPairs.get(w).getRt2()) < 2.0 || desertPairs.get(w).getRt1() >
	 * 10.0) {
	 * 
	 * String secondStr = String.format("%7.5f", desertPairs.get(w).getRt1() +
	 * desertPairs.get(w).getRt2()); String firstStr = String.format("%7.5f",
	 * desertPairs.get(w).getRt1()); String deltaStr = String.format("%7.5f",
	 * desertPairs.get(w).getRt2());
	 * 
	 * Double projPt = desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2();
	 * Double diff = desertPairs.get(w).getRt2();
	 * 
	 * if (lastProjPt != null && Math.abs(projPt - lastProjPt) < .001) if
	 * (Math.abs(diff - lastDiff) < .002) continue;
	 * 
	 * reportPairs.add(new RtPair(desertPairs.get(w).getRt1(),
	 * desertPairs.get(w).getRt1() + desertPairs.get(w).getRt2()));
	 * 
	 * System.out.println(firstStr + ",  " + secondStr + ", " + deltaStr);
	 * lastProjPt = projPt; lastDiff = diff; } } AnchorFileWriter writer = new
	 * AnchorFileWriter(true); if (maxBatch.equals(2))
	 * writer.outputResults(outputFileName + "_Lattice_" + desertKey + ".csv",
	 * reportPairs, "Batch02", "Batch01"); //else
	 * backTrackedPairsByBatch.get(batchKey).addAll(reportPairs); }
	 * 
	 * writeAnchors(maxBatch, backTrackedPairsByBatch, outputFileName); /*for (int w
	 * = 0; w < 3; w++) System.out.println();
	 * 
	 * if (!maxBatch.equals(2)) { for (String batchKey :
	 * backTrackedPairsByBatch.keySet()) { AnchorFileWriter writer = new
	 * AnchorFileWriter(true); writer.outputResults(outputFileName + "_Lattice_" +
	 * batchKey + ".csv", backTrackedPairsByBatch.get(batchKey), "Batch02",
	 * "Batch01"); } }
	 */
	/*
	 * nFullMatches = fullMatches - ambiguousFullMatches; nAmbiguousFullMatches =
	 * ambiguousFullMatches; return backTrackedPairsByBatch; }
	 *//*
		 * 
		 * private void writeAnchors(Integer maxBatch, Map<String, List<RtPair>>
		 * backTrackedPairsByBatch, String outputFileName ) { for (int w = 0; w < 3;
		 * w++) System.out.println();
		 * 
		 * if (!maxBatch.equals(2)) { for (String batchKey :
		 * backTrackedPairsByBatch.keySet()) { AnchorFileWriter writer = new
		 * AnchorFileWriter(true); writer.outputResults(outputFileName + "_Lattice_" +
		 * batchKey + ".csv", backTrackedPairsByBatch.get(batchKey), "Batch02",
		 * "Batch01"); } } }
		 * 
		 * 
		 * private void runDiagnostics(List<FeatureFromFile> featuresToMap, Double
		 * massTol, Integer minBatchSize) {
		 * 
		 * 
		 * Map<Integer, List<Integer>> featureIndicesByBatch = new HashMap<Integer,
		 * List<Integer>>();
		 * 
		 * Collections.sort(featuresToMap, new FeatureByMassComparator());
		 * 
		 * Integer minBatch = Integer.MAX_VALUE; Integer maxBatch = Integer.MIN_VALUE;
		 * Integer batchIdx; for (int i = 0; i < featuresToMap.size(); i++) {
		 * 
		 * FeatureFromFile f = featuresToMap.get(i); batchIdx = f.getBatchIdx();
		 * 
		 * if (!featureIndicesByBatch.containsKey(batchIdx))
		 * featureIndicesByBatch.put(batchIdx, new ArrayList<Integer>());
		 * 
		 * if (batchIdx < minBatch) minBatch = batchIdx;
		 * 
		 * if (batchIdx > maxBatch) maxBatch = batchIdx;
		 * 
		 * featureIndicesByBatch.get(batchIdx).add(i); }
		 * 
		 * // suspend multibatch to start if (featureIndicesByBatch.keySet().size() > 2)
		 * return;
		 * 
		 * List<Integer> targetIndices = featureIndicesByBatch.get(minBatch);
		 * List<FeatureFromFile> featuresToScreen = new ArrayList<FeatureFromFile>();
		 * for (int i = 0; i < targetIndices.size(); i++)
		 * featuresToScreen.add(featuresToMap.get(targetIndices.get(i)));
		 * 
		 * targetIndices = featureIndicesByBatch.get(maxBatch); List<FeatureFromFile>
		 * candidateMatches = new ArrayList<FeatureFromFile>(); for (int i = 0; i <
		 * targetIndices.size(); i++)
		 * candidateMatches.add(featuresToMap.get(targetIndices.get(i)));
		 * 
		 * 
		 * Collections.sort(featuresToScreen, new FeatureByMassComparator());
		 * Collections.sort(candidateMatches, new FeatureByMassComparator());
		 * 
		 * Double targetMass = null, targetRt = null, candidateRt = null, rtDiff = null;
		 * Integer lastMatch = 0; Integer nMatches = 0, nMatches1 = 0, nMatches2 = 0,
		 * nMatches3 = 0, nMatches4 = 0, nMatches5 = 0;
		 * 
		 * for (int screenIdx = 0; screenIdx < featuresToScreen.size(); screenIdx++) {
		 * targetMass = featuresToScreen.get(screenIdx).getMass(); targetRt =
		 * featuresToScreen.get(screenIdx).getRt();
		 * 
		 * for (int candidateIdx = lastMatch; candidateIdx < candidateMatches.size();
		 * candidateIdx++) { candidateRt = candidateMatches.get(candidateIdx).getMass();
		 * if (Math.abs(targetMass - candidateMatches.get(candidateIdx).getMass()) <
		 * massTol){
		 * 
		 * candidateRt = candidateMatches.get(candidateIdx).getOldRt();
		 * 
		 * rtDiff = Math.abs(candidateRt - targetRt); if (rtDiff < 1.0) nMatches1++; if
		 * (rtDiff < 2.0) nMatches2++; if (rtDiff < 3.0) nMatches3++; if (rtDiff < 4.0)
		 * nMatches4++; if (rtDiff < 5.0) nMatches5++; nMatches++; lastMatch =
		 * candidateIdx + 1; break; } }
		 * 
		 * }
		 * 
		 * String valStr = String.format("%.3f", massTol);
		 * 
		 * System.out.println("Assuming mass tolerance " + massTol + "...");
		 * 
		 * valStr = String.format("%.2f", 100 *(nMatches/ (1.0 * minBatchSize)));
		 * System.out.println("Upper limit on mass matches ignoring RT proximity is " +
		 * nMatches + " features out of " + minBatchSize + " = " + valStr + "%");
		 * 
		 * valStr = String.format("%.2f", 100 *(nMatches1/ (1.0 * minBatchSize)));
		 * System.out.
		 * println("Upper limit on mass matches falling within RT tolerance 1.0 is " +
		 * nMatches1 + " out of " + minBatchSize + " = " + valStr + "%");
		 * 
		 * valStr = String.format("%.2f", 100 *(nMatches2/ (1.0 * minBatchSize)));
		 * System.out.
		 * println("Upper limit on mass matches falling within RT tolerance 2.0 is " +
		 * nMatches2 + " out of " + minBatchSize + " = " + valStr + "%");
		 * 
		 * valStr = String.format("%.2f", 100 *(nMatches3/ (1.0 * minBatchSize)));
		 * System.out.
		 * println("Upper limit on mass matches falling within RT tolerance 3.0 is " +
		 * nMatches3 + " out of " + minBatchSize + " = " + valStr + "%");
		 * 
		 * valStr = String.format("%.2f", 100 *(nMatches4/ (1.0 * minBatchSize)));
		 * System.out.
		 * println("Upper limit on mass matches falling within RT tolerance 4.0 is " +
		 * nMatches4 + " out of " + minBatchSize + " = " + valStr + "%");
		 * 
		 * valStr = String.format("%.2f", 100 *(nMatches5/ (1.0 * minBatchSize)));
		 * System.out.
		 * println("Upper limit on mass matches falling within RT tolerance 5.0 is " +
		 * nMatches5 + " out of " + minBatchSize + " = " + valStr + "%");
		 * 
		 * System.out.println(); System.out.println(); }
		 * 
		 * 
		 * 
		 * private Map<String, List<RtPair>>
		 * determineCandidateBackTrackPoints(PostProcessDataSet data, Map<String,
		 * List<RtPair>> desertRtMassMap, Double massTol, Boolean inPairMode) {
		 * 
		 * Collections.sort(data.getFeatures(), new FeatureByMassComparator());
		 * 
		 * String keyFormat = "%.0f";
		 * 
		 * String valStr = String.format("%.3f", massTol); System.out.println();
		 * System.out.println();
		 * System.out.println("After merging all batches, at mass tolerance " +
		 * String.format("%.3f", massTol) +
		 * " the following unmatched features can be accounted for by backtracking... "
		 * );
		 * 
		 * //System.out.println("For mass tolerance " + valStr +
		 * ", the following backtrack points exist...");
		 * //System.out.println("==============================================");
		 * 
		 * System.out.println(); // Code replaced by this fn call is pasted in Section F
		 * of scrap code Map<Integer, Map<String, List<FeatureFromFile>>>
		 * featuresByBatchAndMassMap = mapFeaturesByBatchAndMass(data.getFeatures(),
		 * inPairMode);
		 * 
		 * //12
		 * 
		 * List<String> orderedDesertKeys =
		 * ListUtils.makeListFromCollection(desertRtMassMap.keySet());
		 * Collections.sort(orderedDesertKeys);
		 * 
		 * Map<String, List<RtPair>> suggestedTweakPoints = new HashMap<String,
		 * List<RtPair>>();
		 * 
		 * for (int i = 0; i < orderedDesertKeys.size(); i++ ) { int dashIdxForKey =
		 * orderedDesertKeys.get(i).indexOf("_"); String currBatchStr =
		 * orderedDesertKeys.get(i).substring(0, dashIdxForKey); Integer sourceBatch =
		 * Integer.parseInt(currBatchStr);
		 * 
		 * List<RtPair> desertPts = desertRtMassMap.get(orderedDesertKeys.get(i));
		 * List<RtPair> suggestedTweakPrs = new ArrayList<RtPair>();
		 * 
		 * if (desertPts.size() > 0) { System.out.println("To fill desert " +
		 * orderedDesertKeys.get(i));
		 * System.out.println("-------------------------------------------"); } int
		 * printed = 0; for (int j = 0; j < desertPts.size(); j++) {
		 * 
		 * Double targetMass = desertPts.get(j).getRt2(); Double targetRt =
		 * desertPts.get(j).getRt1(); String massKey = String.format(keyFormat,
		 * Math.floor(targetMass));
		 * 
		 * List<FeatureFromFile> featuresToScreen =
		 * featuresByBatchAndMassMap.get(sourceBatch).get(massKey);
		 * 
		 * if (featuresToScreen == null) continue;
		 * 
		 * Collections.sort(featuresToScreen, new FeatureByBatchAndMassComparator());
		 * Double deltaMass = null, deltaRt = null;
		 * 
		 * 
		 * for (int k = 0; k < featuresToScreen.size(); k++) {
		 * 
		 * //System.out.println("Redundancy group " +
		 * featuresToScreen.get(k).getRedundancyGroup());
		 * //System.out.println("Target is " + targetBatch + " Batch is " +
		 * featuresToScreen.get(k).getBatchIdx() // + " Mass is " +
		 * featuresToScreen.get(k).getMass() + " Redundancy is " +
		 * featuresToScreen.get(k).getRedundancyGroup());
		 * 
		 * featuresToScreen.get(k).setDeltaMass(Double.POSITIVE_INFINITY);
		 * featuresToScreen.get(k).setDeltaRt(Double.POSITIVE_INFINITY);
		 * 
		 * 
		 * if (!featuresToScreen.get(k).getBatchIdx().equals(sourceBatch)) continue;
		 * 
		 * if (!(featuresToScreen.get(k).getRedundancyGroup() == null)) { //if (k <
		 * featuresToScreen.size() - 1) //
		 * System.out.print(featuresToScreen.get(k).getRedundancyGroup() + ","); //else
		 * // System.out.println(featuresToScreen.get(k).getRedundancyGroup() + ",");
		 * 
		 * continue; }
		 * 
		 * if (featuresToScreen.get(k).getOldRt() == null)
		 * featuresToScreen.get(k).setOldRt(featuresToScreen.get(k).getRt());
		 * 
		 * deltaMass = targetMass - featuresToScreen.get(k).getMass(); deltaRt =
		 * targetRt - featuresToScreen.get(k).getOldRt();
		 * 
		 * if (Math.abs(deltaMass) > massTol) continue; //15 if (Math.abs(deltaRt) > 1.0
		 * && targetRt < 10) continue;
		 * 
		 * BAD if (Math.abs(deltaRt) > 2.0) continue;
		 * 
		 * //String str = String.
		 * format("Adding delta RT for batch %s at Mass %8.4f and RT %8.4f : RT Delta : %8.4f"
		 * , targetBatch, targetMass, targetRt, deltaRt); //System.out.println(str);
		 * //"Adding delta RT for batch " + targetBatch + " at" + deltaMass + " " +
		 * deltaRt); //System.out.println("Redundancy group " +
		 * featuresToScreen.get(k).getRedundancyGroup() + " MR " +
		 * featuresToScreen.get(k).getMatchReplicate());
		 * featuresToScreen.get(k).setDeltaMass(deltaMass);
		 * featuresToScreen.get(k).setDeltaRt(deltaRt); //
		 * System.out.println(targetBatch + ":" + " Deltas are : " + deltaMass + "  " +
		 * deltaRt // + " for " + featuresToScreen.get(k).getMass() + " " +
		 * featuresToScreen.get(k).getRt()); }
		 * 
		 * //for Merge File Collections.sort(featuresToScreen, new
		 * FeatureByAbsDeltaRtComparator());
		 * 
		 * //int nFound = 0; //Boolean foundOne = false; for (int k = 0; k < Math.min(4,
		 * featuresToScreen.size()); k++) { if (featuresToScreen.get(k).getDeltaRt() >
		 * 10.0) continue; //System.out.println("Group was " +
		 * (featuresToScreen.get(k).getRedundancyGroup() == null ? "NULL" :
		 * featuresToScreen.get(k).getRedundancyGroup())); if
		 * (featuresToScreen.get(k).getRedundancyGroup() == null) {//||
		 * featuresToScreen.get(k).getBatchIdx().equals(targetBatch)) {
		 * suggestedTweakPrs.add(new RtPair(featuresToScreen.get(k).getOldRt(),
		 * featuresToScreen.get(k).getDeltaRt())); String targetRtStr =
		 * String.format("%7.5f", targetRt); String deltaRtStr = String.format("%7.5f",
		 * featuresToScreen.get(k).getDeltaRt()); String rtStr = String.format("%7.5f",
		 * featuresToScreen.get(k).getOldRt()); String massStr = String.format("%.4f",
		 * featuresToScreen.get(k).getMass()); String deltaMassStr =
		 * String.format("%5.4f", featuresToScreen.get(k).getDeltaMass()); if
		 * (featuresToScreen.get(k).getDeltaMass() < 1.0) {
		 * System.out.println("Shifting unmatched feature at " + rtStr + " by " +
		 * deltaRtStr + " would account for missed desert feature at RT " + targetRtStr
		 * + " and mass: " + massStr + ". Candidate point falls within " + deltaMassStr
		 * + " daltons of the missed target mass"); // foundOne = true; } //nFound++;
		 * break; } } }
		 * 
		 * suggestedTweakPoints.put(orderedDesertKeys.get(i), suggestedTweakPrs);
		 * System.out.println("\n"); } return suggestedTweakPoints; }
		 * 
		 * // Pair version of createByDesertRtMass Map. Needs to return map with single
		 * key "2-1" (targetBatch (2) - desert id\x for target batch (1)) // pointing
		 * pointing to a list with mass/rt? targets for all unclaimed batch 1 features
		 * private Map<String, List<RtPair>>
		 * grabFakeMapFromUnMatchedTargetBatchFeatures(List<FeatureFromFile>
		 * featuresToMap, Integer targetIdx, Integer sourceIdx, Integer minDesertSize) {
		 * 
		 * Collections.sort(featuresToMap, new FeatureByRtOnlyComparator());
		 * 
		 * List<RtPair> unclaimedTargetBatchMassRtPairs = new ArrayList<RtPair>();
		 * 
		 * int consecutiveCt = 0; List<RtPair> candidatePairs = new ArrayList<RtPair>();
		 * FeatureFromFile f = null;
		 * 
		 * for (int j = 0; j < featuresToMap.size(); j++) { f = featuresToMap.get(j); if
		 * (f.getRedundancyGroup() != null) { if (f.getBatchIdx().equals(targetIdx)) {
		 * consecutiveCt = 0; candidatePairs = new ArrayList<RtPair>(); } continue; }
		 * 
		 * if (!f.getBatchIdx().equals(targetIdx)) continue;
		 * 
		 * consecutiveCt++;
		 * 
		 * if (consecutiveCt > minDesertSize) { for (int i = 0; i <
		 * candidatePairs.size(); i++) {
		 * unclaimedTargetBatchMassRtPairs.add(candidatePairs.get(i)); } consecutiveCt =
		 * 0; candidatePairs = new ArrayList<RtPair>(); candidatePairs.add(new
		 * RtPair(1111111111.0, 111111111111.0)); } else candidatePairs.add(new
		 * RtPair(f.getOldRt() == null ? f.getRT() : f.getOldRt(), f.getMass())); }
		 * //Map<Integer, Map<String, List<FeatureFromFile>>> featuresByBa
		 * 
		 * Map<String, List<RtPair>> fakeDesertRtMassMapForUnclaimedTarget = new
		 * HashMap<String, List<RtPair>>();
		 * fakeDesertRtMassMapForUnclaimedTarget.put(sourceIdx + "_" + targetIdx,
		 * unclaimedTargetBatchMassRtPairs); return
		 * fakeDesertRtMassMapForUnclaimedTarget; }
		 * 
		 * /* private Map<String, List<RtPair>> createByDesertRtMassMap(Map<String,
		 * Integer> offByOneMatchGroups, // Map<String, List<Integer>>
		 * matchGroupToBatchIdsMap, Map<Double, List<String>> matchGroupsByAverageRtMap,
		 * Map<Integer, Double> averageRtsByMatchGroupMap, Map<Integer, Double>
		 * averageMassesByMatchGroupMap, Integer minDesertSize, int maxBatchId, int
		 * minBatchId) {
		 * 
		 * Map<String, List<RtPair>> desertRtMassMap = new HashMap<String,
		 * List<RtPair>>(); List<Double> sortedRts =
		 * ListUtils.makeListFromCollection(matchGroupsByAverageRtMap.keySet());
		 * Collections.sort(sortedRts);
		 * 
		 * List<String> offByOneMatchGroupsSortedByRt = new ArrayList<String>();
		 * 
		 * for (int i = 0; i < sortedRts.size(); i++) {
		 * 
		 * List<String> matchGroupsForRt =
		 * matchGroupsByAverageRtMap.get(sortedRts.get(i));
		 * 
		 * for (int j = 0; j < matchGroupsForRt.size(); j++) { if
		 * (offByOneMatchGroups.containsKey(matchGroupsForRt.get(j))) {
		 * offByOneMatchGroupsSortedByRt.add(matchGroupsForRt.get(j)); } } }
		 * 
		 * Integer missingBatch = null, prevMissingBatch = null, consecutiveCt = 0;;
		 * Double rtForRangeStart = null, rtForRangeEnd = null;
		 * 
		 * List<RtPair> desertPts = new ArrayList<RtPair>(); for (int i = 0; i <
		 * offByOneMatchGroupsSortedByRt.size(); i++) {
		 * 
		 * String matchGroup = offByOneMatchGroupsSortedByRt.get(i);
		 * 
		 * if (matchGroup == null) continue;
		 * 
		 * Integer matchGroupAsInt = Integer.parseInt(matchGroup); Double rtForGroup =
		 * averageRtsByMatchGroupMap.get(matchGroupAsInt); Double massForGroup =
		 * averageMassesByMatchGroupMap.get(matchGroupAsInt);
		 * 
		 * if (rtForRangeStart == null) rtForRangeStart = rtForGroup;
		 * 
		 * List<Integer> batchesForOffByOneGroup =
		 * matchGroupToBatchIdsMap.get(matchGroup);
		 * Collections.sort(batchesForOffByOneGroup);
		 * 
		 * 
		 * 
		 * missingBatch = null;// findMissingBatch(List<Integer> batchesForGroup,
		 * minBatchId, maxBatchId, null);
		 * 
		 * 
		 * 
		 * 
		 * /*missingBatch = null;
		 * 
		 * if (batchesForOffByOneGroup.get(0).equals(minBatchId+1)) missingBatch =
		 * minBatchId; else { for (int j = 1; j < batchesForOffByOneGroup.size(); j++) {
		 * if (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
		 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
		 * 
		 * if (missingBatch == null) missingBatch = maxBatchId;
		 */
	/*
	 * if (prevMissingBatch != null) { if (missingBatch.equals(prevMissingBatch)) {
	 * consecutiveCt++; rtForRangeEnd = rtForGroup; desertPts.add(new
	 * RtPair(rtForGroup, massForGroup)); } else { if (consecutiveCt >=
	 * minDesertSize) { String rtStartStr = String.format("%.4f", rtForRangeStart);
	 * String rtEndStr = String.format("%.4f", rtForRangeEnd); String nextKey =
	 * pullNextKey(desertRtMassMap, prevMissingBatch);
	 * 
	 * System.out.println(nextKey + ". Found a desert for Batch " + prevMissingBatch
	 * + ". Length: " + consecutiveCt + ". RT range: " + rtStartStr + " to " +
	 * rtEndStr);
	 * 
	 * desertRtMassMap.put(nextKey, desertPts);
	 * 
	 * // rtForLastSavedDesert = rtForRangeEnd;
	 * 
	 * // for (int k = 0; k < desertPts.size(); k++) // System.out.println(nextKey +
	 * " Mass " + desertPts.get(k).getRt2() + " RT: " + desertPts.get(k).getRt1());
	 * } consecutiveCt = 0; rtForRangeStart = rtForGroup; desertPts = new
	 * ArrayList<RtPair>(); } } prevMissingBatch = missingBatch; }
	 * 
	 */
	// desertPts = new ArrayList<RtPair>();
	/*
	 * //printed++ for (int i = 0; i < offByOneMatchGroupsSortedByRt.size(); i++) {
	 * 
	 * String matchGroup = offByOneMatchGroupsSortedByRt.get(i);
	 * 
	 * if (matchGroup == null) continue;
	 * 
	 * Integer matchGroupAsInt = Integer.parseInt(matchGroup); Double rtForGroup =
	 * averageRtsByMatchGroupMap.get(matchGroupAsInt); Double massForGroup =
	 * averageMassesByMatchGroupMap.get(matchGroupAsInt); if (rtForGroup <=
	 * rtForLastSavedDesert) continue;
	 * 
	 * desertPts.add(new RtPair(rtForGroup, massForGroup));
	 * 
	 * missingBatch = null;
	 * 
	 * List<Integer> batchesForOffByOneGroup =
	 * matchGroupToBatchIdsMap.get(matchGroup);
	 * Collections.sort(batchesForOffByOneGroup);
	 * 
	 * if (batchesForOffByOneGroup.get(0).equals(2)) missingBatch = 1; else { for
	 * (int j = 1; j < batchesForOffByOneGroup.size(); j++) { if
	 * (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
	 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
	 * 
	 * if (missingBatch == null) missingBatch = maxBatchId;
	 * 
	 * String nextKey = pullNextKey(desertRtMassMap, missingBatch);
	 * desertRtMassMap.put(nextKey, desertPts); desertPts = new ArrayList<RtPair>();
	 * 
	 * System.out.println("Adding end point " + rtForGroup + " and mass  " +
	 * massForGroup); }
	 */

	// return desertRtMassMap;
	// }

	/*
	 * private Integer findMissingBatch(List<Integer> batchesForGroup, Integer
	 * minBatchId, Integer maxBatchId, Integer skipBatchId) {
	 * 
	 * Integer missingBatch = null;
	 * 
	 * List<Integer> newBatchesForGroup = new ArrayList<Integer>(); //int j = 0; for
	 * (int i = 0; i < batchesForGroup.get(i); i++) { if (skipBatchId != null && i
	 * == skipBatchId) continue; newBatchesForGroup.add(batchesForGroup.get(i)); }
	 * 
	 * 
	 * 
	 * if (batchesForGroup.get(0).equals(minBatchId+1)) missingBatch = minBatchId;
	 * else { for (int j = 1; j < batchesForGroup.size(); j++) { if
	 * (batchesForGroup.get(j) - batchesForGroup.get(j-1) > 1) { missingBatch =
	 * batchesForGroup.get(j-1) + 1; break; } } }
	 * 
	 * if (missingBatch == null) missingBatch = maxBatchId;
	 * 
	 * return missingBatch; }
	 * 
	 * 
	 * 
	 * private Map<Integer, Map<String, List<FeatureFromFile>>>
	 * mapFeaturesByBatchAndMass(List<FeatureFromFile> featuresToMap, Boolean
	 * unClaimedOnly) {
	 * 
	 * Collections.sort(featuresToMap, new FeatureByMassComparator());
	 * 
	 * String keyFormat = "%.0f";
	 * 
	 * //System.out.println("Mass tolerance " + massTol); Map<Integer, Map<String,
	 * List<FeatureFromFile>>> featuresByBatchAndMassMap = new HashMap<Integer,
	 * Map<String, List<FeatureFromFile>>>();
	 * 
	 * for (FeatureFromFile f : featuresToMap) { if (unClaimedOnly &&
	 * f.getRedundancyGroup() != null) continue;
	 * 
	 * Integer batchKeyForFeature = f.getBatchIdx(); if
	 * (!featuresByBatchAndMassMap.containsKey(batchKeyForFeature))
	 * featuresByBatchAndMassMap.put(batchKeyForFeature, new HashMap<String,
	 * List<FeatureFromFile>>());
	 * 
	 * Double massKeyAsFloorDbl = Math.floor(f.getMass());
	 * 
	 * String massKeyForFeature = String.format(keyFormat, massKeyAsFloorDbl);
	 * 
	 * Map<String, List<FeatureFromFile>> featuresByMassMap =
	 * featuresByBatchAndMassMap.get(batchKeyForFeature);
	 * 
	 * if (!featuresByMassMap.containsKey(massKeyForFeature))
	 * featuresByMassMap.put(massKeyForFeature, new ArrayList<FeatureFromFile>());
	 * 
	 * featuresByMassMap.get(massKeyForFeature).add(f);
	 * 
	 * // if (massKeyForFeature.equals("177") && f.getBatchIdx().equals(1)) //
	 * System.out.println(f); featuresByBatchAndMassMap.put(batchKeyForFeature,
	 * featuresByMassMap); } return featuresByBatchAndMassMap; }
	 * 
	 * 
	 * String pullNextKey(Map<String, List<RtPair>> desertRtMassMap, Integer
	 * missingBatch) {
	 * 
	 * Integer nCurrDesertsForMissingBatch = 0; for (String key :
	 * desertRtMassMap.keySet()) { if (key.startsWith(missingBatch.toString()))
	 * nCurrDesertsForMissingBatch++; }
	 * 
	 * Integer nextDesert = nCurrDesertsForMissingBatch + 1; String nextKey =
	 * missingBatch.toString() + "_" + nextDesert.toString(); return nextKey; }
	 * 
	 * 
	 * public Integer getnFullMatches() { return nFullMatches; }
	 * 
	 * public Integer getnAmbiguousFullMatches() { return nAmbiguousFullMatches; }
	 */
}

/////////////  Scrap

/*
 * private Map<String, List<RtPair>> doBackTrackForPairs(PostProcessDataSet
 * data, Double massTol, Double rtTol) {
 * 
 * List<FeatureFromFile> unmatchedFeatures = new ArrayList<FeatureFromFile>();
 * 
 * 
 * for (FeatureFromFile f : data.getFeatures()) { if (f.getRedundancyGroup() ==
 * null) unmatchedFeatures.add(f); }
 * 
 * Collections.sort(unmatchedFeatures, new FeatureByMassComparator());
 * 
 * List<MatchedFeatureGroup> possibleMatchedFeatures = new
 * ArrayList<MatchedFeatureGroup>(); FeatureFromFile f1 = null, f2 = null;
 * 
 * int nMatches = 0; Boolean skipNext = false; Integer batchIdx1 = null,
 * batchIdx2 = null; for (int i = 1; i < unmatchedFeatures.size(); i++) {
 * 
 * if (skipNext) continue;
 * 
 * skipNext = false; f1 = unmatchedFeatures.get(i-1); f2 =
 * unmatchedFeatures.get(i);
 * 
 * if (Math.abs(f2.getMass() - f1.getMass()) > massTol) continue;
 * 
 * if (Math.abs(f2.getOldRt() - f1.getOldRt()) > rtTol) continue;
 * 
 * batchIdx1 = f1.getBatchIdx(); batchIdx2 = f2.getBatchIdx();
 * 
 * if (batchIdx1.equals(batchIdx2)) continue;
 * 
 * //skipNext = true;
 * 
 * List<FeatureFromFile> matchPair = new ArrayList<FeatureFromFile>();
 * matchPair.add(batchIdx1.equals(1) ? f1 : f2);
 * matchPair.add(batchIdx1.equals(1) ? f2 : f1);
 * 
 * possibleMatchedFeatures.add(new MatchedFeatureGroup(++nMatches, matchPair));
 * 
 * }
 * 
 * for (int i = 0; i < possibleMatchedFeatures.size(); i++) {
 * 
 * MatchedFeatureGroup currGrp = possibleMatchedFeatures.get(i);
 * System.out.println(currGrp.printMatchInfo2()); }
 * 
 * for (int i = 0; i < 2; i++) System.out.println();
 * System.out.println("Suggested Pairs");
 * System.out.println("================"); System.out.println();
 * 
 * for (int i = 0; i < possibleMatchedFeatures.size(); i++) {
 * 
 * MatchedFeatureGroup currGrp = possibleMatchedFeatures.get(i);
 * 
 * System.out.println(currGrp.printOldRts(true)); }
 * 
 * return null;
 * 
 * }
 */

/*
 * private Map<String, List<RtPair>>
 * createRtMassMapForUnmatchedFeatures(PostProcessDataSet data) {
 * 
 * 
 * List<FeatureFromFile> unMatchedFeatures = new ArrayList<FeatureFromFile>();
 * 
 * Integer firstBatch = null, secondBatch = null; for (FeatureFromFile f :
 * data.getFeatures()) { if (f.getRedundancyGroup() == null) {
 * 
 * if (firstBatch == null) firstBatch = f.getBatchIdx(); else if (secondBatch ==
 * null) secondBatch = f.getBatchIdx(); unMatchedFeatures.add(f); } }
 * 
 * Collections.sort(unMatchedFeatures, new FeatureByRtOnlyComparator());
 * 
 * Map<String, List<FeatureFromFile>> unmatchedFeaturesByBatchAndRTRegionMap =
 * new HashMap<String, List<FeatureFromFile>>();
 * 
 * Double lower = 0.0, upper = 2.0; String regionKey =
 * String.format("%.1f-%.1f", lower, upper);
 * 
 * for (int i = 0; i < unMatchedFeatures.size(); i++) { String
 * regionKeyForFeature = String.format("%d-%s",
 * unMatchedFeatures.get(i).getBatchIdx(), regionKey); if
 * (!unmatchedFeaturesByBatchAndRTRegionMap.containsKey(regionKeyForFeature))
 * unmatchedFeaturesByBatchAndRTRegionMap.put(regionKeyForFeature, new
 * ArrayList<FeatureFromFile>());
 * 
 * }
 * 
 * return null; }
 */

/*
 * private Map<String, List<RtPair>> doNewBackTrackForPairs(PostProcessDataSet
 * data, Double massTol) {
 * 
 * Map<Integer, Map<String, List<FeatureFromFile>>>
 * unClaimedFeaturesByBatchAndMassMap =
 * mapFeaturesByBatchAndMass(data.getFeatures(), true);
 * 
 * Integer targetIdx = 2; Map<String, List<FeatureFromFile>>
 * unclaimedBatch2FeaturesByMassMap =
 * unClaimedFeaturesByBatchAndMassMap.get(targetIdx);
 * 
 * }
 */

/*
 * Scrap code Section F
 * 
 * new HashMap<Integer, Map<String, List<FeatureFromFile>>>();
 * 
 * 
 * 
 * for (FeatureFromFile f : data.getFeatures()) { Integer batchKeyForFeature =
 * f.getBatchIdx(); if
 * (!featuresByBatchAndMassMap.containsKey(batchKeyForFeature))
 * featuresByBatchAndMassMap.put(batchKeyForFeature, new HashMap<String,
 * List<FeatureFromFile>>());
 * 
 * Double massKeyAsFloorDbl = Math.floor(f.getMass());
 * 
 * String massKeyForFeature = String.format(keyFormat, massKeyAsFloorDbl);
 * 
 * Map<String, List<FeatureFromFile>> featuresByMassMap =
 * featuresByBatchAndMassMap.get(batchKeyForFeature);
 * 
 * if (!featuresByMassMap.containsKey(massKeyForFeature))
 * featuresByMassMap.put(massKeyForFeature, new ArrayList<FeatureFromFile>());
 * 
 * featuresByMassMap.get(massKeyForFeature).add(f);
 * featuresByBatchAndMassMap.put(batchKeyForFeature, featuresByMassMap); }
 */