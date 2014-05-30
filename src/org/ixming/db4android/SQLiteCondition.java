package org.ixming.db4android;

import org.ixming.base.framework.annotation.UncertainState;

/**
 * 为了更好、更方便地使用Android API 提供的数据库操作（查询，更新等），以及方便本框架内部的
 * 处理，就封装了该类——SQLiteCondition。
 * 
 * <p>
 * 请使用静态方法{@link #create()}获得一个新的实例。
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
public class SQLiteCondition {

	public static final String SORT_ASC = "ASC";
	public static final String SORT_DESC = "DESC";
	
	/**
	 * 封装内部处理（虽然现在还是最简单的处理），得到一个SQLiteCondition对象。
	 * @return 新的SQLiteCondition对象
	 */
	@UncertainState
	public static SQLiteCondition create() {
		return new SQLiteCondition();
	}
	
	private String where; 
	private String[] whereArgs;
	private String sortClause;
	
	private SQLiteCondition() { }
	
	/**
	 * 调用处定义where并将之传入
	 */
	public SQLiteCondition setWhereClause(String where) {
		this.where = where;
		return this;
	}
	
	/**
	 * 使用者提供字段，内部将拼凑成“XX=? AND YY=?”的形式
	 * @param cols 所有字段
	 */
	public SQLiteCondition setWhereColumnArray(String...cols) {
		if (null == cols || cols.length == 0) {
			this.where = null;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			for (int i = 0; i < cols.length; i++) {
				if (i > 0) {
					sb.append(" AND ");
				}
				sb.append(cols[i]).append(" = ? ");
			}
			this.where = sb.toString();
		}
		return this;
	}
	
	/**
	 * 获得selection语句
	 */
	public String getWhereClause() {
		return this.where;
	}
	
	/**
	 * 设置selection语句中（“XX=? AND YY=?”）与“?”相对应的参数。
	 */
	public SQLiteCondition setWhereArgs(String...whereArgs) {
		this.whereArgs = whereArgs;
		return this;
	}

	/**
	 * 获得selection语句相应的参数
	 */
	public String[] getWhereArgs() {
		return this.whereArgs;
	}
	
	/**
	 * 不需要“SORTBY”，只需要类似“column1, column2 ASC/DESC”的语句。
	 */
	public SQLiteCondition setSortOrder(String sortClause) {
		this.sortClause = sortClause;
		return this;
	}
	
	/**
	 * @param sortType 你可以使用{@link #SORT_ASC}和{@link #SORT_DESC}
	 * @param cols 所有字段
	 */
	public SQLiteCondition setSortByColumn(String sortType, String...cols) {
		if (null == cols || cols.length == 0) {
			this.sortClause = null;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			for (int i = 0; i < cols.length; i++) {
				if (i > 0) {
					sb.append(" , ");
				}
				sb.append(cols[i]);
			}
			sb.append(" ").append(sortType).append(" ");
			this.sortClause = sb.toString();
		}
		return this;
	}

	/**
	 * 获得排序语句
	 */
	public String getSortOrder() {
		return this.sortClause;
	}
	
	/**
	 * 重置内部数据
	 */
	public SQLiteCondition reset() {
		this.where = null;
		this.whereArgs = null;
		this.sortClause = null;
		return this;
	}
}
