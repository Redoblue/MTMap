package com.hltc.mtmap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.FriendStatusActivity;
import com.hltc.mtmap.gmodel.FriendStatus;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-6-29.
 */
public class FriendStatusAdapter extends BaseAdapter {

    private List<FriendStatus> list = null;
    private Context mContext;

    public FriendStatusAdapter(Context mContext, List<FriendStatus> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FriendStatus getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend_status, null);
            holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_item_friend_status_portrait);
            holder.name = (TextView) convertView.findViewById(R.id.tv_item_friend_status_name);
            holder.text = (TextView) convertView.findViewById(R.id.tv_item_friend_status_text);
            holder.status = (TextView) convertView.findViewById(R.id.tv_item_friend_status_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(getItem(position).getUserPortrait(), holder.portrait);
        holder.name.setText(getItem(position).getNickName());
        holder.text.setText(getItem(position).getText());
        String s = getItem(position).getStatus();
        if (s.equals(FriendStatusActivity.STATUS_WAITING)) {
            holder.status.setText("等待验证");
            holder.status.setBackgroundResource(R.color.transparent);
        } else if (s.equals(FriendStatusActivity.STATUS_UNACCEPTED)) {
            holder.status.setText("接受");
            holder.status.setBackgroundColor(Color.BLUE);
        } else if (s.equals(FriendStatusActivity.STATUS_ADDABLE)) {
            holder.status.setText("添加");
            holder.status.setBackgroundColor(Color.RED);
        } else if (s.equals(FriendStatusActivity.STATUS_ACCEPTED)) {
            holder.status.setText("已添加");
            holder.status.setBackgroundResource(R.color.transparent);
        }

        return convertView;
    }

    class ViewHolder {
        CircleImageView portrait;
        TextView name;
        TextView text;
        TextView status;
    }
}
