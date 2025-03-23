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

package edu.umich.med.mrc2.batchmatch.taskcontrol.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.umich.med.mrc2.batchmatch.gui.BatchMatchMainWindow;
import edu.umich.med.mrc2.batchmatch.gui.GuiUtils;
import edu.umich.med.mrc2.batchmatch.gui.table.BasicTable;
import edu.umich.med.mrc2.batchmatch.main.BMActionCommands;
import edu.umich.med.mrc2.batchmatch.main.BatchMatch;
import edu.umich.med.mrc2.batchmatch.taskcontrol.Task;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskController;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskPriority;
import edu.umich.med.mrc2.batchmatch.taskcontrol.TaskStatus;
import edu.umich.med.mrc2.batchmatch.taskcontrol.impl.WrappedTask;

public class TaskProgressPanel extends JPanel implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -6250584696801682393L;

	private BasicTable taskTable;
	private JPopupMenu popupMenu;
	private JMenu priorityMenu;

	private JMenuItem
		cancelTaskMenuItem,
		cancelAllMenuItem,
		highPriorityMenuItem,
		normalPriorityMenuItem;

	private JMenuItem restartMenuItem;

	private JMenuItem clearQueueMenuItem;

	/**
	 * Constructor
	 */
	public TaskProgressPanel(TaskController taskController) {

		super();
		// setPreferredSize(new Dimension(300, 200));
		// setSize(new Dimension(300, 200));
		setAlignmentY(Component.TOP_ALIGNMENT);
		setAlignmentX(Component.LEFT_ALIGNMENT);

		taskTable = new BasicTable(taskController.getTaskQueue());
		taskTable.setAlignmentY(Component.TOP_ALIGNMENT);
		taskTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		taskTable.setCellSelectionEnabled(false);
		taskTable.setColumnSelectionAllowed(false);
		taskTable.setRowSelectionAllowed(true);
		taskTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		taskTable.setDefaultRenderer(JComponent.class, new ComponentCellRenderer());
		taskTable.getTableHeader().setReorderingAllowed(false);
		taskTable.setAutoCreateRowSorter(false);
		taskTable.setRowSorter(null);
		setLayout(new BorderLayout(0, 0));

		JScrollPane jJobScroll = new JScrollPane(taskTable);
		jJobScroll.setAlignmentY(Component.TOP_ALIGNMENT);
		jJobScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		jJobScroll.setViewportView(taskTable);
		add(jJobScroll);

		// Create popup menu and items
		popupMenu = new JPopupMenu();

		cancelTaskMenuItem = GuiUtils.addMenuItem(popupMenu,
				BMActionCommands.CANCEL_SELECTED_TASK_COMMAND.getName(), this,
				BMActionCommands.CANCEL_SELECTED_TASK_COMMAND.getName());

		cancelAllMenuItem = GuiUtils.addMenuItem(popupMenu,
				BMActionCommands.CANCEL_ALL_TASKS_COMMAND.getName(), this,
				BMActionCommands.CANCEL_ALL_TASKS_COMMAND.getName());

		restartMenuItem = GuiUtils.addMenuItem(popupMenu,
				BMActionCommands.RESTART_SELECTED_TASK_COMMAND.getName(), this,
				BMActionCommands.RESTART_SELECTED_TASK_COMMAND.getName());

		// Add popup menu to the task table
		taskTable.setComponentPopupMenu(popupMenu);

		// Set the width for first column (task description)
		taskTable.getColumnModel().getColumn(0).setPreferredWidth(350);

		// Set position and size
		setBounds(20, 20, 600, 150);
	}

	public Task[] getSelectedTasks() {

		WrappedTask currentQueue[] = 
				BatchMatch.getTaskController().getTaskQueue().getQueueSnapshot();
		ArrayList<Task>selected = new ArrayList<Task>();

		int[] selectedRows = taskTable.getSelectedRows();

		for (int i : selectedRows) {

			if ((i < currentQueue.length) && (i >= 0))
				selected.add(currentQueue[taskTable.convertRowIndexToModel(i)].getActualTask());
		}
		return selected.toArray(new Task[selected.size()]);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {

		String command = event.getActionCommand();

		if (command.equals(BMActionCommands.CANCEL_ALL_TASKS_COMMAND.getName())) {

			BatchMatch.getTaskController().cancelAllTasks();
			BatchMatchMainWindow.hideProgressDialog();
			return;
		}
		else {
			Task[] selectedTasks = getSelectedTasks();

			for (Task t : selectedTasks) {

				if (t != null) {

					if (command.equals(BMActionCommands.CANCEL_SELECTED_TASK_COMMAND.getName())){

						if ((t.getStatus() == TaskStatus.WAITING) || (t.getStatus() == TaskStatus.PROCESSING))
							t.cancel();
					}
					if (command.equals(BMActionCommands.RESTART_SELECTED_TASK_COMMAND.getName())) {

						Task clonedTask = t.cloneTask();
						t.setStatus(TaskStatus.REPROCESSING);
						BatchMatch.getTaskController().addTask(clonedTask, TaskPriority.NORMAL);
					}
				}
			}
		}
	}
}
