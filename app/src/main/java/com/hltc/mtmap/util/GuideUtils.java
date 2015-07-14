package com.hltc.mtmap.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hltc.mtmap.R;

/**
 * @类名:GuideUtil
 * @类描述:引导工具界面
 */
public class GuideUtils {
    private static GuideUtils instance = null;
    private Context context;
    private WindowManager windowManager;
    /**
     * 是否第一次进入该程序 *
     */
    private boolean isFirst = true;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // 设置LayoutParams参数
                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                    // 设置显示格式
                    params.format = PixelFormat.RGBA_8888;
                    // 设置对齐方式
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    // 设置宽高
                    params.width = ScreenUtils.getScreenWidth(context);
                    params.height = ScreenUtils.getScreenHeight(context);
                    // 设置动画
                    params.windowAnimations = R.style.anim_guide;
                    // 添加到当前的窗口上
//                    windowManager.addView(imgView, params);
                    break;
            }
        }
    };

    /**
     * 采用私有的方式，只保证这种通过单例来引用，同时保证这个对象不会存在多个*
     */
    private GuideUtils() {
    }

    /**
     * 采用单例的设计模式，同时用了同步锁*
     */
    public static GuideUtils getInstance() {
        synchronized (GuideUtils.class) {
            if (null == instance) {
                instance = new GuideUtils();
            }
        }
        return instance;
    }

    /**
     * @方法说明:初始化 * @方法名称:initGuide
     * * @param context
     * * @param drawableRourcesId：引导图片的资源Id
     * * @返回值:void
     */
    public void initGuide(Activity context, int drawableRourcesId) {
        /**如果不是第一次进入该界面**/
        if (!isFirst) {
            return;
        }
        this.context = context;
        windowManager = context.getWindowManager();        /** 动态初始化图层**/
        final ImageView imgView = new ImageView(context);
        imgView.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imgView.setScaleType(ImageView.ScaleType.FIT_START);
        imgView.setImageResource(drawableRourcesId);        /**这里我特意用了一个handler延迟显示界面，主要是为了进入界面后，你能看到它淡入得动画效果，不然的话，引导界面就直接显示出来**/
//        handler.sendEmptyMessageDelayed(1, 500);

        windowManager.addView(imgView, getParams());
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {                /** 点击图层之后，将图层移除**/
                windowManager.removeView(imgView);
            }
        });
    }

    private WindowManager.LayoutParams getParams() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置显示格式
        params.format = PixelFormat.RGBA_8888;
        // 设置对齐方式
        params.gravity = Gravity.LEFT | Gravity.TOP;
        // 设置宽高
        params.width = ScreenUtils.getScreenWidth(context);
        params.height = ScreenUtils.getScreenHeight(context);
        // 设置动画
        params.windowAnimations = R.style.anim_guide;

        return params;
    }
}
