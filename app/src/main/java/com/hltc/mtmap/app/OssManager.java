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

    private static final OssManager manager = new OssManager();
    public static String serverAddress;
    public static String ossHost;
    public static String imgHost;
    public static String bucketName;
    public static String cdnHost;
    public OSSService ossService;
    public OSSBucket ossBucket;
    public OSSBucket imgChannel;

    private OssManager() {
        initConfig();
        initOssService();
    }

    public static OssManager getOssManager() {
        return manager;
    }

    private void initOssService() {
        ossService = OSSServiceProvider.getService();
        ossService.setApplicationContext(MyApplication.getContext());
        ossService.setGlobalDefaultHostId(ossHost);
        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);
        ossService.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 默认为private
        // 为指定的用户拿取服务其授权需求的FederationToken
        ossService.setAuthenticationType(AuthenticationType.FEDERATION_TOKEN);
        ossService.setGlobalDefaultStsTokenGetter(new StsTokenGetter() {
            @Override
            public OSSFederationToken getFederationToken() {
//                OSSFederationToken token = FederationTokenGetter.getToken();
//                Log.d("Publish", token.toString());
                OSSFederationToken token = new OSSFederationToken();
                token.setTempAk("STS.ovUUkQeuEJe6jJfOIJ00");
                token.setTempSk("A9fFqXaV2MwEQYhxonrjUmzG9c0bFxP8qdeY17rW");
                token.setSecurityToken("CAES/gMIARKAAUuQL4oq6h+oQSWeYD9IQ0lVuhDmmDhvvdXIvB6mFdtDcL1HZDZbs1HQ0Msj++NonhbLDBcir/6cNxysN3+3BYhrkIoajj8v63N+cEXzHECDC+U4osBzUEGWxspEmxda8cWsTRNWF7tlOBNSLsyijiF5qF/AsfhnMKO+r0jEbeyIGhhTVFMub3ZVVWtRZXVFSmU2akpmT0lKMDAiEDExOTQ2Mjc4Nzk4Njk2MzUqDHVzZXIubWFpdGlhbjDzn8uH4yk6BlJzYU1ENUKtAgoBMRqaAQoFQWxsb3cSKQoMQWN0aW9uRXF1YWxzEgZBY3Rpb24aEQoPb3NzOkxpc3RPYmplY3RzEjUKDlJlc291cmNlRXF1YWxzEghSZXNvdXJjZRoZChdhY3M6b3NzOio6KjptYWl0aWFuZGl0dRIvCgpTdHJpbmdMaWtlEgpvc3M6UHJlZml4GhUKEzIzMzcyMjM0MjBAcXEuY29tLyoaigEKBUFsbG93EkgKDEFjdGlvbkVxdWFscxIGQWN0aW9uGjAKDW9zczpQdXRPYmplY3QKDW9zczpHZXRPYmplY3QKEG9zczpEZWxldGVPYmplY3QSNwoOUmVzb3VyY2VFcXVhbHMSCFJlc291cmNlGhsKGWFjczpvc3M6KjoqOm1haXRpYW5kaXR1Lyo=");
                token.setExpiration(System.currentTimeMillis() + 3600 * 1000);
                return token;
            }
        });

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
        conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
        conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
        ossService.setClientConfiguration(conf);

        ossBucket = ossService.getOssBucket(bucketName);
//        ossBucket.setBucketHostId(ossHost);

        imgChannel = ossService.getOssBucket(bucketName);
//        imgChannel.setBucketHostId(imgHost);
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
        try {
            ossFile.setUploadFilePath(from, "image/jpeg");
            ossFile.enableUploadCheckMd5sum();
            ossFile.upload();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OSSException e) {
            e.printStackTrace();
        }
//        try {
//            ossFile.setUploadFilePath(from, "image/jpeg");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        ossFile.enableUploadCheckMd5sum();
//        Log.d("Publish", ossFile.toString());
//        ossFile.uploadInBackground(new SaveCallback() {
//            @Override
//            public void onSuccess(String s) {
//                Log.d("Publish", "Upload Success!");
//            }
//
//            @Override
//            public void onProgress(String s, int i, int i1) {
//
//            }
//
//            @Override
//            public void onFailure(String s, OSSException e) {
//
//            }
//        });
    }

}
