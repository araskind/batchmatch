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

package edu.umich.mrc2.batchmatch.main.config;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import edu.umich.mrc2.batchmatch.main.BatchMatch;

/**
 * PreferencesFactory implementation that stores the preferences in a
 * user-defined file. To use it, set the system property
 * <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>
 * <p/>
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the
 * system property <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>
 *
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferencesFactory.java 282 2009-06-18 17:05:18Z david $
 */
public class FilePreferencesFactory implements PreferencesFactory {
	private static final Logger log = Logger.getLogger(FilePreferencesFactory.class.getName());

	private Preferences rootPreferences;
	public static final String SYSTEM_PROPERTY_FILE = 
			"edu.umich.mrc2.batchmatch.main.config.FilePreferencesFactory.preferencesFile";

	public Preferences systemRoot() {
		return userRoot();
	}

	public Preferences userRoot() {

		if (rootPreferences == null) {

			log.finer("Instantiating root preferences");
			rootPreferences = new FilePreferences(null, "");
		}
		return rootPreferences;
	}

	private static File preferencesFile;

	static File getPreferencesFile() {

		if (preferencesFile == null) {

			String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);

			if (prefsFile == null || prefsFile.length() == 0)
				prefsFile = BatchMatch.configFile.getAbsolutePath();

			preferencesFile = new File(prefsFile).getAbsoluteFile();
			log.finer("Preferences file is " + preferencesFile);
		}
		return preferencesFile;
	}
}
