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
    public int siteSource;
    public String siteId;
    public String siteName;
    public String siteAddress;
    public String sitePhone;
    public String siteType;
    public double latitude;
    public double longitude;
    public String cityCode;
    public int isPublic;
    public String text;

    public ParcelableGrain() {
    }

    private ParcelableGrain(Parcel in) {
        this.userId = in.readString();
        this.token = in.readString();
        this.mcateId = in.readString();
        this.siteSource = in.readInt();
        this.siteId = in.readString();
        this.siteName = in.readString();
        this.siteAddress = in.readString();
        this.sitePhone = in.readString();
        this.siteType = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.cityCode = in.readString();
        this.isPublic = in.readInt();
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
        dest.writeInt(this.siteSource);
        dest.writeString(this.siteId);
        dest.writeString(this.siteName);
        dest.writeString(this.siteAddress);
        dest.writeString(this.sitePhone);
        dest.writeString(this.siteType);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.cityCode);
        dest.writeInt(this.isPublic);
        dest.writeString(this.text);
    }
}
