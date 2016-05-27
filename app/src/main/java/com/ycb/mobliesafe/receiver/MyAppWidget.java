package com.ycb.mobliesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.ycb.mobliesafe.service.KillProcessWidgetService;

/**
 * 创建桌面小部件的步骤：
 * 1 需要在清单文件里面配置元数据
 * 2 需要配置当前元数据里面要用到xml
 * res/xml
 * 3 需要配置一个广播接受者
 * 4 实现一个桌面小部件的xml
 * (根据需求。桌面小控件涨什么样子。就实现什么样子)
 * <p/>
 * Created by where on 2016/4/8.
 */
public class MyAppWidget extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    /**
     * 当桌面上所有的桌面小部件都删除时调用.
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }

    /**
     * 删除一个桌面小部件时会调用
     *
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 每次有新的桌面小部件生成的时候会调用
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 第一次调用的时候运行
     *
     * @param context 当前广播的生命周期只有10秒
     *                不能做耗时的操作
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }
}
