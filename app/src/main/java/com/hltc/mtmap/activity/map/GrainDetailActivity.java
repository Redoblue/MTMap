package com.hltc.mtmap.activity.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hltc.mtmap.R;

/**
 * ViewPager实现画廊效果
 *
 * @author Trinea 2013-04-03
 *         <p/>
 *         需要setOnTouchListener函数中将滑动滑动事件传递给viewPager，否则只有viewPager中间的view可以滑动，设置后整个viewPager都可以滑动。
 *         可能运行后出现viewpager的部分Fragment无法看见或是突然消失的问题，请确保RelativeLayout和ViewPager的android:
 *         clipChildren都设置为了false并且viewPager.setOffscreenPageLimit(TOTAL_COUNT);其中TOTAL_COUNT大于0.
 *         当然子Fragment本身不能是match_parent的。viewpager设置了paddingTop也会导致无法实现画廊而只是显示一屏。
 */
public class GrainDetailActivity extends FragmentActivity {

    private static int TOTAL_COUNT = 5;

    private RelativeLayout viewPagerContainer;
    private ViewPager photoViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grain_detail);

        photoViewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerContainer = (RelativeLayout) findViewById(R.id.pager_layout);
        photoViewPager.setAdapter(new MyPagerAdapter());
        // to cache all page, or we will see the right item delayed
        photoViewPager.setOffscreenPageLimit(TOTAL_COUNT);
        photoViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin));
        photoViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        viewPagerContainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                return photoViewPager.dispatchTouchEvent(event);
            }
        });
    }

    /**
     * this is a example fragment, just a imageview, u can replace it with your needs
     *
     * @author Trinea 2013-04-03
     */
    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return TOTAL_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(GrainDetailActivity.this);
            imageView.setImageResource(R.drawable.pic_guide_1 + position);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView, position);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // to refresh frameLayout
            if (viewPagerContainer != null) {
                viewPagerContainer.invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}