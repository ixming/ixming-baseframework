package org.ixming.base.view;

import org.ixming.base.file.cache.CacheCallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebView extends WebView {

	private WebViewClient mWebViewClient;
	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public CustomWebView(Context context) {
		super(context);
		
		init();
	}
	
	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
	}
	
	public void loadUrl(String url, CacheCallback callback) {
		compatAddURLCacheCallback(url, callback);
		super.loadUrl(url);
	}
	
	private void compatAddURLCacheCallback(String url, CacheCallback callback) {
		if (android.os.Build.VERSION.SDK_INT >=
				android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			org.ixming.base.file.cache.WebViewCacheClient wvcc = 
					(org.ixming.base.file.cache.WebViewCacheClient) mWebViewClient;
			wvcc.getWebViewCache().addURLCacheCallback(url, callback);
		}
	}
	
	/**
	 * 加载页面，根据参数  <code>token</code> 判定缓存的有效状态
	 */
	public void loadUrlWithToken(String url, Object token) {
		compatAddURLToken(url, token);
		super.loadUrl(url);
	}
	
	/**
	 * 加载页面，根据参数  <code>token</code> 判定缓存的有效状态
	 */
	public void loadUrlWithToken(String url, Object token, CacheCallback callback) {
		compatAddURLToken(url, token);
		compatAddURLCacheCallback(url, callback);
		super.loadUrl(url);
	}
	
	private void compatAddURLToken(String url, Object token) {
		if (android.os.Build.VERSION.SDK_INT >=
				android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			org.ixming.base.file.cache.WebViewCacheClient wvcc = 
					(org.ixming.base.file.cache.WebViewCacheClient) mWebViewClient;
			wvcc.getWebViewCache().addURLToken(url, token);
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		// enable javascript
		getSettings().setJavaScriptEnabled(true);
		
		setWebViewClient(null);
	}
	
	@Override
	public void setWebViewClient(WebViewClient client) {
		if (android.os.Build.VERSION.SDK_INT >=
				android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			super.setWebViewClient(mWebViewClient = new 
					org.ixming.base.file.cache.WebViewCacheClient(client));
		} else {
			super.setWebViewClient(mWebViewClient = client);
		}
	}
	
	
}
