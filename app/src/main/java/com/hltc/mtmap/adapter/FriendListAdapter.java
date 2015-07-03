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
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.gmodel.Friend;
import com.hltc.mtmap.orm.model.MTUser;
import com.hltc.mtmap.util.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
    private List<Friend> list = null;
    private Context mContext;

    public FriendListAdapter(Context mContext, List<MTUser> list) {
        this.mContext = mContext;
        this.list = userToFriend(list);
    }

    public void updateListView(List<MTUser> list) {
        this.list = userToFriend(list);
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

    public View getView(final int position, View view, ViewGroup parent) {
        if (getItem(position).isFolder()) {
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

        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            letter.setVisibility(View.VISIBLE);
            letter.setText(getItem(position).getFirstCharacter().substring(0, 1));///
        } else {
            letter.setVisibility(View.GONE);
        }

        if (getItem(position).isFolder())
            letter.setVisibility(View.GONE);

        String remark = getItem(position).getRemark();
        name.setText(StringUtils.isEmpty(remark) ? getItem(position).getNickName() : "手机联系人： " + remark);

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

    private List<Friend> userToFriend(List<MTUser> users) {
        List<Friend> friends = new ArrayList<>();
        for (MTUser m : users) {
            Friend f = new Friend();
            f.setUserId(m.getUserId());
            f.setNickName(m.getNickName());
            f.setPortrait(m.getPortrait());
            f.setFirstCharacter(m.getFirstCharacter());
            f.setRemark(m.getRemark());
            f.setIsFolder(false);
            friends.add(f);
        }
        return friends;
    }

    final static class ViewHolder {
        TextView letter;
        CircleImageView portrait;
        TextView name;
    }
}