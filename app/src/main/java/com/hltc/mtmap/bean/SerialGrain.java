package com.hltc.mtmap.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redoblue on 15-5-15.
 */
public class SerialGrain implements Parcelable {

    public static final Parcelable.Creator<SerialGrain> CREATOR = new Parcelable.Creator<SerialGrain>() {
        public SerialGrain createFromParcel(Parcel source) {
            return new SerialGrain(source);
        }

        public SerialGrain[] newArray(int size) {
            return new SerialGrain[size];
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
    public boolean isPublic;
    public String text;
    public ArrayList<String> images;

    public SerialGrain() {
    }

    private SerialGrain(Parcel in) {
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
        this.isPublic = in.readByte() != 0;
        this.text = in.readString();
        this.images = (ArrayList<String>) in.readSerializable();
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
        dest.writeByte(isPublic ? (byte) 1 : (byte) 0);
        dest.writeString(this.text);
        dest.writeSerializable(this.images);
    }
}
