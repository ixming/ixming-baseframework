package org.ixming.base.common.task;

import java.util.concurrent.atomic.AtomicBoolean;

import org.ixming.base.taskcenter.callback.OnLoadListener;
import org.ixming.base.taskcenter.entity.ReqBean;
import org.ixming.base.taskcenter.task.BaseTask;

/**
 * 用于相同任务
 * 
 * @author Yin Yong
 *
 * @param <Params>
 * @param <Result>
 */
public abstract class BaseProgressTask<Params, Result> {

	private AtomicBoolean mIsProgress = new AtomicBoolean(false);
	private BaseTask mTask;
	public void start(ReqBean reqBean) {
		
		showProgress();
		
	}
	
//	public boolean isProgress() {
//		
//	}
	
	/**
	 * 
	 * 相同任务
	 * 
	 * @param params
	 */
	public void execute(Params...params) {
		
	}
	
	protected abstract void onPreExecute() ;
	protected abstract Result doInBackground(Params...params) ;
	protected abstract void onPostExecute(Result result) ;
	
	
	
	protected abstract void showProgress() ;
	
	protected abstract void hideProgress() ;
	
	public synchronized void cancel() {
		hideProgress();
		mIsProgress.set(true);
		if (null == mTask) {
			return ;
		}
		mTask.setPastFlag(true);
		mTask = null;
	}
	
//	@Override
//	public void onSuccess(Object obj, ReqBean reqMode) {
//	}
//	
//	@Override
//	public void onError(Object obj, ReqBean reqMode) {
//	}
	
}
