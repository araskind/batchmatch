package edu.umich.batchmatch.io.sheetwriters;

//import edu.umich.wld.ListUtils;

//import edu.umich.wld.RtPair;

public class RTDesertHandler {
}
/*
 * public RTDesertHandler() { }
 * 
 * private Map<String, List<RtPair>> createByDesertRtMassMap(Map<String,
 * Integer> offByOneMatchGroups, // Map<String, List<Integer>>
 * matchGroupToBatchIdsMap, Map<Double, List<String>> matchGroupsByAverageRtMap,
 * Map<Integer, Double> averageRtsByMatchGroupMap, Map<Integer, Double>
 * averageMassesByMatchGroupMap, Integer minDesertSize, int maxBatchId) {
 * 
 * 
 * List<String> offByOneMatchGroupsSortedByRt =
 * getOffByOneMatchGroupsSortedByRt(offByOneMatchGroups,
 * matchGroupsByAverageRtMap);
 * 
 * Map<String, List<RtPair>> desertRtMassMap =
 * createDesertRtMapForOneOffBatches(offByOneMatchGroupsSortedByRt,
 * averageRtsByMatchGroupMap, averageMassesByMatchGroupMap,
 * matchGroupToBatchIdsMap, minDesertSize, maxBatchId);
 * 
 * return desertRtMassMap; }
 * 
 * 
 * private Map<String, List<RtPair>>
 * createDesertRtMapForOneOffBatches(List<String> offByOneMatchGroupsSortedByRt,
 * Map<Integer, Double> averageRtsByMatchGroupMap, Map<Integer, Double>
 * averageMassesByMatchGroupMap, Map<String, List<Integer>>
 * matchGroupToBatchIdsMap, Integer minDesertSize, int maxBatchId) {
 * 
 * Map<String, List<RtPair>> desertRtMassMap = new HashMap<String,
 * List<RtPair>>();
 * 
 * Integer missingBatch = null, prevMissingBatch = null, consecutiveCt = 0;;
 * Double rtForRangeStart = null, rtForRangeEnd = null; List<Integer>
 * missingBatches = null;
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
 * //List<Integer> batchesForOffByOneGroup =
 * matchGroupToBatchIdsMap.get(matchGroup);
 * //Collections.sort(batchesForOffByOneGroup);
 * 
 * missingBatches = determineMissingBatches(
 * matchGroupToBatchIdsMap.get(matchGroup), maxBatchId);
 * 
 * 
 * /* missingBatch = null;
 * 
 * if (batchesForOffByOneGroup.get(0).equals(2)) missingBatch = 1; else { for
 * (int j = 1; j < batchesForOffByOneGroup.size(); j++) { if
 * (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
 * 
 * if (missingBatch == null) missingBatch = maxBatchId;
 * 
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
 * desertRtMassMap.put(nextKey, desertPts); } consecutiveCt = 0; rtForRangeStart
 * = rtForGroup; desertPts = new ArrayList<RtPair>(); } } prevMissingBatch =
 * missingBatch; } return desertRtMassMap; }
 * 
 * 
 * 
 * private List<RtPair> figureItOut(Integer prevMissingBatch, Integer
 * missingBatch, Integer consecutiveCt, Integer minDesertSize, Double
 * rtForRangeEnd, Double rtForGroup, Double massForGroup, List<RtPair>
 * desertPts) {
 * 
 * /* if (prevMissingBatch != null) { if (missingBatch.equals(prevMissingBatch))
 * { consecutiveCt++; rtForRangeEnd = rtForGroup; desertPts.add(new
 * RtPair(rtForGroup, massForGroup)); } else { if (consecutiveCt >=
 * minDesertSize) { String rtStartStr = String.format("%.4f", ); String rtEndStr
 * = String.format("%.4f", rtForRangeEnd); String nextKey =
 * pullNextKey(desertRtMassMap, prevMissingBatch);
 * 
 * System.out.println(nextKey + ". Found a desert for Batch " + prevMissingBatch
 * + ". Length: " + consecutiveCt + ". RT range: " + rtStartStr + " to " +
 * rtEndStr);
 * 
 * desertRtMassMap.put(nextKey, desertPts); } consecutiveCt = 0; rtForRangeStart
 * = rtForGroup; desertPts = new ArrayList<RtPair>(); } } prevMissingBatch =
 * missingBatch; return null; }
 * 
 * 
 * private List<Integer> determineMissingBatches(List<Integer>
 * batchesForOffByOneGroup, Integer maxBatchId ) {
 * 
 * Collections.sort(batchesForOffByOneGroup);
 * 
 * List<Integer> missingBatches = new ArrayList<Integer>();
 * 
 * Integer missingBatch1 = null;
 * 
 * if (batchesForOffByOneGroup.get(0).equals(2)) missingBatch1 = 1; else { for
 * (int j = 1; j < batchesForOffByOneGroup.size(); j++) { if
 * (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
 * missingBatch1 = batchesForOffByOneGroup.get(j-1) + 1; break; } } } if
 * (missingBatch1 == null) { missingBatch1 = maxBatchId; }
 * missingBatches.add(missingBatch1);
 * 
 * if (batchesForOffByOneGroup.size() == maxBatchId - 1) return missingBatches;
 * 
 * Integer missingBatch2 = null;
 * 
 * for (int j = missingBatch1 + 1; j < batchesForOffByOneGroup.size(); j++) { if
 * (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
 * missingBatch2 = batchesForOffByOneGroup.get(j-1) + 1; break; } } if
 * (missingBatch2 == null) { missingBatch2 = maxBatchId; }
 * missingBatches.add(missingBatch2); return missingBatches; }
 * 
 * 
 * //private List<String> getOffByOneMatchGroupsSortedByRt(Map<String, Integer>
 * offByOneMatchGroups, Map<Double, List<String>> matchGroupsByAverageRtMap) {
 * 
 * List<Double> sortedRts =
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
 * offByOneMatchGroupsSortedByRt.add(matchGroupsForRt.get(j)); } } } return
 * offByOneMatchGroupsSortedByRt; }
 * 
 * 
 * private Map<String, List<RtPair>> createByDesertRtMassMap2(Map<String,
 * Integer> offByOneMatchGroups, // Map<String, List<Integer>>
 * matchGroupToBatchIdsMap, Map<Double, List<String>> matchGroupsByAverageRtMap,
 * Map<Integer, Double> averageRtsByMatchGroupMap, Map<Integer, Double>
 * averageMassesByMatchGroupMap, Integer minDesertSize, int maxBatchId) {
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
 * missingBatch = null;
 * 
 * if (batchesForOffByOneGroup.get(0).equals(2)) missingBatch = 1; else { for
 * (int j = 1; j < batchesForOffByOneGroup.size(); j++) { if
 * (batchesForOffByOneGroup.get(j) - batchesForOffByOneGroup.get(j-1) > 1) {
 * missingBatch = batchesForOffByOneGroup.get(j-1) + 1; break; } } }
 * 
 * if (missingBatch == null) missingBatch = maxBatchId;
 * 
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
 * 
 * //desertPts = new ArrayList<RtPair>(); /*printed++ for (int i = 0; i <
 * offByOneMatchGroupsSortedByRt.size(); i++) {
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
 * 
 * return desertRtMassMap; }
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
 * }
 * 
 * 
 * //desertPts = new ArrayList<RtPair>(); /*printed++ for (int i = 0; i <
 * offByOneMatchGroupsSortedByRt.size(); i++) {
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
