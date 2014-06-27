package org.ixming.task4android;

public abstract class BaseTask implements Runnable {

	private TaskState mState;
	protected BaseTask() {
	}
	
	/**
	 * add this task to task queue, and set current state to IDEL
	 */
	public void addToTaskQueue() {
		mState = TaskState.Preparing;
		TaskQueue.execute(this);
	}
	
	/**
	 * remove this task from task queue
	 */
	public void removeFromTaskQueue() {
		// set State Interrupted
		if (checkState(null, TaskState.Preparing, TaskState.Running)) {
			setTaskState(TaskState.Interrupted);
		}
		mTaskQueue.removeTask(this);
	}
	
	public synchronized void setTaskState(TaskState state) {
		switch (state) {
		case Preparing :
			if (!checkState(null, TaskState.Preparing)) {
				return ;
			}
			break;
		case Running :
			if (checkState(TaskState.Interrupted, TaskState.Finished)) {
				return ;
			}
			break;
		case Interrupted :
			if (checkState(TaskState.Finished)) {
				return ;
			}
			break;
		case Finished:
			if (checkState(TaskState.Interrupted)) {
				return ;
			}
			break;
		}
		mState = state;
	}
	
	public synchronized TaskState getTaskState() {
		return mState;
	}
	
	protected boolean checkState(TaskState...targetStates) {
		if (null == targetStates || 0 == targetStates.length) return false;
		for (int i = 0; i < targetStates.length; i++) {
			if (targetStates[i] == mState) {
				return true;
			}
		}
		return false;
	}
	
	public Object getTaskToken() {
		return null;
	}
	
}
