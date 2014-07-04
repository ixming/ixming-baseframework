package org.ixming.task4android;

import android.util.Log;

class LogUtils {

	static boolean out_print_info = true;
	static boolean out_print_warn = true;
	static boolean out_print_debug = true;
	static boolean out_print_error = true;

	public static void i(String tag, String msg) {
		if (out_print_info) {
			Log.i(tag, msg);
		}
	}

	public static void i(Class<?> c, String msg) {
		i(c.getSimpleName(), msg);
	}

	public static void d(String tag, String msg) {
		if (out_print_debug) {
			Log.d(tag, msg);
		}
	}

	public static void d(Class<?> c, String msg) {
		d(c.getSimpleName(), msg);
	}

	public static void w(String tag, String msg) {
		if (out_print_warn) {
			Log.w(tag, msg);
		}
	}

	public static void w(Class<?> c, String msg) {
		w(c.getSimpleName(), msg);
	}

	public static void e(String tag, String msg) {
		if (out_print_info) {
			Log.e(tag, msg);
		}
	}

	public static void e(Class<?> c, String msg) {
		e(c.getSimpleName(), msg);
	}
}
