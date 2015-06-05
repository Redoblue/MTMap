package com.hltc.mtmap.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.util.AMapUtils;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-6-3.
 */
public class UpdatePasswdActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.et_update_passwd_phone)
    EditText etUpdatePasswdPhone;
    @InjectView(R.id.btn_update_passwd_send_verifycode)
    Button btnUpdatePasswdSendVerifycode;
    @InjectView(R.id.et_update_passwd_verifycode)
    EditText etUpdatePasswdVerifycode;
    @InjectView(R.id.btn_update_passwd_comfirm)
    Button btnUpdatePasswdComfirm;
    @InjectView(R.id.layout_update_passwd_step_one)
    LinearLayout layoutUpdatePasswdStepOne;
    @InjectView(R.id.et_update_passwd_passwd)
    EditText etUpdatePasswdPasswd;
    @InjectView(R.id.btn_update_passwd_create)
    Button btnUpdatePasswdCreate;
    @InjectView(R.id.layout_update_passwd_step_two)
    LinearLayout layoutUpdatePasswdStepTwo;

    private DownTimer mDownTimer;
    private String mPhone;
    private String mVCode;
    private String mPasswd;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            btnUpdatePasswdComfirm.setEnabled(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_passwd);
        ButterKnife.inject(this);

        initView();
        mDownTimer = new DownTimer(60000, 1000);
        AppManager.getAppManager().addActivity(this);
    }

    private void initView() {
        tvBarTitle.setText("修改密码");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));
        etUpdatePasswdPhone.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_phone), 20));
        etUpdatePasswdVerifycode.setHint(ViewUtils.getHint(getResources().getString(R.string.singup_verifycode_hint), 20));
        etUpdatePasswdPasswd.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_password), 20));
        etUpdatePasswdVerifycode.addTextChangedListener(watcher);
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_update_passwd_send_verifycode,
            R.id.btn_update_passwd_comfirm,
            R.id.btn_update_passwd_create})
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
            case R.id.btn_update_passwd_send_verifycode:
                String phone = etUpdatePasswdPhone.getText().toString();
                if (!StringUtils.isMobilePhone(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                    break;
                }
//                ToastUtils.showShort(this, "验证码发送成功");
                mDownTimer.start();
                mPhone = phone;
                httpSubmitPhone();
                break;
            case R.id.btn_update_passwd_comfirm:
                String vcode = etUpdatePasswdVerifycode.getText().toString();
                if (!StringUtils.isVerifyCode(vcode)) {
                    ToastUtils.showShort(this, "验证码格式错误");
                    break;
                }
                mVCode = vcode;
                httpValidateVCode();
                break;
            case R.id.btn_update_passwd_create:
                String passwd = etUpdatePasswdPasswd.getText().toString();
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
                                ToastUtils.showShort(UpdatePasswdActivity.this, "验证码发送成功");
                            } else {
                                JSONObject object = new JSONObject(result);
                                String errorMsg = object.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(UpdatePasswdActivity.this, errorMsg);
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
                                ToastUtils.showShort(UpdatePasswdActivity.this, "验证成功");
                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                String tmpToken = son.getString(ApiUtils.KEY_TMP_TOKEN);
                                AppConfig.getAppConfig(UpdatePasswdActivity.this).setToken(tmpToken);     //将临时Token保存
                                // 进行布局转换
                                layoutUpdatePasswdStepOne.setVisibility(View.GONE);
                                layoutUpdatePasswdStepTwo.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(UpdatePasswdActivity.this, errorMsg);
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
                                AppConfig.getAppConfig(UpdatePasswdActivity.this).setUserInfo(userInfo);
                                //TODO 已注册好 下一步干吗？
                                ToastUtils.showShort(UpdatePasswdActivity.this, "注册成功");
                                LogUtils.d(userInfo.toString());
                                //TODO
                                Intent intent = new Intent(UpdatePasswdActivity.this, MainActivity.class);
                                startActivity(intent);
                                AppManager.getAppManager().finishActivity(UpdatePasswdActivity.this);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(UpdatePasswdActivity.this, errorMsg);
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
            btnUpdatePasswdComfirm.setEnabled(false);
            btnUpdatePasswdComfirm.setText(millisUntilFinished / 1000 + " 秒");
        }

        @Override
        public void onFinish() {
            btnUpdatePasswdComfirm.setEnabled(true);
            btnUpdatePasswdComfirm.setText("重发验证码");
        }
    }
}
