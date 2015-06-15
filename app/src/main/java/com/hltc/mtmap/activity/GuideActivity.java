package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.GuidePagerAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.AppUtils;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Redoblue on 2015/4/18.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {

    //引导图片资源
    private static final int[] pics = {
            R.drawable.pic_guide_1,
            R.drawable.pic_guide_2,
            R.drawable.pic_guide_3,
            R.drawable.pic_guide_4,
            R.drawable.pic_guide_5
    };
    @InjectView(R.id.pager_guide)
    ViewPager pager;
    @InjectView(R.id.layout_guide_dots)
    LinearLayout layout;
    @InjectView(R.id.btn_guide_enter)
    Button enterBtn;
    private GuidePagerAdapter adapter;
    private List<View> views = new ArrayList<>();
    //    private List<ImageView> dotViews = new ArrayList<>();
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        initViews();
//        initDots();
    }

    @OnClick(R.id.btn_guide_enter)
    public void onClick() {
        // 设置第一次使用为False
        AppConfig.getAppConfig(this).set(AppConfig.CONF_FIRST_USE, "false");

        //TODO 判断是否登陆
        Class toClass;
        if (AppUtils.isSignedIn(this)) {
            LogUtils.d("已登录分支");
            toClass = MainActivity.class;
        } else {
            LogUtils.d("未登录分支");
            toClass = StartActivity.class;
        }
        Intent intent = new Intent(this, toClass);
        startActivity(intent);
        AppManager.getAppManager().finishActivity(GuideActivity.this);
    }

    private void initViews() {
        views = new ArrayList<>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(params);
            iv.setImageResource(pics[i]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            views.add(iv);
        }

        adapter = new GuidePagerAdapter(views);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
    }

//    private void initDots() {
//        for (int i = 0; i < pics.length; i++) {
//            dotViews.add(i, (ImageView) layout.getChildAt(i));
//            dotViews.get(i).setEnabled(true);
//            dotViews.get(i).setTag(i);//set position tag
//        }
//
//        index = 0;
//        dotViews.get(index).setEnabled(false);
//    }
//
//    private void setCurrentDot(int position) {
//        if (position < 0 || position >= pics.length || position == index)
//            return;
//        dotViews.get(index).setEnabled(true);
//        dotViews.get(position).setEnabled(false);
//
//        index = position;
//    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
//        setCurrentDot(i);
        if (i == pics.length - 1) {
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
