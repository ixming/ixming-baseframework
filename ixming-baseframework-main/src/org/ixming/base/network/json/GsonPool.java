package org.ixming.base.network.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * get different Gson instance
 * 
 * @author Yin Yong
 *
 */
public class GsonPool {

	private GsonPool() { }
	
	private static Gson mDefaultGson;
	
	public synchronized static Gson getDefault() {
		if (null == mDefaultGson) {
			mDefaultGson = new Gson();
		}
		return mDefaultGson;
	}
	
	private static Gson mExposeRestrictGson;
	public synchronized static Gson getExposeRestrict() {
		if (null == mExposeRestrictGson) {
			mExposeRestrictGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		}
		return mExposeRestrictGson;
	}
}
