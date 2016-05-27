package com.ycb.mobliesafe.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ycb.mobliesafe.R;
import com.ycb.mobliesafe.bean.AppInfo;
import com.ycb.mobliesafe.db.dao.AppLockDao;
import com.ycb.mobliesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by where on 2016/4/10.
 */
public class UnLockFragment extends Fragment {

    private ListView list_view;
    private TextView tv_unLock;
    private List<AppInfo> appinfos;
    private AppLockDao dao;
    private List<AppInfo> unLockLists;
    private UnLockAdapter adapter;

    //类似setContentView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_unlock_fragment, null);
        tv_unLock = (TextView) view.findViewById(R.id.tv_unLock);
        list_view = (ListView) view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appinfos = AppInfos.getAppinfos(getActivity());
        //初始化一个没有加锁的集合
        unLockLists = new ArrayList<AppInfo>();
        //获取到程序锁的dao
        dao = new AppLockDao(getActivity());
        for (AppInfo appInfo : appinfos) {
            //判断当前应用是否在程序锁的数据库里面
            if (dao.find(appInfo.getApkPackageName())) {

            } else {
                //查询不到就说明没有在程序锁的数据库中
                unLockLists.add(appInfo);
            }
        }
        adapter = new UnLockAdapter();
        list_view.setAdapter(adapter);
        

    }

    public class UnLockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            tv_unLock.setText("未加锁(" + unLockLists.size() + ")个");
            return unLockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final View view ;
            final AppInfo appInfo;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_unlock, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            //获取到当前对象
            appInfo = unLockLists.get(position);
            holder.iv_icon.setImageDrawable(unLockLists.get(position).getIcon());
            holder.tv_name.setText(unLockLists.get(position).getApkName());
            //把程序添加到数据库里面
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //初始化一个位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    //设置动画时间
                    translateAnimation.setDuration(3000);
                    //开始动画
                    view.startAnimation(translateAnimation);
                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(3000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //添加到数据库里面
                                    dao.add(appInfo.getApkPackageName());
                                    //从当前的页面移除对象
                                    unLockLists.remove(position);
                                    //刷新界面
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();

                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
