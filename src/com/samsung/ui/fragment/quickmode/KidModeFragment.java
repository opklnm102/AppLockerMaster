package com.samsung.ui.fragment.quickmode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.AplicationAdapter;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.table.TblAppLocked;
import com.samsung.contentprovider.LockAppProvider;

public class KidModeFragment extends Fragment {
	private ListView mListApplication;
	private List<ResolveInfo> mListAplicationInfo;
	private List<MyApplication> mListApplicationList = new ArrayList<MyApplication>();
	private Context mContext;
	private AplicationAdapter mAplicationAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_quickmode_kid,
				container, false);
		mListApplication = (ListView) rootView
				.findViewById(R.id.listview_qm_kid);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		loadAplication();
	}

	private void loadAplication() {
		mAplicationAdapter = new AplicationAdapter(mContext,
				R.layout.aplication_item, mListApplicationList);
		mListApplication.setAdapter(mAplicationAdapter);
		getApplication();

	}

	private void getApplication() {
		mListApplicationList.removeAll(mListApplicationList);
		new taskLoadFile().execute();
	}

	class taskLoadFile extends AsyncTask<Void, Void, Void> {

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

				if (mListAplicationInfo.get(i).activityInfo.packageName
						.equals("com.kidlandstudio.perfectapplock")) {
					continue;
				}
				String applicationname = mListAplicationInfo.get(i).loadLabel(
						mContext.getPackageManager())
						+ "";
				Log.e("activityInfo.packageName",
						mListAplicationInfo.get(i).activityInfo.packageName);
				Log.e("activityInfo.processName", mListAplicationInfo.get(i)
						.loadLabel(mContext.getPackageManager()) + "");
				MyApplication appInfo = new MyApplication(
						applicationname,
						checkApp(mListAplicationInfo.get(i).activityInfo.packageName),
						mListAplicationInfo.get(i).loadIcon(
								mContext.getPackageManager()),
						mListAplicationInfo.get(i).activityInfo.processName);
				mListApplicationList.add(appInfo);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAplicationAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

		private boolean checkApp(String name) {
			Cursor c = mContext.getContentResolver().query(
					LockAppProvider.URI_APPLOCK,
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

}
