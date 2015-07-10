package com.hltc.mtmap.gmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redoblue on 15-7-7.
 */
public class GrainDetail implements Parcelable {

    public static final Parcelable.Creator<GrainDetail> CREATOR = new Parcelable.Creator<GrainDetail>() {
        public GrainDetail createFromParcel(Parcel source) {
            return new GrainDetail(source);
        }

        public GrainDetail[] newArray(int size) {
            return new GrainDetail[size];
        }
    };
    public long grainId;
    public String text;
    public String createTime;
    public List<String> images;
    public Publisher publisher;
    public Site site;
    public List<Praise> praise;
    public List<Comment> comment;

    public GrainDetail() {
    }

    protected GrainDetail(Parcel in) {
        this.grainId = in.readLong();
        this.text = in.readString();
        this.createTime = in.readString();
        this.images = in.createStringArrayList();
        this.publisher = in.readParcelable(Publisher.class.getClassLoader());
        this.site = in.readParcelable(Site.class.getClassLoader());
        this.praise = new ArrayList<Praise>();
        in.readList(this.praise, Praise.class.getClassLoader());
        this.comment = new ArrayList<Comment>();
        in.readList(this.comment, Comment.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.grainId);
        dest.writeString(this.text);
        dest.writeString(this.createTime);
        dest.writeStringList(this.images);
        dest.writeParcelable(this.publisher, flags);
        dest.writeParcelable(this.site, flags);
        dest.writeList(this.praise);
        dest.writeList(this.comment);
    }

    public static class Publisher implements Parcelable {
        public static final Creator<Publisher> CREATOR = new Creator<Publisher>() {
            public Publisher createFromParcel(Parcel source) {
                return new Publisher(source);
            }

            public Publisher[] newArray(int size) {
                return new Publisher[size];
            }
        };
        public long userId;
        public String portrait;
        public String nickName;
        public String remark;

        public Publisher() {
        }

        protected Publisher(Parcel in) {
            this.userId = in.readLong();
            this.portrait = in.readString();
            this.nickName = in.readString();
            this.remark = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.userId);
            dest.writeString(this.portrait);
            dest.writeString(this.nickName);
            dest.writeString(this.remark);
        }
    }

    public static class Site implements Parcelable {
        public static final Creator<Site> CREATOR = new Creator<Site>() {
            public Site createFromParcel(Parcel source) {
                return new Site(source);
            }

            public Site[] newArray(int size) {
                return new Site[size];
            }
        };
        public String siteId;
        public double lat;
        public double lon;
        public String name;
        public String address;
        public String phone;

        public Site() {
        }

        protected Site(Parcel in) {
            this.siteId = in.readString();
            this.lat = in.readDouble();
            this.lon = in.readDouble();
            this.name = in.readString();
            this.address = in.readString();
            this.phone = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.siteId);
            dest.writeDouble(this.lat);
            dest.writeDouble(this.lon);
            dest.writeString(this.name);
            dest.writeString(this.address);
            dest.writeString(this.phone);
        }
    }

    public static class Praise implements Parcelable {
        public static final Creator<Praise> CREATOR = new Creator<Praise>() {
            public Praise createFromParcel(Parcel source) {
                return new Praise(source);
            }

            public Praise[] newArray(int size) {
                return new Praise[size];
            }
        };
        public long userId;
        public String nickName;
        public String remark;

        public Praise() {
        }

        protected Praise(Parcel in) {
            this.userId = in.readLong();
            this.nickName = in.readString();
            this.remark = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.userId);
            dest.writeString(this.nickName);
            dest.writeString(this.remark);
        }
    }

    public static class Comment implements Parcelable {
        public static final Creator<Comment> CREATOR = new Creator<Comment>() {
            public Comment createFromParcel(Parcel source) {
                return new Comment(source);
            }

            public Comment[] newArray(int size) {
                return new Comment[size];
            }
        };
        public long cid;
        public long tocid;
        public long userId;
        public String portrait;
        public String nickName;
        public String remark;
        public String text;
        public String createTime;

        public Comment() {
        }

        protected Comment(Parcel in) {
            this.cid = in.readLong();
            this.tocid = in.readLong();
            this.userId = in.readLong();
            this.portrait = in.readString();
            this.nickName = in.readString();
            this.remark = in.readString();
            this.text = in.readString();
            this.createTime = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.cid);
            dest.writeLong(this.tocid);
            dest.writeLong(this.userId);
            dest.writeString(this.portrait);
            dest.writeString(this.nickName);
            dest.writeString(this.remark);
            dest.writeString(this.text);
            dest.writeString(this.createTime);
        }
    }
}
