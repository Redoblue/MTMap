package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.SwipeGrainItem;
import com.hltc.mtmap.fragment.GrainFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-6-30.
 */
public class GrainSwipeViewAdapter extends ArrayAdapter<SwipeGrainItem> {

    private List<SwipeGrainItem> mList;
    private Context mContext;

    public GrainSwipeViewAdapter(Context context, int resource, List<SwipeGrainItem> objects) {
        super(context, resource, objects);
        this.mList = objects;
        this.mContext = context;
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
            holder.image = (ImageView) convertView.findViewById(R.id.iv_item_grain_card_image);
            holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_item_grain_card_portrait);
            holder.comment = (TextView) convertView.findViewById(R.id.tv_item_grain_card_comment);
            holder.cover = (ImageView) convertView.findViewById(R.id.iv_item_grain_card_cover);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (getItem(position).getStatus()) {
            case GrainFragment.SWIPE_GRAIN:
                holder.image.setVisibility(View.VISIBLE);
                holder.portrait.setVisibility(View.VISIBLE);
                holder.comment.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(getItem(position).getImage(), holder.image);
                ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), holder.portrait);
                holder.comment.setText(getItem(position).getText());
                break;
            case GrainFragment.SWIPE_NOMORE:
                holder.image.setVisibility(View.INVISIBLE);
                holder.portrait.setVisibility(View.INVISIBLE);
                holder.comment.setVisibility(View.INVISIBLE);
                holder.cover.setImageResource(R.drawable.pic_grain_card_nomore);
                break;
            case GrainFragment.SWIPE_OFFLINE:
                holder.image.setVisibility(View.INVISIBLE);
                holder.portrait.setVisibility(View.INVISIBLE);
                holder.comment.setVisibility(View.INVISIBLE);
                holder.cover.setImageResource(R.drawable.pic_grain_card_404);
                break;
        }

        /*ImageLoader.getInstance().displayImage(getItem(position).getImage(), holder.image);
        ImageLoader.getInstance().displayImage(getItem(position).getPortrait(), holder.portrait);
        holder.comment.setText(getItem(position).getText());*/

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        CircleImageView portrait;
        TextView comment;
        ImageView cover;
    }
}
