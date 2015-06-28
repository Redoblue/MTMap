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
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.profile.FriendListActivity;
import com.hltc.mtmap.activity.profile.SettingsActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_header_view, null, false);
        View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_content_view, null, false);
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

        //更新麦粒数量
        httpGetGrainNumber();
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
            default:
                break;
        }
        Intent intent = new Intent(getActivity(), toClass);
        startActivity(intent);
    }

    private void httpGetGrainNumber() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USR_ID, AppConfig.getAppConfig(getActivity()).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(getActivity()).getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_GRAIN_NUMBER,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_chihe))
                                        .setText(String.valueOf(data.getInt(ApiUtils.KEY_GRAIN_CHIHE)));
                                ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_wanle))
                                        .setText(String.valueOf(data.getInt(ApiUtils.KEY_GRAIN_WANLE)));
                                ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_other))
                                        .setText(String.valueOf(data.getInt(ApiUtils.KEY_GRAIN_OTHER)));
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(getActivity(), errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        httpGetGrainNumber();
    }
}
