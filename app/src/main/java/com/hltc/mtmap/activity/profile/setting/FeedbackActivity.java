package com.hltc.mtmap.activity.profile.setting;

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

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
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
 * Created by redoblue on 15-6-5.
 */
public class FeedbackActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.et_feedback_content)
    EditText etFeedbackContent;
    @InjectView(R.id.et_feedback_email)
    EditText etFeedbackEmail;
    @InjectView(R.id.btn_feedback_submit)
    Button btnFeedbackSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        tvBarTitle.setText("用户反馈");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));

        btnFeedbackSubmit.setEnabled(false);
        etFeedbackEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnFeedbackSubmit.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.btn_bar_left, R.id.btn_feedback_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_feedback_submit:
                httpFeedBack();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    private void httpFeedBack() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            json.put(ApiUtils.KEY_CONTENT, etFeedbackContent.getText().toString());
            json.put(ApiUtils.KEY_EMAIL, etFeedbackEmail.getText().toString());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getFeedbackUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                //TODO
                                Intent intent = new Intent(MyApplication.getContext(), FeedbackSuccessDialog.class);
                                startActivity(intent);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(MyApplication.getContext(), errorMsg);
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
