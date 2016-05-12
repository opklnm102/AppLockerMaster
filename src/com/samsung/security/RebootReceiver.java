package com.samsung.security;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.android.service.ProtectAppService;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;
import com.samsung.contentprovider.LockAppProvider;

public class RebootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Updatedatabase(context);
		context.startService(new Intent(context, ProtectAppService.class));
	}

	private void Updatedatabase(Context context) {
		Cursor c = context.getContentResolver().query(
				LockAppProvider.URI_APPLOCK, null,
				TblAppLocked.APP_ALLOW + " = '1'", null, null);
		while (c.moveToNext()) {
			String name = c.getString(c
					.getColumnIndexOrThrow(TblAppLocked.APP_PROCESS));
			ContentValues contentValues = new ContentValues();
			contentValues.put(TblAppLocked.APP_ALLOW, 0);
			context.getContentResolver().update(LockAppProvider.URI_APPLOCK,
					contentValues, TblAppLocked.APP_PROCESS + "=?",
					new String[] { name });
		}
		c.close();
		Cursor e = context.getContentResolver().query(
				LockAppProvider.URI_APPKID, null,
				TblKidApp.APP_ALLOW + " = '1'", null, null);
		while (e.moveToNext()) {
			String name = e.getString(e
					.getColumnIndexOrThrow(TblKidApp.APP_PROCESS));
			ContentValues contentValues = new ContentValues();
			contentValues.put(TblKidApp.APP_ALLOW, 0);
			context.getContentResolver().update(LockAppProvider.URI_APPKID,
					contentValues, TblKidApp.APP_PROCESS + "=?",
					new String[] { name });
		}
		e.close();
		Cursor d = context.getContentResolver().query(
				LockAppProvider.URI_APPFRIEND, null,
				TblFriendApp.APP_ALLOW + " = '1'", null, null);
		while (d.moveToNext()) {
			String name = d.getString(d
					.getColumnIndexOrThrow(TblFriendApp.APP_PROCESS));
			ContentValues contentValues = new ContentValues();
			contentValues.put(TblFriendApp.APP_ALLOW, 0);
			context.getContentResolver().update(LockAppProvider.URI_APPFRIEND,
					contentValues, TblFriendApp.APP_PROCESS + "=?",
					new String[] { name });
		}
		d.close();

	}

};