package org.ixming.db4android.dbmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ixming.base.framework.annotation.TemporarilyDone;
import org.ixming.base.utils.NumberUtils;
import org.ixming.base.utils.android.FrameworkLog;
import org.ixming.db4android.BaseSQLiteModel;
import org.ixming.db4android.SQLiteCondition;
import org.ixming.db4android.SQLiteConditionDefiner;
import org.ixming.db4android.UpdateContentValues;
import org.ixming.db4android.annotation.Column;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


/**
 * 这是一个基于ContentProvider的数据库管理类。
 * @author Yin Yong
 * @version 1.0
 */
public class DBManager<T extends BaseSQLiteModel> {
	final String TAG = DBManager.class.getSimpleName();
	
	private static final SQLiteCondition SQLCONDICION_NONE = SQLiteCondition.create();
	private static SQLiteCondition checkSQLiteCondition(SQLiteCondition instance) {
		return null == instance ? SQLCONDICION_NONE : instance;
	}
	
	/**
	 * 根据提供的clz对象，使用反射查找Table和Column信息，然后运行时自动组合成Sql语句。
	 */
	public static <T extends BaseSQLiteModel>String getTableCreation(Class<T> clz) {
		return SQLiteModelInfo.parseModel(clz).toSql();
	}
	
	/**
	 * 根据提供的clz对象，使用反射查找Table和Column信息，然后运行时自动组合成Index创建语句。
	 * @return null, or 长度为 {@link Column#asIndex()} == true的列个数的String数组。
	 */
	public static <T extends BaseSQLiteModel>String[] getTableIndexCreation(Class<T> clz) {
		return SQLiteModelInfo.parseModel(clz).getIndexCreations();
	}
	
	private ContentResolver mContentResolver;
	private Class<T> mClass;
	private Uri mTableBaseUri;
	private String mAuthority;
	private String mTableName;
	private String[] mColumns;
	private SQLiteModelInfo mSQLiteModelInfo;
	DBManager(Context context, Class<T> clz) {
		mContentResolver = context.getContentResolver();
		mClass = clz;
		// get and remove from SQLiteModelInfo self cache
		mSQLiteModelInfo = SQLiteModelInfo.parseOfPullFromCache(mClass, true);
		mAuthority = mSQLiteModelInfo.getAuthority();
		mTableName = mSQLiteModelInfo.getTableName();
		mColumns = mSQLiteModelInfo.getColumns();
		
		mTableBaseUri = Uri.parse("content://" + mAuthority).buildUpon().appendPath(mTableName).build();
	}
	
	@Override
	protected void finalize() throws Throwable {
		mContentResolver = null;
		mTableBaseUri = null;
		mAuthority = null;
		mTableName = null;
		mColumns = null;
		mSQLiteModelInfo = null;
		super.finalize();
	}
	
	private ContentValues createFromBean(T bean) {
		ContentValues values = new ContentValues();
		createFromBean(bean, values);
		return values;
	}
	
	private void createFromBean(T bean, ContentValues values) {
		if (null == bean) {
			return ;
		}
		// bean 对象的Class是否是mClass或者mClass的子类
		if (!mClass.isAssignableFrom(bean.getClass())) {
			throw new IllegalArgumentException("createFromBean:: bean's class is not " + mClass.getClass());
		}
		int colIndex = 0;
		if (null != mSQLiteModelInfo.getPKInfo()) {
			colIndex ++;
		}
		for (int i = colIndex; i < mColumns.length; i++) {
			SQLiteColumnInfo colInfo = mSQLiteModelInfo.getColumnInfo(mColumns[i]);
			colInfo.putValueToContentValues(mColumns[i], bean, values);
		}
	}
	
