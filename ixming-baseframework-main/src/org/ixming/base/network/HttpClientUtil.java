package org.ixming.base.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.ixming.base.network.core.DefHttpClientFactory;
import org.ixming.base.network.core.HttpClientProxyHelper;
import org.ixming.base.network.core.IHttpClientFactory;
import org.ixming.base.utils.android.AndroidUtils;

import android.Manifest.permission;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class HttpClientUtil {
	
	private final static String TAG = "HttpClientUtil";
	public static final int GET = 0;
	public static final int POST = 1;
	public static final int PUT = 2;
	public static final int DELETE = 3;
	
	private static HttpClient sHttpClient = null;
	private static final IHttpClientFactory sDefHttpClientFactory = new DefHttpClientFactory();
	private static IHttpClientFactory sHttpClientFactory;
	static {
		setHttpClientFactory(sDefHttpClientFactory);
	}
	
	/**
	 * 设置自定义的IHttpClientFactory
	 */
	public static void setHttpClientFactory(IHttpClientFactory factory) {
		if (null == factory || factory == sHttpClientFactory) {
			return ;
		}
		synchronized (HttpClientUtil.class) {
			sHttpClientFactory = factory;
			sHttpClient = null;
			AndroidUtils.requestPermission(permission.ACCESS_NETWORK_STATE);
			AndroidUtils.requestPermission(permission.INTERNET);
			try {
				sHttpClient = sHttpClientFactory.createHttpClient();
				HttpClientProxyHelper.proxy(sHttpClient);
			} catch (Exception e) {
				// 1、java.lang.SecurityException: 
				// 	content: ConnectivityService: 
				// 	Neither user 10066 nor current process has android.permission.XXX.

				Log.e(TAG, "setHttpClientFactory Exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取当前的HttpClient
	 */
	public static HttpClient getHttpClient() {
		return sHttpClient;
	}

	// TODO 获取并设置设备请求的UA
	public static String getUserAgentString() {
		String osType = "Android";
		String sdkVersion = android.os.Build.VERSION.RELEASE;
		String device = android.os.Build.MODEL;
		String id = android.os.Build.ID;
		return String.format("Mozilla/5.0 (Linux; U; %s %s; %s Build/%s)",
				osType, sdkVersion, device, id);
		// Mozilla/5.0 (Linux; U; Android 4.3; en-us; HTC One - 4.3 - API 18 -
		// 1080x1920 Build/JLS36G)
	}
	
//	public static Object httpRequest() {
//		
//	}

	public static HttpRes proxyHttpGet(String url, Map<String, String> data) {
		HttpGet httpGet = null;
		HttpRes res = null;
		try {
			url = addParams(url, data);
			httpGet = new HttpGet(url);
			sHttpClient = getHttpClient();
			HttpResponse response = sHttpClient.execute(httpGet);
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
			if (sHttpClient != null) {
				sHttpClient.getConnectionManager().closeExpiredConnections();
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
			sHttpClient = getHttpClient();
			response = sHttpClient.execute(httpDelete);
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
			if (sHttpClient != null) {
				sHttpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return res;
	}

	public static HttpRes proxyHttpPost(String url, Map<String, String> data) {
		String logHeader = "url : " + url;
		HttpPost httpPost = null;
		HttpResponse response = null;
		sHttpClient = getHttpClient();
		HttpRes res = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.addHeader("Accept", "application/json, */*; q=0.01");
			httpPost.addHeader("Accept-Encoding", "gzip,deflate");
			httpPost = addParams(httpPost, data);
			response = sHttpClient.execute(httpPost);
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
			if (sHttpClient != null) {
				sHttpClient.getConnectionManager().closeExpiredConnections();
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
		sHttpClient = getHttpClient();
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
			response = sHttpClient.execute(httpPost);
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
			if (sHttpClient != null) {
				sHttpClient.getConnectionManager().closeExpiredConnections();
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
		sHttpClient = getHttpClient();
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
			response = sHttpClient.execute(httpPut);
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
			if (sHttpClient != null) {
				sHttpClient.getConnectionManager().closeExpiredConnections();
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
		sHttpClient = getHttpClient();
		HttpGet httpGet = null;
		long totalSize = 0;
		try {
			long startTime = System.currentTimeMillis();
			Log.i("downImage", url + " downImage start-----" + startTime);
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = sHttpClient.execute(httpGet);
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
				if (sHttpClient != null) {
					sHttpClient.getConnectionManager().closeExpiredConnections();
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
		sHttpClient = getHttpClient();
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = sHttpClient.execute(httpGet);
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
				if (sHttpClient != null) {
					sHttpClient.getConnectionManager().closeExpiredConnections();
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
		sHttpClient = getHttpClient();
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = sHttpClient.execute(httpGet);
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
				if (sHttpClient != null) {
					sHttpClient.getConnectionManager().closeExpiredConnections();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
