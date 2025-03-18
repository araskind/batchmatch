////////////////////////////////////////////////////
// PaletteRow.java
// Written by Jan Wigginton, Jan 31, 2017
////////////////////////////////////////////////////
package edu.umich.mrc2.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaletteRow implements Serializable {
	private static final long serialVersionUID = -4254814941320598189L;

	String label;
	List<String> values;

	public PaletteRow(String label, List<String> vals) {
		this.label = label;
		values = new ArrayList<String>();
		for (String val : values)
			values.add(val);
	}

	public PaletteRow(String label) {
		this.label = label;
		values = new ArrayList<String>();
	}

	public String getLabel() {
		return label;
	}

	public List<String> getValues() {
		return values;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public String getValues(int i) {
		return values.get(i);
	}

	public void setValues(int i, String value) {
		if (values.size() >= i)
			values.set(i, value);
	}
}

/*
 * 
 * {"FF3D49", "FF3E48", "FF4D47", "FF4247", "FF4446", "FF4645", "FF4845",
 * "FF4A44", "FF4C43", "FF4C43", "FF4E43", "FF5042", "FF5241", "FF5441",
 * "FF5640", "FF583F", "FF5A3F", "FF5C3E", "FF5E3D", "FF603D", "FF623C",
 * "FF643B", "FF683A", "FF6A39", "FF6C39", "FF6E38", "FF7037", "FF7237",
 * "FF7436", "FF7635", "FF7835", "FF7A34", "FF7B33", "FF7D33", "FF7F32",
 * "FF8131", "FF8131", "FF8831", "FF8530", "FF872F", "FF892F", "FF882E"}
 * 
 */