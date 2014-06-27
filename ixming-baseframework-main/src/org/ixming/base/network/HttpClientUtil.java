package org.ixming.base.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.ixming.base.common.BaseApplication;
import org.ixming.base.network.utils.NetWorkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;


public class HttpClientUtil {
	final static String TAG = "HttpClientUtil";
	final static int TIMEOUT = 35 * 1000;
	private static HttpClient httpClient = null;
	public static final int GET = 0;
	public static final int POST = 1;
	public static final int PUT = 2;
	public static final int DELETE = 3;

	public static HttpClient getNewHttpClient() {
		try {
			if (httpClient == null) {
				KeyStore trustStore = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trustStore.load(null, null);
				SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, "UTF-8");
				HttpProtocolParams.setUserAgent(params, getUserAgentString());
				HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
				HttpConnectionParams.setSoTimeout(params, TIMEOUT);
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));
				ClientConnectionManager ccm = new ThreadSafeClientConnManager(
						params, registry);
				httpClient = new DefaultHttpClient(ccm, params);
			}
			getUserAgentString();
			httpClient = setNetWork(BaseApplication.getAppContext(), httpClient);
		} catch (Exception e) {
			Log.e(TAG,
					"HttpClientUtil   getNewHttpClient Exception "
							+ e.getMessage());
			e.printStackTrace();
		}
		return httpClient;
	}

	// TODO 获取并设置设备请求的UA
	private static String getUserAgentString() {
		String osType = "Android";
		String sdkVersion = android.os.Build.VERSION.RELEASE;
		String device = android.os.Build.MODEL;
		String id = android.os.Build.ID;
		return String.format("Mozilla/5.0 (Linux; U; %s %s; %s Build/%s)",
				osType, sdkVersion, device, id);
		// Mozilla/5.0 (Linux; U; Android 4.3; en-us; HTC One - 4.3 - API 18 -
		// 1080x1920 Build/JLS36G)
	}

	private static HttpClient setNetWork(Context context, HttpClient client) {
		String netType = NetworkManager
				.getNetWorkType((ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE));
		if (!NetWorkUtils.WIFI_STATE.equalsIgnoreCase(netType)) {
			if (!NetWorkUtils.isOPhone()) {
				Map<String, Object> map = NetworkManager.getProxy();
				if (map != null && !map.isEmpty()) {
					if (android.os.Build.VERSION.SDK_INT <= 7) {
						String proxyHost = (String) map
								.get(NetworkManager.PROXY_HOST);
						int proxyPort = (Integer) map
								.get(NetworkManager.PROXY_PORT);
						HttpHost proxy = new HttpHost(proxyHost, proxyPort);
						client.getParams().setParameter(
								ConnRoutePNames.DEFAULT_PROXY, proxy);
					}
				} else {
					// cmnet set proxy
					client.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, null);
				}
			}

		} else if (NetWorkUtils.WIFI_STATE.equalsIgnoreCase(netType)) {
			client.getParams()
					.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
		return client;

	}

	public static HttpRes proxyHttpGet(String url, Map<String, String> data) {
		HttpGet httpGet = null;
		HttpRes res = null;
		try {
			url = addParams(url, data);
			httpGet = new HttpGet(url);
			httpClient = getNewHttpClient();
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				StatusLine line = response.getStatusLine();
				if (line != null) {
					int resCode = line.getStatusCode();
					if (resCode == HttpStatus.SC_OK) {
						res = new HttpRes();
						res.setEntity(response.getEntity());
						res.setHttpGet(httpGet);
					}
				}
			}
		} catch (Exception e) {
			if (httpGet != null) {
				httpGet.abort();
			}
			Log.e(TAG, "get() Exception -- " + e.toString());
			e.printStackTrace();
			res = null;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return res;
	}

	public static HttpRes proxyHttpDelete(Context context, String url,
			Map<String, String> data) {
		HttpDelete httpDelete = null;
		HttpResponse response = null;
		HttpRes res = null;
		try {
			addParams(url, data);
			httpDelete = new HttpDelete(url);
			httpClient = getNewHttpClient();
			response = httpClient.execute(httpDelete);
			if (response != null) {
				StatusLine line = response.getStatusLine();
				if (line != null) {
					int resCode = line.getStatusCode();
					Log.w(TAG, " httpClientUtil resCode" + resCode);
					if (resCode == HttpStatus.SC_OK) {
						res = new HttpRes();
						res.setEntity(response.getEntity());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = null;
			if (httpDelete != null) {
				httpDelete.abort();
			}
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return res;
	}

	public static HttpRes proxyHttpPost(String url, Map<String, String> data) {
		String logHeader = "url : " + url;
		HttpPost httpPost = null;
		HttpResponse response = null;
		httpClient = getNewHttpClient();
		HttpRes res = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.addHeader("Accept", "application/json, */*; q=0.01");
			httpPost.addHeader("Accept-Encoding", "gzip,deflate");
			httpPost = addParams(httpPost, data);
			response = httpClient.execute(httpPost);
			if (response != null) {
				StatusLine line = response.getStatusLine();
				Log.i(TAG, logHeader + " proxyHttpPost StatusLine------->"
						+ line);
				if (line != null) {
					int resCode = line.getStatusCode();
					Log.i(TAG, logHeader + "proxyHttpPost resCode -- >"
							+ resCode);
					if (resCode > 199 && resCode < 300) {
						res = new HttpRes();
						res.setEntity(response.getEntity());
						res.setHttpPost(httpPost);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, logHeader + " proxyHttpPost connect : " + url
					+ " Exception  " + e.getMessage());
			res = null;
			if (httpPost != null) {
				httpPost.abort();
			}
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return res;
	}

	/**
	 * 使用中...
	 */
	public static HttpRes proxyHttpPostFile(String url, String json,
			String sign, Bundle reqExtras) {
		if (null == reqExtras || reqExtras.isEmpty()) {
			return null;
		}
		String logHeader = "proxyHttpPostFile url : " + url + " json : " + json;
		HttpPost httpPost = null;
		HttpResponse response = null;
		httpClient = getNewHttpClient();
		HttpRes res = null;
		try {
			httpPost = new HttpPost(url);
			final Charset cs = Charset.forName("UTF-8");
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart(new FormBodyPart("p", new StringBody(json, cs)));
			entity.addPart(new FormBodyPart("sign", new StringBody(sign)));
			for (String key : reqExtras.keySet()) {
				ByteArrayOutputStream out = null;
				FileInputStream in = null;
				try {
					File file = new File(reqExtras.getString(key));
					if (file.exists()) {
						out = new ByteArrayOutputStream();
						in = new FileInputStream(file);
						int len = -1;
						byte buf[] = new byte[512];
						while (-1 != (len = in.read(buf))) {
							out.write(buf, 0, len);
						}
						out.flush();
						ByteArrayBody picBody = new ByteArrayBody(
								out.toByteArray(), MimeTypeMap.getSingleton()
										.getMimeTypeFromExtension("jpg"),
								file.getName());
						entity.addPart(new FormBodyPart(key, picBody));
					}
				} catch (Exception e) {
					Log.e(TAG, "proxyHttpPostFile Exception: " + e.getMessage());
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (Exception e_in) {
						}
					}
					if (null != out) {
						try {
							out.close();
						} catch (Exception e_out) {
						}
					}
				}
			}
			httpPost.setEntity(entity);

			httpPost.addHeader("Accept",
					"application/json, image/*, */*; q=0.01");
			httpPost.addHeader("Accept-Encoding", "gzip,deflate");
			response = httpClient.execute(httpPost);
			if (response != null) {
				StatusLine line = response.getStatusLine();

				if (line != null) {
					int resCode = line.getStatusCode();
					if (resCode >= HttpStatus.SC_OK && resCode < 300) {
						res = new HttpRes();
						res.setEntity(response.getEntity());
						res.setHttpPost(httpPost);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, logHeader + " proxyHttpPostFile connect : " + url
					+ " Exception  " + e.getMessage());
			res = null;
			if (httpPost != null) {
				httpPost.abort();
			}
		} finally {
			if (res == null && httpPost != null) {
				httpPost.abort();
			}
			if (httpClient != null) {
				httpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return res;
	}

	/**
	 * 使用中...
	 */
	public static HttpRes proxyHttpPutFile(Context context, String url,
			Map<String, String> data, Bundle reqExtras) {
		if (null == reqExtras || reqExtras.isEmpty()) {
			return null;
		}
		HttpPut httpPut = null;
		HttpResponse response = null;
		httpClient = getNewHttpClient();
		HttpRes res = null;
		try {
			httpPut = new HttpPut(url);
			final Charset cs = Charset.forName("UTF-8");
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String val = data.get(key);
				entity.addPart(new FormBodyPart(key, new StringBody(key)));
				entity.addPart(new FormBodyPart(val, new StringBody(val)));
			}
			for (String key : reqExtras.keySet()) {
				ByteArrayOutputStream out = null;
				FileInputStream in = null;
				try {
					File file = new File(reqExtras.getString(key));
					if (file.exists()) {
						out = new ByteArrayOutputStream();
						in = new FileInputStream(file);
						int len = -1;
						byte buf[] = new byte[512];
						while (-1 != (len = in.read(buf))) {
							out.write(buf, 0, len);
						}
						out.flush();
						ByteArrayBody picBody = new ByteArrayBody(
								out.toByteArray(), MimeTypeMap.getSingleton()
										.getMimeTypeFromExtension("jpg"),
								file.getName());
						entity.addPart(new FormBodyPart(key, picBody));
					}
				} catch (Exception e) {
					Log.e(TAG, "proxyHttpPostFile Exception: " + e.getMessage());
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (Exception e_in) {
						}
					}
					if (null != out) {
						try {
							out.close();
						} catch (Exception e_out) {
						}
					}
				}
			}
			httpPut.setEntity(entity);
			httpPut.addHeader("Accept",
					"application/json, image/*, */*; q=0.01");
			httpPut.addHeader("Accept-Encoding", "gzip,deflate");
			response = httpClient.execute(httpPut);
			if (response != null) {
				StatusLine line = response.getStatusLine();
				if (line != null) {
					int resCode = line.getStatusCode();
					if (resCode >= HttpStatus.SC_OK && resCode < 300) {
						res = new HttpRes();
						res.setEntity(response.getEntity());
						res.setHttpPut(httpPut);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = null;
			if (httpPut != null) {
				httpPut.abort();
			}
		} finally {
			if (res == null && httpPut != null) {
				httpPut.abort();
			}
			if (httpClient != null) {
				httpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return res;
	}

	private static HttpPost addParams(HttpPost httpPost,
			Map<String, String> data) {
		UrlEncodedFormEntity urlEncode = null;
		List<NameValuePair> params = null;
		try {
			Log.i(TAG, "addParams httpPost------>" + httpPost);
			params = new ArrayList<NameValuePair>();
			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String val = data.get(key);
				params.add(new BasicNameValuePair(key, val));
			}
			Log.i(TAG, "params size " + params.size());
			urlEncode = new UrlEncodedFormEntity(params, "utf-8");
			httpPost.setEntity(urlEncode);

		} catch (Exception e) {
			Log.e(TAG, "addParams Exception " + e.getMessage());
		} finally {
			if (params != null) {
				params.clear();
				params = null;
			}
			if (urlEncode != null) {
				urlEncode = null;
			}

		}
		return httpPost;
	}

	@SuppressWarnings("unused")
	private static HttpDelete addParams(HttpDelete httpDelete,
			Map<String, String> data) {
		UrlEncodedFormEntity urlEncode = null;
		List<NameValuePair> params = null;
		try {
			Log.i(TAG, "addParams httpDelete------>" + httpDelete);
			params = new ArrayList<NameValuePair>();
			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String val = data.get(key);
				params.add(new BasicNameValuePair(key, val));
			}
			Log.i(TAG, "params size " + params.size());
			urlEncode = new UrlEncodedFormEntity(params, "utf-8");
		} catch (Exception e) {
			Log.e(TAG, "httpDelete  addParams Exception " + e.getMessage());
		} finally {
			if (params != null) {
				params.clear();
				params = null;
			}
			if (urlEncode != null) {
				urlEncode = null;
			}

		}
		return httpDelete;
	}

	private static String addParams(String url, Map<String, String> data) {
		if (data != null) {
			Iterator<String> iter = data.keySet().iterator();
			boolean flag = false;
			StringBuilder sb = new StringBuilder(url);
			while (iter.hasNext()) {
				if (flag) {
					sb.append("&");
				} else {
					sb.append("?");
					flag = true;
				}
				String key = iter.next();
				String val = data.get(key);
				sb.append(key + "=");
				sb.append(val);
			}
			url = sb.toString();
		}
		return url;
	}

	@SuppressWarnings("finally")
	public static File downloadImageFile(String url, File file) {
		HttpEntity entity = null;
		InputStream conIn = null;
		DataInputStream in = null;
		OutputStream out = null;
		httpClient = getNewHttpClient();
		HttpGet httpGet = null;
		long totalSize = 0;
		try {
			long startTime = System.currentTimeMillis();
			Log.i("downImage", url + " downImage start-----" + startTime);
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			Log.i("downImage", url + " downloadImageFile httpResponse --->"
					+ httpResponse);
			if (httpResponse != null) {
				long endTime = System.currentTimeMillis();
				Log.i("downImage", url + " downImage end-----" + endTime
						+ " use time :" + ((endTime - startTime) / 1000));
				StatusLine line = httpResponse.getStatusLine();
				Log.i("downImage", url + " downloadImageFile line --->" + line);
				if (line != null) {
					int responseCode = line.getStatusCode();
					if (responseCode == HttpStatus.SC_OK) {
						entity = httpResponse.getEntity();
						if (entity != null) {
							conIn = entity.getContent();
							totalSize = entity.getContentLength();
							in = new DataInputStream(conIn);
							out = new FileOutputStream(file);
							byte[] buffer = new byte[1024];
							int byteread = 0;
							while ((byteread = in.read(buffer)) != -1) {
								out.write(buffer, 0, byteread);
							}
						} else {
							if (file != null) {
								file.delete();
								file = null;
							}
						}

					} else {
						Log.i("downImage",
								url
										+ " downLoadImage Server return error, response code = "
										+ responseCode);
						if (file != null) {
							file.delete();
							file = null;
						}
					}
				} else {
					if (file != null) {
						file.delete();
						file = null;
					}
					Log.i("downImage", url
							+ " Server return error, StatusLine  " + line);
				}

			} else {
				if (file != null) {
					file.delete();
					file = null;
				}
				Log.i("downImage", url + " Server return error, httpResponse  "
						+ httpResponse);
			}

		} catch (Exception e) {
			Log.e("downImage",
					url + " downImage Exception -----" + e.getMessage());
			if (file != null) {
				file.delete();
				file = null;
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		} finally {
			if (file != null) {
				if (file.length() != totalSize) {
					file.delete();
				}
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conIn != null) {
					conIn.close();
				}
				if (entity != null) {
					entity.consumeContent();
				}
				if (httpGet != null) {
					httpGet.abort();
					httpGet = null;
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().closeExpiredConnections();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return file;
		}
	}

	public static void downloadFile(String url, File file) {
		HttpEntity entity = null;
		InputStream conIn = null;
		DataInputStream in = null;
		OutputStream out = null;
		httpClient = getNewHttpClient();
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
				entity = httpResponse.getEntity();
				conIn = entity.getContent();
				in = new DataInputStream(conIn);
				out = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int byteread = 0;
				while ((byteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, byteread);
				}

			} else {
				if (file != null) {
					file.delete();
					file = null;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + "");
			e.printStackTrace();
			if (file != null) {
				file.delete();
				file = null;
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conIn != null) {
					conIn.close();
				}
				if (entity != null) {
					entity.consumeContent();
				}
				if (httpGet != null) {
					httpGet.abort();
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().closeExpiredConnections();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 用于下载html 以及添加了过滤HTML
	 * 
	 * @param url
	 * @param params
	 * @param file
	 *            下载的位置
	 */
	public static void downloadHtmlFile(String url, File file) {
		HttpEntity entity = null;
		InputStream conIn = null;
		DataInputStream in = null;
		OutputStream out = null;
		httpClient = getNewHttpClient();
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
				entity = httpResponse.getEntity();
				conIn = entity.getContent();
				in = new DataInputStream(conIn);
				out = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int byteread = 0;
				while ((byteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, byteread);
				}
				/**
				 * 过滤代码
				 */
				// StringFilterUtil.htmlFilter(file.getPath());
			} else {
				if (file != null) {
					file.delete();
					file = null;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + "");
			e.printStackTrace();
			if (file != null) {
				file.delete();
				file = null;
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conIn != null) {
					conIn.close();
				}
				if (entity != null) {
					entity.consumeContent();
				}
				if (httpGet != null) {
					httpGet.abort();
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().closeExpiredConnections();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
