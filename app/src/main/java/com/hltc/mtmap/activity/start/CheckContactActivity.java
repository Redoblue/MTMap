package com.hltc.mtmap.activity.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.ContactInfo;
import com.hltc.mtmap.bean.ContactItem;
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

    private ProgressGenerator mGenerator;
    private List<ContactItem> mContactItems;
    private MyAdapter mAdapter;

    private List<ContactInfo> contacts;

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
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarRight.setText("下一步");
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));
        btnBarRight.setHeight(AMapUtils.dp2px(this, 25));
        btnBarLeft.setVisibility(View.INVISIBLE);
        btnBarRight.setVisibility(View.INVISIBLE);

        btnCheck.setMode(ActionProcessButton.Mode.ENDLESS);

        mContactItems = new ArrayList<>();
        mAdapter = new MyAdapter(this, mContactItems, R.layout.item_check_contact);
        lvContact.setAdapter(mAdapter);
        lvContact.setVisibility(View.INVISIBLE);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mContactItems.get(position).setIsSelected(true);
                mAdapter.notifyDataSetChanged();
            }
        });
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
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                //TODO
                break;
            case R.id.apb_check_contact:
                mGenerator.start(btnCheck);
                fillData();
                mGenerator.stop();
                break;
            case R.id.tv_check_contact_skip:
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

    private void fillData() {
        contacts = AppUtils.getContacts(this);
        httpCheckContact();
        for (ContactItem contactItem : mContactItems) {
            for (ContactInfo contactInfo : contacts) {
                if (contactItem.getPhone().equals(contactInfo.getNumber())) {
                    contactItem.setName(contactInfo.getDisplayName());
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void httpCheckContact() {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USR_ID, AppConfig.getAppConfig(this).getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig(this).getToken());
            JSONArray array = new JSONArray();
            for (ContactInfo contact : contacts) {
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
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            JSONObject farther = new JSONObject(result);
                            if (farther.getBoolean(ApiUtils.KEY_SUCCESS)) {
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);
                                    ContactItem contactItem = new ContactItem();
                                    contactItem.setPhone(item.getString(ApiUtils.KEY_PHONE));
                                    contactItem.setNickName(item.getString(ApiUtils.KEY_USR_NICKNAME));
                                    contactItem.setPortraitSmall(item.getString(ApiUtils.KEY_USR_PORTRAIT_SMALL));
                                    contactItem.setIsSelected(false);
                                    mContactItems.add(contactItem);
                                }
                            } else {
                                String errorMsg = farther.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 登录失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(CheckContactActivity.this, errorMsg);
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

    private class MyAdapter extends CommonAdapter<ContactItem> {

        public MyAdapter(Context context, List<ContactItem> data, int viewId) {
            super(context, data, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, ContactItem contactItem) {
            holder.setCircleImage(R.id.civ_item_check_contact_portrait, contactItem.getPortraitSmall())
                    .setText(R.id.tv_item_check_contact_name, contactItem.getName())
                    .setText(R.id.tv_check_contact_nickname, contactItem.getNickName())
                    .setToggleButton(R.id.tb_item_check_contact_isselected, contactItem.isSelected());
        }
    }
}
