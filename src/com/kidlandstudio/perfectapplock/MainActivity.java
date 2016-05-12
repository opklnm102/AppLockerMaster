package com.kidlandstudio.perfectapplock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.samsung.slide.NavDrawerItem;
import com.android.samsung.slide.NavDrawerListAdapter;
import com.android.service.ProtectAppService;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;
import com.samsung.dialog.DialogManager;
import com.samsung.security.LockActivity;
import com.samsung.security.LockActivityByFingerprint;
import com.samsung.security.LockActivityByPassWord;
import com.samsung.security.LockActivityByPattern;
import com.samsung.security.LockActivity.CallBackLoadApp;
import com.samsung.ui.fragment.AboutFragment;
import com.samsung.ui.fragment.ApplicationsFragment;
import com.samsung.ui.fragment.AppsFragment;
import com.samsung.ui.fragment.HideImageFragment;
import com.samsung.ui.fragment.QuickModeFragment;
import com.samsung.ui.fragment.SettingFragment;

public class MainActivity extends FragmentActivity implements
		LockActivity.OnExitWithoutConfirm {
	private static final String TAG = "MainActivity";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private String[] mAplicationKidMode;
	private String[] mAplicationFriendMode;

	private ActionBar actionBar;

	/*
	 * (non-Javadoc) DialogManager for first lunch and confirm case....
	 */
	private DialogManager dialogMgr;
	private SharedPreferences preferences;

	@Override
	public void onAttachFragment(android.app.Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		Typeface tf = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "font/RobotoCondensed_Light.ttf");

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		Util.mMainActivity = this;
		Util.mContext = getBaseContext();
		// for actionbar
		moveDrawerToTop();
		initActionBar();
		// for fragment
		initFirst(savedInstanceState);
		Log.e("tuyen.px", "onCreate");

	}

	private void moveDrawerToTop() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor,
				null); // "null" is important.

		// HACK: "steal" the first child of decor view
		ViewGroup decor = (ViewGroup) getWindow().getDecorView();
		View child = decor.getChildAt(0);
		decor.removeView(child);
		LinearLayout container = (LinearLayout) drawer
				.findViewById(R.id.drawer_content); // This is the container we
													// defined just now.
		container.addView(child, 0);
		drawer.findViewById(R.id.menu_list).setPadding(0, getStatusBarHeight(),
				0, 0);

		// Make the drawer replace the first child
		decor.addView(drawer);
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("tuyen.px", "onStart");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("tuyen.px", "onStop");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("tuyen.px", "onPause");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e("tuyen.px", "onRestart");
	}

	@Override
	protected void onDestroy() {
		Util.flag_check = false;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.e("tuyen.px", "call onResume Util.flag_check " + Util.flag_check);

		startService(new Intent(getApplicationContext(),
				ProtectAppService.class));
		preferences = getSharedPreferences(Util.PREFEREN_NAME,
				Context.MODE_PRIVATE);
		String flag = preferences.getString(Util.FLAG_FIRST, "");
		Util.EXIT_LISNER = this;

		if (!flag.equals("true")) {
			dialogMgr = new DialogManager(this, DialogManager.DIALOG_SETUP);
			dialogMgr.show();
			new taskLoadFile().execute();

		} else if (!Util.flag_check) {
			if (checkTypeLock().equals(Util.TYPE_LOCK_PASS)) {
				Util.TOP_PROCESS_NEED_IGNOR = "com.kidlandstudio.perfectapplock";
				Intent ik = new Intent(this, LockActivityByPassWord.class);
				ik.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ik.putExtra(ProtectAppService.PROCESS_NAME,
						"com.kidlandstudio.perfectapplock");
				ik.putExtra("tuyen.px", "tuyen.px");
				startActivity(ik);
			} else if (checkTypeLock().equals(Util.TYPE_LOCK_PARTEN)) {
				Util.TOP_PROCESS_NEED_IGNOR = "com.kidlandstudio.perfectapplock";
				Intent ik = new Intent(this, LockActivityByPattern.class);
				ik.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ik.putExtra(ProtectAppService.PROCESS_NAME,
						"com.kidlandstudio.perfectapplock");
				ik.putExtra("tuyen.px", "tuyen.px");
				startActivity(ik);
			} else {
				Util.TOP_PROCESS_NEED_IGNOR = "com.kidlandstudio.perfectapplock";
				Intent ik = new Intent(this, LockActivityByFingerprint.class);
				ik.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ik.putExtra(ProtectAppService.PROCESS_NAME,
						"com.kidlandstudio.perfectapplock");
				ik.putExtra("tuyen.px", "tuyen.px");
				startActivity(ik);
			}
		}
	}

	public void initFirst(Bundle savedInstanceState) {
		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.menu_list);
		navDrawerItems = new ArrayList<NavDrawerItem>();
		mDrawerList.setDivider(null);
		navDrawerItems.add(null);
		// Applications locker
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Hide photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Hide text notification
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// smart locker
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// Setting
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		// About
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1)));
		// Recycle the typed array
		navMenuIcons.recycle();
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.icon_menu, // menu toggle icon
				R.string.menu_name, // drawer open - description for
				// accessibility
				R.string.app_name // drawer close - description for
		// accessibility
		) {
			public void onDrawerClosed(android.view.View drawerView) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			};

			public void onDrawerOpened(android.view.View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			};

		};
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			mDrawerList.setItemChecked(0, true);
			displayView(1);
		}
		// init database for friendmode and kidmode .
		initDatabase();
	}

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Toast.makeText(getApplicationContext(), ""+position,
			// Toast.LENGTH_SHORT).show();

			// display view for selected nav drawer item
			// adapter.resetAll(position);
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// if nav drawer is opened, hide the action items
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		super.setTitle(title);
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			break;
		case 1:
			fragment = new ApplicationsFragment();
			break;
		case 2:
			fragment = new HideImageFragment();
			break;
		case 3:
			fragment = new QuickModeFragment();
			break;
		case 4:
			fragment = new SettingFragment();
			break;
		case 5:
			fragment = new SettingFragment();
			break;
		case 6:
			fragment = new AboutFragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.main_frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			// mDrawerList.setItemChecked(position, true);
			// mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position - 1]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	private String checkTypeLock() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String typelock = sharedPreferences.getString(Util.TYPE_LOCK,
				Util.TYPE_LOCK_PASS);
		// String typelock = Util.TYPE_LOCK_FINGER;
		// String typelock = Util.TYPE_LOCK_PARTEN;
		return typelock;

	}

	@Override
	public void exitWithoutConfirm() {
		Log.e("MainActivity", "exitWithoutConfirm");
		finish();
	}

	private void initDatabase() {
		// init for tblkidmode
		Cursor c = getContentResolver().query(LockAppProvider.URI_APPKID, null,
				null, null, null);
		if (c.getCount() < 1) {
			mAplicationKidMode = getResources().getStringArray(
					R.array.Application_Kid_Mode);
			ContentValues contentValues = new ContentValues();
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[0]);
			contentValues.put(TblKidApp.APP_ALLOW, 0);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[1]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[2]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[3]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[4]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[5]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[6]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[7]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[8]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[9]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[10]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);

			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[11]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[12]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[13]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[14]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);
			contentValues.put(TblKidApp.APP_PROCESS, mAplicationKidMode[15]);
			getContentResolver().insert(LockAppProvider.URI_APPKID,
					contentValues);

		}
		c.close();
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// startActivity(new Intent(getBaseContext(), AddmodActivity.class));
		finish();
	}

	private List<ResolveInfo> mListAplicationInfo = new ArrayList<ResolveInfo>();

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
			mBackLoadApp.onloadonefirst();
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
		void onloadonefirst();
	}

}
