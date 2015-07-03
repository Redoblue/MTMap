package com.hltc.mtmap.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by redoblue on 15-6-16.
 */
public class SiteItem implements Parcelable {
    public static final Parcelable.Creator<SiteItem> CREATOR = new Parcelable.Creator<SiteItem>() {
        public SiteItem createFromParcel(Parcel source) {
            return new SiteItem(source);
        }

        public SiteItem[] newArray(int size) {
            return new SiteItem[size];
        }
    };
    private String siteId;
    private double lon;
    private double lat;
    private String name;
    private String address;
    private String phone;
    private String mtype;
    private String gtype;

    public SiteItem() {
    }

    protected SiteItem(Parcel in) {
        this.siteId = in.readString();
        this.lon = in.readDouble();
        this.lat = in.readDouble();
        this.name = in.readString();
        this.address = in.readString();
        this.phone = in.readString();
        this.mtype = in.readString();
        this.gtype = in.readString();
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getGtype() {
        return gtype;
    }

    public void setGtype(String gtype) {
        this.gtype = gtype;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.siteId);
        dest.writeDouble(this.lon);
        dest.writeDouble(this.lat);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.phone);
        dest.writeString(this.mtype);
        dest.writeString(this.gtype);
    }
}
