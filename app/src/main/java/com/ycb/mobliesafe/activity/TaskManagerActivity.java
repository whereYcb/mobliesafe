package com.ycb.mobliesafe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.bean.AppInfo;
import com.ycb.mobliesafe.bean.TaskInfo;
import com.ycb.mobliesafe.engine.TaskInfoParser;
import com.ycb.mobliesafe.utils.SharedPreferencesUtils;
import com.ycb.mobliesafe.utils.SystemInfoUtils;
import com.ycb.mobliesafe.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


public class TaskManagerActivity extends Activity {
    @ViewInject(R.id.tv_task_process_count)
    private TextView tv_task_process_count;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    private long totalMem;
    @ViewInject(R.id.list_view)
    private ListView listView;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userInfos;
    private List<TaskInfo> systemInfos;
    private TaskManagerAdapter adapter;
    private long availMem;
    private long totalMem1;
    private int processCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        inintData();

    }

    private void inintData() {

        new Thread(){
            @Override
            public void run() {
                taskInfos = TaskInfoParser.getTaskInfo(TaskManagerActivity.this);
                userInfos = new ArrayList<TaskInfo>();
                systemInfos = new ArrayList<TaskInfo>();
               for (TaskInfo taskInfo : taskInfos){
                   if(taskInfo.isUserApp()){
                       userInfos.add(taskInfo);
                   }else{
                       systemInfos.add(taskInfo);
                   }
               }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new TaskManagerAdapter();
                        listView.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }

    /**
     * ActivityManager
     * 活动管理器(任务管理器)
     * PackageManager
     * 包管理器
     */
    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);
        processCount = SystemInfoUtils.processCount(this);
        //获取进程个数
        tv_task_process_count.setText("进程:" + processCount + "个");
        //获取剩余内存
        availMem = SystemInfoUtils.getAvailMem(this);
        //获取总内存
        totalMem1 = SystemInfoUtils.getTotalMem(this);
        tv_task_memory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availMem) + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem1));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = listView.getItemAtPosition(position);
                if(object != null && object instanceof TaskInfo){
                    TaskInfo taskInfo = (TaskInfo) object;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (taskInfo.getPackageName().equals(getPackageName())){
                        return;
                    }
                    if(taskInfo.isChecked()){
                        taskInfo.setChecked(false);
                        holder.cb_task_status.setChecked(false);
                    }else{
                        taskInfo.setChecked(true);
                        holder.cb_task_status.setChecked(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private class TaskManagerAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            boolean result = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, "is_show_system", false);
            if (result){
                return userInfos.size() + 1 + systemInfos.size() + 1;
            }else{
                return userInfos.size() + 1 ;
            }
        }

        @Override
        public Object getItem(int position) {
            if(position == 0){
                return null;
            }else if(position == (userInfos.size() + 1)){
                return null;
            }
            TaskInfo taskInfo;
            if (position < userInfos.size() + 1) {
                //把多出来的条目减掉
                taskInfo = userInfos.get(position - 1);
            } else {
                int location = 1 + userInfos.size() + 1;
                taskInfo = systemInfos.get(position - location);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //如果当前position=0 表示应用程序
            if (position == 0) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText("用户程序(" + userInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userInfos.size() + 1) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText("系统程序(" + systemInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }


            ViewHolder holder;
            View view;
            if (convertView != null && convertView instanceof LinearLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                 view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                 holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_task_memory = (TextView) view.findViewById(R.id.tv_task_memory);
                holder.cb_task_status = (CheckBox) view.findViewById(R.id.cb_task_status);
                view.setTag(holder);
            }



            TaskInfo taskInfo;

            if (position < (userInfos.size() + 1)) {
                //把多出来的条目减掉
                taskInfo = userInfos.get(position - 1);
            } else {
                int location = 1 + userInfos.size() + 1;
                taskInfo = systemInfos.get(position - location);
            }
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());

            holder.tv_name.setText(taskInfo.getAppName());

            holder.tv_task_memory.setText("内存占用:" + Formatter.formatFileSize(TaskManagerActivity.this,taskInfo.getMemorySize()));

            if (taskInfo.isChecked()){
                holder.cb_task_status.setChecked(true);
            }else{
                holder.cb_task_status.setChecked(false);
            }
            //如果当前展示的item是自己的程序，就把程序隐藏
            if(taskInfo.getPackageName().equals(getPackageName())){
                holder.cb_task_status.setVisibility(View.INVISIBLE);
            }else{
                holder.cb_task_status.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_task_memory;
        CheckBox cb_task_status;
    }

    /**
     * 全选
     * @param v
     */
    public void selectAll(View v){
        for (TaskInfo taskInfo : userInfos){
            if (taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : systemInfos){
            taskInfo.setChecked(true);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     * @param v
     */
    public  void selectOppsite(View v){
        for(TaskInfo taskInfo : userInfos){
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        for (TaskInfo taskInfo : systemInfos){
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理
     * @param v
     */
    public void killProcess(View v){
        int totalCount = 0;
        int killMem = 0;
        List<TaskInfo> killLists = new ArrayList<TaskInfo>();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (TaskInfo taskInfo : userInfos){
            if(taskInfo.isChecked()){
                //迭代中不能改变长度,删除不了
//                userInfos.remove(taskInfo);
                killLists.add(taskInfo);
                //杀死进程 参数:包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }
        for (TaskInfo taskInfo : systemInfos){
            if(taskInfo.isChecked()){
                //迭代中不能改变长度,删除不了
//                systemInfos.remove(taskInfo);
                killLists.add(taskInfo);
                //杀死进程 参数:包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }

        // 注意: 当集合在迭代的时候不能改变集合的大小
        for (TaskInfo taskInfo : killLists){
            if (taskInfo.isUserApp()){
                userInfos.remove(taskInfo);
                //杀死进程 参数:包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }else{
                systemInfos.remove(taskInfo);
                //杀死进程 参数:包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        processCount -= totalCount;
        tv_task_process_count.setText("进程:" + processCount + "个");

        tv_task_memory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availMem + killMem) + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem1));

        UIUtils.showToast(this,"共清理" + totalCount + "个进程,释放" + Formatter.formatFileSize(this,killMem));
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置
     * @param v
     */
    public void openSetting(View v){
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
