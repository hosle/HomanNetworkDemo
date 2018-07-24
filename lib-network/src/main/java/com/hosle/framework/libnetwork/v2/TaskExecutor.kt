package com.hosle.framework.libnetwork.v2

import com.hosle.framework.libnetwork.rxretrofit.OnSubscriberListener
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.functions.Func2
import rx.functions.Func3
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by tanjiahao on 2018/6/25
 * Original Project HomanNetwork
 *
 */

/**
 * 创建并激活2个task
 * @param task2
 * @param listener
 * @param <T>
 */
fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiTasks(task2:T, listener: OnSubscriberListener<Any>){
    Observable.merge(doRequestObservable(),
            task2.doRequestObservable())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<Any>(){
                override fun onNext(t: Any) {
                    listener.onSuccess(t)
                }

                override fun onCompleted() {
                    listener.onFinish()
                }

                override fun onError(e: Throwable) {
                    listener.onFailure(e)
                }

            })
}

/**
 * 创建并激活2个task,同步获取结果
 * @param task2
 * @param customFunc2 实现合并所有结果的接口
 * @param listener
 * @param <T>
 */
fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiSyncTasks(task2:T,task3:T? = null,customFunc2:Func2<in Any?,in Any?,out Any>,customFunc3:Func3<in Any?,in Any?,in Any?,out Any>,listener: OnSubscriberListener<Any>){
    Observable.zip(doRequestObservable(),
            task2.doRequestObservable(), customFunc2)
            .onErrorResumeNext(Func1<Throwable, Observable<*>> { Observable.mergeDelayError(doRequestObservable(), task2.doRequestObservable()) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<Any>() {
                override fun onCompleted() {
                    listener.onFinish()
                }

                override fun onError(e: Throwable) {
                    listener.onFailure(e)
                }

                override fun onNext(o: Any) {
                    listener.onSuccess(o)
                }
            })
}

/**
 * 创建并激活3个task,同步获取结果
 * @param task2
 * @param task3
 * @param customfunc3 实现合并所有结果的接口
 * @param listener
 * @param <T>
 */
fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiSyncTasks(task2:T,task3:T,customFunc3:Func3<in Any?,in Any?,in Any?,out Any>,listener: OnSubscriberListener<Any>){
    Observable.zip(doRequestObservable(),
            task2.doRequestObservable(),
            task3.doRequestObservable(),customFunc3)
            .onErrorResumeNext(Func1<Throwable, Observable<*>> { Observable.mergeDelayError(doRequestObservable(), task2.doRequestObservable(),task3.doRequestObservable()) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<Any>() {
                override fun onCompleted() {
                    listener.onFinish()
                }

                override fun onError(e: Throwable) {
                    listener.onFailure(e)
                }

                override fun onNext(o: Any) {
                    listener.onSuccess(o)
                }
            })
}

/**
 * 创建并激活2个task,1个自定义observable事件，同步获取结果
 * @param task2
 * @param observable
 * @param customFunc3 实现合并所有结果的接口
 * @param listener 回调
 * @param <T>
 */
fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiSyncTasks(task2: T,observable: Observable<*>,customFunc3: Func3<in Any?, in Any?, in Any?, out Any>,listener: OnSubscriberListener<Any>){
    Observable.zip(doRequestObservable(),
            task2.doRequestObservable(), observable, customFunc3)
            .onErrorResumeNext(Func1<Throwable, Observable<*>> { Observable.mergeDelayError(doRequestObservable(), task2.doRequestObservable(), observable) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<Any>() {
                override fun onCompleted() {
                    listener.onFinish()
                }

                override fun onError(e: Throwable) {
                    listener.onFailure(e)
                }

                override fun onNext(o: Any) {
                    listener.onSuccess(o)
                }
            })
}

/**
 * 轮询任务
 * @param interval 相等间隔时间
 * @param maxRetries 循环最大次数
 * @param func1 中断轮询约束函数
 * @param subscriber 回调
 * @param <T>
 */
fun <S,M> RxHttpTask<S,M>.activatePeriodicTask(interval:Long,maxRetries:Int,func1:Func1<M,Boolean>,subscriber: Subscriber<M>){
    val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    doRequestObservable()
            .repeatWhen(Func1<Observable<out Void>, Observable<*>> { observable -> observable.zipWith(Observable.range(0, maxRetries)) { aVoid, integer -> integer }.flatMap<Long> { Observable.timer(interval, TimeUnit.SECONDS) } })
            .takeUntil(func1)
            .subscribeOn(scheduler)
            .unsubscribeOn(scheduler)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber)
}

/**
 * 轮询任务
 * @param interval 间隔的时间组，支持每次触发间隔不同时间
 * @param maxRetries 同上
 * @param func1 同上
 * @param subscriber 同上
 * @param <T>
 */
fun <S,M> RxHttpTask<S,M>.activatePeriodicTask(interval:LongArray,maxRetries:Int,func1:Func1<M,Boolean>,subscriber: Subscriber<M>){
    val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    val newInterval = Arrays.copyOf(interval, maxRetries)

    doRequestObservable()
            .repeatWhen(Func1<Observable<out Void>, Observable<*>> { observable -> observable.zipWith(Observable.range(0, maxRetries)) { aVoid, integer -> integer }.flatMap<Long> { integer -> Observable.timer(newInterval[integer], TimeUnit.SECONDS) } })
            .takeUntil(func1)
            .subscribeOn(scheduler)
            .unsubscribeOn(scheduler)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber)
}