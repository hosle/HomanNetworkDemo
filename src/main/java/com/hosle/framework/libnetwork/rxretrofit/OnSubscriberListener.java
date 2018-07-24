package com.hosle.framework.libnetwork.rxretrofit;

/**
 * Created by tanjiahao on 16/10/13.
 * Original Project HomanNetwork
 *
 */

public interface OnSubscriberListener<M> {
    void onStart();
    void onFinish();
    void onSuccess(M model);
    void onFailure(Throwable t);
}
