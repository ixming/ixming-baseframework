package org.ixming.base.network;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class HttpRes {
	private HttpEntity entity = null;
	private HttpPost httpPost = null;
	private HttpGet httpGet = null;
	private HttpDelete httpDelete = null;
	private HttpPut httpPut = null;

	public HttpGet getHttpGet() {
		return httpGet;
	}

	public void setHttpGet(HttpGet httpGet) {
		this.httpGet = httpGet;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public HttpPost getHttpPost() {
		return httpPost;
	}

	public void setHttpPost(HttpPost httpPost) {
		this.httpPost = httpPost;
	}

	public HttpDelete getHttpDelete() {
		return httpDelete;
	}

	public void setHttpDelete(HttpDelete httpDelete) {
		this.httpDelete = httpDelete;
	}

	public HttpPut getHttpPut() {
		return httpPut;
	}

	public void setHttpPut(HttpPut httpPut) {
		this.httpPut = httpPut;
	}

	public void abort() {
		if (getHttpGet() != null) {
			getHttpGet().abort();
		}
		if (getHttpPost() != null) {
			getHttpPost().abort();
		}
		if (getHttpDelete() != null) {
			getHttpDelete().abort();
		}
		if (getHttpPut() != null) {
			getHttpPut().abort();
		}
	}
}
