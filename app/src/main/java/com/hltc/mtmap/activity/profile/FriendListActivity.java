package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.FriendAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.gmodel.Friend;
import com.hltc.mtmap.helper.PinyinComparator;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.CharacterParser;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FriendListActivity extends Activity {

    public static final int FOLDER_NEW_FRIEND = 0;

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

    private FriendAdapter adapter;
    private CharacterParser characterParser;
    private List<Friend> adapterList;
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
        tvBarTitle.setText("好友");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarRight.setBackgroundResource(R.drawable.ic_action_add_friend);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));
        btnBarRight.setWidth(AMapUtils.dp2px(this, 25));
        btnBarRight.setHeight(AMapUtils.dp2px(this, 23));

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
                    Intent intent = new Intent(FriendListActivity.this, UserDetailActivity.class);
                    intent.putExtra("userId", adapterList.get(index).getUserId());
                    startActivity(intent);
                }
            }
        });

//        adapterList = filledData(getResources().getStringArray(R.array.date));
//        Collections.sort(adapterList, pinyinComparator);
        adapterList = new ArrayList<>();
        adapter = new FriendAdapter(this, adapterList);
        sortListView.setAdapter(adapter);
        adapter.updateListView(adapterList);
        httpFetchFriendList();
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

    private void filterData(String filterStr) {
        List<Friend> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = adapterList;
        } else {
            filterDateList.clear();
            for (Friend sortModel : adapterList) {
                String name = StringUtils.isEmpty(sortModel.getRemark()) ? sortModel.getNickName() : sortModel.getRemark();
                if (name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)) {
                    filterDateList.add(sortModel);
                }
            }
        }

        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private void httpFetchFriendList() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_FRIEND_GET_LIST,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<Friend> friends = gson.fromJson(data.toString(), new TypeToken<List<Friend>>() {
                                }.getType());
                                adapterList.addAll(friends);
                                Collections.sort(adapterList, pinyinComparator);
                                adapter.updateListView(friends);

                                Log.d("MT", "friends: " + friends.toString());
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(FriendListActivity.this, errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
    }
}
