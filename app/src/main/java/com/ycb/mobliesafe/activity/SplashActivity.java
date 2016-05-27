package com.ycb.mobliesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.bean.Virus;
import com.ycb.mobliesafe.db.dao.AntivirusDao;
import com.ycb.mobliesafe.utils.StreamUtils;

import net.youmi.android.AdManager;

public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;

	protected static final int CODE_URL_ERR = 1;

	protected static final int CODE_NET_ERR = 2;

	protected static final int CODE_JSON_ERR = 3;

	protected static final int CODE_ENTER_HOME = 4;

	private TextView tvVersion;
	private TextView tvProgressBar;

	private String mVersionName;
	private int mVersionCode;
	private String mDesc;
	private String mDownload = "http://10.0.2.2:8080/mobliesafe2.0.apk";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERR:
				Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT)
						.show();
				enterhome();
				break;
			case CODE_NET_ERR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT)
						.show();
				enterhome();
				break;
			case CODE_JSON_ERR:
				Toast.makeText(SplashActivity.this, "数据解析错误错误",
						Toast.LENGTH_SHORT).show();
				enterhome();
				break;
			case CODE_ENTER_HOME:
				enterhome();
				break;
			}
		};
	};

	private SharedPreferences mPref;

	private RelativeLayout rlRoot;
	private AntivirusDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//添加广告
		AdManager.getInstance(this).init("b933ab8c9938e21d", "40b3047d601a2494",  true);

		rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本号:" + getVersionName());
		tvProgressBar = (TextView) findViewById(R.id.tv_ProgressBar);
		mPref = getSharedPreferences("config", MODE_PRIVATE);

		createShortcut();
		copyDB("address.db");//拷贝归属地查询数据库

		copyDB("antivirus.db");//病毒数据库
		//更新病毒数据库
		updataVirus();
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVersion();
		}else{
			handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);
	}
	//进行更新病毒数据库
	private void updataVirus() {
		HttpUtils httpUtils = new HttpUtils();
		String url = "http://100.73.89.91:8080/virus.json";
		httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
//				﻿{"md5":"5956C29CE2E17F49A71AC8526DD9CDE3","desc":"最新蝗虫病毒,赶快卸载"}
//				System.out.println(responseInfo.result);
				//解析json
				Gson gson = new Gson();
				Virus virus = gson.fromJson(responseInfo.result, Virus.class);
				dao = new AntivirusDao();
				dao.addVirus(virus.md5,virus.desc);
			}

			@Override
			public void onFailure(HttpException e, String s) {

			}
		});
	}

	/**
	 * 创建快捷方式
	 */
	private void createShortcut() {
		System.out.println("-----------------GGG");
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra("duplicate",false);
		/**
		 * 1 要干什么
		 * 2 叫什么
		 * 3 长什么样子
		 */
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");
		//不能用显示意图，需要使用隐式意图
		Intent shortcut_intent = new Intent();
		shortcut_intent.setAction("com.ycb.gg");
		shortcut_intent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(intent.EXTRA_SHORTCUT_INTENT,shortcut_intent);

		sendBroadcast(intent);
	}

	/**
	 * 获取版本名
	 * 
	 * @return
	 */
	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 从服务器获取版本信息校验
	 */
	private void checkVersion() {
		new Thread() {
			HttpURLConnection conn = null;
			Message msg = Message.obtain();

			public void run() {
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL("http://10.0.2.2:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);
						// System.out.println(result);
						// 解析json
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						// mDownload = jo.getString("downloadUrl");
						if (mVersionCode > getVersionCode()) {
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							msg.what = CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = CODE_URL_ERR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = CODE_NET_ERR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = CODE_JSON_ERR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;
					if (timeUsed < 2000) {
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendEmptyMessage(msg.what);
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}.start();

	}

	/**
	 * 升级对话框
	 */
	private void showUpdateDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("最新版本:" + mVersionName);
		builder.setMessage(mDesc);
		builder.setPositiveButton("立即升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("立即更新");
				download();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterhome();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				enterhome();
			}
		});
		builder.create().show();
	};

	/**
	 * 下载更新
	 */
	protected void download() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			tvProgressBar.setVisibility(View.VISIBLE);
			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";
			System.out.println(target);
			HttpUtils utils = new HttpUtils();
			utils.download(mDownload, target, new RequestCallBack<File>() {
				// 下载成功
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					System.out.println("下载成功");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					startActivityForResult(intent, 0);
				}

				// 下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					System.out.println("下载失败");
				}

				// 下载进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					System.out.println("下载进度：" + current + "/" + total);
					tvProgressBar
							.setText("下载进度:" + current * 100 / total + "%");
				}
			});
		} else {
			Toast.makeText(this, "sdcard不存在", Toast.LENGTH_SHORT).show();
		}

	}

	// 如果用户在安装时点击取消，则跳转主页面
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterhome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 跳转主页面
	 */
	protected void enterhome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void copyDB(String dbName){
		File destFile = new File(getFilesDir(),dbName);//要拷贝的目标地址
		if(destFile.exists()){
			System.out.println("数据库" + dbName + "已经存在");
			return;
		}
		FileOutputStream out = null;
		InputStream in=null;
		try {
			in = getAssets().open(dbName);
			out = new FileOutputStream(destFile);
			int len = 0;
			byte[] buffer = new byte[1024];
			while((len=in.read(buffer))!=-1){
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
