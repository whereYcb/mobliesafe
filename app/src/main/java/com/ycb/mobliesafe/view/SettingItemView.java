package com.ycb.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ycb.mobliesafe.R;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.ycb.mobliesafe";
	private TextView tvTitle;
	private TextView tvDesc;
	private CheckBox cbStatus;
	private String mTitle;
	private String mDescOn;
	private String mDescOff;
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		initView();
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}
	
	/**
	 * 初始化布局
	 */
	private void initView(){
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		cbStatus = (CheckBox) findViewById(R.id.cb_status);
		tvTitle.setText(mTitle);
	}

	public void setTitle(String title){
		tvTitle.setText(title);
	}
	public void setDesc(String desc){
		tvDesc.setText(desc);
	}
	public boolean isChecked(){
		return cbStatus.isChecked();
	}
	public void setCheck(boolean check){
		cbStatus.setChecked(check);
		if(check){
			tvDesc.setText(mDescOn);
		}else{
			tvDesc.setText(mDescOff);
		}
	}
}
