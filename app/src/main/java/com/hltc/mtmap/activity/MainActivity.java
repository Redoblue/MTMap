package com.hltc.mtmap.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.fragment.AddFragment;
import com.hltc.mtmap.fragment.FriendFragment;
import com.hltc.mtmap.fragment.HomeFragment;
import com.hltc.mtmap.fragment.MeFragment;
import com.hltc.mtmap.fragment.MessageFragment;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final int[] TAB_NORMAL = {
            R.drawable.ic_tab_home_normal,
            R.drawable.ic_tab_friend_normal,
            R.drawable.ic_tab_add_normal,
            R.drawable.ic_tab_message_normal,
            R.drawable.ic_tab_me_normal
    };
    private static final int[] TAB_PRESSED = {
            R.drawable.ic_tab_home_pressed,
            R.drawable.ic_tab_friend_pressed,
            R.drawable.ic_tab_add_pressed,
            R.drawable.ic_tab_message_pressed,
            R.drawable.ic_tab_me_pressed
    };

    private static final int TAB_HOME = 0;
    private static final int TAB_FRIEND = 1;
    private static final int TAB_ADD = 2;
    private static final int TAB_MESSAGE = 3;
    private static final int TAB_ME = 4;

    private static final int TAB_ITEM_NUM = 5;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFF7597B3;
    private static final int BLUE = 0xFF0AB2FB;
    private FragmentManager fgManager;

    private RelativeLayout tabHome;
    private RelativeLayout tabFriend;
    private RelativeLayout tabAdd;
    private RelativeLayout tabMessage;
    private RelativeLayout tabMe;
    private ImageView imgHome;
    private ImageView imgFriend;
    private ImageView imgAdd;
    private ImageView imgMessage;
    private ImageView imgMe;
    private TextView tvHome;
    private TextView tvFriend;
    private TextView tvAdd;
    private TextView tvMessage;
    private TextView tvMe;
    private List<Fragment> fragmentList;
    private List<RelativeLayout> tabLayoutList;
    private List<ImageView> imageViewList;
    private List<TextView> textViewsList;

    public MyApplication application;
    public DaoManager daoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById();
        fgManager = getSupportFragmentManager();
        application = (MyApplication) getApplication();
        daoManager = DaoManager.getDaoManager(this);
        initListsAndListeners();

        if (!AppUtils.isNetworkConnected(this)) {
            ToastUtils.showShort(this, R.string.network_not_connected);
        }

//        application.initLoginInfo();

        // 检查是否需要下载欢迎图片
        checkWelcomeImage();
    }

    private void findViewById() {
        tabHome = (RelativeLayout) findViewById(R.id.tab_home_layout);
        tabFriend = (RelativeLayout) findViewById(R.id.tab_friend_layout);
        tabAdd = (RelativeLayout) findViewById(R.id.tab_add_layout);
        tabMessage = (RelativeLayout) findViewById(R.id.tab_message_layout);
        tabMe = (RelativeLayout) findViewById(R.id.tab_me_layout);

        imgHome = (ImageView) findViewById(R.id.img_home);
        imgFriend = (ImageView) findViewById(R.id.img_friend);
        imgAdd = (ImageView) findViewById(R.id.img_add);
        imgMessage = (ImageView) findViewById(R.id.img_message);
        imgMe = (ImageView) findViewById(R.id.img_me);

        tvHome = (TextView) findViewById(R.id.tv_home);
        tvFriend = (TextView) findViewById(R.id.tv_friend);
        tvAdd = (TextView) findViewById(R.id.tv_add);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvMe = (TextView) findViewById(R.id.tv_me);
    }

    private void initListsAndListeners() {
        fragmentList = new ArrayList<Fragment>(TAB_ITEM_NUM);
        for (int i = 0; i < TAB_ITEM_NUM; i++) {
            fragmentList.add(i, null);
        }

        tabLayoutList = new ArrayList<RelativeLayout>(TAB_ITEM_NUM);
        tabLayoutList.add(tabHome);
        tabLayoutList.add(tabFriend);
        tabLayoutList.add(tabAdd);
        tabLayoutList.add(tabMessage);
        tabLayoutList.add(tabMe);

        imageViewList = new ArrayList<ImageView>(TAB_ITEM_NUM);
        imageViewList.add(imgHome);
        imageViewList.add(imgFriend);
        imageViewList.add(imgAdd);
        imageViewList.add(imgMessage);
        imageViewList.add(imgMe);

        textViewsList = new ArrayList<TextView>(TAB_ITEM_NUM);
        textViewsList.add(tvHome);
        textViewsList.add(tvFriend);
        textViewsList.add(tvAdd);
        textViewsList.add(tvMessage);
        textViewsList.add(tvMe);

        for (RelativeLayout layout : tabLayoutList) {
            layout.setOnClickListener(this);
        }

        setChioceItem(0); // 首先看到首页
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_home_layout:
                setChioceItem(TAB_HOME);
                break;
            case R.id.tab_friend_layout:
                setChioceItem(TAB_FRIEND);
                break;
            case R.id.tab_add_layout:
                setChioceItem(TAB_ADD);
                break;
            case R.id.tab_message_layout:
                setChioceItem(TAB_MESSAGE);
                break;
            case R.id.tab_me_layout:
                setChioceItem(TAB_ME);
                break;
            default:
                break;
        }
    }

    public void setChioceItem(int index) {
        FragmentTransaction transaction = fgManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);

        imageViewList.get(index).setImageResource(TAB_PRESSED[index]);
        textViewsList.get(index).setTextColor(BLUE);
//        tabLayoutList.get(index).setBackgroundResource(R.drawable.ic_tabbar_bg_click);
        tabLayoutList.get(index).setBackgroundColor(getResources().getColor(R.color.grey));
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
            case TAB_HOME:
                return new HomeFragment();
            case TAB_FRIEND:
                return new FriendFragment();
            case TAB_ADD:
                return new AddFragment();
            case TAB_MESSAGE:
                return new MessageFragment();
            case TAB_ME:
                return new MeFragment();
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
            imageViewList.get(i).setImageResource(TAB_NORMAL[i]);
            tabLayoutList.get(i).setBackgroundColor(WHITE);
            textViewsList.get(i).setTextColor(GRAY);
        }
    }

    private void checkWelcomeImage() {
        if (!AppUtils.isNetworkConnected(this)) {
            return;
        }
        //通过结构查询是否有更新的图片，并得到列表，然后使用HttpUtils进行下载
    }
}