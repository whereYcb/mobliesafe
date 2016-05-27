package com.ycb.mobliesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by where on 2016/4/10.
 */
public class AntivirusDao {
    /**
     * 检测当前的md5值是否在病毒库
     *
     * @param md5
     * @return
     */
    public static String CheckFileVirus(String md5) {
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.ycb.mobliesafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        return desc;
    }

    /**
     * 添加病毒数据库
     *
     * @param md5  特征码
     * @param desc 描述内容
     */
    public static void addVirus(String md5, String desc) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.ycb.mobliesafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("desc", desc);
        values.put("type", 6);
        values.put("name", "Android.Adware.AirAD.a");
        db.insert("datable", null, values);
        db.close();
    }
}
