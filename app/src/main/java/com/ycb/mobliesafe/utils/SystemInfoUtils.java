package com.ycb.mobliesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by where on 2016/4/7.
 */
public class SystemInfoUtils {
    /**
     * 判断一个服务是否处于运行状态
     * @param context
     * @param className
     * @return
     */
    public static  boolean isServiceRunning(Context context,String className){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo info : runningServices){
            String serviceClassName = info.service.getClassName();
            if(className.equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取系统总进程个数
     * @param context
     * @return
     */
    public static int processCount(Context context){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取剩余内存
     * @param context
     * @return
     */
    public  static  long getAvailMem(Context context){
        //得到进程管理器
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取到内存的基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //获取到剩余内存
        return memoryInfo.availMem;
    }

    public static long getTotalMem(Context context){
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : readLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
           return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
