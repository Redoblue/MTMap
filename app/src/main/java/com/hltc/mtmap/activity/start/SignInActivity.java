package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.helper.ProgressGenerator;
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
 * Created by Redoblue on 2015/4/26.
 */
public class SignInActivity extends Activity implements ProgressGenerator.OnCompleteListener {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.btn_bar_right)
    TextView btnBarRight;
    @InjectView(R.id.et_signin_phone)
    EditText etSigninPhone;
    @InjectView(R.id.et_signin_passwd)
    EditText etSigninPasswd;
    @InjectView(R.id.btn_process_signin)
    ActionProcessButton btnProcessSignin;

    private ProgressGenerator mGenerator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signin);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        initViews();
        mGenerator = new ProgressGenerator(this);
    }

    private void initViews() {
        etSigninPhone.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_phone), 20));
        etSigninPasswd.setHint(ViewUtils.getHint(getResources().getString(R.string.hint_password), 20));
        etSigninPasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnProcessSignin.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnProcessSignin.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_bar_right,
            R.id.btn_process_signin})
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
                Intent intent = new Intent(this, SignUpActivity.class);
                intent.putExtra("source", 1);
                startActivity(intent);
                break;
            case R.id.btn_process_signin:
                if (StringUtils.isEmpty(etSigninPhone.getText().toString())
                        || StringUtils.isEmpty(etSigninPasswd.getText().toString())) {
                    Toast.makeText(this, "请完善登录信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnProcessSignin.setEnabled(false);
//                btnProcessSignin.setClickable(true);
                etSigninPasswd.setEnabled(false);
                httpSignIn();
                break;
        }
    }

    @Override
    public void onComplete() {
        btnProcessSignin.setEnabled(true);
//        btnProcessSignin.setClickable(true);
        etSigninPasswd.setEnabled(true);
    }

    private void httpSignIn() {
        // button begin to animate
        mGenerator.start(btnProcessSignin);

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_UNIQUE_INFO, etSigninPhone.getText());
            json.put(ApiUtils.KEY_PASSWD, StringUtils.toMD5(etSigninPasswd.getText().toString()));
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.configTimeout(5000);
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getSigninUrl(),
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
                                //更新身份状态
                                MyApplication.signInStatus = "11";

                                //同步数据
                                new SyncDataAsyncTask().execute();

                                // 进入主界面
                                Toast.makeText(SignInActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                AppManager.getAppManager().finishActivity(SignInActivity.this);
                                AppManager.getAppManager().finishActivity(StartActivity.class);
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
                        Toast.makeText(SignInActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
