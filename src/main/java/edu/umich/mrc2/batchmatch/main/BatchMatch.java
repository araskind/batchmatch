package edu.umich.mrc2.batchmatch.main;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

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
	public static final File lockFile = Paths.get(homeDirLocation, "app.lock").toFile();
	public static final File configFile = Paths.get(homeDirLocation, "BatchMatchConfig.txt").toFile();

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
		System.setProperty("java.util.prefs.PreferencesFactory", 
				FilePreferencesFactory.class.getName());		
		System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, configFile.getAbsolutePath());
		
		Preferences prefs = 
				Preferences.userRoot().node(BatchMatch.class.getName());
		
		mainWindow = new BatchMatchMainWindow();
		mainWindow.setVisible(true);
	}
	
	public static void shutDown() {
		
		mainWindow.dispose();
		System.gc();
		System.exit(0);
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
