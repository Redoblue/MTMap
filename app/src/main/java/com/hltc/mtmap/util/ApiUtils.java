package com.hltc.mtmap.util;

import android.util.Log;
import android.widget.Toast;

import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.gmodel.ContactItem;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ApiUtils {
    public static final String URL_ROOT = "http://www.maitianditu.com/maitian/v1/";
    //    public static final String URL_ROOT = "http://192.168.0.109/maitian/v1/";
    public static final String URL_REQ_VCODE_0 = "user/register/verify_code.json";
    public static final String URL_VAL_VCODE_0 = "user/register/verify.json";
    public static final String URL_CRE_ACCOUNT = "user/register/new_user.json";
    public static final String URL_SIG_IN = "user/login/login.json";
    public static final String URL_PUB_GRAIN = "grain/publish.json";
    public static final String URL_UPDATE_NICKNAME = "my/settings/update_nickname.json";
    public static final String URL_REC_GRAIN_0 = "user/getRecommendGrain.json";
    // 找回密码
    public static final String URL_REQ_VCODE_1 = "user/login/forget/verify_code.json";
    public static final String URL_VAL_VCODE_1 = "user/login/forget/verify.json";
    public static final String URL_RST_PASSWD = "user/login/forget/reset_password.json";
    // 游客模块
    public static final String URL_REC_GRAIN_1 = "visitor/getRecommendGrain.json";
    public static final String URL_VISITOR_RECOMMAND = "visitor/getRecommendGrain.json";
    public static final String URL_GET_VISITOR_ID = "visitor/getVid.json";
    public static final String URL_VISITOR_READ_GRAIN = "visitor/read_grain.json";
    // 检索通讯录
    public static final String URL_CHE_CONTACT = "user/register/check_contact.json";
    // 反馈
    public static final String URL_FEEDBACK = "my/settings/feedback.json";
    // FederationToken
    public static final String URL_FEDERATION_TOKEN = "auth/oss_federation_token.json";
    // 麦粒
    public static final String URL_GRAIN_QUERY = "grain/home_query.json";
    public static final String URL_GRAIN_RECOMMAND = "grain/getRecommendGrain.json";
    public static final String URL_GRAIN_NUMBER = "my/grain_statistic.json";
    public static final String URL_FAVOR_GRAIN = "grain/favor.json";
    public static final String URL_READ_GRAIN = "grain/read_grain.json";
    public static final String URL_GRAIN_DETAIL = "grain/detail.json";
    public static final String URL_DELETE_GRAIN = "grain/delete.json";
    public static final String URL_COMMENT_GRAIN = "grain/comment.json";
    public static final String URL_PRAISE_GRAIN = "grain/praise.json";
    private static final  String URL_IGNORE_GRAIN = "grain/neglect.json";
    // 登录
    public static final String URL_LOGIN_BY_TOKEN = "user/login/login_by_token.json";
    // 朋友
    public static final String URL_FRIEND_ADD_FRIEND = "friend/add_friend.json";
    public static final String URL_FRIEND_GET_LIST = "my/friends.json";
    public static final String URL_FRIEND_GET_STATUS = "my/friends/adding.json";
    public static final String URL_FRIEND_AGREE = "friend/agree.json";
    //个人
    public static final String URL_UPDATE_PORTRAIT = "my/portrait.json";
    public static final String URL_SEARCH_FRIEND_BY_KEYWORD = "my/friends/search.json";
    public static final String URL_MY_GRAIN = "my/maitian.json";
    public static final String URL_MY_FAVOURITE = "my/favourites.json";

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
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USR_NAME = "userName";
    public static final String KEY_USR_IS_LOG_IN = "isLogin";
    public static final String KEY_USR_NICKNAME = "nickName";
    public static final String KEY_USR_PHONE = "phone";
    public static final String KEY_PORTRAIT = "portrait";
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
    public static final String KEY_GRAIN_CHIHE = "chihe";
    public static final String KEY_GRAIN_WANLE = "wanle";
    public static final String KEY_GRAIN_OTHER = "other";
    //朋友
    public static final String KEY_TOID = "toId";
    public static final String KEY_TEXT = "text";
    public static final String KEY_REMARK = "remark";
    public static final String KEY_FIRST_CHARACTER = "firstCharacter";
    public static final String KEY_FROM_ID = "fromId";
    public static final String KEY_KEYWORD = "keyword";
    public static final String KEY_GRAIN_ID ="gid" ;
    public static final String TIP_LOAD_DATA_FAIL="加载数据失败...";
    public static final String TIP_LOAD_DATA="加载数据...";
    public static final String API_OBTAIN_FRIEND_GRAIN = URL_ROOT+"friend/personal/maitian.json";
    public static final String API_FRIEND_REMARK = URL_ROOT +"friend/remark.json";
    public static final String API_FRIEND_DELETE = URL_ROOT +"friend/delete.json";
    public static final String TIP_NET_EXCEPTION = "网络异常";

    public static final String TIP_FAVOR_GRAIN_SUCCESS="收藏成功";
    public static final String TIP_FAVOR_GRAIN_FAIL = "收藏失败";
    public static final String TIP_WX_NOT_SUPPORT = "微信版本不支持分享功能，请下载或者更新微信版本";

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
        return URL_ROOT + URL_UPDATE_NICKNAME;
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

    public static String getVisitorRecommandUrl() {
        return URL_ROOT + URL_VISITOR_RECOMMAND;
    }

    public static String getGrainRecommandUrl() {
        return URL_ROOT + URL_GRAIN_RECOMMAND;
    }

    public static String getFriendAddFriendUrl() {
        return URL_ROOT + URL_FRIEND_ADD_FRIEND;
    }

    public static String getIgnoreGrainURL(){return  URL_ROOT+URL_IGNORE_GRAIN;};
    public static void httpAddFriend(ContactItem contact) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put(ApiUtils.KEY_TOID, contact.getUserId());
            json.put(ApiUtils.KEY_TEXT, contact.getText());
            json.put(ApiUtils.KEY_REMARK, contact.getName());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getFriendAddFriendUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d("MT", responseInfo.toString());
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result)) {
                            Toast.makeText(MyApplication.getContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Toast.makeText(MyApplication.getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    Toast.makeText(MyApplication.getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                    }
                });
    }
}
