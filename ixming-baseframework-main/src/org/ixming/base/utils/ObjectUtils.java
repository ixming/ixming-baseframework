package org.ixming.base.utils;

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
	
}
