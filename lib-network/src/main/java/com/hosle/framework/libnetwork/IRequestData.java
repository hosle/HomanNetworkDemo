package com.hosle.framework.libnetwork;




import com.hosle.framework.libnetwork.rxretrofit.OnSubscriberListener;

import rx.Observable;
import rx.Subscription;

/**
 * Created by tanjiahao on 16/10/9.
 * Original Project HomanNetwork
 */

public interface IRequestData<T> {
    Subscription doRequestData(OnSubscriberListener<T> listener);
    Observable<T> doRequestObservable();

}
