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
import java.util.Set;
import java.util.TreeMap;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.utils.ProjectUtils;

public class BatchMatchProject {

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
}
