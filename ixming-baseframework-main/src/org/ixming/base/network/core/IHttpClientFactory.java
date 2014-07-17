package org.ixming.base.network.core;

import org.apache.http.client.HttpClient;

/**
 * 创建HttpClient实例的工厂接口
 * 
 * @author Yin Yong
 *
 */
public interface IHttpClientFactory {

	/**
	 * 创建新的HttpClient对象
	 * 
	 * @return 创建的HttpClient对象
	 */
	HttpClient createHttpClient() throws Exception;
	
}
