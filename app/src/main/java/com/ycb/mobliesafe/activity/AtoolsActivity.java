package com.ycb.mobliesafe.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.utils.SmsUtils;
import com.ycb.mobliesafe.utils.UIUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

/**
 * 高级工具
 * @author where
 *
 */
public class AtoolsActivity extends Activity {

	private ProgressDialog pd;
	@ViewInject(R.id.progressBar1)
	private ProgressBar progressBar1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		ViewUtils.inject(this);
	}

	/**
	 * 广告
	 * @param v
	 */
	public void appRecomment(View v){
		OffersManager.getInstance(this).showOffersWall(
				new Interface_ActivityListener() {

					/**
					 * 但积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
					 */
					@Override
					public void onActivityDestroy(Context context) {
						Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
					}
				});
	}
	
	/**
	 * 归属都查询
	 */
	public void numberAddressQuery(View v){
		startActivity(new Intent(this, AddressActivity.class));
	}

	/**
	 * 备份短信
	 * @param v
	 */
	public void backUpSms(View v){
		//初始化一个进度条
		pd = new ProgressDialog(this);
		pd.setTitle("提示");
		pd.setMessage("正在备份中....");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			@Override
			public void run() {
				boolean result = SmsUtils.backUp(AtoolsActivity.this, new SmsUtils.BackUpCallBackSms() {
					@Override
					public void befor(int count) {
						pd.setMax(count);
						progressBar1.setMax(count);
					}

					@Override
					public void onBackUpSms(int process) {
						pd.setProgress(process);

						progressBar1.setProgress(process);
					}
				});
				if (result){
//					Looper.prepare();
//					Toast.makeText(AtoolsActivity.this,"备份短信成功",Toast.LENGTH_SHORT).show();
//					Looper.loop();
					UIUtils.showToast(AtoolsActivity.this,"备份成功");
				}else{
//					Looper.prepare();
//					Toast.makeText(AtoolsActivity.this,"备份短信失败",Toast.LENGTH_SHORT).show();
//					Looper.loop();
					UIUtils.showToast(AtoolsActivity.this,"备份失败");
				}
				pd.dismiss();

			}
		}.start();

	}

	/**
	 * 程序锁
	 * @param v
	 */
	public void appLock(View v){
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
}
