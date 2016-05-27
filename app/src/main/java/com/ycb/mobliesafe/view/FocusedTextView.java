package com.ycb.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class FocusedTextView extends TextView {

	// 有style样式走次方法
	public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	// 有属性时走次方法
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 用代码new对象时走此方法
	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 表示有没有获取焦点 跑马灯要运行，首先调用此函数判断有没有焦点，是true的话，跑马灯才会有效果
	 * 所以我们不管实际上textview有没有获取焦点，我们都强制返回true，让跑马灯认为有焦点。
	 */
	@Override
	public boolean isFocused() {
		return true;
	}

}
