package com.hltc.mtmap.gmodel;

/**
 * Created by redoblue on 15-7-8.
 */
public class SwipeGrain {

    public long grainId;
    public String text;
    public String image;
    public long userId;
    public String portraitSmall;
    public Site site;

    public static class Site {
        public String siteId;
        public double lon;
        public double lat;
        public String name;
        public String address;
        public String phone;
        public String mtype;
        public String gtype;
    }
}
