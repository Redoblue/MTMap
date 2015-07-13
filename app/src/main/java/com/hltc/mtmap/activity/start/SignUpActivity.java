package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.task.SyncDataAsyncTask;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.hltc.mtmap.util.ViewUtils;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Redoblue on 2015/4/24.
 */
public class SignUpActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.et_signup_phone)
    EditText etSignupPhone;
    @InjectView(R.id.btn_signup_send_verifycode)
    Button btnSignupSendVerifycode;
    @InjectView(R.id.et_signup_verifycode)
    EditText etSignupVerifycode;
    @InjectView(R.id.btn_signup_comfirm)
    Button btnSignupComfirm;
    @InjectView(R.id.layout_signup_step_one)
    LinearLayout layoutSignupStepOne;
    @InjectView(R.id.et_signup_passwd)
    EditText etSignupPasswd;
    @InjectView(R.id.btn_signup_create)
    Button btnSignupCreate;
    @InjectView(R.id.layout_signup_step_two)
    LinearLayout layoutSignupStepTwo;

    private int source; //用来判断是注册还是找回密码
    private DownTimer mDownTimer;
    private String mPhone;
    private String mVCode;
    private String mPasswd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        mDownTimer = new DownTimer(60000, 1000);
        source = getIntent().getIntExtra("source", 0);
        initView();
        Log.d("Settings", "source: " + source + "\n source == 0: " + String.valueOf(source == 0));
    }

    private void initView() {
        tvBarTitle.setText(source == 0 ? "注册" : "修改密码");

        etSignupPhone.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_phone), 20));
        etSignupVerifycode.setHint(ViewUtils.getHint(getResources().getString(R.string.singup_verifycode_hint), 20));
        etSignupPasswd.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_password), 20));
        etSignupVerifycode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSignupComfirm.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_signup_send_verifycode,
            R.id.btn_signup_comfirm,
            R.id.btn_signup_create,
    })
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.btn_bar_left
                && !AppUtils.isNetworkConnected(this)) {
            ToastUtils.showShort(this, "网络连接失败");
            return;
        }

        switch (id) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_signup_send_verifycode:
                String phone = etSignupPhone.getText().toString();
                if (!StringUtils.isPhone(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                    break;
                }
//                ToastUtils.showShort(this, "验证码发送成功");
                mDownTimer.start();
                mPhone = phone;
                httpSubmitPhone();
                break;
            case R.id.btn_signup_comfirm:
                String vcode = etSignupVerifycode.getText().toString();
                if (!StringUtils.isVerifyCode(vcode)) {
                    ToastUtils.showShort(this, "验证码格式错误");
                    break;
                }
                mVCode = vcode;
                httpValidateVCode();
                break;
            case R.id.btn_signup_create:
                String passwd = etSignupPasswd.getText().toString();
                if (!StringUtils.isPasswd(passwd)) {
                    ToastUtils.showShort(this, "密码格式错误");
                    break;
                }
                mPasswd = passwd;
                httpSignUp();
                break;
        }
    }

    private void httpSubmitPhone() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(ApiUtils.KEY_SOURCE, "Android");
        params.addQueryStringParameter(ApiUtils.KEY_PHONE, mPhone);

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                ApiUtils.getRequestVCodeUrl(source),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {
                                // 发送验证码成功
                                ToastUtils.showShort(SignUpActivity.this, "验证码发送成功");
                            } else {
                                JSONObject object = new JSONObject(result);
                                String errorMsg = object.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SignUpActivity.this, errorMsg);
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

    private void httpValidateVCode() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(ApiUtils.KEY_SOURCE, "Android");
        params.addQueryStringParameter(ApiUtils.KEY_PHONE, mPhone);
        params.addQueryStringParameter(ApiUtils.KEY_VCODE, mVCode);

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                ApiUtils.getValidateVCodeUrl(source),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                ToastUtils.showShort(SignUpActivity.this, "验证成功");
                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                String tmpToken = son.getString(ApiUtils.KEY_TMP_TOKEN);
                                AppConfig.getAppConfig().setConfTmpToken(tmpToken);     //将临时Token保存
                                // 进行布局转换
                                layoutSignupStepOne.setVisibility(View.GONE);
                                layoutSignupStepTwo.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SignUpActivity.this, errorMsg);
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

    private void httpSignUp() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_PHONE, mPhone);
            json.put(ApiUtils.KEY_PASSWD, StringUtils.toMD5(mPasswd));
            json.put(ApiUtils.KEY_TMP_TOKEN, AppConfig.getAppConfig().getConfTmpToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                source == 0 ?
                        ApiUtils.getCreateAccountUrl() :
                        ApiUtils.getResetPasswdUrl(),
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
                                AppConfig.getAppConfig().setConfToken(data.getString(ApiUtils.KEY_TOKEN));
                                //TODO 已注册好 下一步干吗？
                                ToastUtils.showShort(SignUpActivity.this, "注册成功");
                                Log.d("SignUpActivity", userInfo.toString());

                                //更新身份状态
                                MyApplication.signInStatus = "11";

                                //同步数据
                                new SyncDataAsyncTask().execute();

                                Intent intent = new Intent(SignUpActivity.this, CheckContactActivity.class);
                                startActivity(intent);
                                AppManager.getAppManager().finishActivity(SignUpActivity.this);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SignUpActivity.this, errorMsg);
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

    /**
     * ************************** Life Cycle ************************
     */

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    private class DownTimer extends CountDownTimer {
        public DownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnSignupSendVerifycode.setEnabled(false);
            btnSignupSendVerifycode.setText(millisUntilFinished / 1000 + " 秒");
        }

        @Override
        public void onFinish() {
            btnSignupSendVerifycode.setEnabled(true);
            btnSignupSendVerifycode.setText("重发验证码");
        }
    }
}
