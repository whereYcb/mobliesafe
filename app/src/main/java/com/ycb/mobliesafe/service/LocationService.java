package com.ycb.mobliesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 获取经纬度坐标的service
 * 
 * @author where
 * 
 */
public class LocationService extends Service {

	private LocationManager lm;
	private MyLocationListener listener;
	private SharedPreferences mPref;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 获取所有位置提供者
		// List<String> allProviders = lm.getAllProviders();

		Criteria criteria = new Criteria();
		// 是否允许付费,比如使用3G网络定位
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// 获取最佳位置提供者
		String bestProvider = lm.getBestProvider(criteria, true);

		listener = new MyLocationListener();

		// 参1表示提供者,参二表示最短更新时间,参三表示最短更新距离,
		lm.requestLocationUpdates(bestProvider, 0, 0, listener);

	}

	class MyLocationListener implements LocationListener {

		// 位置发生变化
		@Override
		public void onLocationChanged(Location location) {
			//将获取到的经纬度保存在sp中
			mPref.edit()
					.putString(
							"location",
							"j :" + location.getLongitude() + " w :"
									+ location.getLatitude()).commit();
			//停掉service
			stopSelf();
		}

		// 位置提供者状态发生变化
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		// 用户打开GPS
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		// 用户关闭GPS
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//当activity销毁时,停止更新位置,节省电量
		lm.removeUpdates(listener);
	}
}
