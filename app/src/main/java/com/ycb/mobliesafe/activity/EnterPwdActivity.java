package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.utils.UIUtils;

/**
 * Created by where on 2016/4/11.
 */
public class EnterPwdActivity extends Activity implements View.OnClickListener {

    private Button btn_0;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_clear_all;
    private Button btn_delete;
    private Button btn_ok;
    private EditText et_pwd;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        Intent intent = getIntent();
        if(intent != null){
            packageName = intent.getStringExtra("packageName");
        }
        setContentView(R.layout.activity_set_pwd);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        //隐藏当前的键盘
        et_pwd.setInputType(InputType.TYPE_NULL);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_clear_all = (Button) findViewById(R.id.btn_clear_all);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_ok.setOnClickListener(this);
        //删除输入框
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                if (TextUtils.isEmpty(str)){
                    return;
                }else{
                    et_pwd.setText(str.substring(0, str.length() - 1));
                }
            }
        });
        //清空输入框
        btn_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_pwd.setText("");
            }
        });
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "0");
            }
        });
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "1");
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "2");
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "3");
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "4");
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "5");
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "6");
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "7");
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "8");
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + "9");
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                String result = et_pwd.getText().toString().trim();
                if("123".equals(result)){
                    //如果密码正确，发送广播，停止保护
                    Intent intent = new Intent();
                    //发哦是那个广播，停止保护
                    intent.setAction("com.ycb.mobliesafe.stopprotect");
                    //跟狗说停止保护
                    intent.putExtra("packageName",packageName);
                    sendBroadcast(intent);
                    finish();

                }else{
                    UIUtils.showToast(this,"密码错误");
                }
                break;
        }
    }
    //监听当前页面的后退键
    // <intent-filter>
    // <action android:name="android.intent.action.MAIN" />
    // <category android:name="android.intent.category.HOME" />
    // <category android:name="android.intent.category.DEFAULT" />
    // <category android:name="android.intent.category.MONKEY"/>
    // </intent-filter>

    @Override
    public void onBackPressed() {
        //当用户输入后退键的时候，我们进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
}
