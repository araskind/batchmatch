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

package edu.umich.med.mrc2.batchmatch.project;

import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;

public enum AlignmentSettings {

	MASS_TOLERANCE(Double.class),
	MASS_TOLERANCE_TYPE(MassErrorType.class),
	RT_TOLERANCE(Double.class),
	ANNEALING_STRETCH_FACTOR(Double.class),
	MAX_SD_FROM_CURVE(Double.class),
	MIN_SEPARATION(Double.class),
	EXCLUDE_DELTA_RT_ABOVE(Double.class),
	EXCLUDE_DELTA_RT_BELOW(Double.class),
	;
	
	private final Class clazz;

	AlignmentSettings(Class clazz) {
		this.clazz = clazz;
	}

	public Class getClazz() {
		return clazz;
	}
}
