package com.hltc.mtmap.gmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;
import com.amp.apis.libc.ClusterItem;

/**
 * Created by redoblue on 15-7-5.
 */
public class ClusterGrain implements ClusterItem, Parcelable {

    public static final Parcelable.Creator<ClusterGrain> CREATOR = new Parcelable.Creator<ClusterGrain>() {
        public ClusterGrain createFromParcel(Parcel source) {
            return new ClusterGrain(source);
        }

        public ClusterGrain[] newArray(int size) {
            return new ClusterGrain[size];
        }
    };
    public long grainId;
    public long userId;
    public String cateId;
    public String nickName;
    public String remark;
    public String text;
    public String userPortrait;
    public ClusterSite site;

    public ClusterGrain() {
    }

    protected ClusterGrain(Parcel in) {
        this.grainId = in.readLong();
        this.userId = in.readLong();
        this.cateId = in.readString();
        this.nickName = in.readString();
        this.remark = in.readString();
        this.text = in.readString();
        this.userPortrait = in.readString();
        this.site = in.readParcelable(ClusterSite.class.getClassLoader());
    }

    @Override
    public long getItemId() {
        return grainId;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(site.lat, site.lon);
    }

    @Override
    public String getPicUrl() {
        return userPortrait;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.grainId);
        dest.writeLong(this.userId);
        dest.writeString(this.cateId);
        dest.writeString(this.nickName);
        dest.writeString(this.remark);
        dest.writeString(this.text);
        dest.writeString(this.userPortrait);
        dest.writeParcelable(this.site, flags);
    }

    public static class ClusterSite implements Parcelable {
        public static final Creator<ClusterSite> CREATOR = new Creator<ClusterSite>() {
            public ClusterSite createFromParcel(Parcel source) {
                return new ClusterSite(source);
            }

            public ClusterSite[] newArray(int size) {
                return new ClusterSite[size];
            }
        };
        public String siteId;
        public String source;
        public String address;
        public String name;
        public String phone;
        public String gtype;
        public String mtype;
        public double lat;
        public double lon;

        public ClusterSite() {
        }

        protected ClusterSite(Parcel in) {
            this.siteId = in.readString();
            this.source = in.readString();
            this.address = in.readString();
            this.name = in.readString();
            this.phone = in.readString();
            this.gtype = in.readString();
            this.mtype = in.readString();
            this.lat = in.readDouble();
            this.lon = in.readDouble();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.siteId);
            dest.writeString(this.source);
            dest.writeString(this.address);
            dest.writeString(this.name);
            dest.writeString(this.phone);
            dest.writeString(this.gtype);
            dest.writeString(this.mtype);
            dest.writeDouble(this.lat);
            dest.writeDouble(this.lon);
        }
    }
}
