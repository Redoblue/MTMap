package com.hltc.mtmap.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.fragment.GrainFragment;
import com.hltc.mtmap.fragment.MapFragment;
import com.hltc.mtmap.fragment.MessageFragment;
import com.hltc.mtmap.fragment.ProfileFragment;
import com.hltc.mtmap.fragment.PublishFragment;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final int[] TAB_NORMAL = {
            R.drawable.ic_tab_map_normal,
            R.drawable.ic_tab_grain_normal,
            R.drawable.transparent,
            R.drawable.ic_tab_message_normal,
            R.drawable.ic_tab_private_normal
    };
    private static final int[] TAB_PRESSED = {
            R.drawable.ic_tab_map_pressed,
            R.drawable.ic_tab_grain_pressed,
            R.drawable.transparent,
            R.drawable.ic_tab_message_pressed,
            R.drawable.ic_tab_private_pressed
    };
    private static final int TAB_ITEM_NUM = 5;
    private static final int TAB_MAP = 0;
    private static final int TAB_GRAIN = 1;
    private static final int TAB_PUBLISH = 2;
    private static final int TAB_MESSAGE = 3;
    private static final int TAB_PRIVATE = 4;
    public MyApplication application;
    @InjectView(R.id.tab_item_map)
    TextView tabMap;
    @InjectView(R.id.tab_item_grain)
    TextView tabGrain;
    @InjectView(R.id.tab_item_publish)
    TextView tabPublish;
    @InjectView(R.id.tab_item_message)
    TextView tabMessage;
    @InjectView(R.id.tab_item_private)
    TextView tabPrivate;
    private int[] ids = {
            R.id.tab_item_map,
            R.id.tab_item_grain,
            R.id.tab_item_publish,
            R.id.tab_item_message,
            R.id.tab_item_private
    };
    private FragmentManager fgManager;
    private int currentTabIndex = 0;
    private List<Fragment> fragmentList;
    private List<TextView> tabItemList;
//    public DaoManager daoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Log.d("MainActivity", "onCreate");///
        fgManager = getSupportFragmentManager();
        application = (MyApplication) getApplication();
//        daoManager = DaoManager.getDaoManager(this);
        initView();

        if (!AppUtils.isNetworkConnected(this)) {
            ToastUtils.showShort(this, R.string.network_not_connected);
        }

//        application.initLoginInfo();

        // 检查是否需要下载欢迎图片
        checkWelcomeImage();
    }

    private void initView() {
        fragmentList = new ArrayList<>(TAB_ITEM_NUM);
        for (int i = 0; i < TAB_ITEM_NUM; i++) {
            fragmentList.add(i, null);
        }

        tabItemList = new ArrayList<>(TAB_ITEM_NUM);
        tabItemList.add(tabMap);
        tabItemList.add(tabGrain);
        tabItemList.add(tabPublish);
        tabItemList.add(tabMessage);
        tabItemList.add(tabPrivate);

        for (TextView item : tabItemList) {
            item.setOnClickListener(this);
        }

        setChioceItem(0); // 首先看到首页
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == view.getId()) {
                if (i == 2) {
                    Intent intent = new Intent(this, PublishActivity.class);
                    startActivity(intent);
                    break;
                }
                currentTabIndex = i;
                setChioceItem(i);
                break;
            }
        }
    }

    public void setChioceItem(int index) {
        FragmentTransaction transaction = fgManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);

        Drawable drawable = getResources().getDrawable(TAB_PRESSED[index]);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tabItemList.get(index).setCompoundDrawables(null, drawable, null, null);
        tabItemList.get(index).setTextColor(index == 2 ? Color.WHITE :
                getResources().getColor(R.color.green_main));

        if (fragmentList.get(index) == null) {
            fragmentList.set(index, getFragmentByIndex(index));
            transaction.add(R.id.content, fragmentList.get(index));
        } else {
            transaction.show(fragmentList.get(index));
        }
        transaction.commit();
    }

    private Fragment getFragmentByIndex(int index) {
        switch (index) {
            case TAB_MAP:
                return new MapFragment();
            case TAB_GRAIN:
                return new GrainFragment();
            case TAB_PUBLISH:
                return new PublishFragment();
            case TAB_MESSAGE:
                return new MessageFragment();
            case TAB_PRIVATE:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        for (Fragment fragment : fragmentList) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

    public void clearChioce() {
        for (int i = 0; i < TAB_ITEM_NUM; i++) {
            Drawable drawable = getResources().getDrawable(TAB_NORMAL[i]);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tabItemList.get(i).setCompoundDrawables(null, drawable, null, null);
            tabItemList.get(i).setTextColor(
                    i == 2 ? Color.WHITE : getResources().getColor(R.color.dark_grey));
        }
    }

    private void checkWelcomeImage() {
        if (!AppUtils.isNetworkConnected(this)) {
            return;
        }
        //通过结构查询是否有更新的图片，并得到列表，然后使用HttpUtils进行下载
    }


    /**
     * ********************** Life Cycle ***********************
     */

    @Override
    protected void onResume() {
        super.onResume();
        setChioceItem(currentTabIndex);
        Log.d("MainActivity", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}