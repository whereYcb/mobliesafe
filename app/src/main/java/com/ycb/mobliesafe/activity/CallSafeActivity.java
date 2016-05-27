package com.ycb.mobliesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import org.w3c.dom.Text;

import java.util.List;

public class CallSafeActivity extends Activity {

    private ListView list_view;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pb;
    int mCurrentPage = 0;
    int mPageSize = 20;
    private TextView tv_page_number;
    private int totalNumber;
    private EditText et_page_number;
    private BlackNumberDao dao;
    private CallSafeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ll_pb.setVisibility(View.INVISIBLE);

            tv_page_number.setText(mCurrentPage + "/" + totalNumber / mPageSize);
            adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
            list_view.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {

                dao = new BlackNumberDao(CallSafeActivity.this);
//                blackNumberInfos = dao.findAll();
                totalNumber = dao.getTotalNumber();
                blackNumberInfos = dao.findPar(mCurrentPage, mPageSize);
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        //显示加载
        ll_pb.setVisibility(View.VISIBLE);
        list_view = (ListView) findViewById(R.id.list_view);
        tv_page_number = (TextView) findViewById(R.id.tv_page_number);
        et_page_number = (EditText) findViewById(R.id.et_page_number);
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }

        @Override
        public View getView(int i, View converView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (converView == null) {
                converView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
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
                        Toast.makeText(CallSafeActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(CallSafeActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
     * 上一页
     * @param view
     */
    public void prePage(View view) {
        if (mCurrentPage <= 0){
            Toast.makeText(CallSafeActivity.this,"已经是第一页了",Toast.LENGTH_SHORT).show();
        }else{
            mCurrentPage--;
            initData();
        }
    }

    /**
     * 下一页
     * @param view
     */
    public void nextPage(View view) {
        if (mCurrentPage >( totalNumber / mPageSize - 1)){
            Toast.makeText(CallSafeActivity.this,"已经是最后一页了",Toast.LENGTH_SHORT).show();
        }else{
            mCurrentPage++;
            initData();
        }
    }

    /**
     * 跳转
     * @param view
     */
    public void jumpPage(View view) {
        String pageNumber = et_page_number.getText().toString().trim();
        if(TextUtils.isEmpty(pageNumber)){
            Toast.makeText(this,"请输入正确页码",Toast.LENGTH_SHORT).show();
        }else{
            int number = Integer.parseInt(pageNumber);
            if(number >= 0 && number < totalNumber / mPageSize){
                mCurrentPage = number;
                initData();
            }else{
                Toast.makeText(this,"请输入正确页码",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
