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

package edu.umich.med.mrc2.batchmatch.testpkg;

import java.io.File;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.process.BatchMatchLatticeBuilder;

public class FunctionTestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 try {
			testLatticeGeneration();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testLatticeGeneration() {
		
		File areasFileOne = new File("E:\\Development\\BatchMatch\\BatchMatch_Process_Demo_02_11_25\\"
				+ "Unnamed Data from Sasha\\EX01355_RP-POS-BATCH01_4BINNER_20231120_121438.txt");
		BatchMatchInputObject objOne = new BatchMatchInputObject(1, null, areasFileOne, false);

		File areasFileTwo = new File("E:\\Development\\BatchMatch\\BatchMatch_Process_Demo_02_11_25\\"
				+ "Unnamed Data from Sasha\\EX01355_RP-POS-BATCH02_4BINNER_20231125_122836.txt");
		BatchMatchInputObject objTwo = new BatchMatchInputObject(1, null, areasFileTwo, false);
				
		BatchMatchLatticeBuilder builder = 
				new BatchMatchLatticeBuilder(objOne, objTwo, 30, 0.005d, 0.5d);
		builder.buildLattice();
		
	}
}
