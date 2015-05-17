package com.hltc.mtmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.ImageBucketAdapter;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.ImageBucket;
import com.hltc.mtmap.helper.AlbumHelper;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GalleryActivity extends Activity {

    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap bimap;

    @InjectView(R.id.btn_gallery_back)
    Button backButton;
    @InjectView(R.id.gv_photo_bucket_gallery)
    GridView gridview;

    List<ImageBucket> dataList;//用来装载数据源的列表
    ImageBucketAdapter adapter;// 自定义的适配器
    AlbumHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_photo_bucket);
        ButterKnife.inject(this);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 获取相册信息
        dataList = helper.getImagesBucketList(false);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
    }

    /**
     * 初始化view视图
     */
    private void initView() {
        adapter = new ImageBucketAdapter(GalleryActivity.this, dataList);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(GalleryActivity.this,
                        PhotoActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_LIST,
                        (Serializable) dataList.get(position).imageList);
                startActivity(intent);
//                finish();
            }

        });
    }

    @OnClick(R.id.btn_gallery_back)
    public void goBack() {
        AppManager.getAppManager().finishActivity(this);
    }
}