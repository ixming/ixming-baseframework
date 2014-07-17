package org.ixming.base.network;

import java.util.Map;

import org.apache.http.HttpEntity;

public interface IXMRequest extends IXMRequestBuilder {

	IXMRequest method(HttpMethod method);
	IXMRequest url(String url);
	IXMRequest header(String name, String value);
	IXMRequest headers(Map<String, String> headers);
	IXMRequest param(String name, String value);
	IXMRequest params(Map<String, String> params);
	IXMRequest entity(HttpEntity entity);
	
	HttpResult execute();
}
