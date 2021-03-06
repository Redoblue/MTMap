package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hltc.mtmap.MFriend;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.FriendListAdapter;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.PhoneContact;
import com.hltc.mtmap.event.BaseMessageEvent;
import com.hltc.mtmap.gmodel.Friend;
import com.hltc.mtmap.gmodel.FriendStatus;
import com.hltc.mtmap.helper.ApiHelper;
import com.hltc.mtmap.helper.PinyinComparator;
import com.hltc.mtmap.util.CharacterParser;
import com.hltc.mtmap.widget.CharacterBar;
import com.hltc.mtmap.widget.CharacterBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class FriendListActivity extends Activity {

    public static final int FOLDER_NEW_FRIEND = 0;
    public static List<FriendStatus> friendStatuses = new ArrayList<>();
    @InjectView(R.id.lv_friend_list)
    ListView sortListView;
    @InjectView(R.id.tv_friend_list_dialog)
    TextView dialog;
    @InjectView(R.id.sb_friend_list_sidebar)
    CharacterBar characterBar;
    private List<PhoneContact> contacts = new ArrayList<>();

    private FriendListAdapter adapter;
    private CharacterParser characterParser;
    private List<MFriend> adapterList;
    private PinyinComparator pinyinComparator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_list);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        initView();
        EventBus.getDefault().register(this);
    }


    public void onEvent(BaseMessageEvent event) {
        if (event == null) return;
        switch (event.action) {
            case BaseMessageEvent.EVENT_DELETE_USER: {
                handleDeleteEvent(event);
                break;
            }
            case BaseMessageEvent.EVENT_MODIFY_USER_NAME:
                handleModifyUserNameEvent(event);
                break;
            default:
                break;
        }
    }

    private void handleModifyUserNameEvent(BaseMessageEvent event) {
        String newName = (String) event.tag;
        for (MFriend friend : adapterList) {
            if (friend.getUserId() == event.userId) {
                friend.setRemark(newName);
            }
        }
        adapter.update(adapterList);
    }

    private void handleDeleteEvent(BaseMessageEvent deleteFrientEvent) {
        for (MFriend friend : adapterList) {
            if (friend.getUserId() == deleteFrientEvent.userId) {
                friend.delete();
                adapterList.remove(friend);
            }
        }
        adapter.update(adapterList);
    }

    private void initView() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        characterBar.setTextView(dialog);
        characterBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == FOLDER_NEW_FRIEND) {

                    MyApplication.isShowRedTipPro = false;
                    BaseMessageEvent event = new BaseMessageEvent();
                    event.action = BaseMessageEvent.EVENT_FRIENTLIST_RED_ROT_HIDE;
                    EventBus.getDefault().post(event);

                    view.findViewById(R.id.iv_red_tip_pro).setVisibility(View.GONE);
                    Intent intent = new Intent(FriendListActivity.this, FriendStatusActivity.class);
                    startActivity(intent);
                } else {//点击了联系人
                    int index = position;
                    ApiHelper.httpGetFriendProfile(FriendListActivity.this, adapterList.get(index).getUserId());
                }
            }
        });

        adapterList = DaoManager.getManager().getAllFriend();
        adapter = new FriendListAdapter(this, adapterList);
        sortListView.setAdapter(adapter);
    }

    @OnClick({R.id.btn_bar_left,
            R.id.btn_bar_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                Intent intent = new Intent(this, AddFriendActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void refreshList() {
        adapterList = DaoManager.getManager().getAllFriend();
        adapter.update(adapterList);
    }

    private List<Friend> filledData(String[] date) {
        List<Friend> list = new ArrayList<>();

        for (String d : date) {
            Friend friend = new Friend();
            friend.setRemark(d);

            String pinyin = characterParser.getSelling(d);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                friend.setFirstCharacter(sortString.toUpperCase());
            } else {
                friend.setFirstCharacter("#");
            }

            list.add(friend);
        }
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
