package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.map.LargeImageActivity;
import com.hltc.mtmap.activity.profile.setting.FriendSettingActivity;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.event.BaseMessageEvent;
import com.hltc.mtmap.gmodel.FriendProfile;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-7-12.
 */
public class FriendProfileActivity extends Activity {

    public static final String TAG = "FriendProfileActivity";
    @InjectView(R.id.sv_profile)
    PullToZoomScrollViewEx scrollView;

    private FriendProfile mProfile;

    private Button btnSetting;

    private Button btnBack;
    private TextView nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.fragment_profile);
        ButterKnife.inject(this);

        EventBus.getDefault().register(this);
        mProfile = getIntent().getParcelableExtra("friend");

        initView();
    }

    public void onEvent(BaseMessageEvent event) {
        switch (event.action) {
            case BaseMessageEvent.EVENT_DELETE_USER:
                AppManager.getAppManager().finishActivity(this);
                break;
            default:
                break;
        }

    }

    private void initView() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.fragment_profile_header_view, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.fragment_profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(this).inflate(R.layout.fragment_profile_content_view, null, false);
        scrollView.setHeaderView(headerView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, AMapUtils.dp2px(this, 270));
        scrollView.setHeaderLayoutParams(localObject);

        // 我的朋友的页面忽略收藏和朋友两个Tab
        View viewFavourite = scrollView.getPullRootView().findViewById(R.id.btn_profile_favourite);
        View viewFriends = scrollView.getPullRootView().findViewById(R.id.btn_profile_friend);
        viewFavourite.setVisibility(View.GONE);
        viewFriends.setVisibility(View.GONE);

        btnSetting = (Button) scrollView.getPullRootView().findViewById(R.id.btn_profile_settings);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendSettingActivity.startForResult(FriendProfileActivity.this, mProfile);
            }
        });
        btnBack = (Button) scrollView.getPullRootView().findViewById(R.id.btn_bar_left);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(FriendProfileActivity.this);
            }
        });

        Button maitian = (Button) scrollView.getPullRootView().findViewById(R.id.btn_profile_maitian);
        maitian.setText("Ta的麦田");
        maitian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendGrainActivity.start(FriendProfileActivity.this, mProfile);
            }
        });

        //编辑头像
        CircleImageView portraitCiv = (CircleImageView) scrollView.getPullRootView().findViewById(R.id.civ_profile_header_pic);
        ImageLoader.getInstance().displayImage(OssManager.getRemotePortraitUrl(mProfile.user.portrait),
                portraitCiv, MyApplication.displayImageOptions);
        portraitCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendProfileActivity.this, LargeImageActivity.class);
                intent.putExtra("image", mProfile.user.portrait);
                startActivity(intent);
            }
        });
        ImageView cover = (ImageView) scrollView.getPullRootView().findViewById(R.id.iv_profile_header_cover);
        ImageLoader.getInstance().displayImage(mProfile.user.coverImg, cover, MyApplication.displayImageOptions);

        //昵称和签名
        nickName = (TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_header_nickname);
        nickName.setText(StringUtils.isEmpty(mProfile.user.remark) ? mProfile.user.nickName : mProfile.user.remark);

        //更新麦粒数量
        ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_chihe))
                .setText(mProfile.grainStatistics.chihe + "");
        ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_wanle))
                .setText(mProfile.grainStatistics.wanle + "");
        ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_other))
                .setText(mProfile.grainStatistics.other + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == FriendSettingActivity.RESULT_CODE_FROM_FRIENDSETTING) {
            mProfile.user.remark = data.getStringExtra(TAG);
            nickName.setText(mProfile.user.remark);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
