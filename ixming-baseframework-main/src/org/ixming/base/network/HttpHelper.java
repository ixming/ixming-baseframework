package org.ixming.base.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.ixming.base.io.IOConstants;
import org.ixming.base.network.core.IXMRequest;
import org.ixming.base.network.simple.BasicXMRequest;
import org.ixming.base.utils.android.FrameworkLog;

/**
 * 一些请求相关的辅助方法
 * 
 * @author Yin Yong
 *
 */
public class HttpHelper {

	private static final String TAG = HttpHelper.class.getSimpleName();
	private HttpHelper() { }
	
	private static IXMRequest defHeaders(IXMRequest xmRequest) {
		xmRequest.header("Accept", "application/json;q=1.0,image/*;q=0.9,*/*;q=0.3");
		xmRequest.header("Accept-Encoding", "gzip;q=1.0,identity;q=0.8,deflate;q=0.3");
		xmRequest.header("Accept-Language", "zh-CN,zh;q=0.8");
		xmRequest.header("Accept-Charset", "GBK,utf-8,iso-8859-1,utf-16;q=0.7,*;q=0.3");
		return xmRequest;
	}
	
	public static HttpUriRequest get(String url, Map<String, String> params) {
		return defHeaders(BasicXMRequest.newInstance().url(url).method(HttpMethod.Get)
				.params(params)).create();
	}
	
	public static HttpUriRequest post(String url, Map<String, String> params) {
		return defHeaders(BasicXMRequest.newInstance().url(url).method(HttpMethod.Post)
				.params(params)).create();
	}
	
	public static HttpUriRequest put(String url, Map<String, String> params) {
		return defHeaders(BasicXMRequest.newInstance().url(url).method(HttpMethod.Put)
				.params(params)).create();
	}
	
	public static HttpUriRequest post(String url, HttpEntity entity) {
		return defHeaders(BasicXMRequest.newInstance().url(url).method(HttpMethod.Post)
				.entity(entity)).create();
	}
	
	public static HttpUriRequest put(String url, HttpEntity entity) {
		return defHeaders(BasicXMRequest.newInstance().url(url).method(HttpMethod.Put)
				.entity(entity)).create();
	}
	
	private static final char QP_SEP_QM = '?';
	private static final char QP_SEP_A = '&';
    private static final String NAME_VALUE_SEPARATOR = "=";
    
    /**
     * 结合URL和params（一般用于GET方式的URL拼凑）
     */
	public static String urlParams(String url, Map<String, String> params) {
		if (null != params && !params.isEmpty()) {
			StringBuilder urlBuilder = new StringBuilder((url.length() + 1) * 2);
			urlBuilder.append(url);
			urlBuilder.append(QP_SEP_QM);
			
			Iterator<Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String name = entry.getKey();
				String value = entry.getValue();
				try {
					urlBuilder
						.append(name).append(NAME_VALUE_SEPARATOR)
						.append(URLEncoder.encode(value, IOConstants.DEF_CHARSET));
	            } catch (UnsupportedEncodingException e) {
	            	FrameworkLog.w(TAG, "urlParams Exception: " + e.getMessage());
	            	e.printStackTrace();
	            	urlBuilder
						.append(name).append(NAME_VALUE_SEPARATOR).append(value);
	            }
				if (iter.hasNext()) {
					urlBuilder.append(QP_SEP_A);
				}
			}
			url = urlBuilder.toString();
		}
		return url;
	}
	
	/**
	 * 简单的POST表单（URL加密方式）数据拼凑成HttpEntity
	 */
	public static HttpEntity urlEncodedFormEntity(Map<String, String> params, String encoding)
			throws UnsupportedEncodingException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
		if (null != params && !params.isEmpty()) {
			Iterator<Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String name = entry.getKey();
				String value = entry.getValue();
				pairs.add(new BasicNameValuePair(name, value));
			}
		}
		if (null == encoding) {
			encoding = IOConstants.DEF_CHARSET;
		}
		return new UrlEncodedFormEntity(pairs, encoding);
	}
	
	/**
	 * POST（多结构数据）数据拼凑成HttpEntity
	 */
	public static HttpEntity multipartEntity(Map<String, String> params, String encoding) 
			throws UnsupportedEncodingException {
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		Charset cs;
		try {
			cs = Charset.forName(encoding);
		} catch (Exception e) {
			cs = Charset.defaultCharset();
		}
		if (null != params && !params.isEmpty()) {
			Iterator<Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String name = entry.getKey();
				String value = entry.getValue();
				entity.addPart(new FormBodyPart(name, new StringBody(value, cs)));
			}
		}
		return entity;
	}
	
	/**
	 * 为request添加请求头
	 */
	public static void setHeaders(HttpRequest request, Map<String, String> headers) {
		if (null == headers || headers.isEmpty()) {
			return ;
		}
		Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String name = entry.getKey();
			String value = entry.getValue();
			request.setHeader(name, value);
		}
	}
	
	public static void abortRequest(HttpUriRequest request) {
		if (null == request) {
			return ;
		}
		try {
			request.abort();
		} catch (Exception e) { }
	}
	
}
