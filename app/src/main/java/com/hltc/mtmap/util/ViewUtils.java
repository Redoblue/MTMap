package com.hltc.mtmap.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;

import com.hltc.mtmap.app.MyApplication;

import java.io.ByteArrayOutputStream;

/**
 * Created by Redoblue on 2015/4/23.
 */
public class ViewUtils {

    public static Drawable getDrawable(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        Bitmap bitmap = view.getDrawingCache();
        return getDrawable(bitmap);
    }

    public static Drawable getDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getBitmap(byte[] array) {
        if (array.length != 0) {
            return BitmapFactory.decodeByteArray(array, 0, array.length);
        } else {
            return null;
        }
    }

    // 设置hint字体大小
    public static SpannedString getHint(String str, int size) {
        SpannableString ss = new SpannableString(str);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpannedString(ss);
    }

    // 对BitMap进行模糊
    public static Bitmap blur1(Bitmap bitmap, int scale, int radius) {
        long startMs = System.currentTimeMillis();
//        float scaleFactor = 1;
//        float radius = 20;
//        if (downScale.isChecked()) {
//            scaleFactor = 8;
//            radius = 2;
//        }

        Canvas canvas = new Canvas();
        canvas.scale(1 / scale, 1 / scale);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap = FastBlur.doBlur(bitmap, radius, true);
        ToastUtils.showLong(MyApplication.getContext(), System.currentTimeMillis() - startMs + "ms");

        return bitmap;
    }
}
