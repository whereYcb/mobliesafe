package com.ycb.mobliesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by where on 2016/3/12.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    public List<T> lists;
    public Context mContext;

    public MyBaseAdapter(List<T> lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
    }

    public MyBaseAdapter() {
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
