package org.ixming.inject4android.themed;

import org.ixming.inject4android.InjectConfigure;

/**
 * 配置那些需要使用动态注入。
 * <p>
 * 默认情况下，全部支持。
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
public class ThemedInjectConfigure extends InjectConfigure {

	@Override
	public String toString() {
		return "ThemedInjectConfigure [isInjectReses()=" + isInjectReses()
				+ ", isInjectViews()=" + isInjectViews()
				+ ", isInjectOnClickMethods()=" + isInjectOnClickMethods()
				+ "]";
	}

}
