package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.ImageGridAdapter;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.ImageItem;
import com.hltc.mtmap.helper.AlbumHelper;
import com.hltc.mtmap.helper.PhotoHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PhotoGridActivity extends Activity {
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    @InjectView(R.id.btn_image_grid_back)
    Button backButton;
    @InjectView(R.id.gv_image_grid_gallery)
    GridView gridview;
    @InjectView(R.id.bt)
    Button bt;

    List<ImageItem> dataList;
    ImageGridAdapter adapter;
    AlbumHelper helper;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(PhotoGridActivity.this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_image_grid);
        ButterKnife.inject(this);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        dataList = (List<ImageItem>) getIntent().getSerializableExtra(
                EXTRA_IMAGE_LIST);

        initView();
    }

    @OnClick(R.id.btn_image_grid_back)
    public void goBack() {
        AppManager.getAppManager().finishActivity(this);
    }

    private void initView() {
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(PhotoGridActivity.this, dataList,
                mHandler);
        gridview.setAdapter(adapter);
        adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
            public void onListen(int count) {
                bt.setText("完成" + "(" + count + ")");
            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
            }

        });

        bt.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<String>();
                Collection<String> c = adapter.map.values();
                Iterator<String> it = c.iterator();
                for (; it.hasNext(); ) {
                    list.add(it.next());
                }

                if (PhotoHelper.act_bool) {
                    Intent intent = new Intent(PhotoGridActivity.this,
                            CreateGrainActivity.class);
                    startActivity(intent);
                    PhotoHelper.act_bool = false;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (PhotoHelper.addresses.size() < 9) {
                        PhotoHelper.addresses.add(list.get(i));
                    }
                }
                finish();
            }

        });
    }
}
