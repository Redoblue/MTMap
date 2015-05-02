package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.GuidePagerAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.AppUtils;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redoblue on 2015/4/18.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {

    private ViewPager pager;
    private LinearLayout layout;
    private Button enterBtn;

    private GuidePagerAdapter adapter;
    private List<View> views;

    private Context mContext;

    //引导图片资源
    private static final int[] pics = {
            R.drawable.guide_1,
            R.drawable.guide_2,
            R.drawable.guide_3,
            R.drawable.guide_4
    };

    private ImageView dotViews[];

    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        LogUtils.d("GuideActivity Created");
        AppManager.getAppManager().addActivity(this);

        mContext = this;

        findViewById();
        initViews();
        initDots();
    }

    private void findViewById() {
        pager = (ViewPager) findViewById(R.id.pager_guide);
        layout = (LinearLayout) findViewById(R.id.layout_guide_dots);
        enterBtn = (Button) findViewById(R.id.btn_guide_enter);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置第一次使用为False
                AppConfig.getAppConfig(mContext).set(AppConfig.CONF_FIRST_USE, "false");

                //TODO 判断是否登陆
                if (AppUtils.isSignedIn(mContext)) {
                    LogUtils.d("已登录分支");
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                } else {
                    LogUtils.d("未登录分支");
                    Intent intent = new Intent(mContext, StartActivity.class);
                    startActivity(intent);
                }
                AppManager.getAppManager().finishActivity(GuideActivity.this);
            }
        });
    }

    private void initViews() {
        views = new ArrayList<View>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(params);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        adapter = new GuidePagerAdapter(views);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
    }

    private void initDots() {
        dotViews = new ImageView[pics.length];
        for (int i = 0; i < pics.length; i++) {
            dotViews[i] = (ImageView) layout.getChildAt(i);
            dotViews[i].setEnabled(true);
            dotViews[i].setTag(i);//set position tag
        }

        index = 0;
        dotViews[index].setEnabled(false);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position >= pics.length || position == index)
            return;

        dotViews[index].setEnabled(true);
        dotViews[position].setEnabled(false);

        index = position;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        setCurrentDot(i);
        if (i == 3) {
            enterBtn.setVisibility(View.VISIBLE);
        } else {
            enterBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
