package com.hltc.mtmap.app;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.util.OSSToolKit;

/**
 * Created by redoblue on 15-6-23.
 */
public class OssManager {

    private static OssManager manager;
    private OSSService ossService;
    private OSSBucket ossBucket;

    private OssManager() {
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
        //TODO 全局加签
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;
                return OSSToolKit.generateToken("wxGYeoOqFGIikopt", "eQyS38ArhJo0fIotIuLoiz0FCx0J4N", content);
            }
        });
        ossService.setGlobalDefaultHostId(AppConfig.OSS_ROOT);
        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);
        ossService.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 默认为private

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
        conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
        conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
        ossService.setClientConfiguration(conf);

        ossBucket = ossService.getOssBucket(AppConfig.OSS_BUCKET);
    }

    public OSSService getService() {
        return ossService;
    }

    public OSSBucket getBucket() {
        return ossBucket;
    }


}
