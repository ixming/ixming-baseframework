package org.ixming.inject4android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来设置Activity或者View中声明的View变量；
 * <br/>
 * 运行时注入，不必重复编写获取、赋值代码。
 * <br/>
 * 
 * @author Yin Yong
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {
	
	/**
	 * 要获取的相应View的ID值
	 */
    int id();

    
    /**
     * 此属性，主要是为了RootView结构层次下，存在多个ID一样的项，
     * 而其父控件有所不同，因此需要设置其父控件的ID用以互相区分。
     * <p>
     * 如果没有上述的情况可以不予设置。
     * </p>
     * <p>
     * <strong>设计时请注意：</strong>
     * View的结构层次是不可预知的，但是View的层数是应该严格限制的；<br/>
     * 如果一个界面中（除了ListView等特殊情况）存在多个ID相同，parentId又相同的项，
     * 我们认为是无法想象，也不予理解。
     * </p>
     */
    int parentId() default 0;
    
}
