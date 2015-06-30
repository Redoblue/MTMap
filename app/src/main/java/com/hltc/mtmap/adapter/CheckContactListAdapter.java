package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.ContactItem;
import com.hltc.mtmap.util.ApiUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-6-28.
 */
public class CheckContactListAdapter extends BaseAdapter {

    private List<ContactItem> list = null;
    private Context mContext;

    public CheckContactListAdapter(Context context, List<ContactItem> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ContactItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_check_contact, parent, false);
            holder = new ViewHolder();
            holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_item_check_contact_portrait);
            holder.name = (TextView) convertView.findViewById(R.id.tv_item_check_contact_name);
            holder.nickName = (TextView) convertView.findViewById(R.id.tv_check_contact_nickname);
            holder.select = (ToggleButton) convertView.findViewById(R.id.tb_item_check_contact_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), holder.portrait);
        holder.name.setText(getItem(position).getName());
        holder.nickName.setText(getItem(position).getNickName());
        holder.select.setChecked(getItem(position).isSelected());
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getItem(position).isSelected()) {
                    list.get(position).setIsSelected(true);
                    // 加好友请求
                    ApiUtils.httpAddFriend(list.get(position));
                } else {
                    list.get(position).setIsSelected(true);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        CircleImageView portrait;
        TextView name;
        TextView nickName;
        ToggleButton select;
    }
}
