package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.adapter.CheckContactListAdapter;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.PhoneContact;
import com.hltc.mtmap.gmodel.ContactItem;
import com.hltc.mtmap.helper.ProgressGenerator;
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
 * Created by redoblue on 15-6-22.
 */
public class CheckContactActivity extends Activity implements ProgressGenerator.OnCompleteListener {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;
    @InjectView(R.id.lv_check_contact)
    ListView lvContact;
    @InjectView(R.id.apb_check_contact)
    ActionProcessButton btnCheck;
    @InjectView(R.id.tv_check_contact_skip)
    TextView tvSkip;
    @InjectView(R.id.layout_check_contact_top)
    RelativeLayout layoutCheckContactTop;

    private ProgressGenerator mGenerator;
    private List<ContactItem> mContactItems;
    private CheckContactListAdapter mAdapter;

    private List<PhoneContact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check_contact);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        initView();
        mGenerator = new ProgressGenerator(this);
    }

    private void initView() {
        tvBarTitle.setText("麦田");
        btnBarRight.setText("下一步");
        btnBarRight.setHeight(AMapUtils.dp2px(this, 25));
        btnBarRight.setVisibility(View.INVISIBLE);

        btnCheck.setMode(ActionProcessButton.Mode.ENDLESS);

        mContactItems = new ArrayList<>();
        mAdapter = new CheckContactListAdapter(this, mContactItems);
        lvContact.setAdapter(mAdapter);
    }

    @OnClick({
            R.id.btn_bar_left,
            R.id.btn_bar_right,
            R.id.apb_check_contact,
            R.id.tv_check_contact_skip
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                layoutCheckContactTop.setVisibility(View.VISIBLE);
                break;
            case R.id.apb_check_contact:
                httpCheckContact();
                break;
            case R.id.tv_check_contact_skip:
            case R.id.btn_bar_right:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                AppManager.getAppManager().finishActivity(this);
                break;
        }
    }

    @Override
    public void onComplete() {
        tvBarTitle.setText("从通讯录添加好友");
        btnCheck.setVisibility(View.VISIBLE);
        btnBarLeft.setVisibility(View.VISIBLE);
        btnBarRight.setVisibility(View.VISIBLE);
    }

    private void httpCheckContact() {
        mGenerator.start(btnCheck);
        contacts = AppUtils.getContacts(this);
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
                            Toast.makeText(CheckContactActivity.this, "检索失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);
                                    ContactItem contactItem = new ContactItem();
                                    contactItem.setUserId(item.getLong(ApiUtils.KEY_USER_ID));
                                    contactItem.setPhone(item.getString(ApiUtils.KEY_USR_PHONE));
                                    contactItem.setNickName(item.getString(ApiUtils.KEY_USR_NICKNAME));
                                    contactItem.setPortrait(item.getString(ApiUtils.KEY_USR_PORTRAIT_SMALL));
                                    contactItem.setIsSelected(false);
                                    mContactItems.add(contactItem);
                                }

                                for (ContactItem citem : mContactItems) {
                                    Log.d("MT", "contact: " + citem.toString());
                                    for (PhoneContact cinfo : contacts) {
                                        String name = cinfo.getDisplayName();
                                        if (citem.getPhone().equals(cinfo.getNumber())) {
                                            citem.setName(name);
                                        }
                                    }
                                }
                                mGenerator.stop();
                                mAdapter.notifyDataSetChanged();
                                layoutCheckContactTop.setVisibility(View.INVISIBLE);
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(CheckContactActivity.this, errorMsg);
                                    mGenerator.stop();
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
