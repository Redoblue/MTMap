package com.hltc.mtmap.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Redoblue on 2015/4/11.
 */
public class MyApplication extends Application {

    private boolean login = false;    //登录状态
    private long loginUid = 0;    //登录用户的id

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader();
    }

    private void initImageLoader() {
        //使用默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * 初始化用户登录信息,可能会有其他的登录检测方法//TODO
     */
//    public void initLoginInfo() {
//        LocalUserInfo loginUser = getLoginInfo();
//        if (loginUser != null && loginUser.getId() > 0) {
//            this.loginUid = loginUser.getId();
//            this.login = true;
//        } else {
//            this.Logout();
//        }
//    }

//    public LocalUserInfo getLoginInfo() {
//        LocalUserInfo user = new LocalUserInfo();
//        user.setId(StringUtils.toLong(getProperty("user.id")));
//        user.setNickname(getProperty("user.nickname"));
//        user.setPhone(getProperty("user.phone"));
//        user.setPassword(CodeUtils.decode("crop_map", getProperty("user.password")));
//        user.setAvatarURL(getProperty("user.avatarURL"));
//        user.setCoverURL(getProperty("user.coverURL"));
//        user.setRememberMe(StringUtils.toBool(getProperty("user.rememberMe")));
//        //如果有其他信息，可以加上去
//        return user;
//    }
    public void Logout() {
//        ApiClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;
    }

    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }
}
