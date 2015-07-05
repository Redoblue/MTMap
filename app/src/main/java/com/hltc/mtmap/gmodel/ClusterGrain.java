package com.hltc.mtmap.gmodel;

import com.amap.api.maps.model.LatLng;
import com.amp.apis.libc.ClusterItem;

/**
 * Created by redoblue on 15-7-5.
 */
public class ClusterGrain implements ClusterItem {

    public long grainId;
    public long userId;
    public String cateId;
    public String nickName;
    public String remark;
    public String text;
    public String userPortrait;
    public ClusterSite site;

    @Override
    public long getItemId() {
        return grainId;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(site.lat, site.lon);
    }

    @Override
    public String getPicUrl() {
        return userPortrait;
    }

    public static class ClusterSite {
        public String siteId;
        public String source;
        public String address;
        public String name;
        public String phone;
        public String gtype;
        public String mtype;
        public double lat;
        public double lon;
    }
}
