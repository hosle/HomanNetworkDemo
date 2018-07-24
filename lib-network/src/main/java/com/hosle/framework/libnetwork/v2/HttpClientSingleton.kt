package com.hosle.framework.libnetwork.v2

import com.hosle.framework.libnetwork.CustomOkHttpClient
import okhttp3.CookieJar
import okhttp3.OkHttpClient

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetwork
 *
 */
object HttpClientSingleton {
    private var httpClient: OkHttpClient? = null
    private var cookieJar: CookieJar? = null

    fun getInstance(cookieJar: CookieJar): OkHttpClient {
        if (this.cookieJar != cookieJar || null == httpClient) {
            httpClient = CustomOkHttpClient()
                    .setCookieJar(cookieJar)
                    .createOkHttpClient()
            this.cookieJar = cookieJar
        }

        return httpClient!!
    }

}