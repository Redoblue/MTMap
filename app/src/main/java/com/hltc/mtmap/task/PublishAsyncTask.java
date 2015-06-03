package com.hltc.mtmap.task;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.bean.GrainPhotoInfo;
import com.hltc.mtmap.bean.ParcelableGrain;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.ImageUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by redoblue on 15-5-18.
 */
public class PublishAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    List<GrainPhotoInfo> list = new ArrayList<>();
    private RelativeLayout progressLayout;
    private CircleProgress circleProgress;
    private Context mContext;
    private OSSService ossService = OSSServiceProvider.getService();
    private OSSBucket ossBucket;
    private ParcelableGrain grain;
    private boolean isPushOK;
    private int progress;

    public PublishAsyncTask(Context conext, RelativeLayout layout, CircleProgress circle, ParcelableGrain grain) {
        super();
        this.mContext = conext;
        this.progressLayout = layout;
        this.circleProgress = circle;
        this.grain = grain;
    }

    @Override
    protected void onPreExecute() {
        initOssService();
        initData();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            progressLayout.setVisibility(View.GONE);
        } else {
            Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        circleProgress.setProgress(values[0]);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        for (int i = 0; i < list.size(); i++) {
            upload(list.get(i).getLarge());
            upload(list.get(i).getSmall());
            publishProgress(100 * (i + 1) / list.size());
        }
        FileUtils.deleteDir();
        httpPublish();
        return true;
    }

    // 断点上传
    public void upload(String path) {
        OSSFile file = ossService.getOssFile(ossBucket,
                "10000002015051516420168824015343" + "/" + StringUtils.getFileNameFromPath(path) + ".png");
        try {
            file.setUploadFilePath(path, "image/png");
            file.upload();
            //TODO
            Log.d("Publish", "upload succeed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initOssService() {
        ossService.setApplicationContext(mContext);
        //TODO 全局加签
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;
                return OSSToolKit.generateToken("wxGYeoOqFGIikopt", "eQyS38ArhJo0fIotIuLoiz0FCx0J4N", content);
            }
        });
        ossService.setGlobalDefaultHostId(AppConfig.OSS_ROOT);
        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);
        ossService.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 默认为private

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
        conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
        conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
        ossService.setClientConfiguration(conf);

        ossBucket = ossService.getOssBucket(AppConfig.OSS_BUCKET);
    }

    private void initData() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if (PhotoHelper.larges.size() > 0) {
            for (String large : PhotoHelper.larges) {
                String small = ImageUtils.creatThumbnail(large);
                GrainPhotoInfo info = new GrainPhotoInfo(large, small);
                list.add(info);
            }
        }
    }

    private void httpPublish() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject father = new JSONObject();
        try {
//            father.put("userId", grain.userId);
//            father.put("token", grain.token);
            father.put("userId", "10000002015051516420168824015343");
            father.put("token", "0Iq_Mnp754Da2Nq9oEIlTN");
            father.put("mcateId", grain.mcateId);
            father.put("siteSource", grain.siteSource);
            father.put("siteId", grain.siteId);
            father.put("siteName", grain.siteName);
            father.put("siteAddress", grain.siteAddress);
            father.put("sitePhone", grain.sitePhone);
            father.put("siteType", grain.siteType);
            father.put("lat", grain.latitude);
            father.put("lon", grain.longitude);
            father.put("cityCode", grain.cityCode);
            father.put("isPublic", grain.isPublic);
            father.put("text", grain.text);
            JSONArray array = new JSONArray();
            for (GrainPhotoInfo item : list) {
                JSONObject json = new JSONObject();
                json.put("large", AppConfig.OSS_URL_IMAGE + "10000002015051516420168824015343" + "/"
                        + StringUtils.getFileNameFromPath(item.getLarge()) + ".png");
                json.put("small", AppConfig.OSS_URL_IMAGE + "10000002015051516420168824015343" + "/"
                        + StringUtils.getFileNameFromPath(item.getSmall()) + ".png");
                array.put(json);
            }
            father.put("images", array);
            params.setBodyEntity(new StringEntity(father.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getPublishUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                isPushOK = true;
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    Log.d("Publish", "errorMsg: " + errorMsg);
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
