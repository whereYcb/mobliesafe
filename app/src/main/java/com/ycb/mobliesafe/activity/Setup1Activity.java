package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ycb.mobliesafe.R;

/**
 * 第一个页面
 * 
 * @author where
 * 
 */
public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}


	@Override
	protected void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
	}

	@Override
	protected void showPreviousPage() {
		
	}
}
