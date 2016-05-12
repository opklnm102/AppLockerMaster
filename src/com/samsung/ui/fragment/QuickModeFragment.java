package com.samsung.ui.fragment;

import android.app.Dialog;
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

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.util.Util;
import com.samsung.ui.quickmode.FriendModeActivity;
import com.samsung.ui.quickmode.KidModeActivity;

public class QuickModeFragment extends Fragment implements OnTouchListener,
		OnClickListener {
	private static final int KID_MODE = 1;
	private static final int FRIEND_MODE = 2;
	private static final int CUSTOMIZE_MODE = 3;
	private Context mContext;
	private String TAG = "QuickModeFragment";
	private LinearLayout cmKidMode, cmFriendMode, cmCustomizeMode,
			cmChangeSelect;
	private TextView txtDescripType, txtKidMode, txtFriendMode;
	private Dialog dialog;
	private RadioButton btnKidMode, btnFriendMode, btnCustomizeMode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_quick_mode,
				container, false);

		cmKidMode = (LinearLayout) rootView.findViewById(R.id.cmKidMode);
		cmFriendMode = (LinearLayout) rootView.findViewById(R.id.cmFriendMode);
		cmCustomizeMode = (LinearLayout) rootView
				.findViewById(R.id.cmCustomizeMode);
		cmChangeSelect = (LinearLayout) rootView
				.findViewById(R.id.cmChangeSelect);

		txtDescripType = (TextView) rootView
				.findViewById(R.id.txtDescripSelect);
		txtKidMode = (TextView) rootView.findViewById(R.id.txtKidMode);
		txtFriendMode = (TextView) rootView.findViewById(R.id.txtFriendMode);

		cmKidMode.setOnTouchListener(this);
		cmFriendMode.setOnTouchListener(this);
		cmCustomizeMode.setOnTouchListener(this);
		cmChangeSelect.setOnTouchListener(this);
		mContext = getActivity();

		initDialog();
		return rootView;
	}

	private void initDialog() {
		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.select_mode);
		btnKidMode = (RadioButton) dialog.findViewById(R.id.btnKidMode);
		btnFriendMode = (RadioButton) dialog.findViewById(R.id.btnFriendMode);
		btnCustomizeMode = (RadioButton) dialog
				.findViewById(R.id.btnCustomizeMode);
		btnFriendMode.setOnClickListener(this);
		btnCustomizeMode.setOnClickListener(this);
		btnKidMode.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		SharedPreferences preferences = mContext.getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String typeMode = preferences.getString(Util.TYPE_MODE,
				mContext.getString(R.string.customize));
		if (typeMode.equals("")) {
			Log.e(TAG, "Setup mode is failed");
			btnCustomizeMode.setChecked(true);
		} else {
			txtDescripType.setText(typeMode);
			if (typeMode.equals(mContext.getString(R.string.kid))) {
				// btnKidMode.setChecked(true);
				setKidMode();
				setQuickModeAbility(KID_MODE);
			} else if (typeMode.equals(mContext.getString(R.string.friend))) {
				setFriendMode();
				setQuickModeAbility(FRIEND_MODE);
			} else if (typeMode.equals(mContext.getString(R.string.customize))) {
				setNomarlyMode();
				setQuickModeAbility(CUSTOMIZE_MODE);
			}
		}
		super.onResume();
	}

	private void setQuickModeAbility(int mode) {
		switch (mode) {
		case KID_MODE:
			cmKidMode.setEnabled(true);
			cmFriendMode.setEnabled(false);
			cmCustomizeMode.setEnabled(false);
			txtKidMode.setTextColor(Color.BLACK);
			txtFriendMode.setTextColor((mContext.getResources()
					.getColor(R.color.color_desciption)));
			break;
		case FRIEND_MODE:
			cmKidMode.setEnabled(false);
			cmFriendMode.setEnabled(true);
			cmCustomizeMode.setEnabled(false);
			txtKidMode.setTextColor((mContext.getResources()
					.getColor(R.color.color_desciption)));
			txtFriendMode.setTextColor(Color.BLACK);
			break;
		case CUSTOMIZE_MODE:
			cmKidMode.setEnabled(false);
			cmFriendMode.setEnabled(false);
			cmCustomizeMode.setEnabled(true);
			txtKidMode.setTextColor((mContext.getResources()
					.getColor(R.color.color_desciption)));
			txtFriendMode.setTextColor((mContext.getResources()
					.getColor(R.color.color_desciption)));
			break;

		default: // default is Kid_Mode
			cmKidMode.setEnabled(true);
			cmFriendMode.setEnabled(false);
			cmCustomizeMode.setEnabled(false);
			cmCustomizeMode.setEnabled(false);
			txtKidMode.setTextColor((mContext.getResources()
					.getColor(R.color.list_background)));
			txtFriendMode.setTextColor((mContext.getResources()
					.getColor(R.color.color_desciption)));
			break;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Log.i(TAG, "onTouch...");
		switch (view.getId()) {
		case R.id.cmChangeSelect:
			changeLayoutBG(cmChangeSelect, event.getAction(),
					R.id.cmChangeSelect);
			break;
		case R.id.cmKidMode:
			changeLayoutBG(cmKidMode, event.getAction(), R.id.cmKidMode);
			break;
		case R.id.cmFriendMode:
			// Toast.makeText(mContext,
			// "This feature is not supported! Please update pro version on market!",
			// Toast.LENGTH_SHORT).show();
			changeLayoutBG(cmFriendMode, event.getAction(), R.id.cmFriendMode);
			break;
		case R.id.cmCustomizeMode:
			changeLayoutBG(cmCustomizeMode, event.getAction(),
					R.id.cmCustomizeMode);
			// Toast.makeText(mContext,
			// "This feature is not supported! Please update pro version on market!",
			// Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		return true;
	}

	private void showSettingActivityOrDialog(int id) {
		Intent intent = null;
		switch (id) {
		case R.id.cmChangeSelect:
			if (dialog != null)
				dialog.show();
			break;
		case R.id.cmKidMode:
			// -------------------
			intent = new Intent(mContext, KidModeActivity.class);
			break;
		case R.id.cmFriendMode:
			// ------------------
			intent = new Intent(mContext, FriendModeActivity.class);
			break;
		case R.id.cmTheme:
			// -------------------
			intent = new Intent(mContext, KidModeActivity.class);
			break;
		default:
			break;
		}
		if (intent != null) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}

	}

	private void changeLayoutBG(LinearLayout item, int state, int id) {
		if (state == MotionEvent.ACTION_DOWN) {
			item.setBackgroundColor(mContext.getResources().getColor(
					R.color.color_desciption_press));
			showSettingActivityOrDialog(id);
		} else if (state == MotionEvent.ACTION_UP) {
			item.setBackgroundColor(Color.WHITE);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnKidMode:
			setKidMode();
			setQuickModeAbility(KID_MODE);
			saveSelectedMode(R.id.btnKidMode);
			break;
		case R.id.btnFriendMode:
			setFriendMode();
			setQuickModeAbility(FRIEND_MODE);
			saveSelectedMode(R.id.btnFriendMode);
			break;
		case R.id.btnCustomizeMode:
			setNomarlyMode();
			setQuickModeAbility(CUSTOMIZE_MODE);
			saveSelectedMode(R.id.btnCustomizeMode);
			break;

		default:
			break;
		}
	}

	private void saveSelectedMode(int id) {
		String currentMode = "";
		switch (id) {
		case R.id.btnKidMode:
			currentMode = mContext.getString(R.string.kid);
			break;
		case R.id.btnFriendMode:
			currentMode = mContext.getString(R.string.friend);
			// -------------------
			break;
		case R.id.btnCustomizeMode:
			currentMode = mContext.getString(R.string.customize);
			// ------------------
			break;
		default:
			break;
		}
		if (!currentMode.equals("")) {
			txtDescripType.setText(currentMode);
			SharedPreferences preferences = mContext.getSharedPreferences(
					Util.PREFEREN_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString(Util.TYPE_MODE, currentMode);
			editor.commit();
		}
		dialog.dismiss();
	}

	private void setKidMode() {
		btnKidMode.setChecked(true);
		btnFriendMode.setChecked(false);
		btnCustomizeMode.setChecked(false);
	}

	private void setFriendMode() {
		btnKidMode.setChecked(false);
		btnFriendMode.setChecked(true);
		btnCustomizeMode.setChecked(false);
	}

	private void setNomarlyMode() {
		btnKidMode.setChecked(false);
		btnFriendMode.setChecked(false);
		btnCustomizeMode.setChecked(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
