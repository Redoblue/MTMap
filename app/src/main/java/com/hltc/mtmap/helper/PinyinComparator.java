package com.hltc.mtmap.helper;


import com.hltc.mtmap.MTUser;

import java.util.Comparator;

public class PinyinComparator implements Comparator<MTUser> {

    //    public int compare(Friend o1, Friend o2) {
//        if (o1.getLetter().equals("@")
//                || o2.getLetter().equals("#")) {
//            return 1;
//        } else if (o1.getLetter().equals("#")
//                || o2.getLetter().equals("@")) {
//            return -1;
//        } else {
//            return o1.getLetter().compareTo(o2.getLetter());
//        }
//    }
    public int compare(MTUser f1, MTUser f2) {
        return f1.getFirstCharacter().compareTo(f2.getFirstCharacter());
    }
}
