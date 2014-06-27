package org.ixming.base.network.json;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;
import org.ixming.base.network.HttpRes;

import android.util.Log;

import com.google.gson.stream.JsonReader;

public class GsonHelper {
	private final static String TAG = GsonHelper.class.getSimpleName();

	public GsonHelper() {
	}

	public static String getJson(HttpRes httpRes) {
		HttpEntity entity = httpRes.getEntity();
		StringBuilder entityStringBuilder = new StringBuilder();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(entity.getContent(), "UTF-8"),
					8 * 1024);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				entityStringBuilder.append(line);
			}
		} catch (Exception e) {
			Log.e(TAG, "execute getJson Exception :" + e.getMessage());
			e.printStackTrace();
		} finally {
			httpRes.abort();
		}
		return entityStringBuilder.toString();
	}

	/**
	 * 
	 * @param httpRes
	 * @return
	 */
	public static String getJsonGZIP(HttpRes httpRes) {
		HttpEntity entity = httpRes.getEntity();
		InputStream inputStream = null;
		InputStream ginputStream = null;
		JsonReader reader = null;
		String result = "";
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			// bytes = toByteArray(inputStream, entity.getContentLength());
			// int ret = (int) ((bytes[0] << 8) | bytes[1] & 0xFF);
			// boolean isGzip = ret == 0x1f8b;
			// if (isGzip) {
			ginputStream = new GZIPInputStream(inputStream);
			reader = new JsonReader(new InputStreamReader(ginputStream, "UTF-8"));
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = ginputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			result = new String(content.toByteArray());
			// }
			if (inputStream != null) {
				inputStream.close();
			}
			if (ginputStream != null) {
				ginputStream.close();
			}
			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "getJsonGZIP exception:" + e.getMessage());
			e.printStackTrace();
		} finally {
			httpRes.abort();
		}
		return result;
	}

	public static byte[] toByteArray(InputStream instream, long contentLength)
			throws IOException {

		if (instream == null) {
			return new byte[] {};
		}
		if (contentLength > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"HTTP entity too large to be buffered in memory");
		}
		int i = (int) contentLength;
		if (i < 0) {
			i = 4096;
		}
		ByteArrayBuffer buffer = new ByteArrayBuffer(i);
		try {
			byte[] tmp = new byte[4096];
			int l;
			while ((l = instream.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			instream.close();
		}
		return buffer.toByteArray();
	}

	public static final InputStream byteToInput(byte[] buf) {
		return new ByteArrayInputStream(buf);
	}
}
