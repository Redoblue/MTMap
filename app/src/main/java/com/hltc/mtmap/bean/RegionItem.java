package com.hltc.mtmap.bean;

import android.graphics.drawable.Drawable;
import com.amap.api.maps.model.LatLng;
import com.amp.apis.libc.ClusterItem;

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private Drawable mDrawable;

    public RegionItem(LatLng latLng, Drawable drawable) {
        mLatLng = latLng;
        mDrawable = drawable;
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mLatLng;
    }

    @Override
    public Drawable getDrawable() {
        return mDrawable;
    }

}
