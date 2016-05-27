package com.ycb.mobliesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.bean.AppInfo;
import com.ycb.mobliesafe.engine.AppInfos;


import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> userAppInfos;
    private ArrayList<AppInfo> systemAppInfos;
    @ViewInject(R.id.tv_app)
    private TextView tv_app;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initDate();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter adapter = new AppManagerAdapter();
            listView.setAdapter(adapter);
        }
    };

    private void initDate() {
        new Thread() {
            @Override
            public void run() {
                //获取到所有安装到手机上面的应用程序
                appInfos = AppInfos.getAppinfos(AppManagerActivity.this);
                //用户程序的集合
                userAppInfos = new ArrayList<AppInfo>();
                //系统程序的集合
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();

    }


    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        ViewUtils.inject(this);
        //手机内存剩余可用空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        //sd卡可用空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));

        tv_sd.setText("内存卡可用:" + Formatter.formatFileSize(this, sd_freeSpace));

        UninstallReceiver receiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            /**
             *
             * @param absListView
             * @param firstVisibleItem 第一个可见条目的位置
             * @param visibleItemCount  一页可以展示多少个条目
             * @param totalItemCount    总共的item个数
             */
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupwindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > (userAppInfos.size() + 1)) {
                        tv_app.setText("系统程序(" + systemAppInfos.size() + ")个");
                    } else {
                        tv_app.setText("用户程序(" + userAppInfos.size() + ")个");
                    }
                }

            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object obj = listView.getItemAtPosition(position);
                System.out.println(obj.toString());
                if (obj != null && obj instanceof AppInfo){
                clickAppInfo = (AppInfo) obj;
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);

                LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                LinearLayout ll_run = (LinearLayout) contentView.findViewById(R.id.ll_run);
                LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                LinearLayout ll_setting = (LinearLayout) contentView.findViewById(R.id.ll_setting);

                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_run.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_setting.setOnClickListener(AppManagerActivity.this);


                popupwindowDismiss();
                    System.out.println(position+ "-------0.0");
                    //  -2表示包裹内容
                    popupWindow = new PopupWindow(contentView, -2, -2);
                    //使用popupwindow必须设置背景，不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);
                popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 110, location[1]);

                ScaleAnimation sa = new ScaleAnimation(0.5f,1.0f,0.5f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                sa.setDuration(300);
                contentView.startAnimation(sa);
                }
            }
        });
    }

    private void popupwindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //卸载
            case R.id.ll_uninstall:
                Intent uninstall_intent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                this.startActivity(uninstall_intent);
                popupwindowDismiss();
                break;
            //运行
            case R.id.ll_run:
                Intent run_intent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                this.startActivity(run_intent);
                popupwindowDismiss();
                break;
            //分享
            case R.id.ll_share:
                Intent share_intent = new Intent("android.intent.action.SEND");
                share_intent.setType("text/plain");
                share_intent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_intent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getApkName()+"下载地址:"+"https://play.google.com/store/apps/details?id="+clickAppInfo.getApkPackageName());
                this.startActivity(Intent.createChooser(share_intent, "分享"));
                break;
            case R.id.ll_setting:
                viewAppDetail();
                break;
        }
    }


    private void viewAppDetail() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // dat=package:com.itheima.mobileguard
        intent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
        startActivity(intent);
    }

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0){
                return null;
            }else if(position == userAppInfos.size() + 1){
                return null;
            }
            AppInfo appInfo ;
            if (position < userAppInfos.size() + 1) {
                //把多出来的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = 1 + userAppInfos.size() + 1;
                appInfo = systemAppInfos.get(position - location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertview, ViewGroup viewGroup) {
            //如果当前position=0 表示应用程序
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }

            AppInfo appInfo ;
            if (position < userAppInfos.size() + 1) {
                //把多出来的条目减掉
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = 1 + userAppInfos.size() + 1;
                appInfo = systemAppInfos.get(position - location);
            }
            View view = null;
            ViewHolder holder;
            if (convertview != null && convertview instanceof LinearLayout) {
                view = convertview;
                holder = (ViewHolder) convertview.getTag();
            } else {

                view = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
                view.setTag(holder);
            }

            holder.iv_icon.setBackground(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());
            holder.tv_size.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("外部存储");
            }
            return view;
        }

        private class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_location;
            TextView tv_size;
        }
    }

    @Override
    protected void onDestroy() {
        popupwindowDismiss();
        super.onDestroy();
    }

    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接受到系统卸载广播");
        }
    }
}
