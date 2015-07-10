package com.hltc.mtmap.activity.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hltc.mtmap.MTMyGrain;
import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.DateUtils;
import com.hltc.mtmap.util.StringUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-7-7.
 */
public class MyGrainActivity extends Activity {

    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.listView)
    ListView listView;

    private List<MTMyGrain> mList;
    private MyGrainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_maitian);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        btnBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(MyGrainActivity.this);
            }
        });

        mList = DaoManager.getManager().getAllMyGrains();
        mAdapter = new MyGrainAdapter(this, mList, R.layout.item_my_maitian);
        listView.setAdapter(mAdapter);
    }

    class MyGrainAdapter extends CommonAdapter<MTMyGrain> {

        public MyGrainAdapter(Context context, List<MTMyGrain> data, int viewId) {
            super(context, data, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, MTMyGrain mtMyGrain) {
            holder.setText(R.id.tv_item_my_maitian_comment, mtMyGrain.getText())
                    .setText(R.id.tv_item_my_maitian_address, mtMyGrain.getAddress());
            String image = mtMyGrain.getImage();
            if (image == null || StringUtils.isEmpty(image)) {
                holder.getView(R.id.iv_item_my_maitian_image).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.iv_item_my_maitian_image).setVisibility(View.VISIBLE);
                holder.setGrainThumbnail(R.id.iv_item_my_maitian_image, mtMyGrain.getImage());
            }

//            .setText(R.id.tv_item_my_maitian_time, DateUtils.getFriendlyTime(mtMyGrain.getCreateTime()))
            TextView time = holder.getView(R.id.tv_item_my_maitian_time);
            String date = DateUtils.getFriendlyTime(mtMyGrain.getCreateTime());
            time.setText(date);
            time.setTextSize(AMapUtils.dp2px(MyApplication.getContext(), 40 / date.length()));
        }
    }
}
