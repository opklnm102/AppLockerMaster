package com.samsung.android.util;

import java.util.ArrayList;
import java.util.List;

import com.kidlandstudio.perfectapplock.MainActivity;
import com.samsung.android.adapter.MyApplication;
import com.samsung.security.LockActivity;

import android.content.Context;

public class Util {
	public static String TOP_PROCESS_NEED_IGNOR = "";
	public static final String PREFEREN_NAME = "preference_values";
	public static final String PASS_WORD_VALUE = "password_value";
	public static final String FLAG_FIRST = "flag_first";
	public static final String TYPE_LOCK = "typelock";
	public static final String TYPE_LOCK_PASS = "type_lock_Password";
	public static final String TYPE_LOCK_PARTEN = "type_lock_Pattern";
	public static final String TYPE_LOCK_FINGER = "type_lock_FingerPrint";
	public static final String KEY_CONFIRM_EXIT = "key_confirm_exit";
	public static final String KEY_EXIT_WITHOUT_CONFIRM = "key_exit_without_confirm";
	public static final String TYPE_MODE = "typemode";
	public static final String UNINSTALL_MODE = "uninstall_mode";

	public static LockActivity.OnExitWithoutConfirm EXIT_LISNER;

	public static Context mContext;
	public static List<MyApplication> mListApplicationList = new ArrayList<MyApplication>();
	public static MainActivity mMainActivity;

	public static boolean flag_check = false;
	
	public Util() {
		// TODO Auto-generated constructor stub
	}
}
