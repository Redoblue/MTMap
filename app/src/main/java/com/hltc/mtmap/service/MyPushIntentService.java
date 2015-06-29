package com.hltc.mtmap.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

/**
 * Created by redoblue on 15-6-29.
 */
public class MyPushIntentService extends UmengBaseIntentService {
    // 如果需要打开Activity，请调用Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)；否则无法打开Activity。
    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(msg);//向服务器发送水涨消息打开的数据
            Log.d("MT", "service started");
            Log.d("MT", "message=" + message);
            Log.d("MT", "custom=" + msg.custom);
            // code  to handle message here
            // ...
        } catch (Exception e) {
            Log.d("MT", e.getMessage());
        }
    }
}
