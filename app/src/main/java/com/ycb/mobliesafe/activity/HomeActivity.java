package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.utils.MD5Utils;

public class HomeActivity extends Activity {
    private GridView gvHome;
    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};
    private SharedPreferences mPerf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterhome);
        mPerf = getSharedPreferences("config", MODE_PRIVATE);
        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        showPasswordDialog();
                        break;
                    case 1:
                        //通讯卫士
                        startActivity(new Intent(HomeActivity.this, CallSafeActivity2.class));
                        break;
                    case 2:
                        //通讯卫士
                        startActivity(new Intent(HomeActivity.this,AppManagerActivity.class));
                        break;
                    case 3:
                        //进程管理
                        startActivity(new Intent(HomeActivity.this,TaskManagerActivity.class));
                        break;
                    case 4:
                        //流量统计
                        startActivity(new Intent(HomeActivity.this,TrafficManagerActivity.class));
                        break;
                    case 5:
                        //进程管理
                        startActivity(new Intent(HomeActivity.this,AntivirusActivity.class));
                        break;
                    case 6:
                        //进程管理
                        startActivity(new Intent(HomeActivity.this,CleanCacheActivity.class));
                        break;
                    case 7:
                        //高级工具
                        startActivity(new Intent(HomeActivity.this, AtoolsActivity.class));
                        break;
                    case 8:
                        //设置中心
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     */
    protected void showPasswordDialog() {
        //判断是否设置过密码
        String savedPassword = mPerf.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            showPasswordInputDialog();
        } else {
            showPasswordSetDialog();
        }
    }

    /**
     * 输入密码弹窗
     */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_input_password, null);
        dialog.setView(view, 0, 0, 0, 0);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String password = etPassword.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mPerf.getString("password", null);
                    if (MD5Utils.encode(password).equals(savedPassword)) {

                        Toast.makeText(HomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 设置密码弹窗
     */
    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_password, null);
        dialog.setView(view, 0, 0, 0, 0);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.et_passwordconfirm);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
                    if (password.equals(passwordConfirm)) {
                        Toast.makeText(HomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        mPerf.edit().putString("password", MD5Utils.encode(password)).commit();
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,
                    R.layout.home_list_item, null);
            ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_item);
            ivItem.setBackgroundResource(mPics[position]);
            tvItem.setText(mItems[position]);
            return view;
        }

    }
}
