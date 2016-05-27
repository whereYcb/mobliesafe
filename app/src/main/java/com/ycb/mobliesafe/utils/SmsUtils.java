package com.ycb.mobliesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by where on 2016/3/24.
 */
public class SmsUtils {
    public interface BackUpCallBackSms{
        public void befor(int count);
        public void onBackUpSms(int process);
    }
    public static boolean backUp(Context context, BackUpCallBackSms callBackSms){
        //判断内存卡状态
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                File file = new File(Environment.getExternalStorageDirectory(),"backsms.xml");
                FileOutputStream os = new FileOutputStream(file);
                //得到序列化器
                //在android系统中,所有有关xml解析的都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                //把短信序列化到sd卡，指定编码格式
                serializer.setOutput(os, "utf-8");
                //第一个参数 编码格式，第二个 是否独立文件
                serializer.startDocument("utf-8", true);
                //第一个参数是命名空间,第二个是节点名字
                serializer.startTag(null,"smss");

                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms/");
                //type = 1表示接受到的短信 2表示发送的短信
                Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                int count = cursor.getCount();
//                pd.setMax(count);
                callBackSms.befor(count);
                int process = 0;
                while (cursor.moveToNext()) {
                    System.out.println("-------------------------------------------");
                    System.out.println("address = " + cursor.getString(0));
                    System.out.println("date = " + cursor.getString(1));
                    System.out.println("type = " + cursor.getString(2));
                    System.out.println("body = " + cursor.getString(3));

                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");

                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    /**
                     * 加密短信内容
                     */
                    serializer.text(Crypto.encrypt("123",cursor.getString(3)));

                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");

                    process++;
//                    pd.setProgress(process);
                    callBackSms.onBackUpSms(process);
                    SystemClock.sleep(200);
                }
                cursor.close();
                serializer.endTag(null,"smss");
                serializer.endDocument();
                os.flush();
                os.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
