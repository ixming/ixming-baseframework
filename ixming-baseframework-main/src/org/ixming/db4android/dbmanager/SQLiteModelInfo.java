package org.ixming.db4android.dbmanager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.ixming.base.framework.annotation.UncertainState;
import org.ixming.base.utils.android.FrameworkLog;
import org.ixming.db4android.BaseSQLiteModel;
import org.ixming.db4android.Sqlable;
import org.ixming.db4android.annotation.Table;



class SQLiteModelInfo implements Sqlable{
	final static String TAG = SQLiteModelInfo.class.getSimpleName();
	
	private static final ConcurrentHashMap<Class<?>, WeakReference<SQLiteModelInfo>> mSQLiteModelInfoCache
		= new ConcurrentHashMap<Class<?>, WeakReference<SQLiteModelInfo>>();
	/**
	 * <p>
	 * <i>Tip1</i><br/>
	 * 传入的类必须是Annotation-{@link Table}标识的；
	 * </p>
	 * <p>
	 * <i>Tip2</i><br/>
	 * 传入的类必须是接口{@link BaseSQLiteModel}标识的；
	 * </p>
	 * <p>
	 * <i>Tip3</i><br/>
	 * 表字段列的查找顺序：从当前类往父类依次查询，直到遇到上层类中没有继承BaseSQLiteModel接口的情况才停止；
	 * </p>
	 * <p>
	 * <i>Tip4</i><br/>
	 * 关于<b>主键：</b>此处注意最先找到的主键认定为主键，所以确保主键定义不会受继承关系影响
	 * </p>
	 */
	public static SQLiteModelInfo parseModel(Class<?> clz) {
		return parseOfPullFromCache(clz, false);
	}
	
	static SQLiteModelInfo parseOfPullFromCache(Class<?> clz, boolean pullFromCache) {
		if (null == clz) {
			throw new NullPointerException("clz: " + clz + "!");
		}
		// get from cache
		SQLiteModelInfo targetInfo = findFromCache(clz, pullFromCache);
		if (null != targetInfo) {
			return targetInfo;
		}
		// new generated
		checkModifiers(clz);
		return generateNewInfo(clz);
	}
	
	private static SQLiteModelInfo findFromCache(Class<?> clz, boolean pullFromCache) {
		// get from cache
		WeakReference<SQLiteModelInfo> cacheRef;
		SQLiteModelInfo targetInfo;
		if (pullFromCache) {
			cacheRef = mSQLiteModelInfoCache.remove(clz);
		} else {
			cacheRef = mSQLiteModelInfoCache.get(clz);
		}
		if (null != cacheRef && null != (targetInfo = cacheRef.get())) {
			return targetInfo;
		}
		return null;
	}
	
	private static void checkModifiers(Class<?> clz) {
		int modifiers = clz.getModifiers();
		if (Modifier.isInterface(modifiers)) {
			throw new IllegalArgumentException("clz: " + clz + " is a interface!");
		}
		if (Modifier.isAbstract(modifiers)) {
			throw new IllegalArgumentException("clz: " + clz + " is a abstract class!");
		}
	}
	
	private static SQLiteModelInfo generateNewInfo(Class<?> clz) {
		// create a new one or update the map
		Table tableAn = clz.getAnnotation(Table.class);
		if (null == tableAn) {
			throw new IllegalArgumentException("clz: " + clz + " has no <Table> Annotation!");
		}
		SQLiteColumnInfo pkInfo = null;
		HashMap<String, SQLiteColumnInfo> columnInfoMap = new HashMap<String, SQLiteColumnInfo>();
		HashMap<String, SQLiteColumnInfo> indexedColumnMap = null;
		boolean isPKLoaded = false;
		boolean isTargetClass = true;
		@UncertainState
		Class<?> raw = clz;
		while (null != raw && BaseSQLiteModel.class.isAssignableFrom(raw)) {
			Field[] fields = raw.getDeclaredFields();
			if (null == fields || fields.length == 0) {
				//throw new UnsupportedOperationException("there is no field inside class: " + clz);
			} else {
				for (Field f: fields) {
					SQLiteColumnInfo colInfo = SQLiteColumnInfo.createFrom(f);
					// 不是目标类
					if (null == colInfo) {
						FrameworkLog.d(TAG, "parseModel::not a target Column field");
						continue;
					}
					if (!isTargetClass && !colInfo.isExtendable()) {
						FrameworkLog.d(TAG, "parseModel::not a extendable Column field");
						continue;
					}
					// 判断是否是主键。此处注意最先找到的主键认定为主键，所以确保主键定义不会受继承关系影响
					if (colInfo.isPrimaryKey()) {
						if (isPKLoaded) {
							FrameworkLog.w(TAG, "parseModel::there are multi extendable Column fields!");
							//throw new UnsupportedOperationException("parseModel::there are multi extendable Column fields!"
							//		+ "please set 'false' to Column's 'extendable' field in super classes!");
						} else {
							isPKLoaded = true;
							pkInfo = colInfo;
						}
						continue;
					} else {
						if (colInfo.isAsIndex()) {
							if (null == indexedColumnMap) {
								indexedColumnMap = new HashMap<String, SQLiteColumnInfo>();
							}
							indexedColumnMap.put(colInfo.getColumnName(), colInfo);
						}
					}
					// 内部实现的hash查询应当快一点，与使用HashSet一样，而HashSet内部实际是HashMap实现的
					if (null != columnInfoMap && columnInfoMap.containsKey(colInfo.getColumnName())) {
						throw new RuntimeException("clz: " + clz + " has multi columns named ["
								+ colInfo.getColumnName() + "]!");
					}
					if (null == columnInfoMap) {
						columnInfoMap = new HashMap<String, SQLiteColumnInfo>();	
					}
					columnInfoMap.put(colInfo.getColumnName(), colInfo);
				}
			}
			raw = raw.getSuperclass();
			isTargetClass = false;
		}

		if (null == pkInfo && columnInfoMap.isEmpty()) {
			throw new UnsupportedOperationException("no columns! if exception occurs when doing parsing? or "
					+ "no class implements interface <BaseSQLiteModel>");
		}
		SQLiteModelInfo targetInfo = new SQLiteModelInfo(tableAn, pkInfo, columnInfoMap, indexedColumnMap);
		mSQLiteModelInfoCache.put(clz, new WeakReference<SQLiteModelInfo>(targetInfo));
		return targetInfo;
	}
	
	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// inner implement
	private Table mTableInfo;
	private SQLiteColumnInfo mPrimaryColumn;
	private HashMap<String, SQLiteColumnInfo> mColumnMap;
	private HashMap<String, SQLiteColumnInfo> mIndexedColumnMap;
	SQLiteModelInfo(Table tableInfo, SQLiteColumnInfo primaryColumn,
			HashMap<String, SQLiteColumnInfo> columnMap,
			HashMap<String, SQLiteColumnInfo> indexedColumnMap) {
		mTableInfo = tableInfo;
		mPrimaryColumn = primaryColumn;
		mColumnMap = columnMap;
		mIndexedColumnMap = indexedColumnMap;
	}
	
