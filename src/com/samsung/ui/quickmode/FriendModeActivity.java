package com.samsung.ui.quickmode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.KidModeAdapter1;
import com.samsung.android.adapter.KidModeAdapter2;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblFriendApp;
import com.samsung.contentprovider.LockAppProvider;

public class FriendModeActivity extends Activity {
	private ArrayList<String> list_kid_mode = new ArrayList<String>();
	public static ListView mListApplication_kid, mListApplication_non_kid;
	private List<ResolveInfo> mListAplicationInfo;
	private List<MyApplication> mListApplicationList = new ArrayList<MyApplication>();
	private List<MyApplication> mListApplicationList_non_kid = new ArrayList<MyApplication>();
	private KidModeAdapter1 mAplicationAdapter;
	private KidModeAdapter2 mAplicationAdapter_non_default;
	private Context mContext;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_kid_mode);
		mContext = this;
		mListApplication_kid = (ListView) findViewById(R.id.listview_kidmode_default);
		mListApplication_non_kid = (ListView) findViewById(R.id.listview_kidmode_non_default);
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(R.string.friend);
		loadAplication();
	}

	private void loadKidAplication() {
		Cursor c = getContentResolver().query(LockAppProvider.URI_APPFRIEND,
				new String[] { TblFriendApp.APP_PROCESS }, null, null,
				TblFriendApp.APP_PROCESS);
		while (c.moveToNext()) {
			list_kid_mode.add(c.getString(c
					.getColumnIndexOrThrow(TblFriendApp.APP_PROCESS)));
		}
		c.close();
	}

	private void loadAplication() {
		loadKidAplication();
		mAplicationAdapter = new KidModeAdapter1(mContext,
				R.layout.aplication_item, mListApplicationList);
		mAplicationAdapter_non_default = new KidModeAdapter2(mContext,
				R.layout.aplication_item, mListApplicationList_non_kid);
		mListApplicationList.removeAll(mListApplicationList);
		mListApplicationList_non_kid.removeAll(mListApplicationList_non_kid);
		mListApplication_kid.setAdapter(mAplicationAdapter);
		mListApplication_non_kid.setAdapter(mAplicationAdapter_non_default);
		new loadKidMode().execute(); // load all the available MyApplication
										// into the mListApplicationList, and
										// mListApplicationList_non_kid

		mListApplication_kid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = getProcessFromName(((MyApplication) parent
						.getItemAtPosition(position)).getPackageName());
				getContentResolver().delete(LockAppProvider.URI_APPFRIEND,
						TblFriendApp.APP_PROCESS + "=?", new String[] { name });
				mAplicationAdapter_non_default.add((MyApplication) parent
						.getItemAtPosition(position));
				mAplicationAdapter.remove((MyApplication) parent
						.getItemAtPosition(position));
			}
		});

		mListApplication_non_kid
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ContentValues contentValues = new ContentValues();
						contentValues.put(TblFriendApp.APP_PROCESS,
								((MyApplication) parent
										.getItemAtPosition(position))
										.getPackageName());
						contentValues.put(TblFriendApp.APP_ALLOW, "0");
						getContentResolver().insert(
								LockAppProvider.URI_APPFRIEND, contentValues);
						mAplicationAdapter.add((MyApplication) parent
								.getItemAtPosition(position));
						mAplicationAdapter_non_default
								.remove((MyApplication) parent
										.getItemAtPosition(position));
					}
				});

	}

	class loadKidMode extends AsyncTask<Void, Void, Void> {
		MyApplication appInfo;

		@Override
		protected Void doInBackground(Void... arg0) {
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			mListAplicationInfo = mContext.getPackageManager()
					.queryIntentActivities(intent, 0);
			Collections.sort(
					mListAplicationInfo,
					new ResolveInfo.DisplayNameComparator(mContext
							.getPackageManager()));
			for (int i = 0; i < mListAplicationInfo.size(); i++) {

				String packageName = mListAplicationInfo.get(i).activityInfo.packageName;

				if (packageName.equals("com.kidlandstudio.perfectapplock")) {
					continue;
				}
				String applicationname = mListAplicationInfo.get(i).loadLabel(
						mContext.getPackageManager())
						+ "";
				if (checkApp(packageName)) {
					appInfo = new MyApplication(applicationname, true,
							mListAplicationInfo.get(i).loadIcon(
									mContext.getPackageManager()),
							mListAplicationInfo.get(i).activityInfo.packageName);
					mListApplicationList.add(appInfo);
				} else {
					appInfo = new MyApplication(applicationname, false,
							mListAplicationInfo.get(i).loadIcon(
									mContext.getPackageManager()),
							mListAplicationInfo.get(i).activityInfo.packageName);
					mListApplicationList_non_kid.add(appInfo);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAplicationAdapter.notifyDataSetChanged();
			mAplicationAdapter_non_default.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	private boolean checkApp(String pakageName) {
		boolean flag = false;
		for (String name : list_kid_mode) {
			if (pakageName.contains(name)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private String getProcessFromName(String pakageName) {
		String names = "";
		for (String name : list_kid_mode) {
			if (pakageName.contains(name)) {
				names = pakageName;
				break;
			}
		}
		return names;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_friend_mode, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_default_friend:
			resetDefault();
			loadAplication();
			break;
		case android.R.id.home:
			finish();
			
		default:
			break;
		}
		return true;
	}

	// function to call reset to default
	private void resetDefault() {
	    list_kid_mode.removeAll(list_kid_mode);
		getContentResolver().delete(LockAppProvider.URI_APPFRIEND, null, null);
		// init for tblkidmode
		String[] mAplicationFriendMode;
		// init for tblfriendmode
		Cursor d = getContentResolver().query(LockAppProvider.URI_APPFRIEND,
				null, null, null, null);
		if (d.getCount() < 1) {
			mAplicationFriendMode = getResources().getStringArray(
					R.array.Aplication_Friend_Mode);
			ContentValues contentValues = new ContentValues();
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[0]);
			contentValues.put(TblFriendApp.APP_ALLOW, 0);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[1]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[2]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[3]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[4]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[5]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[6]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[7]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[8]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[9]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[10]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[11]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[12]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[13]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[14]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[15]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[16]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);
			contentValues.put(TblFriendApp.APP_PROCESS,
					mAplicationFriendMode[17]);
			getContentResolver().insert(LockAppProvider.URI_APPFRIEND,
					contentValues);

		}
		d.close();

	}
}