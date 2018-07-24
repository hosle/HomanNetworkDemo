package com.hosle.framework.libnetwork.v2

import rx.Subscriber

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetwork
 *
 */
class LambdaSubscriber<M>(private val onStartCallback: (() -> Unit)? = null,
                          private val onFinishCallback: (() -> Unit)? = null,
                          private val onSuccessCallback: ((M) -> Unit)? = null,
                          private val onFailureCallback: ((Throwable) -> Unit)? = null) : Subscriber<M>() {

    override fun onStart(): Unit = onStartCallback?.invoke() ?: Unit

    override fun onNext(t: M): Unit = onSuccessCallback?.invoke(t) ?: Unit

    override fun onCompleted(): Unit = onFinishCallback?.invoke() ?: Unit

    override fun onError(e: Throwable): Unit = onFailureCallback?.invoke(e) ?: Unit

}