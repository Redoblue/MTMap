package com.hltc.mtmap.activity.profile.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.hltc.mtmap.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
    }
}
