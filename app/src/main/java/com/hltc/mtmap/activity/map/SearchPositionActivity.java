package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-7-5.
 */
public class SearchPositionActivity extends Activity implements TextView.OnEditorActionListener {

    @InjectView(R.id.et_search)
    EditText etSearch;
    @InjectView(R.id.btn_search_cancel)
    Button btnSearchCancel;
    @InjectView(R.id.lv_search_list)
    ListView lvSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_list);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        etSearch.setOnEditorActionListener(this);
        btnSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(SearchPositionActivity.this);
            }
        });

        //init listview
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //TODO 搜索地点
        }
        return true;
    }
}
