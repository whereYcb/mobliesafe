package com.ycb.mobliesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by where on 2016/4/8.
 */
public class KillProcessService extends Service{


    private LockScreenReceiver lockScreenReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lockScreenReceiver = new LockScreenReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver,intentFilter);
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        };
//        /**
//         * 第一个参数表示 用哪个类调度
//         *
//         * 第二个参数表示时间
//         *
//         */
//        timer.schedule(timerTask,1000,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当应用程序退出时，关闭广播
        unregisterReceiver(lockScreenReceiver);
        lockScreenReceiver = null;
    }

    /**
     * 锁屏广播
     */
    private class LockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info :runningAppProcesses){
                activityManager.killBackgroundProcesses(info.processName);
            }
        }
    }
}
