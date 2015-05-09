package com.hltc.mtmap.util;

import android.content.Context;
import com.amap.api.cloud.model.LatLonPoint;
import com.amap.api.maps.model.LatLng;

/**
 * Created by Redoblue on 2015/4/7.
 */
public class AMapUtils {

    public static LatLonPoint convertToLatLonPoint(LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }

    public static LatLng convertToLatlng(LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
