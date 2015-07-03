package com.hltc.mtmap.bean;

import com.amap.api.maps.model.LatLng;
import com.amp.apis.libc.ClusterItem;

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private String mPicUrl;

    public RegionItem(LatLng latLng, String url) {
        mLatLng = latLng;
        mPicUrl = url;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    @Override
    public String getPicUrl() {
        return mPicUrl;
    }
}
