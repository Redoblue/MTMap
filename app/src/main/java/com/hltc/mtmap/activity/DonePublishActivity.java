package com.hltc.mtmap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-5-14.
 */
public class DonePublishActivity extends Activity {

    @InjectView(R.id.btn_done_publish_home)
    Button goBackHomeButton;
    @InjectView(R.id.btn_done_publish_maitian)
    Button goToMaitionButton;
    @InjectView(R.id.btn_done_publish_share)
    Button shareButton;
    @InjectView(R.id.cp_done_publish_upload)
    CircleProgress uploadCircleProgress;
    @InjectView(R.id.layout_done_publish_progress)
    RelativeLayout layoutDonePublishProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_done_publish);
        ButterKnife.inject(this);
    }

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}
