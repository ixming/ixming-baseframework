package org.ixming.base.taskcenter.entity;

import org.ixming.base.taskcenter.callback.OnDownloadListener;

public class ReqFileBean {
	// 文件地址的URL
	private String url = "";
	// 文件存储地址
	private String paramPath = "";
	private OnDownloadListener listener;
	// 后缀
	private String suffix;
	//是否优先取缓存
	private boolean cacheMode;
	
	public boolean isCacheMode() {
		return cacheMode;
	}

	public void setCacheMode(boolean cacheMode) {
		this.cacheMode = cacheMode;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParamPath() {
		return paramPath;
	}

	public void setParamPath(String paramPath) {
		this.paramPath = paramPath;
	}

	public OnDownloadListener getListener() {
		return listener;
	}

	public void setListener(OnDownloadListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj.getClass().getName().equals(this.getClass().getName())) {
			if (obj instanceof ReqFileBean) {
				ReqFileBean temp = (ReqFileBean) obj;
				if (temp.getUrl().equals(this.getUrl())) {
					return true;
				}
			}
		}
		return false;

	}
}