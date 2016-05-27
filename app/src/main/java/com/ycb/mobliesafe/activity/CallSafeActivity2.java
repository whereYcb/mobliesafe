package com.ycb.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.adapter.MyBaseAdapter;
import com.ycb.mobliesafe.bean.BlackNumberInfo;
import com.ycb.mobliesafe.db.dao.BlackNumberDao;

import java.util.List;

public class CallSafeActivity2 extends Activity {

    private ListView list_view;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pb;

    private BlackNumberDao dao;
    private CallSafeAdapter adapter;

    private int mStartIndex = 0;
    private int maxCount = 20;
    private int totalNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ll_pb.setVisibility(View.INVISIBLE);
            if(adapter == null) {
                adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity2.this);
                list_view.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void initData() {
        dao = new BlackNumberDao(CallSafeActivity2.this);
        totalNumber = dao.getTotalNumber();
        new Thread() {
            @Override
            public void run() {
                //分批加载数据
                if (blackNumberInfos == null){
                    blackNumberInfos = dao.findPar2(mStartIndex, maxCount);
                }else{
                    //把后面的数据追加到blackNumberInfos里面,防止黑名单被覆盖
                    blackNumberInfos.addAll(dao.findPar2(mStartIndex,maxCount));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        //显示加载
        ll_pb.setVisibility(View.VISIBLE);
        list_view = (ListView) findViewById(R.id.list_view);
        //设立listView的滚动监听
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            //状态改变时回掉的方法
            // AbsListView.OnScrollListener.SCROLL_STATE_IDLE:闲置状态
            // AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:手指触摸的时候
            // AbsListView.OnScrollListener.SCROLL_STATE_FLING:惯性
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = list_view.getLastVisiblePosition();
                        System.out.println(lastVisiblePosition);
                        if (lastVisiblePosition == blackNumberInfos.size() - 1) {
                            mStartIndex = maxCount + mStartIndex;
                            if (lastVisiblePosition > totalNumber) {
                                Toast.makeText(CallSafeActivity2.this, "已经没有数据了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            initData();
                        }
                        break;
                }
            }
            //listView滚动时候调用的方法
            //实时调用,当我们手指触摸到屏幕的时候就调用
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }

        @Override
        public View getView(int i, View converView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (converView == null) {
                converView = View.inflate(CallSafeActivity2.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) converView.findViewById(R.id.tv_number);
                holder.tv_mode = (TextView) converView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) converView.findViewById(R.id.iv_delete);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }

            holder.tv_number.setText(blackNumberInfos.get(i).getNumber());
            String mode = blackNumberInfos.get(i).getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("电话拦截+短信");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("短信拦截");
            }
            final BlackNumberInfo info = lists.get(i);
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String number = info.getNumber();
                    boolean resule = dao.delete(number);
                    if(resule){
                        Toast.makeText(CallSafeActivity2.this,"删除成功",Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(CallSafeActivity2.this,"删除失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return converView;
        }
    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }

    /**
     * 添加黑名单
     * @param view
     */
    public void addBlackNumber(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialog_view = View.inflate(this, R.layout.dialog_add_black_number, null);
        final EditText et_number = (EditText) dialog_view.findViewById(R.id.et_number);
        Button btn_cancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) dialog_view.findViewById(R.id.btn_ok);
        final CheckBox cb_phone = (CheckBox) dialog_view.findViewById(R.id.cb_phone);
        final CheckBox cb_sms = (CheckBox) dialog_view.findViewById(R.id.cb_sms);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)){
                    Toast.makeText(CallSafeActivity2.this,"请输入黑名单号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = "";
                if (cb_phone.isChecked() && cb_sms.isChecked()){
                    mode = "1";
                }else if(cb_phone.isChecked()){
                    mode = "2";
                }else if(cb_sms.isChecked()){
                    mode = "3";
                }else {
                    Toast.makeText(CallSafeActivity2.this,"请勾选拦截模式",Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.setNumber(number);
                blackNumberInfo.setMode(mode);
                blackNumberInfos.add(0, blackNumberInfo);
                dao.add(number,mode);
                if (adapter == null){
                    adapter = new CallSafeAdapter(blackNumberInfos,CallSafeActivity2.this);
                    list_view.setAdapter(adapter);
                }else{
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        dialog.setView(dialog_view);
        dialog.show();
    }
}
