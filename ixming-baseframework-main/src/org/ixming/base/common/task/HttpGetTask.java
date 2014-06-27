package org.ixming.base.common.task;

import java.util.Map;

import org.ixming.base.common.BaseApplication;
import org.ixming.base.network.HttpClientUtil;
import org.ixming.base.taskcenter.async.TaskHandler;
import org.ixming.base.taskcenter.callback.OnLoadListener;
import org.ixming.base.taskcenter.entity.ReqBean;
import org.ixming.base.taskcenter.task.BaseTask;
import org.ixming.base.taskcenter.task.HttpRequestTask;
import org.ixming.base.utils.android.AndroidUtils;

public class HttpGetTask {

	private IProgressDisplayer mIProgressDisplayer;
	private BaseTask mTask;
	public HttpGetTask() {
	}
	public HttpGetTask(IProgressDisplayer progressDisplayer) {
		mIProgressDisplayer = progressDisplayer;
	}
	
	public void execute(String url, Map<String, String> data,
			int mode, OnLoadListener listener) {
		cancel();
		
		ReqBean reqBean = new ReqBean();
		reqBean.setUrl(url);
		reqBean.setData(data);
		reqBean.setReqMode(mode);
		reqBean.setListener(new OnLoadListenerWrapper(listener));
		reqBean.setReqType(HttpClientUtil.GET);
		mTask = new HttpRequestTask(reqBean);
		TaskHandler.addTask(mTask);
		
		showProgress();
	}
	
	public void cancel() {
		if (null != mTask) {
			mTask.setPastFlag(true);
			hideProgress();
		}
		mTask = null;
	}
	
	private void onPostExecute() {
		if (null != mTask) {
			mTask.setPastFlag(true);
			hideProgress();
		}
		mTask = null;
	}
	
	private class OnLoadListenerWrapper implements OnLoadListener {
		private OnLoadListener mWrapped;
		public OnLoadListenerWrapper(OnLoadListener listener) {
			mWrapped = listener;
		}
		
		@Override
		public void onSuccess(Object obj, ReqBean reqMode) {
			onPostExecute();
			if (null != mWrapped) {
				mWrapped.onSuccess(obj, reqMode);
			}
		}

		@Override
		public void onError(Object obj, ReqBean reqMode) {
			onPostExecute();
			if (null != mWrapped) {
				mWrapped.onError(obj, reqMode);
			}
		}
	}
	
	private void showProgress() {
		if (AndroidUtils.isMainThread()) {
			if (null == mIProgressDisplayer) {
				return ;
			}
			mIProgressDisplayer.displayProgress();
		} else {
			BaseApplication.getHandler().post(new Runnable() {
				
				@Override
				public void run() {
					if (null == mIProgressDisplayer) {
						return ;
					}
					mIProgressDisplayer.displayProgress();
				}
			});
		}
	}
	
	private void hideProgress() {
		if (AndroidUtils.isMainThread()) {
			if (null == mIProgressDisplayer) {
				return ;
			}
			mIProgressDisplayer.hideProgress();
		} else {
			BaseApplication.getHandler().post(new Runnable() {
				
				@Override
				public void run() {
					if (null == mIProgressDisplayer) {
						return ;
					}
					mIProgressDisplayer.hideProgress();
				}
			});
		}
	}
	
}
