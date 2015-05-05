package com.hltc.mtmap.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 一个通用的ViewHolder，用于BaseAdapter的子类中的View的保存
 */
public class CommonViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;
    private int mPosition;

    /**
     * Instantiates a new View holder.
     *
     * @param context  the context
     * @param parent   the parent
     * @param layoutId the layout id
     * @param position the position
     */
    public CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();

        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
    }

    /**
     * Get view holder.
     *
     * @param context     the context
     * @param convertView the convert view
     * @param parent      the parent
     * @param layoutId    the layout id
     * @param position    the position
     * @return the view holder
     */
    public static CommonViewHolder get(Context context, View convertView,
                                       ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId, position);
        } else {
            CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    /**
     * Gets convert view.
     *
     * @return the convert view
     */
    public View getmConvertView() {
        return mConvertView;
    }

    /**
     * Gets view by view id.
     *
     * @param viewId the view id
     * @return the view
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public CommonViewHolder setText(int viewId, String text) {
        ((TextView) getView(viewId)).setText(text);
        return this;
    }

    public CommonViewHolder setImage(int viewId, Drawable drawable) {
//        ImageView imageView = getView(viewId);
//        ImageLoader.getInstance().displayImage(url, imageView);
        ((ImageView) getView(viewId)).setImageDrawable(drawable);
        return this;
    }

    public CommonViewHolder setCircleImage(int viewId, Drawable drawable) {
        ((CircleImageView) getView(viewId)).setImageDrawable(drawable);
        return this;
    }
}
