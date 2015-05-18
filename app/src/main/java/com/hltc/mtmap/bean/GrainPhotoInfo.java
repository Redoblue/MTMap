package com.hltc.mtmap.bean;

import java.util.HashMap;

/**
 * Created by redoblue on 15-5-18.
 */
public class GrainPhotoInfo {

    public static int PHOTO_LARGE = 1;
    public static int PHOTO_SMALL = 2;

    private HashMap<Integer, String> large = new HashMap<>();
    private HashMap<Integer, String> small = new HashMap<>();

    public GrainPhotoInfo(String path1, String path2) {
        large.put(PHOTO_LARGE, path1);
        small.put(PHOTO_SMALL, path2);
    }

    public GrainPhotoInfo() {
    }

    public String getLarge() {
        return large.get(PHOTO_LARGE);
    }

    public void setLarge(String path) {
        large.put(PHOTO_LARGE, path);
    }

    public String getSmall() {
        return small.get(PHOTO_SMALL);
    }

    public void setSmall(String path) {
        small.put(PHOTO_SMALL, path);
    }
}
