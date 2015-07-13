package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hltc.mtmap.R;
import com.hltc.mtmap.app.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by redoblue on 15-7-12.
 */
public class LargeImageActivity extends Activity {

    @InjectView(R.id.iv_large_image)
    ImageView ivLargeImage;
    @InjectView(R.id.pb_large_image)
    ProgressBar pbLargeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_large_image);
        ButterKnife.inject(this);

        String image = getIntent().getStringExtra("image");
        ImageLoader.getInstance().displayImage(image, ivLargeImage, MyApplication.displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Toast.makeText(LargeImageActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                LargeImageActivity.this.finish();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                pbLargeImage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
}
