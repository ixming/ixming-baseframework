package org.ixming.base.network;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * enum for HTTP methods
 * 
 * @author Yin Yong
 *
 */
public enum HttpMethod {

	Get {
		@Override
		public HttpUriRequest newHttpRequest(String url) {
			return new HttpGet(url);
		}
	},
	
	Post {
		@Override
		public HttpUriRequest newHttpRequest(String url) {
			return new HttpPost(url);
		}
	},
	
	Put {
		@Override
		public HttpUriRequest newHttpRequest(String url) {
			return new HttpPut(url);
		}
	},
	
	
	Delete {
		@Override
		public HttpUriRequest newHttpRequest(String url) {
			return new HttpDelete(url);
		}
	}
	
	// and so on...
	;
	
	/**
	 * @throws IllegalArgumentException  if the uri is invalid
	 */
	public abstract HttpUriRequest newHttpRequest(String url) throws IllegalArgumentException;
	
}