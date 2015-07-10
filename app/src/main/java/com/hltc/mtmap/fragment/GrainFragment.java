package com.hltc.mtmap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.gmodel.SwipeGrain;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GrainFragment extends Fragment {

    @InjectView(R.id.btn_grain_ignore)
    Button btnGrainIgnore;
    @InjectView(R.id.btn_grain_favourite)
    Button btnGrainFavourite;
    @InjectView(R.id.view_grain_swipe)
    SwipeFlingAdapterView viewGrainSwipe;
    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.iv_grain_cover)
    ImageView ivGrainCover;

    private List<SwipeGrain> mSwipeItems;
    private SwipeViewAdapter mSwipeAdapter;

    private boolean stateNomore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grain, container, false);
        ButterKnife.inject(this, view);

        initView();
        return view;
    }

    private void initView() {
        mSwipeItems = new ArrayList<>();
        mSwipeAdapter = new SwipeViewAdapter(getActivity(), mSwipeItems, R.layout.item_grain_card);
        viewGrainSwipe.setAdapter(mSwipeAdapter);
        if (AppUtils.isNetworkConnected(getActivity())) {
            httpLoadData(); //首次加载数据
        } else {
            ivGrainCover.setVisibility(View.VISIBLE);
            viewGrainSwipe.setEnabled(false);
            ivGrainCover.setImageResource(R.drawable.pic_grain_card_404);
        }
        viewGrainSwipe.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                mSwipeItems.remove(0);
                mSwipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                httpReadGrain(((SwipeGrain) o).grainId);
            }

            @Override
            public void onRightCardExit(Object o) {
                SwipeGrain sg = (SwipeGrain) o;
                httpReadGrain(sg.grainId);
                if (!MainActivity.isVisitor) {
                    httpFavorGrain(sg.grainId);
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                if (i == 2) {
                    httpLoadData();
                }
                switch (i) {
                    case 0:
                        if (!AppUtils.isNetworkConnected(getActivity())) {
                            ivGrainCover.setVisibility(View.VISIBLE);
                            ivGrainCover.setImageResource(R.drawable.pic_grain_card_404);
                        } else if (stateNomore) {
                            ivGrainCover.setVisibility(View.VISIBLE);
                            ivGrainCover.setImageResource(R.drawable.pic_grain_card_nomore);
                        }
                    case 2:
                        httpLoadData();
                        break;
                }
                Log.d("GrainFragment", "i:" + i);
            }

            @Override
            public void onScroll(float v) {

            }
        });
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_grain_ignore,
            R.id.btn_grain_favourite})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                //TODO 我的麦田
                break;
            case R.id.btn_grain_ignore:
                if (ivGrainCover.getVisibility() != View.VISIBLE) {
                    httpReadGrain(mSwipeItems.get(0).grainId);
                    viewGrainSwipe.getTopCardListener().selectLeft();
                }
                break;
            case R.id.btn_grain_favourite:
                if (ivGrainCover.getVisibility() != View.VISIBLE) {
                    if (!MainActivity.isVisitor) {
                        httpFavorGrain(mSwipeItems.get(0).grainId);
                    }
                    httpReadGrain(mSwipeItems.get(0).grainId);
                    viewGrainSwipe.getTopCardListener().selectRight();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void httpLoadData() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            if (MyApplication.signInStatus.equals("11")) {//11
                json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
                json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            } else {//10
                json.put("vid", StringUtils.toLong(AppConfig.getAppConfig().get(AppConfig.CONFIG_APP, "vid")));
            }
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                MyApplication.signInStatus.equals("11") ?
                        ApiUtils.URL_ROOT + ApiUtils.URL_GRAIN_RECOMMAND :
                        ApiUtils.URL_ROOT + ApiUtils.URL_VISITOR_RECOMMAND,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result)) {
                            return;
                        }
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                JSONArray array = data.getJSONArray("grain");
                                Gson gson = new Gson();
                                List<SwipeGrain> temp = gson.fromJson(array.toString(), new TypeToken<List<SwipeGrain>>() {
                                }.getType());
                                if (temp == null || temp.size() < 1) {
                                    stateNomore = true;
                                    if (mSwipeItems.size() < 1) {
                                        ivGrainCover.setVisibility(View.VISIBLE);
                                        viewGrainSwipe.setEnabled(false);
                                        ivGrainCover.setImageResource(R.drawable.pic_grain_card_nomore);
                                    }
                                } else {
                                    stateNomore = false;
                                    ivGrainCover.setVisibility(View.INVISIBLE);

                                    mSwipeItems.addAll(temp);
//                                downloadAndRedirect();
                                    mSwipeAdapter.notifyDataSetChanged();
                                }
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
                        ivGrainCover.setVisibility(View.VISIBLE);
                        ivGrainCover.setImageResource(R.drawable.pic_grain_card_404);
                    }
                });
    }

    private void httpFavorGrain(long grainId) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", grainId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiUtils.URL_ROOT + ApiUtils.URL_FAVOR_GRAIN, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                    // 收藏成功
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                // 收藏失败
            }
        });
    }

    private void httpReadGrain(long grainId) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            if (MyApplication.signInStatus.equals("11")) {
                json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
                json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            } else {
                json.put("vid", StringUtils.toLong(AppConfig.getAppConfig().get(AppConfig.CONFIG_APP, "vid")));
            }
            json.put("gid", grainId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                MyApplication.signInStatus.equals("11") ?
                        ApiUtils.URL_ROOT + ApiUtils.URL_READ_GRAIN :
                        ApiUtils.URL_ROOT + ApiUtils.URL_VISITOR_READ_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            // 收藏成功
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        // 收藏失败
                    }
                });
    }

    /**
     * ******************* Life Cycle *********************
     */

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void downloadAndRedirect() {
        for (final SwipeGrain sg : mSwipeItems) {
            try {
                final String to = FileUtils.getAppCache(getActivity(), "photo")
                        + FileUtils.getFileName(sg.image);
                final String key = OssManager.getFileKeyByRemoteUrl(sg.image);
                File file = new File(to);
                if (!file.exists()) {
                    OSSFile ossFile = new OSSFile(OssManager.getOssManager().imgChannel, key + OssManager.STYLE_SWIPE);
                    ossFile.downloadToInBackground(to, new GetFileCallback() {
                        @Override
                        public void onSuccess(String s, String s1) {
                            sg.image = to;
                        }

                        @Override
                        public void onProgress(String s, int i, int i1) {

                        }

                        @Override
                        public void onFailure(String s, OSSException e) {

                        }
                    });
                } else {
                    sg.image = to;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ******************* Adapter *******************
     */

    private class SwipeViewAdapter extends CommonAdapter<SwipeGrain> {
        SwipeViewAdapter(Context context, List<SwipeGrain> list, int viewId) {
            super(context, list, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, SwipeGrain swipeGrainItem) {
            holder.setText(R.id.tv_item_grain_card_comment, swipeGrainItem.text)
                    .setSwipeImage(R.id.iv_item_grain_card_image, swipeGrainItem.image)
                    .setPortraitImage(R.id.civ_item_grain_card_portrait, swipeGrainItem.portraitSmall); //用户头像
        }
    }
}