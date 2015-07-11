package com.hltc.mtmap.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.MFriend;
import com.hltc.mtmap.MFriendStatus;
import com.hltc.mtmap.MTMyFavourite;
import com.hltc.mtmap.MTMyGrain;
import com.hltc.mtmap.activity.profile.FriendStatusActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.bean.PhoneContact;
import com.hltc.mtmap.gmodel.ContactItem;
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

/**
 * Created by redoblue on 15-7-2.
 */
public class SyncDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public SyncDataAsyncTask() {
        super();
    }

    /**
     * 好友数据更新
     */
    public static void httpSyncFriendData() {
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
                                List<MFriend> users = gson.fromJson(data.toString(), new TypeToken<List<MFriend>>() {
                                }.getType());

                                if (users != null) {
                                    try {
                                        DaoManager.getManager().daoSession.getMFriendDao().deleteAll();
                                        for (MFriend user : users) {
                                            user.setIsFolder(false);
                                            DaoManager.getManager().daoSession.getMFriendDao().insertOrReplace(user);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
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

    public static void httpSyncFriendStatusData() {

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
                                List<MFriendStatus> friendStatuses =
                                        gson.fromJson(data.toString(), new TypeToken<List<MFriendStatus>>() {
                                        }.getType());

                                //保存到数据库
                                if (friendStatuses != null) {
                                    try {
                                        DaoManager.getManager().daoSession.getMFriendStatusDao().deleteAll();
                                        for (MFriendStatus f : friendStatuses) {
                                            DaoManager.getManager().daoSession.getMFriendStatusDao().insertOrReplace(f);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
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

    public static void httpSyncContactStatusData() {
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
                                    for (ContactItem ci : cis) {
                                        MFriendStatus temp = DaoManager.getManager().daoSession.getMFriendStatusDao().load(ci.getUserId());
                                        if (temp == null) {
                                            for (PhoneContact c : contacts) {
                                                if (ci.getPhone().equals(c.getNumber())) {
                                                    MFriendStatus fs = new MFriendStatus();
                                                    fs.setUserPortrait(ci.getPortrait());
                                                    fs.setUserId(ci.getUserId());
                                                    fs.setNickName(ci.getNickName());
                                                    fs.setText(c.getDisplayName());
                                                    fs.setStatus(FriendStatusActivity.STATUS_ADDABLE);

                                                    DaoManager.getManager().daoSession.getMFriendStatusDao().insertOrReplace(fs);
                                                    break;
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

    public static void httpSyncMyGrainData() {
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

        HttpUtils http1 = new HttpUtils();
        http1.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_MY_GRAIN,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<MTMyGrain> mgs = gson.fromJson(data.toString(), new TypeToken<List<MTMyGrain>>() {
                                }.getType());

                                //保存到数据库
                                if (mgs != null) {
                                    try {
                                        DaoManager.getManager().daoSession.getMTMyGrainDao().deleteAll();
                                        for (MTMyGrain f : mgs) {
                                            DaoManager.getManager().daoSession.getMTMyGrainDao().insertOrReplace(f);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
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

    public static void httpSyncMyFavouriteData() {
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

        HttpUtils http1 = new HttpUtils();
        http1.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_MY_FAVOURITE,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Gson gson = new Gson();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                List<MTMyFavourite> mgs = gson.fromJson(data.toString(), new TypeToken<List<MTMyFavourite>>() {
                                }.getType());

                                //保存到数据库
                                if (mgs != null) {
                                    try {
                                        DaoManager.getManager().daoSession.getMTMyFavouriteDao().deleteAll();
                                        for (MTMyFavourite f : mgs) {
                                            DaoManager.getManager().daoSession.getMTMyFavouriteDao().insertOrReplace(f);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
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

    public static void httpSyncGrainNumber() {
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
        httpSyncMyGrainData();
        httpSyncMyFavouriteData();
        return true;
    }
}
