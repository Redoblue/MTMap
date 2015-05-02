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

/**
 * Created by Redoblue on 2015/4/18.
 */
public class StartActivity extends Activity implements View.OnClickListener {

    private KenBurnsView kenBurnsView;
    private Button signInBtn;
    private Button signUpBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        AppManager.getAppManager().addActivity(this);

//        initBackground();
        findViewById();
        initViews();
    }

    private void findViewById() {
        kenBurnsView = (KenBurnsView) findViewById(R.id.img_start_bg);
        signInBtn = (Button) findViewById(R.id.btn_start_signin);
        signUpBtn = (Button) findViewById(R.id.btn_start_signup);
    }

    private void initViews() {
        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_signin:
                Intent signinIntent = new Intent(this, SignInActivity.class);
                startActivity(signinIntent);
                break;
            case R.id.btn_start_signup:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
