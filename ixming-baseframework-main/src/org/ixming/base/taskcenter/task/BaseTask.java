package org.ixming.base.taskcenter.task;

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
	private Boolean pastFlag = false;

	public Boolean getPastFlag() {
		return pastFlag;
	}

	public void setPastFlag(Boolean pastFlag) {
		this.pastFlag = pastFlag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
