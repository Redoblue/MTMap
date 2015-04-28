package com.hltc.mtmap.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hltc.mtmap.app.AppConfig;

import java.io.File;

/**
 * Created by Redoblue on 2015/4/20.
 */
public class AppUtils {

    public static boolean isFirstTimeToUse(Context context) {
        File file = new File(context.getDir(AppConfig.APP_CONFIG, context.MODE_PRIVATE).getPath()
                + File.separator + AppConfig.APP_CONFIG);
        if (!file.exists()) {
            return true;
        } else {
            String value = AppConfig.getAppConfig(context).get(AppConfig.CONF_FIRST_USE);
            if (value == null || value.equals("true")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isSignedIn(Context context) {
        return false;
        //是否登陆
    }

    /**
     * 判断网络是否连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}