package com.hltc.mtmap.gmodel;

import java.util.List;

/**
 * Created by redoblue on 15-7-7.
 */
public class GrainDetail {

    public long grainId;
    public String text;
    public String createTime;
    public List<String> images;
    public Publisher publisher;
    public Site site;
    public List<Praise> praise;
    public List<Comment> comment;


    public static class Publisher {
        public long userId;
        public String portrait;
        public String nickName;
        public String remark;
    }

    public static class Site {
        public String siteId;
        public double lat;
        public double lon;
        public String name;
        public String address;
        public String phone;
    }

    public static class Praise {
        public long userId;
        public String nickName;
        public String remark;
    }

    public static class Comment {
        public long cid;
        public long tocid;
        public long userId;
        public String nickName;
        public String remark;
        public String text;
        public String createTime;
    }
}
