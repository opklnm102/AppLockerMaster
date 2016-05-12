package com.samsung.security;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.service.ProtectAppService;
import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;

public class LockActivityByPassWord extends LockActivity implements
		OnClickListener, OnLongClickListener {
	private static final String TAG = "LockActivityByPassWord";
	// Layout of unlock
	private ImageView mImageViewIcon;
	private EditText mPassword;
	private Button mNumber0;
	private Button mNumber1;
	private Button mNumber2;
	private Button mNumber3;
	private Button mNumber4;
	private Button mNumber5;
	private Button mNumber6;
	private Button mNumber7;
	private Button mNumber8;
	private Button mNumber9;
	private Button mNumberok;
	private Button mNumberdel;
	private String mPasswd = "";
	private TextView checkInput;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		String mProcessName = getIntent().getStringExtra(
				ProtectAppService.PROCESS_NAME);
		setContentView(R.layout.confirmlock);
		mImageViewIcon = (ImageView) findViewById(R.id.passicon);
		//setIcon(mProcessName, mImageViewIcon);

		//mPassword = (EditText) findViewById(R.id.passtxtPassword);
		checkInput = (TextView) findViewById(R.id.confirmlock_tv_show);
		mNumber0 = (Button) findViewById(R.id.passbtnNum0);
		mNumber1 = (Button) findViewById(R.id.passbtnNum1);
		mNumber2 = (Button) findViewById(R.id.passbtnNum2);
		mNumber3 = (Button) findViewById(R.id.passbtnNum3);
		mNumber4 = (Button) findViewById(R.id.passbtnNum4);
		mNumber5 = (Button) findViewById(R.id.passbtnNum5);
		mNumber6 = (Button) findViewById(R.id.passbtnNum6);
		mNumber7 = (Button) findViewById(R.id.passbtnNum7);
		mNumber8 = (Button) findViewById(R.id.passbtnNum8);
		mNumber9 = (Button) findViewById(R.id.passbtnNum9);
		mNumberok = (Button) findViewById(R.id.passbtnNumOK);
		mNumberdel = (Button) findViewById(R.id.passbtnNumDel);
		//mPassword.setOnClickListener(this);
		mNumberdel.setOnLongClickListener(this);
		mNumber0.setOnClickListener(this);
		mNumber1.setOnClickListener(this);
		mNumber2.setOnClickListener(this);
		mNumber3.setOnClickListener(this);
		mNumber4.setOnClickListener(this);
		mNumber5.setOnClickListener(this);
		mNumber6.setOnClickListener(this);
		mNumber7.setOnClickListener(this);
		mNumber8.setOnClickListener(this);
		mNumber9.setOnClickListener(this);
		mNumberok.setOnClickListener(this);
		mNumberdel.setOnClickListener(this);
	}

	public void addSymbol(String symbol) {
		mPasswd = mPasswd + symbol;
		checkInput.append("O ");
		//mPassword.setText(mPasswd);
		if (mPasswd.length() == 4) {
			checkOK(this);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.passbtnNum0:
		case R.id.passbtnNum1:
		case R.id.passbtnNum2:
		case R.id.passbtnNum3:
		case R.id.passbtnNum4:
		case R.id.passbtnNum5:
		case R.id.passbtnNum6:
		case R.id.passbtnNum7:
		case R.id.passbtnNum8:
		case R.id.passbtnNum9:
			addSymbol(((Button) view).getText() + "");
			break;
		case R.id.passbtnNumOK:
			checkOK(this);
			break;
		case R.id.passbtnNumDel:
			deleteSymbol();
			break;
		default:
			break;
		}
	}

	public void deleteSymbol() {
		if (mPasswd.length() <= 1) {
			mPasswd = "";
			//mPassword.setText(mPasswd);
			checkInput.setText("");
		} else {
			mPasswd = mPasswd.substring(0, mPasswd.length() - 1);
			//mPassword.setText(mPasswd);
			checkInput.setText(checkInput.getText().toString().substring(0, checkInput.length()-2));
		}
	}

	private void checkOK(LockActivityByPassWord lockActivityByPassWord) {
		SharedPreferences sharedPreferences = lockActivityByPassWord
				.getSharedPreferences(Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		if (mPasswd.equals(sharedPreferences
				.getString(Util.PASS_WORD_VALUE, ""))) {
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
			// Util.flag_check = true;
			try {
				String s = getIntent().getStringExtra("tuyen.px");
				if (s.equals("tuyen.px"))
					Util.flag_check = true;
			} catch (Exception e) {
			}
			finish();
		} else {
			mPasswd = "";
			//mPassword.setText(mPasswd);
			checkInput.setText("");
			Toast.makeText(getBaseContext(),
					getString(R.string.annouce_wrong_password),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.btnNumDel:
			mPasswd = "";
			//mPassword.setText(mPasswd);
			break;
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		if (Util.EXIT_LISNER != null)
			Util.EXIT_LISNER.exitWithoutConfirm();
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
