package com.hltc.mtmap.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.StsTokenGetter;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.hltc.mtmap.helper.FederationTokenGetter;

import java.io.FileNotFoundException;

/**
 * Created by redoblue on 15-6-23.
 */
public class OssManager {

    public static String serverAddress;
    public static String ossHost;
    public static String imgHost;
    public static String bucketName;
    public static String cdnHost;
    private static OssManager manager;
    public OSSService ossService;
    public OSSBucket ossBucket;
    public OSSBucket imgChannel;

    private OssManager() {
        initConfig();
        initOssService();
    }

    public static OssManager getOssManager() {
        if (manager == null) {
            manager = new OssManager();
        }
        return manager;
    }

    private void initOssService() {
        ossService = OSSServiceProvider.getService();
        ossService.setApplicationContext(MyApplication.getContext());
        ossService.setGlobalDefaultHostId(ossHost);
        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);
        ossService.setGlobalDefaultACL(AccessControlList.PRIVATE); // 默认为private
        // 为指定的用户拿取服务其授权需求的FederationToken
        ossService.setAuthenticationType(AuthenticationType.FEDERATION_TOKEN);
        ossService.setGlobalDefaultStsTokenGetter(new StsTokenGetter() {
            @Override
            public OSSFederationToken getFederationToken() {
                return FederationTokenGetter.getToken();
            }
        });

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
        conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
        conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
        ossService.setClientConfiguration(conf);

        ossBucket = ossService.getOssBucket(bucketName);
        ossBucket.setBucketHostId(ossHost);

        imgChannel = ossService.getOssBucket(bucketName);
        imgChannel.setBucketHostId(imgHost);
//        imgChannel.setCdnAccelerateHostId(cdnHost);
    }

    private void initConfig() {
        // 从Manifest.xml的Meta-data中获得加签服务器地址
        try {
            ApplicationInfo appInfo = MyApplication.getContext().getPackageManager().
                    getApplicationInfo(MyApplication.getContext().getPackageName(), PackageManager.GET_META_DATA);
            serverAddress = appInfo.metaData.getString("ServerAddress");
            bucketName = appInfo.metaData.getString("BucketName");
            ossHost = appInfo.metaData.getString("OssHost");
            imgHost = appInfo.metaData.getString("ImgHost");
            cdnHost = appInfo.metaData.getString("CdnHost");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void downloadImage(String to, String objectKey) {
        OSSFile ossFile = ossService.getOssFile(imgChannel, objectKey);
        String filePath = to + objectKey.substring(objectKey.lastIndexOf("/") + 1, objectKey.indexOf("@"));
        Log.d("OssManager", "filePath: " + filePath);
        try {
            ossFile.downloadTo(filePath);
        } catch (OSSException e) {
            e.printStackTrace();
        }
    }

    public void uploadImage(String from, String objectKey) {
        OSSFile ossFile = ossService.getOssFile(ossBucket, objectKey);
//        try {
//            ossFile.setUploadFilePath(from, "image/jpg");
//            ossFile.enableUploadCheckMd5sum();
//            ossFile.upload();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (OSSException e) {
//            e.printStackTrace();
//        }
        try {
            ossFile.setUploadFilePath(from, "image/jpeg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ossFile.enableUploadCheckMd5sum();
        Log.d("Publish", ossFile.toString());
        ossFile.uploadInBackground(new SaveCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d("Publish", "Upload Success!");
            }

            @Override
            public void onProgress(String s, int i, int i1) {

            }

            @Override
            public void onFailure(String s, OSSException e) {

            }
        });
    }

}
