package com.hltc.mtmap.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.activity.map.GrainDetailActivity;
import com.hltc.mtmap.activity.map.GrainInfoDialog;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DialogManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.gmodel.GrainDetail;
import com.hltc.mtmap.util.ApiUtils;
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
 * Created by redoblue on 15-7-9.
 */
public class ApiHelper {

    public static final int ACTION_FAVOR = 0;
    public static final int ACTION_DELETE = 1;

    public static void httpActOnGrain(int action, long grainId) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", grainId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = null;
        switch (action) {
            case ACTION_FAVOR:
                url = ApiUtils.URL_ROOT + ApiUtils.URL_FAVOR_GRAIN;
                break;
            case ACTION_DELETE:
                url = ApiUtils.URL_ROOT + ApiUtils.URL_DELETE_GRAIN;
                break;
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                    // 收藏成功
                    Toast.makeText(MyApplication.getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                // 收藏失败
            }
        });
    }

    public static void httpGetGrainDetail(final Context context, long grainId) {
        final ProgressDialog dialog = DialogManager.buildProgressDialog(context, "加载中...");
        dialog.show();

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", grainId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_GRAIN_DETAIL,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("MapFragment", result);
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject json = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                Gson gson = new Gson();
                                GrainDetail grainDetail = gson.fromJson(json.toString(), new TypeToken<GrainDetail>() {
                                }.getType());

                                dialog.dismiss();
                                if (grainDetail != null) {
                                    //TODO 进入详情界面
                                    Intent intent = new Intent(MyApplication.getContext(), GrainDetailActivity.class);
                                    intent.setExtrasClassLoader(GrainDetail.Praise.class.getClassLoader());
                                    intent.putExtra("grain", grainDetail);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MyApplication.getContext().startActivity(intent);
                                    AppManager.getAppManager().finishActivity(GrainInfoDialog.class);
                                } else {
                                    Toast.makeText(MyApplication.getContext(), "检索详情失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
