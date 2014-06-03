package org.ixming.db4android;

/**
 * 对象可以转为SQL语句
 * @author Yin Yong
 * @version 1.0
 */
public interface Sqlable {

	String SEPERATOR = " ";
	
	String COMMA = ",";
	
	String LEFT_BRACKET = "(";
	
	String RIGHT_BRACKET = ")";
	
	String UNDERSCORE = "_";
	
	String END = ";";
	
	/**
	 * 转化为SQL语句
	 */
	String toSql();
	
}
