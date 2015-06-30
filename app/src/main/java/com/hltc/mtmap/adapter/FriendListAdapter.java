package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.FriendListActivity;
import com.hltc.mtmap.gmodel.Friend;
import com.hltc.mtmap.util.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
    private List<Friend> list = null;
    private Context mContext;

    public FriendListAdapter(Context mContext, List<Friend> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void updateListView(List<Friend> list) {
        this.list = list;
        //为每项添加区分符
        for (Friend f : this.list) {
            f.setIsFolder(false);
        }

        //添加最前面的文件夹
        Friend folder = new Friend();
        folder.setRemark("新的朋友");
        folder.setIsFolder(true);
        folder.setFirstCharacter("@");
//        this.list.add(folder);
        this.list.add(FriendListActivity.FOLDER_NEW_FRIEND, folder);

        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Friend getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /*public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list_person, null);
            holder.letter = (TextView) view.findViewById(R.id.tv_item_friend_list_catalog);
            holder.portrait = (CircleImageView) view.findViewById(R.id.civ_item_friend_list_portrait);
            holder.name = (TextView) view.findViewById(R.id.tv_item_friend_list_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            holder.letter.setVisibility(View.VISIBLE);
            holder.letter.setText(getItem(position).getFirstCharacter());
        } else {
            holder.letter.setVisibility(View.GONE);
        }

        holder.letter.setText(list.get(position).getFirstCharacter());
//        ImageLoader.getInstance().displayImage(list.get(position).getPortrait(), holder.portrait);
        holder.portrait.setImageResource(R.drawable.cluster_pic);
        if (StringUtils.isEmpty(getItem(position).getRemark()))
            holder.name.setText(getItem(position).getNickName());
        else holder.name.setText(getItem(position).getRemark());

        return view;
    }*/
    public View getView(final int position, View view, ViewGroup parent) {
        if (getItem(position).isFolder()) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list_folder, null);
            ImageView portrait = (ImageView) view.findViewById(R.id.civ_item_friend_list_portrait);
            portrait.setImageResource(R.drawable.profile_header_pic);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list_person, null);
            CircleImageView portrait = (CircleImageView) view.findViewById(R.id.civ_item_friend_list_portrait);
            ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), portrait);
        }
        TextView name = (TextView) view.findViewById(R.id.tv_item_friend_list_name);
        TextView letter = (TextView) view.findViewById(R.id.tv_item_friend_list_catalog);

        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            letter.setVisibility(View.VISIBLE);
            letter.setText(getItem(position).getFirstCharacter());
        } else {
            letter.setVisibility(View.GONE);
        }

        if (getItem(position).isFolder())
            letter.setVisibility(View.GONE);

        String remark = getItem(position).getRemark();
        name.setText(StringUtils.isEmpty(remark) ? getItem(position).getNickName() : remark);

        return view;
    }

    public int getSectionForPosition(int position) {
        return getItem(position).getFirstCharacter().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getItem(i).getFirstCharacter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    final static class ViewHolder {
        TextView letter;
        CircleImageView portrait;
        TextView name;
    }
}