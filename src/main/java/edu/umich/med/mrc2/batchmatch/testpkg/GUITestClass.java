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
import java.nio.file.Paths;

import edu.umich.med.mrc2.batchmatch.main.config.FilePreferencesFactory;

public class GUITestClass {

	public static final String homeDirLocation = ".";
	public static final File configFile = Paths.get(homeDirLocation, "BatchMatchConfig.txt").toFile();
	
	private static TestFrame mainWindow;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("java.util.prefs.PreferencesFactory", 
				FilePreferencesFactory.class.getName());		
		System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, configFile.getAbsolutePath());
		
		mainWindow = new TestFrame();
		mainWindow.setVisible(true);
	}
	
	public static void shutDown() {
		
		mainWindow.dispose();
		System.gc();
		System.exit(0);
	}

}
