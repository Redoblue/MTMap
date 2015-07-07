package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppManager;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> mList = new ArrayList<>();
    private ArrayAdapter mAdapter;

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
        etSearch.setInputType(InputType.TYPE_CLASS_TEXT);
        etSearch.setHint("输入搜索地点");
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                Inputtips tips = new Inputtips(SearchPositionActivity.this, new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> list, int i) {
                        mList.clear();
                        for (Tip t : list) {
                            mList.add(t.getName());
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
                try {
                    tips.requestInputtips(newText, "");
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(SearchPositionActivity.this);
            }
        });

        //init listview
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList);
        lvSearchList.setAdapter(mAdapter);
        lvSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("keyword", mList.get(position));
                setResult(RESULT_OK, intent);
                AppManager.getAppManager().finishActivity(SearchPositionActivity.this);
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (etSearch.getText().toString().isEmpty()) {
                Toast.makeText(SearchPositionActivity.this, "请输入搜索地点", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent intent = new Intent();
            intent.putExtra("keyword", etSearch.getText().toString());
            setResult(RESULT_OK, intent);
            AppManager.getAppManager().finishActivity(SearchPositionActivity.this);
        }
        return true;
    }
}
