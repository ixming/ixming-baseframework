package org.ixming.base.utils.android;

import java.io.ByteArrayOutputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();
	private static String DEVICE_ID = null;
	static InputMethodManager inputMethodManager;

	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;

	/**
	 * 获取deviceId
	 */
	public static String getDeviceId(Context context) {
		if (DEVICE_ID == null || DEVICE_ID.length() > 0) {
			DeviceUuidFactory factory = new DeviceUuidFactory(context);
			DEVICE_ID = factory.getDeviceUuid();
		}
		return DEVICE_ID;
	}

	public static boolean isNotNull(String str) {
		if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim())
				&& !"null".equals(str)) {
			return true;
		}
		return false;
	}

	// 键盘开启
	public static void keyboardOn(Context context) {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// 键盘关闭
	public static void keyboardOff(Context context, EditText et) {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}

		inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

	/**
	 * 图片等比缩放
	 * 
	 * @return
	 * 
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap) {
		// 图片缩放
		Log.i(TAG, "execute scaleBitmap!!!");
		int newWidth = 720;
		float scale = (float) newWidth / bitmap.getWidth();
		int newHeight = (int) (bitmap.getHeight() * scale);
		return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
	}

	/**
	 * 11 校验银行卡卡号 12
	 * 
	 * @param cardId
	 *            13
	 * @return 14
	 */
	public static boolean checkBankCard(String cardId) {
		if (!isNotNull(cardId)) {
			return false;
		}
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 24 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 25
	 * 
	 * @param nonCheckCodeCardId
	 *            26
	 * @return 27
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * @param context
	 */
	public static void deviceScreen(Context context) {
		WindowManager wm = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels; // 屏幕宽度（像素）
		int height = dm.heightPixels; // 屏幕高度（像素）
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		System.out.println("----------------------------------");
		System.out.println("width:" + width);
		System.out.println("height:" + height);
		System.out.println("density:" + density);
		System.out.println("densityDpi:" + densityDpi);
		System.out.println("----------------------------------");
	}

	/**
	 * 将Bitmap转化为字节数组
	 * 
	 * @param bmp
	 *            target bitmap to read
	 * @param needRecycle
	 *            是否需要回收bitmap
	 * @added 1.0
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		if (null == bmp) {
			return null;
		}
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, output);
			if (needRecycle) {
				bmp.recycle();
			}
			byte[] result = output.toByteArray();
			return result;
		} catch (Exception e) {
			return null;
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 对象不为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean notNull(Object obj) {
		if (null != obj && obj != "") {
			return true;
		}
		return false;
	}

	public static boolean isNull(Object obj) {
		if (null == obj || obj == "" || obj.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			// 当前应用的版本名称
			String versionName = info.versionName;
			// 当前版本的版本号
			versionCode = info.versionCode;
			// 当前版本的包名
			String packageNames = info.packageName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}

	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			// 当前应用的版本名称
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionName;
	}

	public static int getNetworkState(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info == null) {
			// 没有网络
			return NETWORKTYPE_INVALID;
		}
		if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
				// 2G
				return NETWORKTYPE_2G;
			} else {
				// 其他 默认为3G
				return NETWORKTYPE_3G;
			}
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			// WIFI
			return NETWORKTYPE_WIFI;
		}
		return NETWORKTYPE_WAP;
	}

	// public static Bitmap getTimeForBitmap(Context context, long time) {
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	// String dateStr = dateFormat.format(new Date(time));
	// int len = dateStr.length();
	// float density = context.getResources().getDisplayMetrics().density;
	// Bitmap timeBitmap =
	// Bitmap.createBitmap((int) (120 * (density / 1.5)), (int) (28 * (density /
	// 1.5)),
	// Config.ARGB_8888);
	// Canvas cv = new Canvas(timeBitmap);
	//
	// int spacing = (int) (12 * (density / 1.5));
	//
	// for (int i = 0; i < len; i++) {
	// switch (dateStr.charAt(i)) {
	// case '0':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number0), i * spacing, 0, null);
	// break;
	// case '1':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number1), i * spacing, 0, null);
	// break;
	// case '2':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number2), i * spacing, 0, null);
	// break;
	// case '3':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number3), i * spacing, 0, null);
	// break;
	// case '4':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number4), i * spacing, 0, null);
	// break;
	// case '5':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number5), i * spacing, 0, null);
	// break;
	// case '6':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number6), i * spacing, 0, null);
	// break;
	// case '7':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number7), i * spacing, 0, null);
	// break;
	// case '8':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number8), i * spacing, 0, null);
	// break;
	// case '9':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number9), i * spacing, 0, null);
	// break;
	// case '.':
	// cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.number_dot), i * spacing, 0, null);
	// break;
	// default:
	// break;
	// }
	// }
	// cv.save(Canvas.ALL_SAVE_FLAG);
	// cv.restore();
	// return timeBitmap;
	// }

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return "";
	}
}
