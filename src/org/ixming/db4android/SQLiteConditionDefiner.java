package org.ixming.db4android;

import org.ixming.base.framework.annotation.TemporarilyDone;

/**
 * 这是一个辅助接口，用来动态定义搜索条件；<br/>
 * 因为在更新的数据超过一个时，如果不是按照统一的条件语句，则需要通过本接口定义修改单条记录的条件。
 * 
 * <p>
 * 具体做法是，根据传入的T对象，在调用处动态将条件传入SQLiteCondition中。DBManager会自行应用该方式进行更新。
 * </p>
 * @author Yin Yong
 * @version 1.0
 * @param <T> extends BaseSQLiteModel, 限制一下支持该接口的类型
 */
@TemporarilyDone
public interface SQLiteConditionDefiner<T extends BaseSQLiteModel> {

	/**
	 * @param condition never null，调用者自行设置SQL语句的条件
	 * @param t never null，某一个具体对象
	 */
	void config(SQLiteCondition condition, T t);
	
}
