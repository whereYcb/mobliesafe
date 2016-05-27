package com.ycb.mobliesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 服务状态工具
 * 
 * @author where
 * 
 */
public class ServiceStatusUtils {
	/**
	 * 检查服务是否正在运行
	 * 
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		//获取系统所有正在运行的服务，最多100个
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//获取服务的名称
			String className = runningServiceInfo.service.getClassName();
			System.out.println(className);
			if(className.equals(serviceName)){
				return true;
			}
		}
		return false;
	}
}
