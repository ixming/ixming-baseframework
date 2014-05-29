package com.vip.base.utils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * a reusable toast utility class
 * @author YinYong
 * @version 1.0
 */
public class ToastUtils {
	private ToastUtils() { }

	private static WeakReference<Toast> mToastRef;
	
	private static Toast ensureToastInstance(Context context){
		Toast temp;
		if (null == mToastRef || null == (temp = mToastRef.get())) {
			mToastRef = new WeakReference<Toast>(Toast.makeText(context, "", Toast.LENGTH_SHORT));
			temp = mToastRef.get();
		} 
		return temp;
	}
	
	public static void showToast(Context context, CharSequence message){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_SHORT);
			temp.setText(message);
			temp.show();
		} catch (Exception e) { }
	}
	
	public static void showToast(Context context, int resId){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_SHORT);
			temp.setText(resId);
			temp.show();
		} catch (Exception e) { }
	}
	
	public static void showToast(final Context context, Handler handler, final int resId){
		if (AndroidUtils.isMainThread()) {
			showToast(context, resId);
		} else {
			try {
				handler.post(new Runnable() {
					@Override
					public void run() {
						showToast(context, resId);
					}
				});
			} catch (Exception e) { }
		}
	}

	public static void showToast(final Context context, Handler handler, final CharSequence message){
		if (AndroidUtils.isMainThread()) {
			showToast(context, message);
		} else {
			try {
				handler.post(new Runnable() {
					@Override
					public void run() {
						showToast(context, message);
					}
				});
			} catch (Exception e) { }
		}
	}
	
	public static void showLongToast(Context context, int resId){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_LONG);
			temp.setText(resId);
			temp.show();
		} catch (Exception e) { }
	}
	
	public static void showLongToast(Context context, CharSequence message){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_LONG);
			temp.setText(message);
			temp.show();
		} catch (Exception e) { }
	}

	public static void showLongToast(final Context context, Handler handler, final int resId){
		if (AndroidUtils.isMainThread()) {
			showLongToast(context, resId);
		} else {
			try {
				handler.post(new Runnable() {
					@Override
					public void run() {
						showLongToast(context, resId);
					}
				});
			} catch (Exception e) { }
		}
	}

	public static void showLongToast(final Context context, Handler handler, final CharSequence message){
		if (AndroidUtils.isMainThread()) {
			showLongToast(context, message);
		} else {
			try {
				handler.post(new Runnable() {
					@Override
					public void run() {
						showLongToast(context, message);
					}
				});
			} catch (Exception e) { }
		}
	}
}
