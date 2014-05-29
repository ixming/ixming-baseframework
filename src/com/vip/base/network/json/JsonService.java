package com.vip.base.network.json;

import com.google.gson.Gson;

class JsonService {
	private static Gson gson=new Gson();
	
//	public static String inObjToJson(Object o, String t) throws ClassNotFoundException {
//		Class c = Class.forName(t);
//		return gson.toJson(o, c);
//	}
//	public static Object outObjFromJson(String s, String t) throws ClassNotFoundException {
//		Class c = Class.forName(t);
//		return gson.fromJson(s, c);
//	}
	
	/**
	 * 改造后的inObjToJson
	 * @param o clz类的一个具体对象
	 * @param clz 一个能转为json的类
	 * @return 将clz的一个对象转为一个json字符串
	 * @throws ClassNotFoundException
	 * @added 1.0
	 */
	public static <T>String inObjToJson(T o, Class<? extends T> clz) throws ClassNotFoundException {
		return gson.toJson(o, clz);
	}
	
	/**
	 * 改造后的outObjFromJson
	 * @param json 一个json字符串
	 * @param clz 一个能yu与json互转的类
	 * @return 将json转为clz的一个对象，或者如果json错误，clz不能转等因素返回null
	 * @throws ClassNotFoundException
	 * @added 1.0
	 */
	public static <T>T outObjFromJson(String json, Class<? extends T> clz) throws ClassNotFoundException {
		return gson.fromJson(json, clz);
	}
}
