package com.hltc.mtmap.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.app.AppConfig;
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
 * Created by redoblue on 15-7-8.
 */
public class OpenGrainDetailTask extends AsyncTask<Long, Void, Boolean> {

    private GrainDetail grainDetail;
    private boolean success = false;

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            Intent intent = new Intent();
            intent.putExtra("grain", grainDetail);
            MyApplication.getContext().startActivity(intent);
        } else {
            Toast.makeText(MyApplication.getContext(), "获取详情失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        httpGetGrainDetail(params[0]);
        return success;
    }

    private void httpGetGrainDetail(long grainId) {
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
                                grainDetail = gson.fromJson(json.toString(), new TypeToken<GrainDetail>() {
                                }.getType());
                                success = true;

                                //TODO 保存到数据库
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
