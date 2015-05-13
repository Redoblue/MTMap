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
    @InjectView(R.id.btn_photo_bucket_back)
    Button backButton;
    @InjectView(R.id.gv_photo_bucket_gallery)
    GridView gridview;
    // ArrayList<Entity> dataList;//用来装载数据源的列表
    List<ImageBucket> dataList;
    ImageBucketAdapter adapter;// 自定义的适配器
    AlbumHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
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
        // /**
        // * 这里，我们假设已经从网络或者本地解析好了数据，所以直接在这里模拟了10个实体类，直接装进列表中
        // */
        // dataList = new ArrayList<Entity>();
        // for(int i=-0;i<10;i++){
        // Entity entity = new Entity(R.drawable.picture, false);
        // dataList.add(entity);
        // }
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
                /**
                 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
                 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
                 */
                // if(dataList.get(position).isSelected()){
                // dataList.get(position).setSelected(false);
                // }else{
                // dataList.get(position).setSelected(true);
                // }
                /**
                 * 通知适配器，绑定的数据发生了改变，应当刷新视图
                 */
                // adapter.notifyDataSetChanged();
                Intent intent = new Intent(GalleryActivity.this,
                        PhotoGridActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_LIST,
                        (Serializable) dataList.get(position).imageList);
                startActivity(intent);
//                finish();
            }

        });
    }

    @OnClick(R.id.btn_photo_bucket_back)
    public void goBack() {
        AppManager.getAppManager().finishActivity(this);
    }
}
