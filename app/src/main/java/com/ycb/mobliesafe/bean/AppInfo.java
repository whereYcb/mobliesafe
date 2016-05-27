package com.ycb.mobliesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by where on 2016/3/15.
 */
public class AppInfo {
    /**
     * 图片的icon
     */
    private Drawable icon;

    /**
     * 程序的名字
     */
    private String apkName;

    /**
     * 程序的大小
     */
    private long apkSize;

    /**
     * 表示到底是用户APP还是系统APP
     * 如果表示true 就是用户APP
     * 如果是false 就是系统app
     */
    private boolean userApp;

    /**
     * 防止的位置
     */
    private boolean isRom;

    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * 包名
     */
    private String apkPackageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", apkName='" + apkName + '\'' +
                ", apkSize=" + apkSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPackageName='" + apkPackageName + '\'' +
                '}';
    }
}
