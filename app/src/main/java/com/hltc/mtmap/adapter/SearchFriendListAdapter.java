package com.hltc.mtmap.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.FriendRequestActivity;
import com.hltc.mtmap.orm.model.MTUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-6-30.
 */
public class SearchFriendListAdapter extends BaseAdapter {

    private List<MTUser> mList;
    private Context mContext;

    public SearchFriendListAdapter(Context context, List<MTUser> objects) {
        mContext = context;
        mList = objects;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public MTUser getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_display_user, null);
            holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_portrait);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.signature = (TextView) convertView.findViewById(R.id.tv_signature);
            holder.select = (ToggleButton) convertView.findViewById(R.id.tb_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), holder.portrait);
        holder.name.setText(getItem(position).getNickName());
        holder.signature.setText(getItem(position).getSignature());
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton tb = (ToggleButton) v;
                if (tb.isChecked()) {
                    Log.d("MT", "unchecked");
                    tb.setChecked(true);
                    //弹出一个框
                    Intent intent = new Intent(mContext, FriendRequestActivity.class);
                    intent.putExtra("toId", getItem(position).getUserId());
                    intent.putExtra("remark", getItem(position).getNickName());
                    mContext.startActivity(intent);
                } else {
                    tb.setChecked(true);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        CircleImageView portrait;
        TextView name;
        TextView signature;
        ToggleButton select;
    }
}
