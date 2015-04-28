package com.hltc.mtmap.util;

import android.util.Log;

/**
 * Created by Redoblue on 2015/4/18.
 */
public class LogUtils {

    public static boolean display = true;

    public static void v(Class<?> clazz, String msg) {
        if (display) {
            Log.v(clazz.getSimpleName(), msg + "");
        }
    }

    public static void i(Class<?> clazz, String msg) {
        if (display) {
            Log.i(clazz.getSimpleName(), msg + "");
        }
    }

    public static void d(Class<?> clazz, String msg) {
        if (display) {
            Log.d(clazz.getSimpleName(), msg + "");
        }
    }

    public static void w(Class<?> clazz, String msg) {
        if (display) {
            Log.w(clazz.getSimpleName(), msg + "");
        }
    }

    public static void e(Class<?> clazz, String msg) {
        if (display) {
            Log.e(clazz.getSimpleName(), msg + "");
        }
    }
}
