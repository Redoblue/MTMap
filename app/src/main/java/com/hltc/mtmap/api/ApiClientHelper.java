package com.hltc.mtmap.api;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hltc.mtmap.app.MyApplication;

/**
 * Created by redoblue on 15-7-7.
 */
public class ApiClientHelper {
    /**
     * 获得请求的服务端数据的userAgent
     *
     * @return
     */
    public static String getUserAgent() {
        StringBuilder ua = new StringBuilder("OSChina.NET");
        ua.append('/' + getPackageInfo().versionName + '_'
                + getPackageInfo().versionCode);// app版本信息
        ua.append("/Android");// 手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
        ua.append("/" + android.os.Build.MODEL); // 手机型号
        return ua.toString();
    }

    /**
     * 获取App安装包信息
     */
    public static PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = MyApplication.getContext().getPackageManager()
                    .getPackageInfo(MyApplication.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

}
