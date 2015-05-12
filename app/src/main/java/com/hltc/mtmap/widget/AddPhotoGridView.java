package com.hltc.mtmap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义gridview，解决ListView中嵌套gridview显示不正常的问题（1行半）
 *
 * @author wangyx
 * @version 1.0.0 2012-9-14
 */
public class AddPhotoGridView extends GridView {
    public AddPhotoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddPhotoGridView(Context context) {
        super(context);
    }

    public AddPhotoGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
