package org.ixming.inject4android.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.ixming.inject4android.annotation.OnClickMethodInject;
import org.ixming.inject4android.annotation.ResInject;
import org.ixming.inject4android.annotation.ViewInject;

/**
 * 综合所有IOC接口
 * 
 * @author Yin Yong
 *
 */
interface IInterfaces extends IViewInjector, IResInjector, IOnClickListenerInjector { }


// >>>>>>>>>>>>>>>>>>>>>>>>>
// inner interfaces
/**
 * 此处提供View的运行时注入，运行时获取，给target中的成员变量赋值。
 * 
 * @author Yin Yong
 * @version 1.0
 */
interface IViewInjector {
	
	/**
	 * 实现动态获取并注入View。
	 * @return 如果field中存在{@link ViewInject}该标注，则返回TRUE。
	 */
	boolean injectView(Object target, Field field);
}

/**
 * 此处提供除了layout之外的资源文件的运行时注入，运行时获取资源，给target中的成员变量并赋值。
 * 
 * <p>
 * <strong>注意：</strong>
 * 该工具的确提供了图片的加载，但是获取到的图片没有规避OOM的操作；<br/>
 * 此处可以用于加载单个的，相对小的图片；<br/>
 * 如果需要加载大型图片，或者需要自定义/获取图片局部，推荐使用框架中的“图片加载工具”。
 * </p>
 * 
 * @author Yin Yong
 * @version 1.0
 */
interface IResInjector {

	/**
	 * 实现动态获取并注入资源。
	 * @return 如果field中存在{@link ResInject}该标注，则返回TRUE。
	 */
	boolean injectRes(Object target, Field field);
	
	/**
	 * 实现动态获取并注入资源。与{@link #injectRes(Object, Field)}不同的是，
	 * 该方法实现获取具有Themed风格的资源。
	 * 
	 * @return 如果field中存在{@link ResInject}该标注，则返回TRUE。
	 */
	boolean injectThemedRes(Object target, Field field);
	
}

/**
 * 
 * @author Yin Yong
 * @version 1.0
 */
interface IOnClickListenerInjector {

//	/**
//	 * 实现动态获取并注入单击事件监听器。
//	 * @return 如果method中存在{@link ViewOnClickInject}该标注，则返回TRUE。
//	 */
//	boolean injectViewOnClickListener(Object target, Method method);
	
	/**
	 * 实现动态获取并注入单击事件监听器。
	 * @return 如果method中存在 {@link OnClickMethodInject} 标注，则返回TRUE。
	 */
	boolean injectOnClickMethodListener(Object target, Method method);
	
}