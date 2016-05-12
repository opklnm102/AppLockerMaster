package com.samsung.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.AplicationLockedAdapter;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;
import com.samsung.ui.fragment.ApplicationsFragment.TabChangeListener;

public class ProtectedFragment extends Fragment implements
		OnItemLongClickListener, TabChangeListener {
	private static ListView mListApplication;
	private List<MyApplication> mListApplicationList = new ArrayList<MyApplication>();
	private Context mContext;
	private static AplicationLockedAdapter mAplicationAdapter;
	private String TAG = "ProtectedFragment";
	public static boolean checkShow = false;
	private AplicationLockedAdapter.ViewHolder holder;
	private MenuItem actionDel, selectAll;
	private boolean stateCheck = false;
	private Drawable checkDrawable, unCheckDrawable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity().getApplicationContext();
		Log.i(TAG, "onCreateView...");
		final View rootView = inflater.inflate(
				R.layout.application_fragment_layout, container, false);
		mListApplication = (ListView) rootView
				.findViewById(R.id.list_application);
		mListApplication.setOnItemLongClickListener(this);
		setHasOptionsMenu(true);
		loadAplication();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		checkDrawable = mContext.getResources()
				.getDrawable(R.drawable.cb_check);
		unCheckDrawable = mContext.getResources().getDrawable(
				R.drawable.cb_uncheck);
		unSelectAll();
		stateCheck = false;
	}

	private void loadAplication() {
		Log.e(TAG,
				"daload app mListApplicationList = "
						+ mListApplicationList.size());
		mAplicationAdapter = new AplicationLockedAdapter(mContext,
				R.layout.aplication_item_locked, mListApplicationList);
		mListApplication.setAdapter(mAplicationAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_locked_app, menu);

		actionDel = menu.findItem(R.id.action_delete);
		selectAll = menu.findItem(R.id.action_select_all);
		actionDel.setVisible(false);
		selectAll.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			unLockApps();

			break;
		case R.id.action_select_all:
			if (!stateCheck) {
				selectAll.setIcon(checkDrawable);
				stateCheck = true;
				selectAll();
			} else {
				stateCheck = false;
				selectAll.setIcon(unCheckDrawable);
				unSelectAll();
			}
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int arg2,
			long arg3) {
		if (!checkMode().trim().equals(getString(R.string.customize))) {
			Toast.makeText(mContext, "Kid mode or Friend mode is actived!",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		if (checkShow)
			return true;
		else
			showAllCheck();
		return true;
	}

	private void showAllCheck() {
		for (int i = 0; i < mListApplication.getChildCount(); i++) {
			holder = (AplicationLockedAdapter.ViewHolder) mListApplication
					.getChildAt(i).getTag();
			holder.mCheck.setVisibility(View.VISIBLE);
		}
		checkShow = true;
		actionDel.setVisible(true);
		selectAll.setVisible(true);
		stateCheck = false;
		selectAll.setIcon(unCheckDrawable);
	}

	private void hideAllCheck() {
		for (int i = 0; i < mListApplication.getChildCount(); i++) {
			holder = (AplicationLockedAdapter.ViewHolder) mListApplication
					.getChildAt(i).getTag();
			holder.mCheck.setVisibility(View.INVISIBLE);
		}
		checkShow = false;
		if (actionDel != null && selectAll != null) {
			actionDel.setVisible(false);
			selectAll.setVisible(false);
		}
	}

	private void selectAll() {
		for (int i = 0; i < mListApplication.getChildCount(); i++) {
			holder = (AplicationLockedAdapter.ViewHolder) mListApplication
					.getChildAt(i).getTag();
			holder.mCheck.setChecked(true);
		}

		for (MyApplication myApplication : mListApplicationList) {
			myApplication.setIschecked(true);
		}

	}

	private void unSelectAll() {
		for (int i = 0; i < mListApplication.getChildCount(); i++) {
			holder = (AplicationLockedAdapter.ViewHolder) mListApplication
					.getChildAt(i).getTag();
			holder.mCheck.setChecked(false);
		}
		for (MyApplication myApplication : mListApplicationList) {
			myApplication.setIschecked(false);
		}
	}

	private void unLockApps() {

		ArrayList<MyApplication> mList = new ArrayList<MyApplication>();
		for (MyApplication application : mListApplicationList) {
			if (application.isIschecked()) {
				mList.add(application);
				mContext.getContentResolver().delete(
						LockAppProvider.URI_APPLOCK,
						TblAppLocked.APP_PROCESS + "=?",
						new String[] { application.getPackageName() });
				for (MyApplication myApplication : Util.mListApplicationList) {
					if (myApplication.getPackageName().equals(
							application.getPackageName())) {
						myApplication.setLock(false);
					}
				}
			}

		}
		mListApplicationList.removeAll(mList);
		mAplicationAdapter.notifyDataSetChanged();
		hideAllCheck();
	}

	private String checkMode() {
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String mode = sharedPreferences.getString(Util.TYPE_MODE,
				mContext.getString(R.string.customize));
		return mode;
	}

	@Override
	public void onDestroy() {
		hideAllCheck();
		super.onDestroy();
		mListApplication = null;
		mAplicationAdapter = null;
	}

	@Override
	public void onTabChange() {
		Log.e(TAG, "ProtectedFragment called onTabChange");
		for (MyApplication myApplication : Util.mListApplicationList) {
			if (myApplication.isLock()) {

				MyApplication application = new MyApplication(
						myApplication.getName(), myApplication.isLock(),
						myApplication.getIcon(),
						myApplication.getPackageName(), false);
				if (!mListApplicationList.contains(application)) {
					Log.e(TAG, "add application = " + application.toString());
					mListApplicationList.add(application);
					mAplicationAdapter.notifyDataSetChanged();

				}
			}
		}

	}

}
