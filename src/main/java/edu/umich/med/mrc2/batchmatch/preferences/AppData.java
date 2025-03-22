package edu.umich.med.mrc2.batchmatch.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppData {

	private List<File> expFileStore = new ArrayList<File>();
	private File expFile = null;
	private List<File> libFileStore = new ArrayList<File>();
	private File libFile = null;
	private List<File> annotFileStore = new ArrayList<File>();
	private File annotFile = null;

	public List<File> getExpFileStore() {
		return expFileStore;
	}

	public void setExpFileStore(List<File> fileDataStore) {
		this.expFileStore = fileDataStore;
	}

	public File getExpFile() {
		return expFile;
	}

	public void setExpFile(File fileData) {
		this.expFile = fileData;
	}

	public List<File> getLibFileStore() {
		return libFileStore;
	}

	public void setLibFileStore(List<File> fileDataStore) {
		this.libFileStore = fileDataStore;
	}

	public File getLibFile() {
		return libFile;
	}

	public void setLibFile(File fileData) {
		this.libFile = fileData;
	}

	public List<File> getAnnotFileStore() {
		return annotFileStore;
	}

	public void setAnnotFileStore(List<File> fileDataStore) {
		this.annotFileStore = fileDataStore;
	}

	public File getAnnotFile() {
		return annotFile;
	}

	public void setAnnotFile(File fileData) {
		this.annotFile = fileData;
	}
}
