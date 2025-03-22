////////////////////////////////////////////////////////
// PostProcessUtils.java
// Written by Bill Duren
// September 2017
////////////////////////////////////////////////////////////

package edu.umich.med.mrc2.batchmatch.utils.orig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;

public class PostProcessUtils {

	public static String getTempDirectoryName() {
		return BatchMatch.tmpDir.getAbsolutePath();
	}

	public static String getBinnerProperty(String filename, String key) {
		return readBinnerSettingsFromFile(filename).getProperty(key);
	}
	
	public static void setBinnerProperty(String filename, String key, String value) {
		Properties props = readBinnerSettingsFromFile(filename);
		props.setProperty(key, value);
		writeBinnerSettingsToFile(filename, props);
	}

	public static <T> T getBinnerObjectProperty(String filename, String key, Class<T> objectType) {
		
		ObjectMapper mapper = new ObjectMapper();
		String rawValue = getBinnerProperty(filename, key);
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

	public static void setBinnerObjectProperty(String filename, String key, Object objValue) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			setBinnerProperty(filename, key, mapper.writeValueAsString(objValue));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public static Properties readBinnerSettingsFromFile(String filename) {

		File file = Paths.get(
				BatchMatchConstants.HOME_DIRECTORY, 
				BatchMatchConstants.CONFIGURATION_DIRECTORY, filename).toFile();
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			fis = new FileInputStream(file);
			props.loadFromXML(fis);
		} catch (InvalidPropertiesFormatException ipfe) {
			ipfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
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

	public static void writeBinnerSettingsToFile(String filename, Properties props) {
		
		File file = Paths.get(
				BatchMatchConstants.HOME_DIRECTORY, 
				BatchMatchConstants.CONFIGURATION_DIRECTORY, filename).toFile();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			props.storeToXML(fos, null);
			fos.flush();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
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
