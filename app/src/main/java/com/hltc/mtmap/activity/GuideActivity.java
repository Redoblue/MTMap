/*
package com.hltc.mtmap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hltc.mtmap.R;
import com.hltc.mtmap.util.GuideUtils;

public class GuideActivity extends Activity {
    private GuideUtils guideUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
*获取引导界面工具类的实例*

        guideUtil = GuideUtils.getInstance();
*调用引导界面*

        guideUtil.initGuide(this, R.drawable.add_guide);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*按钮的方式点击显示引导界面*

                guideUtil.initGuide(GuideActivity.this, R.drawable.add_guide);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*实际程序中，如果没有第一次了，那不会显示引导界面了。
                 这这时候，我们在setFirst中设置false，当我们点击的时候，
                 就没有效果了！不会再弹出了*

                guideUtil.setFirst(false);
                guideUtil.initGuide(GuideActivity.this, R.drawable.add_guide);
            }
        });
    }
}
*/
