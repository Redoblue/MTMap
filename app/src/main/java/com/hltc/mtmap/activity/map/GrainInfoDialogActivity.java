package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.tencent.mm.sdk.openapi.IWXAPI;

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
public class GrainInfoDialogActivity extends Activity {

    private static final String TAG = "GrainInfoDialogActivity";
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
            R.id.civ_grain_info_portrait,
            R.id.tv_grain_info_detail
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_grain_info_exit:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.iv_grain_info_ignore:
                ignoreSelectDilogShow();
                break;
            case R.id.tv_grain_info_detail:
                ApiHelper.httpGetGrainDetail(GrainInfoDialogActivity.this, mGrainItem.grainId);
                break;
            case R.id.civ_grain_info_portrait:
                ApiHelper.httpGetFriendProfile(this, mGrainItem.userId);
                break;
            default:
                break;
        }
    }

    private void ignoreSelectDilogShow(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ignore_grain,null);
        final AlertDialog ignoreDilog=  builder.setView(view).show();
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ignoreDilog.dismiss();
                ignoreTheGrain();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreDilog.dismiss();
            }
        });

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
                            Handler mHandler = ((MyApplication) getApplication()).getShareHandler();
                            if (mHandler == null) {
                                AppManager.getAppManager().finishActivity(GrainInfoDialogActivity.this);
                            } else {
                                Message msg = mHandler.obtainMessage();
                                msg.what = MapFragment.MSG_IGNORE_GRAIN;
                                msg.obj = mGrainItem;
                                mHandler.sendMessage(msg);
                                AppManager.getAppManager().finishActivity(GrainInfoDialogActivity.this);
                            }
                        } else {
                            ToastUtils.showShort(GrainInfoDialogActivity.this, ApiUtils.TIP_NET_EXCEPTION);
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.showShort(GrainInfoDialogActivity.this, ApiUtils.TIP_NET_EXCEPTION);
                    }
                });
    }

}
