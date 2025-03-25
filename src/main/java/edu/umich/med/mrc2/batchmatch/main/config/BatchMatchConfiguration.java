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

package edu.umich.med.mrc2.batchmatch.main.config;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

import edu.umich.med.mrc2.batchmatch.preferences.BackedByPreferences;

public class BatchMatchConfiguration implements BackedByPreferences{

	private static final Preferences prefs = 
			Preferences.userRoot().node(BatchMatchConfiguration.class.getName());
	
	public static final String MAX_WORKING_THREADS = "MAX_WORKING_THREADS";
	private static int maxWorkingThreads = 4;
	
	public static final String PROJECT_DIRECTORY = "Projects";
	private static File projectDirectory;
	
	public static File iconsDirectory = Paths.get(".", "Icons").toFile();
	
	public static final DateFormat defaultTimeStampFormat = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat defaultFileTimeStampFormat = 
			new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	public static final NumberFormat defaultMzFormat = new DecimalFormat("#.####");
    public static final NumberFormat defaultRtFormat = new DecimalFormat("#.###");
    public static final NumberFormat defaultPpmFormat = new DecimalFormat("#.##");
	
	public static final String BATCH_MATCH_PROJECT_FILE_EXTENSION = "bmproj";
	public static final String BINNER_FILES_DIRECTORY = "BinnerFiles";
	public static final String RAW_INPUT_DATA_DIRECTORY = "RawInputData";
	public static final String ITERATIVE_ANALYSIS_RESULTS_DIRECTORY = "IntermediateResults";
	public static final String FINAL_RESULTS_DIRECTORY = "FinalResults";
	
	public static final Font panelTitleFont = new Font("Arial", Font.PLAIN, 16);
	public static final Color panelTitleColor = new Color(0, 0, 205);
		
	public BatchMatchConfiguration() {
		super();
		loadPreferences();
	}

	public static void setDefaultProjectDirectory(File newDefaultProjectDirectory) {
		projectDirectory = newDefaultProjectDirectory;
		prefs.put(PROJECT_DIRECTORY, projectDirectory.getAbsolutePath());
	}
	
	@Override
	public void loadPreferences(Preferences preferences) {
		
		projectDirectory = new File(preferences.get(
				PROJECT_DIRECTORY, Paths.get(".", PROJECT_DIRECTORY).toString()));
		if(!projectDirectory.exists()) {
			try {
				projectDirectory.mkdir();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		maxWorkingThreads = preferences.getInt(MAX_WORKING_THREADS, 4);		
	}
	@Override
	public void loadPreferences() {
		loadPreferences(prefs);
	}
	
	@Override
	public void savePreferences() {

		if(projectDirectory != null && projectDirectory.exists())
			prefs.put(PROJECT_DIRECTORY, projectDirectory.getAbsolutePath());
		
		prefs.putInt(MAX_WORKING_THREADS, maxWorkingThreads);
	}

	public static File getProjectDirectory() {
		return projectDirectory;
	}

	public static int getMaxWorkingThreads() {
		return maxWorkingThreads;
	}

	public static void setMaxWorkingThreads(int maxWorkingThreads) {
		BatchMatchConfiguration.maxWorkingThreads = maxWorkingThreads;
		prefs.putInt(MAX_WORKING_THREADS, maxWorkingThreads);
	}
}
