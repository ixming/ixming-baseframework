package org.ixming.task4android;

/**
 * 所有Task的基类，封装了加入任务队列的操作和处理过程、结果。
 * 
 * 
 * @author Yin Yong
 *
 */
public abstract class BaseTask implements Runnable {

	private static final String TAG = BaseTask.class.getSimpleName();
	
	private TaskState mState;
	private Object[] mCancelToken;
	protected BaseTask() {
	}
	
	/**
	 * add this task to task queue, and set current state to IDEL
	 */
	public final void execute() {
		LogUtils.i(TAG, "execute");
		purelySetState(TaskState.Preparing);
		TaskQueue.addTask(this);
	}
	
	/**
	 * interrupt if not executing or try to let it interrupted during running
	 * 
	 * @param cancelToken 附加的cancel信息，将在{@link #postCanceled(Object...)}调用时传递 
	 */
	public final void cancel(Object...cancelToken) {
		LogUtils.i(TAG, "cancel");
		purelySetState(TaskState.Interrupted);
		mCancelToken = cancelToken;
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
		if (!checkCancelStateAndPostOrSet(TaskState.Running)) {
			doInBackground();
			checkStateAndPostResult();
		}
	}
	
	protected abstract void doInBackground() ;
	
	protected abstract void postExecuted() ;
	
	protected abstract void postCanceled(Object...cancelToken) ;
	
	private synchronized boolean checkCancelStateAndPost() {
		if (isCanceled()) {
			TaskQueue.getHandler().postCanceledMessage(this);
			return true;
		}
		return false;
	}
	
	private synchronized boolean checkCancelStateAndPostOrSet(TaskState newState) {
		if (checkCancelStateAndPost()) {
			return true;
		}
		purelySetState(newState);
		return false;
	}
	
	private synchronized void checkStateAndPostResult() {
		if (!checkCancelStateAndPostOrSet(TaskState.Finished)) {
			TaskQueue.getHandler().postExecutedMessage(this);
		}
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
	
	Object[] getCancelToken() {
		return mCancelToken;
	}
	
	public Object getTaskToken() {
		return null;
	}
	
}
