package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.LocalUserInfo;
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
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Redoblue on 2015/4/24.
 */
public class SignUpActivity extends Activity implements View.OnClickListener {

    private DownTimer mDownTimer;
    private String mPhone;
    private String mVCode;
    private String mPasswd;

    private LinearLayout stepOneLayout;
    private LinearLayout stepTwoLayout;

    private Button mGoBackButton;
    private TextView mTitleTextView;
    private EditText mPhoneEditText;
    private Button mSendVerifyCodeButton;
    private EditText mVerifyCodeEditText;
    private Button mConfirmButton;
    private EditText mPasswdEditText;
    private Button mlastConfirmButton;
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mConfirmButton.setEnabled(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);

        findViewById();
        initViews();
        mDownTimer = new DownTimer(60000, 1000);
        AppManager.getAppManager().addActivity(this);
    }

    private void findViewById() {
        stepOneLayout = (LinearLayout) findViewById(R.id.layout_signup_step_one);
        stepTwoLayout = (LinearLayout) findViewById(R.id.layout_signup_step_two);
        mGoBackButton = (Button) findViewById(R.id.btn_bar_left);
        mTitleTextView = (TextView) findViewById(R.id.tv_bar_title);
        mPhoneEditText = (EditText) findViewById(R.id.et_signup_phone);
        mSendVerifyCodeButton = (Button) findViewById(R.id.btn_signup_send_verifycode);
        mVerifyCodeEditText = (EditText) findViewById(R.id.et_signup_verifycode);
        mConfirmButton = (Button) findViewById(R.id.btn_signup_comfirm);
        mPasswdEditText = (EditText) findViewById(R.id.et_signup_passwd);
        mlastConfirmButton = (Button) findViewById(R.id.btn_signup_create);
    }

    private void initViews() {
        mTitleTextView.setText(R.string.sign_up);
        mGoBackButton.setBackgroundResource(R.drawable.back);
        mGoBackButton.setOnClickListener(this);
        mSendVerifyCodeButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
        mlastConfirmButton.setOnClickListener(this);
        mPhoneEditText.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_phone), 20));
        mVerifyCodeEditText.setHint(ViewUtils.getHint(getResources().getString(R.string.singup_verifycode_hint), 20));
        mPasswdEditText.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_password), 20));
        mVerifyCodeEditText.addTextChangedListener(watcher);
    }

    @Override
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
                String phone = mPhoneEditText.getText().toString();
                if (!StringUtils.isMobilePhone(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                    break;
                }
//                ToastUtils.showShort(this, "验证码发送成功");
                mDownTimer.start();
                mPhone = phone;
                httpSubmitPhone();
                break;
            case R.id.btn_signup_comfirm:
                String vcode = mVerifyCodeEditText.getText().toString();
                if (!StringUtils.isVerifyCode(vcode)) {
                    ToastUtils.showShort(this, "验证码格式错误");
                    break;
                }
                mVCode = vcode;
                httpValidateVCode();
                break;
            case R.id.btn_signup_create:
                String passwd = mPasswdEditText.getText().toString();
                if (!StringUtils.isPasswd(passwd)) {
                    ToastUtils.showShort(this, "密码格式错误");
                    break;
                }
                mPasswd = passwd;
                httpSignUp();
                break;
            default:
                break;
        }
    }

    private void httpSubmitPhone() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(ApiUtils.KEY_SOURCE, "Android");
        params.addQueryStringParameter(ApiUtils.KEY_PHONE, mPhone);

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                ApiUtils.getRequestVCodeUrl(),
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
                ApiUtils.getValidateVCodeUrl(),
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
                                AppConfig.getAppConfig(SignUpActivity.this).setToken(tmpToken);     //将临时Token保存
                                // 进行布局转换
                                stepOneLayout.setVisibility(View.GONE);
                                stepTwoLayout.setVisibility(View.VISIBLE);
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
        String tmpToken = AppConfig.getAppConfig(this).getToken();

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_PHONE, mPhone);
            json.put(ApiUtils.KEY_PASSWD, mPasswd);
            json.put(ApiUtils.KEY_TMP_TOKEN, tmpToken);
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
                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                LocalUserInfo userInfo = new LocalUserInfo();
                                userInfo.setId(son.getString(ApiUtils.KEY_USR_ID));
                                userInfo.setNickname(son.getString(ApiUtils.KEY_USR_NICKNAME));
                                userInfo.setCreateTime(son.getString(ApiUtils.KEY_USR_CREATE_TIME));
                                userInfo.setAvatarURL(son.getString(ApiUtils.KEY_USR_AVATARURL));
                                userInfo.setRawAvatarURL(son.getString(ApiUtils.KEY_USR_RAW_AVATARURL));
                                userInfo.setPhone(son.getString(ApiUtils.KEY_USR_PHONE));
                                userInfo.setCoverURL(son.getString(ApiUtils.KEY_USR_COVERURL));
                                AppConfig.getAppConfig(SignUpActivity.this).setUserInfo(userInfo);
                                //TODO 已注册好 下一步干吗？
                                ToastUtils.showShort(SignUpActivity.this, "注册成功");
                                LogUtils.d(userInfo.toString());
                                //TODO
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
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
            mSendVerifyCodeButton.setEnabled(false);
            mSendVerifyCodeButton.setText(millisUntilFinished / 1000 + " 秒");
        }

        @Override
        public void onFinish() {
            mSendVerifyCodeButton.setEnabled(true);
            mSendVerifyCodeButton.setText("重发验证码");
        }
    }
}
