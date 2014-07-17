package org.ixming.task4android;

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
	
	// sync token for Pool
	static final Object sPoolSync = new Object();
	
	// use and listen for before-execute and after-execute events
	static final TaskThreadPool sTaskThreadPool;
	private static TaskHelperHandler sHandler;
	static {
		sTaskThreadPool = new TaskThreadPool();
		
		sTaskThreadPool.setTaskThreadPoolListener(new TaskThreadPoolListenerImpl());
	}
	
	public static TaskHelperHandler getHandler() {
		checkAndInitHandler();
		return sHandler;
	}
	
	private static void checkAndInitHandler() {
		if (null != sHandler) {
			return ;
		}
		synchronized (sPoolSync) {
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
		synchronized (sPoolSync) {
			sTaskThreadPool.execute(task);
		}
	}

	public static boolean removeTaskFromIdles(Runnable task) {
		synchronized (sPoolSync) {
			// queue is locked itself
			if (sTaskThreadPool.getQueue().remove(task)) {
				return true;
			}
			return false;
		}
	}

	private static class TaskThreadPoolListenerImpl implements TaskThreadPoolListener {

		@Override
		public void beforeExecute(Thread t, Runnable r) {
			LogUtils.i(TAG, "beforeExecute thread name = " + Thread.currentThread().getName());
		}

		@Override
		public void afterExecute(Runnable r, Throwable t) {
			LogUtils.i(TAG, "afterExecute thread name = " + Thread.currentThread().getName());
		}

		@Override
		public void terminated() {
			LogUtils.i(TAG, "terminated thread name = " + Thread.currentThread().getName());
		}
	}
}
