package com.hltc.mtmap.bean;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hltc.mtmap.app.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by redoblue on 15-7-4.
 */
public class GrainDetailPhotoAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mList;

    public GrainDetailPhotoAdapter(Context context, List<String> objects) {
        mContext = context;
        mList = objects;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = new ImageView(mContext);
//        iv.setImageDrawable(Drawable.createFromPath(mList.get(position)));
        ImageLoader.getInstance().displayImage(mList.get(position), iv, MyApplication.displayImageOptions);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(iv, position);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
