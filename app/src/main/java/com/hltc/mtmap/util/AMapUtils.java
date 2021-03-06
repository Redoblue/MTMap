package com.hltc.mtmap.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

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

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isLocationProviderAvailable(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network ? true : false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static void toggleGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回两个经纬度点之间的距离，单位：m
     *
     * @param latLng0
     * @param latLng1
     * @return
     */
    public static long calculateDistance(LatLng latLng0, LatLng latLng1) {
        double earthRadius = 6378.137;
        double radLat1 = Math.toRadians(latLng0.latitude);
        double radLat2 = Math.toRadians(latLng1.latitude);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(latLng0.longitude) - Math.toRadians(latLng1.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s *= earthRadius;
//        s = Math.round(s * 10000) / 10000;
        return Math.round(s * 1000);
    }

}