	private T createFromCursor(Cursor cursor) {
		try {
			T t = (T) mClass.newInstance();
			int colIndex = 0;
			SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
			if (null != pkInfo) {
				colIndex ++;
				pkInfo.setValueToField(pkInfo.getColumnName(), t, cursor);
			}
			pkInfo = null;
			for (int i = colIndex; i < mColumns.length; i++) {
				SQLiteColumnInfo colInfo = mSQLiteModelInfo.getColumnInfo(mColumns[i]);
				colInfo.setValueToField(mColumns[i], t, cursor);
			}
			return t;
		} catch (Exception e) {
			throw new RuntimeException("createFromCursor:: e " + e.getMessage());
		}
	}
	
	private boolean hasPrimaryKey() {
		return null == mSQLiteModelInfo.getPKInfo();
	}
	
	private void checkHasPK() {
		if (hasPrimaryKey()) {
			throw new UnsupportedOperationException("checkHasPK there's no PK of clz: " + mClass);
		}
	}
	
	SQLiteCondition createSQLiteConditionFromPKValue(Object value) {
		checkHasPK();
		SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
		return SQLiteCondition.create().setWhereColumnArray(pkInfo.getColumnName()).setWhereArgs(String.valueOf(value));
	}
	
	SQLiteCondition createSQLiteConditionFromPK(T target) {
		checkHasPK();
		SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
		return SQLiteCondition.create().setWhereColumnArray(pkInfo.getColumnName()).setWhereArgs(String.valueOf(pkInfo.getFieldValueFromTarget(target)));
	}
	
	void setSQLiteConditionFromPKValue(SQLiteCondition condition, Object value) {
		checkHasPK();
		SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
		condition.reset().setWhereColumnArray(pkInfo.getColumnName()).setWhereArgs(String.valueOf(value));
	}
	
	void setSQLiteConditionFromPK(SQLiteCondition condition, T target) {
		checkHasPK();
		SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
		condition.reset().setWhereColumnArray(pkInfo.getColumnName()).setWhereArgs(String.valueOf(pkInfo.getFieldValueFromTarget(target)));
	}
	
	Object getPrimaryKeyValue(T target) {
		checkHasPK();
		SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
		return pkInfo.getFieldValueFromTarget(target);
	}
	
	long getLongPK(T target) {
		try {
			return (Long) getPrimaryKeyValue(target);
		} catch (Exception e) {
			throw new UnsupportedOperationException("getLongPK e " + e.getMessage());
		}
	}
	
	
	// =============================================================================
	// ================================[insert]=====================================
	// =============================================================================
	@TemporarilyDone
	// insert
	private boolean setNewInsertId(Uri resultUri, T t) {
		if (null == resultUri || null == t) return false;
		
		List<String> pathSet = resultUri.getPathSegments();
		if (null == pathSet || pathSet.size() < 2) {
			FrameworkLog.w(TAG, "setNewInsertId uri path is null or incorrect");
			return false;
		}
		
		//if (null == mSQLiteModelInfo.getPKInfo()) return true;
		
		SQLiteColumnInfo pkInfo = mSQLiteModelInfo.getPKInfo();
		pkInfo.setValueToField(t, NumberUtils.getLong(pathSet.get(1)));
		return true;
	}
	
	/**
	 * @see {@link #insertData(T, boolean)}
	 */
	public final boolean insertData(T t) {
		return insertData(t, false);
	}
	
	/**
	 * @see {@link #insertData(Collection, boolean)}
	 */
	public final boolean insertData(Collection<T> list) {
		return insertData(list, false);
	}
	
	/**
	 * @see {@link #insertData(Map, boolean)}
	 */
	public final boolean insertData(Map<?, T> map) {
		return insertData(map, false);
	}
	
	/**
	 * 插入单条记录。
	 * @param t 记录
	 * @param deleteBeforeInsert 是否在插入之前清空表
	 * @return 返回TRUE表示插入成功，并赋予主键值（如果含有主键）——如果没有主键，则根据返回的Uri的特性是否属于插入成功。
	 */
	public final boolean insertData(T t, boolean deleteBeforeInsert) {
		if (deleteBeforeInsert) deleteAll(); // delete all 
		if (null == t) return false;
		
		return setNewInsertId(mContentResolver.insert(mTableBaseUri, createFromBean(t)), t);
	}
	
