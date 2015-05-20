package com.hltc.mtmap.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by redoblue on 15-5-15.
 */
public class ParcelableGrain implements Parcelable {

    public static final Creator<ParcelableGrain> CREATOR = new Creator<ParcelableGrain>() {
        public ParcelableGrain createFromParcel(Parcel source) {
            return new ParcelableGrain(source);
        }

        public ParcelableGrain[] newArray(int size) {
            return new ParcelableGrain[size];
        }
    };
    public String userId;
    public String token;
    public String mcateId;
    public String siteSource;
    public String siteId;
    public String siteName;
    public String siteAddress;
    public String sitePhone;
    public String siteType;
    public String longitude;
    public String latitude;
    public String cityCode;
    public String isPublic;
    public String text;

    public ParcelableGrain() {
    }

    private ParcelableGrain(Parcel in) {
        this.userId = in.readString();
        this.token = in.readString();
        this.mcateId = in.readString();
        this.siteSource = in.readString();
        this.siteId = in.readString();
        this.siteName = in.readString();
        this.siteAddress = in.readString();
        this.sitePhone = in.readString();
        this.siteType = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.cityCode = in.readString();
        this.isPublic = in.readString();
        this.text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.token);
        dest.writeString(this.mcateId);
        dest.writeString(this.siteSource);
        dest.writeString(this.siteId);
        dest.writeString(this.siteName);
        dest.writeString(this.siteAddress);
        dest.writeString(this.sitePhone);
        dest.writeString(this.siteType);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.cityCode);
        dest.writeString(this.isPublic);
        dest.writeString(this.text);
    }
}
