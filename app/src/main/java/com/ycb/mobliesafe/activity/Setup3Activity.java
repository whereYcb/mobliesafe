package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ycb.mobliesafe.R;

/**
 * 第三个页面
 * 
 * @author where
 * 
 */
public class Setup3Activity extends BaseSetupActivity {
	private EditText etPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		etPhone = (EditText) findViewById(R.id.et_safe_phone);
		String phone = mPref.getString("safe_phone", "");
		etPhone.setText(phone);
	}

	@Override
	protected void showNextPage() {
		String phone = etPhone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mPref.edit().putString("safe_phone", phone).commit();
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画

	}

	@Override
	protected void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// 进入动画和退出动画
	}
	public void selectContact(View v){
		Intent intent = new Intent(this,ContactActivity.class);
		startActivityForResult(intent, 0);
		}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			String phone = data.getStringExtra("phone");
			phone = phone.replaceAll("-", " ").replaceAll(" ", "");
			etPhone.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