	/**
	 * 插入多条记录。
	 * @param list 记录集合
	 * @param deleteBeforeInsert 是否在插入之前清空表
	 * @return 返回TRUE表示插入成功，并赋予主键值（如果含有主键）——如果没有主键，则根据返回的Uri的特性是否属于插入成功。
	 */
	public final boolean insertData(Collection<T> list, boolean deleteBeforeInsert) {
		if (deleteBeforeInsert) deleteAll();// delete all
		if (null == list || list.isEmpty()) return false;
		try {
			ArrayList<ContentProviderOperation> operations = 
					new ArrayList<ContentProviderOperation>();
			Iterator<T> ite = list.iterator();
			while (ite.hasNext()) {
				T t = ite.next();
				if (null == t) {
					ite.remove();
					continue;
				}
				operations.add(ContentProviderOperation.newInsert(mTableBaseUri).withValues(createFromBean(t)).build());
			}
			ContentProviderResult[] results = mContentResolver.applyBatch(mTableBaseUri.getAuthority(), operations);
			if (hasPrimaryKey()) {
				ite = list.iterator();
				int index = 0;
				// 将id设置进Model对象中
				while (ite.hasNext()) {
					setNewInsertId(results[index++].uri, ite.next());
				}
			}
			FrameworkLog.d(TAG, "insertData<C, boolean> insert size: " + list.size());
			FrameworkLog.d(TAG, "insertData<C, boolean> results.length: " + results.length);
			return results.length == list.size();
		} catch (Exception e) {
			FrameworkLog.e(TAG, "insertData<C, boolean> Exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 插入多条记录。
	 * @param map 记录Map
	 * @param deleteBeforeInsert 是否在插入之前清空表
	 * @return 返回TRUE表示插入成功，并赋予主键值（如果含有主键）——如果没有主键，则根据返回的Uri的特性是否属于插入成功。
	 */
	public final boolean insertData(Map<?, T> map, boolean deleteBeforeInsert) {
		if (null == map) {
			return false;
		}
		return insertData(map.values(), deleteBeforeInsert);
	}
	
	
	
	// =============================================================================
	// ================================[query]======================================
	// =============================================================================
	// =============================================================================
	@TemporarilyDone
	// query
	/**
	 * @param condition null 相当于查询全部。
	 */
	public List<T> queryList(SQLiteCondition condition) {
		Cursor cursor = null;
		try {
			condition = checkSQLiteCondition(condition);
			cursor = mContentResolver.query(mTableBaseUri, mColumns, 
					condition.getWhereClause(), condition.getWhereArgs(), condition.getSortOrder());
			
			if (!cursor.moveToFirst()) return null;
			
			List<T> list = new ArrayList<T>();
			do {
				list.add(createFromCursor(cursor));
			} while (cursor.moveToNext());
			return list;
		} catch (Exception e) {
			FrameworkLog.e(TAG, "<List>query Exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if (null != cursor){
				cursor.close();
			}
		}
	}
	
	/**
	 * 根据条件查询单条记录。
	 * @return 查询到的单个对象，如果没有则返回null
	 */
	public T queryOne(SQLiteCondition condition) {
		Cursor cursor = null;
		try {
			condition = checkSQLiteCondition(condition);
			cursor = mContentResolver.query(mTableBaseUri, mColumns, 
					condition.getWhereClause(), condition.getWhereArgs(), condition.getSortOrder());
			if (!cursor.moveToFirst()) return null;
			return createFromCursor(cursor);
		} catch (Exception e) {
			FrameworkLog.e(TAG, "<T>queryOne Exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if (null != cursor){
				cursor.close();
			}
		}
	}
	
	/**
	 * 根据主键（该处特殊为数值型主键）查询单条记录。
	 * @return 查询到的单个对象，如果没有则返回null
	 */
	public T queryById(long id) {
		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(ContentUris.withAppendedId(mTableBaseUri, id),
					mColumns, null, null, null);
			if (!cursor.moveToFirst()) return null;
			FrameworkLog.d(TAG, "queryById get first record");
			return createFromCursor(cursor);
		} catch (Exception e) {
			FrameworkLog.e(TAG, "queryById Exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if (null != cursor){
				cursor.close();
			}
		}
	}
	
	/**
	 * 根据主键查询单条记录。
	 * @return 查询到的单个对象，如果没有则返回null
	 */
	public T queryByPrimaryKey(Object value) {
		return queryOne(createSQLiteConditionFromPKValue(value));
	}
	
	
	// =============================================================================
	// ================================[update]=====================================
	// =============================================================================
	// update
	protected final UpdateContentValues<T> mDefaultUpdateContentValues = new UpdateContentValues<T>() {
		@Override
		public void setUpdateContentValues(T t, ContentValues values) {
			createFromBean(t, values);
		}
	};
	
	protected final SQLiteConditionDefiner<T> mPKSQLiteConditionDefiner = new SQLiteConditionDefiner<T>() {
		@Override
		public void config(SQLiteCondition condition, T t) {
			setSQLiteConditionFromPK(condition, t);
		}
	};
	
	/**
	 * 这是最通用，最具灵活性的方法，外部设置更新哪些字段（UpdateContentValues）以及根据哪些条件更新（SQLiteConditionDefiner）
	 * @param list
	 * @param updateValues 如果为NULL，则使用默认的UpdateContentValues，即相当于
	 * {@link #createFromBean(BaseSQLiteModel, ContentValues)}
	 * @param definer 如果为NULL，则不予执行
	 */
	public <C extends Collection<T>>boolean updateList(C list, UpdateContentValues<T> updateValues,
			SQLiteConditionDefiner<T> definer) {
		try {
			if (null == list || list.isEmpty()) return false;
			if (null == definer) return false;
			if (null == updateValues) updateValues = mDefaultUpdateContentValues;
			
			ArrayList<ContentProviderOperation> operations = 
					new ArrayList<ContentProviderOperation>();
			Iterator<T> ite = list.iterator();
			SQLiteCondition condition = SQLiteCondition.create();
			while (ite.hasNext()) {
				T t = ite.next();
				if (null == t) {
					ite.remove();
					continue;
				}
				// 能使用此方式的原因是ContentProviderOperation的withValues的实现是new自己内部的ContentValues对象，
				// 而不是直接使用。
				ContentValues values = new ContentValues();
				updateValues.setUpdateContentValues(t, values);
				definer.config(condition.reset(), t);
				operations.add(ContentProviderOperation.newUpdate(mTableBaseUri).withValues(values)
						.withSelection(condition.getWhereClause(), condition.getWhereArgs()).build());
			}
			ContentProviderResult[] results = mContentResolver.applyBatch(mAuthority, operations);
			FrameworkLog.d(TAG, "update<List, values, definer> list.size(): " + list.size());
			FrameworkLog.d(TAG, "update<List, values, definer> results.length: " + results.length);
			return results.length == list.size();
		} catch (Exception e) {
			FrameworkLog.e(TAG, "update<List, values, definer> Exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * first you should ensure that all items in the collection must be loaded from database,
	 * in other words, it must contains a database _ID field
	 * @param list
	 * @param updateValues null to use default, just like invoking {@link #createFromBean}
	 */
	public final <C extends Collection<T>>boolean updateListByPrimaryKey(C list, UpdateContentValues<T> updateValues) {
		return updateList(list, updateValues, mPKSQLiteConditionDefiner);
	}
	
	/**
	 * 根据条件，批量更新
	 */
	public final int updateByClause(ContentValues values, SQLiteCondition condition) {
		try {
			condition = checkSQLiteCondition(condition);
			int updateRow = mContentResolver.update(mTableBaseUri, values, condition.getWhereClause(), condition.getWhereArgs());
			return updateRow;
		} catch (Exception e) {
			FrameworkLog.e(TAG, "updateByClause Exception: " + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * 根据“某一列=某个值”的条件，批量更新<br/>
	 * 请需要这样调用时再选择此方法，否则错误不可预测
	 */
	public final int updateByColumn(ContentValues values, String colName, String value) {
		return updateByClause(values, SQLiteCondition.create().setWhereColumnArray(colName).setWhereArgs(value));
	}
	
	/**
	 * 根据ID更新
	 */
	public final boolean updateById(long id, ContentValues values) {
		return 1 == mContentResolver.update(ContentUris.withAppendedId(mTableBaseUri, id), values, null, null);
	}
	
	/**
	 * @param values 要更新的字段及值
	 * @param value 主键的值
	 */
	public final boolean updateByPrimaryKey(ContentValues values, Object value) {
		return 1 == updateByClause(values, createSQLiteConditionFromPKValue(value));
	}
	
	
	
	// =============================================================================
	// ================================[delete]=====================================
	// =============================================================================
	@TemporarilyDone
	// delete
	/**
	 * 删除该表中所有的数据
	 */
	public int deleteAll() {
		return delete(null);
	}
	
	/**
	 * 根据“某列=某值”的情况删除<br/>
	 * 请需要这样调用时再选择此方法，否则错误不可预测
	 */
	public int deleteByColumn(String colName, String value) {
		return delete(SQLiteCondition.create().setWhereColumnArray(colName).setWhereArgs(value));
	}
	
	/**
	 * @param condition 如果为null，相当于删除所有
	 * @return 删除的列数
	 */
	public int delete(SQLiteCondition condition) {
		condition = checkSQLiteCondition(condition);
		return mContentResolver.delete(mTableBaseUri, condition.getWhereClause(),
				condition.getWhereArgs());
	}
	
	/**
	 * 根据外部定义的SQLiteConditionDefiner，确定条件删除集合当中与数据库相关的列。
	 */
	public <C extends Collection<T>>boolean deleteList(C list, SQLiteConditionDefiner<T> definer) {
		try {
			if (null == list || list.isEmpty()) return false;
			if (null == definer) return false;
			ArrayList<ContentProviderOperation> operations = 
					new ArrayList<ContentProviderOperation>();
			SQLiteCondition condition = SQLiteCondition.create();
			Iterator<T> ite = list.iterator();
			while (ite.hasNext()) {
				T t = ite.next();
				if (null == t) {
					ite.remove();
					continue;
				}
				definer.config(condition.reset(), t);
				operations.add(ContentProviderOperation.newDelete(mTableBaseUri)
						.withSelection(condition.getWhereClause(), condition.getWhereArgs()).build());
			}
			ContentProviderResult[] results = mContentResolver.applyBatch(mAuthority, operations);
			FrameworkLog.d(TAG, "delete<List, SQLiteConditionDefiner> list.size(): " + list.size());
			FrameworkLog.d(TAG, "delete<List, SQLiteConditionDefiner> results.length: " + results.length);
			return results.length == list.size();
		} catch (Exception e) {
			FrameworkLog.e(TAG, "delete<List, SQLiteConditionDefiner> Exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 根据主键删除列，框架内部集成了获取主键的方法，如果获取不到，则会抛出相应的异常，让客户端捕获，提醒使用者正确的使用方法。
	 */
	public <C extends Collection<T>>boolean deleteListByPrimaryKey(C list) {
		return deleteList(list, mPKSQLiteConditionDefiner);
	}
	
	/**
	 * 该方法是一个更加具体的提供机制，适用于主键是INTEGER的情况。
	 */
	public final boolean deleteById(long id) {
		return 1 == mContentResolver.delete(ContentUris.withAppendedId(mTableBaseUri, id), null, null);
	}
	
	/**
	 * 该方法是一个相对具体的提供机制，适用于任何数据类型的主键的情况。
	 * @param value PK的值
	 */
	public final boolean deleteByPrimaryKey(Object value) {
		return delete(createSQLiteConditionFromPKValue(value)) > 0;
	}

}