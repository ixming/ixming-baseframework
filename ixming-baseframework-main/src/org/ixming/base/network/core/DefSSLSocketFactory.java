package org.ixming.base.network.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.ixming.base.utils.android.LogUtils;

/**
 * 默认的SSL Socket创建类
 * 
 * @author Yin Yong
 *
 */
public class DefSSLSocketFactory extends SSLSocketFactory {
	
	private static final String TAG = DefSSLSocketFactory.class.getSimpleName();
	
	SSLContext sslContext = null;

	public DefSSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		sslContext = SSLContext.getInstance("TLS");
		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			public X509Certificate[] getAcceptedIssuers() {

				return null;

			}
		};
		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		LogUtils.w(TAG, "socket--->" + socket);
		LogUtils.w(TAG, "host--->" + host);
		LogUtils.w(TAG, "port--->" + port);
		LogUtils.w(TAG, "autoClose--->" + autoClose);
		if (port == -1) {
			port = 443;
		}
		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
}
