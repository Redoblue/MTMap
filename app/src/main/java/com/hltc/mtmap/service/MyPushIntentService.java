package com.hltc.mtmap.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.MyGrainActivity2;
import com.hltc.mtmap.gmodel.UmengMessage;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

/**
 * Created by redoblue on 15-6-29.
 */
public class MyPushIntentService extends UmengBaseIntentService {

    public static final int TYPE_PRAISE = 1;
    public static final int TYPE_COMMENT = 2;
    public static final int TYPE_ADD_FRIEND = 3;
    public static final int TYPE_AGREE_REQUEST = 4;

    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(msg);//向服务器发送水涨消息打开的数据
            Log.d("MT", "message=" + message);

            JSONObject json = new JSONObject(message).getJSONObject("extra");
            UmengMessage umeng = new Gson().fromJson(json.toString(), UmengMessage.class);
            handleMessage(umeng);
        } catch (Exception e) {
            Log.d("MT", e.getMessage());
        }
    }

    private void handleMessage(UmengMessage msg) {
        int type = getType(msg);
        switch (type) {
            case TYPE_PRAISE:
                showNotification("你收到了一个赞", "你的好友xxx赞了你的麦粒", MyGrainActivity2.class);
                break;
            case TYPE_COMMENT:
                //TODO
                break;
            case TYPE_ADD_FRIEND:
                //TODO
                break;
            case TYPE_AGREE_REQUEST:
                //TODO
                break;
        }
    }

    private int getType(UmengMessage msg) {
        int type = 0;
        if (msg.type.equals("praise"))
            type = TYPE_PRAISE;
        else if (msg.type.equals("comment"))
            type = TYPE_COMMENT;
        else if (msg.type.equals("add_friend"))
            type = TYPE_ADD_FRIEND;
        else if (msg.type.equals("agree_request"))
            type = TYPE_AGREE_REQUEST;
        return type;
    }

    /**
     * 在状态栏显示通知
     */
    private void showNotification(String ticker, String content, Class toClass) {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        // 定义Notification的各种属性
        Notification notification = new Notification(R.mipmap.ic_launcher, ticker, System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
//        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
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
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "麦田地图", content, contentItent);

        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }

    /*private void showNotification2(String ticker, String content, Class toClass) {
        Intent notificationIntent = new Intent(this, toClass); // 点击该通知后要跳转的Activity
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setContentTitle("麦田地图")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setLights(Color.BLUE, 1000, 1000)
                .setContentIntent(contentItent)
                .build();
    }*/

    //删除通知
    private void clearNotification() {
        // 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

}
