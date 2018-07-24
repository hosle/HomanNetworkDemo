package com.hosle.framework.libnetwork;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hosle.framework.libnetwork.converter.stringable.StringableGsonConverterFactory;
import com.hosle.framework.libnetwork.rxretrofit.RtfServiceHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscription;

/**
 * Created by tanjiahao on 16/9/27.
 * Original Project HomanNetwork
 */

public abstract class RxHttpTask<S> implements IRequestData, RtfServiceHandler<S> {

    private String HOST;
    private String BASEURL;

    private Retrofit retrofit;
    protected Subscription taskSubscription;//retrofit建立连接后返回的订阅者实例,用于取消

    protected Map<String, String> mUrlParams = new HashMap<String, String>();
    protected Map<String, String> mFormParams = new HashMap<String, String>();

    private static volatile OkHttpClient httpClient = null;

    private String TAG = null;

    public RxHttpTask() {
        initCommonParams();
    }

    public static OkHttpClient getHttpClient(CookieJar cookieJar) {
        if (null == httpClient)
            synchronized (RxHttpTask.class) {
                if (null == httpClient) {
                    httpClient = new CustomOkHttpClient()
                            .setCookieJar(cookieJar)
                            .createOkHttpClient();
                }
            }
        return httpClient;
    }



    public RxHttpTask createUrl(String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl)) {
            BASEURL = baseUrl;
            Uri uri = Uri.parse(BASEURL);

            String scheme = uri.getScheme();

            HOST = uri.getScheme() + "://" + uri.getHost();

            int port = uri.getPort();

            if ("http".equals(scheme)) {

                if (-1 != port && 80 != port) {

                    HOST += (":" + port);

                }

            } else if ("https".equals(scheme)) {

                if (-1 != port && 443 != port) {

                    HOST += (":" + port);

                }

            }

        }
        return this;
    }

    public abstract CookieJar createCookieStore();

    public Retrofit createRetrofit() {

        retrofit = new Retrofit.Builder()
                .client(getHttpClient(createCookieStore()))
                .addConverterFactory(StringableGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HOST)
                .build();

        return retrofit;
    }


    public Retrofit createRetrofit(ResponseBodyStringListener listener) {

        retrofit = new Retrofit.Builder()
                .client(getHttpClient(createCookieStore()))
                .addConverterFactory(StringableGsonConverterFactory.create(listener))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HOST)
                .build();

        return retrofit;
    }

    @Override
    public S createService(String apiUrlString) {
        Type t = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        Class<S> cls = (Class<S>) params[0];
        S service = this.createUrl(apiUrlString)
                .createRetrofit()
                .create(cls);

        return service;
    }

    @Override
    public S createService(String apiString, ResponseBodyStringListener listener) {
        Type t = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        Class<S> cls = (Class<S>) params[0];
        S service = this.createUrl(apiString)
                .createRetrofit(listener)
                .create(cls);

        return service;
    }

    private void initCommonParams() {
//        addURLParams("from", "na-android");
//        String timeStamp = System.currentTimeMillis() + "";
//        addURLParams("request_time", timeStamp);

    }

    public void addURLParams(String name, String value) {
        if(!TextUtils.isEmpty(value)){
            value=value.trim();
        }
        mUrlParams.put(name, value);
    }

    public void addFormParams(String name, String value) {
        if (!TextUtils.isEmpty(name)) {
            if(!TextUtils.isEmpty(value)){
                value=value.trim();
            }
            mFormParams.put(name, TextUtils.isEmpty(value) ? "" : value);
        }
    }


    public Map<String, String> getFormParams() {
        return mFormParams;
    }

    public Map<String, String> getUrlParams() {
        return mUrlParams;
    }

    public Subscription getTaskSubscription() {
        return taskSubscription;
    }

    public void setTaskSubscription(Subscription taskSubscription) {
        this.taskSubscription = taskSubscription;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }
}
