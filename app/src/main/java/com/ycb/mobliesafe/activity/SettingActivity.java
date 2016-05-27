package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.service.AddressService;
import com.ycb.mobliesafe.service.CallSafeService;
import com.ycb.mobliesafe.service.WatchDogService;
import com.ycb.mobliesafe.utils.ServiceStatusUtils;
import com.ycb.mobliesafe.view.SettingClickView;
import com.ycb.mobliesafe.view.SettingItemView;

public class SettingActivity extends Activity {
	private SettingItemView sivUpdate;
	private SharedPreferences mPref;
	private SettingItemView sivAddress;
	private SettingItemView siv_callsafe;
	private SettingClickView scvAddressStyle;
	private SettingClickView scvAddressLocation;
	private SettingItemView siv_watch_dog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		initUpdate();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initBlackView();
		initWatchDog();
	}

	/**
	 * 初始化看门狗开关
	 */
	private void initWatchDog() {
		siv_watch_dog = (SettingItemView) findViewById(R.id.siv_watch_dog);

		// 根据归属地服务是否运行来更新CheckBox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.ycb.mobliesafe.service.WatchDogService");


		if (serviceRunning) {
			siv_watch_dog.setCheck(true);
		} else {
			siv_watch_dog.setCheck(false);
		}
		siv_watch_dog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watch_dog.isChecked()) {
					siv_watch_dog.setCheck(false);
					stopService(new Intent(SettingActivity.this,
							WatchDogService.class));
				} else {
					siv_watch_dog.setCheck(true);
					startService(new Intent(SettingActivity.this,
							WatchDogService.class));
				}
			}
		});
	}

	/**
	 * 初始化黑名单
	 */
	private void initBlackView() {
		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);

		// 根据归属地服务是否运行来更新CheckBox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.ycb.mobliesafe.service.CallSafeService");

		if (serviceRunning) {
			siv_callsafe.setCheck(true);
		} else {
			siv_callsafe.setCheck(false);
		}
		siv_callsafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsafe.isChecked()) {
					siv_callsafe.setCheck(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));
				} else {
					siv_callsafe.setCheck(true);
					startService(new Intent(SettingActivity.this,
							CallSafeService.class));
				}
			}
		});
	}



	/**
	 * 初始化自动更新开关
	 */
	private void initUpdate() {
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// sivUpdate.setTitle("自动更新设置");
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			// sivUpdate.setDesc("自动更新设置已开启");
			sivUpdate.setCheck(true);
		} else {
			// sivUpdate.setDesc("自动更新设置已关闭");
			sivUpdate.setCheck(false);
		}
		sivUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sivUpdate.isChecked()) {
					sivUpdate.setCheck(false);
					// sivUpdate.setDesc("自动更新已关闭");
					mPref.edit().putBoolean("auto_update", false).commit();
				} else {
					sivUpdate.setCheck(true);
					// sivUpdate.setDesc("自动更新已开启");
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});

	}

	/**
	 * 初始化归属地开关
	 */
	private void initAddressView() {
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);

		// 根据归属地服务是否运行来更新CheckBox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.ycb.mobliesafe.service.AddressService");

		if (serviceRunning) {
			sivAddress.setCheck(true);
		} else {
			sivAddress.setCheck(false);
		}
		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setCheck(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));
				} else {
					sivAddress.setCheck(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));
				}
			}
		});
	}

	final String items[] = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

	/**
	 * 初始化风格
	 */
	private void initAddressStyle() {
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		scvAddressStyle.setTitle("归属地风格");
		int style = mPref.getInt("address_style", 0);
		scvAddressStyle.setDesc(items[style]);
		scvAddressStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSingleChooseDialog();
			}
		});
	}

	/**
	 * 弹出选择风格单选框
	 */
	protected void showSingleChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);// logo
		builder.setTitle("归属地提示框风格");
		// 读取保存的风格
		int style = mPref.getInt("address_style", 0);

		builder.setSingleChoiceItems(items, style,
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 保存选择的风格
						mPref.edit().putInt("address_style", which).commit();
						// 隐藏dialog
						dialog.dismiss();

						// 更新组合控件的文字
						scvAddressStyle.setDesc(items[which]);
					}
				});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	/**
	 * 修改归属地显示位置
	 */
	private void initAddressLocation() {
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		scvAddressLocation.setTitle("归属地提示框显示位置");
		scvAddressLocation.setDesc("设置归属地提示框的显示位置");
		scvAddressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,
						DragViewActivity.class));
			}
		});
	}
}
