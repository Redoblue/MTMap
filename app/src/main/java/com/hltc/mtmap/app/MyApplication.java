package com.hltc.mtmap.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Redoblue on 2015/4/11.
 */
public class MyApplication extends Application {

    public static String signInStatus = ""; // "00", "01", "10", "11" 第一位: 1 在线 0 离线  第二位： 1 登录 0 未登录
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        //用户身份状态检测
        if (AppUtils.isNetworkConnected(mContext)) {
            if (!StringUtils.isEmpty(AppConfig.getAppConfig(mContext).getConfToken())) {
                httpLoginByToken();
            } else {
                signInStatus = "10";
            }
        } else {
            if (!StringUtils.isEmpty(AppConfig.getAppConfig(mContext).getConfToken())) {
                signInStatus = "01";
            } else {
                signInStatus = "00";
            }
        }

        initImageLoader();
    }

    private void initImageLoader() {
        //使用默认的ImageLoader配置参数
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
//        ImageLoader.getInstance().init(configuration);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(mContext)
//                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
//                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(mContext, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(config);
    }

    private void httpLoginByToken() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USR_ID, AppConfig.getAppConfig(mContext).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(mContext).getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getLoginByTokenUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                LocalUserInfo userInfo = new LocalUserInfo();
                                userInfo.setUserId(data.getLong(ApiUtils.KEY_USR_ID));
                                userInfo.setUserName(data.getString(ApiUtils.KEY_USR_NAME));
                                userInfo.setIsLogin(StringUtils.toBool(data.getString(ApiUtils.KEY_USR_IS_LOG_IN)));
                                userInfo.setNickName(data.getString(ApiUtils.KEY_USR_NICKNAME));
                                userInfo.setPhone(data.getString(ApiUtils.KEY_USR_PHONE));
                                userInfo.setCreateTime(data.getString(ApiUtils.KEY_USR_CREATE_TIME));
                                userInfo.setPortrait(data.getString(ApiUtils.KEY_USR_PORTRAIT));
                                userInfo.setPortraitSmall(data.getString(ApiUtils.KEY_USR_PORTRAIT_SMALL));
                                userInfo.setCoverImg(data.getString(ApiUtils.KEY_USR_COVER_IMG));
                                AppConfig.getAppConfig(mContext).setUserInfo(userInfo);

                                Log.d("MyApplication", userInfo.toString());

                                signInStatus = "11";
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    signInStatus = "10";
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        signInStatus = "10";
                    }
                });
    }
}
