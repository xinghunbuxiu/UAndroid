package com.lixh.rxhttp.convert;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.lixh.base.BaseResPose;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by God on 2016/8/20.
 */
public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson mGson;
    private final TypeAdapter<T> adapter;

    public JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        mGson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String json = verify(value.string());
        try {
            return adapter.read(mGson.newJsonReader(new StringReader(json)));
        } finally {
            value.close();
        }
    }

    private <T> String verify(String json) {
        boolean isOk = json.matches("code|msg|data");
        if (!isOk) {
            BaseResPose result = new BaseResPose();
            result.message = "ok";
            result.code = 200;
            if (json.startsWith("\\[|\\{")) {
                result.data = JSON.parse(json);
            } else {
                result.data = json;
            }
            json = JSON.toJSONString(result);
        }
        return json;
    }
}