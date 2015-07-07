package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.MFriendStatus;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.task.SyncDataAsyncTask;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-6-29.
 */
public class FriendStatusActivity extends Activity {

    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_UNACCEPTED = "unaccepted";
    public static final String STATUS_ADDABLE = "addable";
    public static final String STATUS_ACCEPTED = "accepted";
    public static List<MFriendStatus> adapterList;
    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.lv_new_friend)
    ListView lvNewFriend;
    private FriendStatusListAdapter adapter;

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

        adapterList = DaoManager.getManager().getAllFriendStarus();
        adapter = new FriendStatusListAdapter(this, adapterList);
        lvNewFriend.setAdapter(adapter);
    }

    @OnClick(R.id.btn_bar_left)
    public void onClick() {
        AppManager.getAppManager().finishActivity(this);
    }

    private void httpAgreeRequest(final int index) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put(ApiUtils.KEY_FROM_ID, adapterList.get(index).getUserId());
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
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                updateStatusInDb(adapterList.get(index), STATUS_ACCEPTED);
                                adapterList.get(index).setStatus(STATUS_ACCEPTED);
                                adapter.notifyDataSetChanged();
                                SyncDataAsyncTask.httpSyncFriendData();
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
        final MFriendStatus fs = adapterList.get(index);
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
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
                        String result = responseInfo.result;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                updateStatusInDb(fs, STATUS_WAITING);
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
    public void onResume() {
        super.onResume();
        try {
            adapterList = DaoManager.getManager().daoSession.getMFriendStatusDao().loadAll();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    //修改数据库中的信息
    private void updateStatusInDb(MFriendStatus fs, String status) {
        MFriendStatus m = new MFriendStatus();
        m.setUserId(fs.getUserId());
        m.setNickName(fs.getNickName());
        m.setText(fs.getText());
        m.setUserPortrait(fs.getUserPortrait());
        m.setStatus(status);
        DaoManager.getManager().daoSession.getMFriendStatusDao().update(m);
    }

    public class FriendStatusListAdapter extends BaseAdapter {

        private List<MFriendStatus> list = null;
        private Context mContext;

        public FriendStatusListAdapter(Context mContext, List<MFriendStatus> list) {
            this.mContext = mContext;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MFriendStatus getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend_status, null);
                holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_item_friend_status_portrait);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_friend_status_name);
                holder.text = (TextView) convertView.findViewById(R.id.tv_item_friend_status_text);
                holder.status = (TextView) convertView.findViewById(R.id.btn_item_friend_status_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(
                    getItem(position).getUserPortrait(), holder.portrait, MyApplication.displayImageOptions);
            holder.name.setText(getItem(position).getNickName());
            holder.text.setText(getItem(position).getText());
            final String s = getItem(position).getStatus();
            if (s.equals(FriendStatusActivity.STATUS_WAITING)) {
                holder.status.setText("等待验证");
                holder.status.setBackgroundResource(R.color.transparent);
            } else if (s.equals(FriendStatusActivity.STATUS_UNACCEPTED)) {
                holder.status.setText("接受");
                holder.status.setBackgroundResource(R.drawable.selector_btn_green_press);
            } else if (s.equals(FriendStatusActivity.STATUS_ADDABLE)) {
                holder.status.setText("添加");
                holder.status.setBackgroundResource(R.drawable.selector_btn_blue_press);
            } else if (s.equals(FriendStatusActivity.STATUS_ACCEPTED)) {
                holder.status.setText("已添加");
                holder.status.setBackgroundResource(R.color.transparent);
            }

            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (s) {
                        case FriendStatusActivity.STATUS_ADDABLE:
                            Intent intent = new Intent(mContext, FriendRequestActivity.class);
                            intent.putExtra("positon", position);
                            intent.putExtra("toId", getItem(position).getUserId());
                            intent.putExtra("remark", getItem(position).getText());
                            mContext.startActivity(intent);
                            break;
                        case FriendStatusActivity.STATUS_UNACCEPTED:
                            httpAgreeRequest(position);
                            break;
                    }
                    SyncDataAsyncTask.httpSyncFriendStatusData();
                    SyncDataAsyncTask.httpSyncContactStatusData();
                    adapterList = DaoManager.getManager().getAllFriendStarus();
                    adapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }

        class ViewHolder {
            CircleImageView portrait;
            TextView name;
            TextView text;
            TextView status;
        }
    }
}
