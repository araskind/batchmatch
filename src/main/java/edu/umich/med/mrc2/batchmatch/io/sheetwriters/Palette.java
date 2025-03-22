////////////////////////////////////////////////////
// Palette.java
// Written by Jan Wigginton, Jan 31, 2017
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Palette implements Serializable {
	private static final long serialVersionUID = -3417284124658204831L;

	public static int VALS_PER_ROW = 10;

	List<PaletteRow> rows;

	public Palette() {
		rows = new ArrayList<PaletteRow>();
	}

	public Palette(List<String> values) {
		makeRows(values, true);
	}

	public Palette(List<String> values, boolean forHTML) {
		makeRows(values, forHTML);
	}

	public int makeRows(List<String> values, Boolean forHTML) {
		int rowStart = 0;

		rows = new ArrayList<PaletteRow>();

		while (rowStart < values.size()) {
			PaletteRow newRow = new PaletteRow("Row." + rows.size());
			for (int i = rowStart; i < rowStart + VALS_PER_ROW; i++)
				if (forHTML)
					newRow.values.add(i < values.size() ? values.get(i) : "");
				else
					newRow.values.add(i < values.size() ? "#" + values.get(i) : "");

			rowStart += VALS_PER_ROW;
			addRow(newRow);
		}

		return rows.size();
	}

	public void addRow(PaletteRow row) {
		rows.add(row);
	}

	public List<PaletteRow> getRows() {
		return rows;
	}

	public PaletteRow getRows(int i) {
		return getRows().get(i);
	}

	public void setRows(List<PaletteRow> newRows) {
		rows = new ArrayList<PaletteRow>();

		for (PaletteRow row : newRows)
			rows.add(row);
	}

}
