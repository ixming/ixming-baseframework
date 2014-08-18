package org.ixming.base.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 
 * utils for objects
 * 
 * @author Yin Yong
 *
 */
public class ObjectUtils {

	private ObjectUtils() { }
	
	public static boolean checkNull(Object obj) {
		return null == obj;
	}
	
	public static void checkNullAndThrow(Object obj, String tag) {
		if (checkNull(obj)) {
			throw new NullPointerException(tag + " is null");
		}
	}
	
	/**
	 * 判断一个对象是否为null或者内容为空。
	 * 
	 * <p>
	 * 包括CharSequence，Collection，Map，Array。
	 * </p>
	 */
	public static boolean isNullOrEmpty(Object obj) {
		if (checkNull(obj)) {
			return true;
		}
		if (obj instanceof CharSequence) {
			CharSequence c = (CharSequence) obj;
			return c.length() == 0;
		} else if (obj instanceof Collection) {
			Collection<?> c = (Collection<?>) obj;
			return c.isEmpty();
		} else if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			return map.isEmpty();
		} else if (obj.getClass().isArray()) {
			int len = Array.getLength(obj);
			return len == 0;
		}
		return false;
	}
	
	/**
	 * 判断两个对象是否相等（包括null）
	 */
	public static boolean equals(Object obj1, Object obj2) {
		if (null == obj1) {
			return null == obj2;
		} else {
			return obj1.equals(obj2);
		}
	}
}
