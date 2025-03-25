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
import java.util.TreeMap;

import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.project.AlignmentSettings;

public class ProjectUtils {

	public static Map<AlignmentSettings,Object> getDefaultAlignmentSettings() {
		
		Map<AlignmentSettings,Object>alignmentSettings = 
				new TreeMap<AlignmentSettings,Object>();
		
		alignmentSettings.put(AlignmentSettings.MASS_TOLERANCE, 5.0d);
		alignmentSettings.put(AlignmentSettings.MASS_TOLERANCE_TYPE, MassErrorType.mDa);
		alignmentSettings.put(AlignmentSettings.RT_TOLERANCE, 1.0d);
		alignmentSettings.put(AlignmentSettings.ANNEALING_STRETCH_FACTOR, 1.7d);
		alignmentSettings.put(AlignmentSettings.MAX_SD_FROM_CURVE, 0.1d);
		alignmentSettings.put(AlignmentSettings.MIN_SEPARATION, 0.1d);
		alignmentSettings.put(AlignmentSettings.EXCLUDE_DELTA_RT_ABOVE, 1.7d);
		alignmentSettings.put(AlignmentSettings.EXCLUDE_DELTA_RT_BELOW, 0.0d);
		
		return alignmentSettings;
	}
}
