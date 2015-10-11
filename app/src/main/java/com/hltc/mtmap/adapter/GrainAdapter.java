package com.hltc.mtmap.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hltc.mtmap.MTMyGrain;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.DateUtils;
import com.hltc.mtmap.util.StringUtils;

import java.util.List;
public class GrainAdapter extends CommonAdapter<MTMyGrain> {
    public GrainAdapter(Context context, List<MTMyGrain> data, int viewId) {
        super(context, data, viewId);
    }

    @Override
    public void convert(CommonViewHolder holder, MTMyGrain mtMyGrain) {
        holder.setText(R.id.tv_item_my_maitian_comment, mtMyGrain.getText())
                .setText(R.id.tv_item_my_maitian_address, mtMyGrain.getAddress())
                .setText(R.id.tv_item_my_maitian_sitename,mtMyGrain.getSiteName());
        String image = mtMyGrain.getImage();
        if (image == null || StringUtils.isEmpty(image)) {
            holder.getView(R.id.iv_item_my_maitian_image).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.iv_item_my_maitian_image).setVisibility(View.VISIBLE);
            holder.setGrainThumbnail(R.id.iv_item_my_maitian_image, mtMyGrain.getImage());
        }

        TextView time = holder.getView(R.id.tv_item_my_maitian_time);
        String date = DateUtils.getFriendlyTime(mtMyGrain.getCreateTime());
        time.setText(date);
       // time.setTextSize(AMapUtils.dp2px(MyApplication.getContext(), 40 / date.length()));
    }
}
