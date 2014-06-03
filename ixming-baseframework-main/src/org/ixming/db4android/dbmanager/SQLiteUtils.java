package org.ixming.db4android.dbmanager;

import org.ixming.base.utils.android.FrameworkLog;
import org.ixming.db4android.BaseSQLiteModel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


public class SQLiteUtils {

	static final String TAG = SQLiteUtils.class.getSimpleName();
	
	private SQLiteUtils() { }

	public static <T extends BaseSQLiteModel>boolean createTableAndIndices(SQLiteDatabase db, Class<T> clz) {
		try {
			String sql = DBManager.getTableCreation(clz);
			FrameworkLog.d(TAG, "createTableAndIndices table: " + sql);
			db.execSQL(sql);
			String indexes[] = DBManager.getTableIndexCreation(clz);
			if (null != indexes) {
				for (String indexSql : indexes) {
					FrameworkLog.d(TAG, "createTableAndIndices index: " + indexSql);
					db.execSQL(indexSql);
				}
			}
			return true;
		} catch (Exception e) {
			FrameworkLog.e(TAG, "createTableAndIndices exception : " + e.getMessage());
			return false;
		}
	}
	
	// utilities
	public static boolean isTableExisted(SQLiteDatabase db, String tableName) {
		boolean flag = false;
		Cursor c = null;
		if (TextUtils.isEmpty(tableName)) {
			return flag;
		}
		try {
			c = db.query(" sqlite_master ", new String[] { "name" },
					" type='table' AND name = ? ", new String[] { tableName },
					null, null, null);
			if (null != c) {
				flag = c.getCount() > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			if (null != c) {
				c.close();
			}
			FrameworkLog.i(TAG, tableName + " isTableExisted " + flag);
		}
		return flag;
	}

	public static void dropTable(SQLiteDatabase db, String tableName) {
		if (TextUtils.isEmpty(tableName)) {
			return;
		}
		try {
			db.execSQL(String.format("DROP TABLE IF EXISTS %s;", tableName));
		} catch (Exception e) {
			FrameworkLog.e(TAG, "dropTable Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void dropTables(SQLiteDatabase db, String[] tableNames) {
		if (null == tableNames || tableNames.length == 0) {
			return;
		}
		db.beginTransaction();
		try {
			final String ORIGINAL_SQL = "DROP TABLE IF EXISTS %s;";
			for (String tabName : tableNames) {
				if (!TextUtils.isEmpty(tabName))
					db.execSQL(String.format(ORIGINAL_SQL, tabName));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			FrameworkLog.e(TAG, "dropTables Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// 结束事务
			if (null != db)
				db.endTransaction();
		}
	}
	
	public static boolean excSQLByTransactions(SQLiteDatabase db, String[] sqls) {
		if (null == sqls || sqls.length == 0) {
			return false;
		}
		db.beginTransaction();
		try {
			for (String sql : sqls) {
				if (!TextUtils.isEmpty(sql))
					db.execSQL(sql);
			}
			db.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			FrameworkLog.e(TAG, "excSQLByTransactions Exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			// 结束事务
			if (null != db)
				db.endTransaction();
		}
	}

}
