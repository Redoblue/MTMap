package com.hltc.mtmap.task;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.FileUtils;

/**
 * Created by redoblue on 15-5-18.
 */
public class PublishAsyncTask extends AsyncTask<Void, Integer, Void> {

    public PublishAsyncTask() {
        super();
    }

    @Override
    protected void onPreExecute() {
        initData();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < PhotoHelper.larges.size(); i++) {
            String path = PhotoHelper.larges.get(i);
            OssManager.getOssManager().uploadImage(path, OssManager.getFileKeyByLocalUrl(path));
        }
        try {
            FileUtils.deleteDir();
            PhotoHelper.clearData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initData() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
    }

}
