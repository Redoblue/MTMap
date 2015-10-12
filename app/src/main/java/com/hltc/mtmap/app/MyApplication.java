package com.hltc.mtmap.app;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hltc.mtmap.MTMessage;
import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.LocalUserInfo;
import com.hltc.mtmap.event.BaseMessageEvent;
import com.hltc.mtmap.gmodel.PraiseAndCommentInfo;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;

public class MyApplication extends Application {

    public static final int TYPE_PRAISE = 1;
    public static final int TYPE_COMMENT = 2;
    public static final int TYPE_ADD_FRIEND = 3;
    public static final int TYPE_AGREE_REQUEST = 4;
    private static final String TAG = "MyApplication";

    public static boolean isShowRedTipPro = false;

    public static String signInStatus = "00"; // "00", "01", "10", "11" 第一位: 1 在线 0 离线  第二位： 1 登录 0 未登录
    //显示图片的配置
    public static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    public static boolean isDownloadingNewVersion = false;
    private static Context mContext;
    private PushAgent mPushAgent;

    private Handler shareHandler;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        initIdentify();
        initImageLoader();
        initPushAgent();
    }

    private void initImageLoader() {
        //使用默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    private void initPushAgent() {
        mPushAgent = PushAgent.getInstance(mContext);
       // mPushAgent.setDebugMode(true);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        manageMessage(msg);//处理消息
                       return buildNotification(context, msg);
//                        return buildNotification(msg, null);
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Log.d("MT", "click:" + msg);
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    private Notification buildNotification(Context context, UMessage msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_umeng);
        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
        myNotificationView.setImageViewResource(R.id.notification_large_icon, R.mipmap.ic_launcher);
//        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
        builder.setContent(myNotificationView);
        builder.setAutoCancel(true);
        Notification mNotification = builder.build();
        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
        mNotification.contentView = myNotificationView;
        return mNotification;
    }

    /**
     * 在状态栏显示通知
     */
    private Notification buildNotification2(UMessage msg, Class toClass) {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        // 定义Notification的各种属性
        Notification notification = new Notification(R.mipmap.ic_launcher, msg.ticker, System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        //notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_SOUND;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS = 5000; //闪光时间，毫秒

        // 设置通知的事件消息
        Intent notificationIntent = new Intent(this, toClass); // 点击该通知后要跳转的Activity
//        if (target != -1)
//            notificationIntent.putExtra("target_fragment", target);
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, msg.title, msg.text, contentItent);

        // 把Notification传递给NotificationManager
//        notificationManager.notify(0, notification);
        return notification;
    }


    private void manageMessage(UMessage msg) {
        int type = getType(msg);
        switch (type) {
            case TYPE_PRAISE:
                httpGetMessageInfo(TYPE_PRAISE, msg);
                break;
            case TYPE_COMMENT:
                httpGetMessageInfo(TYPE_COMMENT, msg);
                break;
            case TYPE_ADD_FRIEND:
            case TYPE_AGREE_REQUEST:
                isShowRedTipPro = true;
                BaseMessageEvent event = new BaseMessageEvent();
                event.action = BaseMessageEvent.EVENT_FRIENTLIST_RED_ROT_SHOW;
                EventBus.getDefault().post(event);
                break;
        }
    }

    private int getType(UMessage msg) {
        String type = msg.extra.get("type");
        if (type.equals("praise"))
            return TYPE_PRAISE;
        else if (type.equals("comment"))
            return TYPE_COMMENT;
        else if (type.equals("add_friend"))
            return TYPE_ADD_FRIEND;
        else if (type.equals("agree_friend"))
            return TYPE_AGREE_REQUEST;
        else return -1;
    }


    private void httpGetMessageInfo(int type, final UMessage msg) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put("grainId", StringUtils.toLong(msg.extra.get("grainId")));
            if (type == TYPE_PRAISE) {
                json.put("praiseId", StringUtils.toLong(msg.extra.get("praiseId")));
            } else if (type == TYPE_COMMENT) {
                json.put("commentId", StringUtils.toLong(msg.extra.get("commentId")));
            }
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "";
        if (type == TYPE_PRAISE) {
            url = ApiUtils.URL_ROOT + "message/praise.json";
        } else if (type == TYPE_COMMENT) {
            url = ApiUtils.URL_ROOT + "message/comment.json";
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            Gson gson = new Gson();
                            try {
                                String data = new JSONObject(result).getString(ApiUtils.KEY_DATA);
                                PraiseAndCommentInfo paci = gson.fromJson(data, PraiseAndCommentInfo.class);
                                paci.type = msg.extra.get("type");
                                MTMessage message = toMTMessage(paci);
                                if (message != null) {
                                    long id = DaoManager.getManager().daoSession.getMTMessageDao().insert(message);
                                    BaseMessageEvent event = new BaseMessageEvent();
                                    event.action = BaseMessageEvent.EVENT_MESSAGE_CHANGE;
                                    EventBus.getDefault().post(event);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.e(TAG, "获取评论消息失败");
                    }
                });
    }

    private MTMessage toMTMessage(PraiseAndCommentInfo paci) {
        MTMessage message = null;
        try {
            message = new MTMessage();
            message.setId(System.currentTimeMillis());
            message.setType(paci.type);
            message.setUserId(paci.user.userId);
            message.setPortrait(paci.user.portrait);
            message.setNickName(paci.user.nickName);
            message.setRemark(paci.user.remark);
            message.setGrainId(paci.grain.grainId);
            message.setName(paci.grain.name);
            message.setAddress(paci.grain.address);
            message.setImage(paci.grain.image);
            message.setText(paci.grain.text);
            message.setCommentTxt(paci.commentTxt);
            message.setCreateTime(paci.createTime);
        } catch (Exception e) {
            e.printStackTrace();
            message = null;
        }
        return message;
    }

    private void initIdentify() {
        //用户身份状态检测
        if (AppUtils.isNetworkConnected(this)) {
            if (!StringUtils.isEmpty(AppConfig.getAppConfig().getConfToken())) {
                httpLoginByToken();
            } else {
                MyApplication.signInStatus = "10";
            }
        } else {
            if (!StringUtils.isEmpty(AppConfig.getAppConfig().getConfToken())) {
                MyApplication.signInStatus = "01";
            } else {
                MyApplication.signInStatus = "00";
            }
        }
    }

    private void httpLoginByToken() {
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

        HttpUtils http = new HttpUtils();
        http.configTimeout(2500);
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getLoginByTokenUrl(),
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                LocalUserInfo userInfo = new LocalUserInfo();
                                userInfo.setUserId(data.getLong(ApiUtils.KEY_USER_ID));
                                userInfo.setUserName(data.getString(ApiUtils.KEY_USR_NAME));
                                userInfo.setIsLogin(StringUtils.toBool(data.getString(ApiUtils.KEY_USR_IS_LOG_IN)));
                                userInfo.setNickName(data.getString(ApiUtils.KEY_USR_NICKNAME));
                                userInfo.setPhone(data.getString(ApiUtils.KEY_USR_PHONE));
                                userInfo.setCreateTime(data.getString(ApiUtils.KEY_USR_CREATE_TIME));
                                userInfo.setPortrait(data.getString(ApiUtils.KEY_PORTRAIT));
                                userInfo.setPortraitSmall(data.getString(ApiUtils.KEY_USR_PORTRAIT_SMALL));
                                userInfo.setCoverImg(data.getString(ApiUtils.KEY_USR_COVER_IMG));
                                AppConfig.getAppConfig().setUserInfo(userInfo);

                                Log.d("MyApplication", userInfo.toString());

                                MyApplication.signInStatus = "11";
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    MyApplication.signInStatus = "10";
                                }
                            }
                        } catch (JSONException e) {
                            MyApplication.signInStatus = "10";
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        MyApplication.signInStatus = "10";
                    }
                });
    }

    public Handler getShareHandler() {
        return shareHandler;
    }

    public void setShareHandler(Handler shareHandler) {
        this.shareHandler = shareHandler;
    }
}
