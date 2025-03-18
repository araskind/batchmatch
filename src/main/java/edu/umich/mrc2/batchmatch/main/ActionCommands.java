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

package edu.umich.mrc2.batchmatch.main;

public enum ActionCommands {

	SHOW_ABOUT_DIALOG_COMMAND("About BatchMatch"),
	SHOW_HELP_COMMAND("Program help"),
	;
	
	private final String name;

	ActionCommands(String command) {
		this.name = command;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static ActionCommands getCommandByName(String commandName) {
		
		for(ActionCommands command : ActionCommands.values()) {
			
			if(command.name().equals(commandName))
				return command;
		}	
		return null;
	}
}
