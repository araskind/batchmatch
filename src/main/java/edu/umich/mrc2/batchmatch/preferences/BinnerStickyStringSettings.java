////////////////////////////////////////////////////
// BinnerStickyStringSettings.java
// Created May 2018
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.preferences;

import java.util.HashMap;
import java.util.Map;

public class BinnerStickyStringSettings {
	
	private Map<String, String> stringSettings;

	public BinnerStickyStringSettings() {
		this.stringSettings = new HashMap<String, String>();
	}

	public BinnerStickyStringSettings(Map<String, String> stringSettings) {
		this.stringSettings = stringSettings;
	}

	public Map<String, String> getStringSettings() {
		return stringSettings;
	}
}
