package com.hltc.mtmap.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by X-MH on 2015/8/28.
 */
public class ImageViewerActivity extends Activity {

    @InjectView(R.id.vp_image_show)
    ViewPager mViewPagerImageShow;
    private String[] imageurlList;
    private ImageView[] imageViews;
    private ImageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_imagepager);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);
        initData();
        initView();
    }

    private void initView() {
        mAdapter = new ImageAdapter();
        mViewPagerImageShow.setAdapter(mAdapter);
    }

    private void initData() {
        Intent intent = getIntent();
        imageurlList = intent.getStringArrayExtra("imageUrllist");
        imageViews = new ImageView[imageurlList.length];
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < imageurlList.length; i++) {
            imageViews[i] = new ImageView(this);
            imageViews[i].setLayoutParams(lp);
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.getAppManager().finishActivity(ImageViewerActivity.this);
                }
            });
            ImageLoader.getInstance().displayImage(imageurlList[i], imageViews[i], MyApplication.displayImageOptions);
        }
    }

    public static void start(Activity fromActivity, List<String> imageurlList) {
        if (imageurlList == null || imageurlList.size() == 0) return;
        String[] imagesArray = new String[imageurlList.size()];
        for (int i = 0; i < imageurlList.size(); i++)
            imagesArray[i] = imageurlList.get(i);
        Intent intent = new Intent(fromActivity, ImageViewerActivity.class);
        intent.putExtra("imageUrllist", imagesArray);
        fromActivity.startActivity(intent);
    }

    private class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageurlList.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews[position]);
            return imageViews[position];
        }
    }

}
