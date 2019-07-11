package com.lixh.rxhttp;

import okhttp3.Interceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


public enum ApiFactory {
    INSTANCE;

    ApiFactory() {
    }

    public <T> T createApi(String baseUrl, Class<T> t, Converter.Factory factory) {
        return createApi(baseUrl, t, factory, new Interceptor[0]);
    }

    public <T> T createApi(String baseUrl, Class<T> t, Converter.Factory factory, Interceptor... interceptor) {
        Retrofit.Builder mBuilder = new Retrofit.Builder()
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl);
        ClientFactory clientFactory = ClientFactory.INSTANCE;
        if (interceptor != null) {
            for (Interceptor interceptor1 : interceptor) {
                clientFactory.addInterceptor(interceptor1);
            }
        }
        return mBuilder.client(clientFactory.getHttpClient()).build().create(t);
    }

}