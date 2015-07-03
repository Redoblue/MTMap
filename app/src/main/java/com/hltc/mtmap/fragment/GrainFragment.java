package com.hltc.mtmap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.GrainSwipeViewAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.SiteItem;
import com.hltc.mtmap.bean.SwipeGrainItem;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GrainFragment extends Fragment {

    public static final int SWIPE_GRAIN = 0;
    public static final int SWIPE_OFFLINE = 1;
    public static final int SWIPE_NOMORE = 2;

    @InjectView(R.id.btn_grain_ignore)
    Button btnGrainIgnore;
    @InjectView(R.id.btn_grain_favourite)
    Button btnGrainFavourite;
    @InjectView(R.id.view_grain_swipe)
    SwipeFlingAdapterView viewGrainSwipe;
    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;

    private List<SwipeGrainItem> list = new ArrayList<>();
    private GrainSwipeViewAdapter adapter;
    private List<HashMap<Long, Integer>> states = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grain, container, false);
        ButterKnife.inject(this, view);

        initView();
        return view;
    }

    private void initView() {
        tvBarTitle.setText("麦圈");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_yinyang);
        btnBarLeft.setWidth(AMapUtils.dp2px(getActivity(), 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(getActivity(), 25));

        list = new ArrayList<>();
        if (!AppUtils.isNetworkConnected(getActivity())) {
            Log.d("MT", "no network");
            setSwipeBack(SWIPE_OFFLINE);
//            viewGrainSwipe.setEnabled(false);
        } else {
            httpLoadData(); //首次加载数据
        }

        adapter = new GrainSwipeViewAdapter(getActivity(), R.layout.item_grain_card, list);
        viewGrainSwipe.setAdapter(adapter);
        viewGrainSwipe.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                list.remove(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                SwipeGrainItem swipeGrainItem = (SwipeGrainItem) o;
                HashMap<Long, Integer> map = new HashMap<>();
                map.put(swipeGrainItem.getGrainId(), 0);
                states.add(map);
            }

            @Override
            public void onRightCardExit(Object o) {
                Log.d("GrainFragment", "right");
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                if (i == 2) {
//                    Log.d("GrainFragment", "2 left");
//                    httpLoadData();
//
//                    OssManager.getOssManager().downloadImage(
//                            FileUtils.getAppCache(getActivity(), "swipe") + "180176e4-5103-4ec1-8c1e-48b481788136.jpg",
//                            "users/300204/180176e4-5103-4ec1-8c1e-48b481788136.jpg");
//                    SwipeGrainItem item = new SwipeGrainItem();
//                    item.setImage(FileUtils.getAppCache(getActivity(), "swipe")
//                            + "180176e4-5103-4ec1-8c1e-48b481788136.jpg");
//                    httpLoadData();
                    //TODO
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });

//        FileUtils.getAppCache(getActivity(), "swipe");
//        OssManager.getOssManager().downloadImage(
//                FileUtils.getAppCache(getActivity(), "swipe") + "180176e4-5103-4ec1-8c1e-48b481788136.jpg",
//                "users/300204/180176e4-5103-4ec1-8c1e-48b481788136.jpg");
//        for (int i = 0; i < 5; i++) {
//            SwipeGrainItem item = new SwipeGrainItem();
//            item.setImage(FileUtils.getAppCache(getActivity(), "swipe") + "180176e4-5103-4ec1-8c1e-48b481788136.jpg");
//            item.setPortrait(FileUtils.getAppCache(getActivity(), "swipe") + "180176e4-5103-4ec1-8c1e-48b481788136.jpg");
//            item.setText("我只是一个card而已 " + i);
//            list.add(item);
//        }

    }

    private void setSwipeBack(int status) {
        SwipeGrainItem item = new SwipeGrainItem();
        item.setImage("");
        item.setPortrait("");
        item.setText("");
        item.setStatus(status);
        list.add(item);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_grain_ignore,
            R.id.btn_grain_favourite})
    public void onClick(View v) {
        Log.d("MT", "list.size():" + list.size());
        switch (v.getId()) {
            case R.id.btn_grain_ignore:
                if (!list.isEmpty())
                    viewGrainSwipe.getTopCardListener().selectLeft();
                break;
            case R.id.btn_grain_favourite:
                if (!list.isEmpty())
                    viewGrainSwipe.getTopCardListener().selectRight();
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
                json.put("vid", "Android");
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
                        ApiUtils.getGrainRecommandUrl() :
                        ApiUtils.getVisitorRecommandUrl(),
                params,
                new RequestCallBack<String>() {
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
                                Log.d("MT", array.toString());
                                if (array.length() > 0) {
                                    if (list.size() > 0 && list.get(list.size() - 1).getStatus() != SWIPE_GRAIN)
                                        list.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        SwipeGrainItem swipeGrainItem = new SwipeGrainItem();
                                        JSONObject grain = array.getJSONObject(i);
                                        swipeGrainItem.setGrainId(grain.getLong("grainId"));
                                        swipeGrainItem.setText(grain.getString("text"));
                                        swipeGrainItem.setImage(grain.getString("image"));
                                        swipeGrainItem.setUserId(grain.getLong("userId"));
                                        swipeGrainItem.setPortrait(grain.getString("portraitSmall"));
                                        swipeGrainItem.setStatus(GrainFragment.SWIPE_GRAIN);

                                        SiteItem siteItem = new SiteItem();
                                        JSONObject site = grain.getJSONObject("site");
                                        siteItem.setSiteId(site.getString("siteId"));
                                        siteItem.setLon(site.getDouble("lon"));
                                        siteItem.setLat(site.getDouble("lat"));
                                        siteItem.setName(site.getString("name"));
                                        siteItem.setAddress(site.getString("address"));
                                        siteItem.setPhone(site.getString("phone"));
                                        siteItem.setMtype(site.getString("mtype"));
                                        siteItem.setGtype(site.getString("gtype"));

                                        swipeGrainItem.setSite(siteItem);
                                        list.add(swipeGrainItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    setSwipeBack(SWIPE_NOMORE);
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
                        SwipeGrainItem item = new SwipeGrainItem();
                        item.setStatus(SWIPE_OFFLINE);
                        list.clear();
                        list.add(item);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void httpSubmitGrainOperation(long grainId, int state) {
        // 返回忽略或收藏信息
    }

    /**
     * ******************* Life Cycle *********************
     */

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

}
