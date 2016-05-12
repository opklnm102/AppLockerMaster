package com.samsung.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.AplicationAdapter;
import com.samsung.android.adapter.MyApplication;
import com.samsung.android.util.Util;
import com.samsung.ui.fragment.ApplicationsFragment.TabChangeListener;
import com.samsung.security.*;
import com.kidlandstudio.perfectapplock.*;

;

public class AppsFragment extends Fragment implements OnTouchListener,
		TabChangeListener, LockActivity.CallBackLoadApp,
		MainActivity.CallBackLoadApp {

	private static ListView mListApplication;
	private static AplicationAdapter mAplicationAdapter;
	private static final String TAG = "AppsFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (Util.mListApplicationList == null) {
			Util.mListApplicationList = new ArrayList<MyApplication>();
		}
		final View rootView = inflater.inflate(
				R.layout.application_fragment_layout, container, false);
		mListApplication = (ListView) rootView
				.findViewById(R.id.list_application);
		Log.e(TAG, "onCreateView called");

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadAplication();

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.e(TAG, "onDetach called");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.e(TAG, "ondestroyview called");
	}

	private void loadAplication() {
		try {
			Log.e(TAG, " loadAplication gia tri list da load = "
					+ Util.mListApplicationList.size());
			mAplicationAdapter = new AplicationAdapter(getActivity()
					.getBaseContext(), R.layout.aplication_item,
					Util.mListApplicationList);
			mListApplication.setAdapter(mAplicationAdapter);
		} catch (NullPointerException e) {
			Log.e(TAG, e.toString());
			if (Util.mListApplicationList == null) {
				Log.e(TAG, "mListApplicationList null");
			}
			if (mAplicationAdapter == null) {
				Log.e(TAG, "mAplicationAdapter null");
			}
			if (mListApplication == null) {
				Log.e(TAG, "mListApplication null");
			}
		}

	}

	private String checkMode() {
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String mode = sharedPreferences.getString(Util.TYPE_MODE, getActivity()
				.getBaseContext().getString(R.string.customize));
		return mode;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
			if (!checkMode().equals(
					getActivity().getBaseContext()
							.getString(R.string.customize))) {
				Toast.makeText(getActivity().getBaseContext(),
						getString(R.string.kid_friend), Toast.LENGTH_SHORT)
						.show();
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mListApplication = null;
		mAplicationAdapter = null;
	}

	@Override
	public void onTabChange() {
		Log.e("tuyenpx", "AppsFragment called onTabChange");
		if (mAplicationAdapter != null) {
			try {
				mAplicationAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				Log.e(TAG, "list view is not created , no need notify");
			}
		}

	}

	@Override
	public void onloadone() {
		Log.e(TAG, "onloadone");
		Log.e("tuyenpx", "AppsFragment called onTabChange");
		notifyExitsList();
	}

	private void notifyExitsList() {
		if (mListApplication != null) {
			if (mAplicationAdapter != null) {
				try {
					Log.e("tuyenpx", "notify list");
					mAplicationAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					Log.e(TAG, "list view is not created , no need notify");
				}
			}
		}
	}

	@Override
	public void onloadonefirst() {
		Log.e(TAG, "onloadonefist");
		Log.e("tuyenpx", "AppsFragment called onTabChange");
		notifyExitsList();
	}

}