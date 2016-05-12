package com.samsung.contentprovider;

import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * @author tuyenpx class nay dung de tao co so du lieu sql su dung content
 *         provider
 */

public class LockAppProvider extends ContentProvider {

	public static final String PRO_NAME = "com.samsung.contentprovider";

	public static final Uri URI_APPLOCK = Uri.parse("content://" + PRO_NAME
			+ "/" + TblAppLocked.TBL_NAME);
	public static final Uri URI_APPKID = Uri.parse("content://" + PRO_NAME
			+ "/" + TblKidApp.TBL_NAME);
	public static final Uri URI_APPFRIEND = Uri.parse("content://" + PRO_NAME
			+ "/" + TblFriendApp.TBL_NAME);
	//tbl nomal :
	private static final int M_APPLOCK = 0;
	//tbl kid app :
	private static final int M_APPKID= 1;
	//tbl friend app :
	private static final int M_APPFRIEND = 2;

	private static final String TAG = "LockAppProvider";
	public static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PRO_NAME, TblAppLocked.TBL_NAME, M_APPLOCK);
		uriMatcher.addURI(PRO_NAME, TblFriendApp.TBL_NAME, M_APPFRIEND);
		uriMatcher.addURI(PRO_NAME, TblKidApp.TBL_NAME, M_APPKID);
		
	}
	private static final String DATABASE_NAME = "Database_AppLock";

	private static final int DATABASE_VERSION = 4;

	private DatabaseHelper mDbHelper;

	// using DatabaseHelper Class to help manage your database
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			TblAppLocked.onCreate(db); // 0
			TblKidApp.onCreate(db);
			TblFriendApp.onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			TblAppLocked.onUpgrade(db, oldVersion, newVersion); // 0
			TblKidApp.onUpgrade(db, oldVersion, newVersion); // 0
			TblFriendApp.onUpgrade(db, oldVersion, newVersion); // 0
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = "";
		switch (uriMatcher.match(uri)) {
		case M_APPLOCK: // 0
			table = TblAppLocked.TBL_NAME;
			break;
		case M_APPFRIEND: // 0
			table = TblFriendApp.TBL_NAME;
			break;
		case M_APPKID: // 0
			table = TblKidApp.TBL_NAME;
			break;
		default:
			break;
		}
		Log.e(TAG, "Xoa table " + table);

		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		int deleteCount = sqlDB.delete(table, selection, selectionArgs);
		Log.e(TAG, "Tong so dong da xoa = " + deleteCount);

		// Thong bao den cac observer ve su thay doi
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int key = uriMatcher.match(uri);
		String table = "";

		switch (key) {
		case M_APPLOCK: // 0
			table = TblAppLocked.TBL_NAME;
			break;
		case M_APPFRIEND: // 0
			table = TblFriendApp.TBL_NAME;
			break;
		case M_APPKID: // 0
			table = TblKidApp.TBL_NAME;
			break;
		default:
			break;
		}

		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		long rowID = sqlDB.insertWithOnConflict(table, "", values,
				SQLiteDatabase.CONFLICT_REPLACE);
		getContext().getContentResolver().notifyChange(uri, null);
		if (rowID > 0) {
			return Uri.withAppendedPath(uri, String.valueOf(rowID));
		} else {
			return null;

		}
	}

	@Override
	public boolean onCreate() {

		Context context = getContext();
		mDbHelper = new DatabaseHelper(context);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		int key = uriMatcher.match(uri);
		String table = "";

		switch (key) {
		case M_APPLOCK:// 0
			table = TblAppLocked.TBL_NAME;
			break;
		case M_APPFRIEND:// 0
			table = TblFriendApp.TBL_NAME;
			break;
		case M_APPKID:// 0
			table = TblKidApp.TBL_NAME;
			break;
		default:
			break;
		}

		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(table);
		sqlBuilder.setDistinct(true);
		Cursor c = sqlBuilder.query(sqlDB, projection, selection,
				selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int key = uriMatcher.match(uri);
		String table = "";

		switch (key) {
		case M_APPLOCK:// 0
			table = TblAppLocked.TBL_NAME;
			break;
		case M_APPFRIEND:// 0
			table = TblFriendApp.TBL_NAME;
			break;
		case M_APPKID:// 0
			table = TblKidApp.TBL_NAME;
			break;
		default:
			break;
		}

		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		int rowEffect = sqlDB.update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowEffect;
	}
}
