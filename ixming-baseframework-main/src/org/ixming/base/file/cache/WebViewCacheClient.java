package org.ixming.base.file.cache;

import org.ixming.base.utils.android.LogUtils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WebViewCacheClient extends WebViewClient {
	private static final String TAG = WebViewCacheClient.class.getSimpleName();
	
	private WebViewClient mWrapped;
	private WebViewCache mCache = new WebViewCache();

	public WebViewCacheClient(WebViewClient webViewClient) {
		mWrapped = webViewClient;
	}
	
	public WebViewCache getWebViewCache() {
		return mCache;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (null != mWrapped) {
			return mWrapped.shouldOverrideUrlLoading(view, url);
		} else {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if (null != mWrapped) {
			mWrapped.onPageStarted(view, url, favicon);
		} else {
			super.onPageStarted(view, url, favicon);
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		if (null != mWrapped) {
			mWrapped.onPageFinished(view, url);
		} else {
			super.onPageFinished(view, url);
		}
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		if (null != mWrapped) {
			mWrapped.onLoadResource(view, url);
		} else {
			super.onLoadResource(view, url);
		}
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view,
			String url) {
		LogUtils.d(TAG, "shouldInterceptRequest url = " + url);
		WebResourceResponse response = mCache.getWebViewResponse(url);
		if (null == response) {
			if (null != mWrapped) {
				response = mWrapped.shouldInterceptRequest(view, url);
			} else {
				response = super.shouldInterceptRequest(view, url);
			}
		}
		return response;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		if (null != mWrapped) {
			mWrapped.onReceivedError(view, errorCode, description, failingUrl);
		} else {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		if (null != mWrapped) {
			mWrapped.onFormResubmission(view, dontResend, resend);
		} else {
			super.onFormResubmission(view, dontResend, resend);
		}
	}

	@Override
	public void doUpdateVisitedHistory(WebView view, String url,
			boolean isReload) {
		if (null != mWrapped) {
			mWrapped.doUpdateVisitedHistory(view, url, isReload);
		} else {
			super.doUpdateVisitedHistory(view, url, isReload);
		}
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		if (null != mWrapped) {
			mWrapped.onReceivedSslError(view, handler, error);
		} else {
			super.onReceivedSslError(view, handler, error);
		}
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		if (null != mWrapped) {
			mWrapped.onReceivedHttpAuthRequest(view, handler, host, realm);
		} else {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		if (null != mWrapped) {
			return mWrapped.shouldOverrideKeyEvent(view, event);
		} else {
			return super.shouldOverrideKeyEvent(view, event);
		}
	}

	@Override
	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		if (null != mWrapped) {
			mWrapped.onUnhandledKeyEvent(view, event);
		} else {
			super.onUnhandledKeyEvent(view, event);
		}
	}

	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		if (null != mWrapped) {
			mWrapped.onScaleChanged(view, oldScale, newScale);
		} else {
			super.onScaleChanged(view, oldScale, newScale);
		}
	}

	@Override
	public void onReceivedLoginRequest(WebView view, String realm,
			String account, String args) {
		if (null != mWrapped) {
			mWrapped.onReceivedLoginRequest(view, realm, account, args);
		} else {
			super.onReceivedLoginRequest(view, realm, account, args);
		}
	}
}
