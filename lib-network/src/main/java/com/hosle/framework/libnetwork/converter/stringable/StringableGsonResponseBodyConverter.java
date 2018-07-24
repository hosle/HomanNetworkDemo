package com.hosle.framework.libnetwork.converter.stringable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.hosle.framework.libnetwork.ResponseBodyStringListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by tanjiahao on 2017/11/22.
 * Original Project HomanNetwork
 *
 */

public class StringableGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final ResponseBodyStringListener listener;

    private String valueByteString = "";

    StringableGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, ResponseBodyStringListener listener) {
        this.gson = gson;
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        //ResponseData中的流只能使用一次，我们先将流中的数据读出在byte数组中。这个方法中已经关闭了ResponseBody,所以不需要再关闭了


        valueByteString = new String(value.bytes());
        if (listener != null) {
            listener.onStringCome(valueByteString);
        }
        InputStreamReader charReader = new InputStreamReader(new StringBufferInputStream(valueByteString), "UTF-8");//后面的charset根据服务端的编码来定
        JsonReader jsonReader = gson.newJsonReader(charReader);

        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}

