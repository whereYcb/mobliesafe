package com.ycb.mobliesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.ycb.mobliesafe.activity.EnterPwdActivity;
import com.ycb.mobliesafe.db.dao.AppLockDao;

import java.util.List;

public class WatchDogService extends Service {

    private ActivityManager activityManager;
    private AppLockDao dao;
    private List<String> appLockInfos;
    private WatchDogReceiver receiver;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    //临时停止保护的包名
    private String tempStopProtectPackageName;

    private class WatchDogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.ycb.mobliesafe.stopprotect")) {
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                tempStopProtectPackageName = null;
                flag = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (flag == false) {
                    startWatchDog();
                }
            }
        }
    }

    private class AppLockContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockContentObserver(Handler handler) {
            super(handler);

        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            appLockInfos = dao.findAll();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册内容观察者
        getContentResolver().registerContentObserver
                (Uri.parse("content://com.ycb.mobliesafe.change"), true, new AppLockContentObserver(new Handler()));

        //获取到进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new AppLockDao(this);
        appLockInfos = dao.findAll();
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        //停止保护
        filter.addAction("com.ycb.mobliesafe.stopprotect");
        //注册一个 锁屏广播
        /**
         * 当屏幕锁住时 狗休息
         * 当屏幕解锁时，让狗活过来
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
        //注册广播接收者
        // 1 首先需要获取到当前的任务栈
        // 2 取任务栈最上面的任务
        startWatchDog();
    }

    private boolean flag = false;

    private void startWatchDog() {
        new Thread() {
            @Override
            public void run() {
                //由于这个任务要一直的后台运行，耗时操作，避免线程阻塞
                //获取到当前正在运行的任务栈
                flag = true;
                while (flag) {
                    List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(10);
                    //获取到最上面的进程
                    ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
                    String packageName = taskInfo.topActivity.getPackageName();
                    System.out.println(packageName);
                    //这个可以优化，改成从内存当中寻找
                    if (appLockInfos.contains(packageName)) {
                        //直接从数据库里面查找当前的数据
//                    if(dao.find(packageName)){
//                        System.out.println("在程序锁数据库中!!!!!!");
                        //说明需要临时取消保护
                        //是因为用户输入了正确的密码
                        if ((packageName.equals(tempStopProtectPackageName))) {

                        } else {
                            Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                            //从服务调到activity需要标记
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //停止保护的对象
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }


                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
    }
}
