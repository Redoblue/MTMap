package com.hltc.mtmap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;


/**
 * Created by merlin on 5/5/15.
 */
public class PublishActivity extends Activity implements View.OnClickListener {

    private TextView createChiheButton;
    private TextView createWanleButton;
    private TextView createOtherButton;
    private RelativeLayout exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish);
        AppManager.getAppManager().addActivity(this);

        findViewById();
        initView();
    }

    private void findViewById() {
        createChiheButton = (TextView) findViewById(R.id.tv_publish_chihe);
        createWanleButton = (TextView) findViewById(R.id.tv_publish_wanle);
        createOtherButton = (TextView) findViewById(R.id.tv_publish_other);
        exitButton = (RelativeLayout) findViewById(R.id.layout_publish_exit);
    }

    private void initView() {
        createChiheButton.setOnClickListener(this);
        createWanleButton.setOnClickListener(this);
        createOtherButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_publish_chihe:
                //TODO
                break;
            case R.id.tv_publish_wanle:
                //TODO
                break;
            case R.id.tv_publish_other:
                //TODO
                break;
            case R.id.layout_publish_exit:
                AppManager.getAppManager().finishActivity(this);
                break;
        }
    }
}
