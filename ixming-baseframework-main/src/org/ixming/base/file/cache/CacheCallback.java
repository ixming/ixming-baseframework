package org.ixming.base.file.cache;

import java.io.InputStream;

/**
 * 
 * URLCache组件提供的回调
 * 
 * @author Yin Yong
 */
public interface CacheCallback {

	/**
	 * 
	 * 给外部一个机会，如果需要转换WebView的Response，返回一个新的InputStream
	 * 
	 * @param ins URLCache缓存组件取得的初始InputStream
	 * @param mimeType 暂时无效
	 * @param encoding 暂时无效
	 * @return 如果返回的InputStream不为null，将以返回的InputStream代替初始值
	 * <code> ins </code>
	 */
	InputStream shouldTransformResponse(InputStream ins, 
			String mimeType, String encoding);
	
}
