package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 启动界面
 */
public class SplashActivity extends Activity implements Animation.AnimationListener {

    @InjectView(R.id.activity_start_view)
    LinearLayout background;

    private String status = MyApplication.signInStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        initView();
        initAnimation();
    }

    private void initIdentify() {
        //用户身份状态检测
        if (AppUtils.isNetworkConnected(this)) {
            if (!StringUtils.isEmpty(AppConfig.getAppConfig().getConfToken())) {
                httpLoginByToken();
            } else {
                MyApplication.signInStatus = "10";
            }
        } else {
            if (!StringUtils.isEmpty(AppConfig.getAppConfig().getConfToken())) {
                MyApplication.signInStatus = "01";
            } else {
                MyApplication.signInStatus = "00";
            }
        }
    }

    private void initView() {
        background.setBackgroundResource(R.drawable.pic_start);
    }

    private void initAnimation() {
        // 渐变启动
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        background.startAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        initIdentify();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // 查询是否第一次使用本软件
        if (AppUtils.isFirstTimeToUse(this)) {
            LogUtils.d("第一次使用分支");
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {    //判断登录状态，是则进入主界面，否则进入登录界面
            Log.d("MT", "splash: " + MyApplication.signInStatus);
            if (status.equals("11") || status.equals("01")) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (status.equals("10") || status.equals("00")) {
                LogUtils.d("未登录分支");
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            }
        }
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    private void loadBackground() {
        String path = FileUtils.getAppCache(this, "start");
        List<File> files = FileUtils.getAllFiles(path);
        if (!files.isEmpty()) {
            // 搜索本次要加载的图片并进行加载
            long today = StringUtils.getToday();
            for (File f : files) {
                long time[] = getTime(f.getName());
                if (today >= time[0] && today <= time[1]) {
                    background.setBackgroundDrawable(Drawable.createFromPath(f.getAbsolutePath()));
                    break;
                }
            }
        }
    }

    /**
     * 图片的名字是由显示的时间区间构成的，如“开始时间-结束时间.png”
     * 通过解析文件名字，就可以得到这个文件显示的时间
     *
     * @param time
     * @return
     */
    private long[] getTime(String time) {
        long res[] = new long[2];
        try {
            time = time.substring(0, time.indexOf("."));
            String t[] = time.split("_");
            res[0] = Long.parseLong(t[0]);
            if (t.length >= 2) {
                res[1] = Long.parseLong(t[1]);
            } else {
                res[1] = Long.parseLong(t[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    private void httpLoginByToken() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.configTimeout(2500);
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getLoginByTokenUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                LocalUserInfo userInfo = new LocalUserInfo();
                                userInfo.setUserId(data.getLong(ApiUtils.KEY_USER_ID));
                                userInfo.setUserName(data.getString(ApiUtils.KEY_USR_NAME));
                                userInfo.setIsLogin(StringUtils.toBool(data.getString(ApiUtils.KEY_USR_IS_LOG_IN)));
                                userInfo.setNickName(data.getString(ApiUtils.KEY_USR_NICKNAME));
                                userInfo.setPhone(data.getString(ApiUtils.KEY_USR_PHONE));
                                userInfo.setCreateTime(data.getString(ApiUtils.KEY_USR_CREATE_TIME));
                                userInfo.setPortrait(data.getString(ApiUtils.KEY_PORTRAIT));
                                userInfo.setPortraitSmall(data.getString(ApiUtils.KEY_USR_PORTRAIT_SMALL));
                                userInfo.setCoverImg(data.getString(ApiUtils.KEY_USR_COVER_IMG));
                                AppConfig.getAppConfig().setUserInfo(userInfo);

                                Log.d("MyApplication", userInfo.toString());

                                MyApplication.signInStatus = "11";
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    MyApplication.signInStatus = "10";
                                }
                            }
                        } catch (JSONException e) {
                            MyApplication.signInStatus = "10";
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        MyApplication.signInStatus = "10";
                    }
                });
    }


}
