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

package edu.umich.med.mrc2.batchmatch.main;

public enum BMActionCommands {

	//	Task control commands
	SET_HIGH_PRIORITY_COMMAND("Set high priority"),
	SET_NORMAL_PRIORITY_COMMAND("Set normal priority"),
	RESTART_SELECTED_TASK_COMMAND("Restart selected task"),
	CANCEL_SELECTED_TASK_COMMAND("Cancel selected task"),
	CANCEL_ALL_TASKS_COMMAND("Cancel all tasks"),
	
	//	Help menu
	SHOW_ABOUT_DIALOG_COMMAND("About BatchMatch"),
	SHOW_HELP_COMMAND("Program help"),
	
	//	Project
	CREATE_NEW_PROJECT_COMMAND("Create new BatchMatch project"),
	OPEN_PROJECT_COMMAND("Open BatchMatch project"),
	SAVE_AND_CLOSE_PROJECT_COMMAND("Save and close BatchMatch project"),	
	SET_DEFAULT_PROJECT_DIRECTORY_COMMAND("Set default project directory"),
	
	//	Setup
	SELECT_PEAK_AREA_FILES_COMMAND("Select peak area files"),
	SELECT_BINNER_FILES_COMMAND("Select Binner output files"),
	CLEAR_FILE_SELECTION_COMMAND("Clear file selection table"),

	;
	
	private final String name;

	BMActionCommands(String command) {
		this.name = command;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static BMActionCommands getCommandByName(String commandName) {
		
		for(BMActionCommands command : BMActionCommands.values()) {
			
			if(command.name().equals(commandName))
				return command;
		}	
		return null;
	}
}
