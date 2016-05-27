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
public class LockFragment extends Fragment {

    private TextView tv_lock;
    private ListView list_view;
    private List<AppInfo> lockLists;
    private AppLockDao dao;
    private LockAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_lock_fragment, null);
        tv_lock = (TextView) view.findViewById(R.id.tv_lock);
        list_view = (ListView) view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取到手机上的所有应用程序
        List<AppInfo> appInfos = AppInfos.getAppinfos(getActivity());
        dao = new AppLockDao(getActivity());
        lockLists = new ArrayList<AppInfo>();
        for (AppInfo appInfo:appInfos) {
            if (dao.find(appInfo.getApkPackageName())){
                //表示加锁
                lockLists.add(appInfo);
            }else{
                //表示未加锁
            }
        }

        adapter = new LockAdapter();
        list_view.setAdapter(adapter);
    }
    private class LockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            tv_lock.setText("加锁(" + lockLists.size() + ")个");
            return lockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return lockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if (convertView == null) {
               holder = new ViewHolder();
                view = View.inflate(getActivity(), R.layout.item_lock, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_lock = (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            final AppInfo appInfo = lockLists.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());

            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(5000);
                    view.startAnimation(translateAnimation);
                    new Thread() {
                        @Override
                        public void run() {
                            SystemClock.sleep(5000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dao.delete(appInfo.getApkPackageName());
                                    lockLists.remove(position);
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
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }
}
