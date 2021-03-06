package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.MTMyGrain;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.adapter.GrainAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.DialogManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.helper.ApiHelper;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-7-9.
 */
public class MyGrainActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.listView)
    SwipeMenuListView listView;
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            deleteItem.setWidth(AMapUtils.dp2px(MyGrainActivity.this, 100));
            deleteItem.setIcon(R.drawable.ic_action_delete);
            menu.addMenuItem(deleteItem);
        }
    };
    @InjectView(R.id.tv_hint)
    TextView tvHint;
    private List<MTMyGrain> mList=new ArrayList<>();
    private GrainAdapter mAdapter;

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
        tvBarTitle.setText("我的麦田");
        btnBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(MyGrainActivity.this);
            }
        });

        //如果没有数据或从创建麦粒而来则重新加载
        if(AppUtils.isNetworkConnected(this)){
            httpSyncGrainData(this, "同步数据中...");
        }
        else{
            loadLocalData();
        }
      /*  if (DaoManager.getManager().daoSession.getMTMyGrainDao().count() < 1) {

        } else {
            loadLocalData();
        }*/
        //refreshHint();
        mAdapter = new GrainAdapter(this, mList, R.layout.item_my_maitian);
        listView.setAdapter(mAdapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                MTMyGrain mg = mList.get(position);
                switch (index) {
                    case 0:
                        httpDeleteGrain(mg);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApiHelper.httpGetGrainDetail(MyGrainActivity.this, mList.get(position).getGrainId());
            }
        });
    }

    private void loadLocalData() {
        mList = DaoManager.getManager().getAllMyGrains();
    }

    private void refreshHint() {
        if (mList == null || mList.size() < 1) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("还没有发布过麦粒哦,快去试试吧!");
        } else {
            tvHint.setVisibility(View.INVISIBLE);
        }
    }

    private void httpDeleteGrain(final MTMyGrain mg) {
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
        http.send(HttpRequest.HttpMethod.POST, ApiUtils.URL_ROOT + ApiUtils.URL_DELETE_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            if (mList.remove(mg))
                                mAdapter.update(mList);
                            refreshHint();
                            DaoManager.getManager().daoSession.getMTMyGrainDao().deleteByKey(mg.getGrainId());
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(MyGrainActivity.this,getString(R.string.tip_delete_fail),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void httpSyncGrainData(final Context context, String msg) {
        final ProgressDialog dialog = DialogManager.buildProgressDialog(context, msg);
        dialog.show();

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http1 = new HttpUtils();
        http1.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_MY_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<MTMyGrain> mgs = gson.fromJson(data.toString(), new TypeToken<List<MTMyGrain>>() {
                                }.getType());

                                //保存到数据库
                                if (mgs != null) {
                                    dialog.dismiss();
                                    try {
                                        DaoManager.getManager().daoSession.getMTMyGrainDao().deleteAll();
                                        for (MTMyGrain f : mgs) {
                                            DaoManager.getManager().daoSession.getMTMyGrainDao().insertOrReplace(f);
                                        }
                                        loadLocalData();
                                        mAdapter.update(mList);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    dialog.dismiss();
                                    Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        Toast.makeText(context, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
