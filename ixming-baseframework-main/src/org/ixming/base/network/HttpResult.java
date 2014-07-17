package org.ixming.base.network;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * 依赖：
 * 	Apache HTTP请求
 * 
 * 提供的服务：
 * 
 * 
 * @author Yin Yong
 *
 */
public class HttpResult {

	public static final int CODE_URL = -1;
	
	public static final int CODE_NON_NETWORK = -2;
	
	public static final int CODE_TIMEOUT = -3;
	
	private HttpUriRequest mHttpRequest;
	private HttpResponse mHttpResponse;
	
	private HttpResult() {
		
	}
	
	void executeForResult(HttpUriRequest httpRequest) {
		mHttpRequest = httpRequest;

		try {
			HttpClientUtil.getHttpClient().execute(httpRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			if (e instanceof java.net.SocketException) {

			} else if (e instanceof java.net.SocketTimeoutException) {

			}
			e.printStackTrace();
		}
	}
	
	public void abort() {
		if (null != mHttpRequest) {
			mHttpRequest.abort();
		}
		mHttpRequest = null;
		
		if (null != mHttpResponse) {
		}
	}
	
	/**
	 * 该方法为耗时操作
	 */
	public static HttpResult execute(HttpUriRequest request) {
		HttpResult result = new HttpResult();
		result.executeForResult(request);
		return result;
	}
	
}
