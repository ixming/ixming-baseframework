package org.ixming.task4android;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class TaskConfigure {

	private TaskConfigure() { }
	
	/**
     * Sets the time limit for which threads may remain idle before
     * being terminated.  If there are more than the core number of
     * threads currently in the pool, after waiting this amount of
     * time without processing a task, excess threads will be
     * terminated.  This overrides any value set in the constructor.
     *
     * @param time the time to wait.  A time value of zero will cause
     *        excess threads to terminate immediately after executing tasks.
     * @param unit the time unit of the {@code time} argument
     * @throws IllegalArgumentException if {@code time} less than zero or
     *         if {@code time} is zero and {@code allowsCoreThreadTimeOut}
     */
	public static void setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
		synchronized (TaskQueue.sPoolSync) {
			TaskQueue.sTaskThreadPool.setKeepAliveTime(keepAliveTime, unit);
		}
	}
	
	/**
     * Sets the core number of threads.  This overrides any value set
     * in the constructor.  If the new value is smaller than the
     * current value, excess existing threads will be terminated when
     * they next become idle.  If larger, new threads will, if needed,
     * be started to execute any queued tasks.
     *
     * @param corePoolSize the new core size
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
	public static void setCorePoolSize(int corePoolSize) {
		synchronized (TaskQueue.sPoolSync) {
			TaskQueue.sTaskThreadPool.setCorePoolSize(corePoolSize);
		}
	}
	
	/**
     * Sets the maximum allowed number of threads. This overrides any
     * value set in the constructor. If the new value is smaller than
     * the current value, excess existing threads will be
     * terminated when they next become idle.
     *
     * @param maximumPoolSize the new maximum
     * @throws IllegalArgumentException if the new maximum is
     *         less than or equal to zero, or
     *         less than the {@linkplain #getCorePoolSize core pool size}
     */
	public static void setMaximumPoolSize(int maximumPoolSize) {
		synchronized (TaskQueue.sPoolSync) {
			TaskQueue.sTaskThreadPool.setMaximumPoolSize(maximumPoolSize);
		}
	}
	
	public static void switchToPriorityQueue(Comparator<? super Runnable> comparator) {
		synchronized (TaskQueue.sPoolSync) {
			TaskQueue.sTaskThreadPool.getMyBlockingQueue().setPriorityBlockingQueue(comparator);
		}
	}
	
	public static void switchToDefQueue() {
		synchronized (TaskQueue.sPoolSync) {
			TaskQueue.sTaskThreadPool.getMyBlockingQueue().setDefBlockingQueue();
		}
	}
	
}
