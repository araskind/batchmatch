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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jdom2.Element;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.data.store.ProjectFields;
import edu.umich.med.mrc2.batchmatch.data.store.XmlStorable;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;
import edu.umich.med.mrc2.batchmatch.utils.ProjectUtils;

public class BatchMatchProject implements XmlStorable{

	private String projectName;
	private Set<BatchMatchInputObject>inputObjects;
	private Map<AlignmentSettings,Object>alignmentSettings;
	
	public BatchMatchProject(String projectName, File parentDirectory) {
		this();
		this.projectName = projectName;
		createProjectDirectoryStructure(parentDirectory);
	}

	private void createProjectDirectoryStructure(File parentDirectory) {
		// TODO Auto-generated method stub
		
	}

	public BatchMatchProject() {
		super();
		inputObjects = new HashSet<BatchMatchInputObject>();		
		initDefaultAlignmentSettings();
	}
	
	private void initDefaultAlignmentSettings() {
		
		alignmentSettings = new TreeMap<AlignmentSettings,Object>();
		alignmentSettings.putAll(ProjectUtils.getDefaultAlignmentSettings());
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

	public Map<AlignmentSettings, Object> getAlignmentSettings() {
		return alignmentSettings;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	//	TODO
	public BatchMatchProject(Element xmlElement) {
		
	}

	@Override
	public Element getXmlElement() {

		Element experimentElement = 
				new Element(ProjectFields.BatchMatchProject.name());
	
		if(projectName != null)
			experimentElement.setAttribute(
					ProjectFields.Name.name(), projectName);

		Element inputObjectsListElement = 
				new Element(ProjectFields.InputObjects.name());
		
		if(inputObjects != null && !inputObjects.isEmpty()) {
			
			for(BatchMatchInputObject ipObj : inputObjects)
				inputObjectsListElement.addContent(ipObj.getXmlElement());
		}	
		experimentElement.addContent(inputObjectsListElement);
		
		Element settingsListElement = 
				new Element(ProjectFields.Settings.name());
		for(Entry<AlignmentSettings, Object>ae :alignmentSettings.entrySet()) {
			
			Element aeElement = new Element(ae.getKey().name());
			if(ae.getValue() instanceof Double) {
				aeElement.setAttribute("value", 
						BatchMatchConfiguration.defaultMzFormat.format((Double)ae.getValue()));
			}
			if(ae.getValue() instanceof MassErrorType) {
				aeElement.setAttribute("value", ((MassErrorType)ae.getValue()).name());
			}
			settingsListElement.addContent(aeElement);
		}		
		experimentElement.addContent(settingsListElement);
		return experimentElement;
	}
}
