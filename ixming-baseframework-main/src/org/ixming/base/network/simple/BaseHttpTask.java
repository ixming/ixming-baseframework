package org.ixming.base.network.simple;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.ixming.base.network.state.BlurryReponseCode;
import org.ixming.task4android.BaseTask;

public abstract class BaseHttpTask extends BaseTask {

	private HttpUriRequest mHttpRequest;
	protected BlurryReponseCode mResponseCode;
	/**
	 * @see {@link BasicXMRequest}
	 */
	public BaseHttpTask(HttpUriRequest request) {
		mHttpRequest = request;
	}
	
	protected boolean isConsideredSuccess() {
		return mResponseCode == BlurryReponseCode.Success;
	}
	
	@Override
	protected final void doInBackground() {
		if (isCanceled()) {
			return ;
		}
		HttpRes httpRes = new HttpRes();
		httpRes.execute(mHttpRequest);
		// 分析模糊返回code
		caseBlurryCode(httpRes.getStatusCode());
		if (!isCanceled()) {
			onHttpResult(mResponseCode, httpRes);
		}
		httpRes.abort();
	}
	
	protected void caseBlurryCode(int statusCode) {
		if (statusCode == HttpStatus.SC_OK) {
			mResponseCode = BlurryReponseCode.Success;
		} else if (statusCode >= 300 && statusCode < 400) {
			mResponseCode = BlurryReponseCode.Redirection;
		} else if (statusCode >= 400 && statusCode < 500) {
			mResponseCode = BlurryReponseCode.ClientError;
		} else if (statusCode >= 500 && statusCode < 600) {
			mResponseCode = BlurryReponseCode.ServerError;
		} else if (statusCode < 0) {
			mResponseCode = BlurryReponseCode.Other;
		}
	}
	
	/**
	 * in thread
	 */
	protected abstract void onHttpResult(BlurryReponseCode responseCode, HttpRes httpRes) ;
	
	@Override
	protected final void postExecuted() { /*do nothing*/ }
	
}
