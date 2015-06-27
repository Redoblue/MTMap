package com.hltc.mtmap.task;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.bean.ParcelableGrain;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        FileUtils.deleteDir();
        return true;
    }

    private void initData() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
    }

}
