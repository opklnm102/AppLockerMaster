package com.samsung.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.util.Util;
import com.samsung.security.PolicyManager;
import com.samsung.security.SecurityTypeActivity;

public class SettingFragment extends Fragment implements OnTouchListener,
		OnClickListener {
	private Context mContext;
	private String TAG = "SettingFragment";
	private LinearLayout cmChangeType, cmTimeLock, cmThemeLock, cmUninstall;
	private TextView txtDescripType, txtDescripUninstall;
	private Dialog dialog;
	private RadioButton btnActivate, btnDeactivate;
	private PolicyManager policyManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_setting,
				container, false);

		cmChangeType = (LinearLayout) rootView.findViewById(R.id.cmChangeType);
		cmTimeLock = (LinearLayout) rootView.findViewById(R.id.cmTimeLock);
		cmThemeLock = (LinearLayout) rootView.findViewById(R.id.cmTheme);
		cmUninstall = (LinearLayout) rootView.findViewById(R.id.cmUninstall);
		txtDescripType = (TextView) rootView.findViewById(R.id.txtDescripType);
		txtDescripUninstall = (TextView) rootView
				.findViewById(R.id.txtDescripUninstall);
		cmChangeType.setOnTouchListener(this);
		cmTimeLock.setOnTouchListener(this);
		cmThemeLock.setOnTouchListener(this);
		cmUninstall.setOnTouchListener(this);
		mContext = getActivity();

		policyManager = new PolicyManager(mContext);
		initDialog();
		return rootView;
	}

	private void initDialog() {
		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.check_uninstall);
		btnActivate = (RadioButton) dialog.findViewById(R.id.btnActivate);
		btnDeactivate = (RadioButton) dialog.findViewById(R.id.btnDeActivate);
		btnActivate.setOnClickListener(this);
		btnDeactivate.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "tran.thang onResume()");
		SharedPreferences preferences = mContext.getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String typeSecurity = preferences.getString(Util.TYPE_LOCK, "");
		String uninstallMode = preferences.getString(Util.UNINSTALL_MODE, "");
		if (typeSecurity.equals("")) {
			Log.e(TAG, "tran.thang Setup security is failed");
		} else {
			txtDescripType.setText(typeSecurity.split("_")[2]);
		}
		if (uninstallMode.equals("")) {
			Log.e(TAG, "tran.thang Setup uninstall mode is failed");
			deActivateUninstall();
		} else {
			txtDescripUninstall.setText(uninstallMode);
			String state = mContext.getResources().getString(
					R.string.title_activate_uninstall);
			if (uninstallMode.equals(state)) {
				btnActivate.setChecked(true);
				btnDeactivate.setChecked(false);
			} else {
				btnActivate.setChecked(false);
				btnDeactivate.setChecked(true);
			}
		}
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Log.i(TAG, "onTouch...");
		switch (view.getId()) {
		case R.id.cmChangeType:
			changeLayoutBG(cmChangeType, event.getAction(), R.id.cmChangeType);
			break;
		case R.id.cmUninstall:
			changeLayoutBG(cmUninstall, event.getAction(), R.id.cmUninstall);
			break;
		case R.id.cmTimeLock:
			Toast.makeText(mContext, getString(R.string.annouce_not_support),
					Toast.LENGTH_SHORT).show();
			// changeLayoutBG(cmTimeLock, event.getAction(), R.id.cmTimeLock);
			break;
		case R.id.cmTheme:
			// changeLayoutBG(cmThemeLock, event.getAction(), R.id.cmTheme);
			Toast.makeText(mContext, getString(R.string.annouce_not_support),
					Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		return true;
	}

	public void showSettingActivity(int id) {
		// call to confirmed dialog like you setting before
		// temporary case: go to setting layout
		Intent intent = null;
		switch (id) {
		case R.id.cmChangeType:
			intent = new Intent(mContext, SecurityTypeActivity.class);
			break;
		case R.id.cmUninstall:
			dialog.show();
			break;
		case R.id.cmTimeLock:
			// ------------------
			break;
		case R.id.cmTheme:
			// -------------------
			break;
		default:
			break;
		}
		if (intent != null) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
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
	public void onClick(View arg0) {
		Log.d(TAG, "tran.thang onClick id = " + arg0.getId());
		switch (arg0.getId()) {
		case R.id.btnActivate:
			btnDeactivate.setChecked(false);
			activateUninstall();
			break;
		case R.id.btnDeActivate:
			deActivateUninstall();
			break;
		default:
			break;
		}
	}

	private void activateUninstall() {
		Log.d(TAG, "tran.thang activateUninstall");
		// enable
		if (!policyManager.isAdminActive()) {
			Intent activateDeviceAdmin = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			activateDeviceAdmin.putExtra(
					DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					policyManager.getAdminComponent());
			activateDeviceAdmin
					.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"After activating admin, you will be able to block application uninstallation.");
			Log.d(TAG, "tran.thang startActivityForResult activateDeviceAdmin");
			startActivityForResult(activateDeviceAdmin,
					PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
		}
	}

	private void deActivateUninstall() {
		Log.d(TAG, "tran.thang deActivateUninstall");
		// disable
		if (policyManager.isAdminActive()) {
			Log.d(TAG, "tran.thang disableAdmin");
			policyManager.disableAdmin();

			// change state disable
			SharedPreferences preferences = mContext.getSharedPreferences(
					Util.PREFEREN_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			String state = mContext.getResources().getString(
					R.string.title_deactivate_uninstall);
			editor.putString(Util.UNINSTALL_MODE, state);
			editor.commit();
			txtDescripUninstall.setText(mContext.getResources().getString(
					R.string.title_deactivate_uninstall));
			dialog.dismiss();
			btnActivate.setChecked(false);
			btnDeactivate.setChecked(true);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "tran.thang onActivityResult requestCode = " + requestCode
				+ ", resultCode = " + resultCode);
		if (resultCode == Activity.RESULT_OK
				&& requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {
			// change state enable
			SharedPreferences preferences = mContext.getSharedPreferences(
					Util.PREFEREN_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			String state = mContext.getResources().getString(
					R.string.title_activate_uninstall);
			editor.putString(Util.UNINSTALL_MODE, state);
			editor.commit();
			txtDescripUninstall.setText(state);
			dialog.dismiss();
			btnActivate.setChecked(true);
			btnDeactivate.setChecked(false);
		} else {
			btnActivate.setChecked(false);
			btnDeactivate.setChecked(true);
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}