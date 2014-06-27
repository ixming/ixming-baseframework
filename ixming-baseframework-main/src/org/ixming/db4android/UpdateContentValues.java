package org.ixming.db4android;


import org.ixming.base.framework.annotation.TemporarilyDone;

import android.content.ContentValues;


/**
 * if you want to update some data, you'd determine the specific fields you'd modify,
 * use this class to put names and values of bean's fields to ContentValues
 * 
 * <p>
 * 如果想要更新（Update,delete）表中的信息——又不需要更新所有的字段，或者调用处知道Bean中包含的字段可能不全，
 * 调用处需要确定需要更新的“Model字段”和“表字段”，并将他们设置到方法setUpdateContentValues提供的ContentValues中。
 * </p>
 * @author Yin Yong
 * @version 1.0
 * @param <T> extends BaseSQLiteModel, 限制一下支持该接口的类型
 */
@TemporarilyDone
public interface UpdateContentValues<Bean extends BaseSQLiteModel> {
	
	/**
	 * determine which columns you want to update
	 * @param t get data fields from bean
	 * @param values ContentValues of (column-value) pair to put into
	 */
	abstract void setUpdateContentValues(Bean t, ContentValues values) ;
}
