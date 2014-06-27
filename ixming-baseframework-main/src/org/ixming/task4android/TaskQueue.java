package org.ixming.task4android;

import java.util.LinkedList;

import org.ixming.base.utils.android.FrameworkLog;

public class TaskQueue {

	private static final String TAG = TaskQueue.class.getSimpleName();
	
	private static class WrappedTask implements Runnable {

		private BaseTask mWrapped;
		public WrappedTask() { }
		
		public WrappedTask setTask(BaseTask task) {
			mWrapped = task;
			return this;
		}
		
		@Override
		public void run() {
			if (null == mWrapped) {
				return ;
			}
			
			if (mWrapped.checkState(TaskState.Preparing)) {
				mWrapped.setTaskState(TaskState.Running);
				mWrapped.run();
			}
		}
		
	}
	
	private static final Object sSyncObj = new Object();
	private static final TaskThreadPool sTaskThreadPool;
	private static final ThreadLocal<WrappedTask> sTaskThreadLocal;
	private static final LinkedList<BaseTask> sIdelTasks;
	private static final LinkedList<BaseTask> sRunningTasks;
	static {
		sIdelTasks = new LinkedList<BaseTask>();
		sRunningTasks = new LinkedList<BaseTask>();
		sTaskThreadPool = new TaskThreadPool();
		
		sTaskThreadPool.setListener(new TaskThreadPoolListener() {
			@Override
			public void terminated() { }
			
			@Override
			public void beforeExecute(Thread t, Runnable r) {
				if (!(r instanceof BaseTask)) return ;
				synchronized (sSyncObj) {
					BaseTask task = (BaseTask) r;
					sIdelTasks.remove(task);
					FrameworkLog.d(TAG, "beforeExecute sIdelTasks = " + sIdelTasks.size());
					
					sRunningTasks.add(task);
					task.setTaskState(TaskState.Running);
				}
			}
			
			@Override
			public void afterExecute(Runnable r, Throwable t) {
				if (!(r instanceof BaseTask)) return ;
				
				synchronized (sSyncObj) {
					BaseTask task = (BaseTask) r;
					sRunningTasks.remove(task);
					task.setTaskState(TaskState.Finished);
				}
				
			}
		});
		
		sTaskThreadLocal = new ThreadLocal<TaskQueue.WrappedTask>() {
			@Override
			protected WrappedTask initialValue() {
				return new WrappedTask();
			}
		};
	}
	
	/*package*/ static void execute(BaseTask task) {
		synchronized (sSyncObj) {
			sIdelTasks.add(task);
			FrameworkLog.d(TAG, "addTask sIdelTasks = " + sIdelTasks.size());
			sTaskThreadPool.execute(sTaskThreadLocal.get().setTask(task));
		}
	}

	/*package*/ void removeTask(BaseTask task) {
		synchronized (sSyncObj) {
		}
	}

}
