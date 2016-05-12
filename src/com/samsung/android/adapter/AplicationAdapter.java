package com.samsung.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;

public class AplicationAdapter extends ArrayAdapter<MyApplication> implements
		OnCheckedChangeListener {
	private Context mContext;
	private List<MyApplication> mListApplication;
	private List<String> mListApplicationsLocked = new ArrayList<String>();
	private LayoutInflater inflate;

	public AplicationAdapter(Context context, int resource,
			List<MyApplication> objects) {
		super(context, resource, objects);
		mContext = context;
		inflate = LayoutInflater.from(context);
		mListApplication = objects;
		loadAppLocked();
	}

	private void loadAppLocked() {
		for (MyApplication myApplication : mListApplication) {
			if (myApplication.isLock()) {
				mListApplicationsLocked.add(myApplication.getPackageName());
			}
		}

	}

	protected static class ViewHolder {
		ImageView mImgIcon;

		TextView mTxtName;

		ToggleButton mLock;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {

			convertView = inflate.inflate(R.layout.aplication_item, parent,
					false);

			holder = new ViewHolder();

			holder.mImgIcon = (ImageView) convertView
					.findViewById(R.id.id_item_appicon);
			holder.mTxtName = (TextView) convertView
					.findViewById(R.id.id_item_appname);
			holder.mLock = (ToggleButton) convertView
					.findViewById(R.id.id_item_lock);

			holder.mLock.setOnCheckedChangeListener(this);

			convertView.setTag(R.id.id_item_appicon, holder.mImgIcon);
			convertView.setTag(R.id.id_item_appname, holder.mTxtName);
			convertView.setTag(R.id.id_item_lock, holder.mLock);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MyApplication myapplication = mListApplication.get(position);

		holder.mLock.setTag(position);

		holder.mImgIcon.setImageDrawable(myapplication.getIcon());
		holder.mTxtName.setText(myapplication.getName());
		holder.mLock.setChecked(myapplication.isLock());
		if (!checkMode().equals(mContext.getString(R.string.customize))) {
			holder.mLock.setEnabled(false);
		} else {
			holder.mLock.setEnabled(true);
		}

		convertView.setTag(holder);

		return convertView;
	}

	private String checkMode() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String mode = sharedPreferences.getString(Util.TYPE_MODE,
				mContext.getString(R.string.customize));
		return mode;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean arg1) {
		int getPosition = (Integer) view.getTag();
		mListApplication.get(getPosition).setLock(view.isChecked());
		String packageName = mListApplication.get(getPosition).getPackageName();

		if (view.isChecked()) {
			if (!mListApplicationsLocked.contains(packageName)) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(TblAppLocked.APP_PROCESS, packageName);
				contentValues.put(TblAppLocked.APP_ALLOW, 0);
				mContext.getContentResolver().insert(
						LockAppProvider.URI_APPLOCK, contentValues);
				mListApplicationsLocked.add(packageName);
				for (MyApplication aplication : Util.mListApplicationList) {
					if (aplication.getPackageName().equals(packageName)) {
						Log.e("tuyenpx", aplication.getPackageName() + "set "
								+ true);
						aplication.setLock(true);
					}
				}
			}
		} else {
			if (mListApplicationsLocked.contains(packageName)) {
				for (MyApplication aplication : Util.mListApplicationList) {
					if (aplication.getPackageName().equals(packageName)) {
						aplication.setLock(false);
						Log.e("tuyenpx", aplication.getPackageName() + "set "
								+ false);
					}
				}
				mListApplicationsLocked.remove(packageName);
				mContext.getContentResolver().delete(
						LockAppProvider.URI_APPLOCK,
						TblAppLocked.APP_PROCESS + "=?",
						new String[] { packageName });
			}

		}
	}

}
