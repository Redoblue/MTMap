package com.hltc.mtmap.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.Stack;

/**
 * 应用程序Activity管理类
 *
 * @author Redoblue          Created on 2012-3-21
 */

public class AppManager {

    private static Stack<Activity> stack;
    private static AppManager manager;

    private AppManager() {
    }

    /**
     * Gets app manager.
     *
     * @return the app manager
     */
    public static AppManager getAppManager() {
        if (manager == null) {
            manager = new AppManager();
        }
        return manager;
    }

    /**
     * Add activity.
     *
     * @param activity the activity
     */
    public void addActivity(Activity activity) {
        if (stack == null) {
            stack = new Stack<Activity>();
        }
        stack.add(activity);
    }

    /**
     * Current activity.
     *
     * @return the activity
     */
    public Activity currentActivity() {
        Activity activity = stack.lastElement();
        return activity;
    }

    /**
     * Finish activity.
     *
     * @param activity the activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            stack.remove(activity);
            activity.finish();
//            activity = null;
        }
    }

    /**
     * Finish activity.
     *
     * @param c the c
     */
    public void finishActivity(Class<?> c) {
        for (Activity a : stack) {
            if (a.getClass().equals(c)) {
                finishActivity(a);
            }
        }
    }

    /**
     * Finish all activities.
     */
    public void finishAllActivities() {
        for (Activity activity : stack) {
            if (null != activity) {
                activity.finish();
            }
        }
        stack.clear();
    }

    /**
     * Exit app.
     *
     * @param context the context
     */
    public void exitApp(Context context) {
        try {
            finishAllActivities();
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            am.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
