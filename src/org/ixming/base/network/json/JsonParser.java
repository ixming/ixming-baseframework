package org.ixming.base.network.json;

import org.ixming.base.network.HttpRes;

/**
 * 工具类--解析JSON，并将之转为目标类
 * 
 * @version 1.0
 */
public class JsonParser {
	private static final String TAG = JsonParser.class.getSimpleName();

	private JsonParser() {
	}

	/**
	 * 解析网络请求，并将返回的内容转化为与json互转的类对象
	 * 
	 * @added 1.0
	 */
	public static <T> T parseJson(HttpRes entity, Class<? extends T> clz) {
		T temp = null;
		try {
			if (null == entity) {
				return null;
			}
			// String json = GsonHelper.getJson(entity);
			String json = GsonHelper.getJsonGZIP(entity);
			temp = JsonService.outObjFromJson(json, clz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	public static <T> T parseJson(String json, Class<? extends T> clz) {
		T temp = null;
		try {
			temp = JsonService.outObjFromJson(json, clz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}
}
