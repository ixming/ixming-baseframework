package org.ixming.base.common.manager;

import org.ixming.base.taskcenter.callback.OnLoadListener;
import org.ixming.base.utils.android.ToastUtils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;


/**
 * Activity中，一些复杂的数据请求，逻辑运算等在此处进行，让Activity较为简单地进行界面的更新操作. <br/>
 * <br/>
 * 即Activity调用其相应的Manager类进行控制、动作相关的操作。
 * 
 * @version 1.0
 */
public abstract class BaseManager implements OnLoadListener {
	public Context appContext;
	public Context context;
	public Handler handler;

	public BaseManager(Context context, Handler handler) {
		this.context = context;
		this.appContext = context.getApplicationContext();
		this.handler = handler;
	}

	protected final Context getApplicationContext() {
		return this.appContext;
	}

	protected final Context getContext() {
		return this.context;
	}

	protected final Handler getHandler() {
		return handler;
	}

	public void toastShow(final String arg) {
		if (TextUtils.isEmpty(arg)) {
			return;
		}
		ToastUtils.showToast(arg);
	}

	public void toastShow(final int resId) {
		toastShow(appContext.getString(resId));
	}
}
