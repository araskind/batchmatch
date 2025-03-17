package edu.umich.batchmatch.data;

//Assuming mass tolerance
public class MatchStatusInfo {

	public MatchStatusInfo() {

	}

	/*
	 * public void reportBasics(PostProcessDataSet data) { Map<Integer, Integer>
	 * featureCtsByBatchMap = data.getFeatureCtsByBatchMap();
	 * 
	 * List<Integer> batchNos = data.getSortedUniqueBatchIndices();
	 * 
	 * try { for (int i = 0; i < batchNos.size(); i++) System.out.println("Batch_" +
	 * batchNos.get(i) + "   " + featureCtsByBatchMap.get(batchNos.get(i)));
	 * 
	 * } catch (Exception e) { System.out.println("Can't print stats"); } }
	 */

	/*
	 * public void (PostProcessDataSet data, Boolean reportBasics) {
	 * 
	 * if (reportBasics) reportBasics(data);
	 * 
	 * System.out.println(); System.out.println();
	 * 
	 * Map<String, List<Integer>> matchGrpToBatchIdsMap =
	 * data.buildMatchGroupToBatchIdsMap(true); Map<Integer, Integer>
	 * matchGrpSizesMap = new HashMap<Integer, Integer>();
	 * 
	 * 
	 * Integer currCt = null; for (List<Integer> lst :
	 * matchGrpToBatchIdsMap.values()) { Integer ct = lst.size(); if
	 * (!matchGrpSizesMap.containsKey(ct)) matchGrpSizesMap.put(ct, 0); currCt =
	 * matchGrpSizesMap.get(ct);
	 * 
	 * matchGrpSizesMap.put(ct, currCt + 1); }
	 * 
	 * for (Integer sz : matchGrpSizesMap.keySet())
	 * System.out.println(String.format("%d%5s%d", sz, " ",
	 * matchGrpSizesMap.get(sz))); }
	 */
}
