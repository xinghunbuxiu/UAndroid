package com.lixh.rxhttp;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;


public enum ApiFactory {
    INSTANCE;

    ApiFactory() {
    }

    public  <T> T createApi(String baseUrl, Class<T> t, Converter.Factory factory) {
        Retrofit.Builder mBuilder = new Retrofit.Builder()
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl);


        return mBuilder.client(ClientFactory.INSTANCE.getHttpClient()).build().create(t);
    }



}