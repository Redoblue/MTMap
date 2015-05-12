package com.hltc.mtmap.bean;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by redoblue on 15-5-11.
 */
public class AddPhotoGridItem {

    private Drawable drawable;

    public AddPhotoGridItem(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
