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

package edu.umich.med.mrc2.batchmatch.taskcontrol.tasks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.data.LatticeObject;
import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.process.BatchMatchLatticeBuilder;
import edu.umich.med.mrc2.batchmatch.project.AlignmentSettings;
import edu.umich.med.mrc2.batchmatch.project.BatchMatchProject;
import edu.umich.med.mrc2.batchmatch.taskcontrol.AbstractTask;
import edu.umich.med.mrc2.batchmatch.taskcontrol.Task;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskStatus;

public class LatticeGenerationTask extends AbstractTask {
	
	private Map<AlignmentSettings, Object> alignmentSettings;
	private Set<BatchMatchInputObject>inputObjects;
	
	public LatticeGenerationTask(
			Map<AlignmentSettings, Object> alignmentSettings,
			Set<BatchMatchInputObject> inputObjects) {
		super();
		this.alignmentSettings = alignmentSettings;
		this.inputObjects = inputObjects;
	}


	@Override
	public void run() {

		setStatus(TaskStatus.PROCESSING);
		taskDescription = "Running test ...";
		total = 1000;
		processed = 0;
		BatchMatchProject project = BatchMatch.getCurrentProject();
		BatchMatchInputObject target = 
				inputObjects.stream().filter(o -> o.isTargetBatch()).
				findFirst().orElse(null);
		List<BatchMatchInputObject>toPair = 
				inputObjects.stream().filter(o -> !o.isTargetBatch()).
				collect(Collectors.toList());
		total = toPair.size();
		processed = 0;
		for(BatchMatchInputObject iobj : toPair) {
			
			BatchMatchLatticeBuilder latticeBuilder = new BatchMatchLatticeBuilder(
					iobj, 
					target,
					(int)Math.round((Double)alignmentSettings.get(AlignmentSettings.DEFAULT_LATTICE_SIZE)), 
					(double)alignmentSettings.get(AlignmentSettings.MASS_TOLERANCE), 
					(MassErrorType)alignmentSettings.get(AlignmentSettings.MASS_TOLERANCE_TYPE),
					(double)alignmentSettings.get(AlignmentSettings.RT_TOLERANCE));
			latticeBuilder.buildLattice();
			LatticeObject lattice = latticeBuilder.getLatticeObject();
			if(lattice != null)
				project.addLatticeObject(lattice);
			
			processed++;
		}
		setStatus(TaskStatus.FINISHED);
	}

	@Override
	public Task cloneTask() {
		return new LatticeGenerationTask(alignmentSettings, inputObjects);
	}
}
