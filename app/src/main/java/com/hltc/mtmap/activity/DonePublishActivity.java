package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.bean.SerialGrain;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-5-14.
 */
public class DonePublishActivity extends Activity {

    @InjectView(R.id.btn_done_publish_home)
    Button goBackHomeButton;
    @InjectView(R.id.btn_done_publish_maitian)
    Button goToMaitionButton;
    @InjectView(R.id.btn_done_publish_share)
    Button shareButton;
    @InjectView(R.id.cp_done_publish_upload)
    CircleProgress uploadCircleProgress;
    @InjectView(R.id.layout_done_publish_progress)
    RelativeLayout layoutDonePublishProgress;
    List<String> list = new ArrayList<>();
    private OSSService ossService = OSSServiceProvider.getService();
    private OSSBucket ossBucket;
    private SerialGrain grain;
    private float uploadingIndex;
    private int currentProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_done_publish);
        ButterKnife.inject(this);

        Log.d("Publish", "DonePublishActivity Created!");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initOssService();

        ArrayList<String> urls = new ArrayList<>();
        if (PhotoHelper.addresses.size() > 0) {
            for (int i = 0; i < PhotoHelper.addresses.size(); i++) {
                String name = PhotoHelper.addresses.get(i).substring(
                        PhotoHelper.addresses.get(i).lastIndexOf("/") + 1,
                        PhotoHelper.addresses.get(i).lastIndexOf("."));
                list.add(name + ".jpeg");
                urls.add(AppConfig.OSS_ROOT + "/" + AppConfig.OSS_BUCKET + "/" + name + ".jpeg");
            }

            for (int i = 0; i < list.size(); i++) {
                uploadingIndex = i;
                resumableUpload(list.get(i));
            }
        }

        grain = (SerialGrain) getIntent().getSerializableExtra("GRAIN");
        grain.images = urls;

        httpPublish();
    }

    private void initOssService() {
        ossService.setApplicationContext(getApplicationContext());
        //TODO 全局加签
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;
                return OSSToolKit.generateToken("wxGYeoOqFGIikopt", "eQyS38ArhJo0flotluLoiz0FCx0J4N", content);
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

    // 断点上传
    public void resumableUpload(String name) {
        OSSFile file = ossService.getOssFile(ossBucket, StringUtils.getUUID() + ".jpeg");
        try {
            file.setUploadFilePath(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + name, "image/jpeg");
            file.ResumableUploadInBackground(new SaveCallback() {

                @Override
                public void onSuccess(String objectKey) {
                    Log.d("Publish", "[onSuccess] - " + objectKey + " upload success!");
                    currentProgress = (int) (100 * uploadingIndex / list.size());
                    uploadCircleProgress.setProgress(currentProgress);
                }

                @Override
                public void onProgress(String objectKey, int byteCount, int totalSize) {
                    Log.d("Publish", "[onProgress] - current upload " + objectKey + " bytes: " + byteCount + " in total: " + totalSize);
                    currentProgress += (int) ((100 * 1f / list.size()) * ((float) byteCount / totalSize));
                }

                @Override
                public void onFailure(String objectKey, OSSException ossException) {
                    Log.e("Publish", "[onFailure] - upload " + objectKey + " failed!\n" + ossException.toString());
                    Toast.makeText(DonePublishActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    ossException.printStackTrace();
                    ossException.getException().printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void httpPublish() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put("userId", grain.userId);
            json.put("token", grain.token);
            json.put("mcateId", grain.mcateId);
            json.put("siteSource", grain.siteSource);
            json.put("siteId", grain.siteId);
            json.put("siteName", grain.siteName);
            json.put("siteAddress", grain.siteAddress);
            json.put("sitePhone", grain.sitePhone);
            json.put("siteType", grain.siteType);
            json.put("lat", grain.latitude);
            json.put("lon", grain.longitude);
            json.put("cityCode", grain.cityCode);
            json.put("isPublic", grain.isPublic);
            json.put("text", grain.text);
            JSONArray array = new JSONArray("images");
            array.put("");//TODO
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getCreateAccountUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
//                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
//                                LocalUserInfo userInfo = new LocalUserInfo();
//                                userInfo.setId(son.getString(ApiUtils.KEY_USR_ID));
//                                userInfo.setNickname(son.getString(ApiUtils.KEY_USR_NICKNAME));
//                                userInfo.setCreateTime(son.getString(ApiUtils.KEY_USR_CREATE_TIME));
//                                userInfo.setAvatarURL(son.getString(ApiUtils.KEY_USR_AVATARURL));
//                                userInfo.setRawAvatarURL(son.getString(ApiUtils.KEY_USR_RAW_AVATARURL));
//                                userInfo.setPhone(son.getString(ApiUtils.KEY_USR_PHONE));
//                                userInfo.setCoverURL(son.getString(ApiUtils.KEY_USR_COVERURL));
//                                AppConfig.getAppConfig(DonePublishActivity.this).setUserInfo(userInfo);
//                                //TODO 已注册好 下一步干吗？
//                                ToastUtils.showShort(DonePublishActivity.this, "注册成功");
//                                LogUtils.d(userInfo.toString());
//                                //TODO
//                                Intent intent = new Intent(DonePublishActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                AppManager.getAppManager().finishActivity(DonePublishActivity.this);
                                layoutDonePublishProgress.setVisibility(View.INVISIBLE);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(DonePublishActivity.this, errorMsg);
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
