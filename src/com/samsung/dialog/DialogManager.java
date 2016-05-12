package com.samsung.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.service.ProtectAppService;
import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.util.Util;
import com.samsung.security.SecurityTypeActivity;

public class DialogManager extends Dialog implements
		android.view.View.OnClickListener, OnLongClickListener {

	public static final int DIALOG_SETUP = 1;
	public static final int DIALOG_CONFIRM = 2;
	public static final int DIALOG_LOADING = 3;
	private static final String TAG = "DialogManager";
	// Layout of unlock
	// private EditText mPassword;
	// private EditText mConfirm;
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
	private boolean mPassworkFocus = true;
	private String mPasswd = "";
	private String mConfirmpwd = "";
	private boolean confirmExit = false;

	private TextView tvStatus;
	private TextView tvCheckInput;
	private boolean isConfirmPIN; // false=input PIN/ true = confirm PIN
	// layout of lock Activity
	private Activity mContext;

	public DialogManager(Activity context, int type) {
		super(context, android.R.style.Theme_DeviceDefault_Light);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = context;
		switch (type) {
		case DIALOG_SETUP:
			Log.d(TAG, "tuyen.px : DIALOG_SETUP");
			setContentView(R.layout.confirmlock);
			// mPassword = (EditText) findViewById(R.id.txtPassword);
			// mConfirm = (EditText) findViewById(R.id.txtConfirmPw);
			tvStatus = (TextView) findViewById(R.id.confirmlock_tv_status);
			tvCheckInput = (TextView) findViewById(R.id.confirmlock_tv_show);
			isConfirmPIN = false;
			updateStatus();

			// setIconFocus(mPassworkFocus);
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
			// mPassword.setOnClickListener(this);
			// mConfirm.setOnClickListener(this);
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

			break;
		case DIALOG_CONFIRM:
			setContentView(R.layout.confirmlock);
			break;
		case DIALOG_LOADING:
			break;
		default:
			break;
		}
	}

	public DialogManager(Activity context, int type, boolean confirmExit) {
		this(context, type);
		this.confirmExit = confirmExit;
	}

	public void addSymbol(String symbol) {
		Log.i(TAG, "addSymbol " + symbol);
		tvCheckInput.append("O ");

		if (!isConfirmPIN) {
			mPasswd = mPasswd + symbol;
			// mPassword.setText(mPasswd);
			if (mPasswd.length() == 4) {
				isConfirmPIN = true;
				tvCheckInput.setText("");
				updateStatus();
			}

		} else {
			tvStatus.setText("Xác nhận lại mã PIN");
			mConfirmpwd = mConfirmpwd + symbol;
			// mConfirm.setText(mConfirmpwd);
			if (mConfirmpwd.length() == 4) {
				// state = true;
				checkOK(this);
			}
		}
	}

	public void updateStatus() {
		tvStatus.setText(!isConfirmPIN ? R.string.DialogManager_selectPIN
				: R.string.DialogManager_confirmPIN);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		Log.i(TAG, "id:  " + id);
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
		// case R.id.txtPassword:
		// mPassworkFocus = true;
		// setIconFocus(mPassworkFocus);
		// break;
		// case R.id.txtConfirmPw:
		// mPassworkFocus = false;
		// setIconFocus(mPassworkFocus);
		// break;
		default:
			break;
		}

	}

	// private void setIconFocus(boolean focus) {
	// if (focus) {
	// mPassword
	// .setBackgroundResource(R.drawable.background_focus_editext);
	// mConfirm.setBackgroundResource(R.drawable.background_editext);
	// } else {
	// mConfirm.setBackgroundResource(R.drawable.background_focus_editext);
	// mPassword.setBackgroundResource(R.drawable.background_editext);
	//
	// }
	//
	// }

	private void deleteSymbol() {
		if (!isConfirmPIN) {
			if (mPasswd.length() <= 1) {
				mPasswd = "";
				// mPassword.setText(mPasswd);
				tvCheckInput.setText("");
			} else {
				mPasswd = mPasswd.substring(0, mPasswd.length() - 1);
				// mPassword.setText(mPasswd);
				tvCheckInput.setText(tvCheckInput.getText().toString()
						.substring(0, tvCheckInput.length() - 2));
			}

		} else {
			if (mConfirmpwd.length() <= 1) {
				mConfirmpwd = "";
				// mConfirm.setText(mConfirmpwd);
				tvCheckInput.setText("");

			} else {
				mConfirmpwd = mConfirmpwd
						.substring(0, mConfirmpwd.length() - 1);
				// mConfirm.setText(mConfirmpwd);
				tvCheckInput.setText(tvCheckInput.getText().toString()
						.substring(0, tvCheckInput.length() - 2));
			}
		}
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "tran.thang onBackPressed ");

		if (!confirmExit) {
			SharedPreferences preferences = mContext.getSharedPreferences(
					Util.PREFEREN_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = preferences.edit();
			prefEditor.putString(Util.FLAG_FIRST, "");
			prefEditor.commit();
			Intent homeIntent = new Intent(Intent.ACTION_MAIN);
			homeIntent.addCategory(Intent.CATEGORY_HOME);
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(homeIntent);
		} else {
			super.onBackPressed();
		}
		// mContext.finish();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		Log.d(TAG, "tran.thang cancel ");
		super.cancel();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		Log.d(TAG, "tuyen.px dismiss ");
		super.dismiss();
	}

	private void checkOK(DialogManager dialogManager) {

		if (mPasswd.equals(mConfirmpwd)) {
			if (mPasswd.equals("")) {
				Toast.makeText(mContext,
						mContext.getString(R.string.annouce_input_password),
						Toast.LENGTH_SHORT).show();
			} else {
				SharedPreferences sharedPreferences = getContext()
						.getSharedPreferences(Util.PREFEREN_NAME,
								Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putString(Util.PASS_WORD_VALUE, mPasswd);
				editor.putString(Util.FLAG_FIRST, "true");
				editor.putString(Util.TYPE_LOCK, Util.TYPE_LOCK_PASS);
				editor.commit();
				mContext.startService(new Intent(mContext,
						ProtectAppService.class));

				// return to SecurityTypeActivity activity
				if (confirmExit)
					((SecurityTypeActivity) mContext).finish();
				dialogManager.dismiss();
			}
		} else {
			Toast.makeText(mContext,
					mContext.getString(R.string.annouce_wrong_password),
					Toast.LENGTH_SHORT).show();
			mPasswd = "";
			mConfirmpwd = "";
			mPassworkFocus = true;
			// mPassword.setText(mPasswd);
			// mConfirm.setText(mConfirmpwd);
			isConfirmPIN = false;
			tvCheckInput.setText("");
			updateStatus();
		}

	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
