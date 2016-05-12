package com.samsung.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.adapter.TabsPagerAdapter;

public class ApplicationsFragment extends Fragment implements
		ActionBar.TabListener {

	// private FragmentActivity mContext;
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Ứng dụng đề xuất", "Ứng dụng đã khoá" };

	private List<TabChangeListener> mListeners = new ArrayList<ApplicationsFragment.TabChangeListener>();

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.app_clocker, container,
				false);

		// Initilization
		viewPager = (ViewPager) rootView.findViewById(R.id.pager);
		actionBar = getActivity().getActionBar();
		mAdapter = new TabsPagerAdapter(getChildFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#E56701")));

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		AppsFragment appsFragment = new AppsFragment();
		ProtectedFragment protectedFragment = new ProtectedFragment();
		mListeners.add(appsFragment);
		mListeners.add(protectedFragment);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		// mContext = (FragmentActivity)activity;
		super.onAttach(activity);
	}

	@Override
	public void onPause() {
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		super.onResume();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		mListeners.get(tab.getPosition()).onTabChange();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	interface TabChangeListener {
		void onTabChange();

	}

}
