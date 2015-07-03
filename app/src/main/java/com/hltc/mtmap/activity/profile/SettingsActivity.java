package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.SingleEditActivity;
import com.hltc.mtmap.activity.profile.setting.FeedbackActivity;
import com.hltc.mtmap.activity.profile.setting.UpdateNicknameActivity;
import com.hltc.mtmap.activity.start.SignUpActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
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
 * Created by redoblue on 15-5-30.
 */
public class SettingsActivity extends Activity {

    public static final int UPDATE_NICKNAME_REQUEST_CODE = 0;

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.btn_settings_set_nickname)
    Button btnSettingsSetNickname;
    @InjectView(R.id.btn_settings_change_passwd)
    Button btnSettingsChangePasswd;
    @InjectView(R.id.btn_settings_feedback)
    Button btnSettingsFeedback;
    @InjectView(R.id.btn_settings_check_update)
    Button btnSettingsCheckUpdate;
    @InjectView(R.id.btn_settings_five_star)
    Button btnSettingsFiveStar;
    @InjectView(R.id.btn_settings_recommend)
    Button btnSettingsRecommend;
    @InjectView(R.id.btn_settings_about)
    Button btnSettingsAbout;
    @InjectView(R.id.btn_settings_logout)
    Button btnSettingsLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        tvBarTitle.setText("设置");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_settings_set_nickname,
            R.id.btn_settings_change_passwd,
            R.id.btn_settings_feedback,
            R.id.btn_settings_check_update,
            R.id.btn_settings_five_star,
            R.id.btn_settings_recommend,
            R.id.btn_settings_about})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_settings_set_nickname:
                Intent i1 = new Intent(this, SingleEditActivity.class);
                i1.putExtra("old", AppConfig.getAppConfig().getConfUsrNickName());
                startActivityForResult(i1, UPDATE_NICKNAME_REQUEST_CODE);
                break;
            case R.id.btn_settings_change_passwd:
                Intent i2 = new Intent(this, SignUpActivity.class);
                i2.putExtra("source", 1);
                startActivity(i2);
                break;
            case R.id.btn_settings_feedback:
                Intent i3 = new Intent(this, FeedbackActivity.class);
                startActivity(i3);
                break;
            case R.id.btn_settings_check_update:
                break;
            case R.id.btn_settings_five_star:
                break;
            case R.id.btn_settings_recommend:
                break;
            case R.id.btn_settings_about:
                break;
        }

//        if (v.getId() == R.id.btn_bar_left) {
//            return;
//        }
//
//        Intent intent = new Intent(this, generalClass);
//        if (generalClass == SignUpActivity.class) {
//            intent.putExtra("source", 1);
//        }
//        startActivity(intent);
    }

    @OnClick(R.id.btn_settings_logout)
    public void onClick() {
        AppUtils.logout();
        Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        AppManager.getAppManager().finishActivity(this);
        AppManager.getAppManager().finishActivity(MainActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_NICKNAME_REQUEST_CODE:
                    String newString = data.getStringExtra("new");
                    httpUpdateNickname(newString);
            }
        }
    }

    private void httpUpdateNickname(final String nickname) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
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
                                AppConfig.getAppConfig().setConfUsrNickName(nickname);

                                AppManager.getAppManager().finishActivity(UpdateNicknameActivity.class);
                                AppManager.getAppManager().finishActivity(SettingsActivity.class);

                                Toast.makeText(SettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SettingsActivity.this, errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SettingsActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
