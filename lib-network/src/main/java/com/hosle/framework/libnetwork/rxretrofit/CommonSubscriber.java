package com.hosle.framework.libnetwork.rxretrofit;


import rx.Subscriber;

/**
 * Created by tanjiahao on 16/10/13.
 * Original Project HomanNetwork
 *
 */

public class CommonSubscriber<M> extends Subscriber<M> {

    private OnSubscriberListener onSubscriberListener;


    public CommonSubscriber(OnSubscriberListener onSubscriberListener) {
        this.onSubscriberListener = onSubscriberListener;
    }


    /**
     * 订阅开始时调用
     */
    @Override
    public void onStart() {
        onSubscriberListener.onStart();
    }

    /**
     * 完成
     */
    @Override
    public void onCompleted() {
        onSubscriberListener.onFinish();
    }

    /**
     * 对错误进行统一处理
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        onSubscriberListener.onFailure(e);
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param m 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(M m) {
        if (onSubscriberListener != null) {
            onSubscriberListener.onSuccess(m);
        }
    }

}
