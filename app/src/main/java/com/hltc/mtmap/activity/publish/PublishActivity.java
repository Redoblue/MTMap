package com.hltc.mtmap.activity.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.GuideUtils;
import com.hltc.mtmap.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by merlin on 5/5/15.
 */
public class PublishActivity extends Activity {

    public static final int CREATE_CHIHE = 0;
    public static final int CREATE_WANLE = 1;
    public static final int CREATE_OTHER = 2;
    @InjectView(R.id.tv_publish_chihe)
    TextView createChihe;
    @InjectView(R.id.tv_publish_wanle)
    TextView createWanle;
    @InjectView(R.id.tv_publish_other)
    TextView createOther;
    @InjectView(R.id.layout_publish_exit)
    RelativeLayout exitButton;
    private List<TextView> textViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        initView();
        initGuide();
    }

    private void initGuide() {
        //显示遮罩
        if (!AppUtils.isGuidePresented(AppConfig.CONF_GUIDE_PUBLISH)) {
            GuideUtils guideUtil = GuideUtils.getInstance();
            guideUtil.initGuide(this, R.drawable.guide_publish);
            AppConfig.getAppConfig().set(AppConfig.CONFIG_APP, AppConfig.CONF_GUIDE_PUBLISH, "true");
        }
    }

    private void initView() {
        textViews = new ArrayList<>();
        textViews.add(createChihe);
        textViews.add(createWanle);
        textViews.add(createOther);

        for (TextView view : textViews) {
            Animation animation = new TranslateAnimation(
                    view.getTranslationX(), view.getTranslationX(),
                    1300, view.getTranslationY());
            animation.setDuration(800);

            view.setOnTouchListener(new MyOnTouchListener(this));
            view.startAnimation(animation);
        }
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(PublishActivity.this);
            }
        });
    }

    private int getCreateType(View v) {
        switch (v.getId()) {
            case R.id.tv_publish_chihe:
                return CREATE_CHIHE;
            case R.id.tv_publish_wanle:
                return CREATE_WANLE;
            case R.id.tv_publish_other:
                return CREATE_OTHER;
            default:
                return -1;
        }
    }

    /**
     * ****************** Life Cycle ********************
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }

    private class MyOnTouchListener implements View.OnTouchListener {

        Context mContext;

        MyOnTouchListener(Context context) {
            mContext = context;
        }

        public boolean onTouch(View v, MotionEvent event) {
            TextView tv = (TextView) v;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tv.setTextColor(getResources().getColor(R.color.transparent));
                Animation scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_down_profile_create);
                v.startAnimation(scaleAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Animation scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_down_profile_create);
                v.startAnimation(scaleAnimation);
                tv.setTextColor(getResources().getColor(R.color.white));

                int type = getCreateType(v);
                LogUtils.d(getClass(), String.valueOf(type));///
                if (type != -1) {
                    Intent intent = new Intent(mContext, CreateGrainActivity2.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    AppManager.getAppManager().finishActivity(PublishActivity.this);
                }
            }

            return true;
        }
    }
}
