package com.ycb.mobliesafe.activity;

import com.ycb.mobliesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mDetector;
	public SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		mDetector = new GestureDetector(this, new SimpleOnGestureListener(){
			/**
			 * 监听手势滑动事件，e1表示滑动的起点，e2表示滑动的终点。velocityX表示水平速度 velocityY表示垂直速度
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//纵向滑动幅度是否过大，过大不允许切换
				if(Math.abs(e2.getRawY()-e1.getRawY())>100){
					Toast.makeText(BaseSetupActivity.this, "滑动幅度过大", Toast.LENGTH_SHORT).show();
					return true;
				}
				//判断是否滑动过慢,过慢不允许切换
				if(Math.abs(velocityX)<100){
					Toast.makeText(BaseSetupActivity.this, "滑动过慢", Toast.LENGTH_SHORT).show();
					return true;
				}
				//表示上一页
				if(e2.getRawX()-e1.getRawX()>200){
					showPreviousPage();
					return true;
				}
				//表示下一页
				if(e1.getRawX()-e2.getRawX()>200){
					showNextPage();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
		
	}
	protected abstract void showNextPage();
		
	protected abstract void showPreviousPage();
	

	// 点击下一页
	public void next(View v) {
		showNextPage();
	}

	// 点击上一页
	public void previous(View v) {
		showPreviousPage();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);//委托手势识别器处理触摸事件
		return super.onTouchEvent(event);
	}
}
