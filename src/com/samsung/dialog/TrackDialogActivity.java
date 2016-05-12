package com.samsung.dialog;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;

public class TrackDialogActivity extends Activity {

	protected static final String TAG = "TrackDialogActivity";
	Button okButton, cancelButton;
	private String pkg_name = "";
	private String appName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.track_dialog_activity);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}

		pkg_name = extras.getString("package_name");
		// Get application name
		final PackageManager pm = getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(pkg_name, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		appName = (String) (ai != null ? pm.getApplicationLabel(ai)
				: "(unknown)");

		Log.d(TAG, "tran.thang onCreate package name = " + pkg_name
				+ " and appName = " + appName);
		okButton = (Button) findViewById(R.id.ok_btn);
		okButton.setOnClickListener(clickListenter);
		okButton = (Button) findViewById(R.id.cancel_btn);
		okButton.setOnClickListener(clickListenter);
	}

	private OnClickListener clickListenter = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Log.d(TAG, "tran.thang onClick id = " + view.getId());
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.ok_btn:
				Log.d(TAG, "tran.thang OK");
				update(pkg_name);
				ContentValues contentValues = new ContentValues();
				contentValues.put(TblAppLocked.APP_PROCESS, pkg_name);
				contentValues.put(TblAppLocked.APP_ALLOW, 0);
				getContentResolver().insert(LockAppProvider.URI_APPLOCK,
						contentValues);
				Toast.makeText(getApplicationContext(),
						appName + " was add in Protect App", Toast.LENGTH_SHORT)
						.show();
				finish();
				break;
			case R.id.cancel_btn:
				Log.d(TAG, "tran.thang CANCEL");
				finish();
				break;
			default:
				break;
			}
		}
	};

	private void update(String pkgName) {
		final PackageManager pm = getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (ai != null) {
			MyApplication application = new MyApplication(
					(String) pm.getApplicationLabel(ai), true,
					pm.getApplicationIcon(ai), pkgName);
			MyApplication applicationdel = new MyApplication(
					(String) pm.getApplicationLabel(ai), false,
					pm.getApplicationIcon(ai), pkgName);
			if (Util.mListApplicationList.size() > 2) {
				Util.mListApplicationList.add(application);
				Util.mListApplicationList.remove(applicationdel);
			}
		}
	}
}
