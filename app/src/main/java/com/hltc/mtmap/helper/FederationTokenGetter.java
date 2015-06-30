package com.hltc.mtmap.helper;


import android.util.Log;

import com.alibaba.sdk.android.oss.model.OSSFederationToken;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
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

/**
 * Created by redoblue on 15-6-25.
 */
public class FederationTokenGetter {

//    private static OSSFederationToken token;

    public static OSSFederationToken getToken() {
//        token = getTokenFromServer();
        return getTokenFromServer();
    }

    private static OSSFederationToken getTokenFromServer() {
        final OSSFederationToken localToken = new OSSFederationToken();
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(MyApplication.getContext()).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(MyApplication.getContext()).getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getFederationTokenUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("Publish", result);
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                Log.d("Publish", "result: " + result);
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                localToken.setTempAk(data.getString(ApiUtils.KEY_TMP_AK));
                                localToken.setTempSk(data.getString(ApiUtils.KEY_TMP_SK));
                                localToken.setSecurityToken(data.getString(ApiUtils.KEY_SEC_TOKEN));
                                localToken.setExpiration(System.currentTimeMillis() + 3600 * 1000);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(MyApplication.getContext(), errorMsg);
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
        return localToken;
    }
}
