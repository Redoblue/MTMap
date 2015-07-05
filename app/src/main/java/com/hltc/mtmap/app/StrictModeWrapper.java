package com.hltc.mtmap.app;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by redoblue on 15-7-5.
 */
public class StrictModeWrapper {

    public static void init(Context context) {

        try {
            //Android 2.3及以上调用严苛模式
            Class sMode = Class.forName("android.os.StrictMode");
            Method enableDefaults = sMode.getMethod("enableDefaults");
            enableDefaults.invoke(null);
        } catch (Exception e) {
            // StrictMode not supported on this device, punt
            Log.v("StrictMode", "... not supported. Skipping...");
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .penaltyLog()
                .penaltyDialog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

}
