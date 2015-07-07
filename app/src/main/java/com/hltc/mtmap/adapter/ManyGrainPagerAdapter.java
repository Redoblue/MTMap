package com.hltc.mtmap.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hltc.mtmap.fragment.ManyGrainInfoFragment;
import com.hltc.mtmap.gmodel.ClusterGrain;

import java.util.ArrayList;
import java.util.List;

public class ManyGrainPagerAdapter extends FragmentStatePagerAdapter {

    private final int NUM_PER_PAGE = 9;

    private Context context;
    private List<ClusterGrain> list;
    private int size; //number of fragment in viewpager

    public ManyGrainPagerAdapter(FragmentManager fm, Context context, List<ClusterGrain> objects) {
        super(fm);
        this.context = context;
        this.list = objects;
        this.size = objects.size() / NUM_PER_PAGE + 1;
    }

    @Override
    public Fragment getItem(int pos) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("gs", getListByPos(pos));
        ManyGrainInfoFragment fragment = (ManyGrainInfoFragment) Fragment.instantiate(
                context, ManyGrainInfoFragment.class.getName(), args);
        return fragment;
    }

    @Override
    public int getCount() {
        return size;
    }

    private ArrayList<ClusterGrain> getListByPos(int position) {
        ArrayList<ClusterGrain> tmpList = new ArrayList<>(9);
        for (int i = 0; i < this.list.size(); i++) {
            if (i > position * NUM_PER_PAGE - 1) {
                tmpList.add(this.list.get(i));
                if (tmpList.size() >= 9) {
                    break;
                }
            }
        }
        return tmpList;
    }

}
