package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hltc.mtmap.MFriend;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.FriendListAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.DialogManager;
import com.hltc.mtmap.bean.PhoneContact;
import com.hltc.mtmap.gmodel.Friend;
import com.hltc.mtmap.gmodel.FriendProfile;
import com.hltc.mtmap.gmodel.FriendStatus;
import com.hltc.mtmap.helper.ApiHelper;
import com.hltc.mtmap.helper.PinyinComparator;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.CharacterParser;
import com.hltc.mtmap.widget.CharacterBar;
import com.hltc.mtmap.widget.CharacterBar.OnTouchingLetterChangedListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FriendListActivity extends Activity {

    public static final int FOLDER_NEW_FRIEND = 0;
    public static List<FriendStatus> friendStatuses = new ArrayList<>();
    @InjectView(R.id.lv_friend_list)
    ListView sortListView;
    @InjectView(R.id.tv_friend_list_dialog)
    TextView dialog;
    @InjectView(R.id.sb_friend_list_sidebar)
    CharacterBar characterBar;
    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
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
                    Intent intent = new Intent(FriendListActivity.this, FriendStatusActivity.class);
                    startActivity(intent);
                } else {//点击了联系人
                    int index = position - 1;
                    ApiHelper.httpGetFriendProfile(FriendListActivity.this,adapterList.get(index).getUserId());
                }
            }
        });

//        adapterList = filledData(getResources().getStringArray(R.array.date));
//        Collections.sort(adapterList, pinyinComparator);
        adapterList = DaoManager.getManager().getAllFriend();
//        Collections.sort(adapterList, pinyinComparator);
        adapter = new FriendListAdapter(this, adapterList);
        sortListView.setAdapter(adapter);
//        adapter.updateListView(adapterList);
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
        adapter.notifyDataSetChanged();
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


}
