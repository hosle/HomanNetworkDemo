package com.hosle.framework.demo.network.data.doubansearch

import com.hosle.framework.demo.network.model.SearchBookModel
import com.hosle.framework.libnetwork.v2.RxHttpTask
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

import retrofit2.http.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetworkDemo
 */
class SearchBookTask : RxHttpTask<SearchBookTask.RtfService, SearchBookModel>() {

    override var url: String = "https://api.douban.com/v2/book/search"

    init {
//        addURLParam("q", "python")
    }

    override fun doRequestObservable(): Observable<SearchBookModel> {
        return createService(url)
                .executeGet(getAllUrlParams())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    interface RtfService {
//        @FormUrlEncoded
//        @POST("v2/book/search")
//        fun executePost(@QueryMap(encoded = true) allQueries: Map<String, Any>,
//                        @FieldMap allFields: Map<String, Any>): Observable<SearchBookModel>

        @GET("v2/book/search")
        fun executeGet(@QueryMap(encoded = true) allQueries: Map<String, String>): Observable<SearchBookModel>
    }

    override fun resetCommonFormParams(): Map<String, Any>? {
        return null
    }

    override fun resetCommonUrlParams(): Map<String, Any>? {
        return hashMapOf<String, Any>("q" to "python")
    }

    override fun createCookieStore(): CookieJar {
        return object : CookieJar {
            override fun saveFromResponse(url: HttpUrl?, cookies: List<Cookie>?) {

            }

            override fun loadForRequest(url: HttpUrl?): List<Cookie> {
                return emptyList<Cookie>()
            }

        }
    }
}