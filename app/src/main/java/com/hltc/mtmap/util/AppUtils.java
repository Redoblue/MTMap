package com.hltc.mtmap.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.ContactInfo;

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
        if (AppConfig.getAppConfig(context).getConfUsrUserId() > 0)
            return true;
        return false;
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
        List<ContactInfo> cis = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (cursor != null) {
            outer:
            while (cursor.moveToNext()) {
                String number = StringUtils.getFormatedPhone(cursor.getString(PHONES_NUMBER_INDEX));
                if (StringUtils.isEmpty(number)
                        || number.equals(AppConfig.getAppConfig(MyApplication.getContext()).getConfUsrPhone())) {
                    continue;
                }
                for (ContactInfo c : cis) {
                    if (c.getNumber().equals(number))
                        continue outer;
                }
                String name = cursor.getString(PHONES_DISPLAY_NAME_INDEX);
                ContactInfo ci = new ContactInfo();
                ci.setDisplayName(name);
                ci.setNumber(number);
                cis.add(ci);
            }
            cursor.close();
        }
        return cis;
    }

    public static void showRemindToLoginWindow(View parent) {
// 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater)
                MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.window_remind_login, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        window.showAtLocation(parent, Gravity.VERTICAL_GRAVITY_MASK, 0, 0);

        ImageView iv = (ImageView) view.findViewById(R.id.btn_remind_login);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), StartActivity.class);
                MyApplication.getContext().startActivity(intent);
                AppManager.getAppManager().finishActivity(MainActivity.class);
            }
        });
    }
}