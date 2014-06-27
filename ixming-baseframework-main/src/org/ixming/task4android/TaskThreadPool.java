package org.ixming.task4android;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.ixming.base.utils.android.FrameworkLog;

class TaskThreadPool extends ThreadPoolExecutor {
	
	private static final String TAG = TaskThreadPool.class.getSimpleName();
	
	private static final int CORE_POOL_SIZE = 10;
	private static final int MAXIMUM_POOL_SIZE = 10;
	private static final int KEEP_ALIVE = 15;// 空闲线程的超时时间为1秒(unit Second)
	
	private static final ThreadFactory sThreadFactory;
	private static final BlockingQueue<Runnable> sPoolWorkQueue;
	static {
		synchronized (TaskThreadPool.class) {
			sThreadFactory = new ThreadFactory() {
				private final AtomicInteger mThreadNumber = new AtomicInteger(1);

				public Thread newThread(Runnable r) {
					return new Thread(r, "TaskThreadPool#" + mThreadNumber.getAndIncrement());
				}
			};
			
			sPoolWorkQueue = new LinkedBlockingQueue<Runnable>();
		}
	}
	
	private TaskThreadPoolListener mListener;
	/*package*/ TaskThreadPool() { 
		super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
				sPoolWorkQueue, sThreadFactory);
	}
	
	void setListener(TaskThreadPoolListener l) {
		mListener = l;
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		FrameworkLog.d(TAG, "TaskThreadPool#beforeExecute thread = " + t.getName());
		if (null != mListener) {
			mListener.beforeExecute(t, r);
		}
		super.beforeExecute(t, r);
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (null != mListener) {
			mListener.afterExecute(r, t);
		}
	}
	
	@Override
	protected void terminated() {
		super.terminated();
		if (null != mListener) {
			mListener.terminated();
		}
	}
	
}
