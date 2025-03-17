////////////////////////////////////////////////////
// BinnerOutput.java
// Created Jan 24, 2017
////////////////////////////////////////////////////
package edu.umich.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.umich.batchmatch.data.BinnerGroup;

public class BinnerOutput implements Serializable {
	private static final long serialVersionUID = -9094076418500095505L;

	private String tabName;
	private Integer originalSheetIdx;
	private List<String> headers;
	private List<BinnerGroup> groups;
	private int nGroups, dataStartCol, firstBlankCol, secondBlankCol;

	public BinnerOutput() {
		groups = new ArrayList<BinnerGroup>();
		headers = new ArrayList<String>();
		nGroups = 0;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public Integer getOriginalSheetIdx() {
		return originalSheetIdx;
	}

	public void setOriginalSheetIdx(Integer originalSheetIdx) {
		this.originalSheetIdx = originalSheetIdx;
	}

	public void addGroup(BinnerGroup group) {
		groups.add(group);
		nGroups = groups.size();
	}

	public Integer getNGroups() {
		return nGroups;
	}

	public void setNGroups(int nGroups) {
		this.nGroups = nGroups;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public List<BinnerGroup> getGroups() {
		return groups;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public void setGroups(List<BinnerGroup> groups) {
		this.groups = groups;
	}

	public Integer getGroupCount() {
		return groups.size();
	}

	public int getDataStartCol() {
		return dataStartCol;
	}

	public void setDataStartCol(int dataStartCol) {
		this.dataStartCol = dataStartCol;
	}

	public int getFirstBlankCol() {
		return firstBlankCol;
	}

	public int getSecondBlankCol() {
		return secondBlankCol;
	}

	public void setFirstBlankCol(int firstBlankCol) {
		this.firstBlankCol = firstBlankCol;
	}

	public void setSecondBlankCol(int secondBlankCol) {
		this.secondBlankCol = secondBlankCol;
	}
}
