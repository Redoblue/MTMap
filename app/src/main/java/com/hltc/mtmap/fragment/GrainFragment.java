package com.hltc.mtmap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.bean.SwipeGrainItem;
import com.hltc.mtmap.util.AMapUtils;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GrainFragment extends Fragment {

    @InjectView(R.id.btn_grain_ignore)
    Button btnGrainIgnore;
    @InjectView(R.id.btn_grain_favourite)
    Button btnGrainFavourite;
    @InjectView(R.id.view_grain_swipe)
    SwipeFlingAdapterView viewGrainSwipe;
    @InjectView(R.id.btn_bar_left)
    Button btnBarLeft;
    @InjectView(R.id.tv_bar_title)
    TextView tvBarTitle;
    @InjectView(R.id.btn_bar_right)
    Button btnBarRight;

    private List<SwipeGrainItem> mSwipeItems;
    private SwipeViewAdapter mSwipeAdapter;
    /**
     * ******************* Listener ******************
     */

    private SwipeFlingAdapterView.onFlingListener flingListener = new
            SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    mSwipeItems.remove(0);
                    mSwipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object o) {

                }

                @Override
                public void onRightCardExit(Object o) {

                }

                @Override
                public void onAdapterAboutToEmpty(int i) {

                }

                @Override
                public void onScroll(float v) {

                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grain, container, false);
        ButterKnife.inject(this, view);

        initView();
        return view;
    }

    private void initView() {
        tvBarTitle.setText("麦圈");
        btnBarLeft.setBackgroundResource(R.drawable.ic_action_yinyang);
        btnBarLeft.setWidth(AMapUtils.dp2px(getActivity(), 25));
        btnBarLeft.setHeight(AMapUtils.dp2px(getActivity(), 25));

        mSwipeItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SwipeGrainItem item = new SwipeGrainItem();
            item.setPic(getResources().getDrawable(R.drawable.pic_1));
            item.setAvatar(getResources().getDrawable(R.drawable.cluster_pic));
            item.setComment("我只是一个card而已 " + i);
            mSwipeItems.add(item);
        }
        mSwipeAdapter = new SwipeViewAdapter(getActivity(), mSwipeItems, R.layout.grain_card_item);
        viewGrainSwipe.setAdapter(mSwipeAdapter);
        viewGrainSwipe.setFlingListener(flingListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * ******************* Life Cycle *********************
     */

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    /**
     * ******************* Adapter *******************
     */

    private class SwipeViewAdapter extends CommonAdapter<SwipeGrainItem> {
        SwipeViewAdapter(Context context, List<SwipeGrainItem> list, int viewId) {
            super(context, list, viewId);
        }

        @Override
        public void convert(CommonViewHolder holder, SwipeGrainItem swipeGrainItem) {
            holder.setImage(R.id.iv_swipe_pic, swipeGrainItem.getPic())
                    .setCircleImage(R.id.civ_swipe_avatar, swipeGrainItem.getAvatar())
                    .setText(R.id.tv_swipe_comment, swipeGrainItem.getComment());
        }
    }
}
