package org.ixming.base.file.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.ixming.base.file.FileCompositor;
import org.ixming.base.file.FileManager;
import org.ixming.base.file.app.LocalFileUtility;
import org.ixming.base.network.HttpClientUtil;
import org.ixming.base.network.HttpRes;
import org.ixming.base.secure.encode.Base64;
import org.ixming.base.utils.android.LogUtils;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WebViewCache {

private static final String TAG = WebViewCache.class.getSimpleName();
	
	public static boolean existsCache(String url, Object token) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		FileCompositor fileCompositor = getFileCompositor(url, token);
		try {
			return (fileCompositor.exists()
					&& fileCompositor.getAbsoluteFile().isFile());
		} finally {
			fileCompositor.recycle();
		}
	}
	
	/**
	 * 工具方法
	 * @param string
	 * @param encoding
	 * @return
	 */
	public static InputStream stringToInputStream(String string,
			String encoding) throws UnsupportedEncodingException {
		try {
			Charset charset;
			if (TextUtils.isEmpty(encoding)) {
				charset = Charset.forName(HEADER_CHARSET);
			} else {
				try {
					charset = Charset.forName(encoding);
				} catch (Exception e) {
					charset = Charset.forName(HEADER_CHARSET);
				}
			}
			byte[] data = string.getBytes(charset);
			return new ByteArrayInputStream(data);
		} catch (Exception e) {
			LogUtils.e(TAG, "stringToInputStream Exception: " + e.getMessage());
			return new ByteArrayInputStream(new byte[]{});
		}
	}
	
	private static final Pattern DATA_URL_PATTERN = Pattern.compile(
			"(?:data[:]\\s*)"
					+ "([^;,\\s*]+)"
					+ "(?:\\s*[;]\\s*)" 
					+ "([^;,\\s*]+)" 
					+ "(?:\\s*[;]\\s*)"
					+ "([^;,\\s*]+)");
	
	private String[] URL_FILTERS = {
		"http://", "https://" /*, "data:"*/
	};
	
	// URL对应的回调Map，因为依靠的是WebViewClient的回调，不能再加载页面URL时判定
	private final HashMap<String, CacheCallback>
		mURLCallbackMap = new HashMap<String, CacheCallback>();
	// URL携带的“版本”信息，因为依靠的是WebViewClient的回调，不能再加载页面URL时判定
	private final HashMap<String, Object>
		mURLTokenMap = new HashMap<String, Object>();
	
	public void addURLCacheCallback(String url, CacheCallback callback) {
		synchronized (mURLCallbackMap) {
			if (null == callback) {
				mURLCallbackMap.remove(url);
			} else {
				mURLCallbackMap.put(url, callback);
			}
		}
	}
	
	public void addURLToken(String url, Object object) {
		synchronized (mURLTokenMap) {
			if (null == object) {
				mURLTokenMap.remove(url);
			} else {
				mURLTokenMap.put(url, object);
			}
		}
	}
	
	private Object getURLToken(String url) {
		synchronized (mURLTokenMap) {
			return mURLTokenMap.get(url);
		}
	}
	
	/**
	 * 设置缓存策略（现阶段根据URL中是否包含某些String来做判断）
	 */
	public void setCacheStrategy(String[] strategies) {
		URL_FILTERS = strategies;
	}
	
	/**
	 * 根据提供的URL，生成WebResourceResponse。
	 * <p>
	 * 	<b>PS：</b>这是一个好事操作。
	 * </p>
	 */
	public WebResourceResponse getWebViewResponse(String url) {
		if (!shouldIntercept(url)) {
			return null;
		}
		Object tokenObject = getURLToken(url);
		FileCompositor fileCompositor = getFileCompositor(url, tokenObject);
		try {
			WebResourceResponse response = null;
			// TODO 
//			response = extractEncodedData(url);
//			if (null != response) {
//				return response;
//			}
			
			// 需要提供一个下载机制
			// 因为需要直接返回WebResourceResponse对象，所以不能使用原先的封装好的回调式的下载
			if (null == response) {
				if (fileCompositor.exists()) {
					response = readFromLocalCache(fileCompositor);
				}
			}
			
			// not found
			if (null == response) {
				if (requestUrlFile(url, fileCompositor)) {
					response = readFromLocalCache(fileCompositor);
				}
			}
			
			// callback
			checkNeedTransform(url, response);
			
			return response;
		} catch (Exception e) {
			LogUtils.e(TAG, "getWebViewResponse Exception: " + e.getMessage());
			e.printStackTrace();
			fileCompositor.deleteFile(true);
		} finally {
			fileCompositor.recycle();
		}
		return null;
	}
	
	private static FileCompositor getFileCompositor(String url, Object token) {
		String comUrl = url;
		if (null != token) {
			comUrl += token.toString();
		}
		FileCompositor fileCompositor = LocalFileUtility
				.getCommonFileByUrl(comUrl, null);
		return fileCompositor;
	}
	
	private void checkNeedTransform(String url, WebResourceResponse response) {
		if (null == response) {
			return ;
		}
		CacheCallback callback;
		synchronized (mURLCallbackMap) {
			// remove or just get
			//callback = mURLCallbackMap.remove(url);
			callback = mURLCallbackMap.get(url);
		}
		if (null == callback) {
			return ;
		}
		InputStream transformedIns = callback.shouldTransformResponse(
				response.getData(), response.getMimeType(),
				response.getEncoding());
		if (null != transformedIns) {
			response.setData(transformedIns);
		}
	}
	
	@SuppressWarnings("unused")
	private String findFromLocalByUrl(String url) {
		if (!shouldIntercept(url)) {
			return null;
		}
		FileCompositor fileCompositor = LocalFileUtility
				.getCommonFileByUrl(url, null);
		try {
			if (fileCompositor.exists()) {
				return "file://" + fileCompositor.getAbsoluteFile()
						.getAbsolutePath();
			}
		} finally {
			fileCompositor.recycle();
		}
		return null;
	}
	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// internal implements
	/**
	 * 对应的URL是否需要拦截
	 */
	private boolean shouldIntercept(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		for (int i = 0; i < URL_FILTERS.length; i++) {
			if (url.contains(URL_FILTERS[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 有形如：data:&lt;mime&gt;;&lt;coding&gt;:&lt;data&gt;形式的URL，
	 * 需要特殊处理
	 */
	@SuppressWarnings("unused")
	private WebResourceResponse extractEncodedData(String url) {
		Matcher matcher = DATA_URL_PATTERN.matcher(url);
		if (matcher.find()) {
			LogUtils.d(TAG, "extractEncodedData matcher find!");
			String mimeType = matcher.group(1);
			String encodeType = matcher.group(2);
			String encodedString = matcher.group(3);
			if ("base64".equalsIgnoreCase(encodeType)) {
				return new WebResourceResponse(mimeType, encodeType,
						new ByteArrayInputStream(Base64.decode(encodedString)));
			}
		}
		return null;
	}
	
	
	private static final String HEADER_CHARSET = "UTF-8";
	private boolean requestUrlFile(String url,
			FileCompositor fileCompositor) throws Exception {
		boolean flag = false;
		HttpRes httpRes = null;
		try {
			httpRes = HttpClientUtil.proxyHttpGet(url, null);
			HttpEntity entity = httpRes.getEntity();
			if (fileCompositor.createNewFile()) {
				String contentType = null;
				try {
					contentType = entity.getContentType().getValue();
				} catch (Exception e) { }
				
				String contentEncoding = null;
				try {
					contentEncoding = entity.getContentEncoding().getValue();
				} catch (Exception e) { }
				
				// adjust values
				String regex = "(?:\\s*)"
						+ "([^=;,\\s*]+)"
						+ "(?:[;]\\s*)"
						+ "(?i:charset[=\\s]+)"
						+ "([^=;,\\s*]+)";
				if (null != contentType && null == contentEncoding) {
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(contentType);
					if (m.find()) {
						contentType = m.group(1);
						contentEncoding = m.group(2);
					}
				}
				
				OutputStream out = fileCompositor.openFileOutput(true);
				// write
				StringBuilder sBuilder = new StringBuilder(FileManager.FILE_BUFFER_SIZE);
				sBuilder.append(MARK);
				sBuilder.append("\n");
				sBuilder.append(encValue(contentType));
				sBuilder.append("\n");
				sBuilder.append(encValue(contentEncoding));
				sBuilder.append("\n");
				byte[] headerArrDest = new byte[FileManager.FILE_BUFFER_SIZE];
				byte[] headerArr = sBuilder.toString().getBytes(HEADER_CHARSET);
				System.arraycopy(headerArr, 0, headerArrDest, 0, headerArr.length);
				headerArr = null;
				ByteArrayOutputStream headerOut = new ByteArrayOutputStream(
						FileManager.FILE_BUFFER_SIZE);
				headerOut.write(headerArrDest);
				headerOut.writeTo(out);
				headerOut.flush();
				headerOut.close();
				headerOut = null;
				
				out.flush();
				out.close();
				out = null;
				headerArrDest = null;
				
				// save byte data
				fileCompositor.save(entity.getContent(), true);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != httpRes) {
				httpRes.abort();
			}
			if (!flag) {
				fileCompositor.deleteFile(true);
			}
		}
		return flag;
	}
	
	private WebResourceResponse readFromLocalCache(
			FileCompositor fileCompositor){
		if (!fileCompositor.exists()) {
			return null;
		}
		try {
			byte[] buf = new byte[FileManager.FILE_BUFFER_SIZE];
			InputStream ins = fileCompositor.openFileInput();
			ins.read(buf);
			BufferedReader reader = new BufferedReader(
					new StringReader(new String(buf, HEADER_CHARSET)), 
					FileManager.FILE_BUFFER_SIZE);
			String mark = reader.readLine();
			try {
				if (MARK.equals(mark)) {
					String mimeType = getValue(reader.readLine());
					String encoding = getValue(reader.readLine());
					return new WebResourceResponse(mimeType, encoding, ins);
				}
			} finally {
				reader.close();
			}
			fileCompositor.deleteFile(true);
			throw new UnsupportedOperationException("invalid file data!");
		} catch (Exception e) {
			LogUtils.e(TAG, "readFromLocalCache Exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String MARK = "-" + WebViewCache.class.getName() + "-";
	private final String NULL = "null";
	private String encValue(String val) {
		if (null == val || "".equals(val)) {
			return NULL;
		}
		return val;
	}
	private String getValue(String val) {
		if (NULL.equals(val)) {
			return null;
		}
		return val;
	}
}