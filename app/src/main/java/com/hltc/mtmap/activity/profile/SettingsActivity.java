package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.SingleEditActivity;
import com.hltc.mtmap.activity.profile.setting.AboutActivity;
import com.hltc.mtmap.activity.profile.setting.FeedbackActivity;
import com.hltc.mtmap.activity.start.SignUpActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DialogManager;
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

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-5-30.
 */
public class SettingsActivity extends Activity {

    public static final int UPDATE_NICKNAME_REQUEST_CODE = 0;

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_settings_set_nickname)
    Button btnSettingsSetNickname;
    @InjectView(R.id.btn_settings_change_passwd)
    Button btnSettingsChangePasswd;
    @InjectView(R.id.btn_settings_feedback)
    Button btnSettingsFeedback;
    @InjectView(R.id.btn_settings_check_update)
    Button btnSettingsCheckUpdate;
    @InjectView(R.id.btn_settings_five_star)
    Button btnSettingsFiveStar;
    @InjectView(R.id.btn_settings_recommend)
    Button btnSettingsRecommend;
    @InjectView(R.id.btn_settings_about)
    Button btnSettingsAbout;
    @InjectView(R.id.btn_settings_logout)
    Button btnSettingsLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_settings_set_nickname,
            R.id.btn_settings_change_passwd,
            R.id.btn_settings_feedback,
            R.id.btn_settings_check_update,
            R.id.btn_settings_five_star,
            R.id.btn_settings_recommend,
            R.id.btn_settings_about,
            R.id.btn_settings_logout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_settings_set_nickname:
                SingleEditActivity.startFromSettingActivity(this);
                break;
            case R.id.btn_settings_change_passwd:
                Intent i2 = new Intent(this, SignUpActivity.class);
                i2.putExtra("source", 1);
                startActivity(i2);
                break;
            case R.id.btn_settings_feedback:
                Intent i3 = new Intent(this, FeedbackActivity.class);
                startActivity(i3);
                break;
            case R.id.btn_settings_check_update:
                httpCheckUpdate();
                break;
            case R.id.btn_settings_five_star:
                gotoMarket();
                break;
            case R.id.btn_settings_recommend:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/*");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "大家快来用麦田地图吧！");
                startActivity(sendIntent);
                break;
            case R.id.btn_settings_about:
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_settings_logout:
                AppUtils.logout();
                clearData();
//                Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, StartActivity.class);
                startActivity(intent2);
                AppManager.getAppManager().finishActivity(this);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                break;
        }
    }

    private void clearData() {
        //TODO 清除一些数据
        try {
            AppConfig.getAppConfig().clear(AppConfig.CONFIG_USER);
            FileUtils.clearCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_NICKNAME_REQUEST_CODE:
                    String newString = data.getStringExtra("new");
                    httpUpdateNickname(newString);
            }
        }
    }

    private void gotoMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("检测到新版本")
                .setMessage("是否下载更新?")
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoMarket();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void httpUpdateNickname(final String nickname) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put(ApiUtils.KEY_USR_NICKNAME, nickname);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getUpdateNicknameUrl(), params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                AppConfig.getAppConfig().setConfUsrNickName(nickname);
                                Toast.makeText(SettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SettingsActivity.this, errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SettingsActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void httpCheckUpdate() {
        final ProgressDialog dialog = DialogManager.buildProgressDialog(this, "检测更新中...");
        dialog.show();
        RequestParams params = new RequestParams();
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                ApiUtils.URL_ROOT + "my/settings/latest_version.json",
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;

                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                dialog.dismiss();

                                JSONObject json = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                int versionCode = json.getInt("versionCode");

                                PackageManager manager = getPackageManager();
                                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                                int currentVersion = info.versionCode; // 版本号

                                if (versionCode > currentVersion) {
                                    showUpdateDialog();
                                }
                            } else {
                                dialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "检测失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
