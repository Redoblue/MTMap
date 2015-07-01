package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.orm.model.MTUser;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.StringUtils;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-6-30.
 */
public class FriendRequestActivity extends Activity implements EditText.OnEditorActionListener {

    @InjectView(R.id.et_edit)
    EditText etEdit;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;

    private MTUser user = new MTUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_edit);
        ButterKnife.inject(this);

        initData();
        initView();
    }

    private void initData() {
        user.setUserId(getIntent().getLongExtra("toId", 0));
        user.setNickName(getIntent().getStringExtra("remark").trim());
    }

    private void initView() {
        etEdit.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String s = etEdit.getText().toString().trim();
            httpAddFriend(s);
        }
        return true;
    }

    @OnClick(R.id.btn_cancel)
    public void onClick() {
        AppManager.getAppManager().finishActivity(this);
    }

    public void httpAddFriend(String s) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig(MyApplication.getContext()).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(MyApplication.getContext()).getConfToken());
            json.put(ApiUtils.KEY_TOID, user.getUserId());
            json.put(ApiUtils.KEY_TEXT, s);
            json.put(ApiUtils.KEY_REMARK, user.getNickName());
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
                        if (StringUtils.isEmpty(result)) {
                            Toast.makeText(MyApplication.getContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                Toast.makeText(MyApplication.getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                AppManager.getAppManager().finishActivity(FriendRequestActivity.class);
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
}