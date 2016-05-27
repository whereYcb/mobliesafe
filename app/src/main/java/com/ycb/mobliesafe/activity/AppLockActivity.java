package com.ycb.mobliesafe.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.fragment.LockFragment;
import com.ycb.mobliesafe.fragment.UnLockFragment;

import java.util.concurrent.locks.Lock;

public class AppLockActivity extends FragmentActivity implements View.OnClickListener {

    private TextView tv_unLock;
    private TextView tv_lock;
    private FrameLayout fl_content;
    private FragmentManager fragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

    }

    private void initUI() {
        setContentView(R.layout.activity_app_lock);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_unLock = (TextView) findViewById(R.id.tv_unLock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_lock.setOnClickListener(this);
        tv_unLock.setOnClickListener(this);
        //获取到frament的管理者
        fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();
        unLockFragment = new UnLockFragment();
        lockFragment = new LockFragment();
        mTransaction.replace(R.id.fl_content, unLockFragment).commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (v.getId()){
            case R.id.tv_unLock:
                tv_unLock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                ft.replace(R.id.fl_content,unLockFragment);
                System.out.println("切换到未加锁");
                break;
            case R.id.tv_lock:
                tv_unLock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                ft.replace(R.id.fl_content, lockFragment);
                System.out.println("切换到已加锁");
                break;
        }
        ft.commit();
    }
}
