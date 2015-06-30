package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.SearchFriendListAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.ContactInfo;
import com.hltc.mtmap.orm.model.MTUser;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
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
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-6-30.
 */
public class SearchFriendActivity extends Activity implements TextView.OnEditorActionListener {

    @InjectView(R.id.et_search_friend)
    EditText etSearchFriend;
    @InjectView(R.id.btn_search_friend_cancel)
    Button btnSearchFriendCancel;
    @InjectView(R.id.lv_search_friend)
    ListView lvSearchFriend;

    private List<ContactInfo> contacts;
    private List<MTUser> users;
    private SearchFriendListAdapter adapter;
    private List<MTUser> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.inject(this);

        initData();
        initView();
    }

    private void initData() {
        contacts = AppUtils.getContacts(this);
        httpCheckContact();
    }

    private void initView() {
        etSearchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phon = etSearchFriend.getText().toString().trim();
                if (StringUtils.isEmpty(phon)) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                list.clear();
                for (MTUser user : users) {
                    if (user.getPhone().startsWith(phon)) {
                        list.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new SearchFriendListAdapter(this, list);
        lvSearchFriend.setAdapter(adapter);
        lvSearchFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
    }

    @OnClick(R.id.btn_search_friend_cancel)
    public void onClick() {
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEARCH:
                String phone = etSearchFriend.getText().toString().trim();
                if (!StringUtils.isEmpty(phone)) {
                    httpSearchFriendByKeyword(phone);
                }
        }
        return false;
    }

    private void httpCheckContact() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            JSONArray array = new JSONArray();
            for (ContactInfo contact : contacts) {
                array.put(contact.getNumber());
                Log.d("MT", contact.getNumber());
            }
            json.put("phoneNumbers", array);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getCheckContactUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d("MT", responseInfo.toString());
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result)) {
                            Toast.makeText(SearchFriendActivity.this, "检索失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                users = gson.fromJson(data.toString(), new TypeToken<List<MTUser>>() {
                                }.getType());
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SearchFriendActivity.this, errorMsg);
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

    private void httpSearchFriendByKeyword(String keyword) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            json.put(ApiUtils.KEY_KEYWORD, keyword);
            JSONArray array = new JSONArray();
            for (ContactInfo contact : contacts) {
                array.put(contact.getNumber());
                Log.d("MT", contact.getNumber());
            }
            json.put("phoneNumbers", array);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_SEARCH_FRIEND_BY_KEYWORD,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d("MT", responseInfo.toString());
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result)) {
                            Toast.makeText(SearchFriendActivity.this, "检索失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                list = gson.fromJson(data.toString(), new TypeToken<List<MTUser>>() {
                                }.getType());
                                adapter.notifyDataSetChanged();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(SearchFriendActivity.this, errorMsg);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
