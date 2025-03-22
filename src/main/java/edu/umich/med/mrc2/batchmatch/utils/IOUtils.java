/*******************************************************************************
 *
 * (C) Copyright 2018-2020 MRC2 (http://mrc2.umich.edu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Alexander Raskind (araskind@med.umich.edu)
 *
 ******************************************************************************/

package edu.umich.med.mrc2.batchmatch.utils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.umich.med.mrc2.batchmatch.data.enums.BinnerExportFields;

public class IOUtils {

	public static Map<BinnerExportFields,Integer>mapBinnerInputFileHeader(String[]header){
		
		Map<BinnerExportFields,Integer>headerMap = new TreeMap<BinnerExportFields,Integer>();
		for(int i=0; i<header.length; i++) {
			
			BinnerExportFields field = BinnerExportFields.getOptionByUIName(header[i].trim());
			if(field != null)
				headerMap.put(field, i);
		}		
		return headerMap;
	}
	
	public static Set<Integer>getPooledSampleIndices(String[]header, String poolIdentifier){
		
		poolIdentifier = poolIdentifier.toLowerCase();
		Set<Integer>poolIds = new TreeSet<Integer>();
		for(int i=0; i<header.length; i++) {
			
			if(header[i].toLowerCase().contains(poolIdentifier))
				poolIds.add(i);
		}		
		return poolIds;		
	}
}
