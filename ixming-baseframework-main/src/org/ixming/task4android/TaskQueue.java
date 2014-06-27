package org.ixming.task4android;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class TaskQueue {

	private static final int CORE_POOL_SIZE = 10;
	private static final int MAXIMUM_POOL_SIZE = 10;
	private static final int KEEP_ALIVE = 1;// 空闲线程的超时时间为1秒
	
	private final int INIT_QUEUE_SIZE = 10;
	private final PriorityQueue<ITask> mTaskQueue;
	private final LinkedList<ITask> mIdelTasks;
	private final LinkedList<ITask> mRunningTasks;
	public TaskQueue() {
		mTaskQueue = new PriorityQueue<ITask>(INIT_QUEUE_SIZE);
		mIdelTasks = new LinkedList<ITask>();
		mRunningTasks = new LinkedList<ITask>();
	}
	
	/*package*/ void addTask(ITask task) {
		synchronized (mTaskQueue) {
			
		}
	}

	/*package*/ void removeTask(ITask task) {
		synchronized (mTaskQueue) {
		}
	}

}
