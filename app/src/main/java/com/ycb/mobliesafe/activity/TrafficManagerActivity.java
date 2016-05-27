package com.ycb.mobliesafe.activity;

import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.bean.AppInfo;
import com.ycb.mobliesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class TrafficManagerActivity extends Activity {

    private ListView list_view;
    private List<TrafficInfo> trafficLists;
    private List<AppInfo> appInfos;
    private TrafficInfo trafficInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TrafficManagerAdapter adapter = new TrafficManagerAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private class TrafficManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return trafficLists.size();
        }

        @Override
        public Object getItem(int position) {
            return trafficLists.get(position);
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
                view = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic_manager, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_traffic = (TextView) view.findViewById(R.id.tv_total);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.icon.setImageDrawable(trafficLists.get(position).icon);
            holder.tv_name.setText(trafficLists.get(position).appName);
            String size = Formatter.formatFileSize(TrafficManagerActivity.this, trafficLists.get(position).toatlBytes);
            System.out.println(trafficLists.get(position).appName + size);
            if (TextUtils.isEmpty(size)){
                holder.tv_traffic.setText("0KB");
            }else{
                holder.tv_traffic.setText(size);
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView tv_name;
        TextView tv_traffic;
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                appInfos = AppInfos.getAppinfos(TrafficManagerActivity.this);


                for (AppInfo appInfo : appInfos) {
                    trafficInfo = new TrafficInfo();
                    int uid = appInfo.getUid();

                    //获取到手机下载的流量
//            long mobileRxBytes = TrafficStats.getMobileRxBytes(uid);
                    //获取某个网络uid下载的流量
                    long uidRxBytes = TrafficStats.getUidRxBytes(uid);

                    //获取到手机上传的流量
//            long mobileTxBytes = TrafficStats.getMobileTxBytes(uid);
                    //获取某个网络上传的流量
                    long uidTxBytes = TrafficStats.getUidTxBytes(uid);


                    long totalBytes = uidRxBytes + uidTxBytes;
                    trafficInfo.toatlBytes = totalBytes;
                    String appName = appInfo.getApkName();
                    trafficInfo.appName = appName;
                    System.out.println(trafficInfo.appName);
                    trafficInfo.toatlBytes = totalBytes;
                    Drawable icon = appInfo.getIcon();
                    trafficInfo.icon = icon;
                    trafficLists.add(trafficInfo);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    static class TrafficInfo {
        long toatlBytes;
        String appName;
        Drawable icon;
    }

    private void initUI() {
        setContentView(R.layout.activity_traffic_manager);
        list_view = (ListView) findViewById(R.id.list_view);
        trafficLists = new ArrayList<TrafficInfo>();


    }

}
