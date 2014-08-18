package org.ixming.base.network.simple;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.ixming.base.network.HttpClientUtil;
import org.ixming.base.utils.android.FrameworkLog;

public class HttpRes implements HttpStatus {
	
	private static final String TAG = HttpRes.class.getSimpleName();
	
	/**
	 * 其他不可期预的错误
	 */
	public static final int CODE_EXCEPTION = -400;
	/**
	 * 客户端请求协议错误
	 */
	public static final int CODE_CLIENT_PROTOCOL = -401;
	/**
	 * 链接超时
	 */
	public static final int CODE_TIMEOUT = -402;
	/**
	 * 网络无法连通等
	 */
	public static final int CODE_CONNECTION_ERROR = -403;
	
	
	private HttpUriRequest mHttpRequest;
	private int mStatusCode;
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private InputStream mHttpInputStream;
	private Object mErrorExtra;
	
	public void execute(HttpUriRequest httpRequest) {
		try {
			this.mHttpRequest = httpRequest;
			mHttpResponse = HttpClientUtil.getHttpClient().execute(mHttpRequest);
			StatusLine statusLine = mHttpResponse.getStatusLine(); 
			mStatusCode = statusLine.getStatusCode();
			mHttpEntity = mHttpResponse.getEntity();
			mHttpInputStream = mHttpEntity.getContent();
		} catch (ClientProtocolException e) {
			FrameworkLog.e(TAG, "execute ClientProtocol Exception: " + e.getMessage());
			e.printStackTrace();
			mStatusCode = CODE_CLIENT_PROTOCOL;
			mErrorExtra = e;
		} catch (java.net.SocketTimeoutException e) {
			FrameworkLog.e(TAG, "execute SocketTimeout Exception: " + e.getMessage());
			e.printStackTrace();
			mStatusCode = CODE_TIMEOUT;
			mErrorExtra = e;
		} catch (org.apache.http.conn.ConnectTimeoutException e) {
			FrameworkLog.e(TAG, "execute ConnectTimeout Exception: " + e.getMessage());
			e.printStackTrace();
			mStatusCode = CODE_TIMEOUT;
			mErrorExtra = e;
		} catch (org.apache.http.conn.HttpHostConnectException e) {
			FrameworkLog.e(TAG, "execute HttpHostConnect Exception: " + e.getMessage());
			e.printStackTrace();
			mStatusCode = CODE_CONNECTION_ERROR;
			mErrorExtra = e;
		} catch (IOException e) {
			FrameworkLog.e(TAG, "execute IO Exception: " + e.getMessage());
			e.printStackTrace();
			mStatusCode = CODE_EXCEPTION;
			mErrorExtra = e;
		} catch (Exception e) {
			FrameworkLog.e(TAG, "execute Other Exception: " + e.getMessage());
			e.printStackTrace();
			mStatusCode = CODE_EXCEPTION;
			mErrorExtra = e;
		}
	}
	
	/**
	 * 获得当前HttpRes中的请求对象
	 */
	public HttpUriRequest getHttpRequest() {
		return mHttpRequest;
	}

	/**
	 * 获取当前HttpRes执行后的StatusCode
	 */
	public int getStatusCode() {
		return mStatusCode;
	}
	
	public Object getErrorExtra() {
		return mErrorExtra;
	}

	/**
	 * 获取当前HttpRes执行后的HttpResponse。
	 * 
	 * <p>
	 * 	<b>注意：</b>不要使用{@link HttpResponse#getEntity()}获取HttpEntity,
	 * 调用{@link #getHttpEntity()}。
	 * 
	 * <br/><br/>
	 * 通过该方法获得的HttpResponse，只应当为外面提供Header等相关信息。
	 * 
	 * </p>
	 */
	public HttpResponse getHttpResponse() {
		return mHttpResponse;
	}

	/**
	 * 获取当前HttpRes执行后的HttpEntity。
	 * 
	 * <p>
	 * 	<b>注意：</b>不要使用{@link HttpEntity#getContent()}获取InputStream,
	 * 调用{@link #getContent()}。
	 * 
	 * <br/><br/>
	 * 通过该方法获得的HttpEntity，只应当为外面提供content-type等相关信息。
	 * 
	 * </p>
	 */
	public HttpEntity getHttpEntity() {
		return this.mHttpEntity;
	}

	/**
	 * 获取当前HttpRes执行后的InputStream。
	 */
	public InputStream getContent() {
		return this.mHttpInputStream;
	}

	public void abort() {
		if (null != mHttpRequest) {
			mHttpRequest.abort();
		}
		mHttpRequest = null;
		
		mHttpInputStream = null;
		
		if (null != mHttpEntity) {
			try {
				mHttpEntity.consumeContent();
			} catch (Exception ignore) { }
		}
		mHttpEntity = null;
		
		mHttpResponse = null;
		
		HttpClientUtil.closeExpiredConnections();
	}
}
