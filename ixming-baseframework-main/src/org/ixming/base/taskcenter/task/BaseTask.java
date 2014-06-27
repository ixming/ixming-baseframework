package org.ixming.base.taskcenter.task;

import java.util.concurrent.atomic.AtomicBoolean;

import org.ixming.base.taskcenter.async.TaskHandler;

public abstract class BaseTask implements Runnable {

	public BaseTask() {
		priority = TaskHandler.getPriority();
		tag = TaskHandler.getTag();
	}

	private String tag;
	// 标示任务优先级用于排序
	private int priority;
	// 该任务是否过期 设置为过期true则不会在线程中执行 即使执行不会返回结果。
	private AtomicBoolean pastFlag = new AtomicBoolean(false);

	public synchronized boolean getPastFlag() {
		return pastFlag.get();
	}

	public synchronized void setPastFlag(boolean pastFlag) {
		this.pastFlag.set(pastFlag);
	}

	public synchronized String getTag() {
		return tag;
	}

	public synchronized void setTag(String tag) {
		this.tag = tag;
	}

	public synchronized int getPriority() {
		return priority;
	}

	public synchronized void setPriority(int priority) {
		this.priority = priority;
	}

}
