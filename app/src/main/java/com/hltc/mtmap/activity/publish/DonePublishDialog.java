package com.hltc.mtmap.activity.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.MyFavouritesActivity;
import com.hltc.mtmap.activity.profile.MyGrainActivity;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.bean.ParcelableGrain;
import com.hltc.mtmap.gmodel.GrainDetail;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.task.PublishAsyncTask;
import com.hltc.mtmap.task.SyncDataAsyncTask;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.hltc.mtmap.util.WeChatUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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
import butterknife.OnClick;

/**
 * Created by redoblue on 15-5-14.
 */
public class DonePublishDialog extends Activity {

    private static String TAG = "DonePublishDialog";
    @InjectView(R.id.btn_done_publish_home)
    TextView btnGoHome;
    @InjectView(R.id.btn_done_publish_maitian)
    TextView btnMyMaitian;
    @InjectView(R.id.btn_done_publish_share)
    TextView btnShare;

    private ParcelableGrain grain;

    private PopupWindow shareWindow;

    private IWXAPI iwxapi;
    private boolean canShare = false;

    private long grainId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.dialog_done_publish);
        ButterKnife.inject(this);
        registerWX();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getWindow().setBackgroundDrawableResource(R.color.half_transparent);

        grain = getIntent().getParcelableExtra("GRAIN");
        httpPublish();
    }

    private void registerWX() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo == null) {
            return;
        }
        String appId = appInfo.metaData.getString("com.hltc.mtmap.wx_id");
        iwxapi = WXAPIFactory.createWXAPI(this, appId, true);
        iwxapi.registerApp(appId);
    }

    @OnClick({
            R.id.btn_done_publish_home,
            R.id.btn_done_publish_maitian,
            R.id.btn_done_publish_share
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done_publish_home:
                AppManager.getAppManager().finishActivity(this);
                AppManager.getAppManager().finishActivity(CreateGrainActivity2.class);
                break;
            case R.id.btn_done_publish_maitian:
                Intent intent = new Intent(DonePublishDialog.this, MyGrainActivity.class);
                intent.putExtra("from", DonePublishDialog.this.getLocalClassName());
                startActivity(intent);
                AppManager.getAppManager().finishActivity(this);
                AppManager.getAppManager().finishActivity(CreateGrainActivity2.class);
                AppManager.getAppManager().finishActivity(MyFavouritesActivity.class);
                break;
            case R.id.btn_done_publish_share:
                if (shareWindow != null && shareWindow.isShowing()) {
                    shareWindow.dismiss();
                    return;
                }
                showPopwindow();
                break;
        }
    }

    private View.OnClickListener shareListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(!canShare){
                ToastUtils.showShort(DonePublishDialog.this,"网络繁忙，请稍后再试");
                return;
            }
            switch (v.getId()) {
                case R.id.tv_publish_share_wechat:
                    share2WechartFriends();
                    break;
                case R.id.tv_publish_share_circle:
                    share2WechatTimeline();
                    break;
                default:
                    break;
            }
        }
    };

    private void share2WechatTimeline() {
        GrainDetail grainDetail = new GrainDetail();
        grainDetail.text = grain.text;
        grainDetail.grainId = grainId;
        WeChatUtils.shareGrain2TimeLine(this, iwxapi, grainDetail);
    }

    private void share2WechartFriends() {
        GrainDetail grainDetail = new GrainDetail();
        grainDetail.text = grain.text;
        grainDetail.grainId = grainId;
        WeChatUtils.shareGrain2WechatSession(this, iwxapi, grainDetail);
    }

    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.window_publish_share, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        shareWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        shareWindow.setFocusable(true);
        shareWindow.setOutsideTouchable(true);
        shareWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        shareWindow.setAnimationStyle(android.R.style.Widget_PopupWindow);
        // 在底部显示
        shareWindow.showAtLocation(this.findViewById(R.id.layout_done_publish),
                Gravity.BOTTOM, 0, 0);

        // 这里检验popWindow里的button是否可以点击
        List<TextView> popItems = new ArrayList<>();
        TextView wechat = (TextView) view.findViewById(R.id.tv_publish_share_wechat);
        wechat.setTag(0);
        TextView circle = (TextView) view.findViewById(R.id.tv_publish_share_circle);
        circle.setTag(1);
        TextView qq = (TextView) view.findViewById(R.id.tv_publish_share_qq);
        qq.setTag(2);
        TextView weibo = (TextView) view.findViewById(R.id.tv_publish_share_weibo);
        weibo.setTag(3);
        popItems.add(wechat);
        popItems.add(circle);
        popItems.add(qq);
        popItems.add(weibo);

        for (TextView tv : popItems) {
            tv.setOnClickListener(shareListener);
        }

        //popWindow消失监听方法
        shareWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
            }
        });
    }

    private void httpPublish() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put("userId", grain.userId);
            json.put("token", grain.token);
            json.put("mcateId", grain.mcateId);
            json.put("siteSource", grain.siteSource);
            json.put("siteId", grain.siteId);
            json.put("siteName", grain.siteName);
            json.put("siteAddress", grain.siteAddress);
            json.put("sitePhone", grain.sitePhone);
            json.put("siteType", grain.siteType);
            json.put("lat", grain.latitude);
            json.put("lon", grain.longitude);
            json.put("cityCode", grain.cityCode);
            json.put("isPublic", grain.isPublic);
            json.put("text", grain.text);
            JSONArray array = new JSONArray();
            for (String s : PhotoHelper.larges) {
                array.put("http://" + OssManager.bucketName + "." + OssManager.ossHost + "/"
                        + OssManager.getFileKeyByLocalUrl(s));
            }
            json.put("images", array);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getPublishUrl(),
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        try{
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject resultObject = new JSONObject(result);
                                JSONObject dataJson = resultObject.getJSONObject("data");
                                String granIdStr = dataJson.getString("grainId");
                                grainId = Long.valueOf(granIdStr);
                                canShare = true;
                                //同步麦粒
                                SyncDataAsyncTask.httpSyncGrainNumber();
                                SyncDataAsyncTask.httpSyncMyGrainData();
                                // 上传图片
                                if (PhotoHelper.larges.size() > 0) {
                                    new PublishAsyncTask().execute();
                                }
                            } else {
                                Log.d("DonePublishDialog", result);
                            }
                        }catch (Exception e){
                          Log.e(TAG,e.getMessage());

                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.showShort(DonePublishDialog.this, ApiUtils.TIP_NET_EXCEPTION);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
