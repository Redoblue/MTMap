package com.hltc.mtmap.helper;

import com.hltc.mtmap.bean.SortModel;

import java.util.Comparator;

public class PinyinComparator implements Comparator<SortModel> {

    public int compare(SortModel o1, SortModel o2) {
        if (o1.getLetter().equals("@")
                || o2.getLetter().equals("#")) {
            return 1;
        } else if (o1.getLetter().equals("#")
                || o2.getLetter().equals("@")) {
            return -1;
        } else {
            return o1.getLetter().compareTo(o2.getLetter());
        }
    }
}
