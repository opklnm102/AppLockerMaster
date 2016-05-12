package com.samsung.android.table;

import android.database.sqlite.SQLiteDatabase;

public class TblAppLocked {

	public static final String TBL_NAME = "tbl_app_lock";

	public static final String _ID = "id";// 1

	public static final String APP_PROCESS = "app_process";// 3
	public static final String APP_ALLOW = "app_allow";// 3

	private static String createData() {
		StringBuilder sBuiler = new StringBuilder();
		sBuiler.append("create table " + TBL_NAME + " (");
		sBuiler.append(_ID + " integer primary key autoincrement, ");
		sBuiler.append(APP_ALLOW + " integer, ");
		sBuiler.append(APP_PROCESS + " text);");
		return sBuiler.toString();
	}

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(createData());
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
		onCreate(database);
	}
}
