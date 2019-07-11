package com.lixh.rxhttp;


import com.lixh.BuildConfig;
import com.lixh.app.BaseApplication;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public enum ClientFactory {
    INSTANCE;

    private volatile OkHttpClient okHttpClient;

    private static final int TIMEOUT_READ = 15;
    private static final int TIMEOUT_CONNECTION = 15;
    private final OkHttpClient.Builder mBuilder;

    ClientFactory() {
        mBuilder = new OkHttpClient.Builder();
        if (BuildConfig.LOG_DEBUG) {
            mBuilder.addInterceptor(ClientHelper.getHttpLoggingInterceptor());
        }
        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 10 * 1024 * 1024);
        mBuilder.addNetworkInterceptor(ClientHelper.getAutoCacheInterceptor());
        mBuilder.addInterceptor(ClientHelper.getAutoCacheInterceptor());
        mBuilder.cache(cache);
        mBuilder.retryOnConnectionFailure(true)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .build();
    }

    public void addInterceptor(Interceptor interceptor) {
        mBuilder.addInterceptor(interceptor);

    }

    private void onHttpsNoCertficates() {
        try {
            mBuilder.sslSocketFactory(ClientHelper.getSSLSocketFactory()).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Builder getBuilder() {
        return this.mBuilder;
    }

    private void onHttpCertficates(int[] certficates, String[] hosts) {
        mBuilder.socketFactory(ClientHelper.getSSLSocketFactory(BaseApplication.getAppContext(), certficates));
        mBuilder.hostnameVerifier(ClientHelper.getHostnameVerifier(hosts));
    }

    public OkHttpClient getHttpClient() {
        okHttpClient = mBuilder.build();
        return okHttpClient;
    }
}