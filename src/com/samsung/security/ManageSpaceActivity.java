package com.samsung.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.samsung.android.util.Util;

public class ManageSpaceActivity extends Activity implements LockActivity.OnExitWithoutConfirm {

	private static final String TAG = "ManageSpaceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String typeLock = checkTypeLock();
		Intent intent = new Intent();
		if(typeLock.equals(Util.TYPE_LOCK_PASS)){
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, LockActivityByPassWord.class);
			startActivity(intent);
			finish();
		} else if(typeLock.equals(Util.TYPE_LOCK_PARTEN)){
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, LockActivityByPattern.class);
			startActivity(intent);
			finish();
		} else if(typeLock.equals(Util.TYPE_LOCK_FINGER)){
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, LockActivityByFingerprint.class);
			startActivity(intent);
			finish();
		}
	}
	
	private String checkTypeLock() {
		SharedPreferences sharedPreferences = getSharedPreferences(Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String typelock = sharedPreferences.getString(Util.TYPE_LOCK, Util.TYPE_LOCK_PASS);
		Log.i(TAG, "tran.thang checkTypeLock="+typelock);
		return typelock;
	}
	
	@Override
	public void exitWithoutConfirm() {
		Log.e("MainActivity", "exitWithoutConfirm");
		finish();
	}
}
