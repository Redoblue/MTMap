package com.hltc.mtmap.activity.profile.setting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.service.DownloadService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-7-7.
 */
public class CheckUpdateActivity extends Activity {

    @InjectView(R.id.tv_check_update_persent)
    TextView tvCheckUpdatePersent;
    @InjectView(R.id.pb_check_update)
    ProgressBar pbCheckUpdate;
    @InjectView(R.id.btn_check_update_cancel)
    Button btnCheckUpdateCancel;

    private DownloadService.DownloadBinder binder;
    private boolean isBinded = false;
    private boolean isDestroy = true;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            tvCheckUpdatePersent.setText("当前进度 ： " + msg.what + "%");
        }
    };
    private ICallbackResult callback = new ICallbackResult() {

        @Override
        public void OnBackResult(Object result) {
            if ("finish".equals(result)) {
                finish();
                return;
            }
            int i = (Integer) result;
            pbCheckUpdate.setProgress(i);
            mHandler.sendEmptyMessage(i);
        }
    };
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.DownloadBinder) service;
            isBinded = true;
            binder.addCallback(callback);
            binder.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check_update);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_check_update_cancel)
    public void cancelUpdate() {
        binder.cancel();
        binder.cancelNotification();
        finish();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isDestroy && MyApplication.isDownloadingNewVersion) {
            Intent it = new Intent(CheckUpdateActivity.this, DownloadService.class);
            startService(it);
            bindService(it, connection, Context.BIND_AUTO_CREATE);
        }
        System.out.println(" notification  onresume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (isDestroy && MyApplication.isDownloadingNewVersion) {
            Intent it = new Intent(CheckUpdateActivity.this, DownloadService.class);
            startService(it);
            bindService(it, connection, Context.BIND_AUTO_CREATE);
        }
        System.out.println(" notification  onNewIntent");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        isDestroy = false;
        System.out.println(" notification  onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBinded) {
            System.out.println(" onDestroy   unbindservice");
            unbindService(connection);
        }
        if (binder != null && binder.isCanceled()) {
            System.out.println(" onDestroy  stopservice");
            Intent it = new Intent(this, DownloadService.class);
            stopService(it);
        }
    }

    public interface ICallbackResult {
        void OnBackResult(Object result);
    }

}
