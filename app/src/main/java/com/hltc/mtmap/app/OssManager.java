package com.hltc.mtmap.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSToolKit;

import java.io.File;
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

    public static String getRemotePath(String s) {
        String path = "users/" + AppConfig.getAppConfig().getConfUsrUserId()
                + "/" + s.substring(s.lastIndexOf("/") + 1);
        return path;
    }

    private void initOssService() {
        ossService = OSSServiceProvider.getService();
        ossService.setApplicationContext(MyApplication.getContext());
        ossService.setGlobalDefaultHostId(ossHost);
//        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);
        ossService.setGlobalDefaultACL(AccessControlList.PRIVATE); // 默认为private
        // 为指定的用户拿取服务其授权需求的FederationToken
//        ossService.setAuthenticationType(AuthenticationType.FEDERATION_TOKEN);
//        ossService.setGlobalDefaultStsTokenGetter(new StsTokenGetter() {
//            @Override
//            public OSSFederationToken getFederationToken() {
//                OSSFederationToken token = FederationTokenGetter.getToken();
//                Log.d("Publish", token.toString());
//                return token;
//            }
//        });
        ossService.setAuthenticationType(AuthenticationType.ORIGIN_AKSK);
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() {
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date, String ossHeaders, String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken("wxGYeoOqFGIikopt", "eQyS38ArhJo0fIotIuLoiz0FCx0J4N", content);
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
        imgChannel.setCdnAccelerateHostId(cdnHost);
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
        OSSFile ossFile = ossService.getOssFile(ossBucket, objectKey);
        Log.d("OssManager", "to: " + to);
        try {
            ossFile.downloadTo(to);
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
        } finally {
            //删除掉文件
            File file = new File(from);
            if (file.exists())
                file.delete();
        }
    }

}
