package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.GuidePagerAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Redoblue on 2015/4/18.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnTouchListener {

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
    //    @InjectView(R.id.btn_guide_enter)
//    Button enterBtn;
    private GuidePagerAdapter adapter;
    private List<View> views = new ArrayList<>();
    private List<ImageView> dotViews;
    private int index;
    private Map<Integer, Float> x = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        initViews();
        initDots();
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
            iv.setOnTouchListener(this);
            views.add(iv);
        }

        adapter = new GuidePagerAdapter(views);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
    }

    private void initDots() {
        dotViews = new ArrayList<>();
        for (int i = 0; i < pics.length; i++) {
            dotViews.add(i, (ImageView) layout.getChildAt(i));
            dotViews.get(i).setEnabled(true);
            dotViews.get(i).setTag(i);//set position tag
        }

        index = 0;
        dotViews.get(index).setEnabled(false);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position >= pics.length || position == index)
            return;
        dotViews.get(index).setEnabled(true);
        dotViews.get(position).setEnabled(false);

        index = position;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        setCurrentDot(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (index == pics.length - 1) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x.clear();
                x.put(x.size(), event.getX());
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                x.put(x.size(), event.getX());
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                x.put(x.size(), event.getX());
                if (x.size() < 3) {
                    x.clear();
                    return true;
                }
                float average = 0f;
                for (int i = 0; i < x.size(); i++) {
                    average += x.get(i) / x.size();
                }
                if (x.get(x.size() - 1) < average) {
                    // 设置第一次使用为False
                    AppConfig.getAppConfig(this).set(AppConfig.CONFIG_APP, AppConfig.CONF_FIRST_USE, "false");

                    Intent intent = new Intent(this, StartActivity.class);
                    startActivity(intent);
                    AppManager.getAppManager().finishActivity(GuideActivity.this);
                }
                x.clear();
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
