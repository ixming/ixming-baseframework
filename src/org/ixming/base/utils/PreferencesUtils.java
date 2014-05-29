package org.ixming.base.utils;

import org.ixming.base.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PreferencesUtils {

	private static SharedPreferences mShareConfig;

	/**
	 * 添加公共信息
	 * @param key
	 * @param value
	 */
	public static <T> void addConfigInfo(Context context, String key, T value) {
		if (Utils.notNull(value)) {
			mShareConfig = context.getSharedPreferences(
					context.getPackageName(), Context.MODE_PRIVATE);
			Editor conEdit = mShareConfig.edit();
			if (value instanceof String) {
				conEdit.putString(key.trim(), ((String) value).trim());
			} else if (value instanceof Long) {
				conEdit.putLong(key, (Long) value);
			} else if (value instanceof Boolean) {
				conEdit.putBoolean(key, (Boolean) value);
			}
			conEdit.commit();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValueByKey(Context context, String key,
			Class<T> typeClass) {
		T value = null;
		if (Utils.notNull(key)) {
			mShareConfig = context.getSharedPreferences(
					context.getPackageName(), Context.MODE_PRIVATE);
			if (null != mShareConfig) {
				if (typeClass.equals(String.class)) {
					value = (T) mShareConfig.getString(key, "");
				} else if (typeClass.equals(Long.class)) {
					value = (T) Long.valueOf(mShareConfig.getLong(key, 0));
				} else if (typeClass.equals(Boolean.class)) {
					value = (T) Boolean.valueOf(mShareConfig.getBoolean(key,
							true));
				} else if (typeClass.equals(Integer.class)) {
					value = (T) Integer.valueOf(mShareConfig.getInt(key, 0));
				}
			}
		}
		return value;
	}

}
