package com.ycb.mobliesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by where on 2016/4/10.
 */
public class AppLockDao {

    private final AppLockOpenHelper helper;
    private Context context;
    public  AppLockDao(Context context){
        this.context = context;
        helper = new AppLockOpenHelper(context);
    }

    /**
     * 添加到程序锁里面
     * @param packageName 程序的包名
     */
    public  void add(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename",packageName);
        db.insert("info", null, values);
        db.close();
        //自定义注册一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("com.ycb.mobliesafe.change"), null);
    }

    /**
     * 从程序锁里面删除当前的包
     * @param packageName
     */
    public void delete(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info", "packagename=?", new String[]{packageName});
        db.close();
        //自定义注册一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("com.ycb.mobliesafe.change"), null);
    }

    /**
     * 查找当前的包在不在程序锁里面
     * @param packageName
     * @return
     */
    public boolean find(String packageName){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packagename=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()){
            result = true;
        }
        return result;
    }

    /**
     * 查询全部锁定的包名
     * @return
     */
    public List<String> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        List<String> packnames = new ArrayList<String>();
        while (cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
