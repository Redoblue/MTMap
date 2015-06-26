package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.setting.FeedbackActivity;
import com.hltc.mtmap.activity.profile.setting.UpdateNicknameActivity;
import com.hltc.mtmap.activity.start.SignUpActivity;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.AMapUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-5-30.
 */
public class SettingsActivity extends Activity {
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
        Class generalClass = null;
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_settings_set_nickname:
                generalClass = UpdateNicknameActivity.class;
                break;
            case R.id.btn_settings_change_passwd:
                generalClass = SignUpActivity.class;
                break;
            case R.id.btn_settings_feedback:
                generalClass = FeedbackActivity.class;
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

        if (v.getId() == R.id.btn_bar_left) {
            return;
        }

        Intent intent = new Intent(this, generalClass);
        if (generalClass == SignUpActivity.class) {
            intent.putExtra("source", 1);
        }
        startActivity(intent);
    }

}
