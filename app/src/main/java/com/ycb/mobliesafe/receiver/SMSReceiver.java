package com.ycb.mobliesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.service.LocationService;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			// 短信来源号码
			// String originatingAddress = message.getOriginatingAddress();
			// 短信内容
			String messageBody = message.getMessageBody();
			if ("#*alarm*#".equals(messageBody)) {
				// 播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setLooping(true);
				player.setVolume(1f, 1f);
				player.start();
				// 终止短信的传递
				abortBroadcast();
			} else if ("#*location*#".equals(messageBody)) {
				// 获取经纬度坐标
				context.startService(new Intent(context, LocationService.class));
				SharedPreferences sp = context.getSharedPreferences("config",
						context.MODE_PRIVATE);
				String location = sp.getString("location", "getting location...");
				System.out.println(location);
				abortBroadcast();
			}else if("#*wipedata*#".equals(messageBody)){
				dpm.wipeData(0);
				abortBroadcast();
			}else if("#*lockscreen*#".equals(messageBody)){
				dpm.lockNow();
				dpm.resetPassword("0000", 0);
				abortBroadcast();
			}
		}
	}

}
