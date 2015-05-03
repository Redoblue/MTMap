package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.helper.ProgressGenerator;
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

/**
 * Created by Redoblue on 2015/4/26.
 */
public class SignInActivity extends Activity implements View.OnClickListener, ProgressGenerator.OnCompleteListener {

    private Button mBarLeftButton;
    private TextView mTitleTextView;
    private Button mBarRightButton;
    private EditText mPhoneEditText;
    private EditText mPasswdEditText;
    private ActionProcessButton mSignInProcessButton;

    private ProgressGenerator mGenerator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signin);
        AppManager.getAppManager().addActivity(this);

        findViewById();
        initViews();
        mGenerator = new ProgressGenerator(this);
    }

    private void findViewById() {
        mBarLeftButton = (Button) findViewById(R.id.btn_bar_left);
        mTitleTextView = (TextView) findViewById(R.id.tv_bar_title);
        mBarRightButton = (Button) findViewById(R.id.btn_bar_right);
        mPhoneEditText = (EditText) findViewById(R.id.et_signin_phone);
        mPasswdEditText = (EditText) findViewById(R.id.et_signin_passwd);
        mSignInProcessButton = (ActionProcessButton) findViewById(R.id.btn_process_signin);
    }

    private void initViews() {
        mBarLeftButton.setBackgroundResource(R.drawable.back);
        mBarLeftButton.setOnClickListener(this);
        mTitleTextView.setText(R.string.sign_in);
        mBarRightButton.setText(R.string.signin_bar_right);
        mBarRightButton.setOnClickListener(this);
        mPhoneEditText.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_phone)));
        mPasswdEditText.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_password)));
        mSignInProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        mSignInProcessButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.btn_bar_left && id != R.id.btn_bar_right
                && !AppUtils.isNetworkConnected(this)) {
            ToastUtils.showShort(this, "网络连接失败");
            return;
        }
        switch (id) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                // TODO
                break;
            case R.id.btn_process_signin:
                mSignInProcessButton.setEnabled(false);
//                mSignInProcessButton.setClickable(true);
                mPasswdEditText.setEnabled(false);
                mGenerator.start(mSignInProcessButton);
                httpSignIn();
                mGenerator.stop();
                break;
        }
    }

    @Override
    public void onComplete() {
        mSignInProcessButton.setEnabled(true);
//        mSignInProcessButton.setClickable(true);
        mPasswdEditText.setEnabled(true);
    }

    private void httpSignIn() {
        // button begin to animate
        mGenerator.start(mSignInProcessButton);

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_UNIQUE_INFO, mPhoneEditText.getText());
            json.put(ApiUtils.KEY_PASSWD, StringUtils.toMD5(mPasswdEditText.getText().toString()));
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getRequestVCodeUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                LocalUserInfo userInfo = new LocalUserInfo();
                                userInfo.setId(son.getLong(ApiUtils.KEY_USR_ID));
                                userInfo.setNickname(son.getString(ApiUtils.KEY_USR_NICKNAME));
                                userInfo.setCreateTime(son.getString(ApiUtils.KEY_USR_CREATE_TIME));
                                userInfo.setAvatarURL(son.getString(ApiUtils.KEY_USR_AVATARURL));
                                userInfo.setRawAvatarURL(son.getString(ApiUtils.KEY_USR_RAW_AVATARURL));
                                userInfo.setPhone(son.getString(ApiUtils.KEY_USR_PHONE));
                                userInfo.setCoverURL(son.getString(ApiUtils.KEY_USR_COVERURL));
                                AppConfig.getAppConfig(SignInActivity.this).setUserInfo(userInfo);
                                // 进入主界面
                                ToastUtils.showShort(SignInActivity.this, "登录成功");
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                AppManager.getAppManager().finishActivity(SignInActivity.this);
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SignInActivity.this, errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        mGenerator.stop();
                    }
                });
    }
}
