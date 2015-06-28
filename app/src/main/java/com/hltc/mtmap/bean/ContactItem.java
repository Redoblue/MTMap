package com.hltc.mtmap.bean;

/**
 * Created by redoblue on 15-6-23.
 */
public class ContactItem {

    private long userId;
    private String portraitSmall;
    private String name;
    private String nickName;
    private String phone;
    private String text = "";
    private boolean isSelected;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPortraitSmall() {
        return portraitSmall;
    }

    public void setPortraitSmall(String portraitSmall) {
        this.portraitSmall = portraitSmall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
