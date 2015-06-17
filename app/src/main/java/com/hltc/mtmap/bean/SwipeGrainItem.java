package com.hltc.mtmap.bean;

/**
 * Created by merlin on 5/4/15.
 */
public class SwipeGrainItem {

    private long grainId;
    private String text;
    private String image;
    private long userId;
    private String portraitSmall;
    private SiteItem site;
    private int cover;

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public String getPortraitSmall() {
        return portraitSmall;
    }

    public void setPortraitSmall(String portraitSmall) {
        this.portraitSmall = portraitSmall;
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

    public SiteItem getSite() {
        return site;
    }

    public void setSite(SiteItem site) {
        this.site = site;
    }
}
