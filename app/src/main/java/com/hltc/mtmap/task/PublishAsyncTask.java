package com.hltc.mtmap.task;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.helper.PhotoHelper;

/**
 * Created by redoblue on 15-5-18.
 */
public class PublishAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    public PublishAsyncTask() {
        super();
    }

    @Override
    protected void onPreExecute() {
        initData();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        for (int i = 0; i < PhotoHelper.larges.size(); i++) {
            String path = PhotoHelper.larges.get(i);
            OssManager.getOssManager().uploadImage(path, OssManager.getRemotePath(path));
        }
//        FileUtils.deleteDir();
        return true;
    }

    private void initData() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
    }

}
