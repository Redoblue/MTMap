package com.hltc.mtmap.helper;


import android.util.Log;

import com.alibaba.sdk.android.oss.model.OSSFederationToken;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by redoblue on 15-6-25.
 */
public class FederationTokenGetter {

//    private static OSSFederationToken token;

   /* public static OSSFederationToken getToken() {
        return getTokenFromServer();
    }*/

   /* private static OSSFederationToken getTokenFromServer() {
        return httpGetFederationToken2();
    }*/
/*

    private void httpGetFederationToken() {
        RequestParams params1 = new RequestParams();
        params1.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            params1.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getFederationTokenUrl(),
                params1,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("Publish", result);
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                Log.d("Publish", "result: " + result);
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
//                                token.setTempAk(data.getString(ApiUtils.KEY_TMP_AK));
//                                token.setTempSk(data.getString(ApiUtils.KEY_TMP_SK));
//                                token.setSecurityToken(data.getString(ApiUtils.KEY_SEC_TOKEN));
//                                token.setExpiration(System.currentTimeMillis() + 3600 * 1000);
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
    }

    private static OSSFederationToken httpGetFederationToken2() {
        OSSFederationToken token = new OSSFederationToken();

        RequestParams params1 = new RequestParams();
        params1.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            params1.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        try {
            ResponseStream response = http.sendSync(HttpRequest.HttpMethod.POST,
                    ApiUtils.getFederationTokenUrl(),
                    params1);
            InputStream stream = response.getBaseResponse().getEntity().getContent();
            String result = inStream2String(stream);
            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                token.setTempAk(data.getString(ApiUtils.KEY_TMP_AK));
                token.setTempSk(data.getString(ApiUtils.KEY_TMP_SK));
                token.setSecurityToken(data.getString(ApiUtils.KEY_SEC_TOKEN));
                token.setExpiration(System.currentTimeMillis() + 3600 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    //将输入流转换成字符串
    private static String inStream2String(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray());
    }
*/

}
