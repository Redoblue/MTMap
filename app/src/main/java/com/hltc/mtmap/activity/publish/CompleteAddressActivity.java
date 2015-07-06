package com.hltc.mtmap.activity.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-5-15.
 */
public class CompleteAddressActivity extends Activity {

    @InjectView(R.id.et_complete_text)
    EditText editText;
    @InjectView(R.id.lv_complete_text_poi)
    ListView poiListView;
    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;

    private List<String> poiTitles = new ArrayList<>();
    private List<String> poisToDisplay = new ArrayList<>();
    private PoiListAdapter adapter;
    private String oldString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_list_search);
        ButterKnife.inject(this);

        initData();
        initView();
    }

    private void initData() {
        poiTitles = (ArrayList<String>) getIntent().getSerializableExtra("TITLE_LIST");
        oldString = getIntent().getStringExtra("OLD_CONTENT");
        refreshPoisToDisplay(oldString);
    }

    private void initView() {
        tvBarTitle.setText("添加地址");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_arrow_left);
        btnBarRight.setBackgroundResource(R.drawable.ic_action_done);
        btnBarLeft.setWidth(AMapUtils.dp2px(this, 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(this, 25));
        btnBarRight.setWidth(AMapUtils.dp2px(this, 25));
        btnBarRight.setHeight(AMapUtils.dp2px(this, 25));

        editText.setText(oldString);
        editText.setSelection(oldString.length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = editText.getText().toString().trim();
                refreshPoisToDisplay(key);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("SELECTED_POI", editText.getText().toString().trim());
                        setResult(RESULT_OK, returnIntent);
                        AppManager.getAppManager().finishActivity(CompleteAddressActivity.this);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) {
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        adapter = new PoiListAdapter(this, poisToDisplay, R.layout.item_complete_text_poi);
        poiListView.setAdapter(adapter);
        poiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = poisToDisplay.get(position);
                editText.setText(title);
                editText.setSelection(title.length());
                poisToDisplay.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({
            R.id.btn_bar_left,
            R.id.btn_bar_right
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.btn_bar_right:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("SELECTED_POI", editText.getText().toString().trim());
                setResult(RESULT_OK, returnIntent);
                AppManager.getAppManager().finishActivity(CompleteAddressActivity.this);
                break;
        }
    }

    private void refreshPoisToDisplay(String s) {
        poisToDisplay.clear();
        if (StringUtils.isEmpty(s)) {
            for (String string : poiTitles) {
                poisToDisplay.add(string);
            }
        } else {
            for (String string : poiTitles) {
                if (string.contains(s) && !string.equals(s)) {
                    poisToDisplay.add(string);
                }
            }
        }
    }

    private class PoiListAdapter extends CommonAdapter<String> {
        public PoiListAdapter(Context context, List<String> data, int viewId) {
            super(context, data, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, String s) {
            holder.setText(R.id.tv_create_grain_poilist, s);
        }
    }
}
