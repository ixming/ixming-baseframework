package org.ixming.base.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 指示该注解处有疑问，需要进一步研究
 * @author Yin Yong
 * @version 1.0
 */
@Retention(java.lang.annotation.RetentionPolicy.SOURCE)
@Target({java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.ANNOTATION_TYPE, 
	java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.CONSTRUCTOR,
	java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD,
	java.lang.annotation.ElementType.LOCAL_VARIABLE, java.lang.annotation.ElementType.PARAMETER})
public @interface UncertainState {

	/**
	 * 说明问题
	 */
	String value() default "";
	
}
