package com.samsung.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.ui.fragment.ProtectedFragment;

public class AplicationLockedAdapter extends ArrayAdapter<MyApplication> {

	private List<MyApplication> mListApplication;
	private LayoutInflater inflate;

	public ArrayList<ViewHolder> arrViewHolder = new ArrayList<AplicationLockedAdapter.ViewHolder>();

	public AplicationLockedAdapter(Context context, int resource,
			List<MyApplication> objects) {
		super(context, resource, objects);
		inflate = LayoutInflater.from(context);
		mListApplication = objects;
	}

	public static class ViewHolder {
		public ImageView mImgIcon;
		public TextView mTxtName;
		public CheckBox mCheck;
	}

	@Override
	public MyApplication getItem(int position) {
		return mListApplication.get(position);
	}

	public void removeItem(int position) {
		mListApplication.remove(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		final ViewHolder holder;

		if (view == null) {

			view = inflate.inflate(R.layout.aplication_item_locked, parent,
					false);

			holder = new ViewHolder();
			holder.mImgIcon = (ImageView) view
					.findViewById(R.id.id_locked_item_appicon);
			holder.mTxtName = (TextView) view
					.findViewById(R.id.id_locked_item_appname);
			holder.mCheck = (CheckBox) view.findViewById(R.id.id_item_lock);
			view.setTag(R.id.id_locked_item_appicon, holder.mImgIcon);
			view.setTag(R.id.id_locked_item_appname, holder.mTxtName);
			view.setTag(R.id.id_item_lock, holder.mCheck);

			holder.mCheck.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					MyApplication application = (MyApplication) arg0.getTag();
					application.setIschecked(((CheckBox) arg0).isChecked());
					Log.e("tuyen.px",
							"set gia tri : " + application.isIschecked());

				}
			});

		} else {
			holder = (ViewHolder) view.getTag();
		}

		final MyApplication myapplication = mListApplication.get(position);

		holder.mImgIcon.setImageDrawable(myapplication.getIcon());
		holder.mTxtName.setText(myapplication.getName());
		Log.e("tuyen.px", "gia tri : " + myapplication.isIschecked());
		holder.mCheck.setChecked(myapplication.isIschecked());

		if (ProtectedFragment.checkShow) {
			holder.mCheck.setVisibility(View.VISIBLE);
		} else {
			holder.mCheck.setVisibility(View.INVISIBLE);
		}

		holder.mCheck.setTag(myapplication);
		arrViewHolder.add(holder);
		view.setTag(holder);

		return view;
	}

}
