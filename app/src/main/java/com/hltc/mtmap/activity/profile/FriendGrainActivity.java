package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.MTMyGrain;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.GrainAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DialogManager;
import com.hltc.mtmap.gmodel.FriendProfile;
import com.hltc.mtmap.helper.ApiHelper;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.ToastUtils;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FriendGrainActivity extends Activity {
    private static final String TAG ="FriendGrainActivity" ;
    @InjectView(R.id.btn_bar_right)
    Button btnRight;
    @InjectView(R.id.tv_bar_title)
    TextView tvTitle;
    @InjectView(R.id.lv_friend_grain)
    ListView lvFriendGrain;
    private FriendProfile mFriendProfile;

    private List<MTMyGrain> friendGrainList = new ArrayList<>();

    private GrainAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_grain);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        mFriendProfile = getIntent().getParcelableExtra("profile");
        if(mFriendProfile==null)
            AppManager.getAppManager().finishActivity(this);

        initView();
        initData();


    }
    private void initView() {
        btnRight.setVisibility(View.GONE);

        tvTitle.setText((mFriendProfile.user.remark==null?mFriendProfile.user.nickName:mFriendProfile.user.remark)+"的麦田");
        mAdapter = new GrainAdapter(this,friendGrainList,R.layout.item_my_maitian);
        lvFriendGrain.setAdapter(mAdapter);
        lvFriendGrain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApiHelper.httpGetGrainDetail(FriendGrainActivity.this, friendGrainList.get(position).getGrainId());
            }
        });
    }
    private void initData() {
        httpFriendGrain();
    }
    @OnClick({R.id.btn_bar_left})
    public void onClick(View view){
        AppManager.getAppManager().finishActivity(this);
    }
    private void httpFriendGrain() {
        final ProgressDialog dialog = DialogManager.buildProgressDialog(this, ApiUtils.TIP_LOAD_DATA);
        dialog.show();
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try{
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("fuserId",mFriendProfile.user.userId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                ApiUtils.API_OBTAIN_FRIEND_GRAIN, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        dialog.dismiss();
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {
                            try {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                friendGrainList = gson.fromJson(data.toString(), new TypeToken<List<MTMyGrain>>() {
                                }.getType());
                                mAdapter.update(friendGrainList);
                            } catch (JSONException e) {
                                ToastUtils.showShort(FriendGrainActivity.this,ApiUtils.TIP_LOAD_DATA_FAIL);
                                e.printStackTrace();
                                Log.e(TAG,e.getMessage());
                            }
                        }else{
                            ToastUtils.showShort(FriendGrainActivity.this,ApiUtils.TIP_LOAD_DATA_FAIL);
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        ToastUtils.showShort(FriendGrainActivity.this,ApiUtils.TIP_LOAD_DATA_FAIL);
                    }
                });
    }
    public static void start(Activity activity,FriendProfile mProfile){
        Intent intent = new Intent(activity,FriendGrainActivity.class);
        intent.putExtra("profile",mProfile);
        activity.startActivity(intent);
    }

}
