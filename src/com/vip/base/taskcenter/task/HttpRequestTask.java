package com.vip.base.taskcenter.task;

import com.vip.base.network.HttpClientUtil;
import com.vip.base.network.HttpRes;
import com.vip.base.network.utils.NetWorkUtils;
import com.vip.base.taskcenter.callback.OnLoadListener;
import com.vip.base.taskcenter.entity.ReqBean;

public class HttpRequestTask extends BaseTask {
	public HttpRequestTask(ReqBean reqBean) {
		this.bean = reqBean;
	}

	private ReqBean bean;

	@Override
	public void run() {
		HttpRes res = null;
		if (getPastFlag())
			return;
		if (bean != null && NetWorkUtils.isNotNull(bean.getUrl())) {
			int reqType = bean.getReqType();
			// 耗时操作
			if (HttpClientUtil.POST == reqType) {
				res = HttpClientUtil.proxyHttpPost(bean.getUrl(),
						bean.getData());
			} else if (HttpClientUtil.GET == reqType) {
				res = HttpClientUtil
						.proxyHttpGet(bean.getUrl(), bean.getData());
			}
			if (getPastFlag())
				return;
			// 请求后结果回调
			if (res != null) {
				notifySuccessComm(res, bean);

			} else {
				notifyErrorComm(res, bean);
			}
		}
	}

	private void notifySuccessComm(final HttpRes entity, final ReqBean bean) {
		final OnLoadListener listener = bean.getListener();
		if (entity != null) {
			if (listener != null) {
				listener.onSuccess(entity, bean);
			}
		} else {
			notifyErrorComm(null, bean);
		}
	}

	public void notifyErrorComm(final Object o, final ReqBean bean) {
		final OnLoadListener listener = bean.getListener();
		if (listener != null) {
			listener.onError(o, bean);
		}
	}
}
