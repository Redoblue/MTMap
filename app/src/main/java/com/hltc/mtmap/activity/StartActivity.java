package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.Button;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Redoblue on 2015/4/18.
 */
public class StartActivity extends Activity {

    @InjectView(R.id.img_start_bg)
    KenBurnsView kenBurnsView;
    @InjectView(R.id.btn_start_signin)
    Button signInBtn;
    @InjectView(R.id.btn_start_signup)
    Button signUpBtn;
    @InjectView(R.id.btn_start_skip)
    Button skipBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

//        initBackground();
    }

    private void initBackground() {
        RandomTransitionGenerator generator = new RandomTransitionGenerator(5, new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 0;
            }
        });
        kenBurnsView.setTransitionGenerator(generator);
    }

    @OnClick({R.id.btn_start_signin,
            R.id.btn_start_signup,
            R.id.btn_start_skip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_signin:
                Intent intent0 = new Intent(this, SignInActivity.class);
                startActivity(intent0);
                break;
            case R.id.btn_start_signup:
                Intent intent1 = new Intent(this, SignUpActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_start_skip:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
