package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.gmodel.ClusterGrain;
import com.hltc.mtmap.gmodel.GrainDetail;
import com.hltc.mtmap.util.ApiUtils;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-7-3.
 */
public class GrainInfoDialog extends Activity {

    @InjectView(R.id.iv_grain_info_blur)
    ImageView ivGrainInfoBlur;
    @InjectView(R.id.civ_grain_info_portrait)
    CircleImageView civGrainInfoPortrait;
    @InjectView(R.id.tv_grain_info_nickname)
    TextView tvGrainInfoNickname;
    @InjectView(R.id.iv_grain_info_exit)
    ImageView ivGrainInfoExit;
    @InjectView(R.id.iv_grain_info_ignore)
    ImageView ivGrainInfoIgnore;
    @InjectView(R.id.tv_grain_info_address)
    TextView tvGrainInfoAddress;
    @InjectView(R.id.tv_grain_info_text)
    TextView tvGrainInfoText;
    @InjectView(R.id.tv_grain_info_detail)
    TextView tvGrainInfoDetail;

    private ClusterGrain mGrainItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_grain_info);
        ButterKnife.inject(this);

        initData();
        initView();
    }

    private void initData() {
        mGrainItem = getIntent().getParcelableExtra("grain");
    }

    private void initView() {
        civGrainInfoPortrait.setImageDrawable(Drawable.createFromPath(mGrainItem.userPortrait));
        tvGrainInfoNickname.setText(StringUtils.isEmpty(
                mGrainItem.remark) ? mGrainItem.nickName : mGrainItem.remark);
        tvGrainInfoAddress.setText(mGrainItem.site.name);
        tvGrainInfoText.setText(mGrainItem.text);
    }

    @OnClick({
            R.id.iv_grain_info_exit,
            R.id.iv_grain_info_ignore,
            R.id.tv_grain_info_detail
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_grain_info_exit:
                finish();
                break;
            case R.id.iv_grain_info_ignore:
                //TODO ignore this grain
                break;
            case R.id.tv_grain_info_detail:
//                new OpenGrainDetailTask().execute(mGrainItem.grainId);
                httpGetGrainDetail(mGrainItem.grainId);
                finish();
                break;
        }
    }

    private void httpGetGrainDetail(long grainId) {
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
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_GRAIN_DETAIL,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("MapFragment", result);
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject json = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                Gson gson = new Gson();
                                GrainDetail grainDetail = gson.fromJson(json.toString(), new TypeToken<GrainDetail>() {
                                }.getType());

                                if (grainDetail != null) {
                                    //TODO 进入详情界面
                                    Intent intent = new Intent(GrainInfoDialog.this, GrainDetailActivity.class);
                                    intent.setExtrasClassLoader(GrainDetail.Praise.class.getClassLoader());
                                    intent.putExtra("grain", grainDetail);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MyApplication.getContext(), "检索详情失败", Toast.LENGTH_SHORT).show();
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

}
