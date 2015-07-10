package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);
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
                //TODO
                break;
        }
    }
}
