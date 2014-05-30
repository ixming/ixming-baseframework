package org.ixming.db4android.provider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ixming.base.framework.annotation.TemporarilyDone;
import org.ixming.base.framework.annotation.UncertainState;
import org.ixming.base.utils.android.FrameworkLog;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;


/**
 * 这是一个基于ContentProvider的封装。
 * 
 * <p>
 * this is a basic class to deal with database;
 * it doesn't support Uri as complex as “content://xxx/#/xxx/#”
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
@TemporarilyDone
public abstract class BaseDBProvider extends ContentProvider{
	public static final String TAG = BaseDBProvider.class.getSimpleName();

	/** provide a new subclass of SQLiteOpenHelper, this is only called in onCreate()*/
	protected abstract SQLiteOpenHelper provideSQLiteOpenHelper(Context context);
	/** get the database authority*/
	protected abstract String provideAuthority();
//	/** get the current database version int*/
//	protected abstract int getCurrentVersion();
	
	protected final SQLiteOpenHelper getSQLiteOpenHelper() {
		return mSQLiteOpenHelper;
	}
	
	protected final SQLiteDatabase getWritableDatabase() {
		return mDb;
	}
	
	private final String SELECTION_BY_ID = BaseColumns._ID + " = ? ";
	
	private final int URI_MATCHER_TABLE = 0;
	private final int URI_MATCHER_ID = 1;
	
	// inner properties
	private String mAuthority;
	private UriMatcher mUriMatcher;
	private SQLiteOpenHelper mSQLiteOpenHelper;
	private SQLiteDatabase mDb;
	private Context mContext;
	@Override
	public final boolean onCreate() {
		FrameworkLog.i(TAG, "BaseDBProvider onCreate()");
		mContext = getContext();
		mAuthority = provideAuthority();
		if (TextUtils.isEmpty(mAuthority)) {
			return false;
		}
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(mAuthority, "*", URI_MATCHER_TABLE);
		mUriMatcher.addURI(mAuthority, "*/#", URI_MATCHER_ID);
		
		mSQLiteOpenHelper = provideSQLiteOpenHelper(mContext);
		mDb = mSQLiteOpenHelper.getWritableDatabase();
		return true;
	}
	
	@Override
	public final Uri insert(Uri uri, ContentValues values) {
		UriArgs uriArgs = createUriArgsFrom(uri);
		try {
			long rowID = mDb.insert(uriArgs.getTableName(), null, values);
			if (rowID > 0) {
				try {
					return ContentUris.withAppendedId(uri, rowID);
				} finally {
					//TODO 通知监听者
					mContext.getContentResolver().notifyChange(uri, null);
				}
			}
			return null;
		} finally {
			recycleUriArgs(uriArgs);
		}
	}

	@Override
	public final Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		UriArgs uriArgs = createUriArgsFrom(uri);
		if (uriArgs.isById()) {
			// if it is a query-by-id, ignore the basic parameters--selection, selectionArgs
			selection = SELECTION_BY_ID;
			selectionArgs = new String[]{ uriArgs.getIdArgs() };
		}
		try {
			return mDb.query(uriArgs.getTableName(), projection,
					selection, selectionArgs, null, null, sortOrder);
		} finally {
			recycleUriArgs(uriArgs);			
		}
	}
	
	@Override
	public final int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		UriArgs uriArgs = createUriArgsFrom(uri);
		if (uriArgs.isById()) {
			// if it is a query-by-id, ignore the basic parameters--selection, selectionArgs
			selection = SELECTION_BY_ID;
			selectionArgs = new String[]{ uriArgs.getIdArgs() };
		}
		try {
			return mDb.update(uriArgs.getTableName(), values, selection, selectionArgs);
		} finally {
			mContext.getContentResolver().notifyChange(
					uri.buildUpon().encodedPath(uriArgs.getTableName()).build(), null);
			recycleUriArgs(uriArgs);
		}
	}
	
	@Override
	public final int delete(Uri uri, String selection, String[] selectionArgs) {
		UriArgs uriArgs = createUriArgsFrom(uri);
		try {
			if (uriArgs.isById()) {
				// if it is a query-by-id, ignore the basic parameters--selection, selectionArgs
				selection = SELECTION_BY_ID;
				selectionArgs = new String[]{ uriArgs.getIdArgs() };
			}
			return mDb.delete(uriArgs.getTableName(), selection, selectionArgs);
		} finally {
			mContext.getContentResolver().notifyChange(
					uri.buildUpon().encodedPath(uriArgs.getTableName()).build(), null);
			recycleUriArgs(uriArgs);
		}
	}
	
	@Override
	public final ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		// 事务开始
		mDb.beginTransaction();
		try {
			ContentProviderResult[] results = super.applyBatch(operations);
			// 设置事务标示为successful
			mDb.setTransactionSuccessful();
			FrameworkLog.i(TAG, "BaseDBProvider applyBatch count ----->>" + results.length);
			return results;
		} finally {
			// 结束事务
			if (null != mDb)
				mDb.endTransaction();
		}
	}
	
	@Override
	public final String getType(Uri uri) {
		UriArgs uriArgs = null;
		try {
			uriArgs = createUriArgsFrom(uri);
			if (uriArgs.isById()) {
				return "vnd.android.cursor.item/" + uriArgs.getTableName();
			} else {
				return "vnd.android.cursor.dir/" + uriArgs.getTableName();
			}
		} catch (Exception e) {
			return null;
		} finally {
			recycleUriArgs(uriArgs);
		}
	}
	
	private LinkedList<UriArgs> mUriArgsPool = new LinkedList<UriArgs>();
	private void recycleUriArgs(UriArgs uriArgs) {
		if (null == uriArgs) {
			return ;
		}
		synchronized (mUriArgsPool) {
			if (mUriArgsPool.size() < 3) {
				mUriArgsPool.add(uriArgs);
			}
		}
	}
	private UriArgs obtainUriArgs(Uri uri) {
		synchronized (mUriArgsPool) {
			if (!mUriArgsPool.isEmpty()) {
				return mUriArgsPool.remove().update(uri);
			}
		}
		return new UriArgs().update(uri);
	}
	@UncertainState
	protected final UriArgs createUriArgsFrom(Uri uri) {
//		return new WeakReference<UriArgs>(new UriArgs(uri)).get();
		return obtainUriArgs(uri);
	}
	
	/**
	 * 解析出Uri中我们需要的信息
	 * @author Yin Yong
	 * @version 1.0
	 */
	protected final class UriArgs {
		private String mTableName;
		private boolean mIsById;
		private String mIdArgs;
		
		private UriArgs() { }
		
		public UriArgs update(Uri uri) {
			checkUriSecurity(uri);
			List<String> pathSegments = uri.getPathSegments();
			mTableName = pathSegments.get(0);
			if (pathSegments.size() < 2) {
				return this;
			}
			mIsById = checkIsById(uri);
			if (mIsById) {
				mIdArgs = pathSegments.get(1);
			}
			return this;
		}
		
		public UriArgs recycle() {
			mTableName = null;
			mIsById = false;
			mIdArgs = null;
			return this;
		}
		
		public String getTableName() {
			return mTableName;
		}
		
		public boolean isById() {
			return mIsById;
		}
		
		public String getIdArgs() {
			return mIdArgs;
		}
		
		private void checkUriSecurity(Uri uri) {
			if (null == uri) {
				throw new NullPointerException("uri is null!");
			}
			if (!mAuthority.equals(uri.getAuthority())) {
				throw new UnsupportedOperationException("uri's authority is ["
						+ uri.getAuthority() + "], which is unvalid!");
			}
			List<String> pathSegments = uri.getPathSegments();
			if (null == pathSegments || pathSegments.isEmpty()) {
				throw new UnsupportedOperationException("uri is [" + uri.toString() + "], "
						+ "its pathSegment is null, which is unvalid!");
			}
		}
		
		private boolean checkIsById(Uri uri) {
			if (null == uri) {
				return false;
			}
			int match = mUriMatcher.match(uri);
			return URI_MATCHER_ID == match;
		}
	}
	
}