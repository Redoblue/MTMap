package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.event.BaseMessageEvent;
import com.hltc.mtmap.gmodel.GrainDetail;
import com.hltc.mtmap.util.WeChatUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by redoblue on 15-6-29.
 */
public class AddFriendActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.et_add_friend_search)
    EditText etSearchFriend;
    @InjectView(R.id.tv_add_friend_invite)
    TextView tvSearchFriendInvite;

    private IWXAPI iwxapi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        registerWX();
    }
    private void registerWX() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo == null) {
            return;
        }
        String appId = appInfo.metaData.getString("com.hltc.mtmap.wx_id");
        iwxapi = WXAPIFactory.createWXAPI(this, appId, true);
        iwxapi.registerApp(appId);
    }
    public void onEvent(BaseMessageEvent event) {
        switch (event.action) {
            case BaseMessageEvent.EVENT_KILL_SELF:
                AppManager.getAppManager().finishActivity(this);
                break;
            default:
                break;
        }
    }

    @OnClick({
            R.id.btn_bar_left,
            R.id.et_add_friend_search,
            R.id.tv_add_friend_invite
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.et_add_friend_search:
                Intent intent = new Intent(AddFriendActivity.this, SearchFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_add_friend_invite:
                WeChatUtils.shareApp2WecharSession(this,iwxapi);
                break;
        }
    }
}
