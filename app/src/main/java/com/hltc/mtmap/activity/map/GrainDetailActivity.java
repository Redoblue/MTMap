package com.hltc.mtmap.activity.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.GrainDetailPhotoAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

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
    @InjectView(R.id.btn_bar_back)
    Button btnBarBack;
    @InjectView(R.id.btn_bar_favor)
    Button btnBarFavor;
    @InjectView(R.id.btn_bar_share)
    Button btnBarShare;
    @InjectView(R.id.civ_grain_detail_portrait)
    CircleImageView civGrainDetailPortrait;
    @InjectView(R.id.tv_grain_detail_nickname)
    TextView tvGrainDetailNickname;
    @InjectView(R.id.tv_grain_detail_text)
    TextView tvGrainDetailText;
    @InjectView(R.id.tv_grain_detail_address)
    TextView tvGrainDetailAddress;
    @InjectView(R.id.vp_grain_detail)
    ViewPager vpGrainDetail;
    @InjectView(R.id.layout_grain_detail_viewpager)
    RelativeLayout layoutGrainDetailViewpager;
    @InjectView(R.id.tv_grain_detail_time)
    TextView tvGrainDetailTime;
    @InjectView(R.id.btn_grain_detail_operations)
    Button btnGrainDetailOperations;
    @InjectView(R.id.tv_grain_detail_praise)
    TextView tvGrainDetailPraise;
    @InjectView(R.id.layout_grain_detail_comment)
    LinearLayout layoutGrainDetailComment;

    private RelativeLayout viewPagerContainer;
    private ViewPager photoViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grain_detail);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
//        vpGrainDetail.setAdapter(new GrainDetailPhotoAdapter(this,...));
        // to cache all pages, or we will see the right item delayed
        vpGrainDetail.setOffscreenPageLimit(TOTAL_COUNT);
        vpGrainDetail.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin));
        vpGrainDetail.setOnPageChangeListener(new MyOnPageChangeListener());
        layoutGrainDetailViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                return vpGrainDetail.dispatchTouchEvent(event);
            }
        });
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