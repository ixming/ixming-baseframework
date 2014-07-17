package org.ixming.task4android;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*package*/ class TaskThreadPool extends ThreadPoolExecutor {
	
	private static final int CORE_POOL_SIZE = 10;
	private static final int MAXIMUM_POOL_SIZE = 10;
	private static final int KEEP_ALIVE = 15;// 空闲线程的超时时间为15秒(unit Second)
	
	private static final ThreadFactory sThreadFactory;
	private static final ThreadPoolWrapper sPoolWorkQueue;
	static {
		synchronized (TaskQueue.sPoolSync) {
			sThreadFactory = new ThreadFactory() {
				private final AtomicInteger mThreadNumber = new AtomicInteger(1);

				public Thread newThread(Runnable r) {
					return new Thread(r, "TaskThreadPool#" + mThreadNumber.getAndIncrement());
				}
			};
			
			sPoolWorkQueue = new ThreadPoolWrapper();
		}
	}
	
	private TaskThreadPoolListener mListener;
	/*package*/ TaskThreadPool() { 
		super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
				sPoolWorkQueue, sThreadFactory, new DiscardPolicy());
	}
	
	void setTaskThreadPoolListener(TaskThreadPoolListener l) {
		mListener = l;
	}
	
	ThreadPoolWrapper getMyBlockingQueue() {
		return sPoolWorkQueue;
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
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
