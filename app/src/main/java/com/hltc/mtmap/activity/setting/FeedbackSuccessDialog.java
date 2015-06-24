package com.hltc.mtmap.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.SettingsActivity;
import com.hltc.mtmap.app.AppManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-6-24.
 */
public class FeedbackSuccessDialog extends Activity {

    @InjectView(R.id.tv_feedback_success_home)
    TextView goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_feedback_success);
        setFinishOnTouchOutside(false);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);
    }

    @OnClick(R.id.tv_feedback_success_home)
    public void onClick(View v) {
        if (v.getId() == R.id.tv_feedback_success_home) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
            AppManager.getAppManager().finishActivity(FeedbackSuccessDialog.class);
            AppManager.getAppManager().finishActivity(FeedbackActivity.class);
            AppManager.getAppManager().finishActivity(SettingsActivity.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
