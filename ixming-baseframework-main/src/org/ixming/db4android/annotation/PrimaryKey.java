package org.ixming.db4android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识表中主键字段。
 * <p>
 * 如果有继承关系的多个Model中定义了多个主键列（不同Model不同的定义），使用就近原则
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface PrimaryKey {

	/**
	 * 主键是否是自增长。默认为true。
	 */
	boolean autoIncrement() default true;
	
}
