package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Redoblue on 2015/4/18.
 */
public class StartActivity extends Activity {

    @InjectView(R.id.img_start_bg)
    KenBurnsView kenBurnsView;
    @InjectView(R.id.btn_start_signin)
    Button signInBtn;
    @InjectView(R.id.btn_start_signup)
    Button signUpBtn;
    @InjectView(R.id.btn_start_skip)
    Button skipBtn;
    @InjectView(R.id.btn_start_switch_server)
    ToggleButton btnStartSwitchServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        ApiUtils.URL_ROOT = "http://www.maitianditu.com/maitian/v1/";
    }

    private void initBackground() {
        RandomTransitionGenerator generator = new RandomTransitionGenerator(5, new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 0;
            }
        });
        kenBurnsView.setTransitionGenerator(generator);
    }

    @OnClick({R.id.btn_start_signin,
            R.id.btn_start_signup,
            R.id.btn_start_skip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_signin:
                if (!AppUtils.isNetworkConnected(this)) {
                    Toast.makeText(this, "请检查您的网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent0 = new Intent(this, SignInActivity.class);
                startActivity(intent0);
                break;
            case R.id.btn_start_signup:
                if (!AppUtils.isNetworkConnected(this)) {
                    Toast.makeText(this, "请检查您的网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(this, SignUpActivity.class);
                intent1.putExtra("source", 0);
                startActivity(intent1);
                break;
            case R.id.btn_start_skip:
                if (!AppUtils.isNetworkConnected(this)) {
                    Toast.makeText(this, "请检查您的网络", Toast.LENGTH_SHORT).show();
                } else {
                    httpGetVisitorId();
                    Intent intent2 = new Intent(this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
        }
    }

    // will be removed after development
    @OnClick(R.id.btn_start_switch_server)
    public void switchServer() {
        if (btnStartSwitchServer.isChecked()) {
            ApiUtils.URL_ROOT = "http://www.maitianditu.com/maitian/v1/";
        } else {
            ApiUtils.URL_ROOT = "http://192.168.0.109/maitian/v1/";
        }
    }

    private void httpGetVisitorId() {
//        RequestParams params = new RequestParams();
//        params.addQueryStringParameter(ApiUtils.KEY_SOURCE, "Android");
//        params.addQueryStringParameter(ApiUtils.KEY_PHONE, mPhone);
//        params.addQueryStringParameter(ApiUtils.KEY_VCODE, mVCode);

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                ApiUtils.URL_ROOT + ApiUtils.URL_GET_VISITOR_ID,
                null, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                ToastUtils.showShort(StartActivity.this, "验证成功");
                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                long vid = son.getLong("vid");
                                AppConfig.getAppConfig().set(AppConfig.CONFIG_APP, "vid", String.valueOf(vid));     //将临时Token保存
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(StartActivity.this, errorMsg);
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
