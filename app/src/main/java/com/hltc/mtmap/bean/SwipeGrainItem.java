package com.hltc.mtmap.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by merlin on 5/4/15.
 */
public class SwipeGrainItem {

    private Drawable pic;
    private Drawable avatar;
    private String comment;

    public SwipeGrainItem() {

    }

    public SwipeGrainItem(Drawable pic, Drawable avatar, String comment) {
        this.pic = pic;
        this.avatar = avatar;
        this.comment = comment;
    }

    public Drawable getPic() {
        return pic;
    }

    public void setPic(Drawable pic) {
        this.pic = pic;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
