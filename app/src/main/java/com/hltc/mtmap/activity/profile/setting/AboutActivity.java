package com.hltc.mtmap.activity.profile.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AboutActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_bar_left)
    public void exit() {
        AppManager.getAppManager().finishActivity(this);
    }
}
