package com.hltc.mtmap.activity.publish;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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

public class PhotoActivity extends Activity {
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    @InjectView(R.id.btn_photo_back)
    Button backButton;
    @InjectView(R.id.gv_photo_view)
    GridView gridview;
    @InjectView(R.id.btn_done)
    Button doneButton;

    private List<ImageItem> dataList;
    private ImageGridAdapter adapter;
    private AlbumHelper helper;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(PhotoActivity.this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo);
        ButterKnife.inject(this);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        dataList = (List<ImageItem>) getIntent().getSerializableExtra(
                EXTRA_IMAGE_LIST);

        initView();
    }

    @OnClick(R.id.btn_photo_back)
    public void goBack() {
        AppManager.getAppManager().finishActivity(this);
    }

    private void initView() {
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(PhotoActivity.this, dataList,
                mHandler);
        gridview.setAdapter(adapter);
        adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
            public void onListen(int count) {
                doneButton.setText("完成" + "(" + count + ")");
            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
            }

        });

        doneButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                Collection<String> collection = adapter.map.values();
                Iterator<String> it = collection.iterator();
                for (; it.hasNext(); ) {
                    list.add(it.next());
                }
                for (int i = 0; i < list.size(); i++) {
                    if (PhotoHelper.addresses.size() < 9) {
                        PhotoHelper.addresses.add(list.get(i));
                    }
                }

                if (list.size() > 0) {
                    Log.d("Publish", "finishGallery");
                    AppManager.getAppManager().finishActivity(PhotoActivity.this);
                    AppManager.getAppManager().finishActivity(GalleryActivity.class);//TODO 检查CreateGrainActivity.class是否存在
                } else {
                    AppManager.getAppManager().finishActivity(PhotoActivity.this);
                }
            }

        });
    }
}
