package com.samsung.android.adapter;

import android.graphics.drawable.Drawable;

public class MyApplication {
	private String name;
	private boolean isLock;
	private Drawable icon;
	private String packageName;
	private boolean ischecked;

	public MyApplication(String name, boolean isLock, Drawable icon,
			String packageName) {
		super();
		this.name = name;
		this.isLock = isLock;
		this.icon = icon;
		this.packageName = packageName;
	}

	public MyApplication(String name, boolean isLock, Drawable icon,
			String packageName, boolean ischecked) {
		super();
		this.name = name;
		this.isLock = isLock;
		this.icon = icon;
		this.packageName = packageName;
		this.ischecked = ischecked;
	}

	public boolean isIschecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	public MyApplication() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MyApplication : " + name + " islock : " + isLock;
	}
}
