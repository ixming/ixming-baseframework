package org.ixming.db4android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标示一个类为相应的数据库表
 * @author Yin Yong
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE })
public @interface Table {

	/**
	 * android中的authority
	 */
	String authority();
	
	/**
	 * 定义该表的表明
	 */
	String name();
	
}
