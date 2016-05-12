package com.samsung.security.pattentdesign;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.util.Util;

public class PatternConfiguration extends Activity implements
		PatternView.OnPatternListener, OnClickListener {

	private static final String TAG = "PatternConfiguration";
	private LinearLayout panel;
	private PatternView pattenView;
	private Button btnCancel, btnContinue;
	private TextView txtStatus;
	private int status=0;
	private SharedPreferences sharePrefer;
	private List<PatternItem> arr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pattern_layout);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnContinue = (Button) findViewById(R.id.btnContinue);
		txtStatus=(TextView)findViewById(R.id.title);
		btnCancel.setOnClickListener(this);
		btnContinue.setOnClickListener(this);

		btnContinue.setEnabled(false);
		
		panel = (LinearLayout) findViewById(R.id.panel);
		pattenView = new PatternView(this, this);
		panel.addView(pattenView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
	}

	@Override
	public void drawPatternCompleteHalf(List<PatternItem> arr) {
		btnCancel.setText(getString(R.string.state_button_retry));
		status=1;
		btnContinue.setEnabled(true);
		pattenView.lock();
	}

	@Override
	public void drawPatternComplete(List<PatternItem> arr) {
		btnContinue.setEnabled(true);
		this.arr=arr;
		txtStatus.setText(getString(R.string.status_button));
		Log.i(TAG,"drawPatternComplete...");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			if(status==0){
				finish();
			}else if(status==1){
				btnCancel.setText(getString(R.string.btn_cancel));
				btnContinue.setEnabled(false);
				btnContinue.setText((getString(R.string.btn_continue)));
				status=0;
				pattenView.resetAll();
				pattenView.unLock();
			}
			break;
		case R.id.btnContinue:
			if(status==1){
				status=0;
				txtStatus.setText("Draw again to confirm your pattern");
				btnContinue.setText(getString(R.string.btn_confirm));
				btnCancel.setText(getString(R.string.btn_cancel));
				pattenView.resetHalf();
				pattenView.unLock();
				btnContinue.setEnabled(false);
			}else if(status==0){
				//Confirm
				txtStatus.setText(getString(R.string.annouce_password_saving));
				Toast.makeText(this, getString(R.string.annouce_password_saving), Toast.LENGTH_SHORT).show();
				saveData(arr);
				pattenView.resetAll();
				pattenView.unLock();				
				Intent intent=new Intent();
				intent.putExtra("RESULT", Util.TYPE_LOCK_PARTEN);
				setResult(RESULT_OK, intent);
				finish();
				//restoreData();
			}
			break;

		default:
			break;
		}
	}
	public void saveData(List<PatternItem> arr){
		String srt="";
		for (int i = 0; i < arr.size(); i++) {
			srt+=arr.get(i).x +"_"+arr.get(i).y +PatternView.SPACE;
		}
		sharePrefer=getSharedPreferences(Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		Editor editor=sharePrefer.edit();
		editor.putString(PatternView.KEY_PATTERN, srt);
		editor.commit();		
	}
	public List<PatternItem> restoreData(){
		sharePrefer=getSharedPreferences(Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String srt=sharePrefer.getString(PatternView.KEY_PATTERN, "");
		if(srt.equals(""))
			return null;
		List<PatternItem> arr=new ArrayList<PatternItem>();
		String arrS[]=srt.split(PatternView.SPACE);
		int x,y;
		for (int i = 0; i < arrS.length-1; i++) {
			x=Integer.parseInt(arrS[i].split("_")[0]);
			y=Integer.parseInt(arrS[i].split("_")[1]);
			arr.add(new PatternItem(x, y, PatternView.SIZE, PatternView.SIZE));
			Log.i(TAG, "restoreData..."+x+": "+y);
		}
		return arr;
	}

	@Override
	public void drawPatternError(int status) {
		if(status==PatternView.STATUS_NOT_ENOUGH_NODES){
			txtStatus.setText(R.string.status_not_enough_nodes);
		}else if(status==PatternView.STATUS_WRONG_MAP_NODES){
			txtStatus.setText(getString(R.string.annouce_wrong_pattern));
		}
	}
}
