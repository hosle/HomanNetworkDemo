package com.hosle.framework.libnetwork;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.connection.RouteDatabase;

/**
 * Created by tanjiahao on 17/8/16.
 * Original Project HomanNetwork
 *
 */

public class CustomOkHttpClient {
    private static final int TIME_OUT = 15000;
    private static volatile Dispatcher dispatcher;
    private static volatile ConnectionPool connectionPool;

    private CookieJar cookieJar;
    private Dns dns;

    public CustomOkHttpClient setCookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        return this;
    }

    public CustomOkHttpClient setDNS(Dns dns) {
        this.dns = dns;
        return this;
    }


    public OkHttpClient createOkHttpClient() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        try {
                            Log.d("CustomOkHttp", "Request:" + request);
                            Log.d("CustomOkhttp", "ContentLength:" + request.body().contentLength());
                            Log.d("CustomOkhttp", "ContentType:" + request.body().contentType());
                            Log.d("CustomOkhttp", "Headers:" + request.headers());

                        } catch (Exception ignore) {
                        }
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        try {
                            Connection connection = chain.connection();
                            RouteDatabase routeDatabase = Internal.instance.routeDatabase(getConnectionPool());
                            Route route = connection.route();

                            StringBuilder builder = null;
                            try {
                                builder = new StringBuilder()
                                        .append("Route:")
                                        .append(route)
                                        .append("...")
                                        .append(!routeDatabase.shouldPostpone(route));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("GI", builder.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }
                })
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .dispatcher(getDispatcher())
                .connectionPool(getConnectionPool());
        if (null != cookieJar) {
            clientBuilder.cookieJar(cookieJar);
        }
        if (null != dns) {
            clientBuilder.dns(dns);
        }
        return clientBuilder.build();
    }

    private static Dispatcher getDispatcher() {
        if (dispatcher == null) {
            synchronized (CustomOkHttpClient.class) {
                if (dispatcher == null)
                    dispatcher = new Dispatcher();
            }
        }
        return dispatcher;
    }

    private static ConnectionPool getConnectionPool() {
        if (connectionPool == null) {
            synchronized (CustomOkHttpClient.class) {
                if (connectionPool == null)
                    connectionPool = new ConnectionPool(10, 3, TimeUnit.MINUTES);
            }
        }
        return connectionPool;
    }
}
