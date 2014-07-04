package org.ixming.task4android;

import android.os.Handler;
import android.os.Message;

public class TaskHelperHandler extends Handler {

	public static final int MSG_POST_EXECUTED = 0x1;
	public static final int MSG_POST_CANCELED = 0x2;
	
	/*package*/ TaskHelperHandler() { }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final void handleMessage(Message msg) {
		switch (msg.what) {
			case MSG_POST_EXECUTED: {
				BaseTask task = (BaseTask) msg.obj;
				task.postExecuted(task.getExecutedResult());
				break;
			}
			case MSG_POST_CANCELED: {
				BaseTask task = (BaseTask) msg.obj;
				task.postCanceled(task.getExecutedResult());
				break;
			}
		}
	}
	
	public void postExecutedMessage(BaseTask<?> task) {
		Message msg = obtainMessage(MSG_POST_EXECUTED, task);
		sendMessage(msg);
	}
	
	public void postCanceledMessage(BaseTask<?> task) {
		Message msg = obtainMessage(MSG_POST_CANCELED, task);
		sendMessage(msg);
	}
	
}
