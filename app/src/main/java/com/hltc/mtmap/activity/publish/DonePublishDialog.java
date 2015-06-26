package com.hltc.mtmap.activity.publish;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.bean.ParcelableGrain;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.task.PublishAsyncTask;
import com.hltc.mtmap.util.ApiUtils;
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

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-5-14.
 */
public class DonePublishDialog extends Activity {

    @InjectView(R.id.btn_done_publish_home)
    TextView btnGoHome;
    @InjectView(R.id.btn_done_publish_maitian)
    TextView btnMyMaitian;
    @InjectView(R.id.btn_done_publish_share)
    TextView btnShare;
    @InjectView(R.id.layout_done_publish_share)
    LinearLayout layoutDonePublishShare;

    private ParcelableGrain grain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_done_publish);
        setFinishOnTouchOutside(false);
        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getWindow().setBackgroundDrawableResource(R.color.half_transparent);

        grain = getIntent().getParcelableExtra("GRAIN");
        Log.d("Publish", "received grain: " + grain.toString());
        httpPublish();

//        new PublishAsyncTask(grain).execute();
    }

    @OnClick({
            R.id.btn_done_publish_home,
            R.id.btn_done_publish_maitian,
            R.id.btn_done_publish_share
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done_publish_home:
                AppManager.getAppManager().finishActivity(DonePublishDialog.class);
                AppManager.getAppManager().finishActivity(CreateGrainActivity.class);
                break;
            case R.id.btn_done_publish_maitian:
                //TODO
                break;
            case R.id.btn_done_publish_share:
                if (layoutDonePublishShare.getVisibility() == View.INVISIBLE) {
                    layoutDonePublishShare.setVisibility(View.VISIBLE);
                    Animation animation = new TranslateAnimation(
                            layoutDonePublishShare.getTranslationX(), layoutDonePublishShare.getTranslationX(),
                            1300, layoutDonePublishShare.getTranslationY());
                    animation.setDuration(400);
                    layoutDonePublishShare.startAnimation(animation);
                } else {
                    Animation animation = new TranslateAnimation(
                            layoutDonePublishShare.getTranslationX(), layoutDonePublishShare.getTranslationX(),
                            layoutDonePublishShare.getTranslationY(), 1300);
                    animation.setDuration(400);
                    layoutDonePublishShare.startAnimation(animation);
                    layoutDonePublishShare.setVisibility(View.INVISIBLE);
                }
                break;
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
            JSONArray array = new JSONArray();
            for (String s : PhotoHelper.larges) {
                array.put(OssManager.bucketName + "." + OssManager.ossHost + "/"
                        + PublishAsyncTask.getRemotePath(s));
            }
            json.put("images", array);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
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
                                // 上传图片
                                if (PhotoHelper.larges.size() > 0) {
                                    new PublishAsyncTask().execute();
                                }
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
