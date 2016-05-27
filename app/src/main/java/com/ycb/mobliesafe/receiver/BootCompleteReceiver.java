package com.ycb.mobliesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	/**
	 * 监听手机开机启动的sim卡
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		boolean protect = sp.getBoolean("protect", false);
		if (protect) {
			String sim = sp.getString("sim", null);
			if (!TextUtils.isEmpty(sim)) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(context.TELEPHONY_SERVICE);
				String currentSim = tm.getSimSerialNumber();
				if (sim.equals(currentSim)) {
					System.out.println("手机安全");
				} else {
					System.out.println("手机危险");
					String phone = sp.getString("safe_phone", "");
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "sd card changed", null, null);
				}
			}
		}
	}

}