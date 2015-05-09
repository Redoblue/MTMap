package com.hltc.mtmap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hltc.mtmap.R;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ToastUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileFragment extends Fragment {

    @InjectView(R.id.sv_profile)
    PullToZoomScrollViewEx scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
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

        scrollView.getPullRootView().findViewById(R.id.btn_profile_settings).setOnClickListener(onClickListener);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_maitian).setOnClickListener(onClickListener);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_favourite).setOnClickListener(onClickListener);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_friend).setOnClickListener(onClickListener);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_gallery).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_profile_settings:
                    ToastUtils.showShort(getActivity(), "settings");
                    break;
                case R.id.btn_profile_maitian:
                    ToastUtils.showShort(getActivity(), "maitian");
                    break;
                case R.id.btn_profile_favourite:
                    break;
                case R.id.btn_profile_friend:
                    break;
                case R.id.btn_profile_gallery:
                    break;
                default:
                    break;
            }
        }
    };
}
