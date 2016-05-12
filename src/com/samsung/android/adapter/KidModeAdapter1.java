package com.samsung.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.kidlandstudio.perfectapplock.R;

public class KidModeAdapter1 extends ArrayAdapter<MyApplication> implements OnCheckedChangeListener {
	public static List<MyApplication> mListApplication;
	private LayoutInflater inflate;
	public KidModeAdapter1(Context context, int resource, List<MyApplication> objects) {
		super(context, resource, objects);
		inflate=LayoutInflater.from(context);
		mListApplication = objects;
	}

	protected static class ViewHolder {
		ImageView mImgIcon;
		TextView mTxtName;
		Switch mLock;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflate.inflate(R.layout.aplication_item, parent, false);
			holder = new ViewHolder();
			holder.mImgIcon = (ImageView) convertView.findViewById(R.id.id_item_appicon);
			holder.mTxtName = (TextView) convertView.findViewById(R.id.id_item_appname);
			holder.mLock = (Switch) convertView.findViewById(R.id.id_item_lock);

			convertView.setTag(R.id.id_item_appicon, holder.mImgIcon);
			convertView.setTag(R.id.id_item_appname, holder.mTxtName);
			convertView.setTag(R.id.id_item_lock, holder.mLock);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MyApplication myapplication = mListApplication.get(position);

		holder.mImgIcon.setImageDrawable(myapplication.getIcon());
		holder.mTxtName.setText(myapplication.getName());
		holder.mLock.setClickable(false);
		holder.mLock.setChecked(true);
		holder.mLock.setTag(position);

		convertView.setTag(holder);
		return convertView;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean arg1) {

	}
}