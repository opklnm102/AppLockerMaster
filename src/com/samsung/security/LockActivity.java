package com.samsung.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;
import com.samsung.ui.fragment.AppsFragment;

public class LockActivity extends Activity {

	public static final int LOCK_RESUL_OK = 103;
	public static final int LOCK_RESUL_CANCEL = 104;
	private static final String TAG = "LockActivity";
	public OnExitWithoutConfirm exitListener;
	private List<ResolveInfo> mListAplicationInfo = new ArrayList<ResolveInfo>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		new taskLoadFile().execute();
	}

	public void setSecureResult(boolean result) {
		if (result) {
			setResult(LOCK_RESUL_OK);
		} else {
			setResult(LOCK_RESUL_CANCEL);
		}
	}

	public void setIcon(String name, ImageView mImageViewIcon) {
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> mListAplicationInfo = getBaseContext()
				.getPackageManager().queryIntentActivities(intent, 0);
		for (ResolveInfo resolveInfo : mListAplicationInfo) {
			if (resolveInfo.activityInfo.packageName.equals(name)
					|| resolveInfo.activityInfo.processName.equals(name)) {
				mImageViewIcon.setImageDrawable(resolveInfo
						.loadIcon(getBaseContext().getPackageManager()));
			}
		}
	}

	public static interface OnExitWithoutConfirm {
		public void exitWithoutConfirm();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}

	class taskLoadFile extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (Util.mListApplicationList.size() > 0) {
				return null;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			mListAplicationInfo = getPackageManager().queryIntentActivities(
					intent, 0);
			Collections.sort(mListAplicationInfo,
					new ResolveInfo.DisplayNameComparator(getBaseContext()
							.getPackageManager()));
			if (Util.mListApplicationList.size() != (mListAplicationInfo.size() - 1)) {
				// update list if need .
				Util.mListApplicationList.removeAll(Util.mListApplicationList);
				for (int i = 0; i < mListAplicationInfo.size(); i++) {

					if (mListAplicationInfo.get(i).activityInfo.packageName
							.equals("com.kidlandstudio.perfectapplock")) {
						continue;
					}
					String applicationname = mListAplicationInfo.get(i)
							.loadLabel(getPackageManager()) + "";
					MyApplication appInfo = new MyApplication(
							applicationname,
							checkApp(mListAplicationInfo.get(i).activityInfo.packageName),
							mListAplicationInfo.get(i).loadIcon(
									getPackageManager()), mListAplicationInfo
									.get(i).activityInfo.packageName);
					Util.mListApplicationList.add(appInfo);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mBackLoadApp = new AppsFragment();
			mBackLoadApp.onloadone();
		}

		private CallBackLoadApp mBackLoadApp;

		private boolean checkApp(String name) {
			Cursor c = getContentResolver().query(LockAppProvider.URI_APPLOCK,
					new String[] { TblAppLocked._ID },
					TblAppLocked.APP_PROCESS + "=?", new String[] { name },
					null);
			int n = c.getCount();
			c.close();
			if (n > 0) {
				return true;
			} else {
				return false;
			}
		}

	}

	public interface CallBackLoadApp {
		void onloadone();
	}

}
