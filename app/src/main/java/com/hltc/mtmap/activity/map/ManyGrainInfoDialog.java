package com.hltc.mtmap.activity.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.ImageButton;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.ManyGrainPagerAdapter;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.gmodel.ClusterGrain;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ManyGrainInfoDialog extends FragmentActivity {

    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.btn_exit)
    ImageButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_many_grain_info);
        ButterKnife.inject(this);

        initView();
    }

    @OnClick(R.id.btn_exit)
    public void exit() {
        AppManager.getAppManager().finishActivity(this);
    }

    private void initView() {
        List<ClusterGrain> list = getIntent().getParcelableArrayListExtra("grains");
        ManyGrainPagerAdapter adapter = new ManyGrainPagerAdapter(getSupportFragmentManager(), this, list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size() / 9 + 1);//设置缓存视图的数目
    }

}
