package com.hltc.mtmap.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hltc.mtmap.app.AppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by redoblue on 15-5-18.
 */
public class ImageUtils {

    public static String creatThumbnail(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 通过这个bitmap获取图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap == null) {
            System.out.println("bitmap为空");
        }
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);
        // 计算缩放比
        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);

        String name = AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + StringUtils.getUUID() + ".png";
        File f = new File(name);
        try {
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
}
