package org.ixming.base.network.core;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.ixming.base.io.IOConstants;
import org.ixming.base.network.HttpClientUtil;

public class DefHttpClientFactory implements IHttpClientFactory {

	public DefHttpClientFactory() {
	}
	
	@Override
	public HttpClient createHttpClient() throws Exception {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, IOConstants.DEF_CHARSET);
		HttpProtocolParams.setUserAgent(params, HttpClientUtil.getUserAgentString());
		HttpConnectionParams.setConnectionTimeout(params, IOConstants.DEF_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, IOConstants.DEF_TIMEOUT);
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		
		KeyStore trustStore = KeyStore.getInstance(KeyStore
				.getDefaultType());
		trustStore.load(null, null);
		SSLSocketFactory sf = new DefSSLSocketFactory(trustStore);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		registry.register(new Scheme("https", sf, 443));
		
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
		return new DefaultHttpClient(ccm, params);
	}

}
