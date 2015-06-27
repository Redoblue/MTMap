package com.hltc.mtmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.profile.FriendListActivity;
import com.hltc.mtmap.activity.profile.SettingsActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ToastUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.sv_profile)
    PullToZoomScrollViewEx scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (MainActivity.isVisitor) {
            View view = inflater.inflate(R.layout.window_remind_login, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.btn_remind_login);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.inject(this, view);
            initView();
            return view;
        }
    }

    private void initView() {
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_header_view, null, false);
        View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_content_view, null, false);
        scrollView.setHeaderView(headerView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
//        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenHeight / 16.0F)));
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, AMapUtils.dp2px(getActivity(), 270));
        scrollView.setHeaderLayoutParams(localObject);

        scrollView.getPullRootView().findViewById(R.id.btn_profile_settings).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_maitian).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_favourite).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_friend).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_gallery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Class toClass = null;
        switch (v.getId()) {
            case R.id.btn_profile_settings:
                toClass = SettingsActivity.class;
                break;
            case R.id.btn_profile_maitian:
                ToastUtils.showShort(getActivity(), "maitian");
                break;
            case R.id.btn_profile_favourite:
                break;
            case R.id.btn_profile_friend:
                toClass = FriendListActivity.class;
                break;
            case R.id.btn_profile_gallery:
                break;
            default:
                break;
        }
        Intent intent = new Intent(getActivity(), toClass);
        startActivity(intent);
    }

}
