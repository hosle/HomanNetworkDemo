package com.hosle.framework.libnetwork.converter.stringable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.hosle.framework.libnetwork.ResponseBodyStringListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

/**
 * Created by tanjiahao on 2017/11/22.
 * Original Project HomanNetwork
 *
 */

public class StringableGsonConverterFactory extends Factory {
    public static StringableGsonConverterFactory create() {
        return create(new Gson());
    }

    public static StringableGsonConverterFactory create(ResponseBodyStringListener listener) {
        return create(new Gson(), listener);
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static StringableGsonConverterFactory create(Gson gson) {
        return new StringableGsonConverterFactory(gson);
    }

    public static StringableGsonConverterFactory create(Gson gson, ResponseBodyStringListener listener) {
        return new StringableGsonConverterFactory(gson, listener);
    }

    private final Gson gson;
    private final ResponseBodyStringListener listener;

    private StringableGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.listener = null;
    }

    private StringableGsonConverterFactory(Gson gson, ResponseBodyStringListener listener) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.listener = listener;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new StringableGsonResponseBodyConverter<>(gson, adapter, listener);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new StringableGsonRequestdBodyConverter<>(gson, adapter);
    }
}
