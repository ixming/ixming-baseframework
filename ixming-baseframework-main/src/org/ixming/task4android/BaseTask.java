package org.ixming.task4android;

public abstract class BaseTask implements ITask {

	private TaskState mState;
	private final TaskQueue mTaskQueue;
	protected BaseTask(TaskQueue taskQueue) {
		mTaskQueue = taskQueue;
	}
	
	/**
	 * add this task to task queue, and set current state to IDEL
	 */
	public void addToTaskQueue() {
		mState = TaskState.Idel;
		mTaskQueue.addTask(this);
	}
	
	/**
	 * remove this task from task queue
	 */
	public void removeFromTaskQueue() {
		// set State Interrupted
		if (checkState(null, TaskState.Idel, TaskState.Running)) {
			setTaskState(TaskState.Interrupted);
		}
		mTaskQueue.removeTask(this);
	}
	
	@Override
	public synchronized void setTaskState(TaskState state) {
		mState = state;
	}
	
	@Override
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
	
	@Override
	public Object getTaskToken() {
		return null;
	}
	
}
