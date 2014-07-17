package org.ixming.base.network;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;

public abstract class BaseXMRequest implements IXMRequest {

	private HttpMethod mHttpMethod;
	private String mUrl;
	private Map<String, String> mHeaders;
	private Map<String, String> mParams;
	private HttpEntity mEntity;
	
	@Override
	public IXMRequest method(HttpMethod method) {
		mHttpMethod = method;
		return this;
	}

	@Override
	public IXMRequest url(String url) {
		mUrl = url;
		return this;
	}

	private void checkHeaderMap() {
		if (null == mHeaders) {
			mHeaders = new HashMap<String, String>();
		}
	}
	@Override
	public IXMRequest header(String name, String value) {
		checkHeaderMap();
		mHeaders.put(name, value);
		return this;
	}

	@Override
	public IXMRequest headers(Map<String, String> headers) {
		if (null != headers) {
			checkHeaderMap();
			mHeaders.putAll(headers);
		}
		return this;
	}

	
	private void checkParamMap() {
		if (null == mParams) {
			mParams = new HashMap<String, String>();
		}
	}
	@Override
	public IXMRequest param(String name, String value) {
		checkParamMap();
		mParams.put(name, value);
		return this;
	}

	@Override
	public IXMRequest params(Map<String, String> params) {
		if (null != params) {
			checkParamMap();
			mParams.putAll(params);
		}
		return this;
	}
	
	@Override
	public IXMRequest entity(HttpEntity entity) {
		mEntity = entity;
		return this;
	}
	
	@Override
	public HttpMethod getMethod() {
		return mHttpMethod;
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

	@Override
	public HttpEntity getEntity() {
		return mEntity;
	}

	@Override
	public Map<String, String> getHeaders() {
		return mHeaders;
	}

	@Override
	public Map<String, String> getParams() {
		return mParams;
	}

}
