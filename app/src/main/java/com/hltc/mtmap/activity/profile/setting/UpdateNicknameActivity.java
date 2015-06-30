package com.hltc.mtmap.activity.profile.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.SettingsActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
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
 * Created by redoblue on 15-6-1.
 */
public class UpdateNicknameActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.et_single_line)
    EditText etSingleLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_nickname);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        tvBarTitle.setText("修改昵称");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarRight.setBackgroundResource(R.drawable.ic_action_done);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));
        btnBarRight.setWidth(AMapUtils.dp2px(this, 25));
        btnBarRight.setHeight(AMapUtils.dp2px(this, 25));

        String nickname = AppConfig.getAppConfig(this).getConfUsrNickName();
        etSingleLine.setText(nickname);
        etSingleLine.setSelection(nickname.length());
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_bar_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                String newNickname = etSingleLine.getText().toString();
                httpUpdateNickname(newNickname);
                break;
        }
    }

    private void httpUpdateNickname(final String nickname) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            json.put(ApiUtils.KEY_USR_NICKNAME, nickname);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getUpdateNicknameUrl(),
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
                                AppConfig.getAppConfig(getApplicationContext()).setConfUsrNickName(nickname);

                                AppManager.getAppManager().finishActivity(UpdateNicknameActivity.class);
                                AppManager.getAppManager().finishActivity(SettingsActivity.class);

                                Toast.makeText(UpdateNicknameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(UpdateNicknameActivity.this, errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(UpdateNicknameActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
