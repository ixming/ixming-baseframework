package org.ixming.base.taskcenter.entity;

import java.util.Map;

import android.content.Context;

public class BaseReqBean {
	// 下载地址
	private String url = "";
	// 请求类型，post put get delete
	private int reqType = 0;
	// 请求标示
	private int reqMode = 0;
	// 地址附带请求参数
	private String json = "";
	private Map<String, String> data;
	// 请求结果
	private Object callbackResult = null;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getReqType() {
		return reqType;
	}

	public void setReqType(int reqType) {
		this.reqType = reqType;
	}

	public int getReqMode() {
		return reqMode;
	}

	public void setReqMode(int reqMode) {
		this.reqMode = reqMode;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public Object getCallbackResult() {
		return callbackResult;
	}

	public void setCallbackResult(Object callbackResult) {
		this.callbackResult = callbackResult;
	}

}
