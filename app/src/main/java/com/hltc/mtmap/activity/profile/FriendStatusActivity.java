package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.FriendStatusListAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.PhoneContact;
import com.hltc.mtmap.bean.ContactItem;
import com.hltc.mtmap.gmodel.FriendStatus;
import com.hltc.mtmap.util.AMapUtils;
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
 * Created by redoblue on 15-6-29.
 */
public class FriendStatusActivity extends Activity {

    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_UNACCEPTED = "unaccepted";
    public static final String STATUS_ADDABLE = "addable";
    public static final String STATUS_ACCEPTED = "accepted";

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.lv_new_friend)
    ListView lvNewFriend;

    private List<FriendStatus> adapterList;
    private FriendStatusListAdapter adapter;
    private List<PhoneContact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        initView();
    }

    private void initView() {
        tvBarTitle.setText("新的朋友");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));

        lvNewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String status = adapterList.get(position).getStatus();
                if (status.equals(STATUS_ADDABLE)) {
                    httpAddFriend(position);
                } else if (status.equals(STATUS_UNACCEPTED)) {
                    httpAgreeRequest(position);
                }
            }
        });

        adapterList = new ArrayList<>();
        adapter = new FriendStatusListAdapter(this, adapterList);
        lvNewFriend.setAdapter(adapter);
        httpFetchFriendStatusList();
        contacts = AppUtils.getContacts(this);
        httpCheckContact();
    }

    @OnClick(R.id.btn_bar_left)
    public void onClick() {
        AppManager.getAppManager().finishActivity(this);
    }

    private void httpFetchFriendStatusList() {
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
                ApiUtils.URL_ROOT + ApiUtils.URL_FRIEND_GET_STATUS,
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
                                List<FriendStatus> friendStatuses = gson.fromJson(data.toString(), new TypeToken<List<FriendStatus>>() {
                                }.getType());

                                adapterList.addAll(friendStatuses);
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(FriendStatusActivity.this, errorMsg);
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

    private void httpCheckContact() {
        contacts = AppUtils.getContacts(this);
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            JSONArray array = new JSONArray();
            for (PhoneContact contact : contacts) {
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
                            Toast.makeText(FriendStatusActivity.this, "检索失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<ContactItem> cis = gson.fromJson(data.toString(), new TypeToken<List<ContactItem>>() {
                                }.getType());

                                for (ContactItem ci : cis) {
                                    for (PhoneContact c : contacts) {
                                        if (ci.getPhone().equals(c.getNumber())) {
                                            ci.setName(c.getDisplayName());

                                            FriendStatus fs = new FriendStatus();
                                            fs.setUserPortrait(ci.getPortrait());
                                            fs.setUserId(ci.getUserId());
                                            fs.setNickName(ci.getNickName());
                                            fs.setText(ci.getName());
                                            fs.setStatus(STATUS_ADDABLE);

                                            adapterList.add(fs);
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(FriendStatusActivity.this, errorMsg);
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

    private void httpAgreeRequest(final int index) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getConfToken());
            json.put(ApiUtils.KEY_FROM_ID, adapterList.get(index).getUserId());
            JSONArray array = new JSONArray();
            for (PhoneContact contact : contacts) {
                array.put(contact.getNumber());
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
                ApiUtils.URL_ROOT + ApiUtils.URL_FRIEND_AGREE,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d("MT", responseInfo.toString());
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result)) {
                            Toast.makeText(FriendStatusActivity.this, "检索失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                adapterList.get(index).setStatus(STATUS_ACCEPTED);
                                adapter.notifyDataSetChanged();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(FriendStatusActivity.this, errorMsg);
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

    private void httpAddFriend(final int index) {
        FriendStatus fs = adapterList.get(index);
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(MyApplication.getContext()).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(MyApplication.getContext()).getConfToken());
            json.put(ApiUtils.KEY_TOID, fs.getUserId());
            json.put(ApiUtils.KEY_TEXT, "服不服");///////TODO 弹出窗口等待输入
            json.put(ApiUtils.KEY_REMARK, fs.getText());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getFriendAddFriendUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d("MT", responseInfo.toString());
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result)) {
                            Toast.makeText(MyApplication.getContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                adapterList.get(index).setStatus(STATUS_WAITING);
                                adapter.notifyDataSetChanged();
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    Toast.makeText(MyApplication.getContext(), errorMsg, Toast.LENGTH_SHORT).show();
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
