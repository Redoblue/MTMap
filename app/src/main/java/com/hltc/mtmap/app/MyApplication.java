package com.hltc.mtmap.app;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Redoblue on 2015/4/11.
 */
public class MyApplication extends Application {

    private boolean login = false;    //登录状态
    private long loginUid = 0;    //登录用户的id

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        initImageLoader();
    }

    private void initImageLoader() {
        //使用默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    public static Context getContext() {
        return mContext;
    }
}
