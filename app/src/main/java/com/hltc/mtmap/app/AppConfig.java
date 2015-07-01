package com.hltc.mtmap.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.bean.MapInfo;
import com.hltc.mtmap.util.StringUtils;

import java.io.File;

/**
 * Created by Redoblue on 2015/4/11.
 */
public class AppConfig {

    public static final String APP_NAME = "MTMap";
    public static final String APP_DB_NAME = "crop_map";
    //    public static final String CONFIG = "config";
    public static final String CONFIG_USER = "mtmap_user_configuration";
    public static final String CONFIG_APP = "mtmap_app_configuration";
    public static final String CONFIG_MAP = "last_know_location";
    public static final String CONF_FIRST_USE = "first_use";
    public static final String CONF_USER_TOKEN = "user_token";
    public static final String CONF_USER_TMP_TOKEN = "user_token_tmp";
    public static final String CONF_USR_USER_ID = "user_userId";
    public static final String CONF_USER_USERNAME = "user_userName";
    public static final String CONF_USER_IS_LOGIN = "user_isLogin";
    public static final String CONF_USR_NICK_NAME = "user_nickName";
    public static final String CONF_USR_CREATE_TIME = "user_createTime";
    public static final String CONF_USR_PORTRAIT = "user_portrait";
    public static final String CONF_USR_PORTRAIT_SMALL = "user_portraitSmall";
    public static final String CONF_USR_PHONE = "user_phone";
    public static final String CONF_USR_COVER_IMG = "user_coverImg";
    //map
    public static final String CONF_MAP_LAST_KNOW_LAT = "last_know_lat";
    public static final String CONF_MAP_LAST_KNOW_LNG = "last_know_lng";
    public static final String CONF_MAP_CITY_CODE = "cityCode";
    public static final String CONF_MAP_PROVINCE = "province";
    public static final String CONF_MAP_AD_CODE = "adCode";
    public static final String CONF_MAP_DISTRICT = "district";
    public static final String CONF_MAP_CITY = "city";

    public static final String DEFAULT_APP_ROOT_PATH =
            Environment.getExternalStorageDirectory() + File.separator + APP_NAME + File.separator;
    private static AppConfig config;
    private Context mContext;
    private LocalUserInfo mUserInfo;

    public static AppConfig getAppConfig(Context context) {
        if (config == null) {
            config = new AppConfig();
            config.mContext = context;
        }
        return config;
    }

    /**
     * ******************************* Token ******************************
     */
    public String getConfToken() {
        return get(CONFIG_USER, CONF_USER_TOKEN);
    }

    public void setConfToken(String token) {
        set(CONFIG_USER, CONF_USER_TOKEN, token);
    }

    public String getConfTmpToken() {
        return get(CONFIG_USER, CONF_USER_TMP_TOKEN);
    }

    public void setConfTmpToken(String token) {
        set(CONFIG_USER, CONF_USER_TMP_TOKEN, token);
    }

    /**
     * ****************************** LocalUserInfo ****************************
     */

    public long getConfUsrUserId() {
        return StringUtils.toLong(get(CONFIG_USER, CONF_USR_USER_ID));
    }

    public void setConfUsrUserId(long id) {
        set(CONFIG_USER, CONF_USR_USER_ID, String.valueOf(id));
    }

    public String getConfUserUsername() {
        return get(CONFIG_USER, CONF_USER_USERNAME);
    }

    public void setConfUserUsername(String arg) {
        set(CONFIG_USER, CONF_USER_USERNAME, arg);
    }

    public boolean getConfUserIsLogin() {
        return StringUtils.toBool(get(CONFIG_USER, CONF_USER_IS_LOGIN));
    }

    public void setConfUserIsLogin(boolean arg) {
        set(CONFIG_USER, CONF_USER_IS_LOGIN, String.valueOf(arg));
    }

    public String getConfUsrNickName() {
        return get(CONFIG_USER, CONF_USR_NICK_NAME);
    }

    public void setConfUsrNickName(String nickname) {
        set(CONFIG_USER, CONF_USR_NICK_NAME, nickname);
    }

    public String getConfUsrPhone() {
        return get(CONFIG_USER, CONF_USR_PHONE);
    }

    public void setConfUsrPhone(String phone) {
        set(CONFIG_USER, CONF_USR_PHONE, phone);
    }

    public String getConfUsrCreateTime() {
        return get(CONFIG_USER, CONF_USR_CREATE_TIME);
    }

    public void setConfUsrCreateTime(String createTime) {
        set(CONFIG_USER, CONF_USR_CREATE_TIME, createTime);
    }

    public String getConfUsrPortrait() {
        return get(CONFIG_USER, CONF_USR_PORTRAIT);
    }

    public void setConfUsrPortrait(String avatarURL) {
        set(CONFIG_USER, CONF_USR_PORTRAIT, avatarURL);
    }

