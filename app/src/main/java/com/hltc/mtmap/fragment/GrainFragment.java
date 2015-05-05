package com.hltc.mtmap.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hltc.mtmap.R;
import com.hltc.mtmap.adapter.CommonAdapter;
import com.hltc.mtmap.adapter.CommonViewHolder;
import com.hltc.mtmap.bean.SwipeGrainItem;
import com.lorentzos.flingswipe.FlingCardListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class GrainFragment extends Fragment {

	private SwipeFlingAdapterView mSwipeView;
	private List<SwipeGrainItem> mSwipeItems;
	private SwipeViewAdapter mSwipeAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grain, container,false);

		findViewById(view);
		initView();

		return view;
	}

	private void findViewById(View view) {
		mSwipeView = (SwipeFlingAdapterView) view.findViewById(R.id.view_grain_swipe);
	}

	private void initView() {
		mSwipeItems = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			SwipeGrainItem item = new SwipeGrainItem();
			item.setPic(getResources().getDrawable(R.drawable.pic_1));
			item.setAvatar(getResources().getDrawable(R.drawable.cluster_pic));
			item.setComment("我只是一个card而已 " + i);
			mSwipeItems.add(item);
		}
		mSwipeAdapter = new SwipeViewAdapter(getActivity(), mSwipeItems, R.layout.item_grain_card);
		mSwipeView.setAdapter(mSwipeAdapter);
		mSwipeView.setFlingListener(flingListener);
	}

	/********************** Listener *******************/

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

	/********************** Adapter ********************/

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

	/********************** Life Cycle **********************/

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}
}
