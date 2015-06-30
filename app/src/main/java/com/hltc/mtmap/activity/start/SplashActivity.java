package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 启动界面
 */
public class SplashActivity extends Activity implements Animation.AnimationListener {

    @InjectView(R.id.activity_start_view)
    LinearLayout background;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);
//        loadBackground();

        initView();
        initAnimation();
    }

    private void initView() {
        background.setBackgroundResource(R.drawable.pic_start);
    }

    private void initAnimation() {
        // 渐变启动
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(2000);
        background.startAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // 查询是否第一次使用本软件
        if (AppUtils.isFirstTimeToUse(this)) {
            LogUtils.d("第一次使用分支");
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {    //判断登录状态，是则进入主界面，否则进入登录界面
            Log.d("MT", "splash: " + MyApplication.signInStatus);
            if (MyApplication.signInStatus.equals("11") || MyApplication.signInStatus.equals("01")) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (MyApplication.signInStatus.equals("10") || MyApplication.signInStatus.equals("00")) {
                LogUtils.d("未登录分支");
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            }
        }
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    private void loadBackground() {
        String path = FileUtils.getAppCache(this, "start");
        List<File> files = FileUtils.getAllFiles(path);
        if (!files.isEmpty()) {
            // 搜索本次要加载的图片并进行加载
            long today = StringUtils.getToday();
            for (File f : files) {
                long time[] = getTime(f.getName());
                if (today >= time[0] && today <= time[1]) {
                    background.setBackgroundDrawable(Drawable.createFromPath(f.getAbsolutePath()));
                    break;
                }
            }
        }
    }

    /**
     * 图片的名字是由显示的时间区间构成的，如“开始时间-结束时间.png”
     * 通过解析文件名字，就可以得到这个文件显示的时间
     *
     * @param time
     * @return
     */
    private long[] getTime(String time) {
        long res[] = new long[2];
        try {
            time = time.substring(0, time.indexOf("."));
            String t[] = time.split("_");
            res[0] = Long.parseLong(t[0]);
            if (t.length >= 2) {
                res[1] = Long.parseLong(t[1]);
            } else {
                res[1] = Long.parseLong(t[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
