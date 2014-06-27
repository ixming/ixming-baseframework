package org.ixming.task4android;

public interface ITask extends Runnable {

	void setTaskState(TaskState state);
	
	TaskState getTaskState();
	
	Object getTaskToken();
	
	boolean checkTaskToken();
}
