package com.ycb.mobliesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.db.dao.AddressDao;

public class AddressService extends Service {

	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager mWM;
	private View view;
	private SharedPreferences mPref;
	private int width;
	private int height;

	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 监听来电状态

		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		// 动态注册广播
		registerReceiver(receiver, filter);
	}

	/**
	 * 监听去电广播接收者 需要权限android.permission.PROCESS_OUTGOING_CALLS
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = AddressDao.getAddress(number);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}

	}

	class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话响了
				// 根据来电号码查询归属地
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG)
				// .show();
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话闲置状态
				if (mWM != null && view != null) {
					mWM.removeView(view);// 从window中移除view
					view = null;
				}
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		// 动态注销广播
		unregisterReceiver(receiver);
	}

	/**
	 * 自定义归属地浮窗 需要权限android.permission.SYSTEM_ALERT_WINDOW
	 */
	private void showToast(String text) {

		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		width = mWM.getDefaultDisplay().getWidth();
		height = mWM.getDefaultDisplay().getHeight();

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// 电话窗口。它用于电话交互。它置于所有应用程序之上，状态栏之下
		params.type = WindowManager.LayoutParams.TYPE_PHONE;

		// 将重心位置设置为左上方开始，也就是(0,0)从左上方开始，而不是默认的重心位置.
		params.gravity = Gravity.LEFT + Gravity.TOP;

		params.setTitle("Toast");

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		// 设置浮窗的位置，基于左上方的偏移量
		params.x = lastX;
		params.y = lastY;

		view = View.inflate(this, R.layout.toast_address, null);

		int bgs[] = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int style = mPref.getInt("address_style", 0);
		// 根据存储的样式更新背景
		view.setBackgroundResource(bgs[style]);
		TextView tvText = (TextView) view.findViewById(R.id.tv_number);
		tvText.setText(text);

		mWM.addView(view, params);

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 更新浮窗的位置
					params.x = params.x + dx;
					params.y = params.y + dy;

					// 防止坐标偏离屏幕
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > width - view.getWidth()) {
						params.x = width - view.getWidth();
					}
					if (params.y > height - view.getHeight()) {
						params.y = height - view.getHeight();
					}
					mWM.updateViewLayout(view, params);
					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor edit = mPref.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
					break;

				default:
					break;
				}
				return true;
			}
		});
	}
}
