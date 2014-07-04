package org.ixming.task4android;

import java.util.LinkedList;

import org.ixming.base.common.BaseApplication;
import org.ixming.base.utils.android.AndroidUtils;

/**
 * task queue of this util
 * 
 * @author Yin Yong
 *
 */
public class TaskQueue {

	private static final String TAG = TaskQueue.class.getSimpleName();
	
	private static final Object sSyncObj = new Object();
	// use and listen for before-execute and after-execute events
	private static final TaskThreadPool sTaskThreadPool;
	private static final LinkedList<Runnable> sIdleTasks;
	private static final LinkedList<Runnable> sRunningTasks;
	
	private static TaskHelperHandler sHandler;
	static {
		sIdleTasks = new LinkedList<Runnable>();
		sRunningTasks = new LinkedList<Runnable>();
		sTaskThreadPool = new TaskThreadPool();
		
		sTaskThreadPool.setListener(new TaskThreadPoolListenerImpl());
	}
	
	public static TaskHelperHandler getHandler() {
		checkAndInitHandler();
		return sHandler;
	}
	
	private static void checkAndInitHandler() {
		if (null != sHandler) {
			return ;
		}
		synchronized (sSyncObj) {
			if (AndroidUtils.isMainThread()) {
				sHandler = new TaskHelperHandler();
			} else {
				BaseApplication.getHandler().post(new Runnable() {
					
					@Override
					public void run() {
						checkAndInitHandler();
					}
				});
				
				while (null == sHandler) { }
			}
		}
	}
	
	/**
	 * add a task runnable into the pool, and execute it (if any thread idle) 
	 * 
	 * @param task
	 */
	public static void addTask(Runnable task) {
		synchronized (sSyncObj) {
			sIdleTasks.add(task);
			LogUtils.d(TAG, "addTask sIdelTasks = " + sIdleTasks.size());
			sTaskThreadPool.execute(task);
		}
	}

	/*package*/ static boolean removeTaskFromIdles(BaseTask<?> task) {
		synchronized (sSyncObj) {
			// queue is locked itself
			if (sTaskThreadPool.getQueue().remove(task)) {
				return sIdleTasks.remove(task);
			}
			return false;
		}
	}

	private static class TaskThreadPoolListenerImpl implements TaskThreadPoolListener {

		@Override
		public void beforeExecute(Thread t, Runnable r) {
			LogUtils.i(TAG, "beforeExecute thread name = " + Thread.currentThread().getName()
					+ ", is main = " + AndroidUtils.isMainThread());
			synchronized (sSyncObj) {
				sIdleTasks.remove(r);
				sRunningTasks.add(r);
				LogUtils.d(TAG, "beforeExecute sIdleTasks = " + sIdleTasks.size());
			}
		}

		@Override
		public void afterExecute(Runnable r, Throwable t) {
			LogUtils.i(TAG, "afterExecute thread name = " + Thread.currentThread().getName()
					+ ", is main = " + AndroidUtils.isMainThread());
			synchronized (sSyncObj) {
				sRunningTasks.remove(r);
				LogUtils.d(TAG, "afterExecute sRunningTasks = " + sRunningTasks.size());
			}
		}

		@Override
		public void terminated() {
			LogUtils.i(TAG, "terminated thread name = " + Thread.currentThread().getName()
					+ ", is main = " + AndroidUtils.isMainThread());
		}
		
	}
}
