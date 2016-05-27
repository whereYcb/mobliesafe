package com.ycb.mobliesafe.receiver;

import com.ycb.mobliesafe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {
	/**
	 * 监听去电广播接收者 需要权限android.permission.PROCESS_OUTGOING_CALLS
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		String address = AddressDao.getAddress(number);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
