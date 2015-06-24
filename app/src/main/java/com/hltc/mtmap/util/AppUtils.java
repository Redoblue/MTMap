package com.hltc.mtmap.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;

import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.bean.ContactInfo;
import com.hltc.mtmap.bean.LocalUserInfo;

import android.provider.ContactsContract.CommonDataKinds.*;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redoblue on 2015/4/20.
 */
public class AppUtils {

    // 联系人显示名称
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    // 电话号码
    private static final int PHONES_NUMBER_INDEX = 1;
    private static final String[] PHONES_PROJECTION = new String[]{Phone.DISPLAY_NAME, Phone.NUMBER};


    public static boolean isFirstTimeToUse(Context context) {
        File file = new File(context.getDir(AppConfig.APP_CONFIG, Context.MODE_PRIVATE).getPath()
                + File.separator + AppConfig.APP_CONFIG);
        if (!file.exists()) {
            return true;
        } else {
            String value = AppConfig.getAppConfig(context).get(AppConfig.CONF_FIRST_USE);
            return value == null || value.equals("true");
        }
    }

    public static boolean isSignedIn(Context context) {
        return false;
        //是否登陆
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取联系人信息
     *
     * @param context
     * @return
     */
    public static List<ContactInfo> getContacts(Context context) {
        List<ContactInfo> contactInfos = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(PHONES_NUMBER_INDEX);
                if (StringUtils.isEmpty(number)) {
                    continue;
                }
                String name = cursor.getString(PHONES_DISPLAY_NAME_INDEX);
                ContactInfo contactInfo = new ContactInfo();
                contactInfo.setDisplayName(name);
                contactInfo.setNumber(number);
                contactInfos.add(contactInfo);
            }
            cursor.close();
        }
        return contactInfos;
    }
}