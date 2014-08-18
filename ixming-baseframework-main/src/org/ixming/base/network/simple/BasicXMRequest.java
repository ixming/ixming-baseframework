package org.ixming.base.network.simple;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.ixming.base.io.IOConstants;
import org.ixming.base.network.HttpMethod;
import org.ixming.base.network.HttpHelper;
import org.ixming.base.network.core.BaseXMRequest;
import org.ixming.base.network.core.IXMRequest;

public final class BasicXMRequest extends BaseXMRequest implements IXMRequest {

	/**
	 * @return new instance of {@link BasicXMRequest}
	 */
	public static BasicXMRequest newInstance() {
		return new BasicXMRequest();
	}
	
	@Override
	public BasicXMRequest method(HttpMethod method) {
		super.method(method);
		return this;
	}

	@Override
	public BasicXMRequest url(String url) {
		super.url(url);
		return this;
	}

	@Override
	public BasicXMRequest header(String name, String value) {
		super.header(name, value);
		return this;
	}

	@Override
	public BasicXMRequest headers(Map<String, String> headers) {
		super.headers(headers);
		return this;
	}

	@Override
	public BasicXMRequest param(String name, String value) {
		super.param(name, value);
		return this;
	}

	@Override
	public BasicXMRequest params(Map<String, String> params) {
		super.params(params);
		return this;
	}

	@Override
	public BasicXMRequest entity(HttpEntity entity) {
		super.entity(entity);
		return this;
	}

	@Override
	public HttpUriRequest create() {
		return build(this);
	}
	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>
	// inner builder
	public static HttpUriRequest build(IXMRequest request) {
		switch (request.getMethod()) {
		case Get:
			return methodGet(request);
		case Post:
			return methodPost(request);
		case Put:
			return methodPut(request);
		}
		return null;
	}
	
	private static HttpUriRequest methodGet(IXMRequest xmRequest) {
		String url = HttpHelper.urlParams(xmRequest.getUrl(), xmRequest.getParams());
		HttpUriRequest request = HttpMethod.Get.newHttpRequest(url);
		addHeaders(xmRequest, request);
		return request;
	}
	
	private static HttpUriRequest methodPost(IXMRequest xmRequest) {
		return methodEntityEnclosingRequest(xmRequest, HttpMethod.Post);
	}

	private static HttpUriRequest methodPut(IXMRequest xmRequest) {
		return methodEntityEnclosingRequest(xmRequest, HttpMethod.Put);
	}
	
	private static HttpUriRequest methodEntityEnclosingRequest(
			IXMRequest xmRequest, HttpMethod method) {
		HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest)
				method.newHttpRequest(xmRequest.getUrl());
		HttpEntity entity = xmRequest.getEntity();
		if (null != entity) {
			request.setEntity(entity);
		} else {
			try {
				Map<String, String> params = xmRequest.getParams();
				entity = HttpHelper.urlEncodedFormEntity(params, IOConstants.DEF_CHARSET);
				request.setEntity(entity);
			} catch (UnsupportedEncodingException ignore) { }
		}
		addHeaders(xmRequest, request);
		return (HttpUriRequest) request;
	}
	
	private static void addHeaders(IXMRequest xmRequest, HttpRequest request) {
		Map<String, String> headers = xmRequest.getHeaders();
		HttpHelper.setHeaders(request, headers);
	}

}
