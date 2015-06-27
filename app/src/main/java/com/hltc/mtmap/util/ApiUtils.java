package com.hltc.mtmap.util;

/**
 * Created by Redoblue on 2015/4/25.
 */
public class ApiUtils {

    public static final String URL_ROOT = "http://www.maitianditu.com/maitian/v1/";
//    public static final String URL_ROOT = "http://171.113.182.61/maitian/v1";

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
    // 检索通讯录
    public static final String URL_CHE_CONTACT = "user/register/check_contact.json";
    // 反馈
    public static final String URL_FEEDBACK = "user/settings/feedback.json";
    // FederationToken
    public static final String URL_FEDERATION_TOKEN = "auth/oss_federation_token.json";
    // 麦粒
    public static final String URL_GRAIN_QUERY = "grain/home_query.json";
    // 登录
    public static final String URL_LOGIN_BY_TOKEN = "user/login/login_by_token.json";

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

    //反馈
    public static final String KEY_CONTENT = "content";
    public static final String KEY_EMAIL = "email";

    //获取FederationToken
    public static final String KEY_TMP_AK = "tmpAkId";
    public static final String KEY_TMP_SK = "tmpAkSecret";
    public static final String KEY_SEC_TOKEN = "securityToken";
    public static final String KEY_EXP_TIME = "expireTime";

    //query grain
    public static final String KEY_GRAIN_MCATEID = "mcateId";
    public static final String KEY_GRAIN_CITYCODE = "cityCode";
    public static final String KEY_GRAIN_LON = "lon";
    public static final String KEY_GRAIN_LAT = "lat";
    public static final String KEY_GRAIN_RADIUS = "radius";

    public static String getRequestVCodeUrl(int source) {
        return source == 0 ?
                URL_ROOT + URL_REQ_VCODE_0 :
                URL_ROOT + URL_REQ_VCODE_1;
    }

    public static String getValidateVCodeUrl(int source) {
        return source == 0 ?
                URL_ROOT + URL_VAL_VCODE_0 :
                URL_ROOT + URL_VAL_VCODE_1;
    }

    public static String getCreateAccountUrl() {
        return URL_ROOT + URL_CRE_ACCOUNT;
    }

    public static String getResetPasswdUrl() {
        return URL_ROOT + URL_RST_PASSWD;
    }

    public static String getSigninUrl() {
        return URL_ROOT + URL_SIG_IN;
    }

    public static String getPublishUrl() {
        return URL_ROOT + URL_PUB_GRAIN;
    }

    public static String getUpdateNicknameUrl() {
        return URL_ROOT + URL_UPD_NICKNAME;
    }

    public static String getRecommendGrainUrl(boolean isUser) {
        return isUser ? URL_REC_GRAIN_0 : URL_REC_GRAIN_1;
    }

    public static String getCheckContactUrl() {
        return URL_ROOT + URL_CHE_CONTACT;
    }

    public static String getFeedbackUrl() {
        return URL_ROOT + URL_FEEDBACK;
    }

    public static String getFederationTokenUrl() {
        return URL_ROOT + URL_FEDERATION_TOKEN;
    }

    public static String getQueryGrainUrl() {
        return URL_ROOT + URL_GRAIN_QUERY;
    }

    public static String getLoginByTokenUrl() {
        return URL_ROOT + URL_LOGIN_BY_TOKEN;
    }
}
