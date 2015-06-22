package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.SortModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder holder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list, null);
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
            holder.letter.setText(mContent.getLetter());
        } else {
            holder.letter.setVisibility(View.GONE);
        }

        holder.letter.setText(list.get(position).getLetter());
//        ImageLoader.getInstance().displayImage(list.get(position).getPortrait(), holder.portrait);
        holder.portrait.setImageResource(R.drawable.cluster_pic);
        holder.name.setText(this.list.get(position).getName());

        return view;
    }

    public int getSectionForPosition(int position) {
        return list.get(position).getLetter().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getLetter();
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