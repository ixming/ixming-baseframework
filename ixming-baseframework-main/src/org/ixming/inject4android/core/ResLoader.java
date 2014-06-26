package org.ixming.inject4android.core;


import org.ixming.inject4android.ResTargetType;

import android.content.Context;

/**
 * 工具中加载Res的操作类。
 * 
 * @author Yin Yong
 */
class ResLoader {

	public ResLoader() {
	}
	
	private void checkContext(Context context, String which) {
		if (null == context) {
			throw new NullPointerException("context is null, detail = " + which);
		}
	}
	
	private void checkId(int id, String which) {
		if (id <= 0) {
			throw new UnsupportedOperationException("invalid id = " + id + ", detail = " + which);
		}
	}
	
	public Object loadThemedRes(ResTargetType type, Context themedContext, String name) {
		checkContext(themedContext, "loadThemedRes<themedContext>");
		if (!type.isSupportTheme()) {
			throw new UnsupportedOperationException("target res type : " + type.getAsAndroidResType()
					+ " do not support theme operations!");
		}
		int id = themedContext.getResources().getIdentifier(name,
				type.getAsAndroidResType(), themedContext.getPackageName());
		checkId(id, "cannot find a valid id by name = " + name);
		return loadRes(type, themedContext, id);
	}
	
    public Object loadRes(ResTargetType type, Context context, int id) {
    	checkContext(context, "loadRes<localContext>");
    	checkId(id, "loadRes<localContext>");
    	try {
    		return type.loadRes(context, id);
		} catch (Exception e) {
			throw new RuntimeException("Is ResTargetType[" + type + "] what you need ? detail: "
					+ e.getMessage());
		}
    }

}