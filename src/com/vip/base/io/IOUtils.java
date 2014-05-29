package com.vip.base.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.vip.base.utils.FrameworkLog;

public class IOUtils {

	private final static String TAG = IOUtils.class.getSimpleName();
	
	private IOUtils() { }
	
	public static boolean writeTo(InputStream in, OutputStream out,
			boolean close) throws IOException {
		if (null == in) {
			throw new NullPointerException("in is null!");
		}
		if (null == out) {
			throw new NullPointerException("out is null!");
		}
		try {
			byte[] buf = new byte[IOConstants.FILE_OUTPUT_BUFFER_SIZE];
			int len = -1;
			while (-1 != (len = in.read(buf))) {
				out.write(buf, 0, len);
			}
			return true;
		} catch (IOException e) {
			FrameworkLog.e(TAG, "writeTo Exception : " + e.getMessage());
			throw e;
		} finally {
			if (close) {
				close(out);
				close(in);
			}
		}
	}
	
	public static String inputStreamToString(InputStream in) throws IOException {
		return inputStreamToString(in, false);
	}
	
	public static String inputStreamToString(InputStream in, boolean remainLine)
			throws IOException {
		if (null == in) {
			throw new NullPointerException("in is null!");
		}
		BufferedReader reader = null;
		try {
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(in));
			String buf = null;
			while (null != (buf = reader.readLine())) {
				sb.append(buf);
				if (remainLine) {
					sb.append("\n");
				}
			}
			return sb.toString();
		} catch (IOException e) {
			FrameworkLog.e(TAG, "inputStreamToString Exception : " + e.getMessage());
			throw e;
		} finally {
			close(reader);
			close(in);
		}
	}
	
	public static void close(Closeable close) {
    	if (null == close) {
    		return ;
    	}
    	try {
    		close.close();
		} catch (Exception ignore) { }
    }
}
