package org.ixming.base.network.utils;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * 请求追加拼装信息
 * 
 * @author sunke
 * 
 */
public class NormalRequestPiecer {
	final static String API_KEY = "api_key";
	final static String API_SECRET = "api_secret";
	final static String APPV = "appv";
	final static String PROV = "prov";
	final static String DEVICE_TYPE = "device_type";
	final static String UA_LABEL = "os";

	public static final String TAG = NormalRequestPiecer.class.getSimpleName();

	/* 将最新的公共信息植入进已有的json中 */
	public static String repieceCommonInfo(Context context, String json) {
		try {
			JSONObject jo = null;
			if (NetWorkUtils.isNotNull(json)) {
				jo = new JSONObject(json);
			} else {
				jo = new JSONObject();
			}
			pieceCommonInfo(context, jo);
			return jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, " repieceToken Exception " + e.getMessage());
		}
		return json;
	}

	/* 拼装公共信息 */
	private static void pieceCommonInfo(Context context, JSONObject jo)
			throws Exception {
//		PackageInfo info = context.getPackageManager().getPackageInfo(
//				context.getPackageName(), 0);
		// jo.put(API_KEY,"69862516");
		jo.put(DEVICE_TYPE, "android");
		// jo.put(API_SECRET, "89w32z2j5jkds02ksdfx0we");
		// appv app的版本号
		// jo.put(APPV, SmartRebateApp.CURRENT_VERSION);
		jo.put(PROV, "1.5");
		// /* 配置平台类型 */
		// jo.put(UA_LABEL, "android");
		// /* 配置手机型号 */
		// jo.put(PM_LABEL, Build.MODEL);
		// /* 配置渠道号 */
		// // jo.put(CID_LABEL,
		// context.getResources().getString(R.string.cid_num));
		// /* 配置SDK系统版本 */
		// jo.put(SDKV_LABEL, Build.VERSION.SDK);
		// /* 配置软件当前版本号 */
		// jo.put(VERCODE_LABEL, info.versionCode);
		// /* 配置软件当前版本名称 */
		// jo.put(VERNAME_LABEL, info.versionName);
	}
}