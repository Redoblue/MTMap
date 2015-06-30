package com.hltc.mtmap.gmodel;

/**
 * Created by redoblue on 15-6-29.
 */
public class Friend {

    long userId;
    String nickName;
    String remark;
    String portrait;
    String firstCharacter;
    boolean isFolder;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getFirstCharacter() {
        return firstCharacter;
    }

    public void setFirstCharacter(String firstCharacter) {
        this.firstCharacter = firstCharacter;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }
}
