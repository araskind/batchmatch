package edu.umich.mrc2.batchmatch.utils.orig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class RecentSelectionUtils {

	public static Properties getRecentlySelected(String propsFile) {
		String fileLocation = System.getProperty("user.home") + File.separator + propsFile;
		FileInputStream in = null;
		Properties props = new Properties();
		try {
			in = new FileInputStream(fileLocation);
			props.loadFromXML(in);
		} catch (InvalidPropertiesFormatException ipfe) {
		} catch (IOException ioe) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException sol) {
				}
			}
		}
		return props;
	}

	public static void setRecentlySelected(Properties props, String propsFile) {
		String fileLocation = System.getProperty("user.home") + File.separator + propsFile;
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(fileLocation);
			props.storeToXML(os, null);
			os.flush();
		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException sol) {
				}
			}
		}
	}
}
