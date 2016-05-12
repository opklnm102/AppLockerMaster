package com.samsung.security.pattentdesign;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.kidlandstudio.perfectapplock.R;

public class PatternView extends View implements OnTouchListener {

	public static final int MAX_POINT = 9;

	public static final int SIZE = 20;

	public static final int AREA = SIZE * 4;

	private static final String TAG = "PattentView";
	private List<PatternItem> arrPattern = new ArrayList<PatternItem>();
	private static final int STATUS_HALF = 1;
	private static final int STATUS_FULL = 2;
	private static final int STATUS_NONE = -1;

	public static final int STATUS_NOT_ENOUGH_NODES = 1;
	public static final int STATUS_WRONG_MAP_NODES = 2;

	private PatternItem arrCheck[][] = new PatternItem[9][2];
	private DisplayMetrics metrics = new DisplayMetrics();
	private static int STROKE_WIDTH = 15;
	private int widthScreen, heightView = -1;
	private Paint p = new Paint();
	private float X, Y;
	private List<PatternItem> arrItemSelected = new ArrayList<PatternItem>();
	private int current=-1;
	private int COLOR_PATTERN,COLOR_WRONG_LINE;
	private final int MIN_NODES = 4;
	private OnPatternListener listener;
	private int status = STATUS_NONE;
	private int numNodesSelected = -1;
	private boolean lockTouch = false;
	public static final String SPACE="SPACE";
	public static final String KEY_PATTERN="KEY_PATTERN";
	private int lineColor;

	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(lineColor==COLOR_WRONG_LINE){
				resetHalf();
				lineColor=COLOR_PATTERN;
			}
		}
	};
	public PatternView(Context context, OnPatternListener listener) {
		super(context);
		COLOR_PATTERN = context.getResources().getColor(R.color.title);
		COLOR_WRONG_LINE = context.getResources().getColor(R.color.color_wrong);
		lineColor=COLOR_PATTERN;
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		widthScreen = metrics.widthPixels;
		this.listener = listener;
		status = STATUS_NONE;
		unLock();
		// setBackgroundColor(Color.DKGRAY);
		setOnTouchListener(this);
		innitArr();
	}

	public void innitArr() {
		p.setStyle(Style.FILL);
		p.setColor(Color.WHITE);
		p.setAntiAlias(true);
		p.setStrokeWidth(STROKE_WIDTH);

	}

	public void setupNodes() {
		int x1, x2, x3, y1, y2, y3;
		int priceW = widthScreen / 6 - SIZE / 2;
		int priceH = heightView / 6;
		x1 = priceW;
		x2 = priceW * 3;
		x3 = priceW * 5;
		y1 = priceH;
		y2 = priceH * 3;
		y3 = priceH * 5;
		// ----------
		// row1
		arrPattern.add(new PatternItem(x1, y1, SIZE, SIZE));
		arrPattern.add(new PatternItem(x2, y1, SIZE, SIZE));
		arrPattern.add(new PatternItem(x3, y1, SIZE, SIZE));
		// row2
		arrPattern.add(new PatternItem(x1, y2, SIZE, SIZE));
		arrPattern.add(new PatternItem(x2, y2, SIZE, SIZE));
		arrPattern.add(new PatternItem(x3, y2, SIZE, SIZE));
		// row3
		arrPattern.add(new PatternItem(x1, y3, SIZE, SIZE));
		arrPattern.add(new PatternItem(x2, y3, SIZE, SIZE));
		arrPattern.add(new PatternItem(x3, y3, SIZE, SIZE));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (heightView == -1) {
			heightView = getHeight();
			setupNodes();
		}
		drawLine(canvas);
		drawNodes(canvas);
	}

	public void drawNodes(Canvas v) {
		p.setStyle(Style.FILL);
		p.setColor(Color.WHITE);
		for (int i = 0; i < arrPattern.size(); i++) {
			v.drawCircle(arrPattern.get(i).x, arrPattern.get(i).y,arrPattern.get(i).w, p);
		}
		p.setStyle(Style.STROKE);
		p.setColor(COLOR_PATTERN);
		for (int i = 0; i < arrPattern.size(); i++) {
			v.drawCircle(arrPattern.get(i).x, arrPattern.get(i).y,arrPattern.get(i).w, p);
		}
		if(current>=0){
			p.setStyle(Style.FILL_AND_STROKE);
			p.setColor(COLOR_PATTERN);
			v.drawCircle(arrPattern.get(current).x, arrPattern.get(current).y,arrPattern.get(current).w*2, p);
		}
	}

	public void drawLine(Canvas canvas) {
		p.setColor(lineColor);
		int size = arrItemSelected.size();
		if (arrItemSelected.size() >= 2) {

			for (int i = 0; i < arrItemSelected.size() - 1; i++) {
				canvas.drawLine(arrItemSelected.get(i).x,arrItemSelected.get(i).y, arrItemSelected.get(i + 1).x,arrItemSelected.get(i + 1).y, p);
			}
		}
		if (size > 0 && X != -1000)
			canvas.drawLine(arrItemSelected.get(size - 1).x,arrItemSelected.get(size - 1).y, X, Y, p);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (lockTouch)
			return true;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:			
			Log.i(TAG, "onTouch...ACTION_DOWN");
			arrItemSelected.clear();
			break;
		case MotionEvent.ACTION_MOVE:
			X = event.getX();
			Y = event.getY();
			detectAreas(X, Y);
			postInvalidate();
			break;
		case MotionEvent.ACTION_UP:
			X = -1000;
			Y = -1000;
			current=-1;
			checkLessNode();
			break;
		default:
			break;
		}
		return true;
	}

	public void lock() {
		lockTouch = true;
	}

	public void unLock() {
		lockTouch = false;
	}

	public void resetAll() {
		status = STATUS_NONE;
		arrItemSelected.clear();
		postInvalidate();
	}

	public void resetHalf() {
		arrItemSelected.clear();
		postInvalidate();
	}

	public void checkLessNode() {
		int size = arrItemSelected.size();
		Log.i(TAG, "NUM NODES:" + size);

		if (size < MIN_NODES) {
			Log.w(TAG, "NUM NODES < MIN_NODES" + MIN_NODES);
			listener.drawPatternError(STATUS_NOT_ENOUGH_NODES);
			arrItemSelected.clear();
			postInvalidate();
		} else {
			Log.i(TAG, "NUM NODES is full");
			if (status == STATUS_NONE) {
				Log.i(TAG, "checkLessNode: CASE STATUS_NONE");

				numNodesSelected = size;
				for (int i = 0; i < size; i++) {
					arrCheck[i][0] = arrItemSelected.get(i);
					Log.e(TAG, "tuyenpx: arrItemSelected.get(i)= "+arrItemSelected.get(i).toString());
				}
				status = STATUS_HALF;
				listener.drawPatternCompleteHalf(arrItemSelected);
			} else if (status == STATUS_HALF) {
				Log.i(TAG, "checkLessNode: CASE STATUS_HALF");
				for (int i = 0; i < size; i++) {
					arrCheck[i][1] = arrItemSelected.get(i);
				}
				checkCorrect();
			}
			postInvalidate();
		}
	}

	public boolean checkCorrect() {
		if (numNodesSelected != arrItemSelected.size()){
			lineColor=COLOR_WRONG_LINE;
			sendMessageResetColor();
			listener.drawPatternError(STATUS_WRONG_MAP_NODES);
			return false;
		}
		boolean check = true;
		for (int i = 0; i < numNodesSelected; i++) {
			if (arrCheck[i][0].x != arrCheck[i][1].x || arrCheck[i][0].y != arrCheck[i][1].y) {
				check = false;
				lineColor=COLOR_WRONG_LINE;
				sendMessageResetColor();
				listener.drawPatternError(STATUS_WRONG_MAP_NODES);
				return check;
			}
		}
		lineColor=COLOR_PATTERN;
		status = STATUS_FULL;
		listener.drawPatternComplete(arrItemSelected);
		return check;
	}

	public void detectAreas(float x, float y) {

		for (int i = 0; i < arrPattern.size(); i++) {
			PatternItem item = arrPattern.get(i);
			if (x >= item.x - AREA && x < (item.x + AREA) && y > item.y - AREA && y < (item.y + AREA)) {
				if (!arrItemSelected.contains(item)) {
					boolean kt = true;
					if (arrItemSelected.size() > 0)
						kt = detectNodeMissing(current, i);
					if (kt) {
						current = i;
						arrItemSelected.add(item);
					}
				}
				break;
			}
		}
	}

	public boolean detectTop(int current, int next, int target1, int target2,
			int target3, int pocress1, int pocress2, int pocress3) {
		if (next == target1) {
			if (arrItemSelected.contains(arrPattern.get(pocress1)))
				return false;
			arrItemSelected.add(arrPattern.get(pocress1));
			return true;
		} else if (next == target2) {
			if (arrItemSelected.contains(arrPattern.get(pocress2)))
				return false;
			arrItemSelected.add(arrPattern.get(pocress2));
			return true;
		} else if (next == target3) {
			if (arrItemSelected.contains(arrPattern.get(pocress3)))
				return false;
			arrItemSelected.add(arrPattern.get(pocress3));
			return true;
		}
		return true;
	}

	public boolean detectOne(int current, int next, int target, int pocress) {
		if (next == target) {
			if (arrItemSelected.contains(arrPattern.get(pocress)))
				return false;
			arrItemSelected.add(arrPattern.get(pocress));
			return true;
		}
		return true;
	}

	public boolean detectNodeMissing(int current, int next) {
		Log.i(TAG, "detectNodeMissing..." + current + ":" + next);
		switch (current) {
		case 0:
			return detectTop(current, next, 2, 6, 8, 1, 3, 4);
		case 1:
			return detectOne(current, next, 7, 4);
		case 2:
			return detectTop(current, next, 8, 0, 6, 5, 1, 4);
		case 3:
			return detectOne(current, next, 5, 4);
		case 5:
			return detectOne(current, next, 3, 4);
		case 6:
			return detectTop(current, next, 0, 8, 2, 3, 7, 4);
		case 7:
			return detectOne(current, next, 2, 4);
		case 8:
			return detectTop(current, next, 6, 2, 0, 7, 5, 4);
		default:
			return true;
		}
	}

	public interface OnPatternListener {
		public void drawPatternCompleteHalf(List<PatternItem> arr);
		public void drawPatternComplete(List<PatternItem> arr);
		public void drawPatternError(int status);
	}
	public void sendMessageResetColor(){
		handler.sendMessageDelayed(new Message(), 1000);
	}
}
