package com.vip.base.network.json;

/**
 * 工具类--将目标类转为JSON字符串
 * @version 1.0
 */
public class JsonGenerator {
	private static final String TAG = JsonGenerator.class.getSimpleName();

	private JsonGenerator() {
	}

	public static <T> String toJsonString(T o, Class<? extends T> clz) {
		try {
			return JsonService.inObjToJson(o, clz);
		} catch (Exception e) {
			return "";
		}
	}
}
