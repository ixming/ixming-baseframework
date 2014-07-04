package org.ixming.task4android;

public abstract class BaseTask<Result> implements Runnable {

	private static final String TAG = BaseTask.class.getSimpleName();
	
	private TaskState mState;
	private Result mResult;
	protected BaseTask() {
	}
	
	/**
	 * add this task to task queue, and set current state to IDEL
	 */
	public synchronized final void execute() {
		LogUtils.i(TAG, "execute");
		purelySetState(TaskState.Preparing);
		TaskQueue.addTask(this);
	}
	
	/**
	 * interrupt if not executing or try to let it interrupted during running
	 */
	public final void cancel() {
		LogUtils.i(TAG, "cancel");
		purelySetState(TaskState.Interrupted);
		// remove from
		TaskQueue.removeTaskFromIdles(this);
	}
	
	/**
	 * check whether the task is canceled by {@link #cancel()}
	 */
	public final boolean isCanceled() {
		return purelyCheckState(TaskState.Interrupted);
	}
	
	@Override
	public final void run() {
		if (!checkCancelStateAndPost()) {
			purelySetState(TaskState.Running);
			mResult = doInBackground();
			checkStateAndPostResult();
		}
	}
	
	protected abstract Result doInBackground() ;
	
	protected abstract void postExecuted(Result result) ;
	
	protected abstract void postCanceled(Result result) ;
	
	private synchronized boolean checkCancelStateAndPost() {
		if (isCanceled()) {
			TaskQueue.getHandler().postCanceledMessage(this);
			return true;
		}
		return false;
	}
	
	private synchronized void checkStateAndPostResult() {
		if (!checkCancelStateAndPost()) {
			TaskQueue.getHandler().postExecutedMessage(this);
		}
	}
	
	/*package*/ Result getExecutedResult() {
		return mResult;
	}
	
	/*package*/ synchronized void purelySetState(TaskState state) {
		mState = state;
	}
	
	/*package*/ synchronized TaskState purelyGetState() {
		return mState;
	}
	
	/*package*/ synchronized boolean purelyCheckState(TaskState...targetStates) {
		for (int i = 0; i < (null == targetStates ? 0 : targetStates.length); i++) {
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
