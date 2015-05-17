package com.hltc.mtmap.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.bean.TokenInfo;

import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Redoblue on 2015/4/11.
 */
public class AppConfig {

    public static final String APP_NAME = "MTMap";
    public static final String APP_DB_NAME = "crop_map";
    public static final String APP_CONFIG = "config";
    public static final String CONF_FIRST_USE = "first_use";
    public static final String CONF_TOKEN = "token.value";
    public static final String CONF_TOKEN_EXPIRESIN = "token.expires_in";
    public static final String CONF_USR_ID = "user.id";
    public static final String CONF_USR_NICKNAME = "user.nickname";
    public static final String CONF_USR_CREATE_TIME = "user.create_time";
    public static final String CONF_USR_AVATAR_URL = "user.avatar_url";
    public static final String CONF_USR_RAW_AVATAR_URL = "user.raw_avatar_url";
    public static final String CONF_USR_PHONE = "user.phone";
    public static final String CONF_USR_COVER_URL = "user.cover_url";

    public static final String OSS_ROOT = "oss-cn-hangzhou.aliyuncs.com";
    public static final String OSS_BUCKET = "maitianditu";


    public static final String DEFAULT_APP_ROOT_PATH =
            Environment.getExternalStorageDirectory() + File.separator + APP_NAME + File.separator;
    private static AppConfig config;
    private Context mContext;
    private LocalUserInfo mUserInfo;
    private TokenInfo mTokenInfo;

    public static AppConfig getAppConfig(Context context) {
        if (config == null) {
            config = new AppConfig();
            config.mContext = context;
        }
        return config;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getToken() {
        return get(CONF_TOKEN);
    }

    /**
     * *************************** Token ******************************
     */
    public void setToken(String token) {
        set(CONF_TOKEN, token);
    }

    public long getExpiresIn() {
        return StringUtils.toLong(get(CONF_TOKEN_EXPIRESIN));
    }

    public void setExpiresIn(long expiresIn) {
        set(CONF_TOKEN_EXPIRESIN, String.valueOf(expiresIn));
    }

    public TokenInfo getTokenInfo() {
        if (mTokenInfo == null && !StringUtils.isEmpty(getToken())) {
            mTokenInfo = new TokenInfo();
            mTokenInfo.setToken(getToken());
            mTokenInfo.setExpiresIn(getExpiresIn());
        }
        return mTokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        mTokenInfo = tokenInfo;
        this.setToken(mTokenInfo.getToken());
        this.setExpiresIn(mTokenInfo.getExpiresIn());
    }

    /**
     * ****************************** LocalUserInfo ****************************
     */

    public String getUsrId() {
        return get(CONF_USR_ID);
    }

    public void setUsrId(String id) {
        set(CONF_USR_ID, id);
    }

    public String getUsrNickname() {
        return get(CONF_USR_NICKNAME);
    }

    public void setUsrNickname(String nickname) {
        set(CONF_USR_NICKNAME, nickname);
    }

    public String getUsrCreateTime() {
        return get(CONF_USR_CREATE_TIME);
    }

    public void setUsrCreateTime(String createTime) {
        set(CONF_USR_CREATE_TIME, createTime);
    }

    public String getUsrAvatarURL() {
        return get(CONF_USR_AVATAR_URL);
    }

    public void setUsrAvatarURL(String avatarURL) {
        set(CONF_USR_AVATAR_URL, avatarURL);
    }

    public String getUsrRawAvatarURL() {
        return get(CONF_USR_RAW_AVATAR_URL);
    }

    public void setUsrRawAvatarURL(String rawAvatarURL) {
        set(CONF_USR_RAW_AVATAR_URL, rawAvatarURL);
    }

    public String getUsrPhone() {
        return get(CONF_USR_PHONE);
    }

    public void setUsrPhone(String phone) {
        set(CONF_USR_PHONE, phone);
    }

    public String getUsrCoverURL() {
        return get(CONF_USR_COVER_URL);
    }

    public void setUsrCoverURL(String coverURL) {
        set(CONF_USR_COVER_URL, coverURL);
    }

    public LocalUserInfo getUserInfo() {
        if (mUserInfo == null && !StringUtils.isEmpty(String.valueOf(getUsrId()))) {
            mUserInfo = new LocalUserInfo();
            mUserInfo.setId(getUsrId());
            mUserInfo.setNickname(getUsrNickname());
            mUserInfo.setCreateTime(getUsrCreateTime());
            mUserInfo.setAvatarURL(getUsrAvatarURL());
            mUserInfo.setRawAvatarURL(getUsrRawAvatarURL());
            mUserInfo.setPhone(getUsrPhone());
            mUserInfo.setCoverURL(getUsrCoverURL());
        }
        return mUserInfo;
    }

    public void setUserInfo(LocalUserInfo userInfo) {
        mUserInfo = userInfo;

        this.setUsrId(mUserInfo.getId());
        this.setUsrNickname(mUserInfo.getNickname());
        this.setUsrCreateTime(mUserInfo.getCreateTime());
        this.setUsrAvatarURL(mUserInfo.getAvatarURL());
        this.setUsrRawAvatarURL(mUserInfo.getRawAvatarURL());
        this.setUsrPhone(mUserInfo.getPhone());
        this.setUsrCoverURL(mUserInfo.getCoverURL());
    }

    /**
     * ************************** Operating funcs **********************
     */

    public String get(String key) {
        Properties properties = getProperties();
        return (properties != null) ? properties.getProperty(key) : null;
    }

    public void set(Properties properties) {
        Properties props = getProperties();
        props.putAll(properties);
        setProperties(props);
    }

    public void set(String key, String value) {
        Properties props = getProperties();
        props.setProperty(key, value);
        setProperties(props);
    }

    public void remove(String... key) {
        Properties props = getProperties();
        for (String k : key) {
            props.remove(k);
        }
        setProperties(props);
    }

    public Properties getProperties() {
        FileInputStream fis = null;
        Properties properties = new Properties();
        try {
            // 读取app_config目录下的config
            File dir = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            LogUtils.d(dir.toString());///
            fis = new FileInputStream(dir.getPath() + File.separator + APP_CONFIG);
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

    private void setProperties(Properties properties) {
        FileOutputStream fos = null;
        try {
            // 把config建在app-config目录下
            File dir = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File file = new File(dir.getPath(), APP_CONFIG);///
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
    }
}