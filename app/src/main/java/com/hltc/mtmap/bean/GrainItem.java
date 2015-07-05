package com.hltc.mtmap.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;
import com.amp.apis.libc.ClusterItem;

/**
 * Created by merlin on 5/4/15.
 */
public class GrainItem implements ClusterItem, Parcelable {

    public static final Parcelable.Creator<GrainItem> CREATOR = new Parcelable.Creator<GrainItem>() {
        public GrainItem createFromParcel(Parcel source) {
            return new GrainItem(source);
        }

        public GrainItem[] newArray(int size) {
            return new GrainItem[size];
        }
    };
    private long grainId;
    private String text;
    private String nickName;
    private String remark;
    private String image;
    private long userId;
    private String portrait;
    private String cateId;
    private SiteItem site;
    private int status;
    private int cover;

    public GrainItem() {
    }

    public GrainItem(long grainId, String text, String nickName, String remark, String image,
                     long userId, String portrait, String cateId, SiteItem site, int status, int cover) {
        this.grainId = grainId;
        this.text = text;
        this.nickName = nickName;
        this.remark = remark;
        this.image = image;
        this.userId = userId;
        this.portrait = portrait;
        this.cateId = cateId;
        this.site = site;
        this.status = status;
        this.cover = cover;
    }

    protected GrainItem(Parcel in) {
        this.grainId = in.readLong();
        this.text = in.readString();
        this.nickName = in.readString();
        this.remark = in.readString();
        this.image = in.readString();
        this.userId = in.readLong();
        this.portrait = in.readString();
        this.cateId = in.readString();
        this.site = in.readParcelable(SiteItem.class.getClassLoader());
        this.status = in.readInt();
        this.cover = in.readInt();
    }

    @Override
    public long getItemId() {
        return grainId;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(site.getLat(), site.getLon());
    }

    @Override
    public String getPicUrl() {
        return portrait;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.grainId);
        dest.writeString(this.text);
        dest.writeString(this.nickName);
        dest.writeString(this.remark);
        dest.writeString(this.image);
        dest.writeLong(this.userId);
        dest.writeString(this.portrait);
        dest.writeString(this.cateId);
        dest.writeParcelable(this.site, 0);
        dest.writeInt(this.status);
        dest.writeInt(this.cover);
    }

    public long getGrainId() {
        return grainId;
    }

    public void setGrainId(long grainId) {
        this.grainId = grainId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public SiteItem getSite() {
        return site;
    }

    public void setSite(SiteItem site) {
        this.site = site;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }
}
