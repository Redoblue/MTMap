package com.hltc.mtmap.bean;

/**
 * Created by Redoblue on 2015/4/24.
 */
public class ContactStruct {

    private String mTitle;
    private String mDesc;
    private String mTime;
    private String mPhone;

    public ContactStruct(String mTitle, String mDesc, String mTime, String mPhone) {
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mTime = mTime;
        this.mPhone = mPhone;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }
}
