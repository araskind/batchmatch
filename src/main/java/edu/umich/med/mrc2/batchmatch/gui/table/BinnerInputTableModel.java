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

package edu.umich.med.mrc2.batchmatch.gui.table;

import java.io.File;

public class BinnerInputTableModel extends BasicTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String BATCH_NUMBER_COLUMN = "Batch ##";
	public static final String PEAK_AREAS_FILE_COLUMN = "Peak areas file";
	public static final String BINNER_OUTPUT_FILE_COLUMN = "Binner output file";
	public static final String PRIMARY_BATCH_COLUMN = "Primary";
	
	public BinnerInputTableModel() {

		super();

		columnArray = new ColumnContext[] {
			new ColumnContext(BATCH_NUMBER_COLUMN, BATCH_NUMBER_COLUMN, Integer.class, false),	
			new ColumnContext(PEAK_AREAS_FILE_COLUMN, PEAK_AREAS_FILE_COLUMN, File.class, true),
			new ColumnContext(BINNER_OUTPUT_FILE_COLUMN, BINNER_OUTPUT_FILE_COLUMN, File.class, true),
			new ColumnContext(PRIMARY_BATCH_COLUMN, PRIMARY_BATCH_COLUMN, Boolean.class, true),
		};
	}
	
	
}
