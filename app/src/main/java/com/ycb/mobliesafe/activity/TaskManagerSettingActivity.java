package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.service.KillProcessService;
import com.ycb.mobliesafe.utils.SharedPreferencesUtils;
import com.ycb.mobliesafe.utils.SystemInfoUtils;

/**
 *  进程设置页面
 */
public class TaskManagerSettingActivity extends Activity {

    private CheckBox cb_status;
    private CheckBox cb_status_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager_setting);
        cb_status = (CheckBox) findViewById(R.id.cb_status);
        cb_status.setChecked(SharedPreferencesUtils.getBoolean(TaskManagerSettingActivity.this, "is_show_system", false));
        cb_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", true);
                } else {
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", false);
                }
            }
        });
        cb_status_kill_process = (CheckBox) findViewById(R.id.cb_status_kill_process);
        final Intent intent = new Intent(TaskManagerSettingActivity.this, KillProcessService.class);
        cb_status_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SystemInfoUtils.isServiceRunning(TaskManagerSettingActivity.this, "com.ycb.mobliesafe.service.KillProcessService")){
            cb_status_kill_process.setChecked(true);
        }else{
            cb_status_kill_process.setChecked(false);
        }

    }
}
