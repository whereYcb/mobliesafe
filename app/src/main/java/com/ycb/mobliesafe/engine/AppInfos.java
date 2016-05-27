package com.ycb.mobliesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ycb.mobliesafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by where on 2016/3/15.
 */
public class AppInfos {
    /**
     * 获取到手机里面所有应用程序
      * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppinfos(Context context){
        List<AppInfo> packageAppInfos = new ArrayList<AppInfo>();
        //获取包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获取到安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        for (PackageInfo  packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();
            //获取应用程序的图标
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);
            //获取到应用程序的名字
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);
            //获取到应该程序的uid
            int uid = packageInfo.applicationInfo.uid;
            System.out.println(apkName + "-----" + uid);
            appInfo.setUid(uid);
            //获取应用程序的包名
            String packageName = packageInfo.packageName;
            appInfo.setApkPackageName(packageName);
            //获取到应用程序的路径
            String sourceDir = packageInfo.applicationInfo.sourceDir;

            File file = new File(sourceDir);
            //apk的长度
            long apkSize = file.length();
            appInfo.setApkSize(apkSize);

            //获取到安装应用的标记
            int flags = packageInfo.applicationInfo.flags;
            if((flags& ApplicationInfo.FLAG_SYSTEM)!=0){
                //表示系统APP
                appInfo.setUserApp(false);
            }else{
                //表示用户APP
                appInfo.setUserApp(true);
            }

            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                //表示在sd卡中
                appInfo.setIsRom(false);
            }else{
                //表示内存
                appInfo.setIsRom(true);
            }
            packageAppInfos.add(appInfo);
        }

        return packageAppInfos;
    }
}
