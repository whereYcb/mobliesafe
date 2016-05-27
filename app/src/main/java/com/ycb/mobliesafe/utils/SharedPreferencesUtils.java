package com.ycb.mobliesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by where on 2016/4/8.
 */
public class SharedPreferencesUtils {
    public static final String SP_NAME = "config";
    public static void saveBoolean(Context context,String key,boolean value){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
    public static boolean getBoolean(Context context,String key,boolean defValue){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return  sp.getBoolean(key,defValue);
    }
}
