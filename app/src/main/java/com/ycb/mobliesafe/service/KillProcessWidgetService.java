package com.ycb.mobliesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.receiver.MyAppWidget;
import com.ycb.mobliesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class KillProcessWidgetService extends Service {


    private AppWidgetManager widgetManager;
    private TimerTask timerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        widgetManager = AppWidgetManager.getInstance(this);

        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                /**
                 * 第一个参数表示上下文
                 * 第二个参数表示当前有哪一个广播去进行去处理当前的桌面小控件
                 */
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidget.class);
                /**
                 * 把当前的页面文件添加进去
                 * 初始化一个远程的view
                 * Remote 远程
                 */
                int processCount = SystemInfoUtils.processCount(getApplicationContext());
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                views.setTextViewText(R.id.process_count, "正在运行的软件:" + processCount);
                long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());
                views.setTextViewText(R.id.process_memory,"可用内存:" + Formatter.formatFileSize(getApplicationContext(), availMem));
                Intent intent = new Intent();
                //发送一个隐式意图
                intent.setAction("com.ycb.mobliesafe");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);
                widgetManager.updateAppWidget(provider,views);
            }
        };
        //从0开始，每隔5秒更新一次
        timer.schedule(timerTask,0,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
