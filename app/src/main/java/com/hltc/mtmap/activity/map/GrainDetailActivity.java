package com.hltc.mtmap.activity.map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.SingleEditActivity;
import com.hltc.mtmap.activity.common.ImageViewerActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.DialogManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.event.CommentEvent;
import com.hltc.mtmap.gmodel.GrainDetail;
import com.hltc.mtmap.helper.ApiHelper;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.DateUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class GrainDetailActivity extends FragmentActivity {

    private static final String TAG = "GrainDetailActivity";
    private static int TOTAL_COUNT = 5;
    @InjectView(R.id.btn_bar_back)
    Button btnBarBack;
    @InjectView(R.id.btn_bar_favor)
    ToggleButton btnBarFavor;
    @InjectView(R.id.btn_bar_share)
    Button btnBarShare;
    @InjectView(R.id.civ_grain_detail_portrait)
    CircleImageView civGrainDetailPortrait;
    @InjectView(R.id.tv_grain_detail_nickname)
    TextView tvGrainDetailNickname;
    @InjectView(R.id.tv_grain_detail_text)
    TextView tvGrainDetailText;
    @InjectView(R.id.tv_grain_detail_address)
    TextView tvGrainDetailAddress;
    @InjectView(R.id.tv_grain_detail_time)
    TextView tvGrainDetailTime;
    @InjectView(R.id.btn_grain_detail_actions)
    Button btnGrainDetailActions;
    @InjectView(R.id.tv_grain_detail_praise)
    TextView tvGrainDetailPraise;
    @InjectView(R.id.layout_grain_detail_comment)
    LinearLayout layoutGrainDetailComment;
    @InjectView(R.id.hsv_grain_detail_gallery)
    HorizontalScrollView hsvGrainDetailGallery;
    @InjectView(R.id.layout_grain_detail_image)
    LinearLayout layoutGrainDetailImage;

    private GrainDetail grainDetail;
    private PopupWindow popWindow;
    private boolean isFavored = false;

    //被评论者ID
    private long toUserId = -1;

    private IWXAPI iwxapi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_grain_detail);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        registerWX();
        initView();
        getSignature();
    }

    private void registerWX() {
        ApplicationInfo appInfo=null;
        try {
             appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(appInfo==null){
            return ;
        }
        String appId =appInfo.metaData.getString("com.hltc.mtmap.wx_id");
        iwxapi = WXAPIFactory.createWXAPI(this, appId, true);
        iwxapi.registerApp(appId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({
            R.id.btn_grain_detail_actions,
            R.id.btn_bar_back,
            R.id.btn_bar_favor,
            R.id.btn_bar_share,
            R.id.civ_grain_detail_portrait
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_grain_detail_actions:
                showPopActions();
                break;
            case R.id.btn_bar_back:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_favor:
                httpFavorGrain();
                break;
            case R.id.btn_bar_share:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                share2WX();
                break;
            case R.id.civ_grain_detail_portrait:
                jump2UserProfile();
                break;
            default:
                break;
        }
    }

    private void jump2UserProfile() {
        if(grainDetail.publisher.userId == AppConfig.getAppConfig().getConfUsrUserId()){
            MainActivity.startFromGrainDetailActivity(this);
            AppManager.getAppManager().finishActivity(this);
        }else{
            ApiHelper.httpGetFriendProfile(GrainDetailActivity.this, grainDetail.publisher.userId);
        }
    }
    public void getSignature() {//用来获取signa
        PackageManager manager = getPackageManager();
        PackageInfo packageInfo;
        Signature[] signatures;
        String signature="";
        StringBuilder builder = new StringBuilder();
            try {
                /** 通过包管理器获得指定包名包含签名的包信息 **/
                packageInfo = manager.getPackageInfo("com.hltc.mtmap", PackageManager.GET_SIGNATURES);
                /******* 通过返回的包信息获得签名数组 *******/
                signatures = packageInfo.signatures;
                /******* 循环遍历签名数组拼接应用签名 *******/
                for (Signature signatur : signatures) {
                    Log.i(TAG,signatur.toCharsString());
                    builder.append(signatur.toCharsString());
                }
                /************** 得到应用签名 **************/
                signature = builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        Log.i(TAG,signature);
        return;
    }
    private void share2WX() {
        if(iwxapi==null)return;
        if(!iwxapi.isWXAppSupportAPI()){
            ToastUtils.showShort(this,ApiUtils.TIP_WX_NOT_SUPPORT);
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://www.maitianditu.com/maitian/v1/grain/share/"+String.valueOf(grainDetail.grainId);
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = grainDetail.text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene =  SendMessageToWX.Req.WXSceneTimeline;
        Log.i(TAG,String.valueOf(iwxapi.sendReq(req)));

    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void onEvent(CommentEvent ce) {
        httpCommentGrain(ce.getComment());
    }

    private void initView() {
        grainDetail = getIntent().getParcelableExtra("grain");
        isFavored = grainDetail.isFavored == 1;

        btnBarFavor.setChecked(isFavored);

        ImageLoader.getInstance().displayImage(
                grainDetail.publisher.portrait, civGrainDetailPortrait, MyApplication.displayImageOptions);
        tvGrainDetailNickname.setText(StringUtils.isEmpty(grainDetail.publisher.remark) ?
                grainDetail.publisher.nickName : grainDetail.publisher.remark);
        tvGrainDetailText.setText(grainDetail.text);
        tvGrainDetailAddress.setText(grainDetail.site.name);
        tvGrainDetailTime.setText(DateUtils.getFriendlyTime(grainDetail.createTime));

        refreshPraise();
        refreshComment();

        if (grainDetail != null && grainDetail.images != null && grainDetail.images.size() > 0) {
            for (final String s : grainDetail.images) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(params);
                ImageLoader.getInstance().displayImage(
                        OssManager.getGrainThumbnailUrl(s), iv, MyApplication.displayImageOptions);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageViewerActivity.start(GrainDetailActivity.this, grainDetail.images);
                    }
                });
                layoutGrainDetailImage.addView(iv);
            }
        } else {
            hsvGrainDetailGallery.setVisibility(View.GONE);
        }
    }

    private void httpFavorGrain() {
        final ProgressDialog dialog = DialogManager.buildProgressDialog(this, "操作中...");
        dialog.show();

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", grainDetail.grainId);
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
                            // 收藏成功
                            dialog.dismiss();
                            if (isFavored) {
                                Toast.makeText(GrainDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                                isFavored = false;
                                //DaoManager.getManager().daoSession.getMTMyFavouriteDao().insert()
                            } else {
                                Toast.makeText(MyApplication.getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                                isFavored = true;
                            }
                            btnBarFavor.setChecked(isFavored);
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        Toast.makeText(GrainDetailActivity.this, "操作失败,请检查您的网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void httpCommentGrain(final String s) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", grainDetail.grainId);
            json.put("text", s);
            if (toUserId != -1) {
                json.put("tocid", toUserId);
            }
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiUtils.URL_ROOT + ApiUtils.URL_COMMENT_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            Toast.makeText(GrainDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject json = new JSONObject(responseInfo.result).getJSONObject("data");
                                GrainDetail.Comment comment = new GrainDetail.Comment();
                                comment.cid = json.getLong("cid");
                                comment.nickName = AppConfig.getAppConfig().getConfUsrNickName();
                                comment.userId = AppConfig.getAppConfig().getConfUsrUserId();
                                comment.portrait = AppConfig.getAppConfig().getConfUsrPortrait();
                                comment.createTime = String.valueOf(System.currentTimeMillis());
                                comment.text = s;
                                grainDetail.comment.add(comment);
                                refreshComment();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        // 收藏失败
                    }
                });
    }

    private void refreshPraise() {
        if (grainDetail.praise.size() > 0) {
            tvGrainDetailPraise.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            List<GrainDetail.Praise> praises = grainDetail.praise;
            for (GrainDetail.Praise p : praises) {
                if (p.userId == AppConfig.getAppConfig().getConfUsrUserId())
                    sb.append("我");
                else
                    sb.append(StringUtils.isEmpty(p.remark) ? p.nickName : p.remark);

                if (p.equals(praises.get(praises.size() - 1))) {
                    break;
                }
                sb.append(", ");
            }

            if (praises.size() > 0) {
                sb.append(" 赞了该推荐");
                tvGrainDetailPraise.setText(sb.toString());
            }
        } else {
            tvGrainDetailPraise.setVisibility(View.GONE);
        }
        tvGrainDetailPraise.invalidate();
    }

    private void refreshComment() {
        layoutGrainDetailComment.removeAllViews();
        if (grainDetail.comment.size() > 0) {
            ViewHolder holder = new ViewHolder();
            for (int i = grainDetail.comment.size() - 1; i >= 0; i--) {
                final GrainDetail.Comment comment = grainDetail.comment.get(i);
                View commentView = LayoutInflater.from(this).inflate(R.layout.item_grain_detail_comment, null);
                holder.portrait = (CircleImageView) commentView.findViewById(R.id.civ_item_grain_detail_portrait);
                holder.name = (TextView) commentView.findViewById(R.id.tv_item_grain_detail_nickname);
                holder.time = (TextView) commentView.findViewById(R.id.tv_item_grain_detail_time);
                holder.comment = (TextView) commentView.findViewById(R.id.tv_item_grain_detail_comment);
                holder.cmtValue = (TextView) commentView.findViewById(R.id.tv_cmt_value);
                holder.comment2User = (TextView) commentView.findViewById(R.id.tv_comment2userId);
                holder.cmtValue.setVisibility(View.INVISIBLE);
                holder.comment2User.setVisibility(View.INVISIBLE);
                //TODO 需要在comment中添加被评论者nickName,然后显示在评论列表中
                // if(comment.)
                holder.name.setText(StringUtils.isEmpty(comment.remark) ? comment.nickName : comment.remark);
                holder.time.setText(DateUtils.getFriendlyTime(comment.createTime));
                holder.comment.setText(comment.text);

                try {
                    ImageLoader.getInstance().displayImage(OssManager.getGrainThumbnailUrl(comment.portrait),
                            holder.portrait, MyApplication.displayImageOptions
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                commentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        toUserId = comment.userId;
                        SingleEditActivity.startFromGrainDetailActivtiy(GrainDetailActivity.this);
                        return true;
                    }
                });

                layoutGrainDetailComment.addView(commentView);
            }
        }
        layoutGrainDetailComment.invalidate();
    }

    private void showPopActions() {
        View view = getLayoutInflater().inflate(R.layout.window_grain_detail_actions, null);
        popWindow = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT, AMapUtils.dp2px(this, 30), false);
        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        popWindow.setFocusable(true);
        //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
        popWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        btnGrainDetailActions.getLocationOnScreen(location);
        popWindow.showAtLocation(btnGrainDetailActions,
                Gravity.NO_GRAVITY, location[0] - AMapUtils.dp2px(this, 150), location[1] - AMapUtils.dp2px(this, 5));

        TextView praise = (TextView) view.findViewById(R.id.tv_praise);
        TextView comment = (TextView) view.findViewById(R.id.tv_comment);

        praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                httpPraiseGrain();
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                toUserId = -1;
                SingleEditActivity.startFromGrainDetailActivtiy(GrainDetailActivity.this);
            }
        });
    }

    private void httpPraiseGrain() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("gid", grainDetail.grainId);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiUtils.URL_ROOT + ApiUtils.URL_PRAISE_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.result.contains(ApiUtils.KEY_SUCCESS)) {
                            List<Long> ids = new ArrayList<>();
                            for (GrainDetail.Praise p : grainDetail.praise) {
                                ids.add(p.userId);
                            }

                            if (ids.contains(AppConfig.getAppConfig().getConfUsrUserId())) {
                                Toast.makeText(GrainDetailActivity.this, "取消点赞成功", Toast.LENGTH_SHORT).show();
                                for (GrainDetail.Praise p : grainDetail.praise) {
                                    if (p.userId == AppConfig.getAppConfig().getConfUsrUserId())
                                        grainDetail.praise.remove(p);
                                }
                            } else {

                                Toast.makeText(GrainDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                GrainDetail.Praise praise = new GrainDetail.Praise();
                                praise.userId = AppConfig.getAppConfig().getConfUsrUserId();
                                praise.nickName = AppConfig.getAppConfig().getConfUsrNickName();
                                grainDetail.praise.add(praise);
                            }
                            refreshPraise();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        // 收藏失败
                    }
                }

        );
    }

    class ViewHolder {
        CircleImageView portrait;
        TextView name;
        TextView time;
        TextView comment;
        TextView cmtValue;
        TextView comment2User;
    }


}