package com.samsung.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.fingerprint.FingerprintManager;
import com.samsung.android.util.Util;
import com.samsung.dialog.DialogManager;
import com.samsung.security.pattentdesign.PatternConfiguration;

public class SecurityTypeActivity extends Activity implements OnTouchListener {
	private Context mContext;
	private String TAG = "SettingFragment";
	private LinearLayout cmPattern, cmPaswordLock, cmFingerPrint;
	public static final int REQUEST_PATTERN = 125;
	public static final int REQUEST_PASSWORD = 136;
	public static final int REQUEST_FINGER = 141;
	private RelativeLayout itemFinger;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.changetype_layout);
		mContext = this;
		cmPattern = (LinearLayout) findViewById(R.id.cmPattern);
		cmPaswordLock = (LinearLayout) findViewById(R.id.cmPaswordLock);
		cmFingerPrint = (LinearLayout) findViewById(R.id.cmFingerPrint);

		cmPattern.setOnTouchListener(this);
		cmPaswordLock.setOnTouchListener(this);
		cmFingerPrint.setOnTouchListener(this);

		itemFinger = (RelativeLayout) findViewById(R.id.itemFinger);
		if (!checkIfFingerAvailable())
			itemFinger.setVisibility(View.GONE);
		
		// enabling action bar app icon and behaving it as toggle button
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);

	}

	private boolean checkIfFingerAvailable() {
		boolean flag = false;
		try {
			flag = FingerprintManager.getInstance(this,
					FingerprintManager.SECURITY_LEVEL_HIGH) == null ? false
					: true;
		} catch (NoClassDefFoundError e) {
		}
		return flag;

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Log.i(TAG, "onTouch...");
		switch (view.getId()) {
		case R.id.cmPattern:
			changeLayoutBG(cmPattern, event.getAction(), R.id.cmPattern);
			break;
		case R.id.cmPaswordLock:
			changeLayoutBG(cmPaswordLock, event.getAction(), R.id.cmPaswordLock);
			break;
		case R.id.cmFingerPrint:
			changeLayoutBG(cmFingerPrint, event.getAction(), R.id.cmFingerPrint);
			break;

		default:
			break;
		}
		return true;
	}

	public void showSettingActivity(int id) {
		Intent intent;
		switch (id) {
		case R.id.cmPattern:
			intent = new Intent(this, PatternConfiguration.class);
			startActivityForResult(intent, REQUEST_PATTERN);
			break;
		case R.id.cmPaswordLock:
			DialogManager dialogMgr = new DialogManager(this,
					DialogManager.DIALOG_SETUP, true);
			dialogMgr.show();
			break;
		case R.id.cmFingerPrint:
			intent = new Intent(this, LockActivityByFingerprint.class);
			intent.putExtra(Util.KEY_CONFIRM_EXIT, true);
			startActivityForResult(intent, REQUEST_FINGER);
			break;

		default:
			break;
		}
	}

	public void changeLayoutBG(LinearLayout item, int state, int id) {
		if (state == MotionEvent.ACTION_DOWN) {
			item.setBackgroundColor(mContext.getResources().getColor(
					R.color.color_desciption_press));
			showSettingActivity(id);
		} else if (state == MotionEvent.ACTION_UP) {
			item.setBackgroundColor(Color.WHITE);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			
		default:
			break;
		}
		return true;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onActivityResult..." + requestCode + ":" + resultCode);
		String result;
		SharedPreferences preferences = getSharedPreferences(
				Util.PREFEREN_NAME, MODE_PRIVATE);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PATTERN:
			if (resultCode == RESULT_OK) {
				// get type of
				Log.i(TAG, "onActivityResult...REQUEST_PATTERN");
				result = data.getStringExtra("RESULT");
				Editor editor = preferences.edit();
				editor.putString(Util.TYPE_LOCK, result);
				editor.commit();
				finish();
			}
			break;
		case REQUEST_FINGER:
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "onActivityResult...REQUEST_FINGER");
				// get type of
				result = data.getStringExtra("RESULT");
				Editor editor = preferences.edit();
				editor.putString(Util.TYPE_LOCK, result);
				editor.commit();
				finish();
			}
			break;
		default:
			break;
		}
	}
}