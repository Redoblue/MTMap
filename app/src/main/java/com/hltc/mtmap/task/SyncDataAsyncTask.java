package com.hltc.mtmap.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.activity.profile.FriendStatusActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.PhoneContact;
import com.hltc.mtmap.gmodel.ContactItem;
import com.hltc.mtmap.orm.dao.MTFriendStatusDao;
import com.hltc.mtmap.orm.model.MTFriendStatus;
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
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by redoblue on 15-7-2.
 */
public class SyncDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public SyncDataAsyncTask() {
        super();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        httpSyncFriendData();
        httpSyncFriendStatusData();
        httpSyncContactStatusData();
        httpSyncGrainNumber();
        return true;
    }

    /**
     * 好友数据更新
     */
    private void httpSyncFriendData() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID,
                    AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN,
                    AppConfig.getAppConfig().getConfToken());
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
                                List<MTUser> users = gson.fromJson(data.toString(), new TypeToken<List<MTUser>>() {
                                }.getType());

                                if (users != null && users.size() > 0) {
                                    DaoManager.getManager().daoSession.getMTUserDao().deleteAll();
                                    for (MTUser user : users) {
                                        DaoManager.getManager().daoSession.getMTUserDao().insert(user);
                                    }
                                }
                                Log.d("SyncDataAsyncTask", "user list synced");
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
//                                    ToastUtils.showShort(SplashActivity.this, errorMsg);
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

    private void httpSyncFriendStatusData() {

        //处理好友请求
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
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
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<MTFriendStatus> friendStatuses =
                                        gson.fromJson(data.toString(), new TypeToken<List<MTFriendStatus>>() {
                                        }.getType());

                                //保存到数据库
                                if (friendStatuses != null && friendStatuses.size() > 0) {
                                    DaoManager.getManager().daoSession.getMTFriendStatusDao().deleteAll();
                                    for (MTFriendStatus f : friendStatuses) {
                                        DaoManager.getManager().daoSession.getMTFriendStatusDao().insert(f);
                                    }
                                }
                                Log.d("SyncDataAsyncTask", "friend status data synced");
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(MyApplication.getContext(), errorMsg);
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

    private void httpSyncContactStatusData() {
        // 通讯录中可以添加的好友
        final List<PhoneContact> contacts = AppUtils.getContacts(MyApplication.getContext());
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
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

        HttpUtils http1 = new HttpUtils();
        http1.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getCheckContactUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<ContactItem> cis = gson.fromJson(data.toString(), new TypeToken<List<ContactItem>>() {
                                }.getType());

                                if (cis != null && cis.size() > 0) {
                                    QueryBuilder qb = DaoManager.getManager().daoSession.getMTFriendStatusDao().queryBuilder();
                                    for (ContactItem ci : cis) {
                                        qb.where(MTFriendStatusDao.Properties.UserId.eq(ci.getUserId()));
                                        List<MTFriendStatus> tmpfs = qb.list();
                                        if (tmpfs != null && tmpfs.size() < 1) {
                                            for (PhoneContact c : contacts) {
                                                if (ci.getPhone().equals(c.getNumber())) {
                                                    MTFriendStatus fs = new MTFriendStatus();
                                                    fs.setUserPortrait(ci.getPortrait());
                                                    fs.setUserId(ci.getUserId());
                                                    fs.setNickName(ci.getNickName());
                                                    fs.setText("手机联系人： " + c.getDisplayName());
                                                    fs.setStatus(FriendStatusActivity.STATUS_ADDABLE);

                                                    DaoManager.getManager().daoSession.getMTFriendStatusDao().insert(fs);
                                                }
                                            }
                                        }
                                    }
                                }
                                Log.d("SyncDataAsyncTask", "friend status data2 synced");
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
//                                    ToastUtils.showShort(FriendListActivity.this, errorMsg);
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

    private void httpSyncGrainNumber() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_GRAIN_NUMBER,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONObject data = new JSONObject(result).getJSONObject(ApiUtils.KEY_DATA);
                                AppConfig.getAppConfig().set(
                                        AppConfig.CONFIG_GRAIN, AppConfig.CONF_GRAIN_CHIHE,
                                        String.valueOf(data.getInt(ApiUtils.KEY_GRAIN_CHIHE)));
                                AppConfig.getAppConfig().set(
                                        AppConfig.CONFIG_GRAIN, AppConfig.CONF_GRAIN_WANLE,
                                        String.valueOf(data.getInt(ApiUtils.KEY_GRAIN_WANLE)));
                                AppConfig.getAppConfig().set(AppConfig.CONFIG_GRAIN, AppConfig.CONF_GRAIN_OTHER,
                                        String.valueOf(data.getInt(ApiUtils.KEY_GRAIN_OTHER)));

                                Log.d("SyncDataAsyncTask", "grain number synced");
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
//                                    ToastUtils.showShort(MyApplication.getContext(), errorMsg);
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
