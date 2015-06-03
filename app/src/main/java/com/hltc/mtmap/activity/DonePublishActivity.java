package com.hltc.mtmap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.ParcelableGrain;
import com.hltc.mtmap.task.PublishAsyncTask;

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


    private ParcelableGrain grain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_done_publish);
        setFinishOnTouchOutside(false);
        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        grain = getIntent().getParcelableExtra("GRAIN");
        Log.d("Publish", "received grain: " + grain.toString());

        new PublishAsyncTask(this, layoutDonePublishProgress, uploadCircleProgress, grain).execute();
    }
}