    public String getConfUsrPortraitSmall() {
        return get(CONFIG_USER, CONF_USR_PORTRAIT_SMALL);
    }

    public void setConfUsrPortraitSmall(String rawAvatarURL) {
        set(CONFIG_USER, CONF_USR_PORTRAIT_SMALL, rawAvatarURL);
    }

    public String getConfUsrCoverImg() {
        return get(CONFIG_USER, CONF_USR_COVER_IMG);
    }

    public void setConfUsrCoverImg(String coverURL) {
        set(CONFIG_USER, CONF_USR_COVER_IMG, coverURL);
    }

    public LocalUserInfo getUserInfo() {
        if (mUserInfo == null && !StringUtils.isEmpty(String.valueOf(getConfUsrUserId()))) {
            mUserInfo = new LocalUserInfo();
            mUserInfo.setUserId(getConfUsrUserId());
            mUserInfo.setUserName(getConfUserUsername());
            mUserInfo.setIsLogin(getConfUserIsLogin());
            mUserInfo.setNickName(getConfUsrNickName());
            mUserInfo.setPhone(getConfUsrPhone());
            mUserInfo.setCreateTime(getConfUsrCreateTime());
            mUserInfo.setPortrait(getConfUsrPortrait());
            mUserInfo.setPortraitSmall(getConfUsrPortraitSmall());
            mUserInfo.setCoverImg(getConfUsrCoverImg());
        }
        return mUserInfo;
    }

    public void setUserInfo(LocalUserInfo userInfo) {
        mUserInfo = userInfo;

        this.setConfUsrUserId(mUserInfo.getUserId());
        this.setConfUserUsername(mUserInfo.getUserName());
        this.setConfUserIsLogin(mUserInfo.getIsLogin());
        this.setConfUsrNickName(mUserInfo.getNickName());
        this.setConfUsrPhone(mUserInfo.getPhone());
        this.setConfUsrCreateTime(mUserInfo.getCreateTime());
        this.setConfUsrPortrait(mUserInfo.getPortrait());
        this.setConfUsrPortraitSmall(mUserInfo.getPortraitSmall());
        this.setConfUsrCoverImg(mUserInfo.getCoverImg());
    }

    public MapInfo getMapInfo() {
        MapInfo info = new MapInfo();
        info.setLatitude(get(CONFIG_MAP, CONF_MAP_LAST_KNOW_LAT));
        info.setLongitude(get(CONFIG_MAP, CONF_MAP_LAST_KNOW_LNG));
        info.setCityCode(get(CONFIG_MAP, CONF_MAP_CITY_CODE));
        info.setProvince(get(CONFIG_MAP, CONF_MAP_PROVINCE));
        info.setAdCode(get(CONFIG_MAP, CONF_MAP_AD_CODE));
        info.setDistrict(get(CONFIG_MAP, CONF_MAP_DISTRICT));
        info.setCity(get(CONFIG_MAP, CONF_MAP_CITY));
        return info;
    }

    /**
     * ************************** Operating funcs **********************
     */

/*    public String get(String cache, String key) {
        Properties properties = getProperties(cache);
        return (properties != null) ? properties.getProperty(key) : null;
    }

    public void set(String cache, Properties properties) {
        Properties props = getProperties(cache);
        props.putAll(properties);
        setProperties(cache, props);
    }

    public void set(String cache, String key, String value) {
        Properties props = getProperties(cache);
        props.setProperty(key, value);
        setProperties(cache, props);
    }

    public void remove(String cache, String... key) {
        Properties props = getProperties(cache);
        for (String k : key) {
            props.remove(k);
        }
        setProperties(cache, props);
    }

    public void removeConfig(String config) {
        File dir = mContext.getDir(CONFIG, Context.MODE_PRIVATE);
        File file = new File(dir.getPath(), config);
        if (file.exists()) {
            file.delete();
        }
    }

    public Properties getProperties(String cache) {
        FileInputStream fis = null;
        Properties properties = new Properties();
        try {
            // 读取app_config目录下的config
            File dir = mContext.getDir(CONFIG, Context.MODE_PRIVATE);
            LogUtils.d(dir.toString());///
            fis = new FileInputStream(dir.getPath() + File.separator + cache);
            LogUtils.d(fis.toString());///
            properties.load(fis);
        } catch (IOException e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return properties;
    }

    private void setProperties(String cache, Properties properties) {
        FileOutputStream fos = null;
        try {
            // 把config建在app-config目录下
            File dir = mContext.getDir(CONFIG, Context.MODE_PRIVATE);
            File file = new File(dir.getPath(), cache);///
            fos = new FileOutputStream(file);
            properties.store(fos, null);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    /* SharedPreference */
    public void set(String config, String key, String value) {
        SharedPreferences preferences = mContext.getSharedPreferences(config, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get(String config, String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(config, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }
}