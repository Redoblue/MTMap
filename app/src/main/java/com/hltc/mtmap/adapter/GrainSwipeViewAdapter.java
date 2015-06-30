package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.SwipeGrainItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-6-30.
 */
public class GrainSwipeViewAdapter extends BaseAdapter {

    private List<SwipeGrainItem> mList;
    private Context mContext;

    public GrainSwipeViewAdapter(Context context, List<SwipeGrainItem> objects) {
        mContext = context;
        mList = objects;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SwipeGrainItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grain_card, null);
            holder.image = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_portrait);
            holder.comment = (TextView) convertView.findViewById(R.id.tv_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(getItem(position).getImage(), holder.image);
        ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), holder.portrait);
        holder.comment.setText(getItem(position).getText());

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        CircleImageView portrait;
        TextView comment;
    }
}
