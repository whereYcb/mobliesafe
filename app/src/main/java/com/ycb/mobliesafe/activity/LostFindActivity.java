package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycb.mobliesafe.R;

/**
 * 手机防盗页面
 * 
 * @author where
 * 
 */
public class LostFindActivity extends Activity {
	private SharedPreferences mPref;
	private TextView safePhone;
	private ImageView ivProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPref.getBoolean("configed", false);
		if (configed) {
			setContentView(R.layout.activity_lostfind);
			safePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPref.getString("safe_phone", "");
			safePhone.setText(phone);
			boolean protect = mPref.getBoolean("protect", false);
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			if (protect) {
				ivProtect.setImageResource(R.drawable.lock);
			} else {
				ivProtect.setImageResource(R.drawable.unlock);
			}
		} else {
			startActivity(new Intent(LostFindActivity.this,
					Setup1Activity.class));
			finish();
		}
	}

	/**
	 * 重新进入设置向导
	 * 
	 * @param v
	 */
	public void reset(View v) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
}
