package com.hltc.mtmap.bean;

/**
 * Created by Redoblue on 2015/4/16.
 */
public class LocalUserInfo {

    private long id;
    private String nickname;
    private String createTime;
    private String avatarURL;
    private String rawAvatarURL;
    private String phone;
    private String coverURL;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getRawAvatarURL() {
        return rawAvatarURL;
    }

    public void setRawAvatarURL(String rawAvatarURL) {
        this.rawAvatarURL = rawAvatarURL;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

}
