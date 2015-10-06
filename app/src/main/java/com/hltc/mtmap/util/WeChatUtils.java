package com.hltc.mtmap.util;

import android.content.Context;
import android.util.Log;

import com.hltc.mtmap.gmodel.GrainDetail;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class WeChatUtils {

    public static final String GRAIN_SHRAE_URL_ROOT = "http://www.maitianditu.com/maitian/v1/grain/share/";

    public static final String APP_SHRAE_URL = "http://www.maitianditu.com/maitian/pages/recommend.html?from=timeline&isappinstalled=0";
    public static boolean shareGrain2TimeLine(Context context, IWXAPI iwxapi, GrainDetail grain) {
        if (!iwxapi.isWXAppSupportAPI()) {
            ToastUtils.showShort(context, ApiUtils.TIP_WX_NOT_SUPPORT);
            return false;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = GRAIN_SHRAE_URL_ROOT + String.valueOf(grain.grainId);
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = grain.text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        return iwxapi.sendReq(req);
    }

    public static boolean shareGrain2WechatSession(Context context, IWXAPI iwxapi, GrainDetail grain) {
        if (!iwxapi.isWXAppSupportAPI()) {
            ToastUtils.showShort(context, ApiUtils.TIP_WX_NOT_SUPPORT);
            return false;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = GRAIN_SHRAE_URL_ROOT + String.valueOf(grain.grainId);
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = grain.text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return iwxapi.sendReq(req);
    }

    public static boolean shareApp2WecharSession(Context context,IWXAPI iwxapi){
        if (!iwxapi.isWXAppSupportAPI()) {
            ToastUtils.showShort(context, ApiUtils.TIP_WX_NOT_SUPPORT);
            return false;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = APP_SHRAE_URL;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title ="快来麦田地图一起分享吧";
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return iwxapi.sendReq(req);
    }
}
