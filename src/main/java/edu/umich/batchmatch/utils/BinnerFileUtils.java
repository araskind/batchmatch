package edu.umich.batchmatch.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
//BinnerFileUtils.getFile
import javax.swing.filechooser.FileSystemView;

public class BinnerFileUtils {
	public static int LOAD = 1;
	public static int SAVE = 2;
	public static int DIR = 3;

	private static File previousFile = null;

	public static File getFile(String title, int load_save, String startFile) {
		return getFile(title, load_save, null, null, startFile);
	}

//	public static File[] getFiles()
	public static File getFile(String title, int load_save, String extension, String description, String startFile) {
		JFileChooser chooser = new JFileChooser();
		if (startFile != null) {
			File f = new File(startFile);
			if (f.isDirectory())
				chooser.setCurrentDirectory(f);
			else
				chooser.setSelectedFile(f);
		}

		int returnVal = JFileChooser.CANCEL_OPTION;
		File selectedFile = null;
		chooser.setDialogTitle(title);
		// chooser.setCurrentDirectory(previousFile);
		if (extension != null) {
			chooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extension);
			chooser.setFileFilter(filter);
		}
		if (load_save == LOAD) {
			do {
				returnVal = chooser.showOpenDialog(null);
				selectedFile = chooser.getSelectedFile();
			} while (returnVal == JFileChooser.APPROVE_OPTION && !selectedFile.exists());
		} else if (load_save == SAVE) {
			do {
				returnVal = chooser.showSaveDialog(null);
				selectedFile = chooser.getSelectedFile();
				if (selectedFile == null) {
					return null;
				}
				if (extension != null && !selectedFile.getName().endsWith("." + extension)) {
					selectedFile = new File(selectedFile.getPath() + "." + extension);
				}
				if (returnVal == JFileChooser.APPROVE_OPTION && selectedFile.exists()) {
					int answer = JOptionPane.showConfirmDialog(chooser,
							"The file '" + selectedFile.getName()
									+ "' already exists. Are you sure you want to overwrite it?",
							"File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (answer == JOptionPane.YES_OPTION) {
						break;
					}
				}
			} while (returnVal == JFileChooser.APPROVE_OPTION && selectedFile.exists());
		} else if (load_save == DIR) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnVal = chooser.showSaveDialog(null);
			selectedFile = chooser.getSelectedFile();
			if (selectedFile == null) {
				return null;
			}
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			previousFile = selectedFile;
			return selectedFile;
		}
		return null;
	}

	public static ArrayList<String> choseMultipleFiles() {
		return choseMultipleFiles(null, null, "");
	}

	public static ArrayList<String> choseMultipleFiles(String startDirectory, String extension, String description) {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Multiple file and directory selection:");
		jfc.setMultiSelectionEnabled(true);
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		if (extension != null) {
			jfc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extension);
			jfc.setFileFilter(filter);
		}
		if (startDirectory != null)
			jfc.setCurrentDirectory(new File(startDirectory));

		ArrayList<String> arrayList = null;
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File[] files = jfc.getSelectedFiles();
			System.out.println("Directories found\n");
			// Arrays.asList(files).forEach(x -> {
			// if (x.isDirectory()) {
			// System.out.println(x.getName());
			// }
			// });
			// System.out.println("\n- - - - - - - - - - -\n");
			System.out.println("Files Found\n");

			// for (int i = 0; i < files.length; i++) {
			//
			// }
			// List<File> filesAsList = Arrays.asList(files);

			arrayList = new ArrayList<String>();
			for (File f : Arrays.asList(files)) {
				if (f.isFile()) {
					System.out.println(f.getAbsolutePath());
					arrayList.add(f.getAbsolutePath());
				}

			}
		}
		return arrayList;
	}
}
