package com.hosle.framework.libnetwork.v2

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.hosle.framework.libnetwork.*
import com.hosle.framework.libnetwork.converter.stringable.StringableGsonConverterFactory
import com.hosle.framework.libnetwork.rxretrofit.CommonSubscriber
import com.hosle.framework.libnetwork.rxretrofit.OnSubscriberListener
import okhttp3.CookieJar
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.lang.reflect.ParameterizedType
import java.util.HashMap
import kotlin.reflect.KClass

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetwork
 *
 */
abstract class RxHttpTask<S,M> : IRequestData<M> {

    open var url: String = ""
    private var HOST: String? = null

    private var urlParams: MutableMap<String, Any> = HashMap()
    private var formParams: MutableMap<String, Any> = HashMap()

    private var TAG: String? = null

    private var subscription:Subscription? = null

    override fun doGetData(onStartCallback: (() -> Unit)?, onFinishCallback: (() -> Unit)?, onSuccessCallback: ((M) -> Unit)?, onFailureCallback: ((Throwable) -> Unit)?): Subscription {

        val subscription = doRequestObservable()
                .subscribe(LambdaSubscriber<M>(onStartCallback, onFinishCallback, onSuccessCallback, onFailureCallback))
        this.subscription = subscription
        return subscription
    }

    override fun doPostData(onStartCallback: (() -> Unit)?, onFinishCallback: (() -> Unit)?, onSuccessCallback: ((M) -> Unit)?, onFailureCallback: ((Throwable) -> Unit)?): Subscription {
        val subscription = doRequestObservable()
                .subscribe(LambdaSubscriber<M>(onStartCallback, onFinishCallback, onSuccessCallback, onFailureCallback))
        this.subscription = subscription
        return subscription
    }

    override fun doGetData(listener: OnSubscriberListener<M>): Subscription {
        val subscription = doRequestObservable()
                .subscribe(CommonSubscriber<M>(listener))
        this.subscription = subscription
        return subscription
    }

    override fun doPostData(listener: OnSubscriberListener<M>): Subscription {
        val subscription =  doRequestObservable()
                .subscribe(CommonSubscriber<M>(listener))
        this.subscription = subscription
        return subscription
    }

    override fun cancel() {
        subscription?.unsubscribe()
    }

    fun createService(apiUrlString: String): S {

        val t = javaClass.genericSuperclass
        val params = (t as ParameterizedType).actualTypeArguments
        val cls = params[0] as Class<S>

        return createUrl(apiUrlString)
                .createRetrofit()
                .create(cls)
    }

    fun createService(apiString: String, listener: ResponseBodyStringListener): S {
        val t = javaClass.genericSuperclass
        val params = (t as ParameterizedType).actualTypeArguments
        val cls = params[0] as Class<S>

        return createUrl(apiString)
                .createRetrofit(listener)
                .create(cls)
    }


    fun getPath(): String {
        return url.substring(HOST!!.length + 1)
    }

    private fun createUrl(baseUrl: String): RxHttpTask<*,*> {
        if (!TextUtils.isEmpty(baseUrl)) {
            val uri = Uri.parse(baseUrl)

            val scheme = uri.scheme

            HOST = uri.scheme + "://" + uri.host

            val port = uri.port

            if ("http" == scheme) {

                if (-1 != port && 80 != port) {

                    HOST += ":" + port

                }

            } else if ("https" == scheme) {

                if (-1 != port && 443 != port) {

                    HOST += ":" + port

                }

            }

        }
        return this
    }

    abstract fun createCookieStore(): CookieJar

    private fun createRetrofit(): Retrofit {

        return Retrofit.Builder()
                .client(HttpClientSingleton.getInstance(createCookieStore()))
                .addConverterFactory(StringableGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HOST!!)
                .build()
    }


    private fun createRetrofit(listener: ResponseBodyStringListener): Retrofit {

        return Retrofit.Builder()
                .client(HttpClientSingleton.getInstance(createCookieStore()))
                .addConverterFactory(StringableGsonConverterFactory.create(listener))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HOST!!)
                .build()
    }


    abstract fun resetCommonFormParams(): Map<String, Any>?
    abstract fun resetCommonUrlParams():Map<String,Any>?

    fun addURLParam(name: String, value: Any) {
        urlParams[name] = value
    }

    fun addFormParam(name: String, value: Any) {
        if (!TextUtils.isEmpty(name)) {
            formParams[name] = value
        }
    }

    fun addURLParams(params: Map<String, Any>) {
        urlParams.putAll(params)
    }

    fun addFormParams(params: Map<String, Any>) {
        formParams.putAll(params)
    }

    private val transValueType: (Map.Entry<*, *>) -> String = { entry -> entry.value.toString() }

    fun getAllFormParams(): Map<String, String> {
        return HashMap<String, String>().apply {
            putAll(resetCommonFormParams()?.mapValues(transValueType) ?: emptyMap())
            putAll(formParams.mapValues(transValueType))
        }
    }

    fun getAllUrlParams(): Map<String, String> {
        return HashMap<String, String>().apply {
            putAll(resetCommonUrlParams()?.mapValues(transValueType) ?: emptyMap())
            putAll(urlParams.mapValues(transValueType))
        }
    }

    fun getTAG(): String? {
        return TAG
    }

    fun setTAG(TAG: String) {
        this.TAG = TAG
    }
}