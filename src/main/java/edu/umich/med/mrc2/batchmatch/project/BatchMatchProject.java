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

package edu.umich.med.mrc2.batchmatch.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Element;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.data.LatticeObject;
import edu.umich.med.mrc2.batchmatch.data.store.BatchMatchInputObjectFields;
import edu.umich.med.mrc2.batchmatch.data.store.LatticeObjectFields;
import edu.umich.med.mrc2.batchmatch.data.store.ProjectFields;
import edu.umich.med.mrc2.batchmatch.data.store.XmlStorable;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchParametersContainer;
import edu.umich.med.mrc2.batchmatch.utils.ProjectUtils;

public class BatchMatchProject implements XmlStorable{

	protected String projectName;
	protected File projectDirectory;
	protected File projectFile;
	protected Set<BatchMatchInputObject>inputObjects;
	protected BatchMatchParametersContainer alignmentSettings;
	protected List<LatticeObject>latticeObjects;
	
	protected static final String VALUE_FIELD = "value";
	
	public BatchMatchProject(String projectName, File parentDirectory) throws IOException {
		this();
		this.projectName = projectName;
		projectDirectory = ProjectUtils.createProjectDirectoryStructure(parentDirectory, projectName);
		
		if(projectDirectory == null) {
			throw new IOException("Failed to create project");
		}
		projectFile = 
				Paths.get(projectDirectory.getAbsolutePath(), 
						projectName.replaceAll("[\\\\/:*?\"<>|]", "-") + "." + 
						BatchMatchConfiguration.BATCH_MATCH_PROJECT_FILE_EXTENSION).toFile();
	}

	private BatchMatchProject() {
		super();
		inputObjects = new HashSet<BatchMatchInputObject>();	
		latticeObjects = new ArrayList<LatticeObject>();
		initDefaultAlignmentSettings();
	}
	
	private void initDefaultAlignmentSettings() {		
		alignmentSettings = new BatchMatchParametersContainer();
	}

	public Set<BatchMatchInputObject> getInputObjects() {
		return inputObjects;
	}
	
	public void addInputObject(BatchMatchInputObject inputObject) {
		inputObjects.add(inputObject);
	}
	
	public void setTargetBatch(BatchMatchInputObject target) {
		
		if(!inputObjects.contains(target))
			inputObjects.add(target);
		
		for(BatchMatchInputObject o : inputObjects) {
			
			if(o.equals(target))
				o.setTargetBatch(true);
			else
				o.setTargetBatch(false);
		}
	}

	public BatchMatchParametersContainer getAlignmentSettings() {
		return alignmentSettings;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public File getRawInputFilesDirectory() {
		 return Paths.get(projectDirectory.getAbsolutePath(), 
				 BatchMatchConfiguration.RAW_INPUT_DATA_DIRECTORY).toFile();
	}
	
	public File getBinnerFilesDirectory() {
		 return Paths.get(projectDirectory.getAbsolutePath(), 
				 BatchMatchConfiguration.BINNER_FILES_DIRECTORY).toFile();
	}	
	
	public File getIterativeResultsDirectory() {
		 return Paths.get(projectDirectory.getAbsolutePath(), 
				 BatchMatchConfiguration.ITERATIVE_ANALYSIS_RESULTS_DIRECTORY).toFile();
	}
		
	public File getFinalResultsDirectory() {
		 return Paths.get(projectDirectory.getAbsolutePath(), 
				 BatchMatchConfiguration.FINAL_RESULTS_DIRECTORY).toFile();
	}

	public BatchMatchProject(Element experimentElement, File projFile) {
		
		this();
		this.projectFile  = projFile;
		projectDirectory = projectFile.getParentFile();
		projectName = experimentElement.getAttributeValue(ProjectFields.Name.name());
		
		//	Settings
		Element settingsElement = 
				experimentElement.getChild(BatchMatchParametersContainer.xmlNode);
		alignmentSettings = new BatchMatchParametersContainer(settingsElement);
		
		//	Input objects
		List<Element>inputObjectElementList = 
				experimentElement.getChild(ProjectFields.InputObjects.name()).
				getChildren(BatchMatchInputObjectFields.BatchMatchInputObject.name());
		if(!inputObjectElementList.isEmpty()) {
			
			for(Element inputObjectElement : inputObjectElementList) {
				
				BatchMatchInputObject ipObj = null;
				try {
					ipObj = new BatchMatchInputObject(inputObjectElement, this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(ipObj != null)
					inputObjects.add(ipObj);
			}
		}
		//	Lattice objects
		List<Element>latticeObjectElementList = 
				experimentElement.getChild(ProjectFields.LatticeObjects.name()).
				getChildren(LatticeObjectFields.LatticeObject.name());
		if(!latticeObjectElementList.isEmpty()) {
			
			for(Element latticeObjectElement : latticeObjectElementList) {
				
				LatticeObject latticeObj = null;
				try {
					latticeObj = new LatticeObject(latticeObjectElement, this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(latticeObj != null)
					latticeObjects.add(latticeObj);
			}
		}
	}
	
	@Override
	public Element getXmlElement() {

		Element experimentElement = 
				new Element(ProjectFields.BatchMatchProject.name());
		experimentElement.setAttribute("version", "1.0.0.0");

		experimentElement.setAttribute(ProjectFields.Name.name(), projectName);
		//	Input elements
		Element inputObjectsListElement = 
				new Element(ProjectFields.InputObjects.name());
		
		if(inputObjects != null && !inputObjects.isEmpty()) {
			
			for(BatchMatchInputObject ipObj : inputObjects)
				inputObjectsListElement.addContent(ipObj.getXmlElement());
		}	
		experimentElement.addContent(inputObjectsListElement);	
		
		//	Settings
		experimentElement.addContent(alignmentSettings.getXmlElement());
		
		//	Lattice elements
		Element latticeObjectsListElement = 
				new Element(ProjectFields.LatticeObjects.name());
		
		if(latticeObjects != null && !latticeObjects.isEmpty()) {
			
			for(LatticeObject latticeObj : latticeObjects)
				latticeObjectsListElement.addContent(latticeObj.getXmlElement());
		}
		experimentElement.addContent(latticeObjectsListElement);
		
		return experimentElement;
	}

	public File getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
	}

	public List<LatticeObject> getLatticeObjects() {
		return latticeObjects;
	}
	
	public void addLatticeObject(LatticeObject newLatticeObject) {
		latticeObjects.add(newLatticeObject);
	}
}
