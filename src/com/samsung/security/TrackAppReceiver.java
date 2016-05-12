package com.samsung.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
// import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.service.ProtectAppService;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;
import com.samsung.dialog.TrackDialogActivity;

public class TrackAppReceiver extends BroadcastReceiver {

	private static final String TAG = "LockAppBootReceiver";
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.d(TAG, "tran.thang onReceive action = " + action);
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			context.startService(new Intent(context, ProtectAppService.class));
		} else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
			Uri data = intent.getData();
			String pkgname = data.getEncodedSchemeSpecificPart();
			addApp(pkgname);
			Intent ik = new Intent();
			ik.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ik.putExtra("package_name", pkgname);
			ik.setClass(context, TrackDialogActivity.class);
			context.startActivity(ik);
		} else if (action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
			Uri data = intent.getData();
			String pkgName = data.getEncodedSchemeSpecificPart();
			Toast.makeText(context, "ACTION_PACKAGE_CHANGED" + pkgName,
					Toast.LENGTH_LONG).show();
		} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
			Uri data = intent.getData();
			String pkgName = data.getEncodedSchemeSpecificPart();
			deleteApp(pkgName);
			Toast.makeText(context, "ACTION_PACKAGE_REMOVED" + pkgName,
					Toast.LENGTH_LONG).show();
		}
	}

	private void addApp(String pkgName) {
		Log.e(TAG, "tuyenrcs : add app : " + pkgName);
		final PackageManager pm = mContext.getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (ai != null) {
			MyApplication application = new MyApplication(
					(String) pm.getApplicationLabel(ai), false,
					pm.getApplicationIcon(ai), pkgName);
			if (Util.mListApplicationList.size() > 2) {
				Util.mListApplicationList.add(application);
			}
		}
	}

	private void deleteApp(String pkgName) {
		Log.e(TAG, "tuyenrcs : del app : " + pkgName);
		MyApplication application = null;
		for (MyApplication appdel : Util.mListApplicationList) {
			if (appdel.getPackageName().equals(pkgName)) {
				application = appdel;
				break;
			}
		}
		if (application != null) {
			Util.mListApplicationList.remove(application);
		}
		mContext.getContentResolver().delete(LockAppProvider.URI_APPLOCK, TblAppLocked.APP_PROCESS +"=?", new String[]{pkgName});
	}

}
