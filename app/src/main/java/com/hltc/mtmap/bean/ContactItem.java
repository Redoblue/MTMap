package com.hltc.mtmap.bean;

/**
 * Created by redoblue on 15-6-23.
 */
public class ContactItem {

    private String portraitSmall;
    private String name;
    private String nickName;
    private String phone;
    private boolean isSelected;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
