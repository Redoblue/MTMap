package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hltc.mtmap.MFriend;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.FriendListActivity;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.gmodel.Friend;
import com.hltc.mtmap.util.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
    private List<MFriend> list = new ArrayList<>();
    private Context mContext;

    public FriendListAdapter(Context mContext, List<MFriend> objects) {
        this.mContext = mContext;
        this.list = objects;

        //添加最前面的文件夹
        MFriend folder = new Friend();
        folder.setRemark("新的朋友");
        folder.setIsFolder(true);
        folder.setFirstCharacter("@");
        this.list.add(FriendListActivity.FOLDER_NEW_FRIEND, folder);
    }

    public int getCount() {
        return this.list.size();
    }

    public MFriend getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        if (getItem(position).getIsFolder()) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list_folder, null);
            ImageView portrait = (ImageView) view.findViewById(R.id.civ_item_friend_list_portrait);
            portrait.setImageResource(R.drawable.profile_header_pic);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list_person, null);
            CircleImageView portrait = (CircleImageView) view.findViewById(R.id.civ_item_friend_list_portrait);
            ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), portrait, MyApplication.displayImageOptions);
        }
        TextView name = (TextView) view.findViewById(R.id.tv_item_friend_list_name);
        TextView letter = (TextView) view.findViewById(R.id.tv_item_friend_list_catalog);

        if (getItem(position).getIsFolder()) {
            letter.setVisibility(View.GONE);
        } else {
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                letter.setVisibility(View.VISIBLE);
                letter.setText(getItem(position).getFirstCharacter().substring(0, 1));
            } else {
                letter.setVisibility(View.GONE);
            }
        }

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