package edu.umich.mrc2.batchmatch.main;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.umich.mrc2.batchmatch.gui.BatchMatchMainWindow;
import edu.umich.mrc2.batchmatch.main.config.FilePreferencesFactory;
import edu.umich.mrc2.batchmatch.preferences.AppData;

public class BatchMatch {

	private static AppData appData = null;
	private static Logger logger = LogManager.getLogger(BatchMatch.class.getName());
	private static BatchMatchMainWindow mainWindow;
	public static final String homeDirLocation = ".";
	public static final File lockFile = 
			Paths.get(homeDirLocation, "app.lock").toFile();
	public static final File configDir = 
			Paths.get(homeDirLocation, BatchMatchConstants.CONFIGURATION_DIRECTORY).toFile();
	public static final File configFile = 
			Paths.get(configDir.getAbsolutePath(), "BatchMatchConfig.txt").toFile();
	public static final File tmpDir = 
			Paths.get(homeDirLocation, BatchMatchConstants.TMP_DIRECTORY).toFile();

	public static void main(String[] args) {

		logger.info("Starting BatchMatch");
		
		//	Prevent second copy running
//		FileLock lock = null;
//		try (FileChannel fc = FileChannel.open(lockFile.toPath(),
//	            StandardOpenOption.CREATE,
//	            StandardOpenOption.WRITE)){		    
//		    lock = fc.tryLock();
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
//	    if (lock == null) {
//	        System.out.println("Another instance of the software is already running");
//	        System.exit(1);
//	    }	 
		ensureConfigDirectoryExists();
		ensureTempDirectoryExists();
		
		System.setProperty("java.util.prefs.PreferencesFactory", 
				FilePreferencesFactory.class.getName());		
		System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, configFile.getAbsolutePath());
		
		mainWindow = new BatchMatchMainWindow();
		mainWindow.setVisible(true);
	}
	
	public static void shutDown() {
		
		mainWindow.dispose();
		System.gc();
		System.exit(0);
	}
	
	private static void ensureConfigDirectoryExists() {
				
		if (!configDir.exists()) {
			try {
				configDir.mkdirs();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}
	}	

	private static void ensureTempDirectoryExists() {

		if (!tmpDir.exists()) {
			try {
				tmpDir.mkdirs();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	public static AppData getAppData() {
		if (appData == null) {
			appData = new AppData();
		}
		return appData;
	}

	public static Logger getLogger() {
		return logger;
	}
}
