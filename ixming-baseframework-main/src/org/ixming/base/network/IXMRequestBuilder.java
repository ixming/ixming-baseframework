package org.ixming.base.network;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * 
 * use like javascript
 * 
 * @author Yin Yong
 *
 */
public interface IXMRequestBuilder {
	
	IXMRequestBuilder method(HttpMethod method);
	IXMRequestBuilder url(String url);
	IXMRequestBuilder header(String name, String value);
	IXMRequestBuilder headers(Map<String, String> headers);
	IXMRequestBuilder param(String name, String value);
	IXMRequestBuilder params(Map<String, String> params);
	IXMRequestBuilder entity(HttpEntity entity);
	/**
	 * 根据当前设置创建HttpRequest
	 */
	HttpUriRequest create();
	
	
	HttpMethod getMethod();
	String getUrl();
	HttpEntity getEntity();
	Map<String, String> getHeaders();
	Map<String, String> getParams();
}
