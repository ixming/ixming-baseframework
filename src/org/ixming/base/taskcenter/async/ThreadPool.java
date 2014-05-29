package org.ixming.base.taskcenter.async;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.ixming.base.taskcenter.task.BaseTask;


public class ThreadPool {
	private static final int CORE_POOL_SIZE = 10;
	private static final int MAXIMUM_POOL_SIZE = 10;
	private static final int KEEP_ALIVE = 1;// 空闲线程的超时时间为1秒
	/**
	 * 用于自己计算活跃线程数量
	 */
	static AtomicInteger activeCount = new AtomicInteger(0);
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "ThreadPool#" + mCount.getAndIncrement());
		}
	};
	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(
			100);
	private static final ThreadPoolExecutor EXECUTE = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			sPoolWorkQueue, sThreadFactory);

	/**
	 * 剩下空闲的线程数量
	 * 
	 * @return
	 */
	public static int getrFreeCount() {
		return CORE_POOL_SIZE - activeCount.get();
	}

	// 需要执行的任务
	public static synchronized void addTask(final Runnable runnable) {
		activeCount.getAndIncrement();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				runnable.run();
				activeCount.getAndDecrement();
				BaseTask baseTask = TaskHandler.nextTask();
				if (baseTask != null) {
					System.out.println("取出任务" + baseTask.getTag() + "执行！");
					TaskHandler.addTask(baseTask);
				} else {
					System.out.println("-----------没有任务了-----------");
				}
			}
		};
		EXECUTE.execute(r);

	}
}
