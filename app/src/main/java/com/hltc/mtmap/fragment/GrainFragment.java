package com.hltc.mtmap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.bean.GrainItem;
import com.hltc.mtmap.bean.SiteItem;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
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
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;

    private List<GrainItem> mSwipeItems;
    private List<GrainItem> tempItems;//未获取图片的临时对象
    private SwipeViewAdapter mSwipeAdapter;
    private List<HashMap<Long, Integer>> states = new ArrayList<>();

    private OSSService ossService;
    private OSSBucket ossBucket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grain, container, false);
        ButterKnife.inject(this, view);

        initView();
        ossService = OssManager.getOssManager().ossService;
        ossBucket = OssManager.getOssManager().ossBucket;
        return view;
    }

    private void initView() {
        tvBarTitle.setText("麦圈");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_yinyang);
        btnBarLeft.setWidth(AMapUtils.dp2px(getActivity(), 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(getActivity(), 25));

        mSwipeItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GrainItem item = new GrainItem();
            item.setImage("http://maitianditu.img-cn-hangzhou.aliyuncs.com/27TeEBIp9378uH7KI3rALu.jpg@100x100-5rc.jpg");
            item.setPortraitSmall("http://maitianditu.oss-cn-hangzhou.aliyuncs.com/27TeEBIp9378uH7KI3rALu.jpg");
            item.setText("我只是一个card而已 " + i);
            OssManager.getOssManager().downloadImage(
                    getActivity().getCacheDir().getAbsolutePath() + "/swipe" + "/",
                    "1GJs1_k-Z9wpPOQIlFCwtF.jpg@1e_200w_200h_1c_0i_1o_90q_1x.jpg");
            mSwipeItems.add(item);
        }

        mSwipeAdapter = new SwipeViewAdapter(getActivity(), mSwipeItems, R.layout.item_grain_card);
        viewGrainSwipe.setAdapter(mSwipeAdapter);
        httpLoadData(); //首次加载数据
        viewGrainSwipe.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                mSwipeItems.remove(0);
                mSwipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                GrainItem grainItem = (GrainItem) o;
                HashMap<Long, Integer> map = new HashMap<>();
                map.put(grainItem.getGrainId(), 0);
                states.add(map);
            }

            @Override
            public void onRightCardExit(Object o) {
                Log.d("GrainFragment", "right");
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                if (i == 2) {
                    httpLoadData();
                    for (GrainItem item : tempItems) {
                        resumableDownload(StringUtils.getFileNameFromPath(item.getImage()));
                        item.setImage(FileUtils.getAppCache(getActivity(), "swipe") + StringUtils.getFileNameFromPath(item.getImage()));
                        mSwipeItems.add(item);
                        mSwipeAdapter.notifyDataSetChanged();
                    }
                    //TODO
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });
    }

    @OnClick({R.id.btn_grain_ignore,
            R.id.btn_grain_favourite})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_grain_ignore:
                viewGrainSwipe.getTopCardListener().selectLeft();
                break;
            case R.id.btn_grain_favourite:
                viewGrainSwipe.getTopCardListener().selectRight();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    // 断点下载
    public void resumableDownload(String file) {
        OSSFile bigFile = ossService.getOssFile(ossBucket, file);
        bigFile.ResumableDownloadToInBackground(FileUtils.getAppCache(getActivity(), "swipe") + file, new GetFileCallback() {

            @Override
            public void onSuccess(String objectKey, String filePath) {
                Log.d("GrainFragment", "[onSuccess] - " + objectKey + " storage path: " + filePath);
            }

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.d("GrainFragment", "[onProgress] - current download: " + objectKey + " bytes:" + byteCount + " in total:" + totalSize);
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e("GrainFragment", "[onFailure] - download " + objectKey + " failed!\n" + ossException.toString());
                ossException.printStackTrace();
            }
        });
    }

    private void httpLoadData() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            if (false) {
                //TODO
            } else {
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
                ApiUtils.getCreateAccountUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject son = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                JSONArray array = son.getJSONArray("grain");
                                for (int i = 0; i < array.length(); i++) {
                                    GrainItem grainItem = new GrainItem();
                                    JSONObject grain = array.getJSONObject(i);
                                    grainItem.setGrainId(grain.getLong("grainId"));
                                    grainItem.setText(grain.getString("text"));
                                    grainItem.setImage(grain.getString("image"));
                                    grainItem.setUserId(grain.getLong("userId"));
                                    grainItem.setPortraitSmall(grain.getString("portraitSmall"));

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

                                    grainItem.setSite(siteItem);
                                    tempItems.add(grainItem);
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
                        GrainItem item = new GrainItem();
                        item.setCover(R.drawable.grain_404);
                        mSwipeItems.add(item);
                        mSwipeAdapter.notifyDataSetChanged();
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

    /**
     * ******************* Adapter *******************
     */

    private class SwipeViewAdapter extends CommonAdapter<GrainItem> {
        SwipeViewAdapter(Context context, List<GrainItem> list, int viewId) {
            super(context, list, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, GrainItem grainItem) {
            holder.setImage(R.id.iv_swipe_pic, grainItem.getImage())
                    .setCircleImage(R.id.civ_swipe_avatar, grainItem.getPortraitSmall()) //用户头像
                    .setText(R.id.tv_swipe_comment, grainItem.getText())
                    .setImage(R.id.iv_swipe_cover, grainItem.getCover()
                    );
        }
    }
}
