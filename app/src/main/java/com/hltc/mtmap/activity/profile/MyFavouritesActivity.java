package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hltc.mtmap.MTMyFavourite;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.publish.CreateGrainActivity;
import com.hltc.mtmap.activity.publish.CreateGrainActivity2;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.DateUtils;
import com.hltc.mtmap.util.StringUtils;
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
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-7-9.
 */
public class MyFavouritesActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.listView)
    SwipeMenuListView listView;
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
            openItem.setBackground(R.color.green_favor_bg);
            openItem.setWidth(AMapUtils.dp2px(MyFavouritesActivity.this, 100));
            openItem.setTitle("添加到\n我的麦田");
            openItem.setTitleSize(18);
            openItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(openItem);

            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            deleteItem.setWidth(AMapUtils.dp2px(MyFavouritesActivity.this, 100));
            deleteItem.setIcon(R.drawable.ic_action_delete);
            menu.addMenuItem(deleteItem);
        }
    };
    private List<MTMyFavourite> mList;
    private MyFavouriteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_maitian);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        btnBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(MyFavouritesActivity.this);
            }
        });

        mList = DaoManager.getManager().getAllMyFavourites();
        mAdapter = new MyFavouriteAdapter(this, mList, R.layout.item_my_maitian);
        listView.setAdapter(mAdapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                MTMyFavourite mg = mList.get(position);
                switch (index) {
                    case 0:
                        Intent intent = new Intent(MyFavouritesActivity.this, CreateGrainActivity.class);
                        intent.putExtra("type", getCreateType(mg));
                        intent.putExtra("address", mg.getAddress());
                        LatLng latLng = new LatLng(mg.getLat(), mg.getLon());
                        intent.putExtra("location", latLng);
                        startActivity(intent);
                        break;
                    case 1:
                        httpDeleteFavourite(mg);
                        break;
                }
            }
        });
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
    }

    private int getCreateType(MTMyFavourite mg) {
        for (int i = 0; i < CreateGrainActivity2.mCateId.length; i++) {
            if (mg.getCateId().equals(CreateGrainActivity2.mCateId[i]))
                return i;
        }
        return -1;
    }

    private void httpDeleteFavourite(final MTMyFavourite mg) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", mg.getGrainId());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiUtils.URL_ROOT + ApiUtils.URL_FAVOR_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            if (mList.remove(mg))
                                mAdapter.notifyDataSetChanged();
                            DaoManager.getManager().daoSession.getMTMyFavouriteDao().deleteByKey(mg.getGrainId());
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        // 收藏失败
                    }
                });
    }

    class MyFavouriteAdapter extends CommonAdapter<MTMyFavourite> {

        public MyFavouriteAdapter(Context context, List<MTMyFavourite> data, int viewId) {
            super(context, data, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, MTMyFavourite mtMyGrain) {
            holder.setText(R.id.tv_item_my_maitian_comment, mtMyGrain.getText())
                    .setText(R.id.tv_item_my_maitian_address, mtMyGrain.getAddress());
            String image = mtMyGrain.getImage();
            if (image == null || StringUtils.isEmpty(image)) {
                holder.getView(R.id.iv_item_my_maitian_image).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.iv_item_my_maitian_image).setVisibility(View.VISIBLE);
                holder.setGrainThumbnail(R.id.iv_item_my_maitian_image, mtMyGrain.getImage());
            }

            TextView time = holder.getView(R.id.tv_item_my_maitian_time);
            String date = DateUtils.getFriendlyTime(mtMyGrain.getCreateTime());
            time.setText(date);
            time.setTextSize(AMapUtils.dp2px(MyApplication.getContext(), 30 / date.length()));
        }
    }
}
