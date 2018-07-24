package com.hosle.framework.libnetwork.v2

import com.hosle.framework.libnetwork.rxretrofit.OnSubscriberListener
import rx.Observable
import rx.Subscription

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetwork
 *
 */
interface IRequestData<T> {
    fun doGetData(onStartCallback: (() -> Unit)? = null,
                  onFinishCallback: (() -> Unit)? = null,
                  onSuccessCallback: ((T) -> Unit)? = null,
                  onFailureCallback: ((Throwable) -> Unit)? = null): Subscription

    fun doPostData(onStartCallback: (() -> Unit)? = null,
                   onFinishCallback: (() -> Unit)? = null,
                   onSuccessCallback: ((T) -> Unit)? = null,
                   onFailureCallback: ((Throwable) -> Unit)? = null): Subscription

    fun doGetData(listener: OnSubscriberListener<T>): Subscription
    fun doPostData(listener: OnSubscriberListener<T>): Subscription

    fun doRequestObservable(): Observable<T>

    fun cancel()

}