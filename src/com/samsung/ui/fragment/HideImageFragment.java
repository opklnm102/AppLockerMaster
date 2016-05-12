package com.samsung.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidlandstudio.perfectapplock.R;

public class HideImageFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.setting_item,
				container, false);
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}