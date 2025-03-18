package edu.umich.mrc2.batchmatch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umich.mrc2.batchmatch.main.BinnerConstants;

public class BinnerUtils {
	public static void ensureBinnerConfigDirectoryExists() {
		File directory = new File(
				BinnerConstants.HOME_DIRECTORY
				+ BinnerConstants.FILE_SEPARATOR
				+ BinnerConstants.CONFIGURATION_DIRECTORY);
		if (!directory.exists()) {
			try {
				directory.mkdirs();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	// 0.9.14
	public static String getBinnerProp(String filename, String key) {
		return getBinnerProps(filename).getProperty(key);
	}

	public static <T> T getBinnerObjectProp(String filename, String key, Class<T> objectType) {
		ObjectMapper mapper = new ObjectMapper();
		String rawValue = getBinnerProp(filename, key);

		try {
			return mapper.readValue(rawValue, objectType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setBinnerProp(String filename, String key, String value) {
		Properties props = getBinnerProps(filename);
		props.setProperty(key, value);
		setBinnerProps(filename, props);
	}

	public static void setBinnerObjectProp(String filename, String key, Object objValue) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			setBinnerProp(filename, key, mapper.writeValueAsString(objValue));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public static Properties getBinnerProps(String filename) {
		File file = new File(BinnerConstants.HOME_DIRECTORY + BinnerConstants.FILE_SEPARATOR
				+ BinnerConstants.CONFIGURATION_DIRECTORY + BinnerConstants.FILE_SEPARATOR + filename);
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			fis = new FileInputStream(file);
			props.loadFromXML(fis);
		} catch (InvalidPropertiesFormatException ipfe) {
		} catch (IOException ioe) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException sol) {
				}
			}
		}

		return props;
	}

	public static void setBinnerProps(String filename, Properties props) {
		File file = new File(BinnerConstants.HOME_DIRECTORY + BinnerConstants.FILE_SEPARATOR
				+ BinnerConstants.CONFIGURATION_DIRECTORY + BinnerConstants.FILE_SEPARATOR + filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			props.storeToXML(fos, null);
			fos.flush();
		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException sol) {
				}
			}
		}
	}
}
