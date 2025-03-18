package edu.umich.mrc2.batchmatch.gui;

import java.util.List;

public class ChecklistParameters {

	private String[] inputList = null;
	private String dialogTitle = null;
	private String panelTitle = null;
	private String propsFile = null;
	private String propsValue = null;

	public String[] getInputList() {
		return inputList;
	}

	public void setInputList(String[] inputList) {
		this.inputList = inputList;
	}

	public void setInputList(List<String> list) {

		if (list == null)
			return;

		this.inputList = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
			inputList[i] = list.get(i);
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public String getPanelTitle() {
		return panelTitle;
	}

	public void setPanelTitle(String panelTitle) {
		this.panelTitle = panelTitle;
	}

	public String getPropsFile() {
		return propsFile;
	}

	public void setPropsFile(String propsFile) {
		this.propsFile = propsFile;
	}

	public String getPropsValue() {
		return propsValue;
	}

	public void setPropsValue(String propsValue) {
		this.propsValue = propsValue;
	}
}
