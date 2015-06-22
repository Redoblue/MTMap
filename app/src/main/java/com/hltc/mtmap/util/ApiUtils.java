package com.hltc.mtmap.util;

/**
 * Created by Redoblue on 2015/4/25.
 */
public class ApiUtils {

    public static final String URL_ROOT = "http://www.maitianditu.com/maitian/v1";

    public static final String URL_REQ_VCODE_0 = "user/register/verify_code.json";
    public static final String URL_VAL_VCODE_0 = "user/register/verify.json";
    public static final String URL_CRE_ACCOUNT = "user/register/new_user.json";
    public static final String URL_SIG_IN = "user/login/login.json";
    public static final String URL_PUB_GRAIN = "grain/publish.json";
    public static final String URL_UPD_NICKNAME = "user/settings/update_nickname.json";
    public static final String URL_REC_GRAIN_0 = "user/getRecommendGrain.json";
    // 找回密码
    public static final String URL_REQ_VCODE_1 = "user/login/forget/verify_code.json";
    public static final String URL_VAL_VCODE_1 = "user/login/forget/verify.json";
    public static final String URL_RST_PASSWD = "user/login/forget/reset_password.json";
    // 游客模块
    public static final String URL_REC_GRAIN_1 = "visitor/getRecommendGrain.json";

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

    //注册
    public static final String KEY_USR_ID = "userId";
    public static final String KEY_USR_NAME = "userName";
    public static final String KEY_USR_IS_LOG_IN = "isLogin";
    public static final String KEY_USR_NICKNAME = "nickName";
    public static final String KEY_USR_PHONE = "phone";
    public static final String KEY_USR_PORTRAIT = "portrait";
    public static final String KEY_USR_PORTRAIT_SMALL = "portraitSmall";
    public static final String KEY_USR_CREATE_TIME = "createTime";
    public static final String KEY_USR_COVER_IMG = "coverImg";

    public static String getRequestVCodeUrl(int source) {
        return source == 0 ?
                URL_ROOT + "/" + URL_REQ_VCODE_0 :
                URL_ROOT + "/" + URL_REQ_VCODE_1;
    }

    public static String getValidateVCodeUrl(int source) {
        return source == 0 ?
                URL_ROOT + "/" + URL_VAL_VCODE_0 :
                URL_ROOT + "/" + URL_VAL_VCODE_1;
    }

    public static String getCreateAccountUrl() {
        return URL_ROOT + "/" + URL_CRE_ACCOUNT;
    }

    public static String getResetPasswdUrl() {
        return URL_ROOT + "/" + URL_RST_PASSWD;
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

    public static String getRecommendGrainUrl(boolean isUser) {
        return isUser ? URL_REC_GRAIN_0 : URL_REC_GRAIN_1;
    }
}
