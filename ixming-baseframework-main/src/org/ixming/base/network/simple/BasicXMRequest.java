package org.ixming.base.network.simple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.ixming.base.io.IOConstants;
import org.ixming.base.network.BaseXMRequest;
import org.ixming.base.network.HttpClientUtil;
import org.ixming.base.network.HttpMethod;
import org.ixming.base.network.HttpResult;
import org.ixming.base.network.IXMRequest;
import org.ixming.base.network.state.BlurryReponseCode;

public final class BasicXMRequest extends BaseXMRequest implements IXMRequest {

	public static BasicXMRequest newInstance() {
		return new BasicXMRequest();
	}
	
	private static IXMRequest defHeaders(IXMRequest xmRequest) {
		xmRequest.header("Accept", "application/json;q=1.0,image/*;q=0.9,*/*;q=0.3");
		xmRequest.header("Accept-Encoding", "gzip;q=1.0,identity;q=0.8,deflate;q=0.3");
		xmRequest.header("Accept-Language", "zh-CN,zh;q=0.8");
		xmRequest.header("Accept-Charset", "GBK,utf-8,iso-8859-1,utf-16;q=0.7,*;q=0.3");
		return xmRequest;
	}
	
	public static HttpResult get(String url, Map<String, String> params) {
		return defHeaders(newInstance().url(url).method(HttpMethod.Get)
				.params(params)).execute();
	}
	
	public static HttpResult post(String url, Map<String, String> params) {
		return defHeaders(newInstance().url(url).method(HttpMethod.Post)
				.params(params)).execute();
	}
	
	public static HttpResult post(String url, HttpEntity entity) {
		return defHeaders(newInstance().url(url).method(HttpMethod.Post)
				.entity(entity)).execute();
	}
	
	public static HttpResult put(String url, HttpEntity entity) {
		return defHeaders(newInstance().url(url).method(HttpMethod.Put)
				.entity(entity)).execute();
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
	
	@Override
	public HttpResult execute() {
		HttpUriRequest request = create();
		try {
			HttpResponse response = HttpClientUtil.getHttpClient().execute(request);
			StatusLine statusLine = response.getStatusLine();
			
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private BlurryReponseCode caseBlurryCode(StatusLine statusLine) {
		return null;
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
	
	private static final char QP_SEP_A = '&';
    private static final String NAME_VALUE_SEPARATOR = "=";
	private static HttpUriRequest methodGet(IXMRequest xmRequest) {
		String url = xmRequest.getUrl();
		Map<String, String> params = xmRequest.getParams();
		if (null != params && !params.isEmpty()) {
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(url);
			urlBuilder.append("?");
			
			Iterator<Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String name = entry.getKey();
				String value = entry.getValue();
				try {
					urlBuilder.append(QP_SEP_A)
						.append(name).append(NAME_VALUE_SEPARATOR)
						.append(URLEncoder.encode(value, IOConstants.DEF_CHARSET));
	            } catch (UnsupportedEncodingException e) {
	            	urlBuilder.append(QP_SEP_A)
						.append(name).append(NAME_VALUE_SEPARATOR).append(value);
	            }
			}
			url = urlBuilder.toString();
		}
		
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
			Map<String, String> params = xmRequest.getParams();
			if (null != params && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				
				Iterator<Entry<String, String>> iter = params.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = iter.next();
					String name = entry.getKey();
					String value = entry.getValue();
					pairs.add(new BasicNameValuePair(name, value));
				}
				try {
					request.setEntity(new UrlEncodedFormEntity(pairs, IOConstants.DEF_CHARSET));
				} catch (UnsupportedEncodingException ignore) { }
			}
		}
		addHeaders(xmRequest, request);
		return (HttpUriRequest) request;
	}
	
	private static void addHeaders(IXMRequest xmRequest, HttpRequest request) {
		Map<String, String> headers = xmRequest.getHeaders();
		if (null != headers && !headers.isEmpty()) {
			Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String name = entry.getKey();
				String value = entry.getValue();
				request.setHeader(name, value);
			}
		}
	}

}
