package org.ixming.base.network.state;

import org.ixming.base.network.HttpResult;

/**
 * 模糊的响应类型
 * 
 * @author Yin Yong
 *
 */
public enum BlurryReponseCode {
	
	/**
	 * connect failure or other error during requesting
	 * 
	 * @see {@link HttpResult}
	 */
	BeforeConnection,
	
	/**
	 * connect successfully to Server, and status code is 200
	 */
	Success,
	
	/**
	 * connect successfully to Server, and status code is 3xx
	 */
	Redirection,
	
	/**
	 * connect successfully to Server, and status code is 4xx
	 */
	ClientError,
	
	/**
	 * connect successfully to Server, and status code is 5xx
	 */
	ServerError;
	
}
