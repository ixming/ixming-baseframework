package org.ixming.db4android;


/**
 * 遇到某种约束时，相应的处理行为：
 * 如唯一性约束时，使用{@link #REPLACE}。
 * @author Yin Yong
 * @version 1.0
 */
public enum SQLiteConflictAction implements Sqlable{

	/**
	 * we do not care about his
	 */
	NONE{
		@Override
		public String toSql() {
			return " ";
		}
	},
	
	REPLACE {
		@Override
		public String toSql() {
			return "REPLACE";
		}
	};

	public abstract String toSql();
	
}
