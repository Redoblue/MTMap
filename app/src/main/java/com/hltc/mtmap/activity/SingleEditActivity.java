package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.profile.SettingsActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.event.CommentEvent;
import com.hltc.mtmap.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by redoblue on 15-7-3.
 */
public class SingleEditActivity extends Activity implements TextView.OnEditorActionListener {

    @InjectView(R.id.et_edit)
    EditText etEdit;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.btn_bar_left)
    Button btnBack;
    @InjectView(R.id.btn_bar_right)
    Button btnOk;

    public   static  Source source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_edit);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        String oldString = getIntent().getStringExtra("old");
        if (!StringUtils.isEmpty(oldString)) {
            etEdit.setText(oldString);
            etEdit.setSelection(oldString.length());
        }

        if(source==Source.ModifyNickName){
            ((TextView)findViewById(R.id.tv_bar_title)).setText("修改昵称");
        }else if(source==Source.Comment){
            ((TextView)findViewById(R.id.tv_bar_title)).setText("评论");
        }
        etEdit.setOnEditorActionListener(this);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(SingleEditActivity.this);
            }
        });
    }

    public static void startFromSettingActivity(Activity activity){
        Intent intent = new Intent(activity, SingleEditActivity.class);
        intent.putExtra("old", AppConfig.getAppConfig().getConfUsrNickName());
        activity.startActivityForResult(intent, SettingsActivity.UPDATE_NICKNAME_REQUEST_CODE);
        SingleEditActivity.source = Source.ModifyNickName;
    }
    public static void startFromGrainDetailActivtiy(Activity activity){
        Intent intent = new Intent(activity, SingleEditActivity.class);
        activity.startActivity(intent);
        SingleEditActivity.source=Source.Comment;
    }
    @OnClick({
            R.id.btn_bar_right,
            R.id.btn_bar_left
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                doAction();
                break;
            default:
                break;
        }
    }

    private void doAction() {
        String newString = etEdit.getText().toString();
        if (StringUtils.isEmpty(newString)) {
            Toast.makeText(this, "写点儿什么吧", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("new", newString);
            setResult(RESULT_OK, intent);

            CommentEvent ce = new CommentEvent();
            ce.setComment(newString);
            EventBus.getDefault().post(ce);
            AppManager.getAppManager().finishActivity(this);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doAction();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
    static enum  Source{
        ModifyNickName,
        Comment;
    }
}
