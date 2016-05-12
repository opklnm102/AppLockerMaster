package com.samsung.security;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.service.ProtectAppService;
import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;
import com.samsung.security.pattentdesign.PatternItem;
import com.samsung.security.pattentdesign.PatternView;

public class LockActivityByPattern extends LockActivity implements
		PatternView.OnPatternListener {

	private static final String TAG = "LockActivityByPattern";
	private LinearLayout panel;
	private PatternView pattenView;
	private Button btnCancel, btnContinue;
	private TextView txtStatus, txtTitleAnnouce;
	private SharedPreferences sharePrefer;
	private List<PatternItem> arr;
	private ImageView imageICon;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pattern_layout);

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnContinue = (Button) findViewById(R.id.btnContinue);
		btnCancel.setVisibility(View.GONE);
		btnContinue.setVisibility(View.GONE);
		imageICon = (ImageView) findViewById(R.id.icon_pattern);
		txtTitleAnnouce = (TextView) findViewById(R.id.title_annouce);
		txtTitleAnnouce.setVisibility(View.VISIBLE);
		txtStatus = (TextView) findViewById(R.id.title);
		txtStatus.setVisibility(View.GONE);

		panel = (LinearLayout) findViewById(R.id.panel);
		pattenView = new PatternView(this, this);
		panel.addView(pattenView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		String mProcessName = getIntent().getStringExtra(
				ProtectAppService.PROCESS_NAME);
		Log.d(TAG, "tran.thang mProcessName = " + mProcessName);
		setIcon(mProcessName, imageICon);
		arr = restoreData();
	}

	@Override
	public void drawPatternCompleteHalf(List<PatternItem> arr) {
		Log.i(TAG, "drawPatternCompleteHalf is called..." + arr);
		pattenView.lock();
		if (!isCorrect(arr)) {
			txtStatus.setText(getString(R.string.annouce_wrong_pattern));
			txtTitleAnnouce.setText(getString(R.string.annouce_wrong_pattern));
		} else {
			String mode = checkMode();
			if (mode.equals(getString(R.string.kid))) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(TblKidApp.APP_ALLOW, 1);
				getContentResolver().update(LockAppProvider.URI_APPKID,
						contentValues, TblKidApp.APP_PROCESS + " = ?",
						new String[] { Util.TOP_PROCESS_NEED_IGNOR });
			} else if (mode.equals(getString(R.string.friend))) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(TblFriendApp.APP_ALLOW, 1);
				getContentResolver().update(LockAppProvider.URI_APPFRIEND,
						contentValues, TblFriendApp.APP_PROCESS + " = ?",
						new String[] { Util.TOP_PROCESS_NEED_IGNOR });
			} else {
				ContentValues contentValues = new ContentValues();
				contentValues.put(TblAppLocked.APP_ALLOW, 1);
				getContentResolver().update(LockAppProvider.URI_APPLOCK,
						contentValues, TblAppLocked.APP_PROCESS + " = ?",
						new String[] { Util.TOP_PROCESS_NEED_IGNOR });
			}
			try {
				String s = getIntent().getStringExtra("tuyen.px");
				if (s.equals("tuyen.px"))
					Util.flag_check = true;
			} catch (Exception e) {
			}
			finish();
		}
		pattenView.resetAll();
		pattenView.unLock();
	}

	public boolean isCorrect(List<PatternItem> src) {

		// You dont have pattern which saved before.==>arr = null
		if (arr == null) {
			Toast.makeText(this, getString(R.string.annouce_input_pattern),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (arr.size() != src.size()) {
			Log.w(TAG,
					"isCorrect is called..., arr.size != src.size" + arr.size()
							+ ":" + src.size());
			return false;
		}

		for (int i = 0; i < arr.size(); i++) {
			Log.e(TAG, "tuyenpx: arr.get(i).x : arr.get(i).y = "+arr.get(i).x+" : "+arr.get(i).y );
			Log.e(TAG, "tuyenpx: src.get(i).x : src.get(i).y = "+src.get(i).x+" : "+src.get(i).y );
			if (arr.get(i).x != src.get(i).x || arr.get(i).y != src.get(i).y) {
				Log.e(TAG, "isCorrect is called..., arr diff src at : " + i);
				return false;
			}
		}
		return true;
	}

	@Override
	public void drawPatternComplete(List<PatternItem> arr) {

	}

	public List<PatternItem> restoreData() {
		sharePrefer = getSharedPreferences(Util.PREFEREN_NAME,
				Context.MODE_PRIVATE);
		String srt = sharePrefer.getString(PatternView.KEY_PATTERN, "");
		Log.e("tuyen.px", "gia tri cua srt la : " + srt);
		if (srt.equals(""))
			return null;
		List<PatternItem> arr = new ArrayList<PatternItem>();
		String arrS[] = srt.split(PatternView.SPACE);
		int x, y;
		for (int i = 0; i < arrS.length; i++) {
			x = Integer.parseInt(arrS[i].split("_")[0]);
			y = Integer.parseInt(arrS[i].split("_")[1]);
			arr.add(new PatternItem(x, y, PatternView.SIZE, PatternView.SIZE));
			Log.i(TAG, "restoreData..." + x + ": " + y);
		}
		return arr;
	}

	@Override
	public void drawPatternError(int status) {

	}

	@Override
	public void onBackPressed() {
		if (Util.EXIT_LISNER != null)
			Util.EXIT_LISNER.exitWithoutConfirm();
		Log.d(TAG, "tran.thang onBackPressed");
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
		super.onBackPressed();
	}

	private String checkMode() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String mode = sharedPreferences.getString(Util.TYPE_MODE,
				getString(R.string.customize));
		return mode;
	}

}
