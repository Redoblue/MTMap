package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.fragment.MapFragment;
import com.hltc.mtmap.gmodel.ClusterGrain;
import com.hltc.mtmap.helper.ApiHelper;
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
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-7-3.
 */
public class GrainInfoDialog extends Activity {

    private static final String TAG = "GrainInfoDialog";
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
        AppManager.getAppManager().addActivity(this);
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
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.iv_grain_info_ignore:
                ignoreTheGrain();
                break;
            case R.id.tv_grain_info_detail:
                ApiHelper.httpGetGrainDetail(GrainInfoDialog.this, mGrainItem.grainId);
                break;
        }
    }

    private void ignoreTheGrain() {

        final RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put(ApiUtils.KEY_GRAIN_ID, mGrainItem.grainId);
            requestParams.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getIgnoreGrainURL(),
                requestParams,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.i(TAG, result);

                        if (result.contains(ApiUtils.KEY_SUCCESS)) {
                            //TODO to deal the #success# case
                            Handler mHandler = ((MyApplication) getApplication()).getShareHandler();
                            if (mHandler == null) {
                                AppManager.getAppManager().finishActivity(GrainInfoDialog.this);
                            } else {
                                Message msg = mHandler.obtainMessage();
                                msg.what = MapFragment.MSG_IGNORE_GRAIN;
                                msg.obj = mGrainItem;
                                mHandler.sendMessage(msg);
                                AppManager.getAppManager().finishActivity(GrainInfoDialog.this);
                            }
                        } else {
                            ToastUtils.showShort(GrainInfoDialog.this, "网络出现问题，请稍后再试");
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.showShort(GrainInfoDialog.this, "网络出现问题，请稍后再试");
                    }
                });
    }

}
