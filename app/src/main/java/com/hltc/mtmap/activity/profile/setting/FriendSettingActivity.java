package com.hltc.mtmap.activity.profile.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.FriendProfileActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DialogManager;
import com.hltc.mtmap.event.BaseMessageEvent;
import com.hltc.mtmap.gmodel.FriendProfile;
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
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by X-MH on 2015/8/30.
 */
public class FriendSettingActivity extends Activity {
    public static final int RESULT_CODE_FROM_FRIENDSETTING = 1;
    private static final String TAG = "FriendSettingActivity";
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTile;
    @InjectView(R.id.et_edit)
    EditText evRemark;

    private FriendProfile mFriendProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_setting);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);
        initData();
        initView();
    }

    private void initData() {
        mFriendProfile = getIntent().getParcelableExtra("friendprofile");
        if (mFriendProfile == null)
            AppManager.getAppManager().finishActivity(this);
    }

    private void initView() {
        tvBarTile.setText("设置");
        boolean showRemark = mFriendProfile.user.remark != null && mFriendProfile.user.remark.length() > 0;
        evRemark.setText(showRemark ? mFriendProfile.user.remark : mFriendProfile.user.nickName);
    }

    @OnClick({R.id.btn_bar_right,
            R.id.btn_bar_left,
            R.id.btn_delete_friend})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                modifyAndBack();
                break;
            case R.id.btn_delete_friend:
                deleteUserById();
                break;
            default:
                break;
        }
    }

    private void deleteUserById() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();

        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("fuserId", mFriendProfile.user.userId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                ApiUtils.API_FRIEND_DELETE, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {

                            BaseMessageEvent event = new BaseMessageEvent();
                            event.userId = mFriendProfile.user.userId;
                            event.action = BaseMessageEvent.EVENT_DELETE_USER;
                            EventBus.getDefault().post(event);
                            AppManager.getAppManager().finishActivity(FriendSettingActivity.this);
                        } else {
                            ToastUtils.showShort(FriendSettingActivity.this, ApiUtils.TIP_NET_EXCEPTION);
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.showShort(FriendSettingActivity.this, ApiUtils.TIP_NET_EXCEPTION);
                    }
                });
    }

    private void modifyAndBack() {

        final String remark = evRemark.getText().toString().trim();
        if (remark.length() == 0 || remark.equals(mFriendProfile.user.remark)) {
            ToastUtils.showShort(this, "两次备注一样");
            return;
        }
        final ProgressDialog dialog = DialogManager.buildProgressDialog(this, ApiUtils.TIP_LOAD_DATA);
        dialog.show();
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("fuserId", mFriendProfile.user.userId);
            json.put("remark", evRemark.getText());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        new HttpUtils().send(HttpRequest.HttpMethod.POST,
                ApiUtils.API_FRIEND_REMARK, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        dialog.dismiss();
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {
                            Intent intent = new Intent();
                            intent.putExtra(FriendProfileActivity.TAG, remark);
                            setResult(RESULT_CODE_FROM_FRIENDSETTING, intent);

                            BaseMessageEvent modifyEvent = new BaseMessageEvent();
                            modifyEvent.userId = mFriendProfile.user.userId;
                            modifyEvent.action = BaseMessageEvent.EVENT_MODIFY_USER_NAME;
                            modifyEvent.tag = remark;
                            EventBus.getDefault().post(modifyEvent);
                            finish();
                        } else {
                            ToastUtils.showShort(FriendSettingActivity.this, ApiUtils.TIP_NET_EXCEPTION);
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        ToastUtils.showShort(FriendSettingActivity.this, ApiUtils.TIP_NET_EXCEPTION);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();

    }

    public static void startForResult(Activity activity, FriendProfile friendProfile) {
        Intent intent = new Intent(activity, FriendSettingActivity.class);
        intent.putExtra("friendprofile", friendProfile);
        activity.startActivityForResult(intent, RESULT_CODE_FROM_FRIENDSETTING);
    }

}
