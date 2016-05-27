package com.ycb.mobliesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.view.SettingItemView;

/**
 * 第二个页面
 * 
 * @author where
 * 
 */
public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView sivSim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		sivSim = (SettingItemView) findViewById(R.id.siv_sim);
		String sim = mPref.getString("sim", null);
		if(!TextUtils.isEmpty(sim)){
			sivSim.setCheck(true);
		}else{
			sivSim.setCheck(false);
		}
		sivSim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(sivSim.isChecked()){
					sivSim.setCheck(false);
					mPref.edit().remove("sim").commit();
				}else{
					sivSim.setCheck(true);
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();
					System.out.println(simSerialNumber);
					mPref.edit().putString("sim", simSerialNumber).commit();
				}
			}
		});
	}

	@Override
	protected void showNextPage() {
		String sim = mPref.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "必须绑定sim卡", Toast.LENGTH_SHORT).show();
			return;
		}
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
	}

	@Override
	protected void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// 进入动画和退出动画

	}

}
