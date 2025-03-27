/*******************************************************************************
 *
 * (C) Copyright 2018-2020 MRC2 (http://mrc2.umich.edu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Alexander Raskind (araskind@med.umich.edu)
 *
 ******************************************************************************/

package edu.umich.med.mrc2.batchmatch.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.gui.utils.MessageDialog;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;
import edu.umich.med.mrc2.batchmatch.project.AlignmentSettings;
import edu.umich.med.mrc2.batchmatch.project.BatchMatchProject;

public class ProjectUtils {

	public static Map<AlignmentSettings,Object> getDefaultAlignmentSettings() {
		
		Map<AlignmentSettings,Object>alignmentSettings = 
				new TreeMap<AlignmentSettings,Object>();
		
		alignmentSettings.put(AlignmentSettings.MASS_TOLERANCE, 5.0d);
		alignmentSettings.put(AlignmentSettings.MASS_TOLERANCE_TYPE, MassErrorType.mDa);
		alignmentSettings.put(AlignmentSettings.RT_TOLERANCE, 1.0d);
		alignmentSettings.put(AlignmentSettings.ANNEALING_STRETCH_FACTOR, 1.7d);
		alignmentSettings.put(AlignmentSettings.MAX_SD_FROM_CURVE, 0.1d);
		alignmentSettings.put(AlignmentSettings.MIN_SEPARATION, 0.1d);
		alignmentSettings.put(AlignmentSettings.EXCLUDE_DELTA_RT_ABOVE, 1.7d);
		alignmentSettings.put(AlignmentSettings.EXCLUDE_DELTA_RT_BELOW, 0.0d);
		alignmentSettings.put(AlignmentSettings.DEFAULT_LATTICE_SIZE, 30.0d);
		
		return alignmentSettings;
	}
	
	public static File createProjectDirectoryStructure(File parentDirectory, String projectName) {

		File projectDirectory = 
				Paths.get(parentDirectory.getAbsolutePath(), 
						projectName.replaceAll("\\W+", "-")).toFile();
		try {
			Files.createDirectories(Paths.get(projectDirectory.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showWarningMsg(
					
					"Failed to create project directory", 
					BatchMatch.getMainWindow());
			return null;
		}
		File rawInputFilesDirectory = Paths.get(projectDirectory.getAbsolutePath(), 
				BatchMatchConfiguration.RAW_INPUT_DATA_DIRECTORY).toFile();
		try {
			Files.createDirectories(Paths.get(rawInputFilesDirectory.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showWarningMsg(
					"Failed to create peak area files directory", 
					BatchMatch.getMainWindow());
			return null;
		}
		File binnerFilesDirectory = Paths.get(projectDirectory.getAbsolutePath(), 
				BatchMatchConfiguration.BINNER_FILES_DIRECTORY).toFile();
		try {
			Files.createDirectories(Paths.get(binnerFilesDirectory.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showWarningMsg(
					"Failed to create binner files directory", 
					BatchMatch.getMainWindow());
			return null;
		}
		File iterativeResultsDirectory = Paths.get(projectDirectory.getAbsolutePath(), 
				BatchMatchConfiguration.ITERATIVE_ANALYSIS_RESULTS_DIRECTORY).toFile();
		try {
			Files.createDirectories(Paths.get(iterativeResultsDirectory.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showWarningMsg(
					"Failed to create iterative results directory", 
					BatchMatch.getMainWindow());
			return null;
		}
		File finalResultsDirectory = Paths.get(projectDirectory.getAbsolutePath(), 
				BatchMatchConfiguration.FINAL_RESULTS_DIRECTORY).toFile();
		try {
			Files.createDirectories(Paths.get(finalResultsDirectory.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showWarningMsg(
					"Failed to create final results directory", 
					BatchMatch.getMainWindow());
			return null;
		}
		return projectDirectory;
	}
	
	public static void saveProject(BatchMatchProject project) {
		
		Document document = new Document();
		document.setContent(project.getXmlElement());
        try (FileWriter writer = new FileWriter(project.getProjectFile(), false)){

            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getCompactFormat());
            outputter.output(document, writer);
         } catch (Exception e) {
            e.printStackTrace();
        }	
	}
	
	public static BatchMatchProject readProjectFromFile(File projectFile) {
		
		SAXBuilder sax = new SAXBuilder();
		Document doc = null;
		try {
			doc = sax.build(projectFile);
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;						
		}
		Element experimentElement = doc.getRootElement();
		BatchMatchProject storedProject = null;
		try {
			storedProject = new BatchMatchProject(experimentElement, projectFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return storedProject;
	}
}



















