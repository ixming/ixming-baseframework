package org.ixming.task4android;

import android.os.Handler;
import android.os.Message;

/**
 * 扩展自{@link android.os.Handler};
 * 
 * 用于该工具内部的相关操作
 * 
 * @author Yin Yong
 *
 */
public class TaskHelperHandler extends Handler {

	public static final int MSG_POST_EXECUTED = 0x1;
	public static final int MSG_POST_CANCELED = 0x2;
	
	/*package*/ TaskHelperHandler() { }
	
	@Override
	public final void handleMessage(Message msg) {
		switch (msg.what) {
			case MSG_POST_EXECUTED: {
				BaseTask task = (BaseTask) msg.obj;
				task.postExecuted();
				break;
			}
			case MSG_POST_CANCELED: {
				BaseTask task = (BaseTask) msg.obj;
				task.postCanceled(task.getCancelToken());
				break;
			}
		}
		msg.recycle();
	}
	
	public void postExecutedMessage(BaseTask task) {
		Message msg = obtainMessage(MSG_POST_EXECUTED, task);
		sendMessage(msg);
	}
	
	public void postCanceledMessage(BaseTask task) {
		Message msg = obtainMessage(MSG_POST_CANCELED, task);
		sendMessage(msg);
	}
	
}
