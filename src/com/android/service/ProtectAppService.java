package com.android.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;
import com.samsung.security.LockActivity;
import com.samsung.security.LockActivityByFingerprint;
import com.samsung.security.LockActivityByPassWord;
import com.samsung.security.LockActivityByPattern;

public class ProtectAppService extends Service {

	private Context mContext;
	// private static final String ACTION_CREEN_ON =
	// "android.intent.action.SCREEN_ON";
	private static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
	public static String PROCESS_NAME = "procesname";


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SCREEN_OFF);
		registerReceiver(mBroadcastReceiver, filter);

		new Thread() {
			public void run() {

				Intent ik = new Intent();
				Class<? extends LockActivity> classObject;
				while (true) {
					String packageName = getTopAplication();
					if (checkApp(packageName)) {
						classObject = checkTypeLock().equals(
								Util.TYPE_LOCK_PASS) ? LockActivityByPassWord.class
								: (checkTypeLock()
										.equals(Util.TYPE_LOCK_PARTEN) ? LockActivityByPattern.class
										: (checkTypeLock().equals(
												Util.TYPE_LOCK_FINGER) ? LockActivityByFingerprint.class
												: null));
						if (classObject != null)
							Util.TOP_PROCESS_NEED_IGNOR = packageName;
						ik.setClass(mContext, classObject);
						ik.putExtra(PROCESS_NAME, packageName);
						ik.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(ik);
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private boolean checkApp(String name) {
		Uri mUri = LockAppProvider.URI_APPLOCK;
		String mID = TblAppLocked._ID;
		String mTblName = TblAppLocked.APP_PROCESS;
		String mAllow = TblAppLocked.APP_ALLOW;
		if (checkMode().equals(mContext.getString(R.string.kid))) {
			mUri = LockAppProvider.URI_APPKID;
			mID = TblKidApp._ID;
			mTblName = TblKidApp.APP_PROCESS;
			mAllow = TblAppLocked.APP_ALLOW;
		} else if (checkMode().equals(mContext.getString(R.string.friend))) {
			mUri = LockAppProvider.URI_APPFRIEND;
			mID = TblFriendApp._ID;
			mTblName = TblFriendApp.APP_PROCESS;
			mAllow = TblAppLocked.APP_ALLOW;
		}
		Cursor c = mContext.getContentResolver().query(mUri,
				new String[] { mID },
				mTblName + " = '" + name + "'" + " AND " + mAllow + " = '0'",
				null, null);
		int n = c.getCount();
		c.close();
		if (n > 0) {
			return true;
		} else {
			return false;
		}
	}

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Cursor c = context.getContentResolver().query(
					LockAppProvider.URI_APPLOCK, null,
					TblAppLocked.APP_ALLOW + " = '1'", null, null);
			while (c.moveToNext()) {
				String name = c.getString(c
						.getColumnIndexOrThrow(TblAppLocked.APP_PROCESS));
				ContentValues contentValues = new ContentValues();
				contentValues.put(TblAppLocked.APP_ALLOW, 0);
				context.getContentResolver().update(
						LockAppProvider.URI_APPLOCK, contentValues,
						TblAppLocked.APP_PROCESS + "=?", new String[] { name });
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
				context.getContentResolver().update(
						LockAppProvider.URI_APPFRIEND, contentValues,
						TblFriendApp.APP_PROCESS + "=?", new String[] { name });
			}
			d.close();
		}
	};

	private String checkTypeLock() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String typelock = sharedPreferences.getString(Util.TYPE_LOCK,
				Util.TYPE_LOCK_PASS);
		return typelock;
	}

	private String checkMode() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String mode = sharedPreferences.getString(Util.TYPE_MODE,
				mContext.getString(R.string.customize));
		return mode;
	}

	private String getTopAplication() {
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		ActivityManager am = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = "";
		if (sdkVersion < 21) {
			List<ActivityManager.RunningTaskInfo> pids = am.getRunningTasks(1);
			packageName = pids.get(0).topActivity.getPackageName();
		} else {
			List<ActivityManager.RunningAppProcessInfo> pids = am
					.getRunningAppProcesses();
			ActivityManager.RunningAppProcessInfo info = pids.get(0);

			for (MyApplication myApplication : Util.mListApplicationList) {
				if (info.processName.contains(myApplication.getPackageName())) {
					packageName = myApplication.getPackageName();
					break;
				}
			}
		}
		return packageName;
	}
}
