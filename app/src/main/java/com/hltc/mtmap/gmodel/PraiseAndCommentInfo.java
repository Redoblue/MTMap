package com.hltc.mtmap.gmodel;

/**
 * Created by redoblue on 15-7-11.
 */
public class PraiseAndCommentInfo {

    public String type;
    public User user;
    public Grain grain;
    public String commentTxt;
    public String createTime;

    public static class User {
        public long userId;
        public String portrait;
        public String nickName;
        public String remark;
    }

    public static class Grain {
        public long grainId;
        public String name;
        public String address;
        public String image;
        public String text;
    }
}
