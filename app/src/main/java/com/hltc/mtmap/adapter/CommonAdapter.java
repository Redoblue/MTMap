package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Redoblue on 2015/4/21.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mList;
    protected int mViewId;
    protected LayoutInflater inflater;

    public CommonAdapter(Context context, List<T> data, int viewId) {
        this.mContext = context;
        this.mList = data;
        this.mViewId = viewId;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.get(
                mContext, convertView, parent, mViewId, position);
        convert(holder, getItem(position));
        return holder.getmConvertView();
    }

    public abstract void convert(CommonViewHolder holder, T t);
}
