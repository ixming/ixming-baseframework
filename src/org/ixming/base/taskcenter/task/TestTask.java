package org.ixming.base.taskcenter.task;

public class TestTask extends BaseTask {
	public TestTask() {
		super();
		setPriority(getPriority() + 1);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-----任务：" + getTag() + "执行完成-----");
	}
}
