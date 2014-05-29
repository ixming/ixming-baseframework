package org.ixming.base.taskcenter.entity;

import org.ixming.base.taskcenter.callback.OnLoadListener;

public class ReqBean extends BaseReqBean {
	// 回调
	private OnLoadListener listener;

	public OnLoadListener getListener() {
		return listener;
	}

	public void setListener(OnLoadListener listener) {
		this.listener = listener;
	}

}