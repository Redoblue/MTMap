package com.hltc.mtmap.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.gmodel.ClusterGrain;
import com.hltc.mtmap.util.StringUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ManyGrainInfoFragment extends Fragment {

    @InjectView(R.id.grid)
    GridView grid;

    private MyGridViewAdapter adapter;
    private ArrayList<ClusterGrain> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_many_grain_pager, container, false);
        ButterKnife.inject(this, view);

        initView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void initView() {
        list = getArguments().getParcelableArrayList("gs");
        adapter = new MyGridViewAdapter();
        grid.setAdapter(adapter);
    }

    private class MyGridViewAdapter extends BaseAdapter {

        public MyGridViewAdapter() {

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ClusterGrain getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_many_grain_grid, null);
                holder.portrait = (ImageView) convertView.findViewById(R.id.iv_item_many_grain_info_grid_portrait);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_many_grain_info_grid_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.portrait.setImageDrawable(Drawable.createFromPath(getItem(position).getPicUrl()));
            String name = getItem(position).remark;
            holder.name.setText(StringUtils.isEmpty(name) ? getItem(position).nickName : name);

            return convertView;
        }

        class ViewHolder {
            ImageView portrait;
            TextView name;
        }
    }
}
