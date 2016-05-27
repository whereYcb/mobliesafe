package com.ycb.mobliesafe.engine;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by where on 2016/4/7.
 */
public class TaskInfoParser {
    public static List<TaskInfo> getTaskInfo(Context context){
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();


        //获取到手机上所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> AppProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo runingAppProcessInfo : AppProcesses){
            TaskInfo taskInfo = new TaskInfo();
            //获取到进程的名字
            String processName = runingAppProcessInfo.processName;
            taskInfo.setPackageName(processName);
            Debug.MemoryInfo[] MemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runingAppProcessInfo.pid});
            //dirty弄脏
            //获取到总共弄脏多少内存(当前应用程序占用多少内存)
            int totalPrivateDirty = MemoryInfo[0].getTotalPrivateDirty() * 1024 ;
            taskInfo.setMemorySize(totalPrivateDirty);

            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //获取到图片
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);

                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);
                System.out.println("------------------------------");
                System.out.println("appName=" + appName);
                int flags = packageInfo.applicationInfo.flags;
                if((flags & ApplicationInfo.FLAG_SYSTEM) != 0 ){
                    //系统进程
                    taskInfo.setUserApp(false);
                }else{
                    //用户进程
                    taskInfo.setUserApp(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setAppName("系统");
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
