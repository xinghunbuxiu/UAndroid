package com.lixh.rxhttp.convert;

import com.google.gson.Gson;
import com.lixh.base.BaseResPose;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ResultConverter<T> implements Converter<ResponseBody, BaseResPose<T>> {

    public static final ResultConverter INSTANCE = new ResultConverter();

    @Override
    public BaseResPose<T> convert(ResponseBody value) throws IOException {
        BaseResPose result = new Gson().fromJson(value.string(), BaseResPose.class);
        return result;
    }
}