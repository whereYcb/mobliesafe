package com.ycb.mobliesafe.activity;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.utils.UIUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存清理
 */
public class CleanCacheActivity extends Activity {

    private PackageManager packageManager;
    private List<CacheInfo> cacheLists;
    private ListView list_view;
    private CacheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_clean_cache);
        list_view = (ListView) findViewById(R.id.list_view);
        cacheLists = new ArrayList<CacheInfo>();

        packageManager = getPackageManager();
        new Thread() {
            @Override
            public void run() {
                /**
                 * 接收两个参数
                 * 第一个参数 包名
                 * 第二个参数  aidl的对象
                 */
                //安装到手机上面的所有应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo packInfo : installedPackages) {
                    getCacheSize(packInfo);

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        adapter = new CacheAdapter();
                        list_view.setAdapter(adapter);
                    }
                });


//                handler.sendEmptyMessage(0);
            }
        }.start();
    }
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//
//        }
//    };

    private class CacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            System.out.println(cacheLists.size() + "-----------------------");
            return cacheLists.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean_catch, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.iv_icon.setImageDrawable(cacheLists.get(position).icon);
            holder.tv_app_name.setText(cacheLists.get(position).appName);
            holder.tv_cache_size.setText("缓存:" + Formatter.formatFileSize(CleanCacheActivity.this, cacheLists.get(position).cacheSize));
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_app_name;
        TextView tv_cache_size;
    }

    private void getCacheSize(PackageInfo packInfo) {
        try {
            //通过反射获取到当前的方法
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            /**
             * 第一个参数表示当前这个方法用于谁调用
             * 第二个参数表示包名
             */
            method.invoke(packageManager, packInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private PackageInfo packInfo;

        public MyIPackageStatsObserver(PackageInfo packInfo) {
            this.packInfo = packInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取到当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;
            //如果当前的缓存大小是大于0的话，说明有缓存
            if (cacheSize > 0) {
//                System.out.println("当前应用的名字" + packInfo.applicationInfo.loadLabel(packageManager) + "缓存:" + cacheSize);
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packInfo.applicationInfo.loadIcon(packageManager);
                cacheInfo.icon = icon;
                String appName = packInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.appName = appName;
                cacheInfo.cacheSize = cacheSize;
                cacheLists.add(cacheInfo);
                System.out.println(cacheLists.size() + "```````````````````````````````");
                System.out.println(cacheInfo.appName + cacheInfo.cacheSize + "NNNNNNNNNNNNNNNNNNNNNNN");
                adapter.notifyDataSetChanged();
            }
        }
    }

    static class CacheInfo {
        Drawable icon;
        String appName;
        long cacheSize;
    }
//    freeStorageAndNotify

    /**
     * 全部清除
     *
     * @param v
     */
    public void cleanAll(View v) {
        Method[] methods = PackageManager.class.getMethods();
        adapter.notifyDataSetChanged();

        for (Method method : methods) {
            if (method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE, new MyIPackageDataObserver());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        UIUtils.showToast(CleanCacheActivity.this, "全部清除");


    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String pkgName, boolean successed) throws RemoteException {

        }
    }
}
