package com.hltc.mtmap.util;

import android.os.StrictMode;
import android.util.Log;

import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.alibaba.sdk.android.oss.storage.OSSFile;

import java.io.FileNotFoundException;

/**
 * Created by Redoblue on 2015/4/25.
 */
public class ApiUtils {

    public static final String URL_ROOT = "http://120.25.237.104:8080/maitian/v1";

    public static final String URL_REQ_VCODE = "user/register/verify_code.json";
    public static final String URL_VAL_VCODE = "user/register/verify.json";
    public static final String URL_CRE_ACCOUNT = "user/register/new_user.json";
    public static final String URL_SIG_IN = "user/login/login.json";
    public static final String URL_PUB_GRAIN = "grain/publish.json";
    public static final String URL_UPD_NICKNAME = "user/settings/update_nickname.json";

    public static final String KEY_SOURCE = "source";
    public static final String KEY_PHONE = "phone_number";
    public static final String KEY_VCODE = "verify_code";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_ERROR_CODE = "error_code";
    public static final String KEY_ERROR_MESSAGE = "error_message";
    public static final String KEY_DATA = "data";
    public static final String KEY_TMP_TOKEN = "tmp_token";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PASSWD = "pwd";
    public static final String KEY_UNIQUE_INFO = "unique_info";

    public static final String KEY_USR_ID = "userId";
    public static final String KEY_USR_NICKNAME = "nickName";
    public static final String KEY_USR_CREATE_TIME = "createTime";
    public static final String KEY_USR_AVATARURL = "portraitSmall";
    public static final String KEY_USR_RAW_AVATARURL = "portrait";
    public static final String KEY_USR_PHONE = "phoneNumber";
    public static final String KEY_USR_COVERURL = "coverImg";

    public static String getRequestVCodeUrl() {
        return URL_ROOT + "/" + URL_REQ_VCODE;
    }

    public static String getValidateVCodeUrl() {
        return URL_ROOT + "/" + URL_VAL_VCODE;
    }

    public static String getCreateAccountUrl() {
        return URL_ROOT + "/" + URL_CRE_ACCOUNT;
    }

    public static String getSigninUrl() {
        return URL_ROOT + "/" + URL_SIG_IN;
    }

    public static String getPublishUrl() {
        return URL_ROOT + "/" + URL_PUB_GRAIN;
    }

    public static String getUpdateNicknameUrl() {
        return URL_ROOT + "/" + URL_UPD_NICKNAME;
    }
}
