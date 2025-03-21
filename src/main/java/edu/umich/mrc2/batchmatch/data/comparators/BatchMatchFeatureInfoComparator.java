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

package edu.umich.mrc2.batchmatch.data.comparators;

import edu.umich.mrc2.batchmatch.data.BatchMatchFeatureInfo;

public class BatchMatchFeatureInfoComparator extends ObjectCompatrator<BatchMatchFeatureInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatchMatchFeatureInfoComparator(SortProperty property, SortDirection direction) {
		super(property, direction);
	}

	public BatchMatchFeatureInfoComparator(SortProperty property) {
		super(property);
	}
	
	@Override
	public int compare(BatchMatchFeatureInfo p1, BatchMatchFeatureInfo p2) {

		int result = 0;

		switch (property) {

			case Intensity:
				result = Double.compare(p1.getIntensity(), p2.getIntensity());
	
				if (direction == SortDirection.ASC)
					return result;
				else
					return -result;

			case MZ:
				result = Double.compare(p1.getMass(), p2.getMass());
	
				if (direction == SortDirection.ASC)
					return result;
				else
					return -result;
				
			case RT:
				result = Double.compare(p1.getObservedRt(), p2.getObservedRt());
	
				if (direction == SortDirection.ASC)
					return result;
				else
					return -result;
				
			default:
				break;
		}
		throw (new IllegalStateException());
	}
}
