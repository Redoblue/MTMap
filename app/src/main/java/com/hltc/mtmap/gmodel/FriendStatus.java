package com.hltc.mtmap.gmodel;

/**
 * Created by redoblue on 15-6-29.
 */
public class FriendStatus {

    long userId;
    String userPortrait;
    String nickName;
    String text;
    String status;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
