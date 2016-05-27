package com.ycb.mobliesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * Created by where on 2016/4/9.
 */
public class KillProcessAllReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //得到手机上正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses){
            //杀死所有进程
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
        Toast.makeText(context,"清理完毕",Toast.LENGTH_SHORT).show();
    }
}