	public String getAuthority() {
		return mTableInfo.authority();
	}
	
	public String getTableName() {
		return mTableInfo.name();
	}
	
	public String[] getColumns() {
		int count = 0;
		if (null != mPrimaryColumn) count++;
		if (null != mColumnMap) count += mColumnMap.size();
		String[] cols = new String[count];
		int index = 0;
		if (null != mPrimaryColumn) cols[index++] = mPrimaryColumn.getColumnName();
		for (SQLiteColumnInfo colInfo : mColumnMap.values()) {
			cols[index++] = colInfo.getColumnName();
		}
		return cols;
	}
	
	public SQLiteColumnInfo getPKInfo() {
		return mPrimaryColumn;
	}
	
	public SQLiteColumnInfo getColumnInfo(String colName) {
		if (null != mPrimaryColumn && mPrimaryColumn.getColumnName().equalsIgnoreCase(colName)) {
			return mPrimaryColumn;
		}
		return mColumnMap.get(colName);
	}

	@Override
	public String toSql() {
		StringBuilder sb = new StringBuilder(50);
		
		boolean hasPK = (null != mPrimaryColumn);
		sb.append("CREATE TABLE").append(SEPERATOR)
			.append(mTableInfo.name()).append(SEPERATOR);
		
		sb.append(LEFT_BRACKET).append(SEPERATOR);
		if (hasPK) {
			sb.append(mPrimaryColumn.toSql());
			sb.append(SEPERATOR);
		}
		
		int i = -1;
		Iterator<SQLiteColumnInfo> ite = mColumnMap.values().iterator();
		while (ite.hasNext()) {
			if (i < 0) {
				if (hasPK) {
					sb.append(COMMA).append(SEPERATOR);
				}
				i ++;
			} else {
				sb.append(COMMA).append(SEPERATOR);
			}
			sb.append(ite.next().toSql());
			sb.append(SEPERATOR);
		}
		
		sb.append(RIGHT_BRACKET).append(END);
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return toSql();
	}
	
	public String[] getIndexCreations() {
		if (null == mIndexedColumnMap) {
			return null;
		}
		String[] temp = new String[mIndexedColumnMap.size()];
		Iterator<SQLiteColumnInfo> ite = mIndexedColumnMap.values().iterator();
		String tableName = mTableInfo.name();
		int i = 0;
		while (ite.hasNext()) {
			StringBuffer sb = new StringBuffer(50);
			SQLiteColumnInfo col = ite.next();
			sb.append("CREATE INDEX").append(SEPERATOR)
			.append(tableName).append(UNDERSCORE).append(col.getColumnName()).append(SEPERATOR)
			.append("on").append(SEPERATOR).append(tableName)
			.append(LEFT_BRACKET).append(col.getColumnName()).append(RIGHT_BRACKET);
			sb.append(END);
			temp[i++] = sb.toString();
		}
		return temp;
	}
	
	@Override
	protected void finalize() throws Throwable {
		FrameworkLog.d(TAG, "finalized");
		
		mTableInfo = null;
		mPrimaryColumn = null;
		if (null != mColumnMap) {
			mColumnMap.clear();
			mColumnMap = null;
		}
		if (null != mIndexedColumnMap) {
			mIndexedColumnMap.clear();
			mIndexedColumnMap = null;
		}
		
		super.finalize();
	}
}
