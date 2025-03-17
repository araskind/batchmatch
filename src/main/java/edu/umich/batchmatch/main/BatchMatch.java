package edu.umich.batchmatch.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.umich.batchmatch.gui.BatchMatchMainWindow;
import edu.umich.batchmatch.preferences.AppData;

public class BatchMatch {

	private static AppData appData = null;
	private static Logger logger = LogManager.getLogger(BatchMatch.class.getName());
	private static BatchMatchMainWindow mainWindow;

	public static void main(String[] args) {

		logger.info("Starting BatchMatch");
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
