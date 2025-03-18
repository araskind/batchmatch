/////////////////////////////////////////
// StringListMerger.java
// Written by Jan Wigginton, May 2019
/////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.mrc2.batchmatch.utils.StringUtils;

public class StringListMerger {

	public static Map<Integer, Integer> createColumnMergeMap(List<String> headerList1, List<String> headerList2) {

		String str = null;
		Map<Integer, Integer> mergedColumnMap = new HashMap<Integer, Integer>();
		Map<String, Integer> list1Map = new HashMap<String, Integer>();

		for (int j = 0; j < headerList1.size(); j++) {
			str = headerList1.get(j);
			if (!StringUtils.isEmptyOrNull(str))
				list1Map.put(StringUtils.removeSpaces(str.toLowerCase()), j);
			else if (j > 0)// if (!list1Map.containsKey(""))
				list1Map.put("", j);
		}

		int destinationIdx = headerList1.size();
		String targetStr = null;
		for (int i = 0; i < headerList2.size(); i++) {

			if (!StringUtils.isEmptyOrNull(headerList2.get(i)))
				targetStr = StringUtils.removeSpaces(headerList2.get(i).toLowerCase());
			else
				targetStr = "";

			if (!StringUtils.isEmptyOrNull(targetStr) && list1Map.containsKey(targetStr)) {
				mergedColumnMap.put(i, list1Map.get(targetStr));
			} else
				mergedColumnMap.put(i, destinationIdx++);
		}
		return mergedColumnMap;
	}

	public static List<String> createMergedHeader(Map<Integer, Integer> mergeSetColumnMap,
			List<String> headerToMergeInto, List<String> secondHeader) {

		int maxIdx = headerToMergeInto.size();

		for (Integer origin : mergeSetColumnMap.keySet()) {
			Integer destinationCol = mergeSetColumnMap.get(origin);
			maxIdx = Math.max(maxIdx, destinationCol);
		}

		int nTotalMergeCols = maxIdx + 1; // headerToMergeInto.size();
		List<String> mergedHeader = new ArrayList<String>();
		for (int i = 0; i < nTotalMergeCols; i++)
			mergedHeader.add(null);

		for (int i = 0; i < headerToMergeInto.size(); i++)
			mergedHeader.set(i, headerToMergeInto.get(i));
		for (int i = headerToMergeInto.size(); i < nTotalMergeCols; i++)
			mergedHeader.set(i, "");

		for (int i = 0; i < secondHeader.size(); i++) {
			Integer destinationCol = mergeSetColumnMap.get(i);
			if (destinationCol >= headerToMergeInto.size())
				mergedHeader.set(destinationCol, secondHeader.get(i));
		}

		List<String> finalizedHeader = new ArrayList<String>();
		String prev = "";
		for (int i = 0; i < mergedHeader.size(); i++) {
			String value = mergedHeader.get(i);
			if (i > 0)
				prev = mergedHeader.get(i - 1);

			if (!StringUtils.isEmptyOrNull(value))
				finalizedHeader.add(value);
			else if (i > 0 && !StringUtils.isEmptyOrNull(prev))
				finalizedHeader.add(value);
		}

		return finalizedHeader;
	}

	public static List<String> createMergedStringList(List<String> headerToMergeInto, List<String> secondHeader) {
		Map<Integer, Integer> columnMergeMap = createColumnMergeMap(headerToMergeInto, secondHeader);
		return createMergedHeader(columnMergeMap, headerToMergeInto, secondHeader);
	}
}
