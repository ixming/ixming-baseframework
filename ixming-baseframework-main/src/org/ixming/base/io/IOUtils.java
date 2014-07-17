package org.ixming.base.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.ixming.base.utils.android.FrameworkLog;

/**
 * 工具类，封装简单的I/O转换操作
 * 
 * @author Yin Yong
 *
 */
public class IOUtils {

	private final static String TAG = IOUtils.class.getSimpleName();
	
	private IOUtils() { }
	
	
	// >>>>>>>>>>>>>>>>>>>>>>
	// writeTo 系列
	public static boolean writeTo(InputStream in, OutputStream out,
			boolean close) throws IOException {
		if (null == in) {
			throw new NullPointerException("in is null!");
		}
		if (null == out) {
			throw new NullPointerException("out is null!");
		}
		try {
			byte[] buf = new byte[IOConstants.DEF_BUFFER_SIZE];
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
	
	public static boolean writeTo(Reader in, Writer out,
			boolean close) throws IOException {
		if (null == in) {
			throw new NullPointerException("in is null!");
		}
		if (null == out) {
			throw new NullPointerException("out is null!");
		}
		try {
			char[] buffer = new char[IOConstants.DEF_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = in.read(buffer))) {
				out.write(buffer, 0, n);
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
	
	public static boolean writeTo(InputStream in, Writer out,
			String encoding, boolean close) throws IOException {
		InputStreamReader reader;
		if (encoding == null) {
			encoding = IOConstants.DEF_CHARSET;
		}
		reader = new InputStreamReader(in, encoding);
		return writeTo(reader, out, close);
	}
	
	
	// >>>>>>>>>>>>>>>>>>>>>>
	// toString 系列
	public static String inputStreamToString(InputStream in)
			throws IOException {
		return inputStreamToString(in, IOConstants.DEF_CHARSET);
	}

	public static String inputStreamToString(InputStream in, String encoding)
			throws IOException {
		if (null == in) {
			throw new NullPointerException("in is null!");
		}
		if (null == encoding) {
			encoding = IOConstants.DEF_CHARSET;
		}
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			writeTo(in, writer, encoding, true);
			return writer.toString();
		} catch (IOException e) {
			FrameworkLog.e(TAG, "inputStreamToString Exception : " + e.getMessage());
			throw e;
		}
	}
	
	
	// >>>>>>>>>>>>>>>>>>>>>>
	// close
	public static void close(Closeable close) {
    	if (null == close) {
    		return ;
    	}
    	try {
    		close.close();
		} catch (IOException ignore) { }
    }
}
