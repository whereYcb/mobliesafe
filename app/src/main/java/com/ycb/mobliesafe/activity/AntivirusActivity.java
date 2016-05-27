package com.ycb.mobliesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.db.dao.AntivirusDao;
import com.ycb.mobliesafe.utils.MD5Utils;

import java.util.List;

/**
 * 手机杀毒
 */
public class AntivirusActivity extends Activity {

    private static final int BEGING = 1;
    private static final int SCANING = 2;
    private static final int FINISH = 3;
    private TextView tv_init;
    private ImageView iv_scanning;
    private LinearLayout ll_content;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = BEGING;
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                int size = installedPackages.size();
                progressBar.setMax(size);
                int progress= 0;
                for (PackageInfo packageInfo : installedPackages){
                    ScanInfo scanInfo = new ScanInfo();
                    //获取到当前手机上APP的名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    scanInfo.appName = appName;
                    scanInfo.packageName = packageInfo.applicationInfo.packageName;
                    //首先要得到每个应用程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    String desc = AntivirusDao.CheckFileVirus(md5);

                    if(desc == null){
                        scanInfo.desc = false;
                    }else{
                        scanInfo.desc = true;
                    }
                    progress++;
                    SystemClock.sleep(100);
                    progressBar.setProgress(progress);
                    message = Message.obtain();
                    message.what = SCANING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
               message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BEGING:
                    tv_init.setText("初始化双核杀毒引擎");
                    break;
                case SCANING:
                    tv_init.setText("快速扫描中");
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                   TextView textView = new TextView(AntivirusActivity.this);
                    if(scanInfo.desc){
                        textView.setTextColor(Color.RED);
                        textView.setText(scanInfo.appName + "有病毒危险");
                    }else{
                        textView.setTextColor(Color.BLACK);
                        textView.setText(scanInfo.appName + "扫描安全");
                    }
                    ll_content.addView(textView,0);
                    //自动滚动
//                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            //一直往下面滚动
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
                    break;
                case FINISH:
                    tv_init.setText("扫描完成");
                    //结束后停止动画
                    iv_scanning.clearAnimation();
                    break;
            }
        }
    };
    static class ScanInfo{
        boolean desc;
        String appName;
        String packageName;
    }
    private void initUI() {
        setContentView(R.layout.activity_antivirus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_init = (TextView) findViewById(R.id.tv_init);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        /**
         * 第一个参数表示开始的角度
         * 第二个参数表示结束的角度
         * 第三个参数表示相对于自己
         *
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

        //设置动画的时间
        rotateAnimation.setDuration(5000);
        //设置动画无线循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iv_scanning.setAnimation(rotateAnimation);
    }

}
