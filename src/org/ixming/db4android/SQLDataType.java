package org.ixming.db4android;

/**
 * SQL数据类型，定义该数据类型在SQL中的术语，并提供在SDK中的对应的类的数组。
 * @author Yin Yong
 * @version 1.0
 */
interface SQLDataType {

	/**
	 * 该数据类型，在SQL中的术语
	 */
	String getSQLTypeName();
	
	/**
	 * 该数据类型，在编程语言中相应的类（可能对应多个）
	 */
	Class<?>[] getSQLRelatedClasses();
	
}
