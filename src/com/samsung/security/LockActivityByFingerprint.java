package com.samsung.security;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.service.ProtectAppService;
import com.kidlandstudio.perfectapplock.R;
import com.samsung.android.fingerprint.FingerprintEvent;
import com.samsung.android.fingerprint.FingerprintManager;
import com.samsung.android.fingerprint.IFingerprintClient;
import com.samsung.android.table.TblAppLocked;
import com.samsung.android.table.TblFriendApp;
import com.samsung.android.table.TblKidApp;
import com.samsung.android.util.Util;
import com.samsung.contentprovider.LockAppProvider;

public class LockActivityByFingerprint extends LockActivity implements
		Handler.Callback {
	private static final String TAG = "FingerPrintAnswerCall";
	ImageView mAnimationView;
	ImageView mStatusImage;
	TextView mStatusText;
	private IBinder mToken;
	private Context mContext;
	boolean isFeatureEnabled = false;
	private boolean mIsRegisteredClient;
	private FingerprintManager mFingerprintManager;
	private volatile boolean mIsRunning;
	private Handler mHandler;
	// private boolean mIsVerificationFailed = false;
	boolean isFpEnabled = false;

	private static AnimationDrawable mFrameAnimation;
	private static AnimationDrawable mErrorAnimation;

	private final int IDLE_STATUS = 1;
	private final int SCANNING_STATUS = 2;
	private final int FAILED_STATUS = 3;
	// private final int PASSED_STATUS = 4;
	private final int WAITING_STATUS = 5;
	private final int SUCCEED_STATUS = 6;
	// private final int IDLE_ANIM_STATUS = 7;

	private final int MSG_UNLOCK = 1112;
	private final int MSG_CANCEL = 1113;
	private final int MSG_REPORT_FAILED_ATTEMPT = 1114;
	private final int MSG_READY = 1116;
	private final int MSG_IDENTIFY = 1117;
	private boolean confirmExit = false;
	private int mImageQuality;
	private int countScan = 0;

	private IFingerprintClient mFingerprintClient = new IFingerprintClient.Stub() {
		public void onFingerprintEvent(FingerprintEvent evt)
				throws RemoteException {
			final FingerprintEvent event = evt;
			if (event != null) {
				mHandler.sendMessage(Message.obtain(mHandler, event.eventId,
						event));
			} else {
				Log.e(TAG, "Invalid Event");
			}
		}
	};
	private ImageView mImageViewIcon;

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stubs
		Log.d(TAG, "tran.thang onWindowFocusChanged() hasWindowFocus = "
				+ hasWindowFocus);
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			start();
		} else {
			stop();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "tran.thang onResume");
		// start();
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "tran.thang onCreate");
		super.onCreate(savedInstanceState);
		String mProcessName = getIntent().getStringExtra(
				ProtectAppService.PROCESS_NAME);
		Log.d(TAG, "tran.thang mProcessName = " + mProcessName);
		setContentView(R.layout.finger_print_answer_call);
		mImageViewIcon = (ImageView) findViewById(R.id.passicon);
		setIcon(mProcessName, mImageViewIcon);
		mAnimationView = (ImageView) findViewById(R.id.callui_fingerprint_animation_image);
		mStatusImage = (ImageView) findViewById(R.id.callui_fingerprint_status_image);
		mStatusText = (TextView) findViewById(R.id.callui_fingerprint_status_text);
		mAnimationView.setBackgroundResource(R.drawable.spass_idle_anim);
		Log.d(TAG, " fingerprint enabled = " + isFpEnabled);
		mFingerprintManager = FingerprintManager
				.getInstance(getApplicationContext(),
						FingerprintManager.SECURITY_LEVEL_HIGH);
		mHandler = new Handler(this);
		// start();
		mContext = getApplicationContext();
		confirmExit = getIntent().getBooleanExtra(Util.KEY_CONFIRM_EXIT, false);
	}

	public boolean start() {
		Log.d(TAG, "tran.thang start() mIsRunning = " + mIsRunning);
		if (mIsRunning) {
			return false;
		}
		mIsRegisteredClient = registerClient();
		mIsRunning = true;
		if (mIsRegisteredClient) {
			setIdleStatus(true);
			mStatusText.setText(getString(R.string.tip_fingerprint));
			return true;
		}
		return true;
	}

	public boolean stop() {
		Log.d(TAG, "tran.thang stop()");

		mHandler.removeMessages(FingerprintEvent.EVENT_IDENTIFY_FINISHED);
		mStatusText.setText("");
		mStatusImage.setVisibility(View.INVISIBLE);
		if (mErrorAnimation != null) {
			mErrorAnimation.stop();
		}

		if (mIsRegisteredClient) {
			unregisterClient();
			mIsRegisteredClient = false;
		}
		if (mAnimationView != null) {
			stopAnimation();
		}
		mIsRunning = false;
		return true;
	}

	private void setIdleStatus(boolean failed) {
		Log.d(TAG, "setIdleStatus,failed = " + failed);
		if (failed) {
			startIdentify();
		} else {
			Message message = mHandler.obtainMessage(MSG_IDENTIFY);
			mHandler.sendMessageDelayed(message, 500);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		FingerprintEvent event;
		String errorMessage = null;
		switch (msg.what) {
		case MSG_UNLOCK:
			Log.d(TAG, "MSG_UNLOCK");
			stop();
			break;
		case MSG_CANCEL:
			Log.d(TAG, "MSG_CANCEL");
			stop();
			break;
		case MSG_REPORT_FAILED_ATTEMPT:
			Log.d(TAG, "MSG_REPORT_FAILED_ATTEMPT");
			break;
		case FingerprintEvent.EVENT_IDENTIFY_STARTED:
			Log.d(TAG, "EVENT_IDENTIFY_STARTED");
			mImageQuality = 0;
			if (mStatusText != null) {
				mStatusText.setText("");
			}
			if (mAnimationView != null) {
				startAnimation(SCANNING_STATUS);
			}
			break;
		case FingerprintEvent.EVENT_FINGER_REMOVED:
			Log.d(TAG, "handleMessage : EVENT_FINGER_REMOVED");

			break;

		case FingerprintEvent.EVENT_IDENTIFY_READY:
			Log.d(TAG, "EVENT_IDENTIFY_READY");
			if (mAnimationView != null) {
				startAnimation(IDLE_STATUS);
			}

			break;

		case FingerprintEvent.EVENT_IDENTIFY_FINISHED:
			event = (FingerprintEvent) msg.obj;
			if (event.eventResult == FingerprintEvent.RESULT_SUCCESS) {
				Log.d(TAG, "handleMessage : RESULT_SUCCESS");
				startSucceedAnimation();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.e("tuyenpx", "tuyenpx : name"
								+ Util.TOP_PROCESS_NEED_IGNOR);
						String mode = checkMode();
						if (mode.equals(getString(R.string.kid))) {
							ContentValues contentValues = new ContentValues();
							contentValues.put(TblKidApp.APP_ALLOW, 1);
							getContentResolver()
									.update(LockAppProvider.URI_APPKID,
											contentValues,
											TblKidApp.APP_PROCESS + " = ?",
											new String[] { Util.TOP_PROCESS_NEED_IGNOR });
						} else if (mode.equals(getString(R.string.friend))) {
							ContentValues contentValues = new ContentValues();
							contentValues.put(TblFriendApp.APP_ALLOW, 1);
							getContentResolver()
									.update(LockAppProvider.URI_APPFRIEND,
											contentValues,
											TblFriendApp.APP_PROCESS + " = ?",
											new String[] { Util.TOP_PROCESS_NEED_IGNOR });
						} else {
							ContentValues contentValues = new ContentValues();
							contentValues.put(TblAppLocked.APP_ALLOW, 1);
							getContentResolver()
									.update(LockAppProvider.URI_APPLOCK,
											contentValues,
											TblAppLocked.APP_PROCESS + " = ?",
											new String[] { Util.TOP_PROCESS_NEED_IGNOR });
						}
						Intent intent = new Intent();
						intent.putExtra("RESULT", Util.TYPE_LOCK_FINGER);
						setResult(RESULT_OK, intent);
						try {
							String s = getIntent().getStringExtra("tuyen.px");
							if (s.equals("tuyen.px"))
								Util.flag_check = true;
						} catch (Exception e) {
						}
						finish();
					}
				}, 500);

			} else if (event.eventResult == FingerprintEvent.RESULT_FAILED) {
				Log.d(TAG, "handleMessage : RESULT_FAILED eventStatus = "
						+ event.eventStatus);

				if (event.eventStatus == FingerprintEvent.STATUS_TIMEOUT) {
					Log.d(TAG,
							"tran.thang startIdentify() again due to timeout ");
					startIdentify();
				} else if (event.eventStatus != FingerprintEvent.STATUS_USER_CANCELLED) {
					Log.d(TAG,
							"tran.thang handleMessage: STATUS_USER_CANCELLED");
					errorMessage = event.getImageQualityFeedback();
					Log.d(TAG, "tran.thang errorMessage = " + errorMessage);
					if (errorMessage != null) {
						if (errorMessage.equals("Swipe downwards")) {
							Log.d(TAG, "REJECT DUE TO EQUAL STRING");
							mStatusText.setText(R.string.swipe_downwards);
						}
					}
					startFailedAnimation(errorMessage);
				}
			}
			break;
		case FingerprintEvent.IMAGE_QUALITY_REVERSE_MOTION:
			Log.d(TAG, "tran.thang IMAGE_QUALITY_REVERSE_MOTION");
			mStatusText.setText(R.string.swipe_downwards);

		case FingerprintEvent.EVENT_IDENTIFY_STATUS:
			event = (FingerprintEvent) msg.obj;

			Log.d(TAG, "handleMessage : EVENT_IDENTIFY_STATUS eventStatus = "
					+ event.eventStatus);

			mImageQuality = event.getImageQuality();
			// Log.d(TAG, "quality = " + mImageQuality);

			if (event.eventStatus == FingerprintEvent.STATUS_CAPTURE_FAILED) {
				Log.d(TAG, "handleMessage: STATUS_CAPTURE_FAILED ");

				errorMessage = event.getImageQualityFeedback();
				startFailedAnimation(errorMessage);

			}
			break;
		case MSG_IDENTIFY:
			event = (FingerprintEvent) msg.obj;
			Log.d(TAG, "handleMessage: MSG_IDENTIFY" + event.eventStatus);
			startIdentify();
			break;
		case MSG_READY:
			Log.d(TAG, "handleMessage: MSG_READY");
			setIdleStatus(true);
			break;

		default:
			Log.e(TAG, "Unhandled message = " + msg.what);
			return false;
		}

		return true;
	}

	private void startFailedAnimation(String errorMessage) {
		stopIdentify();
		if (mStatusText != null) {
			setErrorCaseText(mImageQuality, errorMessage);
		}
		if (mAnimationView != null) {
			startAnimation(FAILED_STATUS);
		}
		Message message = mHandler.obtainMessage(MSG_READY);
		mHandler.sendMessageDelayed(message, 405);
	}

	private void startSucceedAnimation() {

		if (mAnimationView != null) {
			startAnimation(SUCCEED_STATUS);
		}
		if (mStatusText != null) {
			mStatusText.setText(R.string.kg_spass_passed);
			mStatusImage.setVisibility(View.INVISIBLE);
			if (mErrorAnimation != null) {
				mErrorAnimation.stop();
			}
		}

	}

	private void setErrorCaseText(int quality, String errorMessage) {
		Log.d(TAG, "setErrorCaseText, quality = " + quality);
		FingerprintManager.getInstance(mContext);
		if (errorMessage == null) {
			mStatusText.setText("");
		} else {
			mStatusText.setText(errorMessage);

			if (mStatusImage != null) {
				mStatusImage.setVisibility(View.VISIBLE);
				mStatusImage.setImageDrawable(FingerprintManager
						.getImageQualityAnimation(quality, mContext));
				mErrorAnimation = (AnimationDrawable) mStatusImage
						.getDrawable();
				if (mErrorAnimation != null) {
					mErrorAnimation.start();
				}
			}
		}
	}

	private void startIdentify() {
		if (mIsRegisteredClient) {
			if (mFingerprintManager != null
					&& mFingerprintManager.isSensorReady() && mToken != null) {
				int result = mFingerprintManager.identify(mToken, null);
				if (result == FingerprintEvent.RESULT_OK) {
					Log.d(TAG, "identify OK");
				} else {
					// TODO: Identify fail case
					Log.e(TAG, "identify request failed.");
				}
			}
		}
	}

	private void stopIdentify() {
		if (mFingerprintManager != null) {
			Log.d(TAG, "tran.thang stopIdentify");
			mFingerprintManager.cancel(mToken);
			if (countScan == 2) {
				Toast.makeText(mContext, R.string.notice_register_in_settings,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClassName("com.android.settings",
						"com.android.settings.GridSettings");
				startActivity(intent);
				countScan = 0;
			} else {
				countScan++;
			}
		} else {
			countScan = 0;
		}
		Log.d(TAG, "tran.thang check count = " + countScan);
	}

	private boolean registerClient() {
		Log.d(TAG, "tran.thang registerClient started");
		countScan = 0;
		mIsRegisteredClient = true;
		mFingerprintManager = FingerprintManager.getInstance(this,
				FingerprintManager.SECURITY_LEVEL_HIGH);
		if (mFingerprintManager == null) {
			Log.d(TAG, "registerClient() : FingerPrintManager is not possilbe");
			return false; // No SPass manager service
		}

		FingerprintManager.FingerprintClientSpecBuilder builder = new FingerprintManager.FingerprintClientSpecBuilder(
				"system");
		builder.demandExtraEvent(true);
		builder.useManualTimeout(true);
		mToken = mFingerprintManager.registerClient(mFingerprintClient,
				builder.build());
		Log.d(TAG, "registerClient() mToken = " + mToken);
		return (mToken != null);
	}

	private void unregisterClient() {
		if (mFingerprintManager != null && mToken != null) {
			Log.d(TAG, "tran.thang unregisterClient");
			mIsRegisteredClient = false;
			mFingerprintManager.unregisterClient(mToken);
			mToken = null;
		}
	}

	private void startAnimation(int status) {
		stopAnimation();

		switch (status) {
		case IDLE_STATUS:
			mAnimationView.setBackgroundResource(R.drawable.spass_idle_anim);
			mFrameAnimation = (AnimationDrawable) mAnimationView
					.getBackground();
			mFrameAnimation.start();
			break;

		case SCANNING_STATUS:
			mAnimationView.setBackgroundResource(R.drawable.spass_scan_anim);
			mFrameAnimation = (AnimationDrawable) mAnimationView
					.getBackground();
			mFrameAnimation.start();
			break;

		case FAILED_STATUS:
			mAnimationView
					.setBackgroundResource(R.drawable.spass_mismatch_anim);
			mFrameAnimation = (AnimationDrawable) mAnimationView
					.getBackground();
			mFrameAnimation.start();
			break;

		case WAITING_STATUS:
			mAnimationView.setBackgroundResource(R.drawable.spass_unlock_dot);
			break;

		case SUCCEED_STATUS:
			mAnimationView.setBackgroundResource(R.drawable.spass_success_anim);
			mFrameAnimation = (AnimationDrawable) mAnimationView
					.getBackground();
			mFrameAnimation.start();
			break;

		default:
			break;
		}
	}

	private void stopAnimation() {
		if (mFrameAnimation != null) {
			mFrameAnimation.stop();
			mFrameAnimation = null;
		}
	}

	@Override
	public void onBackPressed() {
		if (!confirmExit) {
			if (Util.EXIT_LISNER != null)
				Util.EXIT_LISNER.exitWithoutConfirm();
			Log.d(TAG, "tran.thang onBackPressed");
			Intent homeIntent = new Intent(Intent.ACTION_MAIN);
			homeIntent.addCategory(Intent.CATEGORY_HOME);
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(homeIntent);
			super.onBackPressed();
		} else {
			Intent intent = new Intent();
			intent.putExtra("RESULT", Util.TYPE_LOCK_FINGER);
			finish();
		}
	}

	private String checkMode() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				Util.PREFEREN_NAME, Context.MODE_PRIVATE);
		String mode = sharedPreferences.getString(Util.TYPE_MODE,
				getString(R.string.customize));
		return mode;
	}
}
